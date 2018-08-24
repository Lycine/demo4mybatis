/*
Navicat MariaDB Data Transfer

Source Server         : 192.168.1.61
Source Server Version : 100123
Source Host           : 192.168.1.61:3306
Source Database       : MCPLM

Target Server Type    : MariaDB
Target Server Version : 100123
File Encoding         : 65001

Date: 2018-08-19 11:23:56
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for commodity
-- ----------------------------
DROP TABLE IF EXISTS `commodity`;
CREATE TABLE `commodity` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `COMMODITY_ID` int(11) DEFAULT NULL COMMENT '商品ID',
  `COMMODITY_NAME` varchar(63) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '商品名',
  `LOCATION` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '商品所在地点',
  `PRICE` double(11,3) DEFAULT NULL COMMENT '商品价格',
  `UNIT` varchar(11) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '价格单位（两，斤，千克，件）',
  `CATEGORY` varchar(63) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '商品种类',
  `BARCODE` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '商品条形码',
  `CURRENCY` varchar(11) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '币种',
  `EXCHANGE_RATE` double(11,3) DEFAULT NULL,
  `REMARK` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '备注',
  `GMT_REAL` timestamp NULL DEFAULT NULL COMMENT '商品记录对应的时间',
  `GMT_CREATE` timestamp NULL DEFAULT NULL COMMENT '创建这条记录的时间',
  `BROWSER_USER_AGENT` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

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
) ENGINE=InnoDB AUTO_INCREMENT=146360 DEFAULT CHARSET=utf8;

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
