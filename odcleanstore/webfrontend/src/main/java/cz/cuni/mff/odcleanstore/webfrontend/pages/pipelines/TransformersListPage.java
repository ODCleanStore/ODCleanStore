package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Transformer;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.TruncatedLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DataProvider;
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
	
	/*
	 	=======================================================================
	 	Implementace transformersTable
	 	=======================================================================
	*/
	
	private void addTransformersTable()
	{
		IDataProvider<Transformer> data = new DataProvider<Transformer>(transformerDao);
		
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
					new DeleteButton<Transformer>
					(
						transformerDao,
						transformer.getId(),
						"transformer",
						new DeleteConfirmationMessage("transformer", "pipeline assignment"),
						TransformersListPage.this
					)
				);	
			}
		};

		dataView.setItemsPerPage(10);
		
		add(dataView);
		
		add(new PagingNavigator("navigator", dataView));
	}
}
