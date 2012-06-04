DELETE FROM DB.ODCLEANSTORE.TRANSFORMERS;

INSERT INTO DB.ODCLEANSTORE.TRANSFORMERS (label, description, jarPath, fullClassName) 
VALUES (n'QA', n'The standard quality assessment transformer', n'.', n'cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAssessorImpl');

INSERT INTO DB.ODCLEANSTORE.TRANSFORMERS (label, description, jarPath, fullClassName) 
VALUES (n'Linker', n'The standard object identification transformer',  n'.', n'cz.cuni.mff.odcleanstore.linker.impl.LinkerImpl');
