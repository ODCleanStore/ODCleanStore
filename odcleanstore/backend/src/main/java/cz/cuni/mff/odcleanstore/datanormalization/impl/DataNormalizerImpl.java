package cz.cuni.mff.odcleanstore.datanormalization.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import virtuoso.jena.driver.VirtGraph;

import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.sparql.lang.UpdateParserFactory;
import com.hp.hpl.jena.update.UpdateAction;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateRequest;

import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.datanormalization.DataNormalizer;
import cz.cuni.mff.odcleanstore.datanormalization.exceptions.DataNormalizationException;
import cz.cuni.mff.odcleanstore.datanormalization.rules.Rule;
import cz.cuni.mff.odcleanstore.datanormalization.rules.Rule.EnumRuleComponentType;
import cz.cuni.mff.odcleanstore.transformer.EnumTransformationType;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraphException;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;

public class DataNormalizerImpl implements DataNormalizer {
	
	public static void main(String[] args) {
		try {
			for (int i = 1; i < 20 && i < 1844; ++i) {
				final int id = i;

				new DataNormalizerImpl().transformNewGraph(new TransformedGraph() {

					@Override
					public String getGraphName() {
						// TODO Auto-generated method stub
						return "http://opendata.cz/data/namedGraph/" + id;
					}

					@Override
					public String getGraphId() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public String getMetadataGraphName() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public Collection<String> getAttachedGraphNames() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public void addAttachedGraph(String attachedGraphName)
							throws TransformedGraphException {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void deleteGraph() throws TransformedGraphException {
						// TODO Auto-generated method stub
						
					}

					@Override
					public boolean isDeleted() {
						// TODO Auto-generated method stub
						return false;
					}
					
				}, new TransformationContext() {

					@Override
					public JDBCConnectionCredentials getDirtyDatabaseCredentials() {
						// TODO Auto-generated method stub
						return new JDBCConnectionCredentials("jdbc:virtuoso://localhost:1112/UID=dba/PWD=dba", "dba", "dba");
					}

					@Override
					public JDBCConnectionCredentials getCleanDatabaseCredentials() {
						// TODO Auto-generated method stub
						return new JDBCConnectionCredentials("jdbc:virtuoso://localhost:1111/UID=dba/PWD=dba", "dba", "dba");
					}

					@Override
					public String getTransformerConfiguration() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public File getTransformerDirectory() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public EnumTransformationType getTransformationType() {
						// TODO Auto-generated method stub
						return null;
					}
					
				});
			}
		} catch (TransformerException e) {
			System.err.println("DNMain: " + e.getMessage());
		}
	}
	
	private static final Logger LOG = LoggerFactory.getLogger(DataNormalizerImpl.class);
	
	private TransformedGraph inputGraph;
	private TransformationContext context;
	
	private Collection<Rule> rules;

	@Override
	public void transformNewGraph(TransformedGraph inputGraph,
			TransformationContext context) throws TransformerException {
		this.inputGraph = inputGraph;
		this.context = context;
		
		try
		{
			loadRules();
			applyRules();
		} catch (DataNormalizationException e) {
			throw new TransformerException(e);
		}

		LOG.info(String.format("Data Normalization applied to graph %s", inputGraph.getGraphName()));
	}
	
	@Override
	public void transformExistingGraph(TransformedGraph inputGraph,
			TransformationContext context) throws TransformerException {
		throw new TransformerException("Data normalization is supposed to be applied to new graphs.");
	}
	
	private void loadRules () throws DataNormalizationException {
		rules = new ArrayList<Rule>();
		
		rules.add(new Rule(
				EnumRuleComponentType.RULE_COMPONENT_INSERT, "{<http://opendata.cz> <" + ODCS.score + "> ?score} WHERE {<http://opendata.cz> <" + ODCS.score + "> ?oldScore. BIND (replace(str(?oldScore), \"0\", \"1\") AS ?score)}"
				));
	}

	private void applyRules () throws DataNormalizationException {
		Iterator<Rule> i = rules.iterator();
		
		while (i.hasNext()) {
			Rule rule = i.next();

			String[] components = rule.getComponents();
			
			JDBCConnectionCredentials jdbc = context.getDirtyDatabaseCredentials();
			
			VirtGraph graph = new VirtGraph(inputGraph.getGraphName(),
					jdbc.getConnectionString(),
					jdbc.getUsername(),
					jdbc.getPassword());
			
			for (int j = 0; j < components.length; ++j) {
				UpdateRequest updateRequest = UpdateFactory.create(components[j], Syntax.syntaxSPARQL_11);
				
				System.err.println(components[j]);
				
				UpdateAction.execute(updateRequest, graph);
			}
		}
	}

	@Override
	public void shutdown() throws TransformerException {
	}
}
