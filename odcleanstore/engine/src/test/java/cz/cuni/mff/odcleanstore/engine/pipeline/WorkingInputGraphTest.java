package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.util.ArrayList;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import cz.cuni.mff.odcleanstore.engine.Engine;
import cz.cuni.mff.odcleanstore.engine.common.SimpleVirtuosoAccess;

/**
 *  @author Petr Jerman
 */
public class WorkingInputGraphTest {

	private WorkingInputGraph _wig;

	@Before
	public void setUp() throws Exception {
		_wig = new WorkingInputGraph();
	}

	// @Test
	public void test() throws Exception {
		SimpleVirtuosoAccess sva = null;
		String graphName = Engine.DATA_PREFIX + "g/" + UUID.randomUUID().toString();
		try {
			sva = SimpleVirtuosoAccess.createDirtyDBConnection();
			insertGraph(graphName);
			ArrayList<String> al = new ArrayList<String>();
			al.add(graphName);
			_wig.copyGraphsFromDirtyDBToCleanDB(al);

		} finally {
			_wig.deleteGraphFromCleanDB(graphName);
			_wig.deleteGraphFromDirtyDB(graphName);
			sva.close();
		}
	}

	private void insertGraph(String graphName) throws Exception {
		SimpleVirtuosoAccess sva = null;
		try {
			sva = SimpleVirtuosoAccess.createDirtyDBConnection();
			for (int i = 0; i < 10; i++) {
				String ruiid = UUID.randomUUID().toString();
				sva.insertQuad("<" + Engine.DATA_PREFIX + "s/" + ruiid + ">", "<" + Engine.DATA_PREFIX + "p/" + ruiid + ">", "<" + Engine.DATA_PREFIX + "o/" + ruiid + ">", "<" + graphName  + ">");
			}
			sva.commit();

		} finally {
			if (sva != null) {
				sva.close();
			}
		}
	}
}
