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
	
	public class Component implements Entry<EnumRuleComponentType, String> {
		
		public Component (EnumRuleComponentType type, String value) {
			this.type = type;
			this.value = value;
		}
		
		private EnumRuleComponentType type;
		private String value;

		@Override
		public EnumRuleComponentType getKey() {
			return type;
		}

		@Override
		public String getValue() {
			String output = "";

			switch (type) {
				case RULE_COMPONENT_INSERT:
					output = "INSERT " + value;
					break;

				case RULE_COMPONENT_DELETE:
					output = "DELETE " + value;
					break;
			}
			
			return output;
		}

		@Override
		public String setValue(String value) {
			String oldValue = this.value;
			
			this.value = value;
			
			return oldValue;
		}
	}

	Vector<Component> components = new Vector<Component>();
	
	public Rule (Object... components) throws DataNormalizationException {
		if (components.length % 2 == 1) throw new DataNormalizationException("Incomplete rule initialization list");
		
		for (int i = 0; i < components.length; i += 2) {
			if (components[i] instanceof EnumRuleComponentType && components[i + 1] instanceof String) {
				EnumRuleComponentType type = (EnumRuleComponentType)components[i];
				String value = (String)components[i + 1];

				this.components.add(new Component(type, value));
			} else {
				throw new DataNormalizationException("Invalid rule initialization list");
			}
		}
	}
	
	public String[] getComponents () {
		String[] rule = new String[components.size()];
		
		for (int i = 0; i < components.size(); ++i) {
			rule[i] = components.elementAt(i).getValue();
		}
		
		return rule;
	}
}
