/* username: adm, password: adm, roles: ADM */
INSERT INTO DB.ODCLEANSTORE.USERS (username, email, passwordHash, salt, firstname, surname) 
VALUES (n'adm', n'adm@odcleanstore.cz', n'0e2aeeeb4125bea8d439c61050a08b52', n'salted', n'The', n'Administrator');

/* username: poc, password: poc, roles: POC */
INSERT INTO DB.ODCLEANSTORE.USERS (username, email, passwordHash, salt, firstname, surname) 
VALUES (n'poc', n'poc@odcleanstore.cz', n'247cd1ddd4858349720e59486a532100', n'salted', n'The', n'Policy Creator');

INSERT INTO DB.ODCLEANSTORE.ROLES_ASSIGNED_TO_USERS VALUES (1, 4);
INSERT INTO DB.ODCLEANSTORE.ROLES_ASSIGNED_TO_USERS VALUES (2, 3);
