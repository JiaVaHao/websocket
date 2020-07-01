/*
Navicat MySQL Data Transfer

Source Server         : Aliyun
Source Server Version : 50727
Source Host           : 47.98.155.39:3306
Source Database       : doboto_chat

Target Server Type    : MYSQL
Target Server Version : 50727
File Encoding         : 65001

Date: 2020-05-18 09:45:24
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for friend
-- ----------------------------
DROP TABLE IF EXISTS `friend`;
CREATE TABLE `friend` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ownerId` int(11) DEFAULT NULL,
  `friendId` int(11) DEFAULT NULL,
  `is_accept` int(5) DEFAULT NULL,
  `is_new` int(5) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ownerId` (`ownerId`),
  KEY `friendId` (`friendId`),
  CONSTRAINT `friend_ibfk_1` FOREIGN KEY (`ownerId`) REFERENCES `user` (`id`),
  CONSTRAINT `friend_ibfk_2` FOREIGN KEY (`friendId`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=113 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `senderId` int(11) NOT NULL,
  `receiverId` int(11) NOT NULL,
  `msg_type` int(11) NOT NULL DEFAULT '1' COMMENT '1-文本，2-图像，3-影像，4-文件，5-声音',
  `content` varchar(255) NOT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_read` int(11) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `senderId` (`senderId`),
  KEY `receiverId` (`receiverId`),
  CONSTRAINT `message_ibfk_1` FOREIGN KEY (`senderId`) REFERENCES `user` (`id`),
  CONSTRAINT `message_ibfk_2` FOREIGN KEY (`receiverId`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `image_path` varchar(255) DEFAULT NULL,
  `age` int(11) DEFAULT '0',
  `city` varchar(255) DEFAULT '其他',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1594 DEFAULT CHARSET=utf8;
