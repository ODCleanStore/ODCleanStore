package cz.odcleanstore.ws.scraper.client;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;

public class Message implements Serializable
{
	private LinkedList<Triple> data;
	
	private String source;
	private String publisher;
	private Date scrapedAt;
	
	public Message(String source, String publisher, Date scrapedAt)
	{
		this.source = source;
		this.publisher = publisher;
		this.scrapedAt = scrapedAt;
		
		data = new LinkedList<Triple>();
	}
	
	public void addTriple(Triple triple)
	{
		data.add(triple);
	}
	
	public void saveToFile(File file) throws IOException
	{
		// TODO: implement standard java serialization process here
		
	}
	
	public static Message loadFromFile(File file) throws IOException
	{
		// TODO: implement standard java deserialization process here
		return null;
	}
}
