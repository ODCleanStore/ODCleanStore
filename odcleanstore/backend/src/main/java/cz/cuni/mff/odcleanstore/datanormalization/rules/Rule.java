package cz.cuni.mff.odcleanstore.datanormalization.rules;

import java.util.Vector;

import cz.cuni.mff.odcleanstore.datanormalization.exceptions.DataNormalizationException;

public class Rule {
	public enum EnumRuleComponentType {
		RULE_COMPONENT_INSERT {
			public String toString() {
				return "INSERT";
			}
		},
		RULE_COMPONENT_DELETE {
			public String toString() {
				return "DELETE";
			}
		}
	}
	
	public class Component {
		
		public Component(EnumRuleComponentType type, String modification, String description) {
			this.type = type;
			this.modification = modification;
			this.description = description;
		}
		
		private EnumRuleComponentType type;
		private String modification;
		private String description;
		
		public EnumRuleComponentType getType() {
			return type;
		}
		
		public String getModification() {
			return modification;
		}
		
		public String getDescription() {
			return description;
		}

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
	Integer groupId;
	String description;
	Vector<Component> components = new Vector<Component>();
	
	public Rule(Integer id, Integer groupId, String description, String... components) throws DataNormalizationException {
		this.id = id;
		this.groupId = groupId;
		this.description = description;

		if (components.length % 3 != 0) throw new DataNormalizationException("Incomplete rule initialization list");
		
		for (int i = 0; i < components.length; i += 3) {
			addComponent(components[i], components[i + 1], components[i + 2]);
		}
	}
	
	public Integer getId() {
		return id;
	}
	
	public Integer getGroupId() {
		return groupId;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Component[] getComponents() {
		return components.toArray(new Component[this.components.size()]);
	}
	
	public String[] getComponents(String graph) {
		String[] componentStrings = new String[this.components.size()];
		
		Component[] components = this.components.toArray(new Component[this.components.size()]);
		
		for (int i = 0; i < components.length; ++i) {
			componentStrings[i] = components[i].toString(graph);
		}
		
		return componentStrings;
	}
	
	public void addComponent(String type, String modification, String description) throws DataNormalizationException {
		if (type.equals("INSERT")) {
			addComponent(EnumRuleComponentType.RULE_COMPONENT_INSERT, modification, description);
		} else if (type.equals("DELETE")) {
			addComponent(EnumRuleComponentType.RULE_COMPONENT_DELETE, modification, description);
		} else {
			throw new DataNormalizationException("Unknown Data Normalization Rule type");
		}
	}
	
	public void addComponent(EnumRuleComponentType type, String modification, String description) {
		components.add(new Component(type, modification, description));
	}
}
