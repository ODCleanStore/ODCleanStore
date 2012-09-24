package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.RangeValidator;

import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRule;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa.QARuleHelpPanel;

@AuthorizeInstantiation({ "PIC" })
public class EditOIRulePage extends FrontendPage 
{
	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger.getLogger(EditOIRulePage.class);
	
	private DaoForEntityWithSurrogateKey<OIRule> oiRuleDao;
	
	public EditOIRulePage(final Long ruleId) 
	{
		super(
			"Home > Backend > OI > Groups > Rules > Edit", 
			"Edit an OI rule"
		);
		
		// prepare DAO objects
		//
		oiRuleDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(OIRuleDao.class);
		
		// register page components
		//
		addHelpWindow(new OIRuleHelpPanel("content"));
		
		OIRule rule = oiRuleDao.load(ruleId);
		
		add(
			new RedirectWithParamButton(
				OIGroupDetailPage.class,
				rule.getGroupId(), 
				"manageGroupRules"
			)
		);
		
		addEditOIRuleForm(rule);
	}

	private void addEditOIRuleForm(final OIRule rule)
	{
		IModel formModel = new CompoundPropertyModel(rule);
		
		Form<OIRule> form = new Form<OIRule>("editOIRuleForm", formModel)
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmit()
			{
				OIRule rule = getModelObject();
				
				try {
					oiRuleDao.update(rule);
				}
				catch (DaoException ex)
				{
					getSession().error(ex.getMessage());
					return;
				}
				catch (Exception ex)
				{
					logger.error(ex.getMessage());
					
					getSession().error(
						"The rule could not be updated due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The rule was successfuly updated.");
				setResponsePage(new OIGroupDetailPage(rule.getGroupId()));
			}
		};
		
		form.add(createTextfield("label"));
		form.add(createTextfield("linkType"));
		form.add(createTextfield("sourceRestriction", false));
		form.add(createTextfield("targetRestriction", false));
		form.add(createTextarea("linkageRule"));
		form.add(createFilterThresholdTextfield());
		form.add(createFilterLimitTextfield());
		
		add(form);
	}
	
	/**
	 * 
	 * @return
	 */
	private TextField<String> createFilterThresholdTextfield()
	{
		TextField<String> textfield = createTextfield("filterThreshold", false);
		textfield.add(new RangeValidator<Double>(0.0, Double.MAX_VALUE));
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
}
