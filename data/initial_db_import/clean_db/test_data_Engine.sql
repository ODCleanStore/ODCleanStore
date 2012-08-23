set_identity_column('DB.ODCLEANSTORE.TRANSFORMERS','id', 1);
set_identity_column('DB.ODCLEANSTORE.PIPELINES','id', 1);

--
-- fill backup tables
--

INSERT INTO DB.ODCLEANSTORE.BACKUP_PIPELINES (label, description, isDefault)
VALUES (n'Dirty', n'A basic dirty pipeline', 0);

INSERT INTO DB.ODCLEANSTORE.BACKUP_PIPELINES (label, description, isDefault)
VALUES (n'Clean', n'A basic clean pipeline', 1);


INSERT INTO DB.ODCLEANSTORE.BACKUP_TRANSFORMER_INSTANCES (transformerId, pipelineId, workDirPath, configuration, runOnCleanDB, priority) VALUES (
	(SELECT id FROM DB.ODCLEANSTORE.BACKUP_TRANSFORMERS WHERE label = 'Quality Assessment'),
	(SELECT id FROM DB.ODCLEANSTORE.BACKUP_PIPELINES WHERE label = 'Dirty'),
	n'transformers-working-dir/qa',
	n'', 
	1,
	1);

INSERT INTO DB.ODCLEANSTORE.BACKUP_TRANSFORMER_INSTANCES (transformerId, pipelineId, workDirPath, configuration, runOnCleanDB, priority) VALUES (
	(SELECT id FROM DB.ODCLEANSTORE.BACKUP_TRANSFORMERS WHERE label = 'Linker'),
	(SELECT id FROM DB.ODCLEANSTORE.BACKUP_PIPELINES WHERE label = 'Dirty'),
	n'transformers-working-dir/link', 
	n'1',
	1, 
	2);

INSERT INTO DB.ODCLEANSTORE.BACKUP_TRANSFORMER_INSTANCES (transformerId, pipelineId, workDirPath, configuration, runOnCleanDB, priority) VALUES (
	(SELECT id FROM DB.ODCLEANSTORE.BACKUP_TRANSFORMERS WHERE label = 'Data Normalization'),
	(SELECT id FROM DB.ODCLEANSTORE.BACKUP_PIPELINES WHERE label = 'Dirty'),
	n'transformers-working-dir/dn', 
	n'',
	0, 
	3);

INSERT INTO DB.ODCLEANSTORE.BACKUP_TRANSFORMER_INSTANCES (transformerId, pipelineId, workDirPath, configuration, runOnCleanDB, priority) VALUES (
	(SELECT id FROM DB.ODCLEANSTORE.BACKUP_TRANSFORMERS WHERE label = 'Quality Assessment'),
	(SELECT id FROM DB.ODCLEANSTORE.BACKUP_PIPELINES WHERE label = 'Clean'),
	n'transformers-working-dir/qa',
	n'',
	1, 
	1);

INSERT INTO DB.ODCLEANSTORE.BACKUP_TRANSFORMER_INSTANCES (transformerId, pipelineId, workDirPath, configuration, runOnCleanDB, priority) VALUES (
	(SELECT id FROM DB.ODCLEANSTORE.BACKUP_TRANSFORMERS WHERE label = 'Linker'),
	(SELECT id FROM DB.ODCLEANSTORE.BACKUP_PIPELINES WHERE label = 'Clean'),
	n'transformers-working-dir/link', 
	n'',
	1, 
	1);

--
-- fill official tables
--

INSERT INTO DB.ODCLEANSTORE.PIPELINES (label, description, isDefault)
VALUES (n'Dirty', n'A basic dirty pipeline', 0);

INSERT INTO DB.ODCLEANSTORE.PIPELINES (label, description, isDefault)
VALUES (n'Clean', n'A basic clean pipeline', 1);

INSERT INTO DB.ODCLEANSTORE.TRANSFORMER_INSTANCES (transformerId, pipelineId, workDirPath, configuration, runOnCleanDB, priority) VALUES (
	(SELECT id FROM DB.ODCLEANSTORE.TRANSFORMERS WHERE label = 'Quality Assessment'),
	(SELECT id FROM DB.ODCLEANSTORE.BACKUP_PIPELINES WHERE label = 'Dirty'),
	n'transformers-working-dir/qa',
	n'', 
	1,
	1);

INSERT INTO DB.ODCLEANSTORE.TRANSFORMER_INSTANCES (transformerId, pipelineId, workDirPath, configuration, runOnCleanDB, priority) VALUES (
	(SELECT id FROM DB.ODCLEANSTORE.TRANSFORMERS WHERE label = 'Linker'),
	(SELECT id FROM DB.ODCLEANSTORE.PIPELINES WHERE label = 'Dirty'),
	n'transformers-working-dir/link', 
	n'1',
	1, 
	2);

INSERT INTO DB.ODCLEANSTORE.TRANSFORMER_INSTANCES (transformerId, pipelineId, workDirPath, configuration, runOnCleanDB, priority) VALUES (
	(SELECT id FROM DB.ODCLEANSTORE.TRANSFORMERS WHERE label = 'Data Normalization'),
	(SELECT id FROM DB.ODCLEANSTORE.PIPELINES WHERE label = 'Dirty'),
	n'transformers-working-dir/dn', 
	n'',
	0, 
	3);

INSERT INTO DB.ODCLEANSTORE.TRANSFORMER_INSTANCES (transformerId, pipelineId, workDirPath, configuration, runOnCleanDB, priority) VALUES (
	(SELECT id FROM DB.ODCLEANSTORE.TRANSFORMERS WHERE label = 'Quality Assessment'),
	(SELECT id FROM DB.ODCLEANSTORE.PIPELINES WHERE label = 'Clean'),
	n'transformers-working-dir/qa',
	n'',
	1, 
	1);

INSERT INTO DB.ODCLEANSTORE.TRANSFORMER_INSTANCES (transformerId, pipelineId, workDirPath, configuration, runOnCleanDB, priority) VALUES (
	(SELECT id FROM DB.ODCLEANSTORE.TRANSFORMERS WHERE label = 'Linker'),
	(SELECT id FROM DB.ODCLEANSTORE.PIPELINES WHERE label = 'Clean'),
	n'transformers-working-dir/link', 
	n'',
	1, 
	1);

--
-- attached graphs
--

INSERT INTO DB.ODCLEANSTORE.EN_ATTACHED_ENGINES (uuid) VALUES ('88888888-8888-8888-8888-888888888888');
