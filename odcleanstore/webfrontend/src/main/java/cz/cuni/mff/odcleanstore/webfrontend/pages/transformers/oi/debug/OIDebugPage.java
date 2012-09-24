package cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi.debug;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.util.ListModel;

import cz.cuni.mff.odcleanstore.configuration.BackendConfig;
import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.configuration.WebFrontendConfig;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.linker.Linker;
import cz.cuni.mff.odcleanstore.linker.impl.LinkerImpl;
import cz.cuni.mff.odcleanstore.transformer.EnumTransformationType;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;
import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRule;
import cz.cuni.mff.odcleanstore.webfrontend.core.components.UploadButton;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.oi.OIRuleDao;
import cz.cuni.mff.odcleanstore.webfrontend.pages.FrontendPage;

@AuthorizeInstantiation({ "PIC" })
public class OIDebugPage extends FrontendPage 
{	
	private static final long serialVersionUID = 1L;
	private static final String INPUT_ENCODING = "UTF-8";
	
	private DaoForEntityWithSurrogateKey<OIRule> oiRuleDao;
	
	private String rdfInput;
	
	public OIDebugPage(final Long groupId)
	{
		super(
				"Home > Backend > OI > Groups > Debug", 
				"Debug OI rule group"
			);
			
			// prepare DAO objects
			//
			oiRuleDao = daoLookupFactory.getDaoForEntityWithSurrogateKey(OIRuleDao.class);
			
			// register page components
			//
			addInputForm(groupId.intValue());
	}
	
	private void addInputForm(final Integer groupId)
	{
		Form<OIDebugPage> form = new Form<OIDebugPage>(
				"inputForm", new CompoundPropertyModel<OIDebugPage>(this))
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit()
			{
				Linker linker = new LinkerImpl(groupId);
				InputStream is;
				try {
					is = new ByteArrayInputStream(rdfInput.getBytes(INPUT_ENCODING));
					linker.debugRules(is, createContext());
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TransformerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
	
	private TransformationContext createContext()
	{
		final BackendConfig config = ConfigLoader.getConfig().getBackendGroup();
		return new TransformationContext()
		{

			public JDBCConnectionCredentials getDirtyDatabaseCredentials() {
				return config.getDirtyDBJDBCConnectionCredentials();
			}

			public JDBCConnectionCredentials getCleanDatabaseCredentials() {
				return config.getCleanDBJDBCConnectionCredentials();
			}

			public String getTransformerConfiguration() {
				return "";
			}

			public File getTransformerDirectory() {
				// TODO Auto-generated method stub
				return null;
			}

			public EnumTransformationType getTransformationType() {
				return EnumTransformationType.NEW;
			}
			
		};
	}
}
