package cz.cuni.mff.odcleanstore.test;

import java.io.File;

import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;
import cz.cuni.mff.odcleanstore.transformer.EnumTransformationType;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;

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
	private SparqlEndpoint endpoint;
	
	TransformationContextTestImpl(File directory, String config, SparqlEndpoint endpoint) {
		this.directory = directory;
		this.config = config;
		this.endpoint = endpoint;
	}
	
	@Override
	public SparqlEndpoint getDirtyDatabaseEndpoint() {
		return endpoint;
	}

	@Override
	public SparqlEndpoint getCleanDatabaseEndpoint() {
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
