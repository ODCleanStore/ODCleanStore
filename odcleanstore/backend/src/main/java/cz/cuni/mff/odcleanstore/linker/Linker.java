package cz.cuni.mff.odcleanstore.linker;

import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
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
	/**
	 * Generates links between all entities in clean database. 
	 * Uses The Silk Link Discovery Framework.
	 * Performs following steps:
	 * <ul><li>Loads valid linkage rules from database.</li>
	 * <li>Creates configuration XML file in Silk-LSL.</li>
	 * <li>Calls Silk engine providing the created configuration</li></ul>
	 * 
	 * @param context provides linkage rules IDs in TransformerConfiguration
	 * 		and directory for storing temporary files
	 */
	public void linkCleanDatabase(TransformationContext context);
	
	public void linkByConfigFiles(TransformationContext context);
}
