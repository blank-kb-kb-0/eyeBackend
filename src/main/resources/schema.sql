-- 创建数据库
CREATE DATABASE IF NOT EXISTS eye_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE eye_db;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 检测记录表
CREATE TABLE IF NOT EXISTS detection_records (
                                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                 record_id VARCHAR(64) NOT NULL UNIQUE,
    timestamp BIGINT NOT NULL,
    photo MEDIUMTEXT,
    results TEXT,
    avg_confidence DOUBLE,
    username VARCHAR(50)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 异常事件表
CREATE TABLE IF NOT EXISTS abnormal_events (
                                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                               event_id VARCHAR(64) NOT NULL UNIQUE,
    timestamp BIGINT NOT NULL,
    person_name VARCHAR(50),
    behavior VARCHAR(50),
    behavior_label VARCHAR(100),
    location VARCHAR(100),
    confidence DOUBLE,
    source_history_id VARCHAR(64),
    username VARCHAR(50)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 收藏表
CREATE TABLE IF NOT EXISTS favorites (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         record_id VARCHAR(64) NOT NULL,
    username VARCHAR(50)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;