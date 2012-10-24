package cz.cuni.mff.odcleanstore.webfrontend.bo.en;

import java.sql.Timestamp;

import cz.cuni.mff.odcleanstore.data.EnumDatabaseInstance;
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
	public Integer pipelineAuthorId;
	public String stateLabel;
	
	public InputGraph (Integer id, String UUID, Integer stateId, Boolean isInCleanDB, Integer engineId, Integer pipelineId,
			Timestamp updated, String engineUUID, String pipelineLabel, Integer pipelineAuthorId, String stateLabel) {
		
		super(id);

		this.UUID = UUID;
		this.stateId = stateId;
		this.isInCleanDB = isInCleanDB;
		this.engineId = engineId;
		this.pipelineId = pipelineId;
		this.updated = updated;
		
		this.engineUUID = engineUUID;
		this.pipelineLabel = pipelineLabel;
		this.pipelineAuthorId = pipelineAuthorId;
		this.stateLabel = stateLabel;
	}

	public String getUUID()
	{
		return UUID;
	}

	public Integer getStateId()
	{
		return stateId;
	}

	public Boolean getIsInCleanDB()
	{
		return isInCleanDB;
	}

	public Integer getEngineId()
	{
		return engineId;
	}

	public Integer getPipelineId()
	{
		return pipelineId;
	}

	public Timestamp getUpdated()
	{
		return updated;
	}

	public String getEngineUUID()
	{
		return engineUUID;
	}

	public String getPipelineLabel()
	{
		return pipelineLabel;
	}

	public String getStateLabel()
	{
		return stateLabel;
	}

	public EnumDatabaseInstance getDatabaseInstance()
	{
		return isInCleanDB ? EnumDatabaseInstance.CLEAN : EnumDatabaseInstance.DIRTY;
	}

	public Integer getPipelineAuthorId()
	{
		return pipelineAuthorId;
	}
}
