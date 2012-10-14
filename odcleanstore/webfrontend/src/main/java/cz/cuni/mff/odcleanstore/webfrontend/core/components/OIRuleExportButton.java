package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.http.WebResponse;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.data.RDFprefix;
import cz.cuni.mff.odcleanstore.linker.impl.ConfigBuilder;
import cz.cuni.mff.odcleanstore.linker.rules.Output;
import cz.cuni.mff.odcleanstore.linker.rules.SilkRule;
import cz.cuni.mff.odcleanstore.shared.RDFPrefixesLoader;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRule;

public class OIRuleExportButton extends Button 
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(OIRuleImportButton.class);
	
	private OIRule rule;
	
	public OIRuleExportButton(OIRule rule, String compName)
	{
		super(compName);
		this.rule = rule;
		setDefaultFormProcessing(false);
	}
	
	@Override
	public void onSubmit()
	{	
		getRequestCycle().scheduleRequestHandlerAfterCurrent(new IRequestHandler() {

			public void respond(IRequestCycle requestCycle) {
				try 
				{
					SilkRule silkRule = transformToSilkRule(rule);
					List<RDFprefix> prefixes = RDFPrefixesLoader.loadPrefixes(
							ConfigLoader.getConfig().getBackendGroup().getCleanDBJDBCConnectionCredentials());
					String exportedRule = ConfigBuilder.createRuleXML(silkRule, prefixes);
					WebResponse response = (WebResponse) getResponse(); 
			        response.setAttachmentHeader("linkageRule" + (rule.getId() != null ? rule.getId() : "") + ".xml"); 
			        response.setContentType("text/xml");
			        response.write(exportedRule);
				} 
				catch (Exception e) 
				{
					logger.error(e.getMessage(), e);
					getSession().error("Rule export failed due to an unexpected error.");
				}
			}

			public void detach(IRequestCycle requestCycle) {};
		});
		 		 
	}
	
	private SilkRule transformToSilkRule(OIRule oiRule)
	{
		SilkRule rule = new SilkRule();
		rule.setFilterLimit(oiRule.getFilterLimit());
		rule.setFilterThreshold(oiRule.getFilterThreshold());
		rule.setId(oiRule.getId());
		rule.setLabel(oiRule.getLabel());
		rule.setLinkageRule(oiRule.getLinkageRule());
		rule.setLinkType(oiRule.getLinkType());
		rule.setOutputs(new ArrayList<Output>());
		rule.setSourceRestriction(oiRule.getSourceRestriction());
		rule.setTargetRestriction(oiRule.getTargetRestriction());
		return rule;
	}
}
