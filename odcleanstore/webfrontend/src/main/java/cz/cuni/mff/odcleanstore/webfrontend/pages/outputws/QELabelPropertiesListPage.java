package cz.cuni.mff.odcleanstore.webfrontend.pages.outputws;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.qe.LabelProperty;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteRawButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qe.LabelPropertyDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

public class QELabelPropertiesListPage extends FrontendPage 
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(QELabelPropertiesListPage.class);
	
	private DaoForEntityWithSurrogateKey<LabelProperty> labelPropertyDao;

	public QELabelPropertiesListPage() 
	{
		super(
			"Home > Output WS > QE > Label properties > List", 
			"Adjust label properties"
		);
		
		// prepare DAO objects
		//
		labelPropertyDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(LabelPropertyDao.class);
		
		// register page components
		//
		addLabelPropertiesTable();
	}

	private void addLabelPropertiesTable()
	{
		IDataProvider<LabelProperty> data = new DataProvider<LabelProperty>(labelPropertyDao);
		
		DataView<LabelProperty> dataView = new DataView<LabelProperty>("propertiesTable", data)
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(Item<LabelProperty> item) 
			{
				LabelProperty property = item.getModelObject();
				
				item.setModel(new CompoundPropertyModel<LabelProperty>(property));

				item.add(new Label("property"));
				
				item.add(
					new DeleteRawButton<LabelProperty>
					(
						labelPropertyDao,
						property.getId(),
						"property",
						new DeleteConfirmationMessage("label property"),
						QELabelPropertiesListPage.this
					)
				);
			}
		};

		dataView.setItemsPerPage(10);
		
		add(dataView);
		
		add(new PagingNavigator("navigator", dataView));
	}
}
