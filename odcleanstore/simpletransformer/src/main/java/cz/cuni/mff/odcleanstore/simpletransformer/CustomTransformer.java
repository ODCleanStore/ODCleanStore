package cz.cuni.mff.odcleanstore.simpletransformer;

import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.transformer.Transformer;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;

/**
 * Simple Testing custom Tranformer
 * 
 * @author Bramburek
 */
public class CustomTransformer implements Transformer {

	@Override
	public void transformNewGraph(TransformedGraph inputGraph,
			TransformationContext context) throws TransformerException {
	}

	@Override
	public void transformExistingGraph(TransformedGraph inputGraph,
			TransformationContext context) throws TransformerException {
	}

	@Override
	public void shutdown() throws TransformerException {
	}
}
