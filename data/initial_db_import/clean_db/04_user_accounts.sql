/* username: adm, password: adm, roles: ADM */
INSERT INTO DB.ODCLEANSTORE.USERS (username, email, passwordHash, salt, firstname, surname) 
VALUES (n'adm', n'adm@example.com', n'0e2aeeeb4125bea8d439c61050a08b52', n'salted', n'The', n'Administrator');

/* username: scraper, password: reparcs, roles: SCR */
INSERT INTO DB.ODCLEANSTORE.USERS (username, email, passwordHash, salt, firstname, surname)
VALUES (n'scraper', n'scraper@example.com', n'a83d2a0a4ce1839c6884cf1238ce9da6', n'salted', n'The', n'Scraper');

INSERT INTO DB.ODCLEANSTORE.ROLES_ASSIGNED_TO_USERS VALUES (
	(SELECT id FROM DB.ODCLEANSTORE.USERS WHERE username = n'adm'),
	(SELECT id FROM DB.ODCLEANSTORE.ROLES WHERE label = n'ADM'));
INSERT INTO DB.ODCLEANSTORE.ROLES_ASSIGNED_TO_USERS VALUES (
	(SELECT id FROM DB.ODCLEANSTORE.USERS WHERE username = n'adm'),
	(SELECT id FROM DB.ODCLEANSTORE.ROLES WHERE label = n'PIC'));
INSERT INTO DB.ODCLEANSTORE.ROLES_ASSIGNED_TO_USERS VALUES (
	(SELECT id FROM DB.ODCLEANSTORE.USERS WHERE username = n'adm'),
	(SELECT id FROM DB.ODCLEANSTORE.ROLES WHERE label = n'ONC'));
INSERT INTO DB.ODCLEANSTORE.ROLES_ASSIGNED_TO_USERS VALUES (
	(SELECT id FROM DB.ODCLEANSTORE.USERS WHERE username = n'scraper'),
	(SELECT id FROM DB.ODCLEANSTORE.ROLES WHERE label = n'SCR'));


