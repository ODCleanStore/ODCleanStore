package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRule;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.OIRuleExportButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.OIRuleImportButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIRulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LimitedEditingPage;

/**
 * Create-new-oi-rule page component.
 *  
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
@AuthorizeInstantiation({ Role.PIC })
public class NewOIRulePage extends LimitedEditingPage 
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(NewOIRulePage.class);
	
	private static final String DEFAULT_LINK_TYPE = "owl:sameAs";
	
	private OIRuleDao oiRuleDao;
	
	/**
	 * 
	 * @param groupId
	 */
	public NewOIRulePage(final Integer groupId) 
	{
		super(
			"Home > Backend > Linker > Groups > Rules > New", 
			"Add a new Linker rule",
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

	/**
	 * 
	 * @param groupId
	 */
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
		form.add(createTextarea("description", false));
		form.add(createTextfield("linkType"));
		formModel.getObject().setLinkType(DEFAULT_LINK_TYPE);
		form.add(createTextfield("sourceRestriction", false));
		form.add(createTextfield("targetRestriction", false));
		form.add(createTextarea("linkageRule"));
		form.add(createMinimumTextfield("filterThreshold", BigDecimal.ZERO));
		form.add(createMinimumTextfield("filterLimit", 1));
		
		form.setMultiPart(true);
		FileUploadField fileUpload = new FileUploadField("fileUpload", new ListModel<FileUpload>(new ArrayList<FileUpload>()));
		form.add(fileUpload);
		form.add(new OIRuleImportButton(fileUpload, form, "import"));
		form.add(new OIRuleExportButton(form.getModelObject(), "export"));
		
		add(form);
	}
}
