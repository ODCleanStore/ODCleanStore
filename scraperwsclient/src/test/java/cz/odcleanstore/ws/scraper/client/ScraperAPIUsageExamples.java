package cz.odcleanstore.ws.scraper.client;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import cz.odcleanstore.ws.scraper.exceptions.StorageUnreachableException;

// NOTE: 
// This is just a set of examples of how to use the proposed API. It is
// not intended to be run.
//
public class ScraperAPIUsageExamples 
{
	private static final String STORAGE_URL = "insert storage URL here";
	
	private void findOntologyDefinition(String ontologyName)
	{
		Storage storage = new Storage(STORAGE_URL);
		
		try 
		{
			LinkedList<Ontology> ontologies = storage.getOntologies();
			for (Ontology ontology : ontologies)
			{
				if (!ontology.getName().equals(ontologyName))
					continue;
				
				// here it is
				LinkedList<Triple> content = ontology.getContent(storage);
				return;
			}
			
			throw new IllegalArgumentException();
		} 
		catch (StorageUnreachableException e) 
		{
			System.out.println("Could not connect to storage server.");
		}
		catch (IllegalArgumentException e)
		{
			System.out.println("No such ontology on storage server.");
		}
	}
	
	private void checkIfNewerVersionOfOntologyExists(String ontologyName, 
		Date oldLastModified)
	{
		Storage storage = new Storage(STORAGE_URL);
		
		try 
		{
			LinkedList<Ontology> ontologies = storage.getOntologies();
			for (Ontology ontology : ontologies)
			{
				if (!ontology.getName().equals(ontologyName))
					continue;
				
				// here it is
				boolean newerVersionExists = 
					oldLastModified.before(ontology.getLastModified());
				
				return;
			}
			
			throw new IllegalArgumentException();
		} 
		catch (StorageUnreachableException e) 
		{
			System.out.println("Could not connect to storage server.");
		}
		catch (IllegalArgumentException e)
		{
			System.out.println("No such ontology on storage server.");
		}
	}
	
	private void insertScrapedData()
	{
		String source = "insert the source here";
		String publisher = "insert the publisher here";
		Date scrapedAt = Calendar.getInstance().getTime();
		
		Message message = new Message(source, publisher, scrapedAt);
		
		TripleItem subject = new URITripleItem("insert the URI here");
		TripleItem predicate = new URITripleItem("insert the predicate here");
		TripleItem object = new LabelTripleItem("xsd:string", "insert the object here");
		
		Triple triple = new Triple(subject, predicate, object);
		
		// insert some more triples here ...
		
		Storage storage = new Storage(STORAGE_URL);

		try 
		{
			storage.insertData(message);
		} 
		catch (StorageUnreachableException e) 
		{
			System.out.println("Could not connect to storage server.");
		}
		catch (IllegalArgumentException e)
		{
			System.out.println("Invalid message.");
		}
	}
	
	// NOTE: this is to be used when the storage gets inaccessible
	private void saveMessageToFileToBeSentLater()
	{
		String source = "insert the source here";
		String publisher = "insert the publisher here";
		Date scrapedAt = Calendar.getInstance().getTime();
		
		Message message = new Message(source, publisher, scrapedAt);
		
		TripleItem subject = new URITripleItem("insert the URI here");
		TripleItem predicate = new URITripleItem("insert the predicate here");
		TripleItem object = new LabelTripleItem("xsd:string", "insert the object here");
		
		Triple triple = new Triple(subject, predicate, object);
		
		// insert some more triples here ...
		
		File outputFile = new File("insert the file-path here");
		
		try {
			message.saveToFile(outputFile);
		} 
		catch (IOException e) 
		{
			System.out.println("An IO-Error occured.");
		}
	}
	
	// NOTE: this is to be used on previously saved messages (see the previous method)
	private void loadMessageFromFile()
	{
		File inputFile = new File("insert the file-path here");
		
		try {
			// here it is
			Message message = Message.loadFromFile(inputFile);
		} 
		catch (IOException e) 
		{
			System.out.println("An IO-Error occured.");
		}
	}
}
