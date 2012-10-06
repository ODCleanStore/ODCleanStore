ALTER TABLE DB.ODCLEANSTORE.EN_INPUT_GRAPHS_STATES ADD COLUMN canResetPipeline SMALLINT NOT NULL DEFAULT 0;
ALTER TABLE DB.ODCLEANSTORE.EN_INPUT_GRAPHS ADD COLUMN resetPipelineRequest SMALLINT NOT NULL DEFAULT 0;

UPDATE DB.ODCLEANSTORE.EN_INPUT_GRAPHS_STATES SET canResetPipeline = 0  WHERE id = 1;
UPDATE DB.ODCLEANSTORE.EN_INPUT_GRAPHS_STATES SET canResetPipeline = 0  WHERE id = 2;
UPDATE DB.ODCLEANSTORE.EN_INPUT_GRAPHS_STATES SET canResetPipeline = 1  WHERE id = 3;
UPDATE DB.ODCLEANSTORE.EN_INPUT_GRAPHS_STATES SET canResetPipeline = 0  WHERE id = 4;
UPDATE DB.ODCLEANSTORE.EN_INPUT_GRAPHS_STATES SET canResetPipeline = 1  WHERE id = 5;
UPDATE DB.ODCLEANSTORE.EN_INPUT_GRAPHS_STATES SET canResetPipeline = 1  WHERE id = 6;
UPDATE DB.ODCLEANSTORE.EN_INPUT_GRAPHS_STATES SET canResetPipeline = 0  WHERE id = 7;
UPDATE DB.ODCLEANSTORE.EN_INPUT_GRAPHS_STATES SET canResetPipeline = 0  WHERE id = 8;
UPDATE DB.ODCLEANSTORE.EN_INPUT_GRAPHS_STATES SET canResetPipeline = 0  WHERE id = 9;
UPDATE DB.ODCLEANSTORE.EN_INPUT_GRAPHS_STATES SET canResetPipeline = 0  WHERE id = 10;
UPDATE DB.ODCLEANSTORE.EN_INPUT_GRAPHS_STATES SET canResetPipeline = 0  WHERE id = 11;
UPDATE DB.ODCLEANSTORE.EN_INPUT_GRAPHS_STATES SET canResetPipeline = 0  WHERE id = 12;

INSERT INTO DB.ODCLEANSTORE.EN_INPUT_GRAPHS_STATES (id, label, canResetPipeline) VALUES (13, n'FINISHEDINDIRTY', 1);
INSERT INTO DB.ODCLEANSTORE.EN_INPUT_GRAPHS_STATES (id, label, canResetPipeline) VALUES (14, n'REMOVEDOLDGRAPH', 1);


