package cz.cuni.mff.odcleanstore.engine.db.model;

public final class Graph {
	
	public int id;
	public String uuid;
	public GraphStates state;
	public Pipeline pipeline;
	public boolean isInCleanDb;
	public boolean resetPipelineRequest;
	public String engineUuid;
}
