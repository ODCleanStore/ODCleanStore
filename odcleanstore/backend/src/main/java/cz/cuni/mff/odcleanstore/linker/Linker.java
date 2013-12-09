package cz.cuni.mff.odcleanstore.linker;

import cz.cuni.mff.odcleanstore.core.SerializationLanguage;
import cz.cuni.mff.odcleanstore.data.TableVersion;
import cz.cuni.mff.odcleanstore.linker.impl.DebugResult;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.Transformer;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;

import java.io.File;
import java.util.List;

/**
 * Linking component.
 * Provides mechanisms for interlinking open data.
 * Uses user-defined linkage-rules to generate the links.
 * Typically generates owl:sameAs links. Different link types can be also specified.
 *
 * @author Tomas Soukup
 */
public interface Linker extends Transformer {
    /**
     *
     * Debugs linkage rules.
     *
     * @param input RDF data, supported languages are RDF/XML, N3 and its subsets
     * @param context emulates real context used when running in a pipeline;
     *        transformer directory and database credentials are needed
     * @param tableVersion determines, whether to use uncommitted or committed version of rules
     * @return list of debug results, one for each rules
     * @throws TransformerException
     */
    List<DebugResult> debugRules(String input, TransformationContext context, TableVersion tableVersion)
            throws TransformerException;

    /**
     * Debugs linkage rules.
     *
     * @param inputFile input RDF data, file is passed to Silk without any processing
     * @param context emulates real context used when running in a pipeline;
     *        transformer directory and database credentials are needed
     * @param tableVersion determines, whether to use uncommitted or committed version of rules
     * @param language format of the input data, supported languages are RDF/XML, N3 and its subsets
     * @return list of debug results, one for each rules
     * @throws TransformerException
     */
    List<DebugResult> debugRules(File inputFile, TransformationContext context, TableVersion tableVersion,
            SerializationLanguage language) throws TransformerException;
}
