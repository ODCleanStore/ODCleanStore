package cz.cuni.mff.odcleanstore.linker.impl;

import java.io.File;

import cz.cuni.mff.odcleanstore.data.ConnectionCredentials;
import cz.cuni.mff.odcleanstore.transformer.EnumTransformationType;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;

public class TransformationContextMock implements TransformationContext {
	
	private String directoryPath;
	
	public TransformationContextMock(String directoryPath) {
		this.directoryPath = directoryPath;
	}
	
	@Override
	public ConnectionCredentials getDirtyDatabaseCredentials() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConnectionCredentials getCleanDatabaseCredentials() {
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
