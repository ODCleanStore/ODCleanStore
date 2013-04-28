
package cz.cuni.mff.odcleanstore.engine.outputws;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.configuration.OutputWSConfig;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolutionPolicy;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumAggregationErrorStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumCardinality;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolutionStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.ConflictResolutionPolicyImpl;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.ResolutionStrategyImpl;
import cz.cuni.mff.odcleanstore.engine.common.FormatHelper;
import cz.cuni.mff.odcleanstore.engine.outputws.output.HTMLFormatter;
import cz.cuni.mff.odcleanstore.engine.outputws.output.QueryResultFormatter;
import cz.cuni.mff.odcleanstore.engine.outputws.output.RDFXMLFormatter;
import cz.cuni.mff.odcleanstore.engine.outputws.output.TriGFormatter;
import cz.cuni.mff.odcleanstore.queryexecution.QueryExecution;
import cz.cuni.mff.odcleanstore.queryexecution.QueryExecutionException;
import cz.cuni.mff.odcleanstore.queryexecution.impl.PrefixMapping;
import cz.cuni.mff.odcleanstore.queryexecution.impl.PrefixMappingCache;
import cz.cuni.mff.odcleanstore.shared.ODCSUtils;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;

import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *  @author Petr Jerman
 */
public abstract class QueryExecutorResourceBase extends ServerResource {

    private static final Logger LOG = LoggerFactory.getLogger(QueryExecutorResourceBase.class);

    public static final String DEFAULT_AGGREGATION_PARAM = "aggr";
    public static final String DEFAULT_MULTIVALUE_PARAM = "multivalue";
    public static final String ERROR_STRATEGY_PARAM = "es";
    public static final String FORMAT_PARAM = "format";
    public static final String PROPERTY_AGGREGATION_PARAM = "paggr[";
    public static final String PROPERTY_MULTIVALUE_PARAM = "pmultivalue[";

    private static final String TRUE_LITERAL = "1";
    
    /**
     * Class implementing the "initialize-on-demand holder class" idiom for QueryExecution.
     * Used to lazily initialize QueryExecution (when the config is already loaded).
     * @see http://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
     */
    private static class QueryExecutionHolder {
        /**
         * Cached instance of QueryExecution.
         * Query Execution is thread safe and should be maintained between requests because 
         * it can keep cached data of its own.
         */
        static final QueryExecution QUERY_EXECUTION = new QueryExecution(
                ConfigLoader.getConfig().getQueryExecutionGroup().getCleanDBJDBCConnectionCredentials(),
                ConfigLoader.getConfig());
    }
    
    /**
     * Class implementing the "initialize-on-demand holder class" idiom for PrefixMappingCache.
     */
    private static class PrefixMappingHolder {
        /**
         * Cached instance of PrefixMappingCache.
         * PrefixMappingCache is thread safe.
         */
        static final PrefixMappingCache PREFIX_MAPPING_CACHE = new PrefixMappingCache(
                ConfigLoader.getConfig().getQueryExecutionGroup().getCleanDBJDBCConnectionCredentials());
    }

    /**
     * Returns the singleton instance of {@link QueryExecution}.
     * @return QueryExecution instance
     */
    protected static QueryExecution getQueryExecution() {
        return QueryExecutionHolder.QUERY_EXECUTION;
    }
    
    private Form form;
    
    /**
     * Returns prefix mapping. The returned value is cached.
     * @return PrefixMapping instance
     */
    protected PrefixMapping getPrefixMapping() {
        try {
            return PrefixMappingHolder.PREFIX_MAPPING_CACHE.getCachedValue();
        } catch (QueryExecutionException e) {
            LOG.error("Could not load prefix mappings.");
            return new PrefixMapping(Collections.<String, String>emptyMap());
        }
    }
    
    protected String getFormValue(String key) {
        return form.getFirstValue(key);
    }

    protected String[] getFormValuesArray(String key) {
        return form.getValuesArray(key);
    }

    protected Map<String, String> getValuesMap() {
        return form.getValuesMap();
    }

    protected Reference getRequestReference() {
        return this.getRequest().getResourceRef();
    }

    @Get
    public Representation executeGet() {
        return parseRequest(false, null) ? executeInternal() : null;
    }

    @Post
    public Representation executePost(Representation entity) {
        return parseRequest(true, entity) ? executeInternal() : null;
    }

    private boolean parseRequest(boolean isPost, Representation entity) {
        try {
            if (isPost) {
                form = new Form(entity);
            } else {
                form = this.getQuery();
            }
            return true;
        } catch (Exception e) {
            LOG.warn(FormatHelper.formatExceptionForLog(e, "Client error bad request"));
            getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
            return false;
        }
    }

    private Representation executeInternal() {
        try {
            if (((Root) getApplication()).canServeRequest()) {
                return execute();
            }
            getResponse().setStatus(Status.SERVER_ERROR_SERVICE_UNAVAILABLE);
        } catch (ResultEmptyException e) {
            LOG.warn("Response has no content");
            getResponse().setStatus(Status.SUCCESS_NO_CONTENT);
        } catch (QueryExecutionException e) {
            String message = String.format(Locale.ROOT, "%s (error code:%d)", e.getMessage(), e.getErrorCode());
            switch (e.getErrorType()) {
            case QUERY_TOO_LONG:
            case INVALID_QUERY_FORMAT:
            case AGGREGATION_SETTINGS_INVALID:
            case UNKNOWN_PREFIX:
                LOG.warn(FormatHelper.formatExceptionForLog(e, "Client error bad request: " + message));
                getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, message);
                break;
            case DATABASE_ERROR:
            case DEFAULT_AGGREGATION_SETTINGS_INVALID:
            case QUERY_EXECUTION_SETTINGS_INVALID:
            case CONFLICT_RESOLUTION_ERROR:
            default:
                LOG.error(FormatHelper.formatExceptionForLog(e, "Server error internal: "  + message));
                getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, message);
            }
        } catch (Exception e) {
            LOG.error(FormatHelper.formatExceptionForLog(e, "Server error internal"));
            getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
        }
        return null;
    }

    /**
     * Serve the current request.
     * @return response to the current request
     * @throws QueryExecutionException exception
     * @throws ResultEmptyException exception
     * @throws TransformerException exception
     */
    protected abstract Representation execute()
            throws QueryExecutionException, ResultEmptyException, TransformerException;

    /**
     * Returns an appropriate formatter of the result.
     * @param outputWSConfig Configuration of the output webservice from the global configuration file.
     * @return formatter of query result
     *         TODO: accept also content-negotiation (Accept request header)
     */
    protected QueryResultFormatter getFormatter(OutputWSConfig outputWSConfig) {
        String formatName = getFormValue(FORMAT_PARAM);

        if (formatName != null && !formatName.isEmpty()) {
            if (formatName.equalsIgnoreCase("trig")) {
                return new TriGFormatter(outputWSConfig);
            } else if (formatName.equalsIgnoreCase("rdfxml")) {
                return new RDFXMLFormatter(outputWSConfig);
            }
        }
        return new HTMLFormatter(outputWSConfig, getPrefixMapping());
    }

    /**
     * Reads parameters given to the output webservice and build an AggregationSpec object according to it.
     * @return aggregation settings given to the output webservice
     */
    protected ConflictResolutionPolicy getConflictResolutionPolicy() {
        ResolutionStrategyImpl defaultResolutionStrategy = new ResolutionStrategyImpl();
        String defaultFunctionName = getFormValue(DEFAULT_AGGREGATION_PARAM);
        if (defaultFunctionName != null && !defaultFunctionName.isEmpty()) {
            defaultResolutionStrategy.setResolutionFunctionName(defaultFunctionName);
        }
        String defaultMultivalue = getFormValue(DEFAULT_MULTIVALUE_PARAM);
        if (defaultMultivalue != null && !defaultMultivalue.isEmpty()) {
            defaultResolutionStrategy.setCardinality(TRUE_LITERAL.equals(defaultMultivalue)
                    ? EnumCardinality.MULTIVALUE : EnumCardinality.SINGLEVALUE);
        }
        String errorStrategy = getFormValue(ERROR_STRATEGY_PARAM);
        if (errorStrategy != null && !errorStrategy.isEmpty()) {
            defaultResolutionStrategy.setAggregationErrorStrategy(EnumAggregationErrorStrategy.valueOf(errorStrategy));
        }

        Map<String, String> valuesMap = getValuesMap();
        Map<URI, ResolutionStrategy> propertyStrategies = new HashMap<URI, ResolutionStrategy>();

        for (String param : valuesMap.keySet()) {
            if (param.startsWith(PROPERTY_AGGREGATION_PARAM) && param.endsWith("]")) {
                String property = param.substring(PROPERTY_AGGREGATION_PARAM.length(), param.length() - 1);
                String resolutionFunctionName = valuesMap.get(param); // TODO check validity
                if (!ODCSUtils.isNullOrEmpty(resolutionFunctionName) && !property.isEmpty()) {
                    getOrCreateStrategy(property, propertyStrategies).setResolutionFunctionName(resolutionFunctionName);
                }
            } else if (param.startsWith(PROPERTY_MULTIVALUE_PARAM) && param.endsWith("]")) {
                String property = param.substring(PROPERTY_MULTIVALUE_PARAM.length(), param.length() - 1);
                String multivalueString = valuesMap.get(param);
                if (!ODCSUtils.isNullOrEmpty(multivalueString) && !property.isEmpty()) {
                    EnumCardinality cardinality = TRUE_LITERAL.equals(multivalueString)
                            ? EnumCardinality.MULTIVALUE : EnumCardinality.SINGLEVALUE;
                    getOrCreateStrategy(property, propertyStrategies).setCardinality(cardinality);
                }
            }
        }
        
        return new ConflictResolutionPolicyImpl(defaultResolutionStrategy, propertyStrategies);
    }
    
    private ResolutionStrategyImpl getOrCreateStrategy(
            String property, Map<URI, ResolutionStrategy> propertyResolutionStrategies) {
        URI propertyURI = ValueFactoryImpl.getInstance().createURI(property);
        ResolutionStrategy strategy = propertyResolutionStrategies.get(propertyURI);
        if (strategy == null) {
            strategy = new ResolutionStrategyImpl();
            propertyResolutionStrategies.put(propertyURI, strategy);
        }
        return (ResolutionStrategyImpl) strategy;
    }
}