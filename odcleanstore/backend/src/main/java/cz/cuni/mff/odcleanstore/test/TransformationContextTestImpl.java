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
	private JDBCConnectionCredentials cleanDBconnection;
	private JDBCConnectionCredentials dirtyDBconnection;
	

	TransformationContextTestImpl(File directory, String config, JDBCConnectionCredentials cleanDBconnection) {
		this(directory, config, cleanDBconnection, null);
	}
	
	TransformationContextTestImpl(File directory, String config, JDBCConnectionCredentials cleanDBconnection, 
			JDBCConnectionCredentials dirtyDBconnection) {
		this.directory = directory;
		this.config = config;
		this.cleanDBconnection = cleanDBconnection;
		this.dirtyDBconnection = dirtyDBconnection;
	}

	@Override
	public JDBCConnectionCredentials getDirtyDatabaseCredentials() {
		return dirtyDBconnection;
	}

	@Override
	public JDBCConnectionCredentials getCleanDatabaseCredentials() {
		return cleanDBconnection;
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
		return EnumTransformationType.EXISTING;
	}

}
