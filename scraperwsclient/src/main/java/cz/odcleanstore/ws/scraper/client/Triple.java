package cz.odcleanstore.ws.scraper.client;

import java.io.Serializable;

public class Triple implements Serializable
{
	private TripleItem subject;
	private TripleItem predicate;
	private TripleItem object;
	
	public Triple(TripleItem subject, TripleItem predicate, TripleItem object)
	{
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}
}
