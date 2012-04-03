package cz.cuni.mff.odcleanstore.test;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * Interface of a custom transformer.
 *
 * @todo just a prototype
 * @author Jan Michelfeit
 *
 */
public interface Transformer {
    // Na vystupu muze byt bud upraveny inputGraph.getModel() nebo uplne novy Model
    public Model transform(TransformedGraph inputGraph, TransformationContext context);
}
