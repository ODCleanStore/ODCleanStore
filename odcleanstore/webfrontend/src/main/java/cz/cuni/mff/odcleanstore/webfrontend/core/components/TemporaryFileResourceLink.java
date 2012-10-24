package cz.cuni.mff.odcleanstore.webfrontend.core.components;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import org.apache.log4j.Logger;
import org.apache.wicket.IResourceListener;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.request.resource.IResource.Attributes;
import org.apache.wicket.request.resource.ResourceStreamResource;
import org.apache.wicket.util.resource.FileResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;

/**
 * A link for download of contents stored in a temporary file. 
 * The temporary file is generated only on demand and deletion is ensured by this class. 
 * @author Jan Michelfeit
 */
public class TemporaryFileResourceLink<T> extends Link<T> implements IResourceListener
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(TemporaryFileResourceLink.class);
	
	public interface ITempFileCreator extends Serializable
	{
		File createTempFile();
		String getFileName();
	}
	
	private class TemporaryFileResourceStream extends FileResourceStream 
	{
		private static final long serialVersionUID = 1L;

		public TemporaryFileResourceStream(File file)
		{
			super(file);
		}

		@Override
		public void close() throws IOException
		{
			super.close();
			logger.info("Deleting temporary graph dump file " + getFile().getName());
			getFile().delete();
		}
		
		@Override
		public String getContentType()
		{
			return contentType;
		}
	}
	

	private final ITempFileCreator tempFileCreator;
	private String contentType;
	

	/**
	 * Constructs a link directly to the provided resource.
	 * 
	 * @param id
	 *            See Component
	 * @param resource
	 *            The resource
	 */
	public TemporaryFileResourceLink(final String id, String contentType, final ITempFileCreator tempFileCreator)
	{
		super(id);
		this.tempFileCreator = tempFileCreator;
		this.contentType = contentType;
	}
	
	/**
	 * @see org.apache.wicket.IResourceListener#onResourceRequested()
	 */
	public final void onResourceRequested()
	{

		Attributes a = new Attributes(RequestCycle.get().getRequest(), RequestCycle.get().getResponse(), null);
		ResourceStreamResource resource = null;
		logger.info("Creating temporary graph dump file.");
		File tempFile = tempFileCreator.createTempFile();
		if (tempFile != null && tempFile.exists())
		{
			tempFile.deleteOnExit();
			resource = new ResourceStreamResource(new TemporaryFileResourceStream(tempFile));
		}
		else
		{
			resource = new ResourceStreamResource(new StringResourceStream("", contentType));
		}
		resource.setFileName(tempFileCreator.getFileName());
		resource.setContentDisposition(ContentDisposition.ATTACHMENT);
		resource.respond(a);
		onLinkClicked();
	}

	@Override
	protected final CharSequence getURL()
	{
		return urlFor(IResourceListener.INTERFACE, null);
	}

	@Override
	public void onClick()
	{
	}
}
