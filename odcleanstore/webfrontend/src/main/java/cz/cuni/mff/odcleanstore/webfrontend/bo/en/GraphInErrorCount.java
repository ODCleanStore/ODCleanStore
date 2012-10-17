package cz.cuni.mff.odcleanstore.webfrontend.bo.en;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

/**
 * 
 * @author Jakub Daniel
 *
 */
public class GraphInErrorCount extends EntityWithSurrogateKey {

	private static final long serialVersionUID = 1L;
	
	public int pipelineId;
	public String pipelineLabel;
	public int graphCount;
	
	public GraphInErrorCount(int pipelineId, String pipelineLabel, int graphCount) {
		this.pipelineId = pipelineId;
		this.pipelineLabel = pipelineLabel;
		this.graphCount = graphCount;
	}
}
