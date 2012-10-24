package cz.cuni.mff.odcleanstore.webfrontend.core;

import java.io.Serializable;
import java.util.HashMap;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

import virtuoso.jdbc3.VirtuosoDataSource;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.webfrontend.dao.CommittableDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

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
	private final JDBCConnectionCredentials cleanConnectionCredentials;

	/** data source to the clean Virtuoso DB */
	private transient VirtuosoDataSource cleanDataSource;
	
	/** connection credentials to the dirty Virtuoso DB */
	private final JDBCConnectionCredentials dirtyConnectionCredentials;
	
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
		this.cleanConnectionCredentials = cleanConnectionCoords;
		this.dirtyConnectionCredentials = dirtyConnectionCoords;
		
		this.daos = new HashMap<Class<? extends Dao>, Dao>();
	}
	
	public JDBCConnectionCredentials getCleanConnectionCredentials()
	{
		return cleanConnectionCredentials;
	}

	public JDBCConnectionCredentials getDirtyConnectionCredentials()
	{
		return dirtyConnectionCredentials;
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
	public <T extends Dao> T getDao(Class<T> daoClass) throws AssertionError
	{
		if (daos.containsKey(daoClass))
			return (T) daos.get(daoClass);
		
		T daoInstance = createDaoInstance(daoClass);
		daos.put(daoClass, daoInstance);
	
		return daoInstance;
	}
	
	/**
	 * Creates requested DAO object with the option to look for "uncommitted" version of the DAO
	 * @see #getDao(Class)
	 * @param daoClass
	 * @param commitable if true, the uncommitted version of the DAO will be returned
	 * @return
	 * @throws AssertionError
	 */
	@SuppressWarnings("unchecked")
	public <T extends Dao> T getDao(Class<T> daoClass, boolean commitable) throws AssertionError
	{
		Class<T> requestedClass = daoClass;
		if (commitable)
		{
			CommittableDao annotation = daoClass.getAnnotation(CommittableDao.class);
			if (annotation == null) 
			{
				throw new AssertionError("Could not load committable version of DAO class: " + daoClass);
			}
			if (!daoClass.isAssignableFrom(annotation.value()))
			{
				throw new AssertionError("Committable version of DAO must inhterit from the requested class " + daoClass);
			}
			requestedClass = (Class<T>) annotation.value();
			
		}
		return getDao(requestedClass);
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
	 * Returns the data source for the clean Virtuoso DB.
	 * 
	 * The data source is lazily created (based on the connection credentials)
	 * on every request, which allows the factory to be stored in the session
	 * by the Wicket framework.
	 * 
	 * Only for DAO classes.
	 * @return
	 */
	public  VirtuosoDataSource getCleanDataSource()
	{
		if (cleanDataSource == null)
		{
			cleanDataSource = new VirtuosoDataSource();
			cleanDataSource.setServerName(makeVirtuosoDataSourceConnectionString(cleanConnectionCredentials.getConnectionString()));
			cleanDataSource.setUser(cleanConnectionCredentials.getUsername());
			cleanDataSource.setPassword(cleanConnectionCredentials.getPassword());
			cleanDataSource.setCharset(CONNECTION_ENCODING);
		}
		
		return cleanDataSource;
	}
	
	/**
	 * Returns the data source for the dirty Virtuoso DB.
	 * 
	 * The data source is lazily created (based on the connection credentials)
	 * on every request, which allows the factory to be stored in the session
	 * by the Wicket framework.
	 * 
	 * Only for DAO classes.
	 * @return
	 */
	public VirtuosoDataSource getDirtyDataSource()
	{
		if (dirtyDataSource == null)
		{
			dirtyDataSource = new VirtuosoDataSource();
			dirtyDataSource.setServerName(makeVirtuosoDataSourceConnectionString(dirtyConnectionCredentials.getConnectionString()));
			dirtyDataSource.setUser(dirtyConnectionCredentials.getUsername());
			dirtyDataSource.setPassword(dirtyConnectionCredentials.getPassword());
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
	 * Returns the (lazily created on every request) transaction manager over
	 * the clean data source.
	 *  
	 * Only for DAO classes.
	 * @return
	 */
	public AbstractPlatformTransactionManager getCleanTransactionManager()
	{
		if (transactionManager == null)
			transactionManager = new DataSourceTransactionManager(getCleanDataSource());
		
		return transactionManager;
	}
}
