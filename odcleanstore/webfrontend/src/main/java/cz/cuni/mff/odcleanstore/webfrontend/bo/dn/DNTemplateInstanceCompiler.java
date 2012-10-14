package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

import java.io.Serializable;

public abstract class DNTemplateInstanceCompiler<BO extends DNTemplateInstance>
	implements Serializable
{
	private static final long serialVersionUID = 1L;

	public abstract CompiledDNRule compile(BO instance);
}
