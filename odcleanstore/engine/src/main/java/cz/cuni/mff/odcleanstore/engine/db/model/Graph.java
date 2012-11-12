package cz.cuni.mff.odcleanstore.engine.db.model;

import cz.cuni.mff.odcleanstore.model.EnumGraphState;

/**
 * Model class representing a graph.
 * @author Petr Jerman
 */
public final class Graph {
    public int id;
    public String uuid;
    public EnumGraphState state;
    public Pipeline pipeline;
    public boolean isInCleanDb;
    public boolean resetPipelineRequest;
    public String engineUuid;
    public String namedGraphsPrefix;
}
