package cz.cuni.mff.odcleanstore.webfrontend.core;

import java.io.Serializable;
import java.util.HashMap;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

import virtuoso.jdbc3.VirtuosoDataSource;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

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
	 * Creates and returns a DAO instance related to the given class.
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
	 * Only for DAO classes.
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
	 * Only for DAO classes.
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
