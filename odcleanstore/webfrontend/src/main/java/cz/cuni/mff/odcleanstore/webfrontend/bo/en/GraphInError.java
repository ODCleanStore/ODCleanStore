package cz.cuni.mff.odcleanstore.webfrontend.bo.en;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

public class GraphInError extends EntityWithSurrogateKey {
	private static final long serialVersionUID = 1L;

	public int engineId;
	public int pipelineId;
	public String UUID;
	public int stateId;
	public int errorTypeId;
	public String errorMessage;
	public boolean isInCleanDB;
	
	public String engineUUID;
	public String pipelineLabel;
	public String stateLabel;
	public String errorTypeLabel;

	public GraphInError (
			int engineId, int pipelineId, String UUID, int stateId, int errorTypeId, String errorMessage, boolean isInCleanDB,
			String engineUUID, String pipelineLabel, String stateLabel, String errorTypeLabel) {

		this.engineId = engineId;
		this.pipelineId = pipelineId;
		this.UUID = UUID;
		this.stateId = stateId;
		this.errorTypeId = errorTypeId;
		this.errorMessage = errorMessage;
		this.isInCleanDB = isInCleanDB;

		this.engineUUID = engineUUID;
		this.pipelineLabel = pipelineLabel;
		this.stateLabel = stateLabel;
		this.errorTypeLabel = errorTypeLabel;
	}
}
