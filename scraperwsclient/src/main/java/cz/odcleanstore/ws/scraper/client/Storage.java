package cz.odcleanstore.ws.scraper.client;

import cz.odcleanstore.ws.scraper.exceptions.StorageUnreachableException;

import java.util.LinkedList;

public class Storage 
{
	// NOTE:
	// storage url-address stands here in place of parameters needed to connect 
	// to the scraper WS (some of them might be fixed - the intention here is to 
	// allow this class to work with any server with our storage software installed 
	// without any code modifications)
	private String urlAddress;
	
	public Storage(String urlAddress)
	{
		this.urlAddress = urlAddress;
	}

	public void insertData(Message message)
		throws IllegalArgumentException, StorageUnreachableException
	{
		// empty method stub
	}
	
	public LinkedList<Ontology> getOntologies()
		throws StorageUnreachableException
	{
		// empty method stub
		return null;
	}
	
	// intentionally package-scoped
	LinkedList<Triple> getOntologyContent(String ontologyId)
		throws StorageUnreachableException, IllegalArgumentException
	{
		// empty method stub
		return null;
	}
}
