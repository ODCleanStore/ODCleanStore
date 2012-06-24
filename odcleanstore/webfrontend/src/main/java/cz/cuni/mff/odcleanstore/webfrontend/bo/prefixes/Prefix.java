package cz.cuni.mff.odcleanstore.webfrontend.bo.prefixes;

import cz.cuni.mff.odcleanstore.webfrontend.bo.BusinessEntity;

public class Prefix extends BusinessEntity
{
	private static final long serialVersionUID = 1L;

	private String prefix;
	private String url;
	
	public Prefix(String prefix, String url) 
	{
		this.prefix = prefix;
		this.url = url;
	}

	public Prefix() 
	{
	}

	public String getPrefix() 
	{
		return prefix;
	}

	public String getUrl() 
	{
		return url;
	}
}
