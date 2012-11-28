package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa.debug;

import java.util.ArrayList;
import java.util.HashMap;
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
import cz.cuni.mff.odcleanstore.qualityassessment.QualityAssessor.GraphScoreWithTrace;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;
import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.UploadButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.qa.QARulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LimitedEditingPage;
import cz.cuni.mff.odcleanstore.webfrontend.util.TemporaryGraphLoader;
import cz.cuni.mff.odcleanstore.webfrontend.util.TemporaryGraphLoader.TemporaryGraph;

/**
 * Policy creator can debug QA rules on this page.
 * He can upload input RDF data from a file, 
 * run QA with rules from selected group and see its results on following page. 
 * @author Tomas Soukup
 */
@AuthorizeInstantiation({ Role.PIC })
public class QADebugPage extends LimitedEditingPage 
{	
	private static final long serialVersionUID = 1L;
	protected static Logger logger = Logger.getLogger(QADebugPage.class);
	
	private String rdfInput;
	
	/**
	 * @param groupId ID of the group of QA rules to debug
	 */
	public QADebugPage(Integer groupId)
	{
		super(
			"Home > Backend > Quality Assessment > Groups > Debug", 
			"Debug Quality Assessment rule group",
			QARulesGroupDao.class,
			groupId
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
				
				HashMap<String, String> graphs = new HashMap<String, String>();
				TemporaryGraphLoader loader = new TemporaryGraphLoader();
				TemporaryGraph graph = null;
				
				try 
				{
					graph = loader.importToTemporaryGraph(rdfInput);
					graphs.put("Debug Graph", graph.getGraphURI());
					List<GraphScoreWithTrace> results = assessor.debugRules(graphs, createContext(), getVisibleTableVersion());
					setResponsePage(new QADebugResultPage(results, groupId));
				}
				catch (TransformerException e)
				{
					logger.error(e.getMessage(), e);
					
					getSession().error("Rule debugging failed due to an unexpected error.");
				} catch (Exception e) {
					logger.error(e.getMessage(), e);

					getSession().error("Rule debugging failed due to an unexpected error.");
				}
				finally
				{
					if (graph != null) {
						try {
							graph.deleteGraph();
						} catch (Exception e) {
							logger.error("Could not delete temporary graph <" + graph.getGraphURI() + ">.");
						}
					}
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
