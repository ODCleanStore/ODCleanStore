CREATE TABLE `users`(	id int NOT NULL AUTO_INCREMENT PRIMARY KEY,	username varchar(255),	email varchar(255),	createdAt timestamp)ENGINE=INNODB;INSERT INTO `users` (`username`, `email`, `createdAt`) VALUES ('dusanr', 'dusanr@odcleanstore.cz', '2012-03-11 15:45:00');INSERT INTO `users` (`username`, `email`, `createdAt`) VALUES ('jakubd', 'jakubd@odcleanstore.cz', '2012-03-11 15:51:00');