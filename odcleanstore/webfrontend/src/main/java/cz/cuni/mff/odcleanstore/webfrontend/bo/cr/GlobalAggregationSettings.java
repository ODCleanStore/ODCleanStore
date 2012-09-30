package cz.cuni.mff.odcleanstore.webfrontend.bo.cr;

import cz.cuni.mff.odcleanstore.webfrontend.bo.BusinessEntity;

/**
 * The BO to rerepesent the default CR aggregation settings.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class GlobalAggregationSettings extends BusinessEntity
{
	private static final long serialVersionUID = 1L;
	
	/** the default CR error strategy */
	private ErrorStrategy defaultErrorStrategy;
	
	/** the default CR multivalue policy */
	private MultivalueType defaultMultivalueType;
	
	/** the defualt CR aggregation policy */
	private AggregationType defaultAggregationType;
	
	/**
	 * 
	 * @param defaultErrorStrategy
	 * @param defaultMultivalueType
	 * @param defaultAggregationType
	 */
	public GlobalAggregationSettings(ErrorStrategy defaultErrorStrategy,
		MultivalueType defaultMultivalueType, AggregationType defaultAggregationType) 
	{
		this.defaultErrorStrategy = defaultErrorStrategy;
		this.defaultMultivalueType = defaultMultivalueType;
		this.defaultAggregationType = defaultAggregationType;
	}
	
	/**
	 * 
	 */
	public GlobalAggregationSettings()
	{
	}

	/**
	 * 
	 * @return
	 */
	public ErrorStrategy getDefaultErrorStrategy() 
	{
		return defaultErrorStrategy;
	}

	/**
	 * 
	 * @return
	 */
	public MultivalueType getDefaultMultivalueType() 
	{
		return defaultMultivalueType;
	}

	/**
	 * 
	 * @return
	 */
	public AggregationType getDefaultAggregationType() 
	{
		return defaultAggregationType;
	}
}
