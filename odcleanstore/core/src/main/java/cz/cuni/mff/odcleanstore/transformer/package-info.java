/**
 * Interface definitions for named graph transformers.
 * A transformer is a Java class implementing interface {@link cz.cuni.mff.odcleanstore.transformer.Transformer}.
 * When a new graph is sent to ODCleanStore, the Engine component runs them through a pipeline of transformers.
 * Each transformer may modify the processed named graph (e.g. normalize values, deal with blank nodes) or attach
 * a new named graph (e.g. quality assessment results, links to the data in the clean database or links to other
 * datasets). Custom transformers in a .jar archive can be easily plugged in to an arbitrary place in
 * the processing pipeline through Engine configuration.
 */
package cz.cuni.mff.odcleanstore.transformer;