INSERT INTO DB.FRONTEND.USERS (username, email, passwordHash, salt, firstname, surname) 
VALUES ('dusanr', 'dusanr@odcleanstore.cz', 'f33b354b1a67af018bf7725049ad1036', 'salted', 'Dusan', 'Rychnovsky');

INSERT INTO DB.FRONTEND.USERS (username, email, passwordHash, salt, firstname, surname) 
VALUES ('jakubd', 'jakubd@odcleanstore.cz', '669f023337b5b15eed1b3ca8400f4ef1', 'salted', 'Jakub', 'Daniel');

INSERT INTO DB.FRONTEND.ROLES_ASSIGNED_TO_USERS VALUES (1, 4);
INSERT INTO DB.FRONTEND.ROLES_ASSIGNED_TO_USERS VALUES (2, 2);
INSERT INTO DB.FRONTEND.ROLES_ASSIGNED_TO_USERS VALUES (2, 3);
