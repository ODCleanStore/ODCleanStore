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
	
	private File transformerDirectory;
	
	TransformationContextTestImpl(File transformerDirectory) {
		this.transformerDirectory = transformerDirectory;
	}
	
	@Override
	public SparqlEndpoint getDirtyDatabaseEndpoint() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SparqlEndpoint getCleanDatabaseEndpoint() {
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
		return transformerDirectory;
	}

	@Override
	public EnumTransformationType getTransformationType() {
		// TODO Auto-generated method stub
		return null;
	}

}
