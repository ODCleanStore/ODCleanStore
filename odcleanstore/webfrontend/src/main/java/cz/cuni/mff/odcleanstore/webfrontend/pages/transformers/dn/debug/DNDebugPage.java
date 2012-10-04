package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn.debug;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.util.ListModel;

import cz.cuni.mff.odcleanstore.datanormalization.impl.DataNormalizerImpl;
import cz.cuni.mff.odcleanstore.datanormalization.impl.DataNormalizerImpl.GraphModification;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;
import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.UploadButton;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa.debug.QADebugPage;

@AuthorizeInstantiation({ Role.PIC })
public class DNDebugPage extends FrontendPage 
{	
	private static final long serialVersionUID = 1L;
	protected static Logger logger = Logger.getLogger(QADebugPage.class);
	
	private String rdfInput;
	
	public DNDebugPage(Integer groupId)
	{
		super(
			"Home > Backend > DN > Groups > Debug", 
			"Debug DN rule group"
		);
			
		// register page components
		//
		addInputForm(groupId);
	}
	
	private void addInputForm(final Integer groupId)
	{
		Form<DNDebugPage> form = new Form<DNDebugPage>(
				"inputForm", new CompoundPropertyModel<DNDebugPage>(this))
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{	
				DataNormalizerImpl normalizer = new DataNormalizerImpl(groupId);
				try 
				{
					List<GraphModification> results = normalizer.debugRules(rdfInput, createContext());
					setResponsePage(new DNDebugResultPage(results, groupId));
				} 
				catch (TransformerException e)
				{
					logger.error(e.getMessage(), e);
					
					getSession().error("Rule debugging failed due to an unexpected error.");
				}		
			}
		};
				
		TextArea<String> rdfInput = createTextarea("rdfInput");
		form.add(rdfInput);
		
		form.setMultiPart(true);
		FileUploadField fileUpload = new FileUploadField("fileUpload", new ListModel<FileUpload>(new ArrayList<FileUpload>()));
		form.add(fileUpload);
		form.add(new UploadButton(fileUpload, rdfInput, "upload"));
		
		add(form);
	}
}
