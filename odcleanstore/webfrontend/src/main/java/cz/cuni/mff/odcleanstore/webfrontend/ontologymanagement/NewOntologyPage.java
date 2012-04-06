package cz.cuni.mff.odcleanstore.webfrontend.ontologymanagement;

import cz.cuni.mff.odcleanstore.webfrontend.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.bo.Ontology;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class NewOntologyPage extends FrontendPage
{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public NewOntologyPage() 
	{
		super(
			"Home > Ontology Management > Create", 
			"Create a new ontology"
		);

		// 1. Get the DAO beans.
		//
		// TODO:
		
		// 2. Construct the component hierarchy.
		//
		IModel model = new CompoundPropertyModel<Ontology>(new Ontology());
		Form<Ontology> newOntologyForm = new Form<Ontology>("newOntologyForm", model)
		{
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void onSubmit() 
			{
				Ontology ontology = this.getModelObject();

				// TODO: handle ontology insertion
				
				getSession().info("The ontology was successfuly created.");
				setResponsePage(OntologiesListPage.class);
			}
		};

		newOntologyForm.add(new Label("title"));
		newOntologyForm.add(new TextArea<String>("definition"));
		
		add(newOntologyForm);
		
	}

}
