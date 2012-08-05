package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.util.ArrayList;
import java.util.UUID;

import org.junit.Before;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;

/**
 *  @author Petr Jerman
 */
public class WorkingInputGraphTest {

	private TransformedGraphManipulation _wig;

	@Before
	public void setUp() throws Exception {
		_wig = new TransformedGraphManipulation();
	}

	// @Test
	public void test() throws Exception {
		String graphName = ConfigLoader.getConfig().getBackendGroup().getDataGraphURIPrefix() + "g/" + UUID.randomUUID().toString();
		try {
			insertGraph(graphName);
			ArrayList<String> al = new ArrayList<String>();
			al.add(graphName);
			_wig.copyGraphsFromDirtyDBToCleanDB(al);

		} finally {
			_wig.deleteGraphFromCleanDB(graphName);
			_wig.deleteGraphFromDirtyDB(graphName);
		}
	}

	private void insertGraph(String graphName) throws Exception {
		VirtuosoConnectionWrapper con = null;
		try {
			con = VirtuosoConnectionWrapper.createTransactionalLevelConnection(ConfigLoader.getConfig().getBackendGroup().getDirtyDBJDBCConnectionCredentials());
			for (int i = 0; i < 10; i++) {
				String ruiid = UUID.randomUUID().toString();
				String dataPrefix =  ConfigLoader.getConfig().getBackendGroup().getDataGraphURIPrefix().toString();
				con.insertQuad("<" + dataPrefix + "s/" + ruiid+ ">", "<" + dataPrefix + "p/" + ruiid + ">", "<" + dataPrefix + "o/" + ruiid + ">", "<" + graphName  + ">");
			}
			con.commit();

		} finally {
			if (con != null) {
				con.close();
			}
		}
	}
}
