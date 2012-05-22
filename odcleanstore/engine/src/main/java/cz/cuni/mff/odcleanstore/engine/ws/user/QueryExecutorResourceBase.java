package cz.cuni.mff.odcleanstore.engine.ws.user;

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
import cz.cuni.mff.odcleanstore.engine.ws.user.output.HTMLFormatter;
import cz.cuni.mff.odcleanstore.engine.ws.user.output.QueryResultFormatter;

/**
 *  @author Petr Jerman
 */
public abstract class QueryExecutorResourceBase extends ServerResource {

	@Get
	public Representation executeGet() {
		Form form = this.getQuery();
		return execute(form);
	}

	@Post
	public Representation executePost(Representation entity) {
		Form form = new Form(entity);
		return execute(form);
	}

	protected abstract Representation execute(Form form);
	
	/**
	 * Returns an appropriate formatter of the result.
	 * @return formatter of query result
	 * TODO: choose a formatter according to user preferences (URL query variable, Accept request header)
	 */
	protected QueryResultFormatter getFormatter() {
		//return new TriGFormatter(); 
		return new HTMLFormatter();
	}
	
	/**
	 * TODO: only temporary.
	 */
	protected AggregationSpec getAggregationSpec(Form form) {
		AggregationSpec aggregationSpec = new AggregationSpec();
		Map<String, EnumAggregationType> propertyAggregations = new TreeMap<String, EnumAggregationType>();
		Map<String, Boolean> propertyMultivalue = new TreeMap<String, Boolean>();

		String defaultAggregation = form.getFirstValue("aggregation");
		if (defaultAggregation != null && !defaultAggregation.isEmpty()) {
			aggregationSpec.setDefaultAggregation(EnumAggregationType.valueOf(defaultAggregation));
		}
		String defaultMultivalue = form.getFirstValue("multivalue");
		if (defaultMultivalue != null && !defaultMultivalue.isEmpty()) {
			aggregationSpec.setDefaultMultivalue(Boolean.valueOf(defaultMultivalue));
		}
		String errorStrategy = form.getFirstValue("es");
		if (errorStrategy != null && !errorStrategy.isEmpty()) {
			aggregationSpec.setErrorStrategy(EnumAggregationErrorStrategy.valueOf(errorStrategy));
		}
		
		String[] propaNames = form.getValuesArray("propa-name");
		String[] propaValues = form.getValuesArray("propa-value");
		for (int i = 0; i < propaNames.length && i < propaValues.length; i++) {
			if (propaNames[i] != null && !propaNames[i].isEmpty()) {
				propertyAggregations.put(propaNames[i], EnumAggregationType.valueOf(propaValues[i]));
			}
		}
		aggregationSpec.setPropertyAggregations(propertyAggregations);
		
		String[] propmNames = form.getValuesArray("propm-name");
		String[] propmValues = form.getValuesArray("propm-value");
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