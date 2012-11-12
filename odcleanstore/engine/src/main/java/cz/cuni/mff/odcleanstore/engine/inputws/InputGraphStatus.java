package cz.cuni.mff.odcleanstore.engine.inputws;

import java.util.HashSet;

import cz.cuni.mff.odcleanstore.engine.Engine;
import cz.cuni.mff.odcleanstore.engine.db.model.DbOdcsContext;
import cz.cuni.mff.odcleanstore.engine.db.model.Pipeline;

/**
 *  @author Petr Jerman
 */
public final class InputGraphStatus {
	
	private HashSet<String> importingGraphs;
	
	InputGraphStatus() {
		importingGraphs = new HashSet<String>();
	}
    
	String[] getAllImportingGraphUuids() throws InputGraphStatusException {
        DbOdcsContext context = null;
        try {
            context = new DbOdcsContext();
            return context.selectAllImportingGraphsForEngine(Engine.getCurrent().getEngineUuid());
        } catch (Exception e) {
            throw new InputGraphStatusException("Error during getting all importing graph uuids", e);
        } finally {
            if (context != null) {
                context.closeQuietly();
            }
        }
	}
	
	static void deleteImportingGraph(String uuid) throws InputGraphStatusException {
		DbOdcsContext context = null;
        try {
            context = new DbOdcsContext();
            context.deleteImportingGraph(uuid);
        } catch (Exception e) {
            throw new InputGraphStatusException("Error during deleteting importing graph uuid", e);
        } finally {
            if (context != null) {
                context.closeQuietly();
            }
        }
	}

	synchronized void beginImport(String uuid, String pipelineName) throws InputGraphStatusException {

		DbOdcsContext context = null;
        try {
        	if (importingGraphs.contains(uuid)) {
        		String message = String.format("Graph %s is already importing", uuid);
    			throw new InputGraphStatusException(message, InputWSErrorEnumeration.SERVICE_BUSY);
    		}
        	
        	context = new DbOdcsContext();
			
			if (context.isGraphUuidInSystem(uuid)) {
				String message = String.format("Graph %s is already imported", uuid);
				throw new InputGraphStatusException(message, InputWSErrorEnumeration.DUPLICATED_UUID);
			}
			
			int pipelineId = 0;
			if (pipelineName == null || pipelineName.isEmpty())	{
				Pipeline pipeline = context.selectDefaultPipeline();
				if (pipeline == null) {
					throw new InputGraphStatusException("Default pipeline is not defined", InputWSErrorEnumeration.FATAL_ERROR);
				}
				pipelineId = pipeline.id;
			}
			else {
				pipelineId = context.selectPipelineId(pipelineName);
				if (pipelineId == 0) {
					String message = String.format("Graph %s has unknown pipeline name %s", uuid, pipelineName);
					throw new InputGraphStatusException(message, InputWSErrorEnumeration.UNKNOWN_PIPELINENAME);
				}
			}
			
			int engineId = context.selectEngineId(Engine.getCurrent().getEngineUuid());
			context.insertImportingGraph(uuid, pipelineId, engineId);
			context.commit();
			importingGraphs.add(uuid);
        } catch (InputGraphStatusException e) {
        	throw e;
        } catch (Exception e) {
        	String message = String.format("Fatal error in beginning import graph %s", uuid);
            throw new InputGraphStatusException(message, e);
        } finally {
            if (context != null) {
                context.closeQuietly();
            }
        }
	}
	
	synchronized void revertImport(String uuid) throws InputGraphStatusException {
		DbOdcsContext context = null;
        try {
        	context = new DbOdcsContext();
        	context.deleteImportingGraph(uuid);
        	context.commit();
        	importingGraphs.remove(uuid);
        } catch (Exception e) {
        	String message = String.format("Fatal error in reverting import graph %s", uuid);
            throw new InputGraphStatusException(message, e);
        } finally {
            if (context != null) {
                context.closeQuietly();
            }
        }
	}

	synchronized void commitImport(String uuid) throws InputGraphStatusException {
		DbOdcsContext context = null;
        try {
        	context = new DbOdcsContext();
        	context.updateImportingGraphStateToQueued(uuid);
        	context.commit();
        	importingGraphs.remove(uuid);
        } catch (Exception e) {
        	String message = String.format("Fatal error in commiting import graph %s", uuid);
            throw new InputGraphStatusException(message, e);
        } finally {
            if (context != null) {
                context.closeQuietly();
            }
        }
	}
}
