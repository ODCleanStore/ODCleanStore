/* username: adm, password: adm, roles: ADM */
INSERT INTO DB.ODCLEANSTORE.USERS (username, email, passwordHash, salt, firstname, surname) 
VALUES ('adm', 'adm@odcleanstore.cz', '0e2aeeeb4125bea8d439c61050a08b52', 'salted', 'The', 'Administrator');

/* username: poc, password: poc, roles: POC */
INSERT INTO DB.ODCLEANSTORE.USERS (username, email, passwordHash, salt, firstname, surname) 
VALUES ('poc', 'poc@odcleanstore.cz', '247cd1ddd4858349720e59486a532100', 'salted', 'The', 'Policy Creator');

INSERT INTO DB.ODCLEANSTORE.ROLES_ASSIGNED_TO_USERS VALUES (1, 4);
INSERT INTO DB.ODCLEANSTORE.ROLES_ASSIGNED_TO_USERS VALUES (2, 3);

INSERT INTO DB.ODCLEANSTORE.CR_PROPERTIES (property, multivalue, aggregationTypeId)
VALUES ('foaf:name', 1, 9);

INSERT INTO DB.ODCLEANSTORE.CR_PROPERTIES (property, multivalue, aggregationTypeId)
VALUES ('pc:endDate', 1, 6);

INSERT INTO DB.ODCLEANSTORE.PUBLISHERS (uri)
VALUES ('http://www.seznam.cz');
