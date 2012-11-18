package cz.cuni.mff.odcleanstore.datanormalization;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cz.cuni.mff.odcleanstore.data.TableVersion;
import cz.cuni.mff.odcleanstore.datanormalization.rules.DataNormalizationRule;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.Transformer;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;

public interface DataNormalizer extends Transformer {
	/**
	 * Triple that has either been deleted or inserted to a graph in one step of the normalization process
	 * @author Jakub Daniel
	 */
	public class TripleModification implements Serializable {
		private static final long serialVersionUID = 1L;

		String subject;
		String predicate;
		String object;

		public TripleModification(String s, String p, String o) {
			this.subject = s;
			this.predicate = p;
			this.object = o;
		}

		public String getSubject() {
			return subject;
		}

		public String getPredicate() {
			return predicate;
		}

		public String getObject() {
			return object;
		}
	}

	/**
	 * Collection of all the insertions and deletions that were applied by a certain rule
	 * @author Jakub Daniel
	 */
	public class RuleModification implements Serializable {
		private static final long serialVersionUID = 1L;

		private Collection<TripleModification> insertions = new HashSet<TripleModification>();
		private Collection<TripleModification> deletions = new HashSet<TripleModification>();

		public void addInsertion(String s, String p, String o) {
			insertions.add(new TripleModification(s, p, o));
		}

		public void addDeletion(String s, String p, String o) {
			deletions.add(new TripleModification(s, p, o));
		}

		public Collection<TripleModification> getInsertions() {
			return insertions;
		}

		public Collection<TripleModification> getDeletions() {
			return deletions;
		}
	}

	/**
	 * The collection of all modifications done to a graph (grouped by the rules that did them)
	 * @author Jakub Daniel
	 */
	public class GraphModification implements Serializable {
		private static final long serialVersionUID = 1L;

		private Map<DataNormalizationRule, RuleModification> modifications = new HashMap<DataNormalizationRule, RuleModification>();
		private String graphName;

		public void addInsertion (DataNormalizationRule rule, String s, String p, String o) {
			if (modifications.containsKey(rule)) {
				/**
				 * Extend an existing modification done by a certain rule
				 */
				modifications.get(rule).addInsertion(s, p, o);
			} else {
				/**
				 * Add new modification that corresponds to a certain rule
				 */
				RuleModification subModifications = new RuleModification();

				subModifications.addInsertion(s, p, o);

				modifications.put(rule, subModifications);
			}
		}

		public void addDeletion(DataNormalizationRule rule, String s, String p, String o) {
			if (modifications.containsKey(rule)) {
				/**
				 * Extend an existing modification done by a certain rule
				 */
				modifications.get(rule).addDeletion(s, p, o);
			} else {
				/**
				 * Add new modification that corresponds to a certain rule
				 */
				RuleModification subModifications = new RuleModification();

				subModifications.addDeletion(s, p, o);

				modifications.put(rule, subModifications);
			}
		}

		public Iterator<DataNormalizationRule> getRuleIterator() {
			return modifications.keySet().iterator();
		}

		public RuleModification getModificationsByRule(DataNormalizationRule rule) {
			return modifications.get(rule);
		}

		public String getGraphName() {
			return graphName;
		}

		public void setGraphName(String graphName) {
			this.graphName = graphName;
		}
	}

	List<GraphModification> debugRules (HashMap<String, String> graphs, TransformationContext context, TableVersion tableVersion)
			throws TransformerException;
}
