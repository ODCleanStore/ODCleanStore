package cz.cuni.mff.odcleanstore.test;

import com.hp.hpl.jena.rdf.model.Model;

import java.util.Collection;

/**
 * Container for all neccessary information about a transofmed graph for a custom transformer.
 *
 * @todo just a prototype
 * @author Jan Michelfeit
 */
public abstract class TransformedGraph {
    public abstract Model getModel();

    public abstract String getGraphName(); // URI jmenneho graf

    public abstract String getGraphId(); // unikatni ID grafu, aby ho v pripade potreby nebylo nutne tahat z URI grafu

    public abstract Model getMetadata(); // provenance (a dalsi) metadata

    public abstract Collection<String> getSources(); // mozna zpristupnit nektera metadata i explicitne?
}
