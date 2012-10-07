package cz.cuni.mff.odcleanstore.webfrontend.bo.en;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

public class GraphInError extends EntityWithSurrogateKey {
	private static final long serialVersionUID = 1L;

	public Integer engineId;
	public Integer pipelineId;
	public String UUID;
	public Integer stateId;
	public Integer errorTypeId;
	public String errorMessage;
	public Boolean isInCleanDB;
	
	public String engineUUID;
	public String pipelineLabel;
	public String stateLabel;
	public String errorTypeLabel;

	public GraphInError (
			Integer id, Integer engineId, Integer pipelineId, String UUID, Integer stateId, Integer errorTypeId, String errorMessage, Boolean isInCleanDB,
			String engineUUID, String pipelineLabel, String stateLabel, String errorTypeLabel) {

		this.id = id;
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
