/*
Navicat MariaDB Data Transfer

Source Server         : 192.168.1.61
Source Server Version : 100123
Source Host           : 192.168.1.61:3306
Source Database       : demo

Target Server Type    : MariaDB
Target Server Version : 100123
File Encoding         : 65001

Date: 2018-08-17 20:38:40
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for logging_event
-- ----------------------------
DROP TABLE IF EXISTS `logging_event`;
CREATE TABLE `logging_event` (
  `timestmp` bigint(20) NOT NULL,
  `formatted_message` text NOT NULL,
  `logger_name` varchar(254) NOT NULL,
  `level_string` varchar(254) NOT NULL,
  `thread_name` varchar(254) DEFAULT NULL,
  `reference_flag` smallint(6) DEFAULT NULL,
  `arg0` varchar(1000) DEFAULT NULL,
  `arg1` varchar(1000) DEFAULT NULL,
  `arg2` varchar(1000) DEFAULT NULL,
  `arg3` varchar(1000) DEFAULT NULL,
  `caller_filename` varchar(254) NOT NULL,
  `caller_class` varchar(254) NOT NULL,
  `caller_method` varchar(254) NOT NULL,
  `caller_line` char(4) NOT NULL,
  `event_id` bigint(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`event_id`)
) ENGINE=InnoDB AUTO_INCREMENT=145797 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for logging_event_exception
-- ----------------------------
DROP TABLE IF EXISTS `logging_event_exception`;
CREATE TABLE `logging_event_exception` (
  `event_id` bigint(20) NOT NULL,
  `i` smallint(6) NOT NULL,
  `trace_line` varchar(254) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`event_id`,`i`),
  CONSTRAINT `logging_event_exception_ibfk_1` FOREIGN KEY (`event_id`) REFERENCES `logging_event` (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for logging_event_property
-- ----------------------------
DROP TABLE IF EXISTS `logging_event_property`;
CREATE TABLE `logging_event_property` (
  `event_id` bigint(20) NOT NULL,
  `mapped_key` varchar(254) COLLATE utf8_bin NOT NULL,
  `mapped_value` text COLLATE utf8_bin,
  PRIMARY KEY (`event_id`,`mapped_key`),
  CONSTRAINT `logging_event_property_ibfk_1` FOREIGN KEY (`event_id`) REFERENCES `logging_event` (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `ID` int(11) NOT NULL,
  `NAME` varchar(63) COLLATE utf8_bin NOT NULL,
  `PASSWORD` varchar(255) COLLATE utf8_bin NOT NULL,
  `SALT` varchar(255) COLLATE utf8_bin NOT NULL,
  `EMAIL` varchar(127) COLLATE utf8_bin NOT NULL,
  `CELL_PHONE` varchar(63) COLLATE utf8_bin DEFAULT NULL,
  `WECHAT_OPEN_ID` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `ROLE` int(11) DEFAULT NULL,
  `DEPARTMENT` int(11) DEFAULT NULL,
  `STATUS` int(11) DEFAULT NULL,
  `GMT_CREATE` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `GMT_LAST_UPDATE` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `GMT_LAST_LOGIN` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
