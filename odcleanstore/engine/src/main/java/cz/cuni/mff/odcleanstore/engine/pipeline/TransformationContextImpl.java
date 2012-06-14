package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.io.File;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.transformer.EnumTransformationType;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;

/**
 *  @author Petr Jerman
 */
public class TransformationContextImpl implements TransformationContext {

	private String _configuration;
	private String _path;

	TransformationContextImpl(String configuration, String path) {
		_configuration = configuration;
		_path = path;
	}

	@Override
	public JDBCConnectionCredentials getDirtyDatabaseCredentials() {
		return ConfigLoader.getConfig().getInputWSGroup().getDirtyDBJDBCConnectionCredentials();
	}

	@Override
	public JDBCConnectionCredentials getCleanDatabaseCredentials() {
		return ConfigLoader.getConfig().getInputWSGroup().getCleanDBJDBCConnectionCredentials();
	}

	@Override
	public String getTransformerConfiguration() {
		return _configuration;
	}

	@Override
	public File getTransformerDirectory() {
		return new File(_path);
	}

	@Override
	public EnumTransformationType getTransformationType() {
		return EnumTransformationType.NEW;
	}
}
