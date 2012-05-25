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

CREATE TABLE DB.FRONTEND.ROLES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	label NVARCHAR(255) NOT NULL,
	description LONG NVARCHAR
);

DELETE FROM DB.FRONTEND.ROLES;

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

CREATE TABLE DB.FRONTEND.PUBLISHERS
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	uri NVARCHAR(1024) NOT NULL
);

CREATE TABLE DB.FRONTEND.QA_RULES_TO_PUBLISHERS_RESTRICTIONS
(
	ruleId INTEGER NOT NULL,
	publisherId INTEGER NOT NULL,
	
	PRIMARY KEY (ruleId, publisherId),
	FOREIGN KEY (ruleId) REFERENCES DB.FRONTEND.QA_RULES(id),
	FOREIGN KEY (publisherId) REFERENCES DB.FRONTEND.PUBLISHERS(id)
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
CREATE TABLE DB.FRONTEND.CR_AGGREGATION_TYPES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	label NVARCHAR(255) UNIQUE NOT NULL,
	description LONG NVARCHAR	
);

DELETE FROM DB.FRONTEND.CR_AGGREGATION_TYPES;

INSERT INTO DB.FRONTEND.CR_AGGREGATION_TYPES (label, description) VALUES ('DEFAULT', 'Propagates the default aggregation type');
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

CREATE TABLE DB.FRONTEND.CR_MULTIVALUE_TYPES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	label NVARCHAR(255) UNIQUE NOT NULL,
	description LONG NVARCHAR
);

DELETE FROM DB.FRONTEND.CR_MULTIVALUE_TYPES;

INSERT INTO DB.FRONTEND.CR_MULTIVALUE_TYPES (label, description) VALUES ('DEFAULT', 'Propagate the default multivalue settings');
INSERT INTO DB.FRONTEND.CR_MULTIVALUE_TYPES (label, description) VALUES ('YES', 'Mutlivalue allowed');
INSERT INTO DB.FRONTEND.CR_MULTIVALUE_TYPES (label, description) VALUES ('NO', 'Multivalue not allowed');

CREATE TABLE DB.FRONTEND.CR_ERROR_STRATEGIES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	label NVARCHAR(255) UNIQUE NOT NULL,
	description LONG NVARCHAR
);

INSERT INTO DB.FRONTEND.CR_ERROR_STRATEGIES (label, description) VALUES ('IGNORE', 'Discard value');
INSERT INTO DB.FRONTEND.CR_ERROR_STRATEGIES (label, description) VALUES ('RETURN_ALL', 'Return value without aggregation');

CREATE TABLE DB.FRONTEND.CR_PROPERTIES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	property NVARCHAR(1024) UNIQUE NOT NULL,
	multivalueTypeId INTEGER,
	aggregationTypeId INTEGER,
	
	FOREIGN KEY (multivalueTypeId) REFERENCES DB.FRONTEND.CR_MULTIVALUE_TYPES(id),
	FOREIGN KEY (aggregationTypeId) REFERENCES DB.FRONTEND.CR_AGGREGATION_TYPES(id)
);

CREATE TABLE DB.FRONTEND.CR_SETTINGS
(
	defaultAggregationTypeId INT,
	defaultMultivalueTypeId INT,
	defaultErrorStrategyId INT,
	
	FOREIGN KEY (defaultMultivalueTypeId) REFERENCES DB.FRONTEND.CR_MULTIVALUE_TYPES(id),
	FOREIGN KEY (defaultAggregationTypeId) REFERENCES DB.FRONTEND.CR_AGGREGATION_TYPES(id),
	FOREIGN KEY (defaultErrorStrategyId) REFERENCES DB.FRONTEND.CR_ERROR_STRATEGIES(id)
);

DELETE FROM DB.FRONTEND.CR_SETTINGS;

INSERT INTO DB.FRONTEND.CR_SETTINGS (defaultAggregationTypeId, defaultMultivalueTypeId, defaultErrorStrategyId) VALUES (
	(SELECT id FROM DB.FRONTEND.CR_AGGREGATION_TYPES WHERE label = 'ALL'),
	(SELECT id FROM DB.FRONTEND.CR_MULTIVALUE_TYPES WHERE label = 'NO'),
	(SELECT id FROM DB.FRONTEND.CR_ERROR_STRATEGIES WHERE label = 'RETURN_ALL'));

