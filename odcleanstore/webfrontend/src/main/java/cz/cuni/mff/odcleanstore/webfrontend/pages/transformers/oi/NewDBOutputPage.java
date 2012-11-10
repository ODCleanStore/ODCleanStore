package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.MinimumValidator;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIOutput;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIOutputType;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.LimitedEditingForm;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIOutputDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIOutputTypeDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LimitedEditingPage;

/**
 * Add-a-new-DB-output-to-an-oi-rule page component.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
@AuthorizeInstantiation({ Role.PIC })
public class NewDBOutputPage extends LimitedEditingPage 
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(NewDBOutputPage.class);
	
	private OIOutputDao oiOutputDao;
	private OIOutputTypeDao oiOutputTypeDao;
	
	/**
	 * 
	 * @param ruleId
	 */
	public NewDBOutputPage(final Integer ruleId) 
	{
		super(
			"Home > Backend > Linker > Groups > Rules > DB Outputs > New", 
			"Add a new database Output",
			OIRuleDao.class,
			ruleId
		);
		
		// prepare DAO objects
		//
		oiOutputDao = daoLookupFactory.getDao(OIOutputDao.class, isEditable());
		oiOutputTypeDao = daoLookupFactory.getDao(OIOutputTypeDao.class);
		
		// register page components
		//
		addHelpWindow(new DBOutputHelpPanel("content"));
		
		add(
			new RedirectWithParamButton(
				OIRuleDetailPage.class,
				ruleId, 
				"showOIRuleDetail"
			)
		);
		
		addNewDBOutputForm(ruleId);
	}

	/**
	 * 
	 * @param ruleId
	 */
	private void addNewDBOutputForm(final Integer ruleId)
	{
		IModel<OIOutput> formModel = new CompoundPropertyModel<OIOutput>(new OIOutput());
		
		Form<OIOutput> form = new LimitedEditingForm<OIOutput>("newDBOutputForm", formModel, isEditable())
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmitImpl()
			{
				OIOutput output = getModelObject();
				
				output.setRuleId(ruleId);
				
				OIOutputType outputType = oiOutputTypeDao.loadBy("label", OIOutputType.DB_OUTPUT_LABEL);
				output.setOutputTypeId(outputType.getId());
				
				
				try {
					oiOutputDao.save(output);
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
						"The output could not be registered due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The output was successfuly registered.");
				setResponsePage(new OIRuleDetailPage(ruleId));
			}
		};
		
		form.add(createConfidenceTextfield("minConfidence"));
		form.add(createConfidenceTextfield("maxConfidence"));
		
		add(form);
	}
	
	/**
	 * 
	 * @return
	 */
	private TextField<String> createConfidenceTextfield(String compName)
	{
		TextField<String> textfield = createTextfield(compName, false);
		textfield.add(new MinimumValidator<BigDecimal>(BigDecimal.ZERO));
		return textfield;
	}
}
