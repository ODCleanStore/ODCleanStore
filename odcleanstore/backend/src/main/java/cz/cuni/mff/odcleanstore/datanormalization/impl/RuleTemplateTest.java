package cz.cuni.mff.odcleanstore.datanormalization.impl;

import java.sql.Blob;
import java.util.HashMap;

import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.datanormalization.rules.Rule;

public class RuleTemplateTest {
	public static void main(String[] args) {
		HashMap<Integer, String> fieldValueMap = new HashMap<Integer, String>();
		
		/* INPUT */
		fieldValueMap.put(0, "http://purl.org/dc/terms/title");
		fieldValueMap.put(1, "^([^ ]*) (.*)$");
		fieldValueMap.put(2, "$1");

		/* TRANSFORMATION LOGIC */
		String selectRecipes = "SELECT recipes.id AS id, types.label AS type FROM " +
				"DB.ODCLEANSTORE.DN_RULE_TEMPLATE_RECIPES AS recipes JOIN " +
				"DB.ODCLEANSTORE.DN_RULE_TEMPLATES AS templates ON recipes.templateId = templates.id JOIN " +
				"DB.ODCLEANSTORE.DN_RULE_COMPONENT_TYPES AS types ON recipes.typeId = types.id " +
				"WHERE templates.label = 'replace'";
		
		String selectComponentRecipes = "SELECT fieldTypes.label AS type, fields.id AS id, constants.value AS constant FROM " +
				"DB.ODCLEANSTORE.DN_RULE_TEMPLATE_COMPONENT_RECIPES AS componentRecipes JOIN " +
				"DB.ODCLEANSTORE.DN_RULE_TEMPLATE_FIELDS AS fields ON componentRecipes.fieldId = fields.id JOIN " +
				"DB.ODCLEANSTORE.DN_RULE_TEMPLATE_FIELD_TYPES AS fieldTypes ON fields.typeId = fieldTypes.id LEFT JOIN " +
				"DB.ODCLEANSTORE.DN_RULE_TEMPLATE_CONSTANTS AS constants ON constants.fieldId = fields.id " +
				"WHERE recipeId = ";

		try {
			VirtuosoConnectionWrapper connection = VirtuosoConnectionWrapper.createConnection(new JDBCConnectionCredentials("jdbc:virtuoso://localhost:1111/UID=dba/PWD=dba", "dba", "dba"));
			
			WrappedResultSet recipes = connection.executeSelect(selectRecipes);
			
			Rule rule = new Rule(null, 1, "Instance of replacement rule");
			
			while (recipes.next()) {
				Integer recipeId = recipes.getInt("id");
				String componentType = recipes.getString("type");
				
				WrappedResultSet componentRecipes = connection.executeSelect(selectComponentRecipes + recipeId);
				
				String modification = "";
				
				while (componentRecipes.next()) {
					Blob constantBlob = componentRecipes.getCurrentResultSet().getBlob("constant");
					String constant = null;
					
					if (constantBlob != null) {
						constant = new String(constantBlob.getBytes(1, (int)constantBlob.length()));
					}

					String fieldType = componentRecipes.getString("type");
					Integer fieldId = componentRecipes.getInt("id");

					if (fieldType.equals("constant")) {
						modification += constant;
					} else {
						modification += fieldValueMap.get(fieldId);
					}
				}

				System.err.println(componentType + " " + modification);
				rule.addComponent(componentType, modification, "");
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
