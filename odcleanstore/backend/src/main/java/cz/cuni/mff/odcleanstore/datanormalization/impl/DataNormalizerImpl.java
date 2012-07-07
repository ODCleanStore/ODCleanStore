package cz.cuni.mff.odcleanstore.datanormalization.impl;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.connection.EnumLogLevel;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;
import cz.cuni.mff.odcleanstore.datanormalization.DataNormalizer;
import cz.cuni.mff.odcleanstore.datanormalization.exceptions.DataNormalizationException;
import cz.cuni.mff.odcleanstore.datanormalization.rules.Rule;
import cz.cuni.mff.odcleanstore.datanormalization.rules.Rule.EnumRuleComponentType;
import cz.cuni.mff.odcleanstore.transformer.EnumTransformationType;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraphException;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;

public class DataNormalizerImpl implements DataNormalizer {
	
	public static void main(String[] args) {

		try {
			for (int i = 1; i < 2 && i < 1844; ++i) {
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
		} catch (Exception e) {
			System.err.println("DNMain: " + e.getMessage());
		}
	}
	
	private static final String selectFormat = "SPARQL SELECT %s FROM <%s> WHERE %s";
	private static final String insertFormat = "SPARQL INSERT DATA INTO <%s> {%s}";
	private static final String deleteFormat = "SPARQL DELETE DATA FROM <%s> {%s}";
	
	private static final Logger LOG = LoggerFactory.getLogger(DataNormalizerImpl.class);
	
	private TransformedGraph inputGraph;
	private TransformationContext context;
	
	private Collection<Rule> rules;

	/**
	 * Connection to dirty database (needed in all cases to work on a new graph or a copy of an existing one)
	 */
	private VirtuosoConnectionWrapper dirtyConnection;

	private VirtuosoConnectionWrapper getDirtyConnection () throws ConnectionException {
        if (dirtyConnection == null) {
        	dirtyConnection = VirtuosoConnectionWrapper.createConnection(context.getDirtyDatabaseCredentials());
       	}
		return dirtyConnection;
	}

	private void closeDirtyConnection() {
		try {
			if (dirtyConnection != null) {
				dirtyConnection.close();
			}
		} catch (ConnectionException e) {
		} finally {
			dirtyConnection = null;
		}
	}

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
		} finally {
			closeDirtyConnection();
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
				EnumRuleComponentType.RULE_COMPONENT_INSERT, "<http://opendata.cz> <http://opendata.cz> \"bullshit\"", null, null,
				EnumRuleComponentType.RULE_COMPONENT_INSERT, "?a ?b ?d", "?a ?b replace(str(?c), \"u\", \"*\") AS ?d", "{?a ?b ?c. FILTER (?c = \"bullshit\")}"
				));
	}

	private void applyRules () throws DataNormalizationException {
		try {
			getDirtyConnection();
			
			Iterator<Rule> i = rules.iterator();
			
			while (i.hasNext()) {
				Rule rule = i.next();

				getDirtyConnection().adjustTransactionLevel(EnumLogLevel.TRANSACTION_LEVEL, false);
				
				performRule(rule);
				
				getDirtyConnection().commit();
			}
		} catch (ConnectionException e) {
			throw new DataNormalizationException(e.getMessage());
		} catch (QueryException e) {
			throw new DataNormalizationException(e.getMessage());
		} catch (SQLException e) {
			throw new DataNormalizationException(e.getMessage());
		}
	}
	
	private void performRule (Rule rule) throws DataNormalizationException, ConnectionException, QueryException, SQLException {
		Rule.Component[] components = rule.getComponents();

		for (int j = 0; j < components.length; ++j) {
			performRuleComponent(components[j]);
		}
	}

	private void performRuleComponent (Rule.Component component) throws DataNormalizationException, ConnectionException, QueryException, SQLException {
		if (component.getVariables() == null && component.getWhere() == null) {
			performUpdate(component, null);
		} else {
			WrappedResultSet results = getDirtyConnection().executeSelect(
				String.format(selectFormat, component.getVariables(), inputGraph.getGraphName(), component.getWhere()));
			
			while (results.next()) {
				performUpdate(component, results.getCurrentResultSet());
			}
		}
	}
	private void performUpdate (Rule.Component component, ResultSet result) throws DataNormalizationException, ConnectionException, QueryException {
		switch (component.getType()) {
		case RULE_COMPONENT_INSERT:
			String insert = String.format(insertFormat, inputGraph.getGraphName(), bindVariables(component.getTriples(), result));
			
			//System.err.println(insert);
			
			getDirtyConnection().execute(insert);
			break;

		case RULE_COMPONENT_DELETE:
			String delete = String.format(deleteFormat, inputGraph.getGraphName(), bindVariables(component.getTriples(), result));
			
			//System.err.println(delete);
			
			getDirtyConnection().execute(delete);
			break;
		}
	}
	
	private static String bindVariables (String triples, ResultSet result) throws DataNormalizationException {
		
		String bound = "";
		
		while (triples.length() > 0) {
			int variable = findVariable(triples);
			
			if (variable < 0) {
				bound += triples;

				triples = "";
			} else {
				int length = findNonVariable(triples.substring(variable));
				
				String name = triples.substring(variable, variable + length);
				
				try {
					String replacement;
					
					try {
						replacement = "<" + new URI(result.getString(name)).toString() + ">";
					} catch (URISyntaxException e) {
						replacement = "\"" + result.getString(name) + "\"";
					}

					bound += triples.substring(0, variable - 1);
					triples = triples.substring(variable + length);
					bound += replacement;
				} catch (SQLException e) {
					throw new DataNormalizationException ("Unbound variable '" + name + "'");
				}
			}
		}
		
		return bound;
	}
	
	private static int findVariable (String triples) {
		boolean quoted = false;
		boolean url = false;
		
		for (int i = 0; i < triples.length(); ++i) {
			switch (triples.charAt(i)) {
			case '"':
				if (!isEscaped(triples, i)) quoted = !quoted;
				break;
			case '<':
				if (!quoted) url = true;
				break;
			case '>':
				if (!quoted) url = false;
				break;
			case '?':
				if (!quoted && !url) return i + 1;
			}
		}
		
		return -1;
	}
	
	private static int findNonVariable (String triples) {
		int i = 0;
		
		while (triples.length() > i &&
				('a' <= triples.charAt(i) && triples.charAt(i) <= 'z' ||
				'A' <= triples.charAt(i) && triples.charAt(i) <= 'Z' ||
				'0' <= triples.charAt(i) && triples.charAt(i) <= '9' ||
				triples.charAt(i) == '_')) {
		
			++i;
		}
		
		return i;
	}
	
	private static boolean isEscaped (String source, int position) {
		int escapes = 0;
		
		while (position - 1 >= 0) {
			
			if (source.charAt(position - 1) == '\\') ++escapes;
			else break;
			
			--position;
		}
		
		return escapes % 2 == 1;
	}

	@Override
	public void shutdown() throws TransformerException {
	}
}
