package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

import java.io.Serializable;

/**
 * An abstract parent of all compilers of DN template rules.
 * 
 * @author Dušan Rychnovský
 *
 * @param <BO> DN template type
 */
public abstract class DNTemplateInstanceCompiler<BO extends DNTemplateInstance>
	implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * Compiles the given template instance into a raw DN rule.
	 * 
	 * @param instance
	 * @return
	 */
	public abstract CompiledDNRule compile(BO instance);
}
