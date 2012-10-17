package cz.cuni.mff.odcleanstore.webfrontend.bo.en;

import java.sql.Timestamp;

import cz.cuni.mff.odcleanstore.webfrontend.bo.EntityWithSurrogateKey;

/**
 * 
 * @author Jakub Daniel
 *
 */
public class AttachedEngine extends EntityWithSurrogateKey
{
	private static final long serialVersionUID = 1L;
	
	public int id;
	public String uuid;
	public boolean isPipelineError;
	public boolean isNotifyRequired;
	public String stateDescription;
	public Timestamp updated;
	
	public AttachedEngine(int id,
			String uuid,
			boolean isPipelineError,
			boolean isNotifyRequired,
			String stateDescription,
			Timestamp updated) {

		this.id = id;
		this.uuid = uuid;
		this.isPipelineError = isPipelineError;
		this.isNotifyRequired = isNotifyRequired;
		this.stateDescription = stateDescription;
		this.updated = updated;
	}
}
