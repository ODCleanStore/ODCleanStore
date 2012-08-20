package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.io.File;

import org.apache.log4j.Logger;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.transformer.EnumTransformationType;

/**
 *  @author Petr Jerman
 */
public class TransformationContext implements cz.cuni.mff.odcleanstore.transformer.TransformationContext {
	
	private static final Logger LOG = Logger.getLogger(TransformationContext.class);
	
	public static final String ERROR_NOT_ACTIVE_PIPELINE = "Operation is permitted only for transformation context in active pipeline";

	private String configuration;
	private String path;
	private EnumTransformationType type;
	private boolean active;

	TransformationContext(String configuration, String path, EnumTransformationType type) {
		this.configuration = configuration;
		this.path = path;
		this.type = type;
		this.active = true;
	}
	
	void deactivate() {
		this.active = false;
		this.configuration = null;
		this.path = null;
		this.type = null;
	}
	
	@Override
	public JDBCConnectionCredentials getDirtyDatabaseCredentials() {
		if(!this.active) {
			LOG.error(ERROR_NOT_ACTIVE_PIPELINE);
			throw new TransformationContextRuntimeException(ERROR_NOT_ACTIVE_PIPELINE);
		}
		return ConfigLoader.getConfig().getInputWSGroup().getDirtyDBJDBCConnectionCredentials();
	}

	@Override
	public JDBCConnectionCredentials getCleanDatabaseCredentials() {
		if(!this.active) {
			LOG.error(ERROR_NOT_ACTIVE_PIPELINE);
			throw new TransformationContextRuntimeException(ERROR_NOT_ACTIVE_PIPELINE);
		}
		return ConfigLoader.getConfig().getInputWSGroup().getCleanDBJDBCConnectionCredentials();
	}

	@Override
	public String getTransformerConfiguration() {
		String configuration = this.configuration;
		if(!this.active) {
			LOG.error(ERROR_NOT_ACTIVE_PIPELINE);
			throw new TransformationContextRuntimeException(ERROR_NOT_ACTIVE_PIPELINE);
		}
		return configuration;
	}

	@Override
	public File getTransformerDirectory() {
		String path = this.path;
		if(!this.active) {
			LOG.error(ERROR_NOT_ACTIVE_PIPELINE);
			throw new TransformationContextRuntimeException(ERROR_NOT_ACTIVE_PIPELINE);
		}
		return new File(path);
	}

	@Override
	public EnumTransformationType getTransformationType() {
		EnumTransformationType type = this.type;
		if(!this.active) {
			LOG.error(ERROR_NOT_ACTIVE_PIPELINE);
			throw new TransformationContextRuntimeException(ERROR_NOT_ACTIVE_PIPELINE);
		}
		return type;
	}
}
