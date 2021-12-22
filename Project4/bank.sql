-- MySQL dump 10.13  Distrib 8.0.27, for Win64 (x86_64)
--
-- Host: localhost    Database: bank
-- ------------------------------------------------------
-- Server version	8.0.27

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- company databases
DROP DATABASE IF EXISTS `bank`;
CREATE DATABASE IF NOT EXISTS `bank` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `bank`;

--
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `account` (
  `Number` char(6) NOT NULL,
  `balance` bigint NOT NULL,
  `Open_date` char(8) NOT NULL,
  `Password` char(6) NOT NULL,
  `Ssn` char(6) NOT NULL,
  PRIMARY KEY (`Number`),
  KEY `Ssn` (`Ssn`),
  CONSTRAINT `account_ibfk_1` FOREIGN KEY (`Ssn`) REFERENCES `customer` (`Ssn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES ('000001',51000,'20211010','000001','010101'),('010101',46800,'20210606','010101','010101');
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
  `Ssn` char(6) NOT NULL,
  `Name` varchar(15) NOT NULL,
  `Si_do` varchar(20) DEFAULT NULL,
  `Si_gun_gu` varchar(15) DEFAULT NULL,
  `Eup_myeon_dong` varchar(20) DEFAULT NULL,
  `Detailed_address` varchar(70) DEFAULT NULL,
  `Mssn` char(6) NOT NULL,
  PRIMARY KEY (`Ssn`),
  KEY `Mssn` (`Mssn`),
  CONSTRAINT `customer_ibfk_1` FOREIGN KEY (`Mssn`) REFERENCES `manager` (`Ssn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES ('010101','yoon','서울시','성동구','용답동','abc','111111'),('961225','홍길동','경기도','이천시','창전동','cba','111111');
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `history`
--

DROP TABLE IF EXISTS `history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `history` (
  `Date` char(8) NOT NULL,
  `Amount` int NOT NULL,
  `Anum` char(6) NOT NULL,
  `Type` char(4) NOT NULL,
  PRIMARY KEY (`Date`,`Amount`,`Anum`,`Type`),
  KEY `Anum` (`Anum`),
  CONSTRAINT `history_ibfk_1` FOREIGN KEY (`Anum`) REFERENCES `account` (`Number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `history`
--

LOCK TABLES `history` WRITE;
/*!40000 ALTER TABLE `history` DISABLE KEYS */;
INSERT INTO `history` VALUES ('20211201',1000,'000001','입금'),('20211203',50000,'000001','입금'),('20211201',525,'010101','출금'),('20211201',1000,'010101','출금'),('20211202',525,'010101','입금'),('20211202',2525,'010101','입금'),('20211203',200,'010101','입금'),('20211203',200,'010101','출금'),('20211203',1000,'010101','입금'),('20211203',1000,'010101','출금'),('20211203',50000,'010101','출금'),('20211203',95000,'010101','입금');
/*!40000 ALTER TABLE `history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `manager`
--

DROP TABLE IF EXISTS `manager`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `manager` (
  `Ssn` char(6) NOT NULL,
  `Name` varchar(15) NOT NULL,
  `Password` char(6) NOT NULL,
  PRIMARY KEY (`Ssn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `manager`
--

LOCK TABLES `manager` WRITE;
/*!40000 ALTER TABLE `manager` DISABLE KEYS */;
INSERT INTO `manager` VALUES ('111111','yoon','111111'),('999999','master','999999');
/*!40000 ALTER TABLE `manager` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `phone_number`
--

DROP TABLE IF EXISTS `phone_number`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `phone_number` (
  `phone_number` char(11) NOT NULL,
  `Ssn` char(6) NOT NULL,
  PRIMARY KEY (`phone_number`),
  KEY `Ssn` (`Ssn`),
  CONSTRAINT `phone_number_ibfk_1` FOREIGN KEY (`Ssn`) REFERENCES `customer` (`Ssn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `phone_number`
--

LOCK TABLES `phone_number` WRITE;
/*!40000 ALTER TABLE `phone_number` DISABLE KEYS */;
INSERT INTO `phone_number` VALUES ('01066666666','010101'),('01012121212','961225');
/*!40000 ALTER TABLE `phone_number` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-12-03 16:11:24
