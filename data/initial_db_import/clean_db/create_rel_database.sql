/*
	===========================================================================
	USERS MANAGEMENT
	===========================================================================
*/
CREATE TABLE DB.ODCLEANSTORE.USERS
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	username NVARCHAR(255) NOT NULL UNIQUE,
	email NVARCHAR(255) NOT NULL UNIQUE,
	passwordHash NVARCHAR(255) NOT NULL,
	salt NVARCHAR(255) NOT NULL,
	firstname NVARCHAR(255) NOT NULL,
	surname NVARCHAR(255) NOT NULL
);

CREATE TABLE DB.ODCLEANSTORE.ROLES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	label NVARCHAR(255) NOT NULL,
	description LONG NVARCHAR
);

DELETE FROM DB.ODCLEANSTORE.ROLES;

INSERT INTO DB.ODCLEANSTORE.ROLES (label, description) VALUES (n'SCR', n'Scrapper');
INSERT INTO DB.ODCLEANSTORE.ROLES (label, description) VALUES (n'ONC', n'Ontology creator');
INSERT INTO DB.ODCLEANSTORE.ROLES (label, description) VALUES (n'POC', n'Policy creator');
INSERT INTO DB.ODCLEANSTORE.ROLES (label, description) VALUES (n'ADM', n'Administrator');

CREATE TABLE DB.ODCLEANSTORE.ROLES_ASSIGNED_TO_USERS
(
	userId INTEGER NOT NULL,
	roleId INTEGER NOT NULL,
	
	PRIMARY KEY (userId, roleId),
	FOREIGN KEY (userId) REFERENCES DB.ODCLEANSTORE.USERS(id),
	FOREIGN KEY (roleId) REFERENCES DB.ODCLEANSTORE.ROLES(id)
);

/*
	===========================================================================
	ENGINE CONFIGURATION
	===========================================================================
*/
CREATE TABLE DB.ODCLEANSTORE.REGISTERED_TRANSFORMERS
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
CREATE TABLE DB.ODCLEANSTORE.EN_INPUT_GRAPHS
(
	uuid VARCHAR(48) PRIMARY KEY,
	state VARCHAR(16) NOT NULL
);

/* a temporary table (to be replaced later, not included in the diagram) */
CREATE TABLE DB.ODCLEANSTORE.EN_WORKING_ADDED_GRAPHS
(
	name NVARCHAR PRIMARY KEY
);

/*
	===========================================================================
	EL RULES MANAGEMENT
	===========================================================================
*/
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

/*
	===========================================================================
	OI RULES MANAGEMENT
	===========================================================================
*/
CREATE TABLE DB.ODCLEANSTORE.OI_RULES_GROUPS
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	label NVARCHAR(255) NOT NULL,
	description LONG NVARCHAR
);

CREATE TABLE DB.ODCLEANSTORE.OI_RULES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	groupId INTEGER NOT NULL,
	definition LONG NVARCHAR NOT NULL,
	
	FOREIGN KEY (groupId) REFERENCES DB.ODCLEANSTORE.OI_RULES_GROUPS(id)
);

/*
	===========================================================================
	CR RULES MANAGEMENT
	===========================================================================
*/
CREATE TABLE DB.ODCLEANSTORE.CR_AGGREGATION_TYPES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	label NVARCHAR(255) UNIQUE NOT NULL,
	description LONG NVARCHAR	
);

DELETE FROM DB.ODCLEANSTORE.CR_AGGREGATION_TYPES;

INSERT INTO DB.ODCLEANSTORE.CR_AGGREGATION_TYPES (label, description) VALUES (n'DEFAULT', n'Propagates the default aggregation type');
INSERT INTO DB.ODCLEANSTORE.CR_AGGREGATION_TYPES (label, description) VALUES (n'ANY', n'Selects any single value');
INSERT INTO DB.ODCLEANSTORE.CR_AGGREGATION_TYPES (label, description) VALUES (n'ALL', n'Selects all values');
INSERT INTO DB.ODCLEANSTORE.CR_AGGREGATION_TYPES (label, description) VALUES (n'BEST', n'Selects the value with highest aggregated quality');
INSERT INTO DB.ODCLEANSTORE.CR_AGGREGATION_TYPES (label, description) VALUES (n'LATEST', n'Selects the newest value');
INSERT INTO DB.ODCLEANSTORE.CR_AGGREGATION_TYPES (label, description) VALUES (n'BEST_SOURCE', n'Selects the value with the highest score of its named graph');
INSERT INTO DB.ODCLEANSTORE.CR_AGGREGATION_TYPES (label, description) VALUES (n'MAX', n'Selects maximum value');
INSERT INTO DB.ODCLEANSTORE.CR_AGGREGATION_TYPES (label, description) VALUES (n'MIN', n'Selects minimum value');
INSERT INTO DB.ODCLEANSTORE.CR_AGGREGATION_TYPES (label, description) VALUES (n'SHORTEST', n'Selects the shortest value');
INSERT INTO DB.ODCLEANSTORE.CR_AGGREGATION_TYPES (label, description) VALUES (n'LONGEST', n'Selects the longest value');
INSERT INTO DB.ODCLEANSTORE.CR_AGGREGATION_TYPES (label, description) VALUES (n'AVG', n'Computes average value');
INSERT INTO DB.ODCLEANSTORE.CR_AGGREGATION_TYPES (label, description) VALUES (n'MEDIAN', n'Selects the median');
INSERT INTO DB.ODCLEANSTORE.CR_AGGREGATION_TYPES (label, description) VALUES (n'CONCAT', n'Returns all values concatenated');
INSERT INTO DB.ODCLEANSTORE.CR_AGGREGATION_TYPES (label, description) VALUES (n'NONE', n'Selects all values without grouping of the same values');

CREATE TABLE DB.ODCLEANSTORE.CR_MULTIVALUE_TYPES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	label NVARCHAR(255) UNIQUE NOT NULL,
	description LONG NVARCHAR
);

DELETE FROM DB.ODCLEANSTORE.CR_MULTIVALUE_TYPES;

INSERT INTO DB.ODCLEANSTORE.CR_MULTIVALUE_TYPES (label, description) VALUES (n'DEFAULT', n'Propagate the default multivalue settings');
INSERT INTO DB.ODCLEANSTORE.CR_MULTIVALUE_TYPES (label, description) VALUES (n'YES', n'Mutlivalue allowed');
INSERT INTO DB.ODCLEANSTORE.CR_MULTIVALUE_TYPES (label, description) VALUES (n'NO', n'Multivalue not allowed');

CREATE TABLE DB.ODCLEANSTORE.CR_ERROR_STRATEGIES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	label NVARCHAR(255) UNIQUE NOT NULL,
	description LONG NVARCHAR
);

INSERT INTO DB.ODCLEANSTORE.CR_ERROR_STRATEGIES (label, description) VALUES (n'IGNORE', n'Discard value');
INSERT INTO DB.ODCLEANSTORE.CR_ERROR_STRATEGIES (label, description) VALUES (n'RETURN_ALL', n'Return value without aggregation');

CREATE TABLE DB.ODCLEANSTORE.CR_PROPERTIES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	property NVARCHAR(1024) UNIQUE NOT NULL,
	multivalueTypeId INTEGER,
	aggregationTypeId INTEGER,
	
	FOREIGN KEY (multivalueTypeId) REFERENCES DB.ODCLEANSTORE.CR_MULTIVALUE_TYPES(id),
	FOREIGN KEY (aggregationTypeId) REFERENCES DB.ODCLEANSTORE.CR_AGGREGATION_TYPES(id)
);

CREATE TABLE DB.ODCLEANSTORE.CR_SETTINGS
(
	defaultAggregationTypeId INT,
	defaultMultivalueTypeId INT,
	defaultErrorStrategyId INT,
	
	FOREIGN KEY (defaultMultivalueTypeId) REFERENCES DB.ODCLEANSTORE.CR_MULTIVALUE_TYPES(id),
	FOREIGN KEY (defaultAggregationTypeId) REFERENCES DB.ODCLEANSTORE.CR_AGGREGATION_TYPES(id),
	FOREIGN KEY (defaultErrorStrategyId) REFERENCES DB.ODCLEANSTORE.CR_ERROR_STRATEGIES(id)
);

DELETE FROM DB.ODCLEANSTORE.CR_SETTINGS;

INSERT INTO DB.ODCLEANSTORE.CR_SETTINGS (defaultAggregationTypeId, defaultMultivalueTypeId, defaultErrorStrategyId) VALUES (
	(SELECT id FROM DB.ODCLEANSTORE.CR_AGGREGATION_TYPES WHERE label = 'ALL'),
	(SELECT id FROM DB.ODCLEANSTORE.CR_MULTIVALUE_TYPES WHERE label = 'NO'),
	(SELECT id FROM DB.ODCLEANSTORE.CR_ERROR_STRATEGIES WHERE label = 'RETURN_ALL'));
