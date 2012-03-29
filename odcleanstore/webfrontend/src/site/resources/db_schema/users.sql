CREATE TABLE `users`(	id int NOT NULL AUTO_INCREMENT PRIMARY KEY,	username varchar(255),	password varchar(255),	email varchar(255),	createdAt timestamp)ENGINE=INNODB;INSERT INTO `users` (`username`, `password`, `email`, `createdAt`) VALUES ('dusanr', 'dusanrpwd', 'dusanr@odcleanstore.cz', '2012-03-11 15:45:00');INSERT INTO `users` (`username`, `password`, `email`, `createdAt`) VALUES ('jakubd', 'jakubdpwd', 'jakubd@odcleanstore.cz', '2012-03-11 15:51:00');CREATE TABLE `roles`(	id int NOT NULL AUTO_INCREMENT PRIMARY KEY,	label varchar(255),	description text)ENGINE=INNODB;INSERT INTO `roles` (`label`, `description`) VALUES ('SCR', 'Scrapper');INSERT INTO `roles` (`label`, `description`) VALUES ('ONC', 'Ontology creator');INSERT INTO `roles` (`label`, `description`) VALUES ('POC', 'Policy creator');INSERT INTO `roles` (`label`, `description`) VALUES ('ADM', 'Administrator');CREATE TABLE `users_roles`(	user_id int NOT NULL,	role_id int NOT NULL,		PRIMARY KEY (user_id, role_id),	FOREIGN KEY (user_id) REFERENCES users(id),	FOREIGN KEY (role_id) REFERENCES roles(id))ENGINE=INNODB;INSERT INTO `users_roles` VALUES (1, 4);INSERT INTO `users_roles` VALUES (2, 2);INSERT INTO `users_roles` VALUES (2, 3);