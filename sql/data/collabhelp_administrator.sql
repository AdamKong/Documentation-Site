-- MySQL dump 10.13  Distrib 5.7.17, for macos10.12 (x86_64)
--
-- Host: localhost    Database: collabhelp
-- ------------------------------------------------------
-- Server version	5.7.21

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Dumping data for table `administrator`
--

LOCK TABLES `administrator` WRITE;
/*!40000 ALTER TABLE `administrator` DISABLE KEYS */;
INSERT INTO `administrator` VALUES (1,'superadmin','e10adc3949ba59abbe56e057f20f883e','admin@example1.com','FirstName LastName','008613700000001',1,1,'This is a test super admin account! test memory usage!','2018-09-28 08:55:17'),(2,'subadmin1','e10adc3949ba59abbe56e057f20f883e','admin@example.com','Adam M. Kong','008613700000001',0,1,'This is another test sub-admin account!','2018-09-28 08:55:29'),(3,'subadmin2','e10adc3949ba59abbe56e057f20f883e','adkong@cisco.com','Adam M. Kong','123456789',0,1,'This is the third test account.','2018-09-28 08:56:01'),(4,'subadmin3','e10adc3949ba59abbe56e057f20f883e','adkong@cisco.com','Adam M. Kong','',0,1,'','2018-09-28 09:23:03'),(6,'subadmin4','e10adc3949ba59abbe56e057f20f883e','adkong@cisco.com','Adam M. Kong','123456',0,1,'This is another test sub admin account.','2018-09-30 20:27:47'),(7,'subadmin5','84bc888465647a1e9df05b48b06cf021','adkong@cisco.com','','',0,1,'','2018-10-03 20:35:56'),(8,'subadmin6','e10adc3949ba59abbe56e057f20f883e','adkong@cisco.com','','',0,1,'','2018-10-13 22:26:34'),(9,'subadmin7','e10adc3949ba59abbe56e057f20f883e','adkong@cisco.com','','',0,1,'','2018-10-13 22:26:55'),(10,'subadmin8','e10adc3949ba59abbe56e057f20f883e','adkong@cisco.com','','',0,1,'','2018-10-13 22:27:12'),(11,'subadmin9','e10adc3949ba59abbe56e057f20f883e','adkong@cisco.com','','',0,1,'','2018-10-13 22:27:30'),(12,'subadmin10','e10adc3949ba59abbe56e057f20f883e','adkong@cisco.com','','',0,1,'','2018-10-13 22:27:46'),(13,'subadmin11','e10adc3949ba59abbe56e057f20f883e','adkong@cisco.com','','',0,1,'','2018-10-13 22:39:49'),(14,'subadmin12','e10adc3949ba59abbe56e057f20f883e','adkong@cisco.com','','',0,1,'','2018-10-13 22:55:49'),(15,'subadmin13','e10adc3949ba59abbe56e057f20f883e','adkong@cisco.com','','',0,1,'','2018-10-13 22:58:05'),(16,'subadmin14','e10adc3949ba59abbe56e057f20f883e','adkong@cisco.com','','',0,1,'','2018-10-13 22:58:28'),(17,'subadmin15','e10adc3949ba59abbe56e057f20f883e','adkong@cisco.com','','',0,1,'','2018-10-13 22:58:49'),(18,'subadmin16','e10adc3949ba59abbe56e057f20f883e','adkong@ciscc.com','','',0,1,'','2018-10-13 22:59:07'),(19,'subadmin17','e10adc3949ba59abbe56e057f20f883e','adkong@cisco.com','Adam M. Kong','123456789',0,1,'','2018-10-18 15:24:53'),(20,'subadmin18','e10adc3949ba59abbe56e057f20f883e','adkong@cisco.com','Adam M. Kong','1234567890',0,1,'','2018-10-19 13:24:31');
/*!40000 ALTER TABLE `administrator` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-11-07 17:01:41
