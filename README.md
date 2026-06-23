# 智瞳工坊 · 后端服务

> 校园旷课代课智能检测系统 — Spring Boot 后端

---

## 技术栈

- **框架：** Spring Boot 3.2 / Spring Data JPA
- **数据库：** MySQL 8.0（HikariCP 连接池）
- **AI 服务：** Python Flask（dlib 人脸识别 + OpenCV 行为分析）
- **部署：** 阿里云 ECS（Windows Server 2022）
- **构建：** Maven 3.9

## 项目结构

```
src/main/java/com/eye/
├── controller/   # REST API 控制器（8个）
├── service/      # 业务逻辑层
├── repository/   # JPA 数据访问层
└── entity/       # 数据实体（6张表）
```

## API 概览（20+ 接口）

| 模块 | 接口 |
|---|---|
| 用户 | 注册、登录、信息查询 |
| 学生 | 注册人脸、列表、识别、批量导入、删除 |
| 考勤 | 开始课堂、签到、报告、导出 CSV |
| 检测 | 检测历史、异常事件 |
| 课程 | 课程表导入/导出、当前课程检测 |
| 行为 | 行为分析、微信通知推送 |

## 快速启动

```bash
mvn clean package -DskipTests
java -jar target/eye-backend-1.0.0.jar
```

## 数据库配置

```yaml
spring.datasource:
  url: jdbc:mysql://localhost:3306/eye_db
  username: root
  password: 1234
  hikari:
    maximum-pool-size: 20
    leak-detection-threshold: 60000
```

## 项目亮点

- 独立设计 6 张数据库表，实现完整业务闭环
- 对接 Python AI 微服务，完成人脸识别签到
- HikariCP 连接池优化，解决长时间运行卡死问题
- 阿里云 ECS 部署，公网可访问
- Web 管理端：教务 / 教师 / 学生 / 考勤看板 4 端
