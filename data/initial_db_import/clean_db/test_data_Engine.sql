DELETE FROM DB.ODCLEANSTORE.TRANSFORMERS;

INSERT INTO DB.ODCLEANSTORE.TRANSFORMERS (label, jarPath, fullClassName) 
VALUES (n'QA', n'.', n'cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAssessorImpl');

INSERT INTO DB.ODCLEANSTORE.TRANSFORMERS (label, jarPath, fullClassName) 
VALUES (n'Linker', n'.', n'cz.cuni.mff.odcleanstore.linker.impl.LinkerImpl');
