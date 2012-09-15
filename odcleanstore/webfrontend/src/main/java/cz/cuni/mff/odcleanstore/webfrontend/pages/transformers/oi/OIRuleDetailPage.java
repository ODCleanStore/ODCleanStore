package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIOutput;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIOutputType;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRule;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteRawButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectWithParamButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIOutputDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIOutputTypeDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ "POC" })
public class OIRuleDetailPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(OIRuleDetailPage.class);
	
	private DaoForEntityWithSurrogateKey<OIRule> oiRuleDao;
	private DaoForEntityWithSurrogateKey<OIOutput> oiOutputDao;
	private DaoForEntityWithSurrogateKey<OIOutputType> oiOutputTypeDao;
	
	public OIRuleDetailPage(final Long ruleId) 
	{
		super(
			"Home > Backend > OI > Groups > Rules > Detail", 
			"Show OI rule detail"
		);
		
		// prepare DAO objects
		//
		
		oiOutputDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(OIOutputDao.class);
		oiRuleDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(OIRuleDao.class);
		oiOutputTypeDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(OIOutputTypeDao.class);
		
		// register page components
		//
		addHelpWindow("oiRuleHelpWindow", "openOIRuleHelpWindow", new OIRuleHelpPanel("content"));
		addHelpWindow("dbOutputHelpWindow", "openDBOutputHelpWindow", new DBOutputHelpPanel("content"));
		addHelpWindow("fileOutputHelpWindow", "openFileOutputHelpWindow", new FileOutputHelpPanel("content"));
		
		addRuleInformationSection(ruleId);
		addDBOutputsSection(ruleId);
		addFileOutputsSection(ruleId);
	}

	private void addRuleInformationSection(final Long ruleId)
	{
		IModel<OIRule> model = createModelForOverview(oiRuleDao, ruleId); 
		
		add(
			new RedirectWithParamButton
			(
				OIGroupDetailPage.class, 
				model.getObject().getGroupId(), 
				"showOIRulesList"
			)
		);
		
		setDefaultModel(model);
		
		add(new Label("label"));
		add(new Label("linkType"));
		add(new Label("sourceRestriction"));
		add(new Label("targetRestriction"));
		add(new Label("linkageRule"));
		add(new Label("filterThreshold"));
		add(new Label("filterLimit"));
	}
	
	private void addDBOutputsSection(final Long ruleId) 
	{
		add(
			new RedirectWithParamButton(
				NewDBOutputPage.class,
				ruleId, 
				"showNewDBOutputPage"
			)
		);
		
		OIOutputType outputType = oiOutputTypeDao.loadBy("label", OIOutputType.DB_OUTPUT_LABEL);
		
		addDBOutputsTable(ruleId, outputType.getId());
	}

	private void addDBOutputsTable(final Long ruleId, final Long typeId) 
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
					new DeleteRawButton<OIOutput>
					(
						oiOutputDao,
						output.getId(),
						"deleteDBOutput",
						"output",
						new DeleteConfirmationMessage("output"),
						OIRuleDetailPage.this
					)
				);	
				
				item.add(
					new RedirectWithParamButton(
						EditDBOutputPage.class, 
						output.getId(), 
						"showEditDBOutputPage"
					)
				);
			}
		};
		
		dataView.setItemsPerPage(ITEMS_PER_PAGE);
		
		add(dataView);
		
		add(new PagingNavigator("dbOutputsNavigator", dataView));
	}
	
	private void addFileOutputsSection(final Long ruleId) 
	{
		add(
			new RedirectWithParamButton(
				NewFileOutputPage.class,
				ruleId, 
				"showNewFileOutputPage"
			)
		);
		
		OIOutputType outputType = oiOutputTypeDao.loadBy("label", OIOutputType.FILE_OUTPUT_LABEL);
		
		addFileOutputsTable(ruleId, outputType.getId());
	}

	private void addFileOutputsTable(final Long ruleId, final Long typeId) 
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
					new DeleteRawButton<OIOutput>
					(
						oiOutputDao,
						output.getId(),
						"deleteFileOutput",
						"output",
						new DeleteConfirmationMessage("output"),
						OIRuleDetailPage.this
					)
				);
				
				item.add(
					new RedirectWithParamButton(
						EditFileOutputPage.class, 
						output.getId(), 
						"showEditFileOutputPage"
					)
				);
			}
		};
		
		dataView.setItemsPerPage(10);
		
		add(dataView);
		
		add(new PagingNavigator("fileOutputsNavigator", dataView));
	}
}
