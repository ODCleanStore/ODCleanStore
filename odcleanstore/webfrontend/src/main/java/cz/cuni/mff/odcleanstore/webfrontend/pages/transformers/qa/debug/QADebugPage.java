package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa.debug;

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

import cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAssessorImpl;
import cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAssessorImpl.GraphScoreWithTrace;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;
import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.UploadButton;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ Role.PIC })
public class QADebugPage extends FrontendPage 
{	
	private static final long serialVersionUID = 1L;
	protected static Logger logger = Logger.getLogger(QADebugPage.class);
	
	private String rdfInput;
	
	public QADebugPage(Integer groupId)
	{
		super(
			"Home > Backend > QA > Groups > Debug", 
			"Debug QA rule group"
		);
			
		// register page components
		//
		addInputForm(groupId);
	}
	
	private void addInputForm(final Integer groupId)
	{
		Form<QADebugPage> form = new Form<QADebugPage>(
				"inputForm", new CompoundPropertyModel<QADebugPage>(this))
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{	
				QualityAssessorImpl assessor = new QualityAssessorImpl(groupId);
				try 
				{
					List<GraphScoreWithTrace> results = assessor.debugRules(rdfInput, createContext());
					setResponsePage(new QADebugResultPage(results, groupId));
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