CREATE TABLE IF NOT EXISTS `configs` (
    `id` varchar(100) NOT NULL,
    `app` varchar(100) NOT NULL,
    `namespace` varchar(100) NOT NULL,
    `environment` varchar(100) NOT NULL,
    `config_key` varchar(255) NOT NULL,
    `config_val` varchar(500) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;