package cz.cuni.mff.odcleanstore.webfrontend.core;

import java.io.Serializable;
import java.util.HashMap;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

import virtuoso.jdbc3.VirtuosoDataSource;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.cr.GlobalAggregationSettingsDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.EngineOperationsDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.OfficialPipelinesDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerInstanceDao;

/**
 * A factory to lookup DAO objects.
 *  
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class DaoLookupFactory implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private static final String CONNECTION_ENCODING = "UTF-8";
	
	//private static Logger logger = Logger.getLogger(DaoLookupFactory.class);
	
	/** connection credentials to the clean Virtuoso DB */
	private JDBCConnectionCredentials cleanConnectionCoords;
	
	/** data source to the clean Virtuoso DB */
	private transient VirtuosoDataSource cleanDataSource;
	
	/** connection credentials to the dirty Virtuoso DB */
	private JDBCConnectionCredentials dirtyConnectionCoords;
	
	/** data source to the dirty Virtuoso DB */
	private transient VirtuosoDataSource dirtyDataSource;
	
	/** Spring transaction manager */
	private transient AbstractPlatformTransactionManager transactionManager;
	
	private HashMap<Class<? extends Dao>, Dao> daos;
	
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
		
		this.daos = new HashMap<Class<? extends Dao>, Dao>();
	}
	
	public <T extends Dao> T getDao(Class<T> daoClass) throws AssertionError
	{
		if (daos.containsKey(daoClass))
			return (T) daos.get(daoClass);
		
		T daoInstance = createDaoInstance(daoClass);
		daos.put(daoClass, daoInstance);
	
		return daoInstance;
	}
	
	/**
	 * Creates and returns a DAO instance related to the given class.
	 * 
	 * Throws AssertionError if the requested DAO class cannot be instantiated.
	 *  
	 * @param daoClass
	 * @return
	 * @throws AssertionError
	 */
	private <T extends Dao> T createDaoInstance(Class<T> daoClass) throws AssertionError
	{
		T daoInstance;
		
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
<<<<<<< HEAD
	 * Creates and returns the (undecorated) official pipelines DAO.
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
	 * Creates and returns the (undecorated) engine operations DAO.
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
	 * Returns the data source for the clean Virtuoso DB.
	 * 
	 * The data source is lazily created (based on the connection credentials)
	 * on every request, which allows the factory to be stored in the session
	 * by the Wicket framework.
	 * 
=======
	 * Only for DAO classes.
>>>>>>> d44256eac5da0455d22d22858b15faefc601b077
	 * @return
	 */
	public  VirtuosoDataSource getCleanDataSource()
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
<<<<<<< HEAD
	 * Returns the data source for the dirty Virtuoso DB.
	 * 
	 * The data source is lazily created (based on the connection credentials)
	 * on every request, which allows the factory to be stored in the session
	 * by the Wicket framework.
	 * 
=======
	 * Only for DAO classes.
>>>>>>> d44256eac5da0455d22d22858b15faefc601b077
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

	private String makeVirtuosoDataSourceConnectionString(String jdbcConnectionString) 
	{
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
<<<<<<< HEAD
	 * Returns the (lazily created on every request) transaction manager over
	 * the clean data source.
	 *  
=======
	 * Only for DAO classes.
>>>>>>> d44256eac5da0455d22d22858b15faefc601b077
	 * @return
	 */
	public AbstractPlatformTransactionManager getCleanTransactionManager()
	{
		if (transactionManager == null)
			transactionManager = new DataSourceTransactionManager(getCleanDataSource());
		
		return transactionManager;
	}
}
