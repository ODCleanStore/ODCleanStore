package cz.cuni.mff.odcleanstore.engine.pipeline;


/**
 *  @author Petr Jerman
 */
public class WorkingInputGraphTest {

//	private PipelineGraphManipulator _wig;
//
//	@Before
//	public void setUp() throws Exception {
//		_wig = new PipelineGraphManipulator();
//	}
//
//	// @Test
//	public void test() throws Exception {
//		String graphName = ConfigLoader.getConfig().getBackendGroup().getDataGraphURIPrefix() + "g/" + UUID.randomUUID().toString();
//		try {
//			insertGraph(graphName);
//			ArrayList<String> al = new ArrayList<String>();
//			al.add(graphName);
//			//_wig.copyGraphsFromDirtyDBToCleanDB(al);
//
//		} finally {
//			//_wig.deleteGraphFromCleanDB(graphName);
//			//_wig.deleteGraphFromDirtyDB(graphName);
//		}
//	}
//
//	private void insertGraph(String graphName) throws Exception {
//		VirtuosoJdbc4ConnectionForRdf con = null;
//		try {
//			con = VirtuosoJdbc4ConnectionForRdf.createDirtyDbConnection();
//			for (int i = 0; i < 10; i++) {
//				String ruiid = UUID.randomUUID().toString();
//				String dataPrefix =  ConfigLoader.getConfig().getBackendGroup().getDataGraphURIPrefix().toString();
//				con.insertQuad("<" + dataPrefix + "s/" + ruiid+ ">", "<" + dataPrefix + "p/" + ruiid + ">", "<" + dataPrefix + "o/" + ruiid + ">", "<" + graphName  + ">");
//			}
//			con.commit();
//
//		} finally {
//			if (con != null) {
//				con.close();
//			}
//		}
//	}
}
