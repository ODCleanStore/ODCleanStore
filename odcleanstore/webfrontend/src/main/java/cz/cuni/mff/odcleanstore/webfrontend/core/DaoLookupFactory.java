package cz.cuni.mff.odcleanstore.webfrontend.core;

import java.io.Serializable;
import java.util.HashMap;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

import virtuoso.jdbc3.VirtuosoDataSource;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.webfrontend.bo.BusinessEntity;
import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.SafetyDaoDecorator;
import cz.cuni.mff.odcleanstore.webfrontend.dao.SafetyDaoDecoratorForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.EngineOperationsDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.OfficialPipelinesDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerInstanceDao;

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
	
	//private static Logger logger = Logger.getLogger(DaoLookupFactory.class);
	
	private JDBCConnectionCredentials cleanConnectionCoords;
	private transient VirtuosoDataSource cleanDataSource;
	
	private JDBCConnectionCredentials dirtyConnectionCoords;
	private transient VirtuosoDataSource dirtyDataSource;
	
	private transient AbstractPlatformTransactionManager transactionManager;
	
	private HashMap<Class<? extends Dao<? extends BusinessEntity>>, Dao<? extends BusinessEntity>> daos;
	
	//private GlobalAggregationSettingsDao globalAggregationSettingsDao;
	private TransformerInstanceDao transformerInstanceDao;
	
	/**
	 * 
	 * @param cleanConnectionCoords
	 * @param dirtyConnectionCoords
	 */
	public DaoLookupFactory(
		JDBCConnectionCredentials cleanConnectionCoords,
		JDBCConnectionCredentials dirtyConnectionCoords)
	{
		this.cleanConnectionCoords = cleanConnectionCoords;
		this.dirtyConnectionCoords = dirtyConnectionCoords;
		
		this.daos = new HashMap<Class<? extends Dao<? extends BusinessEntity>>, Dao<? extends BusinessEntity>>();
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
	@SuppressWarnings("unchecked")
	public <T extends BusinessEntity> Dao<T> getDao(Class<? extends Dao<T>> daoClass) throws AssertionError
	{
		if (daos.containsKey(daoClass))
			return (Dao<T>) daos.get(daoClass);
		
		Dao<T> daoInstance = createDaoInstance(daoClass);
		Dao<T> safeDaoInstance = new SafetyDaoDecorator<T>(daoInstance);
		
		daos.put(daoClass, safeDaoInstance);
		
		return safeDaoInstance;
	}
	
	/**
	 * 
	 * @param daoClass
	 * @return
	 * @throws AssertionError
	 */
	@SuppressWarnings("unchecked")
	public <T extends EntityWithSurrogateKey> DaoForEntityWithSurrogateKey<T> getDaoForEntityWithSurrogateKey(Class<? extends Dao<T>> daoClass) 
		throws AssertionError
	{
		if (daos.containsKey(daoClass))
			return (DaoForEntityWithSurrogateKey<T>) daos.get(daoClass);
		
		DaoForEntityWithSurrogateKey<T> daoInstance = (DaoForEntityWithSurrogateKey<T>) createDaoInstance(daoClass);
		DaoForEntityWithSurrogateKey<T> safeDaoInstance = new SafetyDaoDecoratorForEntityWithSurrogateKey<T>(daoInstance);
		
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
	public <T extends BusinessEntity> Dao<T> getUnsafeDao(Class<? extends Dao<T>> daoClass) throws AssertionError
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
	private <T extends BusinessEntity> Dao<T> createDaoInstance(Class<? extends Dao<T>> daoClass) throws AssertionError
	{
		Dao<T> daoInstance;
		
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
	public VirtuosoDataSource getCleanDataSource()
	{
		if (cleanDataSource == null)
		{
			cleanDataSource = new VirtuosoDataSource();
			cleanDataSource.setServerName(makeVirtuosoDataSourceConnectionString(cleanConnectionCoords.getConnectionString()));
			cleanDataSource.setUser(cleanConnectionCoords.getUsername());
			cleanDataSource.setPassword(cleanConnectionCoords.getPassword());
			cleanDataSource.setCharset(CONNECTION_ENCODING);
		}
		
		return cleanDataSource;
	}
	
	/**
	 * 
	 * @return
	 */
	public VirtuosoDataSource getDirtyDataSource()
	{
		if (dirtyDataSource == null)
		{
			dirtyDataSource = new VirtuosoDataSource();
			dirtyDataSource.setServerName(makeVirtuosoDataSourceConnectionString(dirtyConnectionCoords.getConnectionString()));
			dirtyDataSource.setUser(dirtyConnectionCoords.getUsername());
			dirtyDataSource.setPassword(dirtyConnectionCoords.getPassword());
			dirtyDataSource.setCharset(CONNECTION_ENCODING);
		}
		
		return dirtyDataSource;
	}

	private String makeVirtuosoDataSourceConnectionString(String jdbcConnectionString) {
		final String connectionPrefix = "jdbc:virtuoso://";
		String result = jdbcConnectionString;
		if (result.startsWith(connectionPrefix))
		{
			result = result.substring(connectionPrefix.length());
		}
		int paramsIndex = result.indexOf('/');
		if (paramsIndex >= 0)
		{
			result = result.substring(0, paramsIndex);
		}
		return result;
	}
	
	/**
	 * 
	 * @return
	 */
	public AbstractPlatformTransactionManager getTransactionManager()
	{
		if (transactionManager == null)
			transactionManager = new DataSourceTransactionManager(getCleanDataSource());
		
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
