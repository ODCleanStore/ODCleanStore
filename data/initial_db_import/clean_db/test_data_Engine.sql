INSERT INTO DB.ODCLEANSTORE.PIPELINES (label, description, isDefault)
VALUES (n'example-pipeline', n'An example pipeline', 1);

INSERT INTO DB.ODCLEANSTORE.TRANSFORMER_INSTANCES (transformerId, pipelineId, configuration, runOnCleanDB, priority) VALUES (
	(SELECT TOP 1 id FROM DB.ODCLEANSTORE.TRANSFORMERS WHERE fullClassName = n'cz.cuni.mff.odcleanstore.datanormalization.impl.DataNormalizerImpl'),
	(SELECT id FROM DB.ODCLEANSTORE.PIPELINES WHERE label = 'example-pipeline'),
	n'',
	0, 
	1);

INSERT INTO DB.ODCLEANSTORE.TRANSFORMER_INSTANCES (transformerId, pipelineId, configuration, runOnCleanDB, priority) VALUES (
	(SELECT TOP 1 id FROM DB.ODCLEANSTORE.TRANSFORMERS WHERE fullClassName = n'cz.cuni.mff.odcleanstore.linker.impl.LinkerImpl'),
	(SELECT id FROM DB.ODCLEANSTORE.PIPELINES WHERE label = 'example-pipeline'),
	n'linkWithinGraph=true',
	1, 
	2);
	
INSERT INTO DB.ODCLEANSTORE.TRANSFORMER_INSTANCES (transformerId, pipelineId, configuration, runOnCleanDB, priority) VALUES (
	(SELECT TOP 1 id FROM DB.ODCLEANSTORE.TRANSFORMERS WHERE fullClassName = n'cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAssessorImpl'),
	(SELECT id FROM DB.ODCLEANSTORE.PIPELINES WHERE label = 'example-pipeline'),
	n'', 
	1,
	3);

INSERT INTO DB.ODCLEANSTORE.TRANSFORMER_INSTANCES (transformerId, pipelineId, configuration, runOnCleanDB, priority) VALUES (
	(SELECT TOP 1 id FROM DB.ODCLEANSTORE.TRANSFORMERS WHERE fullClassName = n'cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAggregatorImpl'),
	(SELECT id FROM DB.ODCLEANSTORE.PIPELINES WHERE label = 'example-pipeline'),
	n'', 
	1,
	4);
