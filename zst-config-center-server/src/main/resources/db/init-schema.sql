CREATE TABLE IF NOT EXISTS `configs` (
    `id` varchar(100) NOT NULL,
    `key` varchar(255) NOT NULL,
    `val` varchar(500) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `configs_key_IDX` (`key`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;