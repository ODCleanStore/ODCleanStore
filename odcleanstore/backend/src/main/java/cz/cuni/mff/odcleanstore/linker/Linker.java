package cz.cuni.mff.odcleanstore.linker;

import java.io.File;
import java.util.List;

import cz.cuni.mff.odcleanstore.linker.impl.DebugResult;
import cz.cuni.mff.odcleanstore.shared.SerializationLanguage;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.Transformer;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;

/**
 * Linking component.
 * Provides mechanisms for interlinking open data. 
 * Uses user-defined linkage-rules to generate the links.
 * Typically generates owl:sameAs links. Different link types can be also specified.
 * 
 * @author Tomas Soukup
 */
public interface Linker extends Transformer {
	public List<DebugResult> debugRules(String input, TransformationContext context)
			throws TransformerException;
	public List<DebugResult> debugRules(File inputFile, TransformationContext context, SerializationLanguage language) 
			throws TransformerException;
}
