package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.validation.validator.RangeValidator;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIOutput;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIOutputType;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRule;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.AuthorizedDeleteButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.AuthorizedRedirectButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.LimitedEditingForm;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.OIRuleExportButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.OIRuleImportButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.UnobtrusivePagingNavigator;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIOutputDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIOutputTypeDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LimitedEditingPage;

@AuthorizeInstantiation({ Role.PIC })
public class OIRuleDetailPage extends LimitedEditingPage
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(OIRuleDetailPage.class);
	
	private OIRuleDao oiRuleDao;
	private OIOutputDao oiOutputDao;
	private OIOutputTypeDao oiOutputTypeDao;
	
	public OIRuleDetailPage(final Integer ruleId) 
	{
		super(
			"Home > Backend > OI > Groups > Rules > Edit", 
			"Edit OI rule",
			OIRuleDao.class,
			ruleId
		);
		
		// prepare DAO objects
		//
		
		oiOutputDao = daoLookupFactory.getDao(OIOutputDao.class, isEditable());
		oiRuleDao = daoLookupFactory.getDao(OIRuleDao.class, isEditable());
		oiOutputTypeDao = daoLookupFactory.getDao(OIOutputTypeDao.class);
		
		// register page components
		//
		addHelpWindow("oiRuleHelpWindow", "openOIRuleHelpWindow", new OIRuleHelpPanel("content"));
		addHelpWindow("dbOutputHelpWindow", "openDBOutputHelpWindow", new DBOutputHelpPanel("content"));
		addHelpWindow("fileOutputHelpWindow", "openFileOutputHelpWindow", new FileOutputHelpPanel("content"));
		
		addEditOIRuleForm(ruleId);
		addDBOutputsSection(ruleId);
		addFileOutputsSection(ruleId);
	}
	
	private void addDBOutputsSection(final Integer ruleId) 
	{
		add(
			new AuthorizedRedirectButton(
				NewDBOutputPage.class,
				ruleId, 
				isEditable(),
				"showNewDBOutputPage"
			)
		);
		
		OIOutputType outputType = oiOutputTypeDao.loadBy("label", OIOutputType.DB_OUTPUT_LABEL);
		
		addDBOutputsTable(ruleId, outputType.getId());
	}

	private void addDBOutputsTable(final Integer ruleId, final Integer typeId) 
	{		
		IDataProvider<OIOutput> data = new OIOutputDataProvider(oiOutputDao, ruleId, typeId);
		
		DataView<OIOutput> dataView = new DataView<OIOutput>("dbOutputsTable", data)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<OIOutput> item) 
			{
				OIOutput output = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<OIOutput>(output));
				
				item.add(createNullResistentTableCellLabel("minConfidence", output.getMinConfidence()));
				item.add(createNullResistentTableCellLabel("maxConfidence", output.getMaxConfidence()));
				
				item.add(
					new AuthorizedDeleteButton<OIOutput>
					(
						oiOutputDao,
						output.getId(),
						isEditable(),
						"deleteDBOutput",
						"output",
						new DeleteConfirmationMessage("output"),
						OIRuleDetailPage.this
					)
				);	
				
				item.add(
					new RedirectWithParamButton(
						DBOutputDetailPage.class, 
						output.getId(), 
						"showEditDBOutputPage"
					)
				);
			}
		};
		
		dataView.setItemsPerPage(ITEMS_PER_PAGE);
		
		add(dataView);
		
		add(new UnobtrusivePagingNavigator("dbOutputsNavigator", dataView));
	}
	
	private void addFileOutputsSection(final Integer ruleId) 
	{
		add(
			new AuthorizedRedirectButton(
				NewFileOutputPage.class,
				ruleId, 
				isEditable(),
				"showNewFileOutputPage"
			)
		);
		
		OIOutputType outputType = oiOutputTypeDao.loadBy("label", OIOutputType.FILE_OUTPUT_LABEL);
		
		addFileOutputsTable(ruleId, outputType.getId());
	}

	private void addFileOutputsTable(final Integer ruleId, final Integer typeId) 
	{	
		IDataProvider<OIOutput> data = new OIOutputDataProvider(oiOutputDao, ruleId, typeId);
		
		DataView<OIOutput> dataView = new DataView<OIOutput>("fileOutputsTable", data)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<OIOutput> item) 
			{
				OIOutput output = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<OIOutput>(output));
				
				item.add(createNullResistentTableCellLabel("minConfidence", output.getMinConfidence()));
				item.add(createNullResistentTableCellLabel("maxConfidence", output.getMaxConfidence()));
				item.add(new Label("filename"));
				item.add(new Label("fileFormat", output.getFileFormat().getLabel()));
				
				item.add(
					new AuthorizedDeleteButton<OIOutput>
					(
						oiOutputDao,
						output.getId(),
						isEditable(),
						"deleteFileOutput",
						"output",
						new DeleteConfirmationMessage("output"),
						OIRuleDetailPage.this
					)
				);
				
				item.add(
					new RedirectWithParamButton(
						FileOutputDetailPage.class, 
						output.getId(), 
						"showEditFileOutputPage"
					)
				);
			}
		};
		
		dataView.setItemsPerPage(10);
		
		add(dataView);
		
		add(new UnobtrusivePagingNavigator("fileOutputsNavigator", dataView));
	}
	
	private void addEditOIRuleForm(final Integer ruleId)
	{
		IModel<OIRule> formModel = createModelForOverview(oiRuleDao, ruleId);
		
		add(
			new RedirectWithParamButton
			(
				OIGroupDetailPage.class, 
				formModel.getObject().getGroupId(), 
				"showOIRulesList"
			)
		);
		
		Form<OIRule> form = new LimitedEditingForm<OIRule>("editOIRuleForm", formModel, isEditable())
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmitImpl()
			{
				OIRule rule = getModelObject();
				
				try {
					oiRuleDao.update(rule);
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
						"The rule could not be updated due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The rule was successfuly updated.");
			}
		};
		
		form.add(createTextfield("label"));
		form.add(createTextfield("linkType"));
		form.add(createTextfield("sourceRestriction", false));
		form.add(createTextfield("targetRestriction", false));
		form.add(createTextarea("linkageRule"));
		form.add(createFilterThresholdTextfield());
		form.add(createFilterLimitTextfield());
		
		form.setMultiPart(true);
		FileUploadField fileUpload = new FileUploadField("fileUpload", new ListModel<FileUpload>(new ArrayList<FileUpload>()));
		form.add(fileUpload);
		form.add(new OIRuleImportButton(fileUpload, form, "import"));
		form.add(new OIRuleExportButton(form.getModelObject(), "export"));
		
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
}
