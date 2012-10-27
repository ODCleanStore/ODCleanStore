package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.transformer.EnumTransformationType;

/**
 *  @author Petr Jerman
 */
public class TransformationContext implements cz.cuni.mff.odcleanstore.transformer.TransformationContext {
    
    private static final Logger LOG = LoggerFactory.getLogger(TransformationContext.class);
    
    public static final String ERROR_NOT_ACTIVE_TRANSFORMER =
            "Operation is permitted only for active transformation context in active pipeline";

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
        if (!this.active) {
            LOG.error(ERROR_NOT_ACTIVE_TRANSFORMER);
            throw new TransformationContextRuntimeException(ERROR_NOT_ACTIVE_TRANSFORMER);
        }
        return ConfigLoader.getConfig().getEngineGroup().getDirtyDBJDBCConnectionCredentials();
    }

    @Override
    public JDBCConnectionCredentials getCleanDatabaseCredentials() {
        if (!this.active) {
            LOG.error(ERROR_NOT_ACTIVE_TRANSFORMER);
            throw new TransformationContextRuntimeException(ERROR_NOT_ACTIVE_TRANSFORMER);
        }
        return ConfigLoader.getConfig().getEngineGroup().getCleanDBJDBCConnectionCredentials();
    }

    @Override
    public String getTransformerConfiguration() {
        String configuration = this.configuration;
        if (!this.active) {
            LOG.error(ERROR_NOT_ACTIVE_TRANSFORMER);
            throw new TransformationContextRuntimeException(ERROR_NOT_ACTIVE_TRANSFORMER);
        }
        return configuration;
    }

    @Override
    public File getTransformerDirectory() {
        String path = this.path;
        if (!this.active) {
            LOG.error(ERROR_NOT_ACTIVE_TRANSFORMER);
            throw new TransformationContextRuntimeException(ERROR_NOT_ACTIVE_TRANSFORMER);
        }
        return new File(path);
    }

    @Override
    public EnumTransformationType getTransformationType() {
        EnumTransformationType type = this.type;
        if (!this.active) {
            LOG.error(ERROR_NOT_ACTIVE_TRANSFORMER);
            throw new TransformationContextRuntimeException(ERROR_NOT_ACTIVE_TRANSFORMER);
        }
        return type;
    }
}
