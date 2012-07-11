package cz.cuni.mff.odcleanstore.engine.outputws;

import java.util.Map;
import java.util.TreeMap;

import org.restlet.data.CharacterSet;
import org.restlet.data.Form;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import cz.cuni.mff.odcleanstore.configuration.OutputWSConfig;
import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumAggregationErrorStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumAggregationType;
import cz.cuni.mff.odcleanstore.engine.outputws.output.HTMLFormatter;
import cz.cuni.mff.odcleanstore.engine.outputws.output.QueryResultFormatter;
import cz.cuni.mff.odcleanstore.engine.outputws.output.TriGFormatter;
import cz.cuni.mff.odcleanstore.shared.Utils;

/**
 *  @author Petr Jerman
 */
public abstract class QueryExecutorResourceBase extends ServerResource {

	private static final String DEFAULT_AGGREGATION_PARAM = "aggr";
	private static final String DEFAULT_MULTIVALUE_PARAM = "multivalue";
	private static final String ERROR_STRATEGY_PARAM = "es";
	private static final String FORMAT_PARAM = "format";
	private static final String PROPERTY_AGGREGATION_PARAM = "paggr[";
	private static final String PROPERTY_MULTIVALUE_PARAM = "pmultivalue[";
	
	private static final String TRUE_LITERAL = "1";
	
	private Form _form;
	
	protected String getFormValue(String key){
		return _form.getFirstValue(key);
	}
	
	protected String[] getFormValuesArray(String key){
		return _form.getValuesArray(key);
	}
	
	protected Map<String, String> getValuesMap() {
		return _form.getValuesMap();
	}
	
	protected Reference getRequestReference() {
		return this.getRequest().getResourceRef();
	}
		
	@Get
	public Representation executeGet() {
		_form = this.getQuery();
		return execute();
	}

	@Post
	public Representation executePost(Representation entity) {
		_form = new Form(entity);
		return execute();
	}
	
	protected abstract Representation execute();
	
	/**
	 * Returns an appropriate formatter of the result.
	 * @param outputWSConfig Configuration of the output webservice from the global configuration file.
	 * @return formatter of query result
	 * TODO: accept also content-negotiation (Accept request header)
	 */
	protected QueryResultFormatter getFormatter(OutputWSConfig outputWSConfig) {
		String formatName = getFormValue(FORMAT_PARAM);
		
		if (formatName != null && !formatName.isEmpty()) {
			if(formatName.equalsIgnoreCase("trig")) {
				return new TriGFormatter(outputWSConfig);
			}
		}
		return new HTMLFormatter(outputWSConfig);
	}
	
	/**
	 * Reads parameters given to the output webservice and build an AggregationSpec object according to it.
	 * @return aggregation settings given to the output webservice
	 */
	protected AggregationSpec getAggregationSpec() {
		AggregationSpec aggregationSpec = new AggregationSpec();
		Map<String, EnumAggregationType> propertyAggregations = new TreeMap<String, EnumAggregationType>();
		Map<String, Boolean> propertyMultivalue = new TreeMap<String, Boolean>();

		String defaultAggregation = getFormValue(DEFAULT_AGGREGATION_PARAM);
		if (defaultAggregation != null && !defaultAggregation.isEmpty()) {
			aggregationSpec.setDefaultAggregation(EnumAggregationType.valueOf(defaultAggregation));
		}
		String defaultMultivalue = getFormValue(DEFAULT_MULTIVALUE_PARAM);
		if (defaultMultivalue != null && !defaultMultivalue.isEmpty()) {
			aggregationSpec.setDefaultMultivalue(TRUE_LITERAL.equals(defaultMultivalue));
		}
		String errorStrategy = getFormValue(ERROR_STRATEGY_PARAM);
		if (errorStrategy != null && !errorStrategy.isEmpty()) {
			aggregationSpec.setErrorStrategy(EnumAggregationErrorStrategy.valueOf(errorStrategy));
		}

		Map<String, String> valuesMap = getValuesMap();
		for (String param : valuesMap.keySet()) {
			if (param.startsWith(PROPERTY_AGGREGATION_PARAM) && param.endsWith("]")) {
				String property = param.substring(PROPERTY_AGGREGATION_PARAM.length(), param.length() - 1);
				String aggregationString = valuesMap.get(param);
				if (!Utils.isNullOrEmpty(aggregationString) && !property.isEmpty()) {
					EnumAggregationType aggregation = EnumAggregationType.valueOf(aggregationString); // TODO: error handling
					propertyAggregations.put(property, aggregation);
				}
			}
			else if (param.startsWith(PROPERTY_MULTIVALUE_PARAM) && param.endsWith("]")) {
				String property = param.substring(PROPERTY_MULTIVALUE_PARAM.length(), param.length() - 1);
				String multivalueString = valuesMap.get(param);
				if (!Utils.isNullOrEmpty(multivalueString) && !property.isEmpty()) {
					propertyMultivalue.put(property, TRUE_LITERAL.equals(multivalueString));
				}
			}
		}
		aggregationSpec.setPropertyAggregations(propertyAggregations);
		aggregationSpec.setPropertyMultivalue(propertyMultivalue);
		return aggregationSpec;
	}

	protected Representation return404() {
		setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		return new StringRepresentation("Error", MediaType.TEXT_PLAIN, Language.ALL, CharacterSet.UTF_8);
	}
}