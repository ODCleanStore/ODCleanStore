package cz.cuni.mff.odcleanstore.qualityassessment.rules;

import java.util.ArrayList;
import java.util.Collection;

import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;
import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;
import cz.cuni.mff.odcleanstore.qualityassessment.exceptions.QualityAssessmentException;

import java.sql.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RulesModel {
	private static final Logger LOG = LoggerFactory.getLogger(RulesModel.class);

	private SparqlEndpoint endpoint;
	
	public RulesModel (SparqlEndpoint endpoint) {
		this.endpoint = endpoint;
	}
	
	public Collection<Rule> getUnrestrictedRules() throws QualityAssessmentException {
		Collection<Rule> rules = new ArrayList<Rule>();
		
		VirtuosoConnectionWrapper connection = null;
		WrappedResultSet results = null;
		
		try {
			connection = VirtuosoConnectionWrapper.createConnection(endpoint);
			results = connection.executeSelect("SELECT * FROM DB.FRONTEND.EL_RULES");
			
			while (results.next()) {
				ResultSet result = results.getCurrentResultSet();
				
				Integer id = result.getInt("id");
				
				Blob filterBlob = result.getBlob("filter");
				String filter = new String(filterBlob.getBytes(1, (int)filterBlob.length()));
				
				Float coefficient = result.getFloat("coeficient"); //SPELLING MISTAKE IN THE DB TABLE
				
				Blob descriptionBlob = result.getBlob("description");
				String description = new String(descriptionBlob.getBytes(1, (int)descriptionBlob.length()));
				
				rules.add(new Rule(id, filter, coefficient, description));
			}
		} catch (ConnectionException e) {
			throw new QualityAssessmentException(e.getMessage());
		} catch (QueryException e) {
			throw new QualityAssessmentException(e.getMessage());
		} catch (SQLException e) {
			throw new QualityAssessmentException(e.getMessage());
		} finally {
			if (results != null) {
				results.closeQuietly();
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (ConnectionException e) {
					LOG.error("Rules Model connection not closed: " + e.getMessage());
				}
			}
		}
		
		return rules;
	}
	
	public Collection<Rule> getRulesForDomain (String domain) throws QualityAssessmentException {
		return getUnrestrictedRules();
	}
}
