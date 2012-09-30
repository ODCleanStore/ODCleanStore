package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi;

import java.math.BigDecimal;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.RangeValidator;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIOutput;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIOutputType;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIOutputDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIOutputTypeDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ Role.PIC })
public class NewDBOutputPage extends FrontendPage 
{
	private static final long serialVersionUID = 1L;
	
	private OIOutputDao oiOutputDao;
	private OIOutputTypeDao oiOutputTypeDao;
	
	public NewDBOutputPage(final Integer ruleId) 
	{
		super(
			"Home > Backend > OI > Groups > Rules > DB Outputs > New", 
			"Add a new DB Output"
		);
		
		// prepare DAO objects
		//
		oiOutputDao = daoLookupFactory.getDao(OIOutputDao.class);
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

	private void addNewDBOutputForm(final Integer ruleId)
	{
		IModel<OIOutput> formModel = new CompoundPropertyModel<OIOutput>(new OIOutput());
		
		Form<OIOutput> form = new Form<OIOutput>("newDBOutputForm", formModel)
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmit()
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
					getSession().error(ex.getMessage());
					return;
				}
				catch (Exception ex)
				{
					// TODO: log the error
					
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
		textfield.add(new RangeValidator<BigDecimal>(new BigDecimal(0), new BigDecimal(Double.MAX_VALUE)));
		return textfield;
	}
}
