package cz.cuni.mff.odcleanstore.webfrontend.bo.en;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

public class InputGraph extends EntityWithSurrogateKey {
	private static final long serialVersionUID = 1L;

	public Integer id;
	public String UUID;
	public Integer stateId;
	public Boolean isInCleanDB;
	public Integer engineId;
	public Integer pipelineId;
	
	public String engineUUID;
	public String pipelineLabel;
	public String stateLabel;
	
	public InputGraph (Integer id, String UUID, Integer stateId, Boolean isInCleanDB, Integer engineId, Integer pipelineId,
			String engineUUID, String pipelineLabel, String stateLabel) {
		
		this.id = id;
		this.UUID = UUID;
		this.stateId = stateId;
		this.isInCleanDB = isInCleanDB;
		this.engineId = engineId;
		this.pipelineId = pipelineId;
		
		this.engineUUID = engineUUID;
		this.pipelineLabel = pipelineLabel;
		this.stateLabel = stateLabel;
	}
}
