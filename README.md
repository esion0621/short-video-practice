# 短视频用户行为分析平台

本项目是一个基于大数据技术的短视频用户行为分析与推荐系统，旨在通过采集用户交互数据，利用实时和离线计算构建用户画像，并实现个性化视频推荐。系统运行在单节点伪分布式环境中，涵盖了从数据采集、存储、处理到展示的完整流程。

## 已完成工作概览

### 1. 环境与组件部署
在虚拟机中完成了以下组件的安装与配置，所有组件均以伪分布式模式运行：
- **Hadoop 3.2.4**：HDFS提供分布式存储，用于存放视频文件、封面图片及原始行为日志。
- **ZooKeeper 3.7.1**：协调服务，为Kafka、HBase提供支持。
- **HBase 2.4.11**：存储用户画像、实时推荐结果等需要快速查询的数据。
- **Kafka 3.2.0**：消息队列，用于解耦用户行为数据的生产与消费。
- **Spark 3.1.3**：后续将用于实时流处理和离线分析（已完成作业框架，待填充业务逻辑）。
- **MySQL 8.0**：关系型数据库，存储用户、视频元数据及最终推荐结果。
- **Redis 5.0.7**：缓存热门视频排行榜、用户会话等，提升响应速度。

### 2. 数据库设计
根据业务需求设计了以下存储结构：
- **MySQL**：包含 `users`、`videos`、`video_categories`、`user_actions`、`user_tags`、`recommendations` 等表，分别管理用户、视频、行为日志、标签和推荐结果。
- **HBase**：创建了 `user_portrait`（用户画像）、`video_features`（视频特征）、`realtime_recommendations`（实时推荐）等表，列族设计考虑了版本控制和压缩。
- **HDFS**：建立了 `/video-analysis/` 目录树，用于存放原始行为日志、处理结果以及视频/封面文件。
- **Kafka**：创建了 `user-behavior-topic`、`video-event-topic` 等主题，用于行为数据流转。
- **Redis**：设计了热门视频排行榜、用户会话等键结构。

### 3. 后端服务（Spring Boot，端口2006）
后端采用 Spring Boot 3.x + Java 17，整合了 MyBatis-Plus、Spring Data Redis、Spring Kafka 以及 HBase/Hadoop 客户端。主要功能包括：
- **用户模块**：注册、登录（JWT鉴权）、个人信息查询与修改。
- **视频模块**：视频上传（支持自动截取封面，使用 FFmpeg 抽取第3秒帧）、视频列表分页、详情获取。
- **行为模块**：接收前端上报的点赞、收藏、分享、观看等行为，同时写入 MySQL 并发送至 Kafka。
- **推荐模块**：提供热门视频（基于 Redis 排行榜）和个性化推荐接口（推荐结果由 Spark 生成后存入 MySQL/HBase）。
- **文件服务**：通过 `/api/files` 接口从 HDFS 读取视频和封面，返回给前端。
- **头像上传**：支持用户上传头像，存储至 HDFS，并更新用户信息。

### 4. 前端应用（Vue 3 + Vite 4，端口2005）
前端采用 Vue 3 组合式 API，Pinia 状态管理，Vite 作为构建工具。主要页面和组件：
- **首页**：展示热门推荐视频，采用毛玻璃卡片设计，背景为动态渐变。
- **视频详情页**：播放视频（从 HDFS 流式加载），展示点赞、收藏、分享按钮，并自动上报观看行为。
- **登录/注册页**：动态渐变背景 + 毛玻璃卡片，支持表单提交。
- **个人中心**：展示用户信息（昵称、邮箱等），支持上传头像，并分选项卡查看“我的视频”、“点赞”、“收藏”列表。
- **视频上传页**：允许用户上传视频，可自定义封面（未选择时自动截取）。

### 5. 关键功能实现细节
- **视频自动截帧**：后端利用 FFmpeg 命令行工具，在上传视频时若未提供封面，则自动抽取第3秒画面作为封面，保存至 HDFS。
- **HDFS 文件访问**：通过自定义文件接口（`/api/files?path=...`）将 HDFS 内部路径转换为可访问的 HTTP URL，前端配合工具函数 `getFileUrl` 完成路径转换。
- **行为上报**：前端在视频播放、点赞等操作时调用 `/api/behaviors` 接口，后端同步写入 MySQL 并发送至 Kafka，为后续 Spark 消费做好准备。
- **用户画像**：HBase 中存储的画像数据可通过 `/api/portrait/{userId}` 查询，画像由 Spark 作业（待完善）计算生成。
- **热门榜单**：Redis 中维护 `hot:videos:daily` 有序集合，目前暂用最新视频替代（待 Spark 流处理更新）。

## 待完成工作
- **Spark 实时处理**：编写 Spark Streaming 作业消费 Kafka 中的用户行为，实时更新 Redis 热门榜和 HBase 用户画像。
- **Spark 离线分析**：每日定时作业，从 HDFS 读取原始日志，计算用户标签和视频特征，写入 MySQL 和 HBase，并生成个性化推荐结果存入 `recommendations` 表。
- **推荐算法优化**：目前推荐仅基于热门和最新，后续可集成协同过滤或内容推荐算法。
- **前端体验完善**：增加加载动画、错误提示，优化移动端适配。

## 项目结构简述
```
video-backend/          # Spring Boot后端
video-frontend/         # Vue前端
├── src/
│   ├── api/            # 接口封装
│   ├── components/     # 公共组件（VideoCard, NavBar, AvatarUploader）
│   ├── views/          # 页面（Home, VideoDetail, Profile, Upload, Login, Register）
│   ├── stores/         # Pinia状态管理
│   ├── utils/          # 工具函数（getFileUrl等）
│   └── router/         # 路由配置
scripts/                # Spark作业（待实现）
```

## 运行方式
1. 启动所有大数据组件（Hadoop、ZooKeeper、HBase、Kafka、Redis、MySQL）。
2. 打包后端：`cd video-backend && mvn clean package`
3. 运行后端：`java -jar target/video-backend-1.0.0.jar`
4. 运行前端：`cd video-frontend && npm run dev`
5. 访问 `http://localhost:2005` 即可体验。

---

本项目已具备完整的业务闭环，后续将逐步完善数据处理层，实现真正的大数据分析与推荐。如有任何问题，欢迎提交 issue。
