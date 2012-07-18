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
	firstname NVARCHAR(255),
	surname NVARCHAR(255)
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
	FOREIGN KEY (userId) REFERENCES DB.ODCLEANSTORE.USERS(id) ON DELETE CASCADE,
	FOREIGN KEY (roleId) REFERENCES DB.ODCLEANSTORE.ROLES(id) ON DELETE CASCADE
);

/*
	===========================================================================
	QA RULES MANAGEMENT
	===========================================================================
*/
CREATE TABLE DB.ODCLEANSTORE.QA_RULES_GROUPS
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	label NVARCHAR(255) UNIQUE NOT NULL,
	description LONG NVARCHAR
);

CREATE TABLE DB.ODCLEANSTORE.QA_RULES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	groupId INTEGER NOT NULL,
	filter LONG NVARCHAR NOT NULL,
	coefficient REAL NOT NULL,
	description LONG NVARCHAR,
	
	FOREIGN KEY (groupId) REFERENCES DB.ODCLEANSTORE.QA_RULES_GROUPS(id) ON DELETE CASCADE
);

CREATE TABLE DB.ODCLEANSTORE.QA_RULES_TO_ONTOLOGIES_MAP
(
	ruleId INTEGER NOT NULL IDENTITY PRIMARY KEY,
	ontologyId INTEGER NOT NULL,

	FOREIGN KEY (ruleId) REFERENCES DB.ODCLEANSTORE.QA_RULES(id) ON DELETE CASCADE
);

/*
	===========================================================================
	DN RULES MANAGEMENT
	===========================================================================
*/
CREATE TABLE DB.ODCLEANSTORE.DN_RULES_GROUPS
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	label NVARCHAR(255) UNIQUE NOT NULL,
	description LONG NVARCHAR
);

CREATE TABLE DB.ODCLEANSTORE.DN_RULES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	groupId INTEGER NOT NULL,
	description LONG NVARCHAR,
	
	FOREIGN KEY (groupId) REFERENCES DB.ODCLEANSTORE.DN_RULES_GROUPS(id) ON DELETE CASCADE
);

CREATE TABLE DB.ODCLEANSTORE.DN_RULE_COMPONENT_TYPES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	label VARCHAR(20) NOT NULL UNIQUE,

	CONSTRAINT labelCheck CHECK (label IN ('INSERT', 'DELETE'))
);

INSERT INTO DB.ODCLEANSTORE.DN_RULE_COMPONENT_TYPES (id, label) VALUES (0, 'INSERT');
INSERT INTO DB.ODCLEANSTORE.DN_RULE_COMPONENT_TYPES (id, label) VALUES (1, 'DELETE');

CREATE TABLE DB.ODCLEANSTORE.DN_RULE_COMPONENTS
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	ruleId INTEGER NOT NULL,
	typeId INTEGER NOT NULL,
	modification LONG NVARCHAR,
	description LONG NVARCHAR,
	
	FOREIGN KEY (ruleId) REFERENCES DB.ODCLEANSTORE.DN_RULES(id) ON DELETE CASCADE,
	FOREIGN KEY (typeId) REFERENCES DB.ODCLEANSTORE.DN_RULE_COMPONENT_TYPES(id) ON DELETE CASCADE
);

/*
	===========================================================================
	OI RULES MANAGEMENT
	===========================================================================
*/
CREATE TABLE DB.ODCLEANSTORE.OI_RULES_GROUPS
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	label NVARCHAR(255) UNIQUE NOT NULL,
	description LONG NVARCHAR
);

CREATE TABLE DB.ODCLEANSTORE.OI_RULES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	groupId INTEGER NOT NULL,
	label NVARCHAR(255) UNIQUE NOT NULL,
	linkType NVARCHAR(255) NOT NULL,
	sourceRestriction NVARCHAR(255),
	targetRestriction NVARCHAR(255),
	linkageRule LONG NVARCHAR NOT NULL,
	filterThreshold DECIMAL,
	filterLimit INTEGER, 
	
	FOREIGN KEY (groupId) REFERENCES DB.ODCLEANSTORE.OI_RULES_GROUPS(id) ON DELETE CASCADE
);

CREATE TABLE DB.ODCLEANSTORE.OI_OUTPUT_TYPES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	label VARCHAR(255) UNIQUE NOT NULL,
	description LONG NVARCHAR
);

DELETE FROM DB.ODCLEANSTORE.OI_OUTPUT_TYPES;

INSERT INTO DB.ODCLEANSTORE.OI_OUTPUT_TYPES (label, description) VALUES ('DB', n'Stores the links into database, using SPARQL endpoint');
INSERT INTO DB.ODCLEANSTORE.OI_OUTPUT_TYPES (label, description) VALUES ('FILE', n'Stores the links into file in transformer directory');

CREATE TABLE DB.ODCLEANSTORE.OI_FILE_FORMATS
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	label VARCHAR(255) UNIQUE NOT NULL,
	description LONG NVARCHAR
);

DELETE FROM DB.ODCLEANSTORE.OI_FILE_FORMATS;

INSERT INTO DB.ODCLEANSTORE.OI_FILE_FORMATS (label, description) VALUES ('NTRIPLES', n'Writes the links as N-Triples statements');
INSERT INTO DB.ODCLEANSTORE.OI_FILE_FORMATS (label, description) VALUES ('ALIGNMENT', n'Writes the links in the OAEI Alignment Format. This includes not only the uris of the source and target entities, but also the confidence of each link');

CREATE TABLE DB.ODCLEANSTORE.OI_OUTPUTS
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	ruleId INTEGER NOT NULL,
	outputTypeId INTEGER NOT NULL,
	minConfidence DECIMAL,
	maxConfidence DECIMAL,
	fileName VARCHAR(255),
	fileFormatId INTEGER,

	FOREIGN KEY (ruleId) REFERENCES DB.ODCLEANSTORE.OI_RULES(id) ON DELETE CASCADE,
	FOREIGN KEY (outputTypeId) REFERENCES DB.ODCLEANSTORE.OI_OUTPUT_TYPES(id) ON DELETE CASCADE,
	FOREIGN KEY (fileFormatId) REFERENCES DB.ODCLEANSTORE.OI_FILE_FORMATS(id) ON DELETE CASCADE
);

/*
	===========================================================================
	ENGINE CONFIGURATION
	===========================================================================
*/

CREATE TABLE DB.ODCLEANSTORE.TRANSFORMERS
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	label NVARCHAR(255) UNIQUE NOT NULL,
	description LONG NVARCHAR,
	jarPath NVARCHAR(255) NOT NULL,
	fullClassName NVARCHAR(255) NOT NULL
);

CREATE TABLE DB.ODCLEANSTORE.BACKUP_TRANSFORMERS
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	label NVARCHAR(255) UNIQUE NOT NULL,
	description LONG NVARCHAR,
	jarPath NVARCHAR(255) NOT NULL,
	fullClassName NVARCHAR(255) NOT NULL
);

CREATE TABLE DB.ODCLEANSTORE.PIPELINES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	label NVARCHAR(255) UNIQUE NOT NULL,
	description LONG NVARCHAR,
	isDefault SMALLINT NOT NULL DEFAULT 0
);

CREATE TABLE DB.ODCLEANSTORE.BACKUP_PIPELINES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	label NVARCHAR(255) UNIQUE NOT NULL,
	description LONG NVARCHAR,
	isDefault SMALLINT NOT NULL DEFAULT 0
);

CREATE TABLE DB.ODCLEANSTORE.TRANSFORMER_INSTANCES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	transformerId INTEGER NOT NULL,
	pipelineId INTEGER NOT NULL,
	workDirPath NVARCHAR(255) NOT NULL,
	configuration LONG NVARCHAR NOT NULL,
	runOnCleanDB SMALLINT NOT NULL DEFAULT 1,
	priority INTEGER NOT NULL DEFAULT 0,
	
	FOREIGN KEY (transformerId) REFERENCES DB.ODCLEANSTORE.TRANSFORMERS(id) ON DELETE CASCADE,
	FOREIGN KEY (pipelineId) REFERENCES DB.ODCLEANSTORE.PIPELINES(id) ON DELETE CASCADE
);

CREATE TABLE DB.ODCLEANSTORE.BACKUP_TRANSFORMER_INSTANCES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	transformerId INTEGER NOT NULL,
	pipelineId INTEGER NOT NULL,
	workDirPath NVARCHAR(255) NOT NULL,
	configuration LONG NVARCHAR NOT NULL,
	runOnCleanDB SMALLINT NOT NULL DEFAULT 1,
	priority INTEGER NOT NULL DEFAULT 0,
	
	FOREIGN KEY (transformerId) REFERENCES DB.ODCLEANSTORE.BACKUP_TRANSFORMERS(id) ON DELETE CASCADE,
	FOREIGN KEY (pipelineId) REFERENCES DB.ODCLEANSTORE.BACKUP_PIPELINES(id) ON DELETE CASCADE
);

CREATE TABLE DB.ODCLEANSTORE.OI_RULES_ASSIGNMENT
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	transformerInstanceId INTEGER NOT NULL,
	groupId INTEGER NOT NULL,
	
	FOREIGN KEY (transformerInstanceId) REFERENCES DB.ODCLEANSTORE.TRANSFORMER_INSTANCES(id) ON DELETE CASCADE,
	FOREIGN KEY (groupId) REFERENCES DB.ODCLEANSTORE.OI_RULES_GROUPS(id) ON DELETE CASCADE
);

CREATE TABLE DB.ODCLEANSTORE.BACKUP_OI_RULES_ASSIGNMENT
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	transformerInstanceId INTEGER NOT NULL,
	groupId INTEGER NOT NULL,
	
	FOREIGN KEY (transformerInstanceId) REFERENCES DB.ODCLEANSTORE.BACKUP_TRANSFORMER_INSTANCES(id) ON DELETE CASCADE,
	FOREIGN KEY (groupId) REFERENCES DB.ODCLEANSTORE.OI_RULES_GROUPS(id) ON DELETE CASCADE
);

CREATE TABLE DB.ODCLEANSTORE.QA_RULES_ASSIGNMENT
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	transformerInstanceId INTEGER NOT NULL,
	groupId INTEGER UNIQUE NOT NULL,
	
	FOREIGN KEY (transformerInstanceId) REFERENCES DB.ODCLEANSTORE.TRANSFORMER_INSTANCES(id) ON DELETE CASCADE,
	FOREIGN KEY (groupId) REFERENCES DB.ODCLEANSTORE.QA_RULES_GROUPS(id) ON DELETE CASCADE
);

CREATE TABLE DB.ODCLEANSTORE.BACKUP_QA_RULES_ASSIGNMENT
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	transformerInstanceId INTEGER NOT NULL,
	groupId INTEGER UNIQUE NOT NULL,
	
	FOREIGN KEY (transformerInstanceId) REFERENCES DB.ODCLEANSTORE.BACKUP_TRANSFORMER_INSTANCES(id) ON DELETE CASCADE,
	FOREIGN KEY (groupId) REFERENCES DB.ODCLEANSTORE.QA_RULES_GROUPS(id) ON DELETE CASCADE
);

CREATE TABLE DB.ODCLEANSTORE.EN_INPUT_GRAPHS
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	uuid VARCHAR(48) UNIQUE,
	state VARCHAR(16) NOT NULL,
	pipelineId INTEGER,
	
	FOREIGN KEY (pipelineId) REFERENCES DB.ODCLEANSTORE.PIPELINES(id) ON DELETE SET NULL
);

CREATE TABLE DB.ODCLEANSTORE.EN_PIPELINE_ERROR_TYPES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	label NVARCHAR(255) UNIQUE NOT NULL,
	description LONG NVARCHAR
);

INSERT INTO DB.ODCLEANSTORE.EN_PIPELINE_ERROR_TYPES (label, description) VALUES (n'TRANSFORMER_FAILURE', n'A failure of a transformer in the pipeline.');

CREATE TABLE DB.ODCLEANSTORE.EN_GRAPHS_IN_ERROR
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	graphId INTEGER NOT NULL,
	errorTypeId INTEGER NOT NULL,
	errorMessage LONG NVARCHAR NOT NULL,
	
	FOREIGN KEY (errorTypeId) REFERENCES DB.ODCLEANSTORE.EN_PIPELINE_ERROR_TYPES(id) ON DELETE CASCADE,
	FOREIGN KEY (graphId) REFERENCES DB.ODCLEANSTORE.EN_INPUT_GRAPHS(id) ON DELETE CASCADE
);

/* a temporary table (to be replaced later, not included in the diagram) */
CREATE TABLE DB.ODCLEANSTORE.EN_WORKING_ADDED_GRAPHS
(
	name NVARCHAR PRIMARY KEY
);

/*
	===========================================================================
	CR RULES MANAGEMENT
	===========================================================================
*/
CREATE TABLE DB.ODCLEANSTORE.CR_AGGREGATION_TYPES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	label VARCHAR(255) UNIQUE NOT NULL,
	description LONG NVARCHAR	
);

DELETE FROM DB.ODCLEANSTORE.CR_AGGREGATION_TYPES;

INSERT INTO DB.ODCLEANSTORE.CR_AGGREGATION_TYPES (label, description) VALUES ('DEFAULT', n'Propagates the default aggregation type');
INSERT INTO DB.ODCLEANSTORE.CR_AGGREGATION_TYPES (label, description) VALUES ('ANY', n'Selects any single value');
INSERT INTO DB.ODCLEANSTORE.CR_AGGREGATION_TYPES (label, description) VALUES ('ALL', n'Selects all values');
INSERT INTO DB.ODCLEANSTORE.CR_AGGREGATION_TYPES (label, description) VALUES ('BEST', n'Selects the value with highest aggregated quality');
INSERT INTO DB.ODCLEANSTORE.CR_AGGREGATION_TYPES (label, description) VALUES ('LATEST', n'Selects the newest value');
INSERT INTO DB.ODCLEANSTORE.CR_AGGREGATION_TYPES (label, description) VALUES ('BEST_SOURCE', n'Selects the value with the highest score of its named graph');
INSERT INTO DB.ODCLEANSTORE.CR_AGGREGATION_TYPES (label, description) VALUES ('MAX', n'Selects maximum value');
INSERT INTO DB.ODCLEANSTORE.CR_AGGREGATION_TYPES (label, description) VALUES ('MIN', n'Selects minimum value');
INSERT INTO DB.ODCLEANSTORE.CR_AGGREGATION_TYPES (label, description) VALUES ('SHORTEST', n'Selects the shortest value');
INSERT INTO DB.ODCLEANSTORE.CR_AGGREGATION_TYPES (label, description) VALUES ('LONGEST', n'Selects the longest value');
INSERT INTO DB.ODCLEANSTORE.CR_AGGREGATION_TYPES (label, description) VALUES ('AVG', n'Computes average value');
INSERT INTO DB.ODCLEANSTORE.CR_AGGREGATION_TYPES (label, description) VALUES ('MEDIAN', n'Selects the median');
INSERT INTO DB.ODCLEANSTORE.CR_AGGREGATION_TYPES (label, description) VALUES ('CONCAT', n'Returns all values concatenated');
INSERT INTO DB.ODCLEANSTORE.CR_AGGREGATION_TYPES (label, description) VALUES ('NONE', n'Selects all values without grouping of the same values');

CREATE TABLE DB.ODCLEANSTORE.CR_MULTIVALUE_TYPES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	label VARCHAR(255) UNIQUE NOT NULL,
	description LONG NVARCHAR
);

DELETE FROM DB.ODCLEANSTORE.CR_MULTIVALUE_TYPES;

INSERT INTO DB.ODCLEANSTORE.CR_MULTIVALUE_TYPES (label, description) VALUES ('DEFAULT', n'Propagate the default multivalue settings');
INSERT INTO DB.ODCLEANSTORE.CR_MULTIVALUE_TYPES (label, description) VALUES ('YES', n'Mutlivalue allowed');
INSERT INTO DB.ODCLEANSTORE.CR_MULTIVALUE_TYPES (label, description) VALUES ('NO', n'Multivalue not allowed');

CREATE TABLE DB.ODCLEANSTORE.CR_ERROR_STRATEGIES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	label VARCHAR(255) UNIQUE NOT NULL,
	description LONG NVARCHAR
);

INSERT INTO DB.ODCLEANSTORE.CR_ERROR_STRATEGIES (label, description) VALUES ('IGNORE', n'Discard value');
INSERT INTO DB.ODCLEANSTORE.CR_ERROR_STRATEGIES (label, description) VALUES ('RETURN_ALL', n'Return value without aggregation');

CREATE TABLE DB.ODCLEANSTORE.CR_PROPERTIES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	property NVARCHAR(1024) UNIQUE NOT NULL,
	multivalueTypeId INTEGER,
	aggregationTypeId INTEGER,
	
	FOREIGN KEY (multivalueTypeId) REFERENCES DB.ODCLEANSTORE.CR_MULTIVALUE_TYPES(id) ON DELETE CASCADE,
	FOREIGN KEY (aggregationTypeId) REFERENCES DB.ODCLEANSTORE.CR_AGGREGATION_TYPES(id) ON DELETE CASCADE
);

CREATE TABLE DB.ODCLEANSTORE.CR_SETTINGS
(
	defaultAggregationTypeId INT,
	defaultMultivalueTypeId INT,
	defaultErrorStrategyId INT,
	
	FOREIGN KEY (defaultMultivalueTypeId) REFERENCES DB.ODCLEANSTORE.CR_MULTIVALUE_TYPES(id) ON DELETE CASCADE,
	FOREIGN KEY (defaultAggregationTypeId) REFERENCES DB.ODCLEANSTORE.CR_AGGREGATION_TYPES(id) ON DELETE CASCADE,
	FOREIGN KEY (defaultErrorStrategyId) REFERENCES DB.ODCLEANSTORE.CR_ERROR_STRATEGIES(id) ON DELETE CASCADE
);

DELETE FROM DB.ODCLEANSTORE.CR_SETTINGS;

INSERT INTO DB.ODCLEANSTORE.CR_SETTINGS (defaultAggregationTypeId, defaultMultivalueTypeId, defaultErrorStrategyId) VALUES (
	(SELECT id FROM DB.ODCLEANSTORE.CR_AGGREGATION_TYPES WHERE label = 'ALL'),
	(SELECT id FROM DB.ODCLEANSTORE.CR_MULTIVALUE_TYPES WHERE label = 'NO'),
	(SELECT id FROM DB.ODCLEANSTORE.CR_ERROR_STRATEGIES WHERE label = 'RETURN_ALL'));

/*
	===========================================================================
	QUERY EXECUTION SETTINGS
	===========================================================================
*/
CREATE TABLE DB.ODCLEANSTORE.QE_LABEL_PROPERTIES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	property NVARCHAR(1024) UNIQUE NOT NULL
);

INSERT INTO DB.ODCLEANSTORE.QE_LABEL_PROPERTIES (property) VALUES (n'http://www.w3.org/2000/01/rdf-schema#label');
