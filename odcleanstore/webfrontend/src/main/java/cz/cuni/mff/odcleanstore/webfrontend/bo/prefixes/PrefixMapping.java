package cz.cuni.mff.odcleanstore.webfrontend.bo.prefixes;

import cz.cuni.mff.odcleanstore.webfrontend.bo.BusinessObject;

public class PrefixMapping extends BusinessObject
{
	private static final long serialVersionUID = 1L;

	private String prefix;
	private String url;
	
	public PrefixMapping(String prefix, String url) 
	{
		this.prefix = prefix;
		this.url = url;
	}

	public PrefixMapping() 
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
