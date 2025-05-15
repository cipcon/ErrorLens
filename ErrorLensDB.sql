-- --------------------------------------------------------
-- Host:                         localhost
-- Server-Version:               10.6.18-MariaDB-0ubuntu0.22.04.1 - Ubuntu 22.04
-- Server-Betriebssystem:        debian-linux-gnu
-- HeidiSQL Version:             12.6.0.6765
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Exportiere Datenbank-Struktur für ErrorLens
CREATE DATABASE IF NOT EXISTS `ErrorLens` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;
USE `ErrorLens`;

-- Exportiere Struktur von Tabelle ErrorLens.logeintrag
CREATE TABLE IF NOT EXISTS `logeintrag` (
  `logeintrag_id` int(11) NOT NULL,
  `logeintrag_beschreibung` text NOT NULL,
  `logfile_id` int(11) NOT NULL,
  `pattern_id` int(11) NOT NULL,
  `gefunden_am` date NOT NULL,
  KEY `logfile_id` (`logfile_id`),
  KEY `pattern_id` (`pattern_id`),
  CONSTRAINT `logeintrag_ibfk_1` FOREIGN KEY (`logfile_id`) REFERENCES `logfile` (`logfile_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `logeintrag_ibfk_2` FOREIGN KEY (`pattern_id`) REFERENCES `pattern` (`pattern_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Exportiere Daten aus Tabelle ErrorLens.logeintrag: ~0 rows (ungefähr)

-- Exportiere Struktur von Tabelle ErrorLens.logfile
CREATE TABLE IF NOT EXISTS `logfile` (
  `logfile_id` int(11) NOT NULL AUTO_INCREMENT,
  `logfile_name` varchar(50) NOT NULL,
  `logfile_pfad` varchar(255) NOT NULL,
  `geaendert_am` date NOT NULL,
  `erstellungs_datum` date NOT NULL,
  `letze_zeile` int(11) NOT NULL,
  PRIMARY KEY (`logfile_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Exportiere Daten aus Tabelle ErrorLens.logfile: ~0 rows (ungefähr)

-- Exportiere Struktur von Tabelle ErrorLens.logfile_pattern
CREATE TABLE IF NOT EXISTS `logfile_pattern` (
  `logfile_id` int(11) NOT NULL,
  `pattern_id` int(11) NOT NULL,
  `reihenfolge_platz_pattern` int(11) NOT NULL,
  KEY `logfile_id` (`logfile_id`),
  KEY `pattern_id` (`pattern_id`),
  CONSTRAINT `logfile_pattern_ibfk_1` FOREIGN KEY (`logfile_id`) REFERENCES `logfile` (`logfile_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `logfile_pattern_ibfk_2` FOREIGN KEY (`pattern_id`) REFERENCES `pattern` (`pattern_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Exportiere Daten aus Tabelle ErrorLens.logfile_pattern: ~0 rows (ungefähr)

-- Exportiere Struktur von Tabelle ErrorLens.passwort
CREATE TABLE IF NOT EXISTS `passwort` (
  `passwort` varchar(100) NOT NULL,
  `datum_geandert` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Exportiere Daten aus Tabelle ErrorLens.passwort: ~0 rows (ungefähr)

-- Exportiere Struktur von Tabelle ErrorLens.pattern
CREATE TABLE IF NOT EXISTS `pattern` (
  `pattern_id` int(11) NOT NULL AUTO_INCREMENT,
  `pattern_name` varchar(50) NOT NULL,
  `pattern` varchar(100) NOT NULL,
  `pattern_beschreibung` varchar(255) NOT NULL,
  `schweregrad` enum('MEDIUM','HIGH','CRITICAL') DEFAULT NULL,
  PRIMARY KEY (`pattern_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Exportiere Daten aus Tabelle ErrorLens.pattern: ~0 rows (ungefähr)

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
