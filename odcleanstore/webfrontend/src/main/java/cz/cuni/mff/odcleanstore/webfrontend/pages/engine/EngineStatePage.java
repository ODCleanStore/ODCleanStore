package cz.cuni.mff.odcleanstore.webfrontend.pages.engine;

import java.util.Locale;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.AttachedEngine;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.BooleanLabel;
import cz.cuni.mff.odcleanstore.webfrontend.core.models.DependentSortableDataProvider;
import cz.cuni.mff.odcleanstore.webfrontend.dao.en.AttachedEngineDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ Role.PIC })
public class EngineStatePage extends FrontendPage 
{
	private static final long serialVersionUID = 1L;

	private AttachedEngineDao attachedEngineDao;
	
	public EngineStatePage() 
	{
		super
		(
			"Home > Backend > Engine > State", 
			"State overview for connected engines"
		);
		
		// prepare DAO objects
		//
		attachedEngineDao = daoLookupFactory.getDao(AttachedEngineDao.class);
		
		// register page components
		//
		addAttachedEngineStatus();
	}
	
	private void addAttachedEngineStatus()
	{
		DependentSortableDataProvider<AttachedEngine> data =
			new DependentSortableDataProvider<AttachedEngine>(attachedEngineDao, "uuid");
		
		DataView<AttachedEngine> dataView = new DataView<AttachedEngine>("pipelineState", data)
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void populateItem(Item<AttachedEngine> item) {
				AttachedEngine attachedEngine = item.getModelObject(); 

				item.setModel(new CompoundPropertyModel<AttachedEngine>(attachedEngine));

				item.add(new Label("uuid"));
				item.add(new BooleanLabel("isPipelineError")
				{
					private static final long serialVersionUID = 1L;
					@Override
					public String convertToString(Boolean value, Locale locale)
					{
						return super.convertToString(value, locale);
					}
				});
				item.add(new BooleanLabel("isNotifyRequired")
				{
					private static final long serialVersionUID = 1L;
					@Override
					public String convertToString(Boolean value, Locale locale)
					{
						return super.convertToString(value, locale);
					}
				});
				item.add(new Label("stateDescription"));
				item.add(new Label("updated"));
			}
		};
		
		add(dataView);
	}
}
