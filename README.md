# 基于大数据的短视频应用实践

本项目是一个基于大数据生态系统的短视频应用实践项目，旨在模拟真实短视频平台的核心业务场景，并以此为基础搭建完整的数据采集、存储、处理与展示链路。系统运行于单节点伪分布式环境，涵盖用户管理、视频上传与播放、行为上报、实时/离线分析、个性化推荐等模块，可作为大数据技术综合学习的参考实现。

## 功能特点

- **用户系统**：注册、登录（JWT 鉴权）、个人信息维护、头像上传（HDFS 存储）
- **视频管理**：视频上传（支持自动截取封面，基于 FFmpeg）、视频流播放、分类浏览
- **行为采集**：点赞、收藏、分享、观看时长等行为实时记录，同步写入 MySQL 并发送至 Kafka
- **推荐服务**：热门视频（Redis 缓存）、个性化推荐（待 Spark 作业完善）
- **数据存储**：MySQL（业务数据）、HDFS（视频/封面/日志）、HBase（用户画像）、Redis（缓存/排行榜）
- **消息队列**：Kafka 解耦行为数据生产与消费
- **数据处理**：Spark Streaming 与 Spark SQL 提供实时与离线分析（作业框架已搭建）

## 技术栈

| 组件 | 版本 | 用途 |
|------|------|------|
| Hadoop | 3.2.4 | HDFS 分布式存储 |
| ZooKeeper | 3.7.1 | 协调服务 |
| HBase | 2.4.11 | 用户画像、实时推荐存储 |
| Kafka | 3.2.0 | 消息队列 |
| Spark | 3.1.3 | 实时/离线数据处理 |
| MySQL | 8.0 | 业务数据库 |
| Redis | 5.0.7 | 缓存、排行榜 |
| Spring Boot | 3.0.5 | 后端业务服务 |
| Vue 3 | 3.5.27 | 前端界面 |
| Vite | 4.5.14 | 前端构建工具 |
| MyBatis-Plus | 3.5.3.1 | ORM 框架 |
| FFmpeg | 任意版本 | 视频自动截帧 |

## 系统架构

```
前端 (Vue 3, port 2005)
    ↓ HTTP
后端 (Spring Boot, port 2006)
    ├── MySQL (用户/视频/推荐结果)
    ├── Redis (缓存/排行榜)
    ├── Kafka (行为消息)
    └── HDFS (视频/封面/日志)
           ↓
Spark Streaming (消费 Kafka → 更新 Redis/HBase)
Spark SQL (离线分析 → 画像/推荐)
```

## 快速开始

### 环境准备

1. **安装 JDK 17** 并设置 `JAVA_HOME`
2. **安装 Maven** 用于后端编译
3. **安装 Node.js 18.20.8** 用于前端开发
4. **部署大数据组件**（所有组件均以伪分布式模式运行在同一节点）：
   - Hadoop 3.2.4（HDFS）
   - ZooKeeper 3.7.1
   - HBase 2.4.11
   - Kafka 3.2.0
   - Spark 3.1.3
   - MySQL 8.0
   - Redis 5.0.7

### 组件启动顺序

```bash
# 1. Hadoop
start-all.sh

# 2. ZooKeeper
zkServer.sh start

# 3. HBase
start-hbase.sh

# 4. Kafka
kafka-server-start.sh -daemon $KAFKA_HOME/config/server.properties

# 5. Redis
redis-server --daemonize yes

# 6. MySQL（自动启动，无需额外操作）
```

### 数据库初始化

见 `docs/schema.txt`。 


### 后端运行

```bash
cd video-backend
# 修改 src/main/resources/application.yml 中的数据库连接等配置
mvn clean package
java -jar target/video-backend-1.0.0.jar
```

### 前端运行

```bash
cd video-frontend
npm install
npm run dev
```

访问 `http://localhost:2005` 即可体验。

### 验证功能

- 注册/登录
- 上传视频（可不上传封面，自动截取第3秒）
- 浏览首页视频列表
- 点击视频播放，进行点赞/收藏操作
- 进入个人中心查看上传、点赞、收藏记录，并更换头像

## 项目结构

```
.
├── video-backend/               # Spring Boot后端
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/video/backend/
│   │   │   │   ├── controller/   # 接口层
│   │   │   │   ├── service/      # 业务逻辑
│   │   │   │   ├── mapper/       # MyBatis接口
│   │   │   │   ├── entity/       # 实体类
│   │   │   │   ├── dto/          # 数据传输对象
│   │   │   │   ├── config/       # 配置类
│   │   │   │   ├── util/         # 工具类（HDFS/HBase/FFmpeg等）
│   │   │   │   └── kafka/        # Kafka生产者
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── mapper/       # XML映射文件
│   │   └── test/
│   └── pom.xml
│
├── video-frontend/               # Vue前端
│   ├── src/
│   │   ├── api/                  # 接口封装
│   │   ├── assets/               
│   │   ├── components/            # 公共组件
│   │   ├── views/                 # 页面
│   │   ├── stores/                # Pinia状态
│   │   ├── router/                # 路由
│   │   ├── utils/                 # 工具函数（如getFileUrl）
│   │   ├── App.vue
│   │   └── main.js
│   ├── index.html
│   ├── vite.config.js
│   └── package.json
│
├── scripts/                       # Spark作业（待完善）
│   ├── streaming/
│   └── offline/
│
└── docs/                          # 文档
    └── schema.txt
```

## 后续计划

- [ ] 实现 Spark Streaming 实时作业，更新 Redis 热门榜与 HBase 画像
- [ ] 实现 Spark SQL 离线分析，计算用户标签和生成推荐结果
- [ ] 集成协同过滤或内容推荐算法，提升推荐质量
- [ ] 增加单元测试与集成测试
- [ ] 优化前端体验，支持移动端适配
- [ ] 补充监控与日志收集（如 Flume + Elasticsearch）

## 许可证

MIT License

---

> 注：本项目为个人学习实践，部分功能尚在完善中。如有任何问题或建议，欢迎提交 Issue 或 Pull Request。
