package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;

import cz.cuni.mff.odcleanstore.linker.impl.ConfigBuilder;
import cz.cuni.mff.odcleanstore.linker.rules.SilkRule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRule;

/**
 * Button for importing OI rules.
 * 
 * @author Tomas Soukup
 */
public class OIRuleImportButton extends Button 
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(OIRuleImportButton.class);
	
	private FileUploadField fileUpload;
	private Form<OIRule> form;
	
	/**
	 * @param fileUpload
	 * @param form
	 * @param compName
	 */
	public OIRuleImportButton(FileUploadField fileUpload, Form<OIRule> form, String compName)
	{
		super(compName);
		this.fileUpload = fileUpload;
		this.form = form;
		setDefaultFormProcessing(false);
	}
	
	@Override
	public void onSubmit()
	{
		final FileUpload uploadedFile = fileUpload.getFileUpload();
		if (uploadedFile != null)
		{
			try 
			{
				SilkRule silkRule = ConfigBuilder.parseRule(uploadedFile.getInputStream());
				form.setModelObject(transformRule(silkRule));	
				form.modelChanged();
			}
			catch (Exception e) 
			{
				logger.error(e.getMessage(), e);
				getSession().error("Failed to parse the provided file.");
			}
		}
	}
	
	private OIRule transformRule(SilkRule silkRule)
	{
		return  new OIRule(
					null,
					null,
					silkRule.getLabel(),
					null,
					silkRule.getLinkType(),
					silkRule.getSourceRestriction(),
					silkRule.getTargetRestriction(),
					silkRule.getLinkageRule(),
					silkRule.getFilterThreshold(),
					silkRule.getFilterLimit()
				);
	}
}
