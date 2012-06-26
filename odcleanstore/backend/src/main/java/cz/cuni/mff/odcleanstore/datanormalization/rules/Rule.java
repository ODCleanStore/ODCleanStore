package cz.cuni.mff.odcleanstore.datanormalization.rules;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Vector;

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
					output = String.format("SPARQL INSERT DATA INTO <%%s> %s", value);
					break;

				case RULE_COMPONENT_DELETE:
					output = String.format("SPARQL DELETE FROM <%%s> %s", value);
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
	
	public String toString (String graph) {
		StringBuilder builder = new StringBuilder();
		
		Iterator<Component> i = components.iterator();
		
		while (i.hasNext()) {
			Component component = i.next();
			
			builder.append(String.format(component.getValue(), graph));
			builder.append(";");
		}
		
		return builder.toString();
	}
}
