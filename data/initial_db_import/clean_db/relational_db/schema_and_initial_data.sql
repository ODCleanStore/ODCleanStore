DROP TABLE DB.FRONTEND.ROLES_ASSIGNED_TO_USERS;
DROP TABLE DB.FRONTEND.USERS;
DROP TABLE DB.FRONTEND.ROLES;

DROP TABLE DB.FRONTEND.REGISTERED_TRANSFORMERS;
DROP TABLE DB.FRONTEND.EN_INPUT_GRAPHS;
DROP TABLE DB.FRONTEND.EN_WORKING_ADDED_GRAPHS;

DROP TABLE DB.FRONTEND.QA_RULES_TO_DOMAINS_RESTRICTIONS;
DROP TABLE DB.FRONTEND.QA_RULES;
DROP TABLE DB.FRONTEND.DATA_DOMAINS;

DROP TABLE DB.FRONTEND.OI_RULES;
DROP TABLE DB.FRONTEND.OI_RULES_GROUPS;

DROP TABLE DB.FRONTEND.CR_SETTINGS;
DROP TABLE DB.FRONTEND.CR_PROPERTIES;
DROP TABLE DB.FRONTEND.CR_AGGREGATION_TYPES;

/*
	===========================================================================
	USERS MANAGEMENT
	===========================================================================
*/
CREATE TABLE DB.FRONTEND.USERS
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	username NVARCHAR(255) NOT NULL UNIQUE,
	email NVARCHAR(255) NOT NULL UNIQUE,
	passwordHash NVARCHAR(255) NOT NULL,
	salt NVARCHAR(255) NOT NULL,
	firstname NVARCHAR(255) NOT NULL,
	surname NVARCHAR(255) NOT NULL
);

INSERT INTO DB.FRONTEND.USERS (username, email, passwordHash, salt, firstname, surname) 
VALUES ('dusanr', 'dusanr@odcleanstore.cz', 'f33b354b1a67af018bf7725049ad1036', 'salted', 'Dusan', 'Rychnovsky');

INSERT INTO DB.FRONTEND.USERS (username, email, passwordHash, salt, firstname, surname) 
VALUES ('jakubd', 'jakubd@odcleanstore.cz', '669f023337b5b15eed1b3ca8400f4ef1', 'salted', 'Jakub', 'Daniel');

CREATE TABLE DB.FRONTEND.ROLES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	label NVARCHAR(255) NOT NULL,
	description LONG NVARCHAR
);

INSERT INTO DB.FRONTEND.ROLES (label, description) VALUES ('SCR', 'Scrapper');
INSERT INTO DB.FRONTEND.ROLES (label, description) VALUES ('ONC', 'Ontology creator');
INSERT INTO DB.FRONTEND.ROLES (label, description) VALUES ('POC', 'Policy creator');
INSERT INTO DB.FRONTEND.ROLES (label, description) VALUES ('ADM', 'Administrator');

CREATE TABLE DB.FRONTEND.ROLES_ASSIGNED_TO_USERS
(
	userId INTEGER NOT NULL,
	roleId INTEGER NOT NULL,
	
	PRIMARY KEY (userId, roleId),
	FOREIGN KEY (userId) REFERENCES DB.FRONTEND.USERS(id),
	FOREIGN KEY (roleId) REFERENCES DB.FRONTEND.ROLES(id)
);

INSERT INTO DB.FRONTEND.ROLES_ASSIGNED_TO_USERS VALUES (1, 4);
INSERT INTO DB.FRONTEND.ROLES_ASSIGNED_TO_USERS VALUES (2, 2);
INSERT INTO DB.FRONTEND.ROLES_ASSIGNED_TO_USERS VALUES (2, 3);

/*
	===========================================================================
	ENGINE CONFIGURATION
	===========================================================================
*/
CREATE TABLE DB.FRONTEND.REGISTERED_TRANSFORMERS
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	label NVARCHAR(255) NOT NULL,
	description LONG NVARCHAR,
	jarPath NVARCHAR(255) NOT NULL,
	fullClassName NVARCHAR(255) NOT NULL,
	workDirPath NVARCHAR(255) NOT NULL,
	configuration LONG NVARCHAR NOT NULL,
	active SMALLINT NOT NULL DEFAULT 0,
	priority INTEGER NOT NULL DEFAULT 0
);

/* a temporary table (to be replaced later, not included in the diagram) */
CREATE TABLE DB.FRONTEND.EN_INPUT_GRAPHS
(
	uuid VARCHAR(48) PRIMARY KEY,
	state VARCHAR(16) NOT NULL
);

/* a temporary table (to be replaced later, not included in the diagram) */
CREATE TABLE DB.FRONTEND.EN_WORKING_ADDED_GRAPHS
(
	name NVARCHAR PRIMARY KEY
);

/*
	===========================================================================
	EL RULES MANAGEMENT
	===========================================================================
*/
CREATE TABLE DB.FRONTEND.QA_RULES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	filter LONG NVARCHAR NOT NULL,
	coefficient REAL NOT NULL,
	description LONG NVARCHAR
);

CREATE TABLE DB.FRONTEND.DATA_DOMAINS
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	uri NVARCHAR(1024) NOT NULL
);

CREATE TABLE DB.FRONTEND.QA_RULES_TO_DOMAINS_RESTRICTIONS
(
	ruleId INTEGER NOT NULL,
	domainId INTEGER NOT NULL,
	
	PRIMARY KEY (ruleId, domainId),
	FOREIGN KEY (ruleId) REFERENCES DB.FRONTEND.QA_RULES(id),
	FOREIGN KEY (domainId) REFERENCES DB.FRONTEND.DATA_DOMAINS(id)
);

/*
	===========================================================================
	OI RULES MANAGEMENT
	===========================================================================
*/
CREATE TABLE DB.FRONTEND.OI_RULES_GROUPS
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	label NVARCHAR(255) NOT NULL,
	description LONG NVARCHAR
);

CREATE TABLE DB.FRONTEND.OI_RULES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	groupId INTEGER NOT NULL,
	definition LONG NVARCHAR NOT NULL,
	
	FOREIGN KEY (groupId) REFERENCES DB.FRONTEND.OI_RULES_GROUPS(id)
);

/*
	===========================================================================
	CR RULES MANAGEMENT
	===========================================================================
*/
CREATE TABLE DB.FRONTEND.CR_SETTINGS
(
	name NVARCHAR(255) NOT NULL PRIMARY KEY,
	value NVARCHAR(255) NOT NULL,
	description LONG NVARCHAR
);

INSERT INTO DB.FRONTEND.CR_SETTINGS (name, value, description) VALUES ('DEFAULT_AGGREGATION', 'ALL', 'Default aggregation method');
INSERT INTO DB.FRONTEND.CR_SETTINGS (name, value, description) VALUES ('DEFAULT_MULTIVALUE', '0', 'Default multivalue setting');
INSERT INTO DB.FRONTEND.CR_SETTINGS (name, value, description) VALUES ('ERROR_STRATEGY', 'RETURN_ALL', 'Default aggregation error strategy');

CREATE TABLE DB.FRONTEND.CR_AGGREGATION_TYPES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	label NVARCHAR(255) UNIQUE NOT NULL,
	description LONG NVARCHAR	
);

INSERT INTO DB.FRONTEND.CR_AGGREGATION_TYPES (label, description) VALUES ('ANY', 'Selects any single value');
INSERT INTO DB.FRONTEND.CR_AGGREGATION_TYPES (label, description) VALUES ('ALL', 'Selects all values');
INSERT INTO DB.FRONTEND.CR_AGGREGATION_TYPES (label, description) VALUES ('BEST', 'Selects the value with highest aggregated quality');
INSERT INTO DB.FRONTEND.CR_AGGREGATION_TYPES (label, description) VALUES ('LATEST', 'Selects the newest value');
INSERT INTO DB.FRONTEND.CR_AGGREGATION_TYPES (label, description) VALUES ('BEST_SOURCE', 'Selects the value with the highest score of its named graph');
INSERT INTO DB.FRONTEND.CR_AGGREGATION_TYPES (label, description) VALUES ('MAX', 'Selects maximum value');
INSERT INTO DB.FRONTEND.CR_AGGREGATION_TYPES (label, description) VALUES ('MIN', 'Selects minimum value');
INSERT INTO DB.FRONTEND.CR_AGGREGATION_TYPES (label, description) VALUES ('SHORTEST', 'Selects the shortest value');
INSERT INTO DB.FRONTEND.CR_AGGREGATION_TYPES (label, description) VALUES ('LONGEST', 'Selects the longest value');
INSERT INTO DB.FRONTEND.CR_AGGREGATION_TYPES (label, description) VALUES ('AVG', 'Computes average value');
INSERT INTO DB.FRONTEND.CR_AGGREGATION_TYPES (label, description) VALUES ('MEDIAN', 'Selects the median');
INSERT INTO DB.FRONTEND.CR_AGGREGATION_TYPES (label, description) VALUES ('CONCAT', 'Returns all values concatenated');
INSERT INTO DB.FRONTEND.CR_AGGREGATION_TYPES (label, description) VALUES ('NONE', 'Selects all values without grouping of the same values');

CREATE TABLE DB.FRONTEND.CR_PROPERTIES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	property NVARCHAR(1024) UNIQUE NOT NULL,
	multivalue SMALLINT NULL DEFAULT 1,
	aggregationTypeId INTEGER NULL,
	
	FOREIGN KEY (aggregationTypeId) REFERENCES DB.FRONTEND.CR_AGGREGATION_TYPES(id)
);

INSERT INTO DB.FRONTEND.CR_PROPERTIES (property, multivalue, aggregationTypeId) VALUES ('http://www.w3.org/1999/02/22-rdf-syntax-ns#type', 1, NULL);