/* username: adm, password: adm, roles: ADM */
INSERT INTO DB.ODCLEANSTORE.USERS (username, email, passwordHash, salt, firstname, surname) 
VALUES (n'adm', n'adm@odcleanstore.cz', n'0e2aeeeb4125bea8d439c61050a08b52', n'salted', n'The', n'Administrator');

/* username: poc, password: poc, roles: POC */
INSERT INTO DB.ODCLEANSTORE.USERS (username, email, passwordHash, salt, firstname, surname) 
VALUES (n'poc', n'poc@odcleanstore.cz', n'247cd1ddd4858349720e59486a532100', n'salted', n'The', n'Policy Creator');

/* username: onc, password: onc, roles: ONC */
INSERT INTO DB.ODCLEANSTORE.USERS (username, email, passwordHash, salt, firstname, surname)
VALUES (n'onc', n'onc@odcleanstore.cz', n'3ff18d5018130a9bf9fa8fc974e9ae3a', n'salted', n'The', n'Ontology Creator');

/* username: scraper, password: reparcs, roles: POC */
INSERT INTO DB.ODCLEANSTORE.USERS (username, email, passwordHash, salt, firstname, surname)
VALUES (n'scraper', n'scraper@odcleanstore.cz', n'a83d2a0a4ce1839c6884cf1238ce9da6', n'salted', n'The', n'Scraper');

INSERT INTO DB.ODCLEANSTORE.ROLES_ASSIGNED_TO_USERS VALUES (
	(SELECT id FROM DB.ODCLEANSTORE.USERS WHERE username = n'adm'),
	(SELECT id FROM DB.ODCLEANSTORE.ROLES WHERE label = n'ADM'));
INSERT INTO DB.ODCLEANSTORE.ROLES_ASSIGNED_TO_USERS VALUES (
	(SELECT id FROM DB.ODCLEANSTORE.USERS WHERE username = n'adm'),
	(SELECT id FROM DB.ODCLEANSTORE.ROLES WHERE label = n'POC'));
INSERT INTO DB.ODCLEANSTORE.ROLES_ASSIGNED_TO_USERS VALUES (
	(SELECT id FROM DB.ODCLEANSTORE.USERS WHERE username = n'adm'),
	(SELECT id FROM DB.ODCLEANSTORE.ROLES WHERE label = n'ONC'));
INSERT INTO DB.ODCLEANSTORE.ROLES_ASSIGNED_TO_USERS VALUES (
	(SELECT id FROM DB.ODCLEANSTORE.USERS WHERE username = n'poc'),
	(SELECT id FROM DB.ODCLEANSTORE.ROLES WHERE label = n'POC'));
INSERT INTO DB.ODCLEANSTORE.ROLES_ASSIGNED_TO_USERS VALUES (
	(SELECT id FROM DB.ODCLEANSTORE.USERS WHERE username = n'onc'),
	(SELECT id FROM DB.ODCLEANSTORE.ROLES WHERE label = n'ONC'));
	INSERT INTO DB.ODCLEANSTORE.ROLES_ASSIGNED_TO_USERS VALUES (
	(SELECT id FROM DB.ODCLEANSTORE.USERS WHERE username = n'scraper'),
	(SELECT id FROM DB.ODCLEANSTORE.ROLES WHERE label = n'SCR'));
