-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: todolist
-- ------------------------------------------------------
-- Server version	8.0.40

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
-- Table structure for table `items`
--

DROP TABLE IF EXISTS items;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE items (
  item_id int NOT NULL,
  label varchar(45) DEFAULT NULL,
  PRIMARY KEY (item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `items`
--

LOCK TABLES items WRITE;
/*!40000 ALTER TABLE items DISABLE KEYS */;
INSERT INTO items VALUES (0,'Milk'),(1,'Cookies'),(2,'Jumping jacks'),(3,'Push ups'),(4,'Honey'),(5,'Ginger');
/*!40000 ALTER TABLE items ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `list_items`
--

DROP TABLE IF EXISTS list_items;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE list_items (
  list_id int NOT NULL,
  item_id int NOT NULL,
  PRIMARY KEY (list_id,item_id),
  KEY fki_item_id_idx (item_id),
  CONSTRAINT fk_list_id_2 FOREIGN KEY (list_id) REFERENCES lists (list_id),
  CONSTRAINT fki_item_id_2 FOREIGN KEY (item_id) REFERENCES items (item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `list_items`
--

LOCK TABLES list_items WRITE;
/*!40000 ALTER TABLE list_items DISABLE KEYS */;
INSERT INTO list_items VALUES (0,0),(0,1),(1,2),(1,3),(2,4),(2,5);
/*!40000 ALTER TABLE list_items ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `list_name`
--

DROP TABLE IF EXISTS list_name;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE list_name (
  list_id int NOT NULL,
  label varchar(45) DEFAULT NULL,
  PRIMARY KEY (list_id),
  CONSTRAINT fk_list_id FOREIGN KEY (list_id) REFERENCES lists (list_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `list_name`
--

LOCK TABLES list_name WRITE;
/*!40000 ALTER TABLE list_name DISABLE KEYS */;
INSERT INTO list_name VALUES (0,'Groceries'),(1,'Sport'),(2,'Groceries');
/*!40000 ALTER TABLE list_name ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lists`
--

DROP TABLE IF EXISTS lists;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE lists (
  list_id int NOT NULL,
  user_id int DEFAULT NULL,
  PRIMARY KEY (list_id),
  KEY fk_used_id_idx (user_id),
  CONSTRAINT fk_used_id FOREIGN KEY (user_id) REFERENCES `user` (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lists`
--

LOCK TABLES lists WRITE;
/*!40000 ALTER TABLE lists DISABLE KEYS */;
INSERT INTO lists VALUES (0,0),(1,0),(2,1);
/*!40000 ALTER TABLE lists ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS user;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  user_id int NOT NULL,
  user_name varchar(45) DEFAULT NULL,
  PRIMARY KEY (user_id),
  UNIQUE KEY user_name_UNIQUE (user_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES user WRITE;
/*!40000 ALTER TABLE user DISABLE KEYS */;
INSERT INTO user VALUES (0,'Bob'),(1,'John');
/*!40000 ALTER TABLE user ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-10-21 12:34:16
