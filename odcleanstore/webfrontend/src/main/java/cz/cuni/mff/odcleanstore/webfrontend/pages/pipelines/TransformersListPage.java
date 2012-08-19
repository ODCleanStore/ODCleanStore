package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Transformer;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteRawButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.SortTableButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.TruncatedLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.GenericSortableDataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class TransformersListPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private DaoForEntityWithSurrogateKey<Transformer> transformerDao;
	
	public TransformersListPage() 
	{
		super
		(
			"Home > Transformers > List", 
			"Registered transformers configuration"
		);
		
		// prepare DAO objects
		//
		transformerDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(TransformerDao.class);
		
		// register page components
		//
		addTransformersTable();
	}

	private void addTransformersTable()
	{
		SortableDataProvider<Transformer> data = new GenericSortableDataProvider<Transformer>(transformerDao, "label");
		
		DataView<Transformer> dataView = new DataView<Transformer>("transformersTable", data)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<Transformer> item) 
			{
				Transformer transformer = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<Transformer>(transformer));
	
				item.add(new Label("label"));
				item.add(new TruncatedLabel("description", MAX_LIST_COLUMN_TEXT_LENGTH));
				item.add(new TruncatedLabel("jarPath", MAX_LIST_COLUMN_TEXT_LENGTH));
				item.add(new TruncatedLabel("fullClassName", MAX_LIST_COLUMN_TEXT_LENGTH));
				
				item.add(
					new DeleteRawButton<Transformer>
					(
						transformerDao,
						transformer.getId(),
						"transformer",
						new DeleteConfirmationMessage("transformer", "pipeline assignment"),
						TransformersListPage.this
					)
				);
				
				item.add(
					new RedirectButton(
						TransformerDetailPage.class, 
						transformer.getId(), 
						"showTransformerDetailPage"
					)
				);
				
				item.add(
					new RedirectButton(
						EditTransformerPage.class, 
						transformer.getId(), 
						"showEditTransformerPage"
					)
				);
			}
		};

		dataView.setItemsPerPage(10);
		
		add(new SortTableButton<Transformer>("sortByLabel", "label", data, dataView));
		add(new SortTableButton<Transformer>("sortByJARPath", "jarPath", data, dataView));
		add(new SortTableButton<Transformer>("sortByFullClassName", "fullClassName", data, dataView));
		
		add(dataView);
		
		add(new PagingNavigator("navigator", dataView));
	}
}
