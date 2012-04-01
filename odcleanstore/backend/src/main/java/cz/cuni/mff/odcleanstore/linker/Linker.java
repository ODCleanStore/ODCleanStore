package cz.cuni.mff.odcleanstore.linker;

import cz.cuni.mff.odcleanstore.shared.SparqlEndpoint;

public interface Linker {
	public void generateLinks(SparqlEndpoint firstSource, 
			SparqlEndpoint secondSource, SparqlEndpoint target);
}
