package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn;

import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.validators.IRIValidator;

import cz.cuni.mff.odcleanstore.webfrontend.bo.cr.*;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRuleComponent;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRuleComponentType;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.RedirectButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.cr.*;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleComponentDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRuleComponentTypeDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.exceptions.DaoException;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class NewDNRuleComponentPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	private DaoForEntityWithSurrogateKey<DNRuleComponent> dnRuleComponentDao;
	private DaoForEntityWithSurrogateKey<DNRuleComponentType> dnRuleComponentTypeDao;
	
	public NewDNRuleComponentPage(Long ruleId) 
	{
		super(
			"Home > Backend > DN > Groups > Rules > Components > New", 
			"Add a new DN rule component"
		);

		// prepare DAO objects
		//
		dnRuleComponentDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(DNRuleComponentDao.class);
		dnRuleComponentTypeDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(DNRuleComponentTypeDao.class);
		
		// register page components
		//
		add(
			new RedirectButton(
				DNRuleDetailPage.class,
				ruleId, 
				"showDNRuleDetailPage"
			)
		);
		
		addNewComponentForm(ruleId);
	}
	
	private void addNewComponentForm(final Long ruleId)
	{
		IModel<DNRuleComponent> formModel = new CompoundPropertyModel<DNRuleComponent>(new DNRuleComponent());
		
		Form<DNRuleComponent> form = new Form<DNRuleComponent>("newDNRuleComponentForm", formModel)
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				DNRuleComponent dnRuleComponent = this.getModelObject();
				dnRuleComponent.setRuleId(ruleId);
				
				try {
					dnRuleComponentDao.save(dnRuleComponent);
				}
				catch (DaoException ex)
				{
					getSession().error(ex.getMessage());
					return;
				}
				catch (Exception ex)
				{
					// TODO: log the error
					
					getSession().error(
						"The component could not be registered due to an unexpected error."
					);
					
					return;
				}
				
				getSession().info("The component was successfuly registered.");
				setResponsePage(new DNRuleDetailPage(ruleId));
			}
		};
		
		form.add(createEnumSelectbox(dnRuleComponentTypeDao, "type"));
		form.add(createTextarea("modification", true));
		form.add(createTextarea("description", false));
		
		add(form);
	}
}
