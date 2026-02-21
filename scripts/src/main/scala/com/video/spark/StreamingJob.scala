package com.video.spark

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010._
import org.apache.kafka.common.serialization.StringDeserializer
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import redis.clients.jedis.Jedis

object StreamingJob {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("VideoBehaviorStreaming").setMaster("local[*]")
    val ssc = new StreamingContext(conf, Seconds(10)) // 每10秒一个批次

    // Kafka 参数
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "spark-streaming-group",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val topics = Array("user-behavior-topic")
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](topics, kafkaParams)
    )

    // 解析 JSON，提取视频ID和行为类型
    val behaviorStream = stream.map(record => {
      val mapper = new ObjectMapper()
      mapper.registerModule(DefaultScalaModule)
      val json: JsonNode = mapper.readTree(record.value())
      val videoId = json.get("videoId").asLong()
      val actionType = json.get("actionType").asInt()
      (videoId, actionType)
    })

    // 过滤出有效行为（点赞、分享、收藏等对热度有贡献的）
    val validActions = behaviorStream.filter { case (_, actionType) =>
      actionType == 2 || actionType == 4 || actionType == 6  // 点赞、分享、收藏
    }

    // 每10秒统计一次每个视频的热度分数（此处简单计数，可根据需求加权）
    val videoCounts = validActions.map { case (videoId, _) => (videoId, 1L) }
      .reduceByKey(_ + _)

    // 更新 Redis 中的 ZSet
    videoCounts.foreachRDD { rdd =>
      rdd.foreachPartition { partition =>
        val jedis = new Jedis("localhost", 6379)
        partition.foreach { case (videoId, count) =>
          // 使用 ZINCRBY 增加视频的热度分数
          jedis.zincrby("hot:videos:daily", count.toDouble, s"video:$videoId")
        }
        jedis.close()
      }
    }

    ssc.start()
    ssc.awaitTermination()
  }
}
