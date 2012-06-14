package cz.cuni.mff.odcleanstore.linker.impl;

import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.transformer.EnumTransformationType;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;

import java.io.File;

public class TransformationContextMock implements TransformationContext {

	private String directoryPath;

	public TransformationContextMock(String directoryPath) {
		this.directoryPath = directoryPath;
	}

	@Override
	public JDBCConnectionCredentials getDirtyDatabaseCredentials() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JDBCConnectionCredentials getCleanDatabaseCredentials() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTransformerConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getTransformerDirectory() {
		File directory = new File(directoryPath);
		if (!directory.exists()) {
			directory.mkdir();
		}
		return directory;
	}

	@Override
	public EnumTransformationType getTransformationType() {
		// TODO Auto-generated method stub
		return null;
	}

}
