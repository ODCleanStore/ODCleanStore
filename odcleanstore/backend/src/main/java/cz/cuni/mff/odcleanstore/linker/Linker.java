package cz.cuni.mff.odcleanstore.linker;

import cz.cuni.mff.odcleanstore.transformer.Transformer;

/**
 * Linking component.
 * Provides mechanisms for interlinking open data. 
 * Uses user-defined linkage-rules to generate the links.
 * Typically generates owl:sameAs links. Different link types can be also specified.
 * 
 * @author Tomas Soukup
 */
public interface Linker extends Transformer {
}
