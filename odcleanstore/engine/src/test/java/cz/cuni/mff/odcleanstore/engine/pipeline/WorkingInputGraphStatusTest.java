package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.util.Collection;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import cz.cuni.mff.odcleanstore.engine.InputGraphState;
import cz.cuni.mff.odcleanstore.engine.common.SimpleVirtuosoAccess;
import cz.cuni.mff.odcleanstore.engine.pipeline.WorkingInputGraphStatus.NotWorkingTransformerException;

public class WorkingInputGraphStatusTest {

	private static String _dbSchemaPrefix = "DB.TEST";
	private WorkingInputGraphStatus _wigs;

	@Before
	public void setUp() throws Exception {
		_wigs = new WorkingInputGraphStatus(_dbSchemaPrefix);
	}

	// @Test
	public void test() throws Exception {
		init();

		String uuid = UUID.randomUUID().toString();
		insertGraphUuid(uuid, InputGraphState.PROCESSING);
		String uuid2 = _wigs.getWorkingTransformedGraphUuid();
		Assert.assertEquals(uuid, uuid2);

		InputGraphState inputGraphState = _wigs.getState(uuid2);
		Assert.assertEquals(InputGraphState.PROCESSING, inputGraphState);

		_wigs.setState(uuid2, InputGraphState.WRONG);
		inputGraphState = _wigs.getState(uuid2);
		Assert.assertEquals(InputGraphState.WRONG, inputGraphState);

		Collection<String> wag = _wigs.getWorkingAttachedGraphNames();
		Assert.assertEquals(0, wag.size());

		TransformedGraphImpl tg = new TransformedGraphImpl(_wigs, uuid2);
		_wigs.setWorkingTransformedGraph(tg);
		_wigs.addAttachedGraphName(tg, "Prd23");
		wag = _wigs.getWorkingAttachedGraphNames();
		Assert.assertEquals(1, wag.size());
		for (String ag : wag) {
			Assert.assertEquals("Prd23", ag);
		}

		_wigs.addAttachedGraphName(tg, "Prd24");
		wag = _wigs.getWorkingAttachedGraphNames();
		Assert.assertEquals(2, wag.size());

		_wigs.deleteGraph(tg);
		Assert.assertEquals(InputGraphState.DELETING, _wigs.getState(uuid2));

		_wigs.deleteGraphAndWorkingAttachedGraphNames(uuid2);
		uuid2 = _wigs.getWorkingTransformedGraphUuid();
		Assert.assertEquals(null, uuid2);
		wag = _wigs.getWorkingAttachedGraphNames();
		Assert.assertEquals(0, wag.size());

		init();
		uuid = UUID.randomUUID().toString();
		insertGraphUuid(uuid, InputGraphState.IMPORTING);

		uuid2 = _wigs.getNextProcessingGraphUuid();
		Assert.assertEquals(null, uuid2);

		uuid = UUID.randomUUID().toString();
		insertGraphUuid(uuid, InputGraphState.PROCESSING);
		uuid2 = _wigs.getNextProcessingGraphUuid();
		Assert.assertEquals(uuid, uuid2);

		_wigs.deleteGraphAndWorkingAttachedGraphNames(uuid2);
		insertGraphUuid(uuid, InputGraphState.IMPORTED);
		uuid2 = _wigs.getNextProcessingGraphUuid();
		Assert.assertEquals(uuid, uuid2);
		inputGraphState = _wigs.getState(uuid2);
		Assert.assertEquals(InputGraphState.PROCESSING, inputGraphState);

		// test NotWorkingTransformerException

		_wigs.deleteGraph(tg);
		_wigs.addAttachedGraphName(tg, "Prd25");

		TransformedGraphImpl tg2 = new TransformedGraphImpl(_wigs, uuid2);
		_wigs.setWorkingTransformedGraph(tg2);

		try {
			_wigs.deleteGraph(tg);
		} catch (NotWorkingTransformerException e) {
		}

		try {
			_wigs.addAttachedGraphName(tg, "Prd25");
		} catch (NotWorkingTransformerException e) {
		}

		_wigs.setWorkingTransformedGraph(null);

		try {
			_wigs.deleteGraph(tg);
		} catch (NotWorkingTransformerException e) {
		}

		try {
			_wigs.addAttachedGraphName(tg, "Prd25");
		} catch (NotWorkingTransformerException e) {
		}
	}

	private void insertGraphUuid(String uuid, InputGraphState inputGraphState) throws Exception {
		SimpleVirtuosoAccess sva = null;
		try {
			sva = SimpleVirtuosoAccess.createCleanDBConnection();
			String sqlStatement = String.format("Insert into %s.EN_INPUT_GRAPHS(uuid, state) VALUES('%s', '%s')", _dbSchemaPrefix, uuid, inputGraphState.toString());
			sva.executeStatement(sqlStatement);
			sva.commit();
		} finally {
			if (sva != null) {
				sva.close();
			}
		}
	}

	private void init() throws Exception {
		SimpleVirtuosoAccess sva = null;
		String sqlStatement = null;
		try {
			sva = SimpleVirtuosoAccess.createCleanDBConnection();
			sqlStatement = String.format("DROP TABLE  %s.EN_INPUT_GRAPHS", _dbSchemaPrefix);
			try {
				sva.executeStatement(sqlStatement);
			} catch (Exception e) {
			}
			sqlStatement = String.format("DROP TABLE  %s.EN_WORKING_ADDED_GRAPHS", _dbSchemaPrefix);
			try {
				sva.executeStatement(sqlStatement);
			} catch (Exception e) {
			}
			sqlStatement = String.format("CREATE TABLE %s.EN_INPUT_GRAPHS (uuid VARCHAR(48) PRIMARY KEY,state VARCHAR(16) NOT NULL)", _dbSchemaPrefix);
			sva.executeStatement(sqlStatement);
			sqlStatement = String.format("CREATE TABLE  %s.EN_WORKING_ADDED_GRAPHS (name VARCHAR PRIMARY KEY)", _dbSchemaPrefix);
			sva.executeStatement(sqlStatement);
			sva.commit();
		} finally {
			if (sva != null) {
				sva.close();
			}
		}
	}
}