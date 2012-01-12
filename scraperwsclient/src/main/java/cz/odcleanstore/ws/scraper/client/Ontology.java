package cz.odcleanstore.ws.scraper.client;

import java.util.Date;
import java.util.List;
import java.util.LinkedList;

import cz.odcleanstore.ws.scraper.exceptions.StorageUnreachableException;

public class Ontology 
{
	private String name;
	private String id;
	private Date lastModified;
	
	private LinkedList<Triple> content;
	
	public Ontology(String name, String id, Date lastModified)
	{
		this.name = name;
		this.id = id;
		this.lastModified = lastModified;
		
		this.content = null;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getId()
	{
		return id;
	}
	
	public Date getLastModified()
	{
		return lastModified;
	}
	
	public LinkedList<Triple> getContent(Storage storage)
		throws StorageUnreachableException
	{
		// lazy-load the content
		if (content == null)
			content = storage.getOntologyContent(name);
		
		return content;
	}
}
