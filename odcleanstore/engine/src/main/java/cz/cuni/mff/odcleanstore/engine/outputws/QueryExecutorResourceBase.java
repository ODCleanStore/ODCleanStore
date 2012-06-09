package cz.cuni.mff.odcleanstore.engine.outputws;

import java.util.Map;
import java.util.TreeMap;

import org.restlet.data.CharacterSet;
import org.restlet.data.Form;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumAggregationErrorStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumAggregationType;
import cz.cuni.mff.odcleanstore.engine.outputws.output.HTMLFormatter;
import cz.cuni.mff.odcleanstore.engine.outputws.output.QueryResultFormatter;
import cz.cuni.mff.odcleanstore.engine.outputws.output.TriGFormatter;

/**
 *  @author Petr Jerman
 */
public abstract class QueryExecutorResourceBase extends ServerResource {

	private Form _form;
	
	protected String getFormValue(String key){
		return _form.getFirstValue(key);
	}
	
	protected String[] getFormValuesArray(String key){
		return _form.getValuesArray(key);
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
	 * @return formatter of query result
	 * TODO: choose a formatter according to user preferences (URL query variable, Accept request header)
	 */
	protected QueryResultFormatter getFormatter() {
		String formatName = getFormValue("format");
		
		if (formatName != null && !formatName.isEmpty()) {
			if(formatName.equalsIgnoreCase("TrigX")) {
				return new TriGFormatter();
			}
		}
		return new HTMLFormatter();
	}
	
	/**
	 * TODO: only temporary.
	 */
	protected AggregationSpec getAggregationSpec() {
		AggregationSpec aggregationSpec = new AggregationSpec();
		Map<String, EnumAggregationType> propertyAggregations = new TreeMap<String, EnumAggregationType>();
		Map<String, Boolean> propertyMultivalue = new TreeMap<String, Boolean>();

		String defaultAggregation = getFormValue("aggregation");
		if (defaultAggregation != null && !defaultAggregation.isEmpty()) {
			aggregationSpec.setDefaultAggregation(EnumAggregationType.valueOf(defaultAggregation));
		}
		String defaultMultivalue = getFormValue("multivalue");
		if (defaultMultivalue != null && !defaultMultivalue.isEmpty()) {
			aggregationSpec.setDefaultMultivalue(Boolean.valueOf(defaultMultivalue));
		}
		String errorStrategy = getFormValue("es");
		if (errorStrategy != null && !errorStrategy.isEmpty()) {
			aggregationSpec.setErrorStrategy(EnumAggregationErrorStrategy.valueOf(errorStrategy));
		}
		
		String[] propaNames = getFormValuesArray("propa-name");
		String[] propaValues = getFormValuesArray("propa-value");
		for (int i = 0; i < propaNames.length && i < propaValues.length; i++) {
			if (propaNames[i] != null && !propaNames[i].isEmpty()) {
				propertyAggregations.put(propaNames[i], EnumAggregationType.valueOf(propaValues[i]));
			}
		}
		aggregationSpec.setPropertyAggregations(propertyAggregations);
		
		String[] propmNames = getFormValuesArray("propm-name");
		String[] propmValues = getFormValuesArray("propm-value");
		for (int i = 0; i < propmNames.length && i < propmValues.length; i++) {
			if (propmNames[i] != null && !propmNames[i].isEmpty()) {
				propertyMultivalue.put(propmNames[i], Boolean.valueOf(propmValues[i]));
			}
		}
		aggregationSpec.setPropertyMultivalue(propertyMultivalue);
		return aggregationSpec;
	}

	protected Representation return404() {
		setStatus(Status.CLIENT_ERROR_NOT_FOUND);
		return new StringRepresentation("NENY TEDKA", MediaType.TEXT_PLAIN, Language.ALL, CharacterSet.UTF_8);
	}
}