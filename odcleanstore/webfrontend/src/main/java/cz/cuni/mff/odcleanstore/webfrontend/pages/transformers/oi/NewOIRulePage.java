package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.validation.validator.RangeValidator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import cz.cuni.mff.odcleanstore.linker.impl.ConfigBuilder;
import cz.cuni.mff.odcleanstore.linker.rules.SilkRule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRule;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIRulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LimitedEditingPage;

@AuthorizeInstantiation({ Role.PIC })
public class NewOIRulePage extends LimitedEditingPage 
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(NewOIRulePage.class);
	
	private static final String DEFAULT_LINK_TYPE = "owl:sameAs";
	
	private OIRuleDao oiRuleDao;
	
	public NewOIRulePage(final Integer groupId) 
	{
		super(
			"Home > Backend > OI > Groups > Rules > New", 
			"Add a new OI rule",
			OIRulesGroupDao.class,
			groupId
		);
		
		checkUnathorizedInstantiation();
		
		// prepare DAO objects
		//
		oiRuleDao = daoLookupFactory.getDao(OIRuleDao.class, isEditable());
		
		// register page components
		//
		addHelpWindow(new OIRuleHelpPanel("content"));
		
		add(
			new RedirectWithParamButton(
				OIGroupDetailPage.class,
				groupId, 
				"manageGroupRules"
			)
		);
		
		addNewOIRuleForm(groupId);
	}

	private void addNewOIRuleForm(final Integer groupId)
	{
		IModel<OIRule> formModel = new CompoundPropertyModel<OIRule>(new OIRule());
		
		Form<OIRule> form = new Form<OIRule>("newOIRuleForm", formModel)
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmit()
			{
				OIRule rule = getModelObject();
				rule.setGroupId(groupId);
				
				int insertId;
				try {
					insertId = oiRuleDao.saveAndGetKey(rule);
				}
				catch (DaoException ex)
				{
					logger.error(ex.getMessage(), ex);
					getSession().error(ex.getMessage());
					return;
				}
				catch (Exception ex)
				{
					logger.error(ex.getMessage(), ex);
					getSession().error(
						"The rule could not be registered due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The rule was successfuly registered.");
				setResponsePage(new OIRuleDetailPage(insertId));
			}
		};
		
		form.add(createTextfield("label"));
		form.add(createTextfield("linkType"));
		formModel.getObject().setLinkType(DEFAULT_LINK_TYPE);
		form.add(createTextfield("sourceRestriction", false));
		form.add(createTextfield("targetRestriction", false));
		form.add(createTextarea("linkageRule"));
		form.add(createFilterThresholdTextfield());
		form.add(createFilterLimitTextfield());
		
		form.setMultiPart(true);
		FileUploadField fileUpload = new FileUploadField("fileUpload", new ListModel<FileUpload>(new ArrayList<FileUpload>()));
		form.add(fileUpload);
		form.add(new ImportButton(fileUpload, form, "import"));
		
		add(form);
	}
	
	/**
	 * 
	 * @return
	 */
	private TextField<String> createFilterThresholdTextfield()
	{
		TextField<String> textfield = createTextfield("filterThreshold", false);
		textfield.add(new RangeValidator<BigDecimal>(new BigDecimal(0), new BigDecimal(Double.MAX_VALUE)));
		return textfield;
	}
	
	/**
	 * 
	 * @return
	 */
	private TextField<String> createFilterLimitTextfield()
	{
		TextField<String> textfield = createTextfield("filterLimit", false);
		textfield.add(new RangeValidator<Integer>(1, Integer.MAX_VALUE));
		return textfield;
	}
	
	private class ImportButton extends Button 
	{
		private static final long serialVersionUID = 1L;
		
		private FileUploadField fileUpload;
		private Form<OIRule> form;
		
		public ImportButton(FileUploadField fileUpload, Form<OIRule> form, String compName)
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
						silkRule.getLinkType(),
						silkRule.getSourceRestriction(),
						silkRule.getTargetRestriction(),
						silkRule.getLinkageRule(),
						silkRule.getFilterThreshold(),
						silkRule.getFilterLimit()
					);
		}
	}
}
