-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.0.30 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             12.1.0.6537
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for attendance
CREATE DATABASE IF NOT EXISTS `attendance` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `attendance`;

-- Dumping structure for table attendance.division
CREATE TABLE IF NOT EXISTS `division` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `start_salary` int DEFAULT NULL,
  `daily_bonus` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table attendance.division: ~4 rows (approximately)
DELETE FROM `division`;
INSERT INTO `division` (`id`, `name`, `start_salary`, `daily_bonus`) VALUES
	(1, 'Human Resource', 10000000, 100000),
	(2, 'Programmer', 30000000, 300000),
	(3, 'Tukang Sapu', 3000000, 50000),
	(4, 'IT Support', 5000000, 100000);

-- Dumping structure for table attendance.log_user
CREATE TABLE IF NOT EXISTS `log_user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `date` date DEFAULT NULL,
  `clock_in` time DEFAULT NULL,
  `clock_out` time DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table attendance.log_user: ~9 rows (approximately)
DELETE FROM `log_user`;
INSERT INTO `log_user` (`id`, `user_id`, `date`, `clock_in`, `clock_out`) VALUES
	(1, 2, '2023-11-29', '06:08:26', '16:08:33'),
	(3, 2, '2023-11-28', '06:08:26', '17:26:06'),
	(4, 2, '2023-11-20', '06:08:26', '18:06:42'),
	(6, 2, '2023-12-02', '09:36:05', '16:48:59'),
	(7, 2, '2023-12-01', '09:36:05', '12:10:45'),
	(8, 2, '2023-11-30', '21:59:24', '23:19:20'),
	(9, 2, '2023-12-03', '09:36:05', '16:48:59'),
	(15, 14, '2023-12-04', '07:13:47', '07:13:51'),
	(16, 2, '2023-12-04', '07:18:06', '08:14:01');

-- Dumping structure for table attendance.settings
CREATE TABLE IF NOT EXISTS `settings` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `value` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table attendance.settings: ~4 rows (approximately)
DELETE FROM `settings`;
INSERT INTO `settings` (`id`, `name`, `value`) VALUES
	(1, 'AppName', 'Aplikasi Kehadiran'),
	(2, 'ClockIn', '09:00:00'),
	(3, 'ClockOut', '17:00:00'),
	(4, 'LogoApp', 'upj.png');

-- Dumping structure for table attendance.tasks
CREATE TABLE IF NOT EXISTS `tasks` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `task_start` time DEFAULT NULL,
  `task_end` time DEFAULT NULL,
  `date` date DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `label` varchar(255) DEFAULT NULL,
  `desc` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table attendance.tasks: ~6 rows (approximately)
DELETE FROM `tasks`;
INSERT INTO `tasks` (`id`, `user_id`, `task_start`, `task_end`, `date`, `status`, `label`, `desc`) VALUES
	(2, 2, '12:04:12', '22:04:14', '2023-11-30', 'Dikerjakan', 'coding', 'Melanjutkan pembuatan modul website'),
	(3, 2, '14:04:12', '22:04:14', '2023-11-30', 'Dikerjakan', 'coding', 'Melanjutkan pembuatan modul website'),
	(4, 2, '22:04:12', '22:04:14', '2023-11-30', 'Dikerjakan', 'coding', 'Melanjutkan pembuatan modul website'),
	(14, 2, '20:48:18', NULL, '2023-12-01', 'Selesai', 'UAS PBO', 'Membuat Design Dashboard'),
	(15, 2, '12:13:19', NULL, '2023-12-02', 'Dikerjakan', 'Mengerjakan UAS PBO', 'Pembuatan scene dashboard Admin'),
	(16, 2, '22:48:47', NULL, '2023-12-03', 'Selesai', 'hadehh', 'wadiaww');

-- Dumping structure for table attendance.users
CREATE TABLE IF NOT EXISTS `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `uid` varchar(50) DEFAULT NULL,
  `first_name` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `last_name` varchar(50) DEFAULT NULL,
  `birth` date DEFAULT NULL,
  `phone` varchar(50) DEFAULT NULL,
  `avatar` varchar(50) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `gender` varchar(50) DEFAULT NULL,
  `password` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `role` enum('admin','user') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'user',
  `division_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uid` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table attendance.users: ~3 rows (approximately)
DELETE FROM `users`;
INSERT INTO `users` (`id`, `uid`, `first_name`, `last_name`, `birth`, `phone`, `avatar`, `email`, `gender`, `password`, `role`, `division_id`) VALUES
	(1, '001', 'Admin', 'Master', NULL, NULL, 'avatar.png', NULL, NULL, '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'admin', 1),
	(2, '123', 'Farouq Mulya', 'Al Simabua', '2023-08-07', '08984288566', 'avatar.png', 'farouqsimabua@gmail.com', 'Laki-Laki', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'user', 2),
	(24, '12', 'Ngehexbol', 'Terbool bool', '2023-12-03', '02150137467', NULL, 'asdasd', 'Laki-Laki', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'user', 4);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
