package cz.cuni.mff.odcleanstore.webfrontend.dao;

import java.util.LinkedList;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import cz.cuni.mff.odcleanstore.util.CodeSnippet;
import cz.cuni.mff.odcleanstore.util.EmptyCodeSnippet;
import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

public class SafetyDaoDecoratorForEntityWithSurrogateKey<T extends EntityWithSurrogateKey> 
	extends DaoForEntityWithSurrogateKey<T>
{
	private static final long serialVersionUID = 1L;
	
	//private static Logger logger = Logger.getLogger(SafetyDaoDecoratorForEntityWithSurrogateKey.class);
	
	private DaoForEntityWithSurrogateKey<T> dao;
	private List<DaoExceptionHandler> exceptionHandlers;
	
	public SafetyDaoDecoratorForEntityWithSurrogateKey(DaoForEntityWithSurrogateKey<T> dao)
	{
		this.dao = dao;
		
		exceptionHandlers = new LinkedList<DaoExceptionHandler>();
		exceptionHandlers.add(new UniquenessViolationHandler());
		exceptionHandlers.add(new NonUniquePrimaryKeyHandler());
	}
	

	@Override
	public String getTableName() 
	{
		return dao.getTableName();
	}

	@Override
	protected ParameterizedRowMapper<T> getRowMapper() 
	{
		return dao.getRowMapper();
	}

	@Override
	public List<T> loadAll() 
	{
		return dao.loadAll();
	}
	
	@Override
	public List<T> loadAllBy(QueryCriteria criteria)
	{
		return dao.loadAllBy(criteria);
	}
	
	@Override
	public List<T> loadAllRaw()
	{
		return dao.loadAllRaw();
	}
	
	@Override
	public T loadFirstRaw()
	{
		return dao.loadFirstRaw();
	}
	
	@Override
	public List<T> loadAllRawBy(String columnName, Object value)
	{
		return dao.loadAllRawBy(columnName, value);
	}
	
	@Override 
	public T loadRawBy(String columnName, Object value)
	{
		return dao.loadRawBy(columnName, value);
	}
	
	@Override
	public T loadRaw(Integer id)
	{
		return dao.loadRaw(id);
	}
	
	@Override
	public T load(Integer id)
	{
		return dao.load(id);
	}
	
	@Override
	public T loadBy(String columnName, Object value)
	{
		return dao.loadBy(columnName, value);
	}
	
	@Override
	public void save(final T item, final CodeSnippet doAfter) throws Exception
	{
		try
		{
			dao.getTransactionTemplate().execute(new TransactionCallbackWithoutResult() 
			{
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) 
				{
					try {
						dao.save(item);
						doAfter.execute();
					}
					catch (Exception ex) {
						throw new RuntimeException(ex.getMessage(), ex);
					}
				}
			});
		}
		catch (Exception ex)
		{
			handleException(ex);
			throw ex;
		}	
	}
	
	@Override
	public void save(T item) throws Exception 
	{
		this.save(item, new EmptyCodeSnippet());
	}
	
	@Override
	public int saveAndGetKey(final T item) throws Exception
	{
		
		final SimpleKeyHolder keyHolder = new SimpleKeyHolder();
		try
		{
			dao.getTransactionTemplate().execute(new TransactionCallbackWithoutResult() 
			{
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) 
				{
					try {
						dao.save(item);
						int insertId = dao.getLastInsertId();
						keyHolder.setKey(insertId);
					}
					catch (Exception ex) {
						throw new RuntimeException(ex.getMessage(), ex);
					}
				}
			});
		}
		catch (Exception ex)
		{
			handleException(ex);
			throw ex;
		}
		return keyHolder.getKey();
	}

	@Override
	public void update(final T item, final CodeSnippet doAfter) throws Exception
	{
		try
		{
			dao.getTransactionTemplate().execute(new TransactionCallbackWithoutResult() 
			{
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) 
				{
					try {
						dao.update(item);
						doAfter.execute();
					}
					catch (Exception ex) {
						throw new RuntimeException(ex.getMessage(), ex);
					}
				}
			});
		}
		catch (Exception ex)
		{
			handleException(ex);
			throw ex;
		}	
	}
	
	@Override
	public void update(final T item) throws Exception
	{
		this.update(item, new EmptyCodeSnippet());
	}
	
	@Override
	public void deleteRaw(Integer id) throws Exception
	{
		// note that there is no need to surround the deleteRaw operation by 
		// a transaction, as every delete is realized using a single
		// SQL DELETE command - deleting related entities is ensured using
		// CASCADING DELETE constraints
		//
		dao.deleteRaw(id);
	}
	
	@Override
	public void delete(final Integer id) throws Exception
	{
		try
		{
			dao.getTransactionTemplate().execute(new TransactionCallbackWithoutResult() 
			{
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) 
				{
					try {
						dao.delete(id);
					}
					catch (Exception ex) {
						throw new RuntimeException(ex.getMessage(), ex);
					}
				}
			});
		}
		catch (Exception ex)
		{
			handleException(ex);
			throw ex;
		}
	}
	
	@Override
	public void delete(final T item) throws Exception
	{
		try
		{
			dao.getTransactionTemplate().execute(new TransactionCallbackWithoutResult() 
			{
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) 
				{
					try {
						dao.delete(item);
					}
					catch (Exception ex) {
						throw new RuntimeException(ex.getMessage(), ex);
					}
				}
			});
		}
		catch (Exception ex)
		{
			handleException(ex);
			throw ex;
		}
	}
	
	private void handleException(Exception ex) throws Exception
	{
		handleMessagingException(ex);
		handleDAOException(ex);
		
		// if neither of the routines above handled the exception (e.g. they
		// did not throw any exceptions), re-throw the exception unchanged
		throw ex;
	}
	
	/**
	 * 
	 * @param ex
	 * @throws Exception
	 */
	private void handleMessagingException(Exception ex) throws Exception
	{
		if (ex instanceof RuntimeException)
		{
			Throwable innerEx = ex.getCause();
			if (innerEx != null && (innerEx instanceof MessagingException))
			{
				throw (MessagingException) innerEx;
			}
		}
	}
	
	/**
	 * 
	 * @param ex
	 * @throws Exception
	 */
	private void handleDAOException(Exception ex) throws Exception
	{
		String relevantMessagePart = getRelevantMessagePart(ex.getMessage());
		if (relevantMessagePart == null)
			throw ex;
		
		for (DaoExceptionHandler handler : exceptionHandlers)
		{
			if (!handler.comprisesException(relevantMessagePart))
				continue;
			
			handler.handleException(relevantMessagePart);
			return;
		}
	}
	
	private String getRelevantMessagePart(String originalMessage)
	{
		String[] tokens = originalMessage.split("; ");
		
		for (String token : tokens)
		{
			if (token.startsWith("SR"))
				return token;
		}
		
		return null;
	}
	
	// TODO: only temporary
	public DaoForEntityWithSurrogateKey<T> getWrappedDao() 
	{
		return dao;
	}
}
