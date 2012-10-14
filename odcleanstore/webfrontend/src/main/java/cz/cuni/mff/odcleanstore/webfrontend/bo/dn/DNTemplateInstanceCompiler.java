package cz.cuni.mff.odcleanstore.webfrontend.bo.dn;

public abstract class DNTemplateInstanceCompiler<BO extends DNTemplateInstance>
{
	public abstract CompiledDNRule compile(BO instance);
}
