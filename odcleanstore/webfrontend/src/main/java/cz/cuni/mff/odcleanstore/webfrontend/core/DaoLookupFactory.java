package cz.cuni.mff.odcleanstore.webfrontend.core;

import java.io.Serializable;
import java.util.HashMap;


import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.SafetyDaoDecorator;
import cz.cuni.mff.odcleanstore.webfrontend.dao.SafetyDaoDecoratorForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.cr.GlobalAggregationSettingsDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.OfficialPipelinesDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerInstanceDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.EngineOperationsDao;

import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

import virtuoso.jdbc3.VirtuosoDataSource;

/**
 * A factory to lookup DAO Spring beans.
 *  
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class DaoLookupFactory implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private static final String CONNECTION_ENCODING = "UTF-8";
	
	private static Logger logger = Logger.getLogger(DaoLookupFactory.class);
	
	private JDBCConnectionCredentials connectionCoords;
	
	private transient VirtuosoDataSource dataSource;
	private transient AbstractPlatformTransactionManager transactionManager;
	
	private HashMap<Class<? extends Dao>, Dao> daos;
	
	private GlobalAggregationSettingsDao globalAggregationSettingsDao;
	private TransformerInstanceDao transformerInstanceDao;
	
	/**
	 * 
	 */
	public DaoLookupFactory(JDBCConnectionCredentials connectionCoords)
	{
		this.connectionCoords = connectionCoords;
		this.daos = new HashMap<Class<? extends Dao>, Dao>();
	}
	
	/**
	 * Creates (lazily) and returns the requested DAO object decorated by
	 * a SafetyDaoDecorator instance. 
	 * 
	 * Throws an AssertionError if the requested DAO class cannot be 
	 * instantiated.
	 * 
	 * @param daoClass
	 * @return
	 * @throws AssertionError
	 */
	public Dao getDao(Class<? extends Dao> daoClass) throws AssertionError
	{
		if (daos.containsKey(daoClass))
			return daos.get(daoClass);
		
		Dao daoInstance = createDaoInstance(daoClass);
		Dao safeDaoInstance = new SafetyDaoDecorator(daoInstance);
		
		daos.put(daoClass, safeDaoInstance);
		
		return safeDaoInstance;
	}
	
	/**
	 * 
	 * @param daoClass
	 * @return
	 * @throws AssertionError
	 */
	public DaoForEntityWithSurrogateKey getDaoForEntityWithSurrogateKey(Class daoClass) 
		throws AssertionError
	{
		if (daos.containsKey(daoClass))
			return (DaoForEntityWithSurrogateKey) daos.get(daoClass);
		
		DaoForEntityWithSurrogateKey daoInstance = (DaoForEntityWithSurrogateKey) createDaoInstance(daoClass);
		DaoForEntityWithSurrogateKey safeDaoInstance = new SafetyDaoDecoratorForEntityWithSurrogateKey(daoInstance);
		
		daos.put(daoClass, safeDaoInstance);
		
		return safeDaoInstance;
	}
	
	/**
	 * Creates and returns a bew raw (e.g. undecorated) instance of the
	 * requested DAO class.
	 * 
	 * Throws an AssertionError if the requested DAO class cannot be 
	 * instantiated.
	 * 
	 * @param daoClass
	 * @return
	 * @throws AssertionError
	 */
	public Dao getUnsafeDao(Class<? extends Dao> daoClass) throws AssertionError
	{
		return createDaoInstance(daoClass);
	}
	
	/**
	 * Creates and returns a DAO instance related to the given class.
	 *  
	 * @param daoClass
	 * @return
	 * @throws AssertionError
	 */
	private Dao createDaoInstance(Class<? extends Dao> daoClass) throws AssertionError
	{
		Dao daoInstance;
		
		try {
			daoInstance = daoClass.newInstance();
		} 
		catch (Exception ex) 
		{
			throw new AssertionError(
				"Could not load DAO class: " + daoClass
			);
		}

		daoInstance.setDaoLookupFactory(this);
		
		return daoInstance;
	}
	
	/**
	 * 
	 * @return
	 */
	public OfficialPipelinesDao getOfficialPipelinesDao()
	{
		OfficialPipelinesDao dao = new OfficialPipelinesDao();
		dao.setDaoLookupFactory(this);
		return dao;
	}
	
	/**
	 * 
	 * @return
	 */
	public EngineOperationsDao getEngineOperationsDao() 
	{
		EngineOperationsDao dao = new EngineOperationsDao();
		dao.setDaoLookupFactory(this);
		return dao;
	}
	
	/**
	 * 
	 * @return
	 */
	public VirtuosoDataSource getDataSource()
	{
		if (dataSource == null)
		{
			dataSource = new VirtuosoDataSource();
			dataSource.setServerName(connectionCoords.getConnectionString());
			dataSource.setUser(connectionCoords.getUsername());
			dataSource.setPassword(connectionCoords.getPassword());
			dataSource.setCharset(CONNECTION_ENCODING);
		}
		
		return dataSource;
	}
	
	/**
	 * 
	 * @return
	 */
	public AbstractPlatformTransactionManager getTransactionManager()
	{
		if (transactionManager == null)
			transactionManager = new DataSourceTransactionManager(getDataSource());
		
		return transactionManager;
	}

	/**
	 * 
	 * @return
	 */
	public TransformerInstanceDao getTransformerInstanceDao()
	{
		if (transformerInstanceDao == null)
		{
			transformerInstanceDao = new TransformerInstanceDao();
			transformerInstanceDao.setDaoLookupFactory(this);
		}
		
		return transformerInstanceDao;
	}
}
