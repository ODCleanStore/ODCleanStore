package cz.cuni.mff.odcleanstore.webfrontend.pages.outputws;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.bo.qe.LabelProperty;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteRawButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.DeleteConfirmationMessage;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.SortTableButton;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.GenericSortableDataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qe.LabelPropertyDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.prefixes.URLPrefixHelpPanel;

@AuthorizeInstantiation({ "POC" })
public class QELabelPropertiesListPage extends FrontendPage 
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(QELabelPropertiesListPage.class);
	
	private DaoForEntityWithSurrogateKey<LabelProperty> labelPropertyDao;

	public QELabelPropertiesListPage() 
	{
		super(
			"Home > Output WS > Label Properties > List", 
			"List all label properties"
		);
		
		// prepare DAO objects
		//
		labelPropertyDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(LabelPropertyDao.class);
		
		// register page components
		//
		addHelpWindow(new LabelPropertyHelpPanel("content"));
		addLabelPropertiesTable();
	}

	private void addLabelPropertiesTable()
	{
		SortableDataProvider<LabelProperty> data = new GenericSortableDataProvider<LabelProperty>(labelPropertyDao, "property");
		
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

		dataView.setItemsPerPage(ITEMS_PER_PAGE);
		
		add(new SortTableButton<LabelProperty>("orderByProperty", "property", data, dataView));
		
		add(dataView);
		
		add(new PagingNavigator("navigator", dataView));
	}
}
