package cz.cuni.mff.odcleanstore.engine.db.model;

public final class Graph {
	
	public int id;
	public String uuid;
	public GraphStates state;
	public Integer pipelineId;
	public boolean isInCleanDb;
	public String engineUuid;
}
