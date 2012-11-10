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

INSERT INTO DB.ODCLEANSTORE.ROLES (label, description) VALUES (n'SCR', n'Scraper');
INSERT INTO DB.ODCLEANSTORE.ROLES (label, description) VALUES (n'ONC', n'Ontology creator');
INSERT INTO DB.ODCLEANSTORE.ROLES (label, description) VALUES (n'PIC', n'Pipeline creator');
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
	description LONG NVARCHAR,
	authorId INTEGER NULL,
	isUncommitted SMALLINT NOT NULL DEFAULT 0,
	
	FOREIGN KEY (authorId) REFERENCES DB.ODCLEANSTORE.USERS(id) ON DELETE SET NULL
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

CREATE TABLE DB.ODCLEANSTORE.QA_RULES_UNCOMMITTED
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	groupId INTEGER NOT NULL,
	filter LONG NVARCHAR NOT NULL,
	coefficient REAL NOT NULL,
	description LONG NVARCHAR,
	
	FOREIGN KEY (groupId) REFERENCES DB.ODCLEANSTORE.QA_RULES_GROUPS(id) ON DELETE CASCADE
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
	description LONG NVARCHAR,
	authorId INTEGER NULL,
	isUncommitted SMALLINT NOT NULL DEFAULT 0,
	
	FOREIGN KEY (authorId) REFERENCES DB.ODCLEANSTORE.USERS(id) ON DELETE SET NULL
);

CREATE TABLE DB.ODCLEANSTORE.DN_RULES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	groupId INTEGER NOT NULL,
	description LONG NVARCHAR,
	
	FOREIGN KEY (groupId) REFERENCES DB.ODCLEANSTORE.DN_RULES_GROUPS(id) ON DELETE CASCADE
);

CREATE TABLE DB.ODCLEANSTORE.DN_RULES_UNCOMMITTED
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
	description LONG NVARCHAR,

	CONSTRAINT labelCheck CHECK (label IN ('INSERT', 'DELETE', 'MODIFY'))
);

INSERT INTO DB.ODCLEANSTORE.DN_RULE_COMPONENT_TYPES (id, label) VALUES (0, 'INSERT');
INSERT INTO DB.ODCLEANSTORE.DN_RULE_COMPONENT_TYPES (id, label) VALUES (1, 'DELETE');
INSERT INTO DB.ODCLEANSTORE.DN_RULE_COMPONENT_TYPES (id, label) VALUES (2, 'MODIFY');

CREATE TABLE DB.ODCLEANSTORE.DN_RULE_COMPONENTS
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	ruleId INTEGER NOT NULL,
	typeId INTEGER NOT NULL,
	modification LONG NVARCHAR NOT NULL,
	description LONG NVARCHAR,
	
	FOREIGN KEY (ruleId) REFERENCES DB.ODCLEANSTORE.DN_RULES(id) ON DELETE CASCADE,
	FOREIGN KEY (typeId) REFERENCES DB.ODCLEANSTORE.DN_RULE_COMPONENT_TYPES(id) ON DELETE CASCADE
);

CREATE TABLE DB.ODCLEANSTORE.DN_RULE_COMPONENTS_UNCOMMITTED
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	ruleId INTEGER NOT NULL,
	typeId INTEGER NOT NULL,
	modification LONG NVARCHAR NOT NULL,
	description LONG NVARCHAR,
	
	FOREIGN KEY (ruleId) REFERENCES DB.ODCLEANSTORE.DN_RULES_UNCOMMITTED(id) ON DELETE CASCADE,
	FOREIGN KEY (typeId) REFERENCES DB.ODCLEANSTORE.DN_RULE_COMPONENT_TYPES(id) ON DELETE CASCADE
);

CREATE TABLE DB.ODCLEANSTORE.DN_REPLACE_TEMPLATE_INSTANCES
(
  id INTEGER NOT NULL IDENTITY PRIMARY KEY,
  groupId INTEGER NOT NULL,
  rawRuleId INTEGER NOT NULL,
  propertyName NVARCHAR(255) NOT NULL,
  pattern NVARCHAR(255) NOT NULL,
  replacement NVARCHAR(255) NOT NULL,

  FOREIGN KEY (rawRuleId) REFERENCES DB.ODCLEANSTORE.DN_RULES_UNCOMMITTED(id) ON DELETE CASCADE
);

CREATE TABLE DB.ODCLEANSTORE.DN_RENAME_TEMPLATE_INSTANCES
(
  id INTEGER NOT NULL IDENTITY PRIMARY KEY,
  groupId INTEGER NOT NULL,
  rawRuleId INTEGER NOT NULL,
  sourcePropertyName NVARCHAR(255) NOT NULL,
  targetPropertyName NVARCHAR(255) NOT NULL,

  FOREIGN KEY (rawRuleId) REFERENCES DB.ODCLEANSTORE.DN_RULES_UNCOMMITTED(id) ON DELETE CASCADE
);

CREATE TABLE DB.ODCLEANSTORE.DN_FILTER_TEMPLATE_INSTANCES
(
  id INTEGER NOT NULL IDENTITY PRIMARY KEY,
  groupId INTEGER NOT NULL,
  rawRuleId INTEGER NOT NULL,
  propertyName NVARCHAR(255) NOT NULL,
  pattern NVARCHAR(255) NOT NULL,
  keep SMALLINT NOT NULL DEFAULT 1,

  FOREIGN KEY (rawRuleId) REFERENCES DB.ODCLEANSTORE.DN_RULES_UNCOMMITTED(id) ON DELETE CASCADE
);

CREATE TABLE DB.ODCLEANSTORE.DN_CONCATENATE_TEMPLATE_INSTANCES
(
  id INTEGER NOT NULL IDENTITY PRIMARY KEY,
  groupId INTEGER NOT NULL,
  rawRuleId INTEGER NOT NULL,
  propertyName NVARCHAR(255) NOT NULL,
  delimiter NVARCHAR(255),

  FOREIGN KEY (rawRuleId) REFERENCES DB.ODCLEANSTORE.DN_RULES_UNCOMMITTED(id) ON DELETE CASCADE
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
	description LONG NVARCHAR,
	authorId INTEGER NULL,
	isUncommitted SMALLINT NOT NULL DEFAULT 0,
	
	FOREIGN KEY (authorId) REFERENCES DB.ODCLEANSTORE.USERS(id) ON DELETE SET NULL
);

CREATE TABLE DB.ODCLEANSTORE.OI_RULES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	groupId INTEGER NOT NULL,
	label NVARCHAR(255) NOT NULL,
  description LONG NVARCHAR,
	linkType NVARCHAR(255) NOT NULL,
	sourceRestriction NVARCHAR(255),
	targetRestriction NVARCHAR(255),
	linkageRule LONG NVARCHAR NOT NULL,
	filterThreshold DECIMAL,
	filterLimit INTEGER, 
	
	FOREIGN KEY (groupId) REFERENCES DB.ODCLEANSTORE.OI_RULES_GROUPS(id) ON DELETE CASCADE
);

CREATE TABLE DB.ODCLEANSTORE.OI_RULES_UNCOMMITTED
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	groupId INTEGER NOT NULL,
	label NVARCHAR(255) NOT NULL,
  description LONG NVARCHAR,
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

CREATE TABLE DB.ODCLEANSTORE.OI_OUTPUTS_UNCOMMITTED
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	ruleId INTEGER NOT NULL,
	outputTypeId INTEGER NOT NULL,
	minConfidence DECIMAL,
	maxConfidence DECIMAL,
	fileName VARCHAR(255),
	fileFormatId INTEGER,

	FOREIGN KEY (ruleId) REFERENCES DB.ODCLEANSTORE.OI_RULES_UNCOMMITTED(id) ON DELETE CASCADE,
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
	workDirPath NVARCHAR(255) NOT NULL,
	fullClassName NVARCHAR(255) NOT NULL
);

DELETE FROM DB.ODCLEANSTORE.TRANSFORMERS;

INSERT INTO DB.ODCLEANSTORE.TRANSFORMERS (label, description, jarPath, workDirPath, fullClassName) VALUES (n'Quality Assessment', n'ODCS Quality Assessment transformer', n'.', n'transformers-working-dir/qassessment', n'cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAssessorImpl');
INSERT INTO DB.ODCLEANSTORE.TRANSFORMERS (label, description, jarPath, workDirPath, fullClassName) VALUES (n'Quality Aggregator', n'ODCS Quality Aggregator transformer', n'.', n'transformers-working-dir/qagregator', n'cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAggregatorImpl');
INSERT INTO DB.ODCLEANSTORE.TRANSFORMERS (label, description, jarPath, workDirPath, fullClassName) VALUES (n'Linker', n'ODCS Object Identification transformer',  n'.', n'transformers-working-dir/link', n'cz.cuni.mff.odcleanstore.linker.impl.LinkerImpl');
INSERT INTO DB.ODCLEANSTORE.TRANSFORMERS (label, description, jarPath, workDirPath, fullClassName) VALUES (n'Data Normalization', n'ODCS Data Normalization transformer', n'.', n'transformers-working-dir/dn', n'cz.cuni.mff.odcleanstore.datanormalization.impl.DataNormalizerImpl');
INSERT INTO DB.ODCLEANSTORE.TRANSFORMERS (label, description, jarPath, workDirPath, fullClassName) VALUES (n'Blank node remover', n'ODCS transformer for replacing blank nodes by new URI resources', n'.', n'transformers-working-dir/bnode-remover', n'cz.cuni.mff.odcleanstore.transformer.odcs.ODCSBNodeToResourceTransformer');

CREATE TABLE DB.ODCLEANSTORE.PIPELINES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	label NVARCHAR(255) UNIQUE NOT NULL,
	description LONG NVARCHAR,
	isDefault SMALLINT NOT NULL DEFAULT 0,
	isLocked SMALLINT NOT NULL DEFAULT 0,
	authorId INTEGER NULL,
	
	FOREIGN KEY (authorId) REFERENCES DB.ODCLEANSTORE.USERS(id) ON DELETE SET NULL
);

CREATE TABLE DB.ODCLEANSTORE.TRANSFORMER_INSTANCES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	transformerId INTEGER NOT NULL,
	pipelineId INTEGER NOT NULL,
	configuration LONG NVARCHAR,
	runOnCleanDB SMALLINT NOT NULL DEFAULT 1,
	priority INTEGER NOT NULL DEFAULT 0,
	
	FOREIGN KEY (transformerId) REFERENCES DB.ODCLEANSTORE.TRANSFORMERS(id) ON DELETE CASCADE,
	FOREIGN KEY (pipelineId) REFERENCES DB.ODCLEANSTORE.PIPELINES(id) ON DELETE CASCADE
);

CREATE TABLE DB.ODCLEANSTORE.OI_RULES_ASSIGNMENT
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	transformerInstanceId INTEGER NOT NULL,
	groupId INTEGER NOT NULL,
	
	FOREIGN KEY (transformerInstanceId) REFERENCES DB.ODCLEANSTORE.TRANSFORMER_INSTANCES(id) ON DELETE CASCADE,
	FOREIGN KEY (groupId) REFERENCES DB.ODCLEANSTORE.OI_RULES_GROUPS(id) ON DELETE CASCADE
);

CREATE TABLE DB.ODCLEANSTORE.QA_RULES_ASSIGNMENT
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	transformerInstanceId INTEGER NOT NULL,
	groupId INTEGER NOT NULL,
	
	FOREIGN KEY (transformerInstanceId) REFERENCES DB.ODCLEANSTORE.TRANSFORMER_INSTANCES(id) ON DELETE CASCADE,
	FOREIGN KEY (groupId) REFERENCES DB.ODCLEANSTORE.QA_RULES_GROUPS(id) ON DELETE CASCADE
);

CREATE TABLE DB.ODCLEANSTORE.DN_RULES_ASSIGNMENT
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	transformerInstanceId INTEGER NOT NULL,
	groupId INTEGER NOT NULL,
	
	FOREIGN KEY (transformerInstanceId) REFERENCES DB.ODCLEANSTORE.TRANSFORMER_INSTANCES(id) ON DELETE CASCADE,
	FOREIGN KEY (groupId) REFERENCES DB.ODCLEANSTORE.DN_RULES_GROUPS(id) ON DELETE CASCADE
);

CREATE TABLE DB.ODCLEANSTORE.EN_INPUT_GRAPHS_STATES
(
	id INTEGER NOT NULL PRIMARY KEY,
	label NVARCHAR(255) UNIQUE NOT NULL,
	canResetPipeline SMALLINT NOT NULL DEFAULT 0
);

INSERT INTO DB.ODCLEANSTORE.EN_INPUT_GRAPHS_STATES (id, label, canResetPipeline) VALUES (1, n'IMPORTING', 0);
INSERT INTO DB.ODCLEANSTORE.EN_INPUT_GRAPHS_STATES (id, label, canResetPipeline) VALUES (2, n'DIRTY', 0);
INSERT INTO DB.ODCLEANSTORE.EN_INPUT_GRAPHS_STATES (id, label, canResetPipeline) VALUES (3, n'PROPAGATED', 1);
INSERT INTO DB.ODCLEANSTORE.EN_INPUT_GRAPHS_STATES (id, label, canResetPipeline) VALUES (4, n'DELETING', 0);
INSERT INTO DB.ODCLEANSTORE.EN_INPUT_GRAPHS_STATES (id, label, canResetPipeline) VALUES (5, n'PROCESSED', 1);
INSERT INTO DB.ODCLEANSTORE.EN_INPUT_GRAPHS_STATES (id, label, canResetPipeline) VALUES (6, n'PROCESSING', 1);
INSERT INTO DB.ODCLEANSTORE.EN_INPUT_GRAPHS_STATES (id, label, canResetPipeline) VALUES (7, n'QUEUED_FOR_DELETE', 0);
INSERT INTO DB.ODCLEANSTORE.EN_INPUT_GRAPHS_STATES (id, label, canResetPipeline) VALUES (8, n'QUEUED_URGENT', 0);
INSERT INTO DB.ODCLEANSTORE.EN_INPUT_GRAPHS_STATES (id, label, canResetPipeline) VALUES (9, n'QUEUED', 0);
INSERT INTO DB.ODCLEANSTORE.EN_INPUT_GRAPHS_STATES (id, label, canResetPipeline) VALUES (10, n'FINISHED', 0);
INSERT INTO DB.ODCLEANSTORE.EN_INPUT_GRAPHS_STATES (id, label, canResetPipeline) VALUES (11, n'WRONG', 0);
INSERT INTO DB.ODCLEANSTORE.EN_INPUT_GRAPHS_STATES (id, label, canResetPipeline) VALUES (12, n'DELETED', 0);
INSERT INTO DB.ODCLEANSTORE.EN_INPUT_GRAPHS_STATES (id, label, canResetPipeline) VALUES (13, n'OLDGRAPHSPREFIXED', 1);
INSERT INTO DB.ODCLEANSTORE.EN_INPUT_GRAPHS_STATES (id, label, canResetPipeline) VALUES (14, n'NEWGRAPHSPREPARED', 1);

CREATE TABLE DB.ODCLEANSTORE.EN_ATTACHED_ENGINES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	uuid VARCHAR(36) UNIQUE NOT NULL,
	isPipelineError SMALLINT NOT NULL,
	isNotifyRequired SMALLINT NOT NULL,
	stateDescription NVARCHAR(255),
	updated TIMESTAMP
);

INSERT INTO DB.ODCLEANSTORE.EN_ATTACHED_ENGINES (uuid, isPipelineError, isNotifyRequired) VALUES ('88888888-8888-8888-8888-888888888888', 0, 0);

CREATE TABLE DB.ODCLEANSTORE.EN_INPUT_GRAPHS
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	uuid VARCHAR(36) UNIQUE NOT NULL,
	stateId INTEGER NOT NULL,
	pipelineId INTEGER,
	engineId INTEGER,
	isInCleanDB SMALLINT NOT NULL DEFAULT 0,
	resetPipelineRequest SMALLINT NOT NULL DEFAULT 0,
	updated TIMESTAMP,

	FOREIGN KEY (stateID) REFERENCES DB.ODCLEANSTORE.EN_INPUT_GRAPHS_STATES(id),	
	FOREIGN KEY (pipelineId) REFERENCES DB.ODCLEANSTORE.PIPELINES(id) ON DELETE SET NULL,
	FOREIGN KEY (engineId) REFERENCES DB.ODCLEANSTORE.EN_ATTACHED_ENGINES(id) ON DELETE SET NULL
);

CREATE TABLE DB.ODCLEANSTORE.EN_PIPELINE_ERROR_TYPES
(
	id INTEGER NOT NULL PRIMARY KEY,
	label NVARCHAR(255) UNIQUE NOT NULL,
	description LONG NVARCHAR
);

INSERT INTO DB.ODCLEANSTORE.EN_PIPELINE_ERROR_TYPES (id, label, description) VALUES (1, n'TRANSFORMER_FAILURE', n'A failure of a transformer in the pipeline.');
INSERT INTO DB.ODCLEANSTORE.EN_PIPELINE_ERROR_TYPES (id, label, description) VALUES (2, n'DATA_LOADING_FAILURE', n'A failure during data loading to the pipeline.');
INSERT INTO DB.ODCLEANSTORE.EN_PIPELINE_ERROR_TYPES (id, label, description) VALUES (3, n'COPY_TO_CLEAN_DB_FAILURE', n'A failure during data copying from dirty to clean database.');

CREATE TABLE DB.ODCLEANSTORE.EN_GRAPHS_IN_ERROR
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	graphId INTEGER NOT NULL,
	errorTypeId INTEGER NOT NULL,
	errorMessage LONG NVARCHAR NOT NULL,
	
	FOREIGN KEY (errorTypeId) REFERENCES DB.ODCLEANSTORE.EN_PIPELINE_ERROR_TYPES(id) ON DELETE CASCADE,
	FOREIGN KEY (graphId) REFERENCES DB.ODCLEANSTORE.EN_INPUT_GRAPHS(id) ON DELETE CASCADE
);

CREATE TABLE DB.ODCLEANSTORE.EN_PIPELINE_RESULTS
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	graphId INTEGER NOT NULL,  
	pipelineId INTEGER NOT NULL,
	pipelineAuthorId INTEGER NULL,
	isExistingGraph SMALLINT NOT NULL DEFAULT 0,
	isSuccess SMALLINT NOT NULL DEFAULT 0,
	errorMessage LONG NVARCHAR NULL,
	created DATETIME,

	FOREIGN KEY (graphId) REFERENCES DB.ODCLEANSTORE.EN_INPUT_GRAPHS(id) ON DELETE CASCADE
);

CREATE TABLE DB.ODCLEANSTORE.EN_WORKING_ADDED_GRAPHS
(
	name NVARCHAR PRIMARY KEY,
	graphId INTEGER NOT NULL,

	FOREIGN KEY (graphId) REFERENCES DB.ODCLEANSTORE.EN_INPUT_GRAPHS(id) ON DELETE CASCADE
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
	(SELECT id FROM DB.ODCLEANSTORE.CR_MULTIVALUE_TYPES WHERE label = 'YES'),
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

/*
	===========================================================================
	ONTOLOGY MANAGEMENT
	===========================================================================
*/
CREATE TABLE DB.ODCLEANSTORE.ONTOLOGIES
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	label NVARCHAR(255) UNIQUE NOT NULL,
	description LONG NVARCHAR,
	graphName NVARCHAR(1024) UNIQUE NOT NULL,
	authorId INTEGER NULL,
	definition LONG NVARCHAR,
	
	FOREIGN KEY (authorId) REFERENCES DB.ODCLEANSTORE.USERS(id) ON DELETE SET NULL
); 

CREATE TABLE DB.ODCLEANSTORE.QA_RULES_GROUPS_TO_ONTOLOGIES_MAP
(
	groupId INTEGER NOT NULL IDENTITY PRIMARY KEY,
	ontologyId INTEGER NOT NULL,

	FOREIGN KEY (groupId) REFERENCES DB.ODCLEANSTORE.QA_RULES_GROUPS(id) ON DELETE CASCADE,
	FOREIGN KEY (ontologyId) REFERENCES DB.ODCLEANSTORE.ONTOLOGIES(id) ON DELETE CASCADE
);

CREATE TRIGGER QA_DELETE AFTER DELETE ON DB.ODCLEANSTORE.QA_RULES_GROUPS_TO_ONTOLOGIES_MAP
{
  DELETE FROM DB.ODCLEANSTORE.QA_RULES_GROUPS WHERE id = groupId;
};

CREATE TABLE DB.ODCLEANSTORE.DN_RULES_GROUPS_TO_ONTOLOGIES_MAP
(
	groupId INTEGER NOT NULL IDENTITY PRIMARY KEY,
	ontologyId INTEGER NOT NULL,

	FOREIGN KEY (groupId) REFERENCES DB.ODCLEANSTORE.DN_RULES_GROUPS(id) ON DELETE CASCADE,
	FOREIGN KEY (ontologyId) REFERENCES DB.ODCLEANSTORE.ONTOLOGIES(id) ON DELETE CASCADE
);

CREATE TRIGGER DN_DELETE AFTER DELETE ON DB.ODCLEANSTORE.DN_RULES_GROUPS_TO_ONTOLOGIES_MAP
{
  DELETE FROM DB.ODCLEANSTORE.DN_RULES_GROUPS WHERE id = groupId;
};

CREATE TABLE DB.ODCLEANSTORE.RELATION_TYPES
(
  id INTEGER NOT NULL IDENTITY PRIMARY KEY,
  uri NVARCHAR(1024) UNIQUE NOT NULL
);

DELETE FROM DB.ODCLEANSTORE.RELATION_TYPES;

INSERT INTO DB.ODCLEANSTORE.RELATION_TYPES (uri) VALUES (n'http://www.w3.org/2002/07/owl#sameAs');
INSERT INTO DB.ODCLEANSTORE.RELATION_TYPES (uri) VALUES (n'http://www.w3.org/2002/07/owl#equivalentProperty');
INSERT INTO DB.ODCLEANSTORE.RELATION_TYPES (uri) VALUES (n'http://www.w3.org/2000/01/rdf-schema#subClassOf');
INSERT INTO DB.ODCLEANSTORE.RELATION_TYPES (uri) VALUES (n'http://www.w3.org/2000/01/rdf-schema#subPropertyOf');




