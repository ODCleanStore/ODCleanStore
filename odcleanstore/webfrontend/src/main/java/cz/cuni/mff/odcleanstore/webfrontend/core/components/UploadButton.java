package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import java.io.IOException;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.util.io.IOUtils;

public class UploadButton extends Button 
{
	private static final long serialVersionUID = 1L;
	protected static final String ENCODING = "UTF-8";
	
	private FileUploadField fileUpload;
	private TextArea<String> textArea;
	
	public UploadButton(FileUploadField fileUpload, TextArea<String> textArea, String compName) 
	{
		super(compName);
		this.fileUpload = fileUpload;
		this.textArea = textArea;
		setDefaultFormProcessing(false);
	}
	
	@Override
	public void onSubmit() 
	{	
		final FileUpload uploadedFile = fileUpload.getFileUpload();
		if (uploadedFile != null)
		{
			try 
			{
				String content = IOUtils.toString(uploadedFile.getInputStream(), ENCODING);
				textArea.getModel().setObject(content);
				textArea.modelChanged();
			} catch (IOException e) 
			{
				getSession().error("Failed to read file: " + fileUpload.getValue());
			}
		}
	}
}
