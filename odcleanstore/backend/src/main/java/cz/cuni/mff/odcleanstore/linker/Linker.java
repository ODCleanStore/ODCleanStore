package cz.cuni.mff.odcleanstore.linker;

import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;

/**
 * Linking component.
 * Provides mechanisms for interlinking open data. 
 * Uses user-defined linkage-rules to generate the links.
 * Typically generates owl:sameAs links. Different link types can be also specified.
 * 
 * @author Tomas Soukup
 */
public interface Linker {
	/**
	 * Generates links between data two datasources specified by given SPARQL endpoints.
	 * Stores them using target endpoint. Uses The Silk Link Discovery Framework.
	 * Performes following steps:
	 * <ul><li>Loads valid linkage rules from database.</li>
	 * <li>Creates configuration XML file in Silk-LSL.</li>
	 * <li>Calls Silk engine providing the created configuration</li></ul>
	 * 
	 * @param firstSource SPARQL endpoint to the first datasource to interlink
	 * @param secondSource SPARQL endpoint to the first datasource to interlink
	 * @param target generated links are stored into datasource specified by this SPARQL endpoint 
	 */
	public void generateLinks(SparqlEndpoint firstSource, 
			SparqlEndpoint secondSource, SparqlEndpoint target);
}
