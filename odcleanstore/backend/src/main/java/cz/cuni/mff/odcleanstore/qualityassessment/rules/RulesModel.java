package cz.cuni.mff.odcleanstore.qualityassessment.rules;

import java.util.ArrayList;
import java.util.Collection;

import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;

import java.sql.*;

public class RulesModel {
	private SparqlEndpoint endpoint;
	
	public RulesModel (SparqlEndpoint endpoint) {
		this.endpoint = endpoint;
	}
	
	public Collection<Rule> getAllRules() {
		Collection<Rule> rules = new ArrayList<Rule>();
		
		try {
			Class.forName("virtuoso.jdbc3.Driver");
			
			Connection con = DriverManager.getConnection(this.endpoint.getUri(), this.endpoint.getUsername(), this.endpoint.getPassword());
			
			Statement st = con.createStatement();
			
			ResultSet rs;
			
			rs = st.executeQuery("SELECT * FROM DB.FRONTEND.EL_RULES");
			
			while (rs.next()) {
				Integer id = rs.getInt("id");
				
				Blob filter_blob = rs.getBlob("filter");
				String filter = new String(filter_blob.getBytes(1, (int)filter_blob.length()));
				
				Float coefficient = rs.getFloat("coeficient"); //SPELLING MISTAKE IN THE DB TABLE
				
				Blob description_blob = rs.getBlob("description");
				String description = new String(description_blob.getBytes(1, (int)description_blob.length()));
				
				rules.add(new Rule(id, filter, coefficient, description));

				//System.out.println(id + "\n" + filter + "\n" + coefficient + "\n" + description);
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
		
		return rules;
	}
}
