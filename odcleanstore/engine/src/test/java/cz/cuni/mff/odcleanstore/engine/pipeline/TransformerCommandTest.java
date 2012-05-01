package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

public class TransformerCommandTest {

	@Before
	public void setUp() throws Exception {
	}

	// @Test
	public void test() throws Exception {
		 Collection<TransformerCommand> tc = TransformerCommand.getActualPlan("DB.FRONTEND");
		 tc.clear();
	}

}
