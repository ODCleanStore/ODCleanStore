package cz.cuni.mff.odcleanstore.webfrontend.bo.en;

import java.sql.Timestamp;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

/**
 * 
 * @author Jakub Daniel
 *
 */
public class InputGraph extends EntityWithSurrogateKey {
	private static final long serialVersionUID = 1L;

	public String UUID;
	public Integer stateId;
	public Boolean isInCleanDB;
	public Integer engineId;
	public Integer pipelineId;
	public Timestamp updated;
	
	public String engineUUID;
	public String pipelineLabel;
	public String stateLabel;
	
	public InputGraph (Integer id, String UUID, Integer stateId, Boolean isInCleanDB, Integer engineId, Integer pipelineId,
			Timestamp updated, String engineUUID, String pipelineLabel, String stateLabel) {
		
		super(id);

		this.UUID = UUID;
		this.stateId = stateId;
		this.isInCleanDB = isInCleanDB;
		this.engineId = engineId;
		this.pipelineId = pipelineId;
		this.updated = updated;
		
		this.engineUUID = engineUUID;
		this.pipelineLabel = pipelineLabel;
		this.stateLabel = stateLabel;
	}
}
