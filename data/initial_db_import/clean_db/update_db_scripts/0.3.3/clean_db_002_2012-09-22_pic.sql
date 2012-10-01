UPDATE DB.ODCLEANSTORE.ROLES SET label = n'PIC', description = n'Pipeline creator' WHERE label = n'POC';

/* Test data */
UPDATE DB.ODCLEANSTORE.USERS SET username = n'pic', email = n'pic@odcleanstore.cz', passwordHash = n'f4371aa7c2147df3c3e17a738e9af5f7', surname = n'Pipeline Creator' WHERE username = n'poc';