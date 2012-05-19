package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.io.File;

import cz.cuni.mff.odcleanstore.data.ConnectionCredentials;
import cz.cuni.mff.odcleanstore.engine.Engine;
import cz.cuni.mff.odcleanstore.transformer.EnumTransformationType;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;

public class TransformationContextImpl implements TransformationContext {

	private String _configuration;
	private String _path;

	TransformationContextImpl(String configuration, String path) {
		_configuration = configuration;
		_path = path;
	}

	@Override
	public ConnectionCredentials getDirtyDatabaseCredentials() {
		return Engine.DIRTY_DATABASE_ENDPOINT;
	}

	@Override
	public ConnectionCredentials getCleanDatabaseCredentials() {
		return Engine.CLEAN_DATABASE_ENDPOINT;
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
