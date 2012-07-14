package cz.cuni.mff.odcleanstore.datanormalization.rules;

import java.util.Vector;

import cz.cuni.mff.odcleanstore.datanormalization.exceptions.DataNormalizationException;

public class Rule {
	public enum EnumRuleComponentType {
		RULE_COMPONENT_INSERT,
		RULE_COMPONENT_DELETE
	}
	
	public class Component {
		
		public Component(EnumRuleComponentType type, String modification) {
			this.type = type;
			this.modification = modification;
		}
		
		private EnumRuleComponentType type;
		private String modification;

		public String toString(String graph) {
			String output = "<" + graph + "> " + modification.replaceAll("GRAPH\\s*\\$\\$graph\\$\\$", "GRAPH <" + graph + ">");

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
	
	public Rule(Integer id) {
		this.id = id;
	}
	
	public Rule(Integer id, String... components) throws DataNormalizationException {
		this.id = id;

		if (components.length % 2 == 1) throw new DataNormalizationException("Incomplete rule initialization list");
		
		for (int i = 0; i < components.length; i += 2) {
			addComponent(components[i], components[i + 1]);
		}
	}
	
	public Integer getId() {
		return id;
	}
	
	public String[] getComponents(String graph) {
		String[] componentStrings = new String[this.components.size()];
		
		Component[] components = this.components.toArray(new Component[this.components.size()]);
		
		for (int i = 0; i < components.length; ++i) {
			componentStrings[i] = components[i].toString(graph);
		}
		
		return componentStrings;
	}
	
	public void addComponent(String type, String modification) throws DataNormalizationException {
		if (type.equals("INSERT")) {
			addComponent(EnumRuleComponentType.RULE_COMPONENT_INSERT, modification);
		} else if (type.equals("DELETE")) {
			addComponent(EnumRuleComponentType.RULE_COMPONENT_DELETE, modification);
		} else {
			throw new DataNormalizationException("Unknown Data Normalization Rule type");
		}
	}
	
	public void addComponent(EnumRuleComponentType type, String modification) {
		components.add(new Component(type, modification));
	}
}
