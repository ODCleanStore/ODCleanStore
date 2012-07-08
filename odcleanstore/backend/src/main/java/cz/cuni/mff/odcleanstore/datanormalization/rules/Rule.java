package cz.cuni.mff.odcleanstore.datanormalization.rules;

import java.util.Map.Entry;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;

import cz.cuni.mff.odcleanstore.datanormalization.exceptions.DataNormalizationException;

public class Rule {
	public enum EnumRuleComponentType {
		RULE_COMPONENT_INSERT,
		RULE_COMPONENT_DELETE
	}
	
	public class Component {
		
		public Component (EnumRuleComponentType type, String triples, String variables, String where) {
			this.type = type;
			this.triples = triples;
			this.variables = variables;
			this.where = where;
		}
		
		private EnumRuleComponentType type;
		private String triples;
		private String variables;
		private String where;

		public EnumRuleComponentType getType () {
			return type;
		}

		public String getTriples () {
			return triples;
		}
		
		public String getVariables () {
			return variables;
		}
		
		public String getWhere () {
			return where;
		}
	}

	Vector<Component> components = new Vector<Component>();
	
	public Rule (Object... components) throws DataNormalizationException {
		if (components.length % 2 == 1) throw new DataNormalizationException("Incomplete rule initialization list");
		
		for (int i = 0; i < components.length; i += 4) {
			if (components[i] instanceof EnumRuleComponentType &&
					components[i + 1] instanceof String &&
					(components[i + 2] instanceof String || components[i + 2] == null) &&
					(components[i + 2] instanceof String || components[i + 2] == null)) {

				EnumRuleComponentType type = (EnumRuleComponentType)components[i];
				
				String triples = (String)components[i + 1];
				String variables = (String)components[i + 2];
				String where = (String)components[i + 3];

				this.components.add(new Component(type, triples, variables, where));
			} else {
				throw new DataNormalizationException("Invalid rule initialization list");
			}
		}
	}
	
	public Component[] getComponents () {
		
		return components.toArray(new Component[components.size()]);
	}
}
