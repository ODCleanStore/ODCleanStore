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
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class TransformersManagementPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private DaoForEntityWithSurrogateKey<Transformer> transformerDao;
	
	public TransformersManagementPage() 
	{
		super
		(
			"Home > Pipelines > Transformers", 
			"Registered transformers configuration"
		);
		
		// prepare DAO objects
		//
		transformerDao = (DaoForEntityWithSurrogateKey<Transformer>) daoLookupFactory.getDao(TransformerDao.class);
		
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
				item.add(new Label("description"));
				item.add(new Label("jarPath"));
				item.add(new Label("fullClassName"));
				
				item.add(
					new DeleteButton<Transformer>
					(
						transformerDao,
						transformer.getId(),
						"transformer",
						new DeleteConfirmationMessage("transformer", "pipeline assignment"),
						TransformersManagementPage.this
					)
				);	
			}
		};

		dataView.setItemsPerPage(10);
		
		add(dataView);
		
		add(new PagingNavigator("navigator", dataView));
	}
}
