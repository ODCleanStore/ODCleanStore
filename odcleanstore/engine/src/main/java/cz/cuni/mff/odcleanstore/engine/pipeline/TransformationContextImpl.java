package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.io.File;
import java.io.InputStream;

import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;
import cz.cuni.mff.odcleanstore.transformer.EnumTransformationType;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;

public class TransformationContextImpl implements  TransformationContext {

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
	public InputStream getTransformerConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File getTransformerDirectory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EnumTransformationType getTransformationType() {
		// TODO Auto-generated method stub
		return null;
	}

}
