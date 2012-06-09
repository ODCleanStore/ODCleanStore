package cz.cuni.mff.odcleanstore.webfrontend.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import cz.cuni.mff.odcleanstore.webfrontend.bo.BusinessObject;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.NonUniquePrimaryKeyException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.UniquenessViolationException;

public class SafetyDaoDecorator<T extends BusinessObject> extends Dao<T>
{
	private static Logger logger = Logger.getLogger(SafetyDaoDecorator.class);
	
	private Dao<T> dao;
	
	public SafetyDaoDecorator(Dao<T> dao)
	{
		this.dao = dao;
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
		String originalMessage = ex.getMessage();
		
		String relevantMessagePart = getRelevantMessagePart(originalMessage);
		if (relevantMessagePart == null)
			throw ex;
			
		if (relevantMessagePart.startsWith("SR175:")) 
		{
			handleUniquenessViolation(relevantMessagePart);
			return;
		}
		
		if (relevantMessagePart.startsWith("SR197:"))
		{
			handleNonUniquePrimaryKey(relevantMessagePart);
			return;
		}
		
		throw ex;
	}
	
	private String getRelevantMessagePart(String originalMessage)
	{
		String[] tokens = originalMessage.split("; ");
		
		for (String token : tokens)
		{
			logger.debug("token: " + token);
			
			if (token.startsWith("SR")) {
				logger.debug("token accepted");
				return token;
			}
		}
		
		logger.debug("No token accepted.");
		return null;
	}
	
	// TODO: move this to an individual strategy-like class later if needed
	private void handleUniquenessViolation(String message) throws UniquenessViolationException
	{
		assert 
			message.startsWith(
				"SR175: Uniqueness violation : Violating unique index DB_ODCLEANSTORE_"
			);
		
		String[] tokens = message.split(" ");
		String constraintName = tokens[7];
		
		String[] parts = constraintName.split("_");
		String attrName = parts[parts.length - 1];
		
		throw new UniquenessViolationException(attrName);
	}
	
	// TODO: move this to an individual strategy-like class later if needed
	private void handleNonUniquePrimaryKey(String message) throws NonUniquePrimaryKeyException
	{
		assert 
			message.startsWith(
				"SR197: Non unique primary key on DB.ODCLEANSTORE."
			);
		
		throw new NonUniquePrimaryKeyException();
	}
}
