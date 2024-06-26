CREATE TABLE IF NOT EXISTS `configs` (
    `id` varchar(100) NOT NULL,
    `app` varchar(100) NOT NULL,
    `namespace` varchar(100) NOT NULL,
    `environment` varchar(100) NOT NULL,
    `config_key` varchar(255) NOT NULL,
    `config_val` varchar(500) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `version` (
    `key` VARCHAR(300) NOT NULL,
    `version` BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `cluster_lock` (
    `id` INT NOT NULL,
    `instance_id` VARCHAR(100) NOT NULL,
    `timeout` DATETIME NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO `cluster_lock` (`id`, `instance_id`, `timeout`) VALUES (0, '', '1970-01-01 00:00:00');