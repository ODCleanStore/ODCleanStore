package cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Transformer;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.TransformerDao;
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
		IModel<List<Transformer>> model = createModelForListView(transformerDao);
		
		ListView<Transformer> listView = new ListView<Transformer>("transformersTable", model)
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(ListItem<Transformer> item) 
			{
				final Transformer transformer = item.getModelObject();
				
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
		
		add(listView);
	}
}
