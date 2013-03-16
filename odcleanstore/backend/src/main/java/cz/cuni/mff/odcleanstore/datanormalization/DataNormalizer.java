package cz.cuni.mff.odcleanstore.datanormalization;

import cz.cuni.mff.odcleanstore.data.TableVersion;
import cz.cuni.mff.odcleanstore.datanormalization.rules.DataNormalizationRule;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.Transformer;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Data normalization transformer interface
 *
 * It is used to modify input graph according to DataNormalizationRules.
 *
 * @author Jakub Daniel
 *
 */
public interface DataNormalizer extends Transformer {
    /**
     * Triple that has either been deleted or inserted to a graph in one step of the normalization process.
     * @author Jakub Daniel
     */
    public interface TripleModification extends Serializable {
        String getSubject();

        String getPredicate();

        String getObject();
    }

    /**
     * Collection of all the insertions and deletions that were applied by a certain rule.
     * @author Jakub Daniel
     */
    public interface RuleModification extends Serializable {
        void addInsertion(String s, String p, String o);

        void addDeletion(String s, String p, String o);

        Collection<TripleModification> getInsertions();

        Collection<TripleModification> getDeletions();
    }

    /**
     * The collection of all modifications done to a graph (grouped by the rules that did them).
     * @author Jakub Daniel
     */
    public interface GraphModification extends Serializable {
        void addInsertion(DataNormalizationRule rule, String s, String p, String o);

        void addDeletion(DataNormalizationRule rule, String s, String p, String o);

        Iterator<DataNormalizationRule> getRuleIterator();

        RuleModification getModificationsByRule(DataNormalizationRule rule);

        String getGraphName();

        void setGraphName(String graphName);
    }

    /**
     * Allows the user to examine what changes took place after each rule was executed.
     *
     * @param graphs (originalGraphName, temporaryGraphName) mapping of input graph names to database unique temporary
     *        identifiers.
     * @param context transformation context containing connection credentials
     * @param tableVersion specifies which version of rule tables will be used (committed / uncommitted)
     * @return modifications of the graph grouped by rules partitioned into insertions and deletions
     * @throws TransformerException
     */
    List<GraphModification> debugRules(HashMap<String, String> graphs, TransformationContext context, TableVersion tableVersion)
            throws TransformerException;
}
