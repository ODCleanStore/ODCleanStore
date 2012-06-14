package cz.cuni.mff.odcleanstore.test;

import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.transformer.EnumTransformationType;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;

import java.io.File;

/**
 *
 * Testovaci implementace
 *
 * @author Tomas Soukup
 *
 */

public class TransformationContextTestImpl implements TransformationContext {

	private File directory;
	private String config;
	private JDBCConnectionCredentials endpoint;

	TransformationContextTestImpl(File directory, String config, JDBCConnectionCredentials endpoint) {
		this.directory = directory;
		this.config = config;
		this.endpoint = endpoint;
	}

	@Override
	public JDBCConnectionCredentials getDirtyDatabaseCredentials() {
		return endpoint;
	}

	@Override
	public JDBCConnectionCredentials getCleanDatabaseCredentials() {
		return endpoint;
	}

	@Override
	public String getTransformerConfiguration() {
		return config;
	}

	@Override
	public File getTransformerDirectory() {
		return directory;
	}

	@Override
	public EnumTransformationType getTransformationType() {
		// TODO Auto-generated method stub
		return null;
	}

}
