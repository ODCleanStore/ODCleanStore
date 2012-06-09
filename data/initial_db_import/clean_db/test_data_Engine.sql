DELETE FROM DB.ODCLEANSTORE.TRANSFORMERS_TO_PIPELINES_ASSIGNMENT;
DELETE FROM DB.ODCLEANSTORE.TRANSFORMERS;
DELETE FROM DB.ODCLEANSTORE.PIPELINES;

set_identity_column('DB.ODCLEANSTORE.TRANSFORMERS','id', 1);
set_identity_column('DB.ODCLEANSTORE.PIPELINES','id', 1);


INSERT INTO DB.ODCLEANSTORE.TRANSFORMERS (label, description, jarPath, fullClassName) 
VALUES (n'QA', n'The standard quality assessment transformer', n'.', n'cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAssessorImpl');

INSERT INTO DB.ODCLEANSTORE.TRANSFORMERS (label, description, jarPath, fullClassName) 
VALUES (n'Linker', n'The standard object identification transformer',  n'.', n'cz.cuni.mff.odcleanstore.linker.impl.LinkerImpl');

INSERT INTO DB.ODCLEANSTORE.TRANSFORMERS (label, description, jarPath, fullClassName)
VALUES (n'DN', n'The standard data normalization transformer', n'.', n'not yet known');


INSERT INTO DB.ODCLEANSTORE.PIPELINES (label, description, runOnCleanDB)
VALUES (n'Dirty', n'A basic dirty pipeline', 0);

INSERT INTO DB.ODCLEANSTORE.PIPELINES (label, description, runOnCleanDB)
VALUES (n'Clean', n'A basic clean pipeline', 1);


INSERT INTO DB.ODCLEANSTORE.TRANSFORMERS_TO_PIPELINES_ASSIGNMENT (transformerId, pipelineId, workDirPath, configuration, priority)
VALUES (1, 1, n'qa', n'', 1);

INSERT INTO DB.ODCLEANSTORE.TRANSFORMERS_TO_PIPELINES_ASSIGNMENT (transformerId, pipelineId, workDirPath, configuration, priority)
VALUES (2, 1, n'link', n'', 2);

INSERT INTO DB.ODCLEANSTORE.TRANSFORMERS_TO_PIPELINES_ASSIGNMENT (transformerId, pipelineId, workDirPath, configuration, priority)
VALUES (3, 1, n'.', n'', 3);

INSERT INTO DB.ODCLEANSTORE.TRANSFORMERS_TO_PIPELINES_ASSIGNMENT (transformerId, pipelineId, workDirPath, configuration, priority)
VALUES (1, 2, n'qa', n'', 1);

INSERT INTO DB.ODCLEANSTORE.TRANSFORMERS_TO_PIPELINES_ASSIGNMENT (transformerId, pipelineId, workDirPath, configuration, priority)
VALUES (2, 2, n'link', n'', 1);

SELECT * FROM DB.ODCLEANSTORE.TRANSFORMERS;
SELECT * FROM DB.ODCLEANSTORE.PIPELINES;
SELECT * FROM DB.ODCLEANSTORE.TRANSFORMERS_TO_PIPELINES_ASSIGNMENT;

