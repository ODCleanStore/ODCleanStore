SET AUTOCOMMIT ON;


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
    label NVARCHAR(255) NOT NULL,
    description LONG NVARCHAR,
    
    FOREIGN KEY (groupId) REFERENCES DB.ODCLEANSTORE.QA_RULES_GROUPS(id) ON DELETE CASCADE
);

CREATE TABLE DB.ODCLEANSTORE.QA_RULES_UNCOMMITTED
(
    id INTEGER NOT NULL IDENTITY PRIMARY KEY,
    groupId INTEGER NOT NULL,
    filter LONG NVARCHAR NOT NULL,
    coefficient REAL NOT NULL,
    label NVARCHAR(255) NOT NULL,
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
    label NVARCHAR(255) NOT NULL,
    description LONG NVARCHAR,
    
    FOREIGN KEY (groupId) REFERENCES DB.ODCLEANSTORE.DN_RULES_GROUPS(id) ON DELETE CASCADE
);

CREATE TABLE DB.ODCLEANSTORE.DN_RULES_UNCOMMITTED
(
    id INTEGER NOT NULL IDENTITY PRIMARY KEY,
    groupId INTEGER NOT NULL,
    label NVARCHAR(255) NOT NULL,
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


DELETE FROM DB.ODCLEANSTORE.PIPELINES;

INSERT INTO DB.ODCLEANSTORE.PIPELINES (label, description, isDefault, authorId)
VALUES (n'default-pipeline', n'A default pipeline', 1, NULL);

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
    namedGraphsPrefix NVARCHAR NOT NULL,
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




/* 
  Dump all triples in the given named graph to a file, serialized as TTL.
  @param srcgraph dumped graph URI
  @param out_file (absolute) path to the written file; backslashes must be escaped
  Adapted from http://www.openlinksw.com/dataspace/dav/wiki/Main/VirtDumpLoadRdfGraphs 
*/
CREATE PROCEDURE dump_graph_ttl (
        IN  srcgraph VARCHAR, 
        IN  out_file VARCHAR) {
    DECLARE  file_name VARCHAR;
    DECLARE  env, ses ANY;
    DECLARE  ses_len, max_ses_len INTEGER;
    SET ISOLATION = 'uncommitted';
    max_ses_len := 10000000;
    string_to_file(
            out_file, 
            sprintf ('# Dump of graph <%s>, as of %s\n', srcgraph, CAST (NOW() AS VARCHAR)), 
            -2);
    env := vector (dict_new (16000), 0, '', '', '', 0, 0, 0, 0);
    ses := string_output ();
    FOR (SELECT * FROM (
            SPARQL DEFINE input:storage "" 
            SELECT ?s ?p ?o { 
	        GRAPH `iri(?:srcgraph)` { ?s ?p ?o } 
	    }) AS sub OPTION (LOOP)) DO {
        http_ttl_triple (env, "s", "p", "o", ses);
        ses_len := length (ses);
        IF (ses_len > max_ses_len) {
            string_to_file (out_file, ses, -1);
            ses := string_output ();
        }
    }
    IF (LENGTH (ses)) {
        http (' .\n', ses);
        string_to_file (out_file, ses, -1);
    }
};



INSERT INTO DB.DBA.SYS_XML_PERSISTENT_NS_DECL VALUES ('odcs', 'http://opendata.cz/infrastructure/odcleanstore/');
INSERT INTO DB.DBA.SYS_XML_PERSISTENT_NS_DECL VALUES ('odcs-data', 'http://opendata.cz/infrastructure/odcleanstore/data/');
INSERT INTO DB.DBA.SYS_XML_PERSISTENT_NS_DECL VALUES ('odcs-metadata', 'http://opendata.cz/infrastructure/odcleanstore/metadata/');
INSERT INTO DB.DBA.SYS_XML_PERSISTENT_NS_DECL VALUES ('odcs-provenance', 'http://opendata.cz/infrastructure/odcleanstore/provenanceMetadata/');



/* username: adm, password: adm, roles: ADM */
INSERT INTO DB.ODCLEANSTORE.USERS (username, email, passwordHash, salt, firstname, surname) 
VALUES (n'adm', n'adm@example.com', n'0e2aeeeb4125bea8d439c61050a08b52', n'salted', n'The', n'Administrator');

/* username: scraper, password: reparcs, roles: SCR */
INSERT INTO DB.ODCLEANSTORE.USERS (username, email, passwordHash, salt, firstname, surname)
VALUES (n'scraper', n'scraper@example.com', n'a83d2a0a4ce1839c6884cf1238ce9da6', n'salted', n'The', n'Scraper');

INSERT INTO DB.ODCLEANSTORE.ROLES_ASSIGNED_TO_USERS VALUES (
	(SELECT id FROM DB.ODCLEANSTORE.USERS WHERE username = n'adm'),
	(SELECT id FROM DB.ODCLEANSTORE.ROLES WHERE label = n'ADM'));
INSERT INTO DB.ODCLEANSTORE.ROLES_ASSIGNED_TO_USERS VALUES (
	(SELECT id FROM DB.ODCLEANSTORE.USERS WHERE username = n'adm'),
	(SELECT id FROM DB.ODCLEANSTORE.ROLES WHERE label = n'PIC'));
INSERT INTO DB.ODCLEANSTORE.ROLES_ASSIGNED_TO_USERS VALUES (
	(SELECT id FROM DB.ODCLEANSTORE.USERS WHERE username = n'adm'),
	(SELECT id FROM DB.ODCLEANSTORE.ROLES WHERE label = n'ONC'));
INSERT INTO DB.ODCLEANSTORE.ROLES_ASSIGNED_TO_USERS VALUES (
	(SELECT id FROM DB.ODCLEANSTORE.USERS WHERE username = n'scraper'),
	(SELECT id FROM DB.ODCLEANSTORE.ROLES WHERE label = n'SCR'));


/*
	Enables and rebuilds fulltext index for use with keyword queries.
*/
DB.DBA.RDF_OBJ_FT_RULE_ADD (null, null, 'All');
DB.DBA.VT_INC_INDEX_DB_DBA_RDF_OBJ ();


INSERT INTO DB.ODCLEANSTORE.PIPELINES (label, description, isDefault, authorId)
VALUES (n'example-pipeline', n'An example pipeline', 1, NULL);

INSERT INTO DB.ODCLEANSTORE.TRANSFORMER_INSTANCES (transformerId, pipelineId, configuration, runOnCleanDB, priority) VALUES (
	(SELECT TOP 1 id FROM DB.ODCLEANSTORE.TRANSFORMERS WHERE fullClassName = n'cz.cuni.mff.odcleanstore.transformer.odcs.ODCSBNodeToResourceTransformer'),
	(SELECT id FROM DB.ODCLEANSTORE.PIPELINES WHERE label = 'example-pipeline'),
	n'',
	0, 
	100);

INSERT INTO DB.ODCLEANSTORE.TRANSFORMER_INSTANCES (transformerId, pipelineId, configuration, runOnCleanDB, priority) VALUES (
	(SELECT TOP 1 id FROM DB.ODCLEANSTORE.TRANSFORMERS WHERE fullClassName = n'cz.cuni.mff.odcleanstore.datanormalization.impl.DataNormalizerImpl'),
	(SELECT id FROM DB.ODCLEANSTORE.PIPELINES WHERE label = 'example-pipeline'),
	n'',
	0, 
	200);

INSERT INTO DB.ODCLEANSTORE.TRANSFORMER_INSTANCES (transformerId, pipelineId, configuration, runOnCleanDB, priority) VALUES (
	(SELECT TOP 1 id FROM DB.ODCLEANSTORE.TRANSFORMERS WHERE fullClassName = n'cz.cuni.mff.odcleanstore.linker.impl.LinkerImpl'),
	(SELECT id FROM DB.ODCLEANSTORE.PIPELINES WHERE label = 'example-pipeline'),
	n'linkWithinGraph=true',
	1, 
	300);
	
INSERT INTO DB.ODCLEANSTORE.TRANSFORMER_INSTANCES (transformerId, pipelineId, configuration, runOnCleanDB, priority) VALUES (
	(SELECT TOP 1 id FROM DB.ODCLEANSTORE.TRANSFORMERS WHERE fullClassName = n'cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAssessorImpl'),
	(SELECT id FROM DB.ODCLEANSTORE.PIPELINES WHERE label = 'example-pipeline'),
	n'', 
	1,
	400);

INSERT INTO DB.ODCLEANSTORE.TRANSFORMER_INSTANCES (transformerId, pipelineId, configuration, runOnCleanDB, priority) VALUES (
	(SELECT TOP 1 id FROM DB.ODCLEANSTORE.TRANSFORMERS WHERE fullClassName = n'cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAggregatorImpl'),
	(SELECT id FROM DB.ODCLEANSTORE.PIPELINES WHERE label = 'example-pipeline'),
	n'', 
	1,
	500);



INSERT INTO DB.ODCLEANSTORE.QA_RULES_GROUPS (label, description) VALUES (n'test group', n'short description of this group');

insert into DB.ODCLEANSTORE.QA_RULES_UNCOMMITTED (groupId, filter, coefficient, description) values ((SELECT id FROM DB.ODCLEANSTORE.QA_RULES_GROUPS WHERE label = n'test group'), n'{{?s <http://purl.org/procurement#referenceNumber> ?o} FILTER (bif:regexp_like(?o, \'[a-zA-Z]\'))}', 0.9, n'PROCUREMENT REFERENCE NUMBER CONSISTS OF UNANTICIPATED CHARACTERS');
insert into DB.ODCLEANSTORE.QA_RULES_UNCOMMITTED (groupId, filter, coefficient, description) values ((SELECT id FROM DB.ODCLEANSTORE.QA_RULES_GROUPS WHERE label = n'test group'), n'{{?s <http://purl.org/procurement#procedureType> ?o}} GROUP BY ?g ?s HAVING count(?o) > 1', 0.75, n'PROCEDURE TYPE AMBIGUOUS');
insert into DB.ODCLEANSTORE.QA_RULES_UNCOMMITTED (groupId, filter, coefficient, description) values ((SELECT id FROM DB.ODCLEANSTORE.QA_RULES_GROUPS WHERE label = n'test group'), n'{{?s <http://purl.org/procurement#tenderDeadline> ?d; <http://purl.org/procurement#endDate> ?e} FILTER (?e > ?d)}', 0.9, n'TENDER COMPLETION DATE EXCEEDED ITS DEADLINE');
insert into DB.ODCLEANSTORE.QA_RULES_UNCOMMITTED (groupId, filter, coefficient, description) values ((SELECT id FROM DB.ODCLEANSTORE.QA_RULES_GROUPS WHERE label = n'test group'), n'{{?s <http://purl.org/procurement#numberOfTenders> ?n. ?s <http://purl.org/procurement#tender> ?t}} GROUP BY ?g ?s ?n HAVING count(?t) != ?n', 0.9, n'LIST OF TENDERS HAS DIFFERENT SIZE FROM WHAT WAS EXPECTED BY \'numberOfTenders\' PROPERTY');
insert into DB.ODCLEANSTORE.QA_RULES_UNCOMMITTED (groupId, filter, coefficient, description) values ((SELECT id FROM DB.ODCLEANSTORE.QA_RULES_GROUPS WHERE label = n'test group'), n'{{?s <http://purl.org/procurement#contactPerson> ?c}} GROUP BY ?g HAVING count(?c) != 1', 0.8, n'PROCUREMENT CONTACT PERSON MISSING');
insert into DB.ODCLEANSTORE.QA_RULES_UNCOMMITTED (groupId, filter, coefficient, description) values ((SELECT id FROM DB.ODCLEANSTORE.QA_RULES_GROUPS WHERE label = n'test group'), n'{{?s <http://purl.org/procurement#lot> ?c; <http://purl.org/procurement#tender> ?t}}', 0.8, n'PROCUREMENT BROKEN INTO SEVERAL CONTRACTS CANNOT HAVE DIRECT TENDERS');
insert into DB.ODCLEANSTORE.QA_RULES_UNCOMMITTED (groupId, filter, coefficient, description) values ((SELECT id FROM DB.ODCLEANSTORE.QA_RULES_GROUPS WHERE label = n'test group'), n'{{?s <http://purl.org/procurement#estimatedPrice> ?p1; <http://purl.org/procurement#actualPrice> ?p2. ?p1 <http://purl.org/goodrelations/v1#hasCurrencyValue> ?v1. ?p2 <http://purl.org/goodrelations/v1#hasCurrencyValue> ?v2} FILTER (2 * ?v1 < ?v2)}', 0.8, n'PROCUREMENT ACTUAL COSTS ARE ABOVE TWICE THE ESTIMATE');
insert into DB.ODCLEANSTORE.QA_RULES_UNCOMMITTED (groupId, filter, coefficient, description) values ((SELECT id FROM DB.ODCLEANSTORE.QA_RULES_GROUPS WHERE label = n'test group'), n'{{?s <http://purl.org/procurement#procedureType> <http://purl.org/procurement#Open>; <http://purl.org/procurement#estimatedPrice> ?p. ?p <http://purl.org/goodrelations/v1#hasCurrencyValue> ?v.} FILTER (?v < 50000 OR ?v > 3000000)}', 0.8, n'PROCEDURE TYPE IS INCOMPATIBLE WITH THE ESTIMATED PRICE');
insert into DB.ODCLEANSTORE.QA_RULES_UNCOMMITTED (groupId, filter, coefficient, description) values ((SELECT id FROM DB.ODCLEANSTORE.QA_RULES_GROUPS WHERE label = n'test group'), n'{{?s <http://purl.org/procurement#awardDate> ?a; <http://purl.org/procurement#tenderDeadline> ?d.} FILTER (?d > ?a)}', 0.8, n'TENDER AWARDED BEFORE APPLICATION DEADLINE');

DELETE FROM DB.ODCLEANSTORE.QA_RULES;
INSERT INTO DB.ODCLEANSTORE.QA_RULES SELECT * FROM DB.ODCLEANSTORE.QA_RULES_UNCOMMITTED;


-- GROUPS
INSERT INTO DB.ODCLEANSTORE.DN_RULES_GROUPS (label, description, authorId) VALUES (n'test group', n'this is a group for testing purposes', NULL);

-- RULES
INSERT INTO DB.ODCLEANSTORE.DN_RULES_UNCOMMITTED (id, groupId, description) VALUES (0, (SELECT id FROM DB.ODCLEANSTORE.DN_RULES_GROUPS WHERE label = 'test group'), '');
INSERT INTO DB.ODCLEANSTORE.DN_RULES_UNCOMMITTED (id, groupId, description) VALUES (1, (SELECT id FROM DB.ODCLEANSTORE.DN_RULES_GROUPS WHERE label = 'test group'), '');

-- COMPONENTS
INSERT INTO DB.ODCLEANSTORE.DN_RULE_COMPONENTS_UNCOMMITTED (ruleId, typeId, modification, description) VALUES (
	0,
	(SELECT id FROM DB.ODCLEANSTORE.DN_RULE_COMPONENT_TYPES WHERE label = 'INSERT'),
	'{?a ?b ?y} WHERE {GRAPH $$$$graph$$$$ {SELECT ?a ?b fn:replace(str(?c), ".", "*") AS ?y WHERE {?a ?b ?c}}}', 
	'');
INSERT INTO DB.ODCLEANSTORE.DN_RULE_COMPONENTS_UNCOMMITTED (ruleId, typeId, modification, description) VALUES (
	0,
	(SELECT id FROM DB.ODCLEANSTORE.DN_RULE_COMPONENT_TYPES WHERE label = 'DELETE'),
	'{?a ?b ?c} WHERE {?a ?b ?c FILTER (contains(str(?c), "*") = false)}',
	'');
INSERT INTO DB.ODCLEANSTORE.DN_RULE_COMPONENTS_UNCOMMITTED (ruleId, typeId, modification, description) VALUES (
	1,
	(SELECT id FROM DB.ODCLEANSTORE.DN_RULE_COMPONENT_TYPES WHERE label = 'INSERT'),
	'{?a <http://example.com/#test> ?b} WHERE {?a ?b ?c FILTER (contains(str(?c), "*******"))}', 
	'');

DELETE FROM DB.ODCLEANSTORE.DN_RULES;
INSERT INTO DB.ODCLEANSTORE.DN_RULES SELECT * FROM DB.ODCLEANSTORE.DN_RULES_UNCOMMITTED;
DELETE FROM DB.ODCLEANSTORE.DN_RULE_COMPONENTS;
INSERT INTO DB.ODCLEANSTORE.DN_RULE_COMPONENTS SELECT * FROM DB.ODCLEANSTORE.DN_RULE_COMPONENTS_UNCOMMITTED;



INSERT INTO DB.ODCLEANSTORE.OI_RULES_GROUPS (label, description) VALUES (n'test group', n'test OI group');
    
INSERT INTO DB.ODCLEANSTORE.OI_RULES_UNCOMMITTED (groupId, label, linkType, sourceRestriction, targetRestriction, linkageRule, filterThreshold, filterLimit) VALUES (
	(SELECT id FROM DB.ODCLEANSTORE.OI_RULES_GROUPS WHERE label = n'test group'),
    n'supplier', 
	n'owl:sameAs', 
	n'?x <http://purl.org/procurement#supplier> ?a .', 
	n'?y <http://purl.org/procurement#supplier> ?b .',
	n'<LinkageRule>
  <Compare weight="1" threshold="0.0" required="true" metric="equality" id="unnamed_3">
    <Input path="?a/&lt;http://purl.org/procurement#title&gt;" id="unnamed_1"></Input>
    <Input path="?b/&lt;http://purl.org/procurement#title&gt;" id="unnamed_2"></Input>
  </Compare>
</LinkageRule>',
	null,
	null
);

INSERT INTO DB.ODCLEANSTORE.OI_OUTPUTS_UNCOMMITTED (ruleId, outputTypeId, minConfidence, maxConfidence, fileName, fileFormatId) VALUES (
	(SELECT id FROM DB.ODCLEANSTORE.OI_RULES_UNCOMMITTED WHERE label = n'supplier'),
	(SELECT id FROM DB.ODCLEANSTORE.OI_OUTPUT_TYPES WHERE label = 'DB'), 
	0.95,
	null, 
	null,
	null);

DELETE FROM DB.ODCLEANSTORE.OI_RULES;
INSERT INTO DB.ODCLEANSTORE.OI_RULES SELECT * FROM DB.ODCLEANSTORE.OI_RULES_UNCOMMITTED;
DELETE FROM DB.ODCLEANSTORE.OI_OUTPUTS;
INSERT INTO DB.ODCLEANSTORE.OI_OUTPUTS SELECT * FROM DB.ODCLEANSTORE.OI_OUTPUTS_UNCOMMITTED;
    
/*INSERT INTO DB.DBA.SYS_XML_PERSISTENT_NS_DECL VALUES ('purl','http://purl.org/procurement#');*/


INSERT INTO DB.ODCLEANSTORE.CR_PROPERTIES (property, multivalueTypeId, aggregationTypeId) VALUES (
	n'http://www.w3.org/2003/01/geo/wgs84_pos#long', 
	(SELECT id FROM DB.ODCLEANSTORE.CR_MULTIVALUE_TYPES WHERE label = 'DEFAULT'),
	(SELECT id FROM DB.ODCLEANSTORE.CR_AGGREGATION_TYPES WHERE label = 'AVG'));

INSERT INTO DB.ODCLEANSTORE.CR_PROPERTIES (property, multivalueTypeId, aggregationTypeId) VALUES (
	n'http://www.w3.org/1999/02/22-rdf-syntax-ns#type',
	(SELECT id FROM DB.ODCLEANSTORE.CR_MULTIVALUE_TYPES WHERE label = 'YES'),
	(SELECT id FROM DB.ODCLEANSTORE.CR_AGGREGATION_TYPES WHERE label = 'DEFAULT'));

SPARQL INSERT INTO <http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/dbpedia> {
	<http://dbpedia.org/resource/Berlin>	<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>	<http://dbpedia.org/class/yago/Locations>.
	<http://dbpedia.org/resource/Berlin>	<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>	<http://schema.org/City>.
	<http://dbpedia.org/resource/Berlin>	<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>	<http://umbel.org/umbel/rc/Village>.
	<http://dbpedia.org/resource/Berlin>	<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>	<http://schema.org/Place>.
	<http://dbpedia.org/resource/Berlin>	<http://www.w3.org/2000/01/rdf-schema#label>	"Berlin"@en.
	<http://dbpedia.org/resource/Berlin>	<http://www.w3.org/2003/01/geo/wgs84_pos#lat>	"52.50055694580078".
	<http://dbpedia.org/resource/Berlin>	<http://www.w3.org/2003/01/geo/wgs84_pos#long>	"13.39888858795166".
#	<http://dbpedia.org/resource/Berlin>	<http://dbpedia.org/ontology/abstract>	"Berlin is the capital city of Germany and is one of the 16 states of Germany. With a population of 3.45 million people, Berlin is Germany's largest city. It is the second most populous city proper and the seventh most populous urban area in the European Union. Located in northeastern Germany, it is the center of the Berlin/Brandenburg Metropolitan Region, which has 4.4 million residents from over 190 nations. Located in the European Plains, Berlin is influenced by a temperate seasonal climate. Around one third of the city's area is composed of forests, parks, gardens, rivers and lakes. First documented in the 13th century, Berlin was the capital of the Kingdom of Prussia (1701-1918), the German Empire (1871-1918), the Weimar Republic (1919-1933) and the Third Reich (1933-1945). Berlin in the 1920s was the third largest municipality in the world. After World War II, the city became divided into East Berlin-the capital of East Germany-and West Berlin, a West German exclave surrounded by the Berlin Wall (1961-1989). Following German reunification in 1990, the city regained its status as the capital of Germany, hosting 147 foreign embassies. Berlin is a world city of culture, politics, media, and science. Its economy is primarily based on the service sector, encompassing a diverse range of creative industries, media corporations, and convention venues. Berlin also serves as a continental hub for air and rail transport, and is a popular tourist destination. Significant industries include IT, pharmaceuticals, biomedical engineering, biotechnology, electronics, traffic engineering, and renewable energy. Berlin is home to renowned universities, research institutes, orchestras, museums, and celebrities, as well as host of many sporting events. Its urban settings and historical legacy have made it a popular location for international film productions. The city is well renowned for its festivals, diverse architecture, nightlife, contemporary arts, public transportation networks and a high quality of living."@en.
	<http://dbpedia.org/resource/Berlin>	<http://dbpedia.org/ontology/populationTotal>	"3450889".
	<http://dbpedia.org/resource/Berlin>	<http://dbpedia.org/property/name>	"Berlin"@en.
	<http://dbpedia.org/resource/Berlin>	<http://dbpedia.org/property/population>	"3450889"^^<http://www.w3.org/2001/XMLSchema#int>.
	<http://dbpedia.org/resource/Berlin>	<http://dbpedia.org/ontology/country>	<http://dbpedia.org/resource/Germany>.
};
SPARQL INSERT INTO <http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/freebase> {
	<http://rdf.freebase.com/ns/en.berlin>	<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>	<http://rdf.freebase.com/ns/location.citytown>.
	<http://rdf.freebase.com/ns/en.berlin>	<http://www.w3.org/2002/07/owl#sameAs>	<http://dbpedia.org/resource/Berlin>.
	<http://rdf.freebase.com/ns/en.berlin>	<http://www.w3.org/2002/07/owl#sameAs>	<http://dbpedia.org/resource/CityBerlin>.
	<http://rdf.freebase.com/ns/en.berlin>	<http://rdf.freebase.com/ns/location.geocode.longtitude>	"13.412687".
	<http://rdf.freebase.com/ns/en.berlin>	<http://rdf.freebase.com/ns/common.topic.alias>	"Berlin, Germany"@en.
	<http://rdf.freebase.com/ns/en.berlin>	<http://rdf.freebase.com/ns/common.topic.alias>	"Land Berlin"@en.
	<http://rdf.freebase.com/ns/en.berlin>	<http://rdf.freebase.com/ns/type.object.name>	"Berlin"@en.
	<http://rdf.freebase.com/ns/en.berlin>	<http://rdf.freebase.com/ns/location.geocode.latitude>	"52.52333831787109".
};
SPARQL INSERT INTO <http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/geonames> {
	<http://sws.geonames.org/2950159/>	<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>	<http://www.geonames.org/ontology#Feature>.
	<http://sws.geonames.org/2950159/>	<http://www.geonames.org/ontology#name>	"Berlin".
	<http://sws.geonames.org/2950159/>	<http://www.geonames.org/ontology#alternateName>	"Berlin"@en.
	<http://sws.geonames.org/2950159/>	<http://www.geonames.org/ontology#population>	"3426354".
	<http://sws.geonames.org/2950159/>	<http://www.w3.org/2003/01/geo/wgs84_pos#lat>	"52.52437".
	<http://sws.geonames.org/2950159/>	<http://www.w3.org/2003/01/geo/wgs84_pos#long>	"13.41053".
};
SPARQL INSERT INTO <http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/linkedgeodata> {
	<http://linkedgeodata.org/triplify/node240109189>	<http://www.w3.org/2000/01/rdf-schema#label> "Berlino"@it .
	<http://linkedgeodata.org/triplify/node240109189>	<http://www.w3.org/2000/01/rdf-schema#label> "Berlin"@en .
	<http://linkedgeodata.org/triplify/node240109189>	<http://www.w3.org/2003/01/geo/wgs84_pos#long> "13.3888548"^^<http://www.w3.org/2001/XMLSchema#decimal> .
	<http://linkedgeodata.org/triplify/node240109189>	<http://www.w3.org/2003/01/geo/wgs84_pos#lat> "52.5170397"^^<http://www.w3.org/2001/XMLSchema#decimal> .
	<http://linkedgeodata.org/triplify/node240109189>	<http://linkedgeodata.org/ontology/population> "3420768"^^<http://www.w3.org/2001/XMLSchema#integer> .
	<http://linkedgeodata.org/triplify/node240109189>	<http://linkedgeodata.org/property/capital> "yes" .
	<http://linkedgeodata.org/triplify/node240109189>	<http://www.georss.org/georss/point> "52.5170397 13.3888548" .
};
SPARQL INSERT INTO <http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/error> {
	<http://dbpedia.org/resource/Berlin>	<http://www.w3.org/2003/01/geo/wgs84_pos#lat>	"13.412687".
	<http://dbpedia.org/resource/Berlin>	<http://rdf.freebase.com/ns/location.geocode.latitude>	"13.412687".
};
SPARQL INSERT INTO <http://odcs.mff.cuni.cz/namedGraph/qe-test/germany/dbpedia> {
	<http://dbpedia.org/resource/Germany>	<http://www.w3.org/2000/01/rdf-schema#label>	"Deutschland".
};

SPARQL INSERT INTO <http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/sameAs> {
	<http://dbpedia.org/resource/Berlin>	<http://www.w3.org/2002/07/owl#sameAs>	<http://dbpedia.org/resource/Berlin>.
	<http://dbpedia.org/resource/Berlin>	<http://www.w3.org/2002/07/owl#sameAs>	<http://sws.geonames.org/2950159/>.
	<http://dbpedia.org/resource/Berlin>	<http://www.w3.org/2002/07/owl#sameAs>	<http://linkedgeodata.org/triplify/node240109189>.
	<http://dbpedia.org/resource/Berlin>	<http://www.w3.org/2002/07/owl#sameAs>	<http://www4.wiwiss.fu-berlin.de/eurostat/resource/regions/Berlin>.

	<http://schema.org/City>	<http://www.w3.org/2002/07/owl#sameAs>	<http://odcs.mff.cuni.cz/resource/qe-test/berlin/City>.
	<http://odcs.mff.cuni.cz/resource/qe-test/berlin/City>	<http://www.w3.org/2002/07/owl#sameAs>	<http://odcs.mff.cuni.cz/resource/qe-test/berlin/City2>.
	<http://odcs.mff.cuni.cz/resource/qe-test/berlin/City2>	<http://www.w3.org/2002/07/owl#sameAs>	<http://rdf.freebase.com/ns/location.citytown>.
};
SPARQL INSERT INTO <http://odcs.mff.cuni.cz/namedGraph/qe-test/property-sameAs> {
	<http://www.w3.org/2003/01/geo/wgs84_pos#lat>	<http://www.w3.org/2002/07/owl#sameAs>	<http://rdf.freebase.com/ns/location.geocode.latitude>.
	<http://www.w3.org/2003/01/geo/wgs84_pos#long>	<http://www.w3.org/2002/07/owl#sameAs>	<http://rdf.freebase.com/ns/location.geocode.longtitude>.
	<http://dbpedia.org/property/name>	<http://www.w3.org/2002/07/owl#sameAs>	<http://www.w3.org/2000/01/rdf-schema#label>.
	<http://dbpedia.org/property/name>	<http://www.w3.org/2002/07/owl#sameAs>	<http://www.geonames.org/ontology#name>.
	<http://dbpedia.org/property/name>	<http://www.w3.org/2002/07/owl#sameAs>	<http://www.geonames.org/ontology#alternateName>.
	<http://dbpedia.org/property/name>	<http://www.w3.org/2002/07/owl#sameAs>	<http://rdf.freebase.com/ns/type.object.name>.
	<http://dbpedia.org/property/population>	<http://www.w3.org/2002/07/owl#sameAs>	<http://dbpedia.org/ontology/populationTotal>.
	<http://dbpedia.org/property/population>	<http://www.w3.org/2002/07/owl#sameAs>	<http://www.geonames.org/ontology#population>.
	<http://dbpedia.org/property/population>	<http://www.w3.org/2002/07/owl#sameAs>	<http://linkedgeodata.org/ontology/population>.
	<http://dbpedia.org/property/population>	<http://www.w3.org/2002/07/owl#sameAs>	<http://www4.wiwiss.fu-berlin.de/eurostat/resource/eurostat/population_total>.
	<http://rdf.freebase.com/ns/common.topic.alias>	<http://www.w3.org/2002/07/owl#sameAs>	<http://www.geonames.org/ontology#alternateName>.
	<http://dbpedia.org/ontology/abstract>	<http://www.w3.org/2002/07/owl#sameAs>	<http://odcs.mff.cuni.cz/resource/qe-test/abstract-syn1>.
	<http://odcs.mff.cuni.cz/resource/qe-test/abstract-syn1>	<http://www.w3.org/2002/07/owl#sameAs>	<http://odcs.mff.cuni.cz/resource/qe-test/abstract-syn2>.
};
SPARQL INSERT INTO <http://odcs.mff.cuni.cz/namedGraph/qe-test/property-labels> {
	<http://www.w3.org/2003/01/geo/wgs84_pos#lat>	<http://www.w3.org/2000/01/rdf-schema#label>	"Latitude".
	<http://www.w3.org/2003/01/geo/wgs84_pos#long>	<http://www.w3.org/2000/01/rdf-schema#label>	"Longtitude".
	<http://dbpedia.org/ontology/abstract>	<http://www.w3.org/2000/01/rdf-schema#label>	"Abstract".
};

SPARQL INSERT INTO <http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/metadata/dbpedia> {
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/dbpedia>	<http://opendata.cz/infrastructure/odcleanstore/metadataGraph> <http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/metadata/dbpedia>.
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/dbpedia>	<http://opendata.cz/infrastructure/odcleanstore/insertedAt>	"2012-04-01T12:34:56"^^<http://www.w3.org/2001/XMLSchema#dateTime>.
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/dbpedia>	<http://opendata.cz/infrastructure/odcleanstore/score>	"0.9"^^<http://www.w3.org/2001/XMLSchema#double>.
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/dbpedia>	<http://opendata.cz/infrastructure/odcleanstore/source>	<http://dbpedia.org/page/Berlin>.
};
SPARQL INSERT INTO <http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/metadata/freebase> {
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/freebase>	<http://opendata.cz/infrastructure/odcleanstore/metadataGraph> <http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/metadata/freebase>.
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/freebase>	<http://opendata.cz/infrastructure/odcleanstore/insertedAt>	"2012-04-02T12:34:56"^^<http://www.w3.org/2001/XMLSchema#dateTime>.
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/freebase>	<http://opendata.cz/infrastructure/odcleanstore/score>	"0.8"^^<http://www.w3.org/2001/XMLSchema#double>.
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/freebase>	<http://opendata.cz/infrastructure/odcleanstore/source>	<http://www.freebase.com/view/en/berlin>.
};
SPARQL INSERT INTO <http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/metadata/geonames> {
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/geonames>	<http://opendata.cz/infrastructure/odcleanstore/metadataGraph> <http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/metadata/geonames>.
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/geonames>	<http://opendata.cz/infrastructure/odcleanstore/insertedAt>	"2012-04-03T12:34:56"^^<http://www.w3.org/2001/XMLSchema#dateTime>.
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/geonames>	<http://opendata.cz/infrastructure/odcleanstore/score>	"0.8"^^<http://www.w3.org/2001/XMLSchema#double>.
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/geonames>	<http://opendata.cz/infrastructure/odcleanstore/source> <http://www.geonames.org/2950159/berlin.html>	.
};
SPARQL INSERT INTO <http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/metadata/linkedgeodata> {
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/linkedgeodata>	<http://opendata.cz/infrastructure/odcleanstore/metadataGraph> <http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/metadata/linkedgeodata>.
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/linkedgeodata>	<http://opendata.cz/infrastructure/odcleanstore/insertedAt>	"2012-04-04T12:34:56"^^<http://www.w3.org/2001/XMLSchema#dateTime>.
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/linkedgeodata>	<http://opendata.cz/infrastructure/odcleanstore/score>	"0.8"^^<http://www.w3.org/2001/XMLSchema#double>.
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/linkedgeodata>	<http://opendata.cz/infrastructure/odcleanstore/source>	<http://linkedgeodata.org/page/node240109189>.
};
SPARQL INSERT INTO <http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/metadata/error> {
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/error>	<http://opendata.cz/infrastructure/odcleanstore/metadataGraph> <http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/metadata/error>.
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/error>	<http://opendata.cz/infrastructure/odcleanstore/score>	"0.8"^^<http://www.w3.org/2001/XMLSchema#double>.
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/berlin/error>	<http://opendata.cz/infrastructure/odcleanstore/source>		<http://example.com>.
};
SPARQL INSERT INTO <http://odcs.mff.cuni.cz/namedGraph/qe-test/germany/metadata/dbpedia> {
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/germany/dbpedia>	<http://opendata.cz/infrastructure/odcleanstore/metadataGraph> <http://odcs.mff.cuni.cz/namedGraph/qe-test/germany/metadata/dbpedia>.
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/germany/dbpedia>	<http://opendata.cz/infrastructure/odcleanstore/insertedAt>	"2012-04-05T12:34:56"^^<http://www.w3.org/2001/XMLSchema#dateTime>.
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/germany/dbpedia>	<http://opendata.cz/infrastructure/odcleanstore/score>	"0.9"^^<http://www.w3.org/2001/XMLSchema#double>.
	<http://odcs.mff.cuni.cz/namedGraph/qe-test/germany/dbpedia>	<http://opendata.cz/infrastructure/odcleanstore/source>	<http://dbpedia.org/page/Germany>.
};



