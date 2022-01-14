/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 80022
Source Host           : 127.0.0.1:3306
Source Database       : cgcg_jobs

Target Server Type    : MYSQL
Target Server Version : 80022
File Encoding         : 65001

Date: 2022-01-14 09:37:06
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for task_info
-- ----------------------------
DROP TABLE IF EXISTS `task_info`;
CREATE TABLE `task_info` (
  `id` int NOT NULL AUTO_INCREMENT,
  `args` varchar(255) DEFAULT NULL,
  `name` varchar(64) DEFAULT NULL,
  `desc` varchar(255) DEFAULT NULL,
  `task_key` varchar(128) DEFAULT NULL,
  `group_key` varchar(64) DEFAULT NULL,
  `cron` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for task_run_recode
-- ----------------------------
DROP TABLE IF EXISTS `task_run_recode`;
CREATE TABLE `task_run_recode` (
  `id` int NOT NULL AUTO_INCREMENT,
  `task_id` int DEFAULT NULL,
  `server_id` int DEFAULT NULL,
  `run_time` int DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `result` tinyint(1) DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for task_server
-- ----------------------------
DROP TABLE IF EXISTS `task_server`;
CREATE TABLE `task_server` (
  `id` int NOT NULL AUTO_INCREMENT,
  `host` varchar(64) NOT NULL,
  `port` int NOT NULL,
  `task_id` int NOT NULL,
  `core_count` int DEFAULT NULL,
  `status` tinyint(1) DEFAULT NULL COMMENT '0停止 1启动 2暂停',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
