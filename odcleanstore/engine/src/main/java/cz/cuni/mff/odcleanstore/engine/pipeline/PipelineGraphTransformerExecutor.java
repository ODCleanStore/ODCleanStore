package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.datanormalization.impl.DataNormalizerImpl;
import cz.cuni.mff.odcleanstore.engine.common.FormatHelper;
import cz.cuni.mff.odcleanstore.engine.db.model.PipelineCommand;
import cz.cuni.mff.odcleanstore.linker.impl.LinkerImpl;
import cz.cuni.mff.odcleanstore.log4j.RollingFileAppender;
import cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAggregatorImpl;
import cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAssessorImpl;
import cz.cuni.mff.odcleanstore.shared.FileUtils;
import cz.cuni.mff.odcleanstore.transformer.EnumTransformationType;
import cz.cuni.mff.odcleanstore.transformer.Transformer;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;
import cz.cuni.mff.odcleanstore.transformer.odcs.ODCSBNodeToResourceTransformer;
import cz.cuni.mff.odcleanstore.transformer.odcs.ODCSLatestUpdateMarkerTransformer;
import cz.cuni.mff.odcleanstore.transformer.odcs.ODCSPropertyFilterTransformer;

/**
 * Class executing the pipeline of transformers for a given graph.
 * 
 * @author Petr Jerman
 */
public class PipelineGraphTransformerExecutor {
    
    private static final Logger LOG = LoggerFactory.getLogger(PipelineGraphTransformerExecutor.class);
    
    private static final String ERROR_WORKING_DIRECTORY_CHECK = "transformer working directory checking error"; 
    private static final String ERROR_LOAD_CUSTOM_TRANSFORMER = "custom transformer loading error";
    private static final String ERROR_ITERATE_TRANSFORMERS = "error during iterate transformers"; 
    private static final String ERROR_TRANSFORMER_PROCESSING = "error during processing transformer";
    private static final String ERROR_TRANSFORMER_SHUTDOWN = "error during shutdowning transformer";
    private static final String ERROR_TRANSFORMER_RUN = "error during running transformer";
    private static final String ERROR_TRANSFORMER_UNKNOWN = "unknown transformer";
    
    // CHECKSTYLE:OFF
    private static final PipelineCommand ODCSPropertyFilterTransformerCommand;
    private static final PipelineCommand ODCSLatestUpdateMarkerTransformerCommand;
    // CHECKSTYLE:ON
    
    static {
        ODCSPropertyFilterTransformerCommand = new PipelineCommand();
        ODCSPropertyFilterTransformerCommand.jarPath = ".";
        ODCSPropertyFilterTransformerCommand.fullClassName = ODCSPropertyFilterTransformer.class.getCanonicalName();
        ODCSPropertyFilterTransformerCommand.transformerInstanceID = 0;
        ODCSPropertyFilterTransformerCommand.configuration = "";
        ODCSPropertyFilterTransformerCommand.runOnCleanDB = false;
        ODCSPropertyFilterTransformerCommand.workDirPath = "transformers-working-dir/odcsInternalTransformers";
        ODCSPropertyFilterTransformerCommand.transformerLabel = "ODCSPropertyFilterTransformer";
        
        ODCSLatestUpdateMarkerTransformerCommand = new PipelineCommand();
        ODCSLatestUpdateMarkerTransformerCommand.jarPath = ".";
        ODCSLatestUpdateMarkerTransformerCommand.fullClassName = ODCSLatestUpdateMarkerTransformer.class.getCanonicalName();
        ODCSLatestUpdateMarkerTransformerCommand.transformerInstanceID = 0;
        ODCSLatestUpdateMarkerTransformerCommand.configuration = "";
        ODCSLatestUpdateMarkerTransformerCommand.runOnCleanDB = false;
        ODCSLatestUpdateMarkerTransformerCommand.workDirPath = "transformers-working-dir/odcsInternalTransformers";
        ODCSLatestUpdateMarkerTransformerCommand.transformerLabel = "ODCSLatestUpdateMarkerTransformer";
    }
    
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
            executeTransformer(ODCSPropertyFilterTransformerCommand, true);
            for (PipelineCommand pipelineCommand : this.graphStatus.getPipelineCommands()) {
                executeTransformer(pipelineCommand, false);
            }
            executeTransformer(ODCSLatestUpdateMarkerTransformerCommand, true);
        } catch (PipelineGraphTransformerExecutorException e) {
            throw e;
        } catch (Exception e) {
            throw new PipelineGraphTransformerExecutorException(format(ERROR_ITERATE_TRANSFORMERS), null, e);
        }
    }
    
    private void executeTransformer(PipelineCommand command, boolean isInternal) 
            throws PipelineGraphTransformerExecutorException {
        
        TransformationContext context = null;
        TransformedGraph graph = null;
        try {
            if (this.graphStatus.isInCleanDb() && !command.runOnCleanDB) {
                return;
            }
            LOG.info(format("start processing", command));
            
            this.currentTransformer = getTransformerForCommand(command, isInternal);
            if (this.currentTransformer == null) {
                throw new PipelineGraphTransformerExecutorException(
                        format(ERROR_TRANSFORMER_UNKNOWN, command), command);
            }
                        
            graph = new TransformedGraph(this.graphStatus);
            String path = checkTransformerWorkingDirectory(command);
            EnumTransformationType type = this.graphStatus.isInCleanDb()
                    ? EnumTransformationType.EXISTING
                    : EnumTransformationType.NEW;
            context = new TransformationContext(command.configuration, path, type);
            
            graphStatus.checkResetPipelineRequest();
            try {
                String logFileName = new File(path, "odcs.engine.log").getAbsolutePath();
                RollingFileAppender.setNewLogFile(logFileName);
                try {
                    this.currentTransformer.transformGraph(graph, context);
                } finally {
                    RollingFileAppender.popPreviousLogFile();
                }
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                throw new PipelineGraphTransformerExecutorException(
                        format(ERROR_TRANSFORMER_RUN, command), command, e);
            }
            
            LOG.info(format("finished processing", command));
            
        } catch (PipelineGraphTransformerExecutorException e) {
            throw e;
        } catch (Exception e) {
            throw new PipelineGraphTransformerExecutorException(
                    format(ERROR_TRANSFORMER_PROCESSING, command), command, e);
        } finally {
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
            } catch (Exception e) {
                throw new PipelineGraphTransformerExecutorException(
                        format(ERROR_TRANSFORMER_SHUTDOWN, command), command, e);
            }
        }
    }

    private Transformer getTransformerForCommand(PipelineCommand command, boolean isInternal)
            throws PipelineGraphTransformerExecutorException  {
        
        Transformer transformer = null;
        if (!command.jarPath.equals(".")) {
            transformer = loadCustomTransformer(command);
        } else if (command.fullClassName.equals(LinkerImpl.class.getCanonicalName())) {
            transformer = new LinkerImpl(this.graphStatus.getOiGroups(command.transformerInstanceID));
        } else if (command.fullClassName.equals(QualityAssessorImpl.class.getCanonicalName())) {
            transformer = new QualityAssessorImpl(this.graphStatus.getQaGroups(command.transformerInstanceID));
        } else if (command.fullClassName.equals(DataNormalizerImpl.class.getCanonicalName())) {
            transformer = new DataNormalizerImpl(this.graphStatus.getDnGroups(command.transformerInstanceID));
        } else if (command.fullClassName.equals(ODCSPropertyFilterTransformer.class.getCanonicalName()) && isInternal) {
            transformer = new ODCSPropertyFilterTransformer();
        } else if (command.fullClassName.equals(QualityAggregatorImpl.class.getCanonicalName())) {
            transformer = new QualityAggregatorImpl();
        } else if (command.fullClassName.equals(ODCSBNodeToResourceTransformer.class.getCanonicalName())) {
            transformer = new ODCSBNodeToResourceTransformer();
        } else if (command.fullClassName.equals(ODCSLatestUpdateMarkerTransformer.class.getCanonicalName()) && isInternal) {
            transformer = new ODCSLatestUpdateMarkerTransformer();
        }
        return transformer;
    }

    private String checkTransformerWorkingDirectory(PipelineCommand command) 
            throws PipelineGraphTransformerExecutorException {
        File file = new File(command.workDirPath, Integer.toString(command.transformerInstanceID));
        String path = file.getPath();
        try {
            return FileUtils.satisfyDirectory(path);
        } catch (Exception e) {
            throw new PipelineGraphTransformerExecutorException(
                    format(ERROR_WORKING_DIRECTORY_CHECK, command), command, e);
        }
    }
    
    private Transformer loadCustomTransformer(PipelineCommand command) throws PipelineGraphTransformerExecutorException  {
        try {
            URL url = new File(command.jarPath).toURI().toURL();
            URLClassLoader loader = new URLClassLoader(new URL[]{url}, command.getClass().getClassLoader());
            Class<?> trida = Class.forName(command.fullClassName, true, loader);
            Object obj = trida.getConstructor(new Class[] {}).newInstance(new Object[] {});
            return obj instanceof Transformer ? (Transformer) obj : null;
        } catch (Exception e) {
            throw new PipelineGraphTransformerExecutorException(
                    format(ERROR_LOAD_CUSTOM_TRANSFORMER, command), command, e); 
        }
    }

    private String format(String message) {
        try {
            return FormatHelper.formatGraphMessage(
                    message, graphStatus.getUuid(), graphStatus.isInCleanDbBeforeProcessing());
        } catch (Exception e) {
            return message;
        }
    }

    private String format(String message, PipelineCommand command) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(command.transformerLabel);
            sb.append(" (instance: ");
            sb.append(command.transformerInstanceID);
            sb.append(") - ");
            sb.append(message);
            return FormatHelper.formatGraphMessage(
                    sb.toString(), graphStatus.getUuid(), graphStatus.isInCleanDbBeforeProcessing());
        } catch (Exception e) {
            return message;
        }
    }
}
