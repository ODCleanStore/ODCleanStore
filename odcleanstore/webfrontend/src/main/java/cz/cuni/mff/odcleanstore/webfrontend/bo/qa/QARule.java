package cz.cuni.mff.odcleanstore.webfrontend.bo.qa;

import java.util.HashSet;
import java.util.Set;

import cz.cuni.mff.odcleanstore.webfrontend.bo.BusinessObject;

public class QARule extends BusinessObject
{
	private static final long serialVersionUID = 1L;

	private String filter;
	private String description;
	private Double coefficient;
	
	private Set<Publisher> publisherRestrictions;
	
	public QARule(Long id, String filter, String description, Double coefficient) 
	{
		this();
		
		this.id = id;
		this.filter = filter;
		this.description = description;
		this.coefficient = coefficient;
	}
	
	public QARule()
	{
		publisherRestrictions = new HashSet<Publisher>();
	}

	public String getFilter() 
	{
		return filter;
	}

	public String getDescription() 
	{
		return description;
	}

	public Double getCoefficient() 
	{
		return coefficient;
	}
	
	public Set<Publisher> getPublisherRestrictions()
	{
		return publisherRestrictions;
	}
	
	public void setPublisherRestrictions(Set<Publisher> restrictions)
	{
		this.publisherRestrictions = restrictions;
	}
	
	public void addPublisherRestriction(Publisher publisher)
	{
		this.publisherRestrictions.add(publisher);
	}
}
