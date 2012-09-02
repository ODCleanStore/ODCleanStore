package cz.cuni.mff.odcleanstore.engine.db.model;

import java.util.Locale;

import cz.cuni.mff.odcleanstore.datanormalization.DataNormalizer;
import cz.cuni.mff.odcleanstore.linker.impl.LinkerImpl;
import cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAssessorImpl;

class SQL {
	
	/**
	 * Set odcs DB prefix.
	 */
	static final String USE_ODCS_SCHEMA = "USE DB";
	
	
	//-----------------------------------------------------------------------------------------------//
	
	/**
	 * Select oldest working graph for given engine uuid.
	 * @param first Engine uuid
	 */
	static final String SELECT_WORKING_GRAPH = String.format(Locale.ROOT, 
			  " SELECT TOP 1 ig.id, ig.uuid, ig.stateId, ig.pipelineId, pi.label, ig.isInCleanDB, ae.uuid" 
			+ " FROM ODCLEANSTORE.EN_INPUT_GRAPHS ig"
			+ " LEFT JOIN ODCLEANSTORE.EN_ATTACHED_ENGINES ae ON ig.engineId = ae.id"
			+ " LEFT JOIN ODCLEANSTORE.PIPELINES pi ON ig.pipelineId = pi.id"
			+ " WHERE (ae.uuid = ? OR ae.uuid IS NULL) AND ig.stateId IN(%s,%s,%s,%s,%s)"
			+ " ORDER BY ig.stateId, ig.updated",
			GraphStates.DIRTY.toId(),
			GraphStates.PROPAGATED.toId(),
			GraphStates.DELETING.toId(),
			GraphStates.PROCESSING.toId(),
			GraphStates.PROCESSED.toId());
	
	static final String ERROR_SELECT_WORKING_GRAPH = "Error during selecting working graph";
	
	
	/**
	 * Select oldest queued graph for given engine uuid.	
	 * @param first Engine uuid
	 */	
	static final String SELECT_QUEUD_GRAPH = String.format(Locale.ROOT, 
			  " SELECT TOP 1 ig.id, ig.uuid, ig.stateId, ig.pipelineId, pi.label, ig.isInCleanDB, ae.uuid" 
			+ " FROM ODCLEANSTORE.EN_INPUT_GRAPHS ig"
			+ " LEFT JOIN ODCLEANSTORE.EN_ATTACHED_ENGINES ae ON ig.engineId = ae.id"
			+ " LEFT JOIN ODCLEANSTORE.PIPELINES pi ON ig.pipelineId = pi.id"
			+ " WHERE (ae.uuid = ? OR ae.uuid IS NULL) AND ig.stateId IN(%s,%s,%s)"
			+ " ORDER BY ig.stateId, ig.updated",
			GraphStates.QUEUED_FOR_DELETE.toId(),
			GraphStates.QUEUED_URGENT.toId(),
			GraphStates.QUEUED.toId());
	
	static final String ERROR_SELECT_QUEUD_GRAPH = "Error during selecting queued graph";

	
	//-----------------------------------------------------------------------------------------------//
	
	/**
	 * Update attached engine id for given graphId.
	 * @param first Engine uuid
	 * @param second graphId
	 * @param third Engine uuid
	 */
	static final String UPDATE_ATTACHED_ENGINE = String.format(Locale.ROOT, 
			  " UPDATE ODCLEANSTORE.EN_INPUT_GRAPHS"
			+ " SET engineId = (SELECT id FROM ODCLEANSTORE.EN_ATTACHED_ENGINES WHERE uuid = ?)"
			+ " WHERE id = ?"
			+ " AND EXISTS (SELECT * FROM ODCLEANSTORE.EN_ATTACHED_ENGINES WHERE uuid = ?)");			

	static final String ERROR_UPDATE_ATTACHED_ENGINE = "Error during updating attached engine";
	
	
	/**
	 * Update graph stateId for given graphId.
	 * @param first stateId
	 * @param second graphId
	 */
	static final String UPDATE_GRAPH_STATE = String.format(Locale.ROOT, 
			  " UPDATE ODCLEANSTORE.EN_INPUT_GRAPHS" 
			+ " SET stateId = ?"
			+ " WHERE id = ?");
	
	static final String ERROR_UPDATE_GRAPH_STATE = "Error during updating graph state";

	
	/**
	 * Update graph stateId and isCleanDb for given graphId.
	 * @param first stateId
	 * @param second isInCleanDb 
	 * @param third graphId
	 */
	static final String UPDATE_GRAPH_STATE_AND_ISINCLEANDB = String.format(Locale.ROOT, 
			  " UPDATE ODCLEANSTORE.EN_INPUT_GRAPHS" 
			+ " SET stateId = ?, isInCleanDb = ?"
			+ " WHERE id = ?");
	
	static final String ERROR_UPDATE_GRAPH_STATE_AND_ISINCLEANDB = "Error during updating graph state";
	
	//-----------------------------------------------------------------------------------------------//
		
	/**
	 * Select all attached graph names for given graphId.
	 * @param first graphId
	 */
	static final String SELECT_ATTACHED_GRAPHS = String.format(Locale.ROOT, 
			  " SELECT name" 
			+ " FROM ODCLEANSTORE.EN_WORKING_ADDED_GRAPHS"
			+ " WHERE graphId = ?");
	
	static final String ERROR_SELECT_ATTACHED_GRAPHS = "Error during selecting attached graphs";
	
	
	/**
	 * Insert name of attached graph to given graphId. 
	 * @param first graphId
	 * @param second graph name
	 */
	static final String INSERT_ATTACHED_GRAPH = String.format(Locale.ROOT, 
			  " INSERT"  
			+ " INTO ODCLEANSTORE.EN_WORKING_ADDED_GRAPHS(graphId, name)"
			+ " VALUES(?,?)");
	
	static final String ERROR_INSERT_ATTACHED_GRAPH = "Error during inserting attached graph";

	
	/**
	 * Delete all attached graphs for given graphId.
	 * @param first graphId
	 */
	static final String DELETE_ATTACHED_GRAPHS = String.format(Locale.ROOT, 
			  " DELETE"  
			+ " FROM ODCLEANSTORE.EN_WORKING_ADDED_GRAPHS"
			+ " WHERE graphId = ?");

	static final String ERROR_DELETE_ATTACHED_GRAPHS = "Error during deleting attached graphs";
	

	//-----------------------------------------------------------------------------------------------//
	
	/**
	 * Select default pipelineId.
	 */
	static final String SELECT_DEFAULT_PIPELINE = 
			  " SELECT TOP 1 id, label"
			+ " FROM ODCLEANSTORE.PIPELINES"
			+ " WHERE isDefault <> 0"; 
	
	static final String ERROR_SELECT_DEFAULT_PIPELINE = "Error during selecting default pipeline";
	
	/**
	 * Select pipeline commands for given pipelineId.
	 * @param first pipelineId
	 */
	static final String SELECT_PIPELINE_COMMANDS = 
			  " SELECT t.jarPath, t.fullClassName, ti.workDirPath, ti.configuration, ti.runOnCleanDB, ti.id, t.label"
			+ " FROM ODCLEANSTORE.TRANSFORMERS t"
			+ " JOIN ODCLEANSTORE.TRANSFORMER_INSTANCES ti ON t.id = ti.transformerId" 
			+ " AND ti.pipelineId = ?" 
			+ " ORDER BY ti.priority";
	
	static final String ERROR_SELECT_PIPELINE_COMMANDS = "Error during selecting pipeline commands";

	
	//-----------------------------------------------------------------------------------------------//
	
	/**
	 * Select qa groups for given pipelineId.
	 * @param first pipelineId
	 */
	static final String SELECT_QA_GROUPS = String.format(Locale.ROOT,
			  " SELECT qa.transformerInstanceId, qa.groupId"
			+ " FROM ODCLEANSTORE.QA_RULES_ASSIGNMENT qa"
			+ " JOIN DB.ODCLEANSTORE.TRANSFORMER_INSTANCES ti ON qa.transformerInstanceId = ti.id"
            + " JOIN DB.ODCLEANSTORE.TRANSFORMERS t ON ti.transformerId = t.id" 
			+ " WHERE ti.pipelineId= ? AND t.fullClassName = '%s'" 
			+ " ORDER BY qa.transformerInstanceId, qa.groupId",
			QualityAssessorImpl.class.getCanonicalName());
	
	static final String ERROR_SELECT_QA_GROUPS = "Error during selecting qa groups";
	
	
	/**
	 * Select dn groups for given pipelineId.
	 * @param first pipelineId
	 */
	static final String SELECT_DN_GROUPS =  String.format(Locale.ROOT,
			  " SELECT dn.transformerInstanceId, dn.groupId"
			+ " FROM ODCLEANSTORE.DN_RULES_ASSIGNMENT dn"
			+ " JOIN DB.ODCLEANSTORE.TRANSFORMER_INSTANCES ti ON dn.transformerInstanceId = ti.id"
            + " JOIN DB.ODCLEANSTORE.TRANSFORMERS t ON ti.transformerId = t.id" 
			+ " WHERE ti.pipelineId= ? AND t.fullClassName = '%s'" 
			+ " ORDER BY dn.transformerInstanceId, dn.groupId",
			DataNormalizer.class.getCanonicalName());
	
	static final String ERROR_SELECT_DN_GROUPS = "Error during selecting dn groups";
	
	/**
	 * Select oi groups for given pipelineId.
	 * @param first pipelineId
	 */
	static final String SELECT_OI_GROUPS =  String.format(Locale.ROOT,
			  " SELECT oi.transformerInstanceId, oi.groupId"
			+ " FROM ODCLEANSTORE.OI_RULES_ASSIGNMENT oi"
			+ " JOIN DB.ODCLEANSTORE.TRANSFORMER_INSTANCES ti ON oi.transformerInstanceId = ti.id"
            + " JOIN DB.ODCLEANSTORE.TRANSFORMERS t ON ti.transformerId = t.id" 
			+ " WHERE ti.pipelineId= ? AND t.fullClassName = '%s'" 
			+ " ORDER BY oi.transformerInstanceId, oi.groupId",
			LinkerImpl.class.getCanonicalName());
	
	static final String ERROR_SELECT_OI_GROUPS = "Error during selecting oi groups";
	
	
	//-----------------------------------------------------------------------------------------------//
	
	/**
	 * Insert graph into pipeline error table.
	 * @param first graphId
	 * @param second errorTypeId
	 * @param third errorMessage  
	 */
	static final String INSERT_GRAPH_IN_ERROR = 
			  " INSERT"  
			+ " INTO ODCLEANSTORE.EN_GRAPHS_IN_ERROR(graphId, errorTypeId, errorMessage)"
			+ " VALUES(?,?,?)";
	
	static final String ERROR_INSERT_GRAPH_IN_ERROR = "Error during inserting graph in graphs in error";
}
