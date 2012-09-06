package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.RangeValidator;

import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIFileFormat;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIOutput;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIOutputType;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIFileFormatDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIOutputDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIOutputTypeDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.RulesGroupHelpPanel;

public class EditFileOutputPage extends FrontendPage 
{
	private static final long serialVersionUID = 1L;
	
	private DaoForEntityWithSurrogateKey<OIOutput> oiOutputDao;
	private DaoForEntityWithSurrogateKey<OIOutputType> oiOutputTypeDao;
	private DaoForEntityWithSurrogateKey<OIFileFormat> oiFileFormatDao;
	
	public EditFileOutputPage(final Long outputId) 
	{
		super(
			"Home > OI > Rules groups > Group > Rules > Rule > File Outputs > Edit", 
			"Add a new file output"
		);
		
		// prepare DAO objects
		//
		oiOutputDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(OIOutputDao.class);
		oiOutputTypeDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(OIOutputTypeDao.class);
		oiFileFormatDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(OIFileFormatDao.class);
		
		// register page components
		//
		addHelpWindow(new FileOutputHelpPanel("content"));
		
		OIOutput output = oiOutputDao.load(outputId);
		
		add(
			new RedirectButton(
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
		
		Form<OIOutput> form = new Form<OIOutput>("editFileOutputForm", formModel)
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmit()
			{
				OIOutput output = getModelObject();
				
				try {
					oiOutputDao.update(output);
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
