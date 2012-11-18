package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.dn.debug;

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

import cz.cuni.mff.odcleanstore.datanormalization.impl.DataNormalizerImpl;
import cz.cuni.mff.odcleanstore.datanormalization.DataNormalizer.GraphModification;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;
import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.UploadButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.dn.DNRulesGroupDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LimitedEditingPage;
import cz.cuni.mff.odcleanstore.webfrontend.util.TemporaryGraphLoader;
import cz.cuni.mff.odcleanstore.webfrontend.util.TemporaryGraphLoader.TemporaryGraph;

@AuthorizeInstantiation({ Role.PIC })
public class DNDebugPage extends LimitedEditingPage 
{	
	private static final long serialVersionUID = 1L;
	protected static Logger logger = Logger.getLogger(DNDebugPage.class);
	
	private String rdfInput;
	
	public DNDebugPage(Integer groupId)
	{
		super(
			"Home > Backend > Data Normalization > Groups > Debug", 
			"Debug Data Normalization rule group",
			DNRulesGroupDao.class,
			groupId
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

				HashMap<String, String> graphs = new HashMap<String, String>();
				TemporaryGraphLoader loader = new TemporaryGraphLoader();
				TemporaryGraph graph = null;
				
				try 
				{
					graph = loader.importToTemporaryGraph(rdfInput);
					graphs.put("Debug Graph", graph.getGraphURI());
					List<GraphModification> results = normalizer.debugRules(graphs, createContext(), getVisibleTableVersion());
					setResponsePage(new DNDebugResultPage(results, groupId));
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
