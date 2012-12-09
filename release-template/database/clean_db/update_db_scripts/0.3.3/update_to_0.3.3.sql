INSERT INTO DB.ODCLEANSTORE.TRANSFORMERS (label, description, jarPath, workDirPath, fullClassName) VALUES (n'Blank node remover', n'ODCS transformer for replacing blank nodes by new URI resources', n'.', n'transformers-working-dir/bnode-remover', n'cz.cuni.mff.odcleanstore.transformer.odcs.ODCSBNodeToResourceTransformer');

UPDATE DB.ODCLEANSTORE.ROLES SET label = n'PIC', description = n'Pipeline creator' WHERE label = n'POC';

/* Test data */
UPDATE DB.ODCLEANSTORE.USERS SET username = n'pic', email = n'pic@odcleanstore.cz', passwordHash = n'f4371aa7c2147df3c3e17a738e9af5f7', surname = n'Pipeline Creator' WHERE username = n'poc';

ALTER TABLE DB.ODCLEANSTORE.PIPELINES ADD COLUMN isLocked SMALLINT NOT NULL DEFAULT 0;

