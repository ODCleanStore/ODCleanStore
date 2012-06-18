package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Transformer;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerDao;
import cz.cuni.mff.odcleanstore.webfrontend.models.DataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class TransformersManagementPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private Dao<Transformer> transformerDao;
	
	public TransformersManagementPage() 
	{
		super
		(
			"Home > Pipelines > Transformers", 
			"Registered transformers configuration"
		);
		
		// prepare DAO objects
		//
		transformerDao = daoLookupFactory.getDao(TransformerDao.class);
		
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
					createDeleteButton(
						transformerDao, 
						transformer, 
						"deleteTransformer",
						"transformer",
						"pipeline assignment",
						TransformersManagementPage.class
					)
				);	
			}
		};

		dataView.setItemsPerPage(10);
		
		add(dataView);
		
		add(new PagingNavigator("navigator", dataView));
	}
}
