package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.RangeValidator;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIOutput;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.LimitedEditingForm;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIFileFormatDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIOutputDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LimitedEditingPage;

@AuthorizeInstantiation({ Role.PIC })
public class FileOutputDetailPage extends LimitedEditingPage 
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(FileOutputDetailPage.class);
	
	private OIOutputDao oiOutputDao;
	//private OIOutputTypeDao oiOutputTypeDao;
	private OIFileFormatDao oiFileFormatDao;
	
	public FileOutputDetailPage(final Integer outputId) 
	{
		super(
			"Home > OI > Rules groups > Group > Rules > Rule > File Outputs > Edit", 
			"Add a new file output",
			OIOutputDao.class,
			outputId
		);
		
		// prepare DAO objects
		//
		oiOutputDao = daoLookupFactory.getDao(OIOutputDao.class, isEditable());
		//oiOutputTypeDao = daoLookupFactory.getDao(OIOutputTypeDao.class);
		oiFileFormatDao = daoLookupFactory.getDao(OIFileFormatDao.class);
		
		// register page components
		//
		addHelpWindow(new FileOutputHelpPanel("content"));
		
		OIOutput output = oiOutputDao.load(outputId);
		
		add(
			new RedirectWithParamButton(
				OIRuleDetailPage.class,
				output.getRuleId(), 
				"showOIRuleDetail"
			)
		);
		
		addEditFileOutputForm(output);
	}

	private void addEditFileOutputForm(final OIOutput output)
	{
		IModel<OIOutput> formModel = new CompoundPropertyModel<OIOutput>(output);
		
		Form<OIOutput> form = new LimitedEditingForm<OIOutput>("editFileOutputForm", formModel, isEditable())
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmitImpl()
			{
				OIOutput output = getModelObject();
				
				try {
					oiOutputDao.update(output);
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
						"The output could not be updated due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The output was successfuly updated.");
				setResponsePage(new OIRuleDetailPage(output.getRuleId()));
			}
		};
		
		form.add(createConfidenceTextfield("minConfidence"));
		form.add(createConfidenceTextfield("maxConfidence"));
		form.add(createTextfield("filename"));
		form.add(createEnumSelectbox(oiFileFormatDao, "fileFormat"));
		
		add(form);
	}
	
	/**
	 * 
	 * @return
	 */
	private TextField<String> createConfidenceTextfield(String compName)
	{
		TextField<String> textfield = createTextfield(compName, false);
		textfield.add(new RangeValidator<Double>(0.0, Double.MAX_VALUE));
		return textfield;
	}
}
