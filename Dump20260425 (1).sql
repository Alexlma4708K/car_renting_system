-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: localhost    Database: car_rental
-- ------------------------------------------------------
-- Server version	8.0.45

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `bookings`
--

DROP TABLE IF EXISTS `bookings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bookings` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `car_id` int DEFAULT NULL,
  `days` int DEFAULT NULL,
  `status` varchar(20) DEFAULT 'BOOKED',
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `total_price` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bookings`
--

LOCK TABLES `bookings` WRITE;
/*!40000 ALTER TABLE `bookings` DISABLE KEYS */;
INSERT INTO `bookings` VALUES (9,1,1,1,'BOOKED',NULL,NULL,NULL),(10,1,2,1,'BOOKED',NULL,NULL,NULL),(11,1,3,1,'BOOKED',NULL,NULL,NULL),(12,1,4,1,'BOOKED',NULL,NULL,NULL),(13,1,5,1,'BOOKED',NULL,NULL,NULL),(18,6,32,100,'RETURNED','2026-04-21',NULL,NULL),(19,6,2,100,'RETURNED','2026-04-21',NULL,NULL),(20,6,2,100,'RETURNED','2026-04-21',NULL,NULL),(21,6,2,1000000000,'RETURNED','2026-04-21',NULL,NULL),(22,6,32,10,'RETURNED','2026-04-21',NULL,NULL),(23,6,1,1,'RETURNED','2026-04-21',NULL,NULL),(24,6,26,1,'RETURNED','2026-04-21',NULL,NULL),(25,6,1,1,'RETURNED','2026-04-21',NULL,NULL),(26,6,32,1,'RETURNED','2026-04-21',NULL,NULL),(27,6,9,1,'RETURNED','2026-04-21',NULL,NULL),(28,6,1,1,'RETURNED','2026-04-22',NULL,NULL),(29,6,9,2,'RETURNED','2026-04-22',NULL,NULL),(30,6,2,1,'RETURNED','2026-04-22',NULL,NULL),(31,6,3,1,'RETURNED','2026-04-22',NULL,NULL),(32,6,2,2,'RETURNED','2026-04-22',NULL,5998),(33,6,3,2,'RETURNED','2026-04-22',NULL,5398),(34,6,2,3,'RETURNED','2026-04-22','2026-04-22',8997),(35,6,3,6,'RETURNED','2026-04-22','2026-04-22',16194),(36,6,2,3,'RETURNED','2026-04-22','2026-04-22',4500),(37,6,2,1,'RETURNED','2026-04-22','2026-04-22',1500),(38,6,29,1,'RETURNED','2026-04-22','2026-04-22',3000),(39,6,2,1,'RETURNED','2026-04-22','2026-04-22',2999),(40,6,2,1,'RETURNED','2026-04-22','2026-04-22',1500),(41,6,3,1,'RETURNED','2026-04-22','2026-04-22',2200),(42,6,3,1,'RETURNED','2026-04-22','2026-04-22',2200),(43,6,2,1,'RETURNED','2026-04-22','2026-04-22',2999),(44,6,2,5,'RETURNED','2026-04-22','2026-04-22',7500),(45,6,1,3,'RETURNED','2026-04-22','2026-04-22',7500),(46,6,3,1,'RETURNED','2026-04-22','2026-04-22',2699),(47,9,1,1,'RETURNED','2026-04-22','2026-04-22',3999),(48,10,2,1,'BOOKED','2026-04-22',NULL,2999),(49,11,1,1,'RETURNED','2026-04-22','2026-04-22',3999),(50,11,5,2,'BOOKED','2026-04-22',NULL,2600),(51,11,3,1,'BOOKED','2026-04-25',NULL,1200);
/*!40000 ALTER TABLE `bookings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cars`
--

DROP TABLE IF EXISTS `cars`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cars` (
  `id` int NOT NULL AUTO_INCREMENT,
  `brand` varchar(50) NOT NULL,
  `model` varchar(60) NOT NULL,
  `price` double NOT NULL,
  `available` tinyint(1) NOT NULL,
  `image_filename` varchar(255) DEFAULT 'default_car.jpg',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cars`
--

LOCK TABLES `cars` WRITE;
/*!40000 ALTER TABLE `cars` DISABLE KEYS */;
INSERT INTO `cars` VALUES (1,'Toyota','Innova',2500,1,'Toyota_Innova.jpeg'),(2,'Honda','City',1500,0,'Honda_City.jpg'),(3,'Maruti Suzuki','Swift',1200,0,'MarutiSuzuki_Swift.jpg'),(4,'Hyundai','i20',1400,1,'Hyndai_i20.jpeg'),(5,'Tata','Altroz',1300,0,'Tata_Altroz.jpeg'),(6,'Maruti Suzuki','WagonR',1000,1,'MarutiSuzuki_WagnoR.jpg'),(7,'Hyundai','Grand i10 Nios',1100,1,'Hyundai_Grand i10 Nios.jpg'),(8,'Maruti Suzuki','Baleno',1300,1,'Maruti Suzuki_Baleno.jpg'),(9,'Tata','Tiago',1000,1,'Tata_Tiago.jpg'),(10,'Maruti Suzuki','S-Presso',900,1,'Maruti Suzuki_S-Presso.jpg'),(11,'Maruti Suzuki','Dzire',1500,1,'MarutiSuzuki_Dzire.jpg'),(12,'Honda','Amaze',1500,1,'Honda_Amaze.jpeg'),(13,'Hyundai','Aura',1400,1,'Hyundai_Aura.jpg'),(14,'Toyota','Etios',1400,1,'Toyota_Etios.jpg'),(15,'Tata','Tigor',1300,1,'Tata_Tigor.jpg'),(16,'Tata','Punch',1200,1,'Tata_Punch.jpg'),(17,'Nissan','Magnite',1500,1,'Nissan_Magnite.jpg'),(18,'Renault','Kiger',1500,1,'Renault_Kiger.jpg'),(19,'Hyundai','Venue',1800,1,'Hyundai_Venue.jpg'),(20,'Maruti Suzuki','Brezza',1800,1,'MarutiSuzuki_Brezza.jpg'),(21,'Kia','Sonet',1800,1,'Kia_Sonet.jpg'),(22,'Hyundai','Creta',2200,1,'Hyundai_Creta.jpg'),(23,'Kia','Seltos',2200,1,'Kia_Seltos.jpg'),(24,'Tata','Nexon',2000,1,'Tata_Nexon.jpg'),(25,'Mahindra','XUV300',2000,1,'Mahindra_XUV300.jpg'),(26,'Toyota','Hyryder',2200,1,'Toyota_Hyryder.jpg'),(27,'Toyota','Innova Crysta',3000,1,'Toyota_Innova_Crysyta.jpeg'),(28,'Toyota','Innova Hycross',3200,1,'Toyota_Innova Hycross.jpg'),(29,'Maruti Suzuki','Ertiga',2000,1,'MarutiSuzuki _Ertiga.jpeg'),(30,'Renault','Triber',1700,1,'Renault_Triber.jpg'),(31,'Toyota','Fortuner',4500,1,'Toyota_Fortuner.jpg'),(32,'BMW','5 Series',7000,1,'BMW_5_Series.jpeg');
/*!40000 ALTER TABLE `cars` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `role` varchar(20) DEFAULT 'user',
  `license_number` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (3,'admin','1234','admin',NULL),(11,'Arnav','12345','customer','UP1234567890123');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-25 19:12:54
