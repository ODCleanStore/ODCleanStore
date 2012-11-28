package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import java.io.IOException;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.util.io.IOUtils;

import cz.cuni.mff.odcleanstore.shared.Utils;

/**
 * Button with file-upload functionality. 
 * On submit loads the chosen file and displays its content in a textfield specified in constructor.
 * 
 * @author Tomas Soukup
 */
public class UploadButton extends Button 
{
	private static final long serialVersionUID = 1L;
	
	private FileUploadField fileUpload;
	private TextArea<String> textArea;
	
	/**
	 * @param fileUpload object representing uploaded file
	 * @param textArea content of the file will be displayed here
	 * @param compName name of the component
	 */
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
				String content = IOUtils.toString(uploadedFile.getInputStream(), Utils.DEFAULT_ENCODING);
				textArea.getModel().setObject(content);
				textArea.modelChanged();
			}
			catch (IOException e) 
			{
				getSession().error("Failed to read file: " + fileUpload.getValue());
			}
		}
	}
}
