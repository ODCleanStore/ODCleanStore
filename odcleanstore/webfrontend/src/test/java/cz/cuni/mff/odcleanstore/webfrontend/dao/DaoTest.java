package cz.cuni.mff.odcleanstore.webfrontend.dao;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.junit.Test;

public class DaoTest {

	@Test
	public void testDateToTimestampTransformation() 
	{
		Dao dao = new TestDao();
		
		Date date = new Date(1331179506000L);
		assertEquals("2012-03-08 05:05:06", dao.dateToMySQLTimestamp(date));
	}

}

class TestDao extends Dao
{
	@Override
	public void insert(Object item) 
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void update(Object item) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public List loadAll() 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object load(int id) 
	{
		// TODO Auto-generated method stub
		return null;
	}
}
