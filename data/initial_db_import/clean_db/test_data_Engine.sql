DELETE FROM DB.ODCLEANSTORE.TRANSFORMERS;

INSERT INTO DB.ODCLEANSTORE.TRANSFORMERS (label, description, jarPath, fullClassName) 
VALUES (n'QA', n'The standard quality assessment transformer', n'.', n'cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAssessorImpl');

INSERT INTO DB.ODCLEANSTORE.TRANSFORMERS (label, description, jarPath, fullClassName) 
VALUES (n'Linker', n'The standard object identification transformer',  n'.', n'cz.cuni.mff.odcleanstore.linker.impl.LinkerImpl');

INSERT INTO DB.ODCLEANSTORE.TRANSFORMERS (label, description, jarPath, fullClassName)
VALUES (n'DN', n'The standard data normalization transformer', n'.', n'not yet known');

DELETE FROM DB.ODCLEANSTORE.PIPELINES;

INSERT INTO DB.ODCLEANSTORE.PIPELINES (label, description, runOnCleanDB)
VALUES (n'Dirty', n'A basic dirty pipeline', 0);

INSERT INTO DB.ODCLEANSTORE.PIPELINES (label, description, runOnCleanDB)
VALUES (n'Clean', n'A basic clean pipeline', 1);

DELETE FROM DB.ODCLEANSTORE.TRANSFORMERS_TO_PIPELINES_ASSIGNMENT;

INSERT INTO DB.ODCLEANSTORE.TRANSFORMERS_TO_PIPELINES_ASSIGNMENT (transformerId, pipelineId, workDirPath, configuration, priority)
VALUES (1, 1, n'.', n'Config', 3);

INSERT INTO DB.ODCLEANSTORE.TRANSFORMERS_TO_PIPELINES_ASSIGNMENT (transformerId, pipelineId, workDirPath, configuration, priority)
VALUES (2, 1, n'.', n'Config', 2);

INSERT INTO DB.ODCLEANSTORE.TRANSFORMERS_TO_PIPELINES_ASSIGNMENT (transformerId, pipelineId, workDirPath, configuration, priority)
VALUES (3, 1, n'.', n'Config', 1);

INSERT INTO DB.ODCLEANSTORE.TRANSFORMERS_TO_PIPELINES_ASSIGNMENT (transformerId, pipelineId, workDirPath, configuration, priority)
VALUES (1, 2, n'.', n'Config', 1);

INSERT INTO DB.ODCLEANSTORE.TRANSFORMERS_TO_PIPELINES_ASSIGNMENT (transformerId, pipelineId, workDirPath, configuration, priority)
VALUES (2, 2, n'.', n'Config', 1);

