DELETE FROM DB.ODCLEANSTORE.EN_INPUT_GRAPHS;

DELETE FROM DB.ODCLEANSTORE.TRANSFORMER_INSTANCES;
DELETE FROM DB.ODCLEANSTORE.TRANSFORMERS;
DELETE FROM DB.ODCLEANSTORE.PIPELINES;
DELETE FROM DB.ODCLEANSTORE.OI_RULES_ASSIGNMENT;
DELETE FROM DB.ODCLEANSTORE.QA_RULES_ASSIGNMENT;

DELETE FROM DB.ODCLEANSTORE.BACKUP_TRANSFORMER_INSTANCES;
DELETE FROM DB.ODCLEANSTORE.BACKUP_TRANSFORMERS;
DELETE FROM DB.ODCLEANSTORE.BACKUP_PIPELINES;
DELETE FROM DB.ODCLEANSTORE.BACKUP_OI_RULES_ASSIGNMENT;
DELETE FROM DB.ODCLEANSTORE.BACKUP_QA_RULES_ASSIGNMENT;

set_identity_column('DB.ODCLEANSTORE.TRANSFORMERS','id', 1);
set_identity_column('DB.ODCLEANSTORE.PIPELINES','id', 1);

--
-- fill backup tables
--

INSERT INTO DB.ODCLEANSTORE.BACKUP_TRANSFORMERS (label, description, jarPath, fullClassName) 
VALUES (n'QA', n'The standard quality assessment transformer', n'.', n'cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAssessorImpl');

INSERT INTO DB.ODCLEANSTORE.BACKUP_TRANSFORMERS (label, description, jarPath, fullClassName) 
VALUES (n'Linker', n'The standard object identification transformer',  n'.', n'cz.cuni.mff.odcleanstore.linker.impl.LinkerImpl');

INSERT INTO DB.ODCLEANSTORE.BACKUP_TRANSFORMERS (label, description, jarPath, fullClassName)
VALUES (n'DN', n'The standard data normalization transformer', n'.', n'not yet known');


INSERT INTO DB.ODCLEANSTORE.BACKUP_PIPELINES (label, description, isDefault)
VALUES (n'Dirty', n'A basic dirty pipeline', 0);

INSERT INTO DB.ODCLEANSTORE.BACKUP_PIPELINES (label, description, isDefault)
VALUES (n'Clean', n'A basic clean pipeline', 1);


INSERT INTO DB.ODCLEANSTORE.BACKUP_TRANSFORMER_INSTANCES (transformerId, pipelineId, workDirPath, configuration, priority)
VALUES (1, 1, n'transformers-working-dir/qa', n'', 1);

INSERT INTO DB.ODCLEANSTORE.BACKUP_TRANSFORMER_INSTANCES (transformerId, pipelineId, workDirPath, configuration, priority)
VALUES (2, 1, n'transformers-working-dir/link', n'1', 2);

INSERT INTO DB.ODCLEANSTORE.BACKUP_TRANSFORMER_INSTANCES (transformerId, pipelineId, workDirPath, configuration, priority)
VALUES (3, 1, n'transformers-working-dir/dn', n'', 3);

INSERT INTO DB.ODCLEANSTORE.BACKUP_TRANSFORMER_INSTANCES (transformerId, pipelineId, workDirPath, configuration, priority)
VALUES (1, 2, n'transformers-working-dir/qa', n'', 1);

INSERT INTO DB.ODCLEANSTORE.BACKUP_TRANSFORMER_INSTANCES (transformerId, pipelineId, workDirPath, configuration, priority)
VALUES (2, 2, n'transformers-working-dir/link', n'', 1);

--
-- fill official tables
--

INSERT INTO DB.ODCLEANSTORE.TRANSFORMERS (label, description, jarPath, fullClassName) 
VALUES (n'QA', n'The standard quality assessment transformer', n'.', n'cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAssessorImpl');

INSERT INTO DB.ODCLEANSTORE.TRANSFORMERS (label, description, jarPath, fullClassName) 
VALUES (n'Linker', n'The standard object identification transformer',  n'.', n'cz.cuni.mff.odcleanstore.linker.impl.LinkerImpl');

INSERT INTO DB.ODCLEANSTORE.TRANSFORMERS (label, description, jarPath, fullClassName)
VALUES (n'DN', n'The standard data normalization transformer', n'.', n'not yet known');


INSERT INTO DB.ODCLEANSTORE.PIPELINES (label, description, isDefault)
VALUES (n'Dirty', n'A basic dirty pipeline', 0);

INSERT INTO DB.ODCLEANSTORE.PIPELINES (label, description, isDefault)
VALUES (n'Clean', n'A basic clean pipeline', 1);


INSERT INTO DB.ODCLEANSTORE.TRANSFORMER_INSTANCES (transformerId, pipelineId, workDirPath, configuration, priority)
VALUES (1, 1, n'transformers-working-dir/qa', n'', 1);

INSERT INTO DB.ODCLEANSTORE.TRANSFORMER_INSTANCES (transformerId, pipelineId, workDirPath, configuration, priority)
VALUES (2, 1, n'transformers-working-dir/link', n'1', 2);

INSERT INTO DB.ODCLEANSTORE.TRANSFORMER_INSTANCES (transformerId, pipelineId, workDirPath, configuration, priority)
VALUES (3, 1, n'transformers-working-dir/dn', n'', 3);

INSERT INTO DB.ODCLEANSTORE.TRANSFORMER_INSTANCES (transformerId, pipelineId, workDirPath, configuration, priority)
VALUES (1, 2, n'transformers-working-dir/qa', n'', 1);

INSERT INTO DB.ODCLEANSTORE.TRANSFORMER_INSTANCES (transformerId, pipelineId, workDirPath, configuration, priority)
VALUES (2, 2, n'transformers-working-dir/link', n'', 1);
