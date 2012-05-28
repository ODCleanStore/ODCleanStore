Delete from DB.ODCLEANSTORE.REGISTERED_TRANSFORMERS;

Insert into DB.ODCLEANSTORE.REGISTERED_TRANSFORMERS(label, jarPath, fullClassName, workDirPath, configuration, active, priority) VALUES('QA', '.', 'cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAssessorImpl', 'transformers-working-dir/qa', '', 1, 1);
Insert into DB.ODCLEANSTORE.REGISTERED_TRANSFORMERS(label, jarPath, fullClassName, workDirPath, configuration, active, priority) VALUES('Linker', '.', 'cz.cuni.mff.odcleanstore.linker.impl.LinkerImpl', 'transformers-working-dir/linker', '1', 1, 2);
