package cz.cuni.mff.odcleanstore.webfrontend.dao;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import cz.cuni.mff.odcleanstore.webfrontend.bo.BusinessObject;

public class SafetyDaoDecorator<T extends BusinessObject> extends Dao<T>
{
	private static Logger logger = Logger.getLogger(SafetyDaoDecorator.class);
	
	private Dao<T> dao;
	private List<DaoExceptionHandler> exceptionHandlers;
	
	public SafetyDaoDecorator(Dao<T> dao)
	{
		this.dao = dao;
		
		exceptionHandlers = new LinkedList<DaoExceptionHandler>();
		exceptionHandlers.add(new UniquenessViolationHandler());
		exceptionHandlers.add(new NonUniquePrimaryKeyHandler());
	}
	

	@Override
	protected String getTableName() 
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
	public List<T> loadAllRaw()
	{
		return dao.loadAllRaw();
	}

	@Override
	public T load(Long id) 
	{
		return dao.load(id);
	}
	
	@Override
	public T loadRaw(Long id)
	{
		return dao.loadRaw(id);
	}
	
	@Override
	public void delete(T item)
	{
		dao.delete(item);
	}
	
	@Override
	public void deleteRaw(Long id)
	{
		dao.deleteRaw(id);
	}
	
	@Override
	public void save(T item) throws Exception 
	{
		try {
			dao.save(item);
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
		try
		{
			dao.transactionTemplate.execute(new TransactionCallbackWithoutResult() 
			{
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) 
				{
					try {
						dao.update(item);
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
		
		// if no handler accepted the exception, throw it unchanged
		throw ex;
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
}
