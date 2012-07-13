package cz.cuni.mff.odcleanstore.datanormalization.rules;

import java.util.Vector;

import cz.cuni.mff.odcleanstore.datanormalization.exceptions.DataNormalizationException;

public class Rule {
	public enum EnumRuleComponentType {
		RULE_COMPONENT_INSERT,
		RULE_COMPONENT_DELETE
	}
	
	public class Component {
		
		public Component (EnumRuleComponentType type, String update) {
			this.type = type;
			this.update = update;
		}
		
		private EnumRuleComponentType type;
		private String update;

		public String toString (String graph) {
			String output = "<" + graph + "> " + update.replaceAll("GRAPH\\s*\\$\\$graph\\$\\$", "GRAPH <" + graph + ">");

			switch (type) {
			case RULE_COMPONENT_INSERT:
				output = "SPARQL INSERT INTO " + output;
				break;
			case RULE_COMPONENT_DELETE:
				output = "SPARQL DELETE FROM " + output;
				break;
			}
			
			return output;
		}
	}

	Integer id;
	Vector<Component> components = new Vector<Component>();
	
	public Rule (Integer id, Object... components) throws DataNormalizationException {
		this.id = id;

		if (components.length % 2 == 1) throw new DataNormalizationException("Incomplete rule initialization list");
		
		for (int i = 0; i < components.length; i += 2) {
			if (components[i] instanceof EnumRuleComponentType && components[i + 1] instanceof String) {

				EnumRuleComponentType type = (EnumRuleComponentType)components[i];
				
				String update = (String)components[i + 1];

				this.components.add(new Component(type, update));
			} else {
				throw new DataNormalizationException("Invalid rule initialization list");
			}
		}
	}
	
	public Integer getId () {
		return id;
	}
	
	public String[] getComponents (String graph) {
		String[] componentStrings = new String[this.components.size()];
		
		Component[] components = this.components.toArray(new Component[this.components.size()]);
		
		for (int i = 0; i < components.length; ++i) {
			componentStrings[i] = components[i].toString(graph);
		}
		
		return componentStrings;
	}
}
