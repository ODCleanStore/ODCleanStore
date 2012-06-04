DROP TABLE DB.ODCLEANSTORE.TRANSFORMERS_TO_PIPELINES_ASSIGNMENT;
DROP TABLE DB.ODCLEANSTORE.TRANSFORMERS;
DROP TABLE DB.ODCLEANSTORE.PIPELINES;

CREATE TABLE DB.ODCLEANSTORE.TRANSFORMERS
(
	id INTEGER NOT NULL IDENTITY PRIMARY KEY,
	label NVARCHAR(255) UNIQUE NOT NULL,
	description LONG NVARCHAR,
	jarPath NVARCHAR(255) NOT NULL,
	fullClassName NVARCHAR(255) NOT NULL
);

INSERT INTO DB.ODCLEANSTORE.TRANSFORMERS (label, description, jarPath, fullClassName) 
VALUES (n'QA', n'Standard quality assessment transformer', n'.', n'cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAssessorImpl');

INSERT INTO DB.ODCLEANSTORE.TRANSFORMERS (label, description, jarPath, fullClassName) 
VALUES (n'Linker', n'Standard object identification transformer', n'.', n'cz.cuni.mff.odcleanstore.linker.impl.LinkerImpl');
