DROP TABLE DB.ODCLEANSTORE.QA_RULES_TO_PUBLISHERS_RESTRICTIONS;
DROP TABLE DB.ODCLEANSTORE.QA_RULES;
DROP TABLE DB.ODCLEANSTORE.PUBLISHERS;

CREATE TABLE DB.ODCLEANSTORE.QA_RULES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	filter LONG NVARCHAR NOT NULL,
	coefficient REAL NOT NULL,
	description LONG NVARCHAR
);

CREATE TABLE DB.ODCLEANSTORE.PUBLISHERS
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	label NVARCHAR(255) UNIQUE NOT NULL,
	uri NVARCHAR(1024) NOT NULL
);

CREATE TABLE DB.ODCLEANSTORE.QA_RULES_TO_PUBLISHERS_RESTRICTIONS
(
	ruleId INTEGER NOT NULL,
	publisherId INTEGER NOT NULL,
	
	PRIMARY KEY (ruleId, publisherId),
	FOREIGN KEY (ruleId) REFERENCES DB.ODCLEANSTORE.QA_RULES(id),
	FOREIGN KEY (publisherId) REFERENCES DB.ODCLEANSTORE.PUBLISHERS(id)
);

INSERT INTO DB.ODCLEANSTORE.QA_RULES (description, filter, coefficient) VALUES (n'Rule no. 1', n'Filter for rule no. 1', 0.1);
INSERT INTO DB.ODCLEANSTORE.QA_RULES (description, filter, coefficient) VALUES (n'Rule no. 2', n'Filter for rule no. 2', 0.5);
