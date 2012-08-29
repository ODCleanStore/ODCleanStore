package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.datanormalization.impl.DataNormalizerImpl;
import cz.cuni.mff.odcleanstore.engine.common.FormatHelper;
import cz.cuni.mff.odcleanstore.engine.common.Utils;
import cz.cuni.mff.odcleanstore.engine.db.model.PipelineCommand;
import cz.cuni.mff.odcleanstore.linker.impl.LinkerImpl;
import cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAssessorImpl;
import cz.cuni.mff.odcleanstore.transformer.EnumTransformationType;
import cz.cuni.mff.odcleanstore.transformer.Transformer;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;

public class PipelineGraphTransformerExecutor {
	
	private static final Logger LOG = LoggerFactory.getLogger(PipelineGraphTransformerExecutor.class);
	
	static final String ERROR_WORKING_DIRECTORY_CHECK = "transformer working directory checking error"; 
	static final String ERROR_LOAD_CUSTOM_TRANSFORMER = "custom transformer loading error";
	static final String ERROR_ITERATE_TRANSFORMERS = "error during iterate transformers"; 
	static final String ERROR_TRANSFORMER_PROCESSING = "error during processing transformer";
	static final String ERROR_TRANSFORMER_SHUTDOWN = "error during shutdowning transformer";
	static final String ERROR_TRANSFORMER_RUN = "error during running transformer";
	static final String ERROR_TRANSFORMER_UNKNOWN = "unknown transformer";
	
	static final String TRANSFORMER_OI_FULL_CLASS_PATH = "cz.cuni.mff.odcleanstore.linker.impl.LinkerImpl";
	static final String TRANSFORMER_QA_FULL_CLASS_PATH = "cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAssessorImpl";
	static final String TRANSFORMER_DN_FULL_CLASS_PATH = "cz.cuni.mff.odcleanstore.datanormalization.impl.DataNormalizerImpl";
	
	private PipelineGraphStatus graphStatus = null;
	private Transformer currentTransformer = null;
	
	PipelineGraphTransformerExecutor(PipelineGraphStatus graphStatus) {
		this.graphStatus = graphStatus;
	}
	
	void shutdown() throws TransformerException {
		Transformer transformer = currentTransformer;
		if (transformer != null) {
			currentTransformer.shutdown();
		}
	}

	void execute() throws PipelineGraphTransformerExecutorException {
		try {
			for (PipelineCommand pipelineCommand : this.graphStatus.getPipelineCommands()) {
				executeTransformer(pipelineCommand);
			}
		} catch(PipelineGraphTransformerExecutorException e) {
			throw e;
		}catch(Exception e) {
			throw new PipelineGraphTransformerExecutorException(format(ERROR_ITERATE_TRANSFORMERS), e);
		}
	}

	private void executeTransformer(PipelineCommand command) throws PipelineGraphTransformerExecutorException {
		TransformationContext context = null;
		TransformedGraph graph = null;
		try {
			if (this.graphStatus.isInCleanDb() && !command.runOnCleanDB) {
				return;
			}
			LOG.info(format("start processing", command));
			if ((this.currentTransformer = getTransformerForCommand(command)) == null) {
				throw new PipelineGraphTransformerExecutorException(format(ERROR_TRANSFORMER_UNKNOWN, command));
			}
						
			graph = new TransformedGraph(this.graphStatus);
			String path = checkTransformerWorkingDirectory(command);
			EnumTransformationType type = this.graphStatus.isInCleanDb() ? EnumTransformationType.EXISTING : EnumTransformationType.NEW;  			
			context = new TransformationContext(command.configuration, path, type);
			
			try {
				if (this.graphStatus.isInCleanDb()) {
					this.currentTransformer.transformExistingGraph(graph, context);
				} else {
					this.currentTransformer.transformNewGraph(graph, context);
				}
			}
			catch (Exception e) {
				throw new PipelineGraphTransformerExecutorException(format(ERROR_TRANSFORMER_RUN, command), e);	
			}
			
			LOG.info(format("finished processing", command));
		} catch(PipelineGraphTransformerExecutorException e) {
			throw e;
		} catch(Exception e) {
			throw new PipelineGraphTransformerExecutorException(format(ERROR_TRANSFORMER_PROCESSING, command), e);
		}
		finally {
			Transformer transformer = this.currentTransformer;
			this.currentTransformer = null;
			if (graph != null) {
				graph.deactivate();
			}
			if (context != null) {
				context.deactivate();
			}
			try {	
				if (transformer != null) {
					transformer.shutdown();
				}
			} catch(Exception e) {
				throw new PipelineGraphTransformerExecutorException(format(ERROR_TRANSFORMER_SHUTDOWN, command), e);
			}
		}
	}

	private Transformer getTransformerForCommand(PipelineCommand command) throws PipelineGraphTransformerExecutorException  {
		
		Transformer transformer = null;
		if (!command.jarPath.equals(".")) {
			transformer = loadCustomTransformer(command);
		} else if (command.fullClassName.equals(TRANSFORMER_OI_FULL_CLASS_PATH)) {
			transformer = new LinkerImpl(this.graphStatus.getOiGroups(command.transformerInstanceID));
		} else if (command.fullClassName.equals(TRANSFORMER_QA_FULL_CLASS_PATH)) {
			transformer = new QualityAssessorImpl(this.graphStatus.getQaGroups(command.transformerInstanceID));
		} else if (command.fullClassName.equals(TRANSFORMER_DN_FULL_CLASS_PATH)) {
			transformer = new DataNormalizerImpl(this.graphStatus.getDnGroups(command.transformerInstanceID));
		}
		return transformer;
	}

	private String checkTransformerWorkingDirectory(PipelineCommand command) throws PipelineGraphTransformerExecutorException {
		try {
			return Utils.satisfyDirectory(command.workDirPath);
		} catch (Exception e) {
			throw new PipelineGraphTransformerExecutorException(format(ERROR_WORKING_DIRECTORY_CHECK, command), e);
		}
	}
	
	private Transformer loadCustomTransformer(PipelineCommand command) throws PipelineGraphTransformerExecutorException  {
		try {
			URL url = new File(command.jarPath).toURI().toURL();
			URLClassLoader loader = new URLClassLoader(new URL[]{url}, command.getClass().getClassLoader());
			Class<?> trida = Class.forName(command.fullClassName, true, loader);
			Object obj = trida.getConstructor(new Class[]{}).newInstance(new Object[]{});
			return  obj instanceof Transformer ? (Transformer) obj : null;
		}
		catch(Exception e) {
			throw new PipelineGraphTransformerExecutorException(format("ERROR_LOAD_CUSTOM_TRANSFORMER", command), e); 
		}
	}

	private String format(String message) {
		return FormatHelper.formatGraphMessage(message, this.graphStatus.getUuid());
	}
	
	private String format(String message, PipelineCommand command) {
		return FormatHelper.formatGraphMessage(message + " for transformer " + command.fullClassName, this.graphStatus.getUuid());
	}	
}
