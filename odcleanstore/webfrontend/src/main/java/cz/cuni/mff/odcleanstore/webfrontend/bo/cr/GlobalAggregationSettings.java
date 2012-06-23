package cz.cuni.mff.odcleanstore.webfrontend.bo.cr;

import cz.cuni.mff.odcleanstore.webfrontend.bo.BusinessEntity;

public class GlobalAggregationSettings extends BusinessEntity
{
	private static final long serialVersionUID = 1L;
	
	private ErrorStrategy defaultErrorStrategy;
	private MultivalueType defaultMultivalueType;
	private AggregationType defaultAggregationType;
	
	public GlobalAggregationSettings(ErrorStrategy defaultErrorStrategy,
		MultivalueType defaultMultivalueType, AggregationType defaultAggregationType) 
	{
		this.defaultErrorStrategy = defaultErrorStrategy;
		this.defaultMultivalueType = defaultMultivalueType;
		this.defaultAggregationType = defaultAggregationType;
	}
	
	public GlobalAggregationSettings()
	{
	}

	public ErrorStrategy getDefaultErrorStrategy() 
	{
		return defaultErrorStrategy;
	}

	public MultivalueType getDefaultMultivalueType() 
	{
		return defaultMultivalueType;
	}

	public AggregationType getDefaultAggregationType() 
	{
		return defaultAggregationType;
	}
}
