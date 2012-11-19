package cz.cuni.mff.odcleanstore.datanormalization;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
	public interface TripleModification extends Serializable {
		public String getSubject();
		public String getPredicate();
		public String getObject();
	}

	/**
	 * Collection of all the insertions and deletions that were applied by a certain rule
	 * @author Jakub Daniel
	 */
	public interface RuleModification extends Serializable {
		public void addInsertion(String s, String p, String o);
		public void addDeletion(String s, String p, String o);
		public Collection<TripleModification> getInsertions();
		public Collection<TripleModification> getDeletions();
	}

	/**
	 * The collection of all modifications done to a graph (grouped by the rules that did them)
	 * @author Jakub Daniel
	 */
	public interface GraphModification extends Serializable {
		public void addInsertion (DataNormalizationRule rule, String s, String p, String o);
		public void addDeletion(DataNormalizationRule rule, String s, String p, String o);
		public Iterator<DataNormalizationRule> getRuleIterator();
		public RuleModification getModificationsByRule(DataNormalizationRule rule);
		public String getGraphName();
		public void setGraphName(String graphName);
	}

	List<GraphModification> debugRules (HashMap<String, String> graphs, TransformationContext context, TableVersion tableVersion)
			throws TransformerException;
}
