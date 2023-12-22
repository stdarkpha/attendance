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
DROP DATABASE IF EXISTS `attendance`;
CREATE DATABASE IF NOT EXISTS `attendance` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `attendance`;

-- Dumping structure for table attendance.division
DROP TABLE IF EXISTS `division`;
CREATE TABLE IF NOT EXISTS `division` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `start_salary` int DEFAULT NULL,
  `daily_bonus` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table attendance.division: ~4 rows (approximately)
INSERT INTO `division` (`id`, `name`, `start_salary`, `daily_bonus`) VALUES
	(1, 'Human Resource', 10000000, 100000),
	(2, 'Programmer', 13000000, 300000),
	(4, 'IT Support', 5000000, 100000);

-- Dumping structure for table attendance.log_user
DROP TABLE IF EXISTS `log_user`;
CREATE TABLE IF NOT EXISTS `log_user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `date` date DEFAULT NULL,
  `clock_in` time DEFAULT NULL,
  `clock_out` time DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=79 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table attendance.log_user: ~59 rows (approximately)
INSERT INTO `log_user` (`id`, `user_id`, `date`, `clock_in`, `clock_out`) VALUES
	(20, 2, '2023-12-11', '08:59:45', '16:20:38'),
	(21, 2, '2023-11-01', '09:02:00', '16:01:41'),
	(22, 2, '2023-11-02', '08:25:00', '16:15:35'),
	(23, 2, '2023-11-03', '08:28:00', '16:12:43'),
	(24, 2, '2023-11-04', '08:37:00', '16:01:50'),
	(25, 2, '2023-11-05', '09:04:00', '16:04:18'),
	(26, 2, '2023-11-06', '09:01:00', '16:21:08'),
	(27, 2, '2023-11-08', '08:13:00', '16:14:41'),
	(28, 2, '2023-11-09', '09:05:00', '16:08:06'),
	(29, 2, '2023-11-10', '09:08:00', '16:06:00'),
	(30, 2, '2023-11-11', '08:09:00', '16:27:27'),
	(31, 2, '2023-11-12', '08:56:00', '16:09:57'),
	(32, 2, '2023-11-13', '08:14:00', '16:21:47'),
	(33, 2, '2023-11-14', '08:58:00', '16:28:04'),
	(34, 2, '2023-11-15', '08:49:00', '16:13:38'),
	(35, 2, '2023-11-16', '08:34:00', '16:19:27'),
	(36, 2, '2023-11-17', '08:45:00', '16:16:37'),
	(37, 2, '2023-11-18', '08:42:00', '16:20:49'),
	(38, 2, '2023-11-19', '09:12:00', '16:03:16'),
	(39, 2, '2023-11-20', '08:31:00', '16:00:14'),
	(40, 2, '2023-11-21', '09:09:00', '16:27:12'),
	(41, 2, '2023-11-22', '08:36:00', '16:22:51'),
	(42, 2, '2023-11-23', '08:23:00', '16:07:46'),
	(43, 2, '2023-11-24', '08:47:00', '16:10:22'),
	(44, 2, '2023-11-25', '08:19:00', '16:26:57'),
	(45, 2, '2023-11-26', '08:17:00', '16:05:14'),
	(46, 2, '2023-11-27', '08:21:00', '16:25:02'),
	(47, 2, '2023-11-28', '08:05:00', '16:09:28'),
	(48, 2, '2023-11-29', '08:38:00', '16:27:06'),
	(49, 2, '2023-11-30', '08:52:00', '16:28:14'),
	(50, 2, '2023-12-01', '09:02:00', '16:26:22'),
	(51, 2, '2023-12-02', '08:25:00', '16:10:51'),
	(52, 2, '2023-12-03', '08:28:00', '16:29:55'),
	(53, 2, '2023-12-04', '08:37:00', '16:26:11'),
	(54, 2, '2023-12-05', '09:04:00', '16:01:15'),
	(55, 2, '2023-12-06', '09:01:00', '16:04:07'),
	(56, 2, '2023-12-08', '08:13:00', '16:22:02'),
	(57, 2, '2023-12-09', '09:05:00', '16:24:08'),
	(58, 2, '2023-12-10', '09:08:00', '16:25:30'),
	(59, 2, '2023-12-11', '08:09:00', '16:12:02'),
	(60, 2, '2023-12-12', '08:56:00', '16:28:44'),
	(61, 2, '2023-12-13', '08:14:00', '16:00:27'),
	(62, 2, '2023-12-14', '08:58:00', '16:03:41'),
	(63, 2, '2023-12-15', '08:49:00', '16:18:35'),
	(64, 2, '2023-12-16', '08:34:00', '16:06:23'),
	(65, 2, '2023-12-17', '08:45:00', '16:20:19'),
	(66, 2, '2023-12-18', '08:42:00', '16:22:05'),
	(67, 2, '2023-12-19', '09:12:00', '16:01:22'),
	(68, 2, '2023-12-20', '08:31:00', '16:27:06'),
	(69, 2, '2023-12-21', '09:09:00', '16:21:13'),
	(70, 2, '2023-12-22', '08:36:00', '16:26:42'),
	(71, 2, '2023-12-23', '08:23:00', '16:17:07'),
	(72, 2, '2023-12-24', '08:47:00', '16:22:15'),
	(73, 2, '2023-12-25', '08:19:00', '16:20:10'),
	(74, 2, '2023-12-26', '08:17:00', '16:11:37'),
	(75, 2, '2023-12-27', '08:21:00', '16:29:57'),
	(76, 2, '2023-12-28', '08:05:00', '16:24:15'),
	(77, 2, '2023-12-29', '08:38:00', '16:10:20'),
	(78, 2, '2023-12-30', '08:52:00', '16:00:18');

-- Dumping structure for table attendance.settings
DROP TABLE IF EXISTS `settings`;
CREATE TABLE IF NOT EXISTS `settings` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `value` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table attendance.settings: ~4 rows (approximately)
INSERT INTO `settings` (`id`, `name`, `value`) VALUES
	(1, 'AppName', 'Aplikasi Kehadiran Karyawan'),
	(2, 'ClockIn', '09:00:00'),
	(3, 'ClockOut', '17:00:00'),
	(4, 'LogoApp', 'upj.png');

-- Dumping structure for table attendance.tasks
DROP TABLE IF EXISTS `tasks`;
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
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table attendance.tasks: ~6 rows (approximately)
INSERT INTO `tasks` (`id`, `user_id`, `task_start`, `task_end`, `date`, `status`, `label`, `desc`) VALUES
	(2, 2, '12:04:12', '22:04:14', '2023-11-30', 'Dikerjakan', 'coding', 'Melanjutkan pembuatan modul website'),
	(3, 2, '14:04:12', '22:04:14', '2023-11-30', 'Dikerjakan', 'coding', 'Melanjutkan pembuatan modul website'),
	(4, 2, '22:04:12', '22:04:14', '2023-11-30', 'Dikerjakan', 'coding', 'Melanjutkan pembuatan modul website'),
	(14, 2, '20:48:18', NULL, '2023-12-01', 'Selesai', 'UAS PBO', 'Membuat Design Dashboard'),
	(15, 2, '12:13:19', NULL, '2023-12-02', 'Dikerjakan', 'Mengerjakan UAS PBO', 'Pembuatan scene dashboard Admin'),
	(16, 2, '22:48:47', NULL, '2023-12-03', 'Selesai', 'hadehh', 'wadiaww'),
	(18, 2, '19:31:50', NULL, '2023-12-09', 'Dikerjakan', 'Pepeok', 'pekok'),
	(19, 2, '13:20:38', NULL, '2023-12-11', 'Selesai', 'Mengerjakan Laporan', 'Mengerjakan Laporan');

-- Dumping structure for table attendance.users
DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `uid` varchar(50) DEFAULT NULL,
  `first_name` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `last_name` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `birth` date DEFAULT NULL,
  `phone` varchar(50) DEFAULT NULL,
  `avatar` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'avatar.png',
  `email` varchar(50) DEFAULT NULL,
  `gender` varchar(50) DEFAULT NULL,
  `password` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `role` enum('admin','user') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'user',
  `division_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uid` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table attendance.users: ~3 rows (approximately)
INSERT INTO `users` (`id`, `uid`, `first_name`, `last_name`, `birth`, `phone`, `avatar`, `email`, `gender`, `password`, `role`, `division_id`) VALUES
	(1, '001', 'Administrator', 'Aplikasi', NULL, NULL, 'avatar.png', NULL, NULL, '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'admin', 1),
	(2, '123', 'Farouq Mulya', 'Al Simabua', '2023-08-07', '08984288566', 'farouq-logo.jpg', 'farouqsimabua@gmail.com', 'Laki-Laki', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'user', 2),
	(27, '11220784', 'Muhammad', 'Windri', '2023-12-20', '123123', 'avatar.png', '123123', 'Laki-Laki', '54d5cb2d332dbdb4850293caae4559ce88b65163f1ea5d4e4b3ac49d772ded14', 'user', 1);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
