package cz.cuni.mff.odcleanstore.comlib;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * Base for Writer helper classes.
 * 
 * @author Petr Jerman
 */
public abstract class Utf8WriterBase {

	protected BufferedWriter writer;
	
	/**
	 * Create object with underline buffered writer.
	 * 
	 * @param stream outputstream for writing.
	 * @param encoding initial stream encoding for reading. 
	 * @throws UnsupportedEncodingException 
	 */
	protected Utf8WriterBase(OutputStream stream) throws UnsupportedEncodingException {
		writer = new BufferedWriter(new OutputStreamWriter(stream, "utf-8"));
	}
	
	/**
	 * Write text to underline stream.
	 * 
	 * @param text source text
	 * @throws IOException
	 */
	public void write(String text) throws IOException {
    	writer.write(text);
	}
	   
    /**
     *  Flush underline stream.
     *  
	 * @throws IOException 
     */
    public void flush() throws IOException {
    	writer.flush();
    }
		
	/**
     * Close underline stream without throwing an exception.
     */
    public void closeQuietly() {
       	try {
       		if (writer != null) {
               writer.close();
       		}
        } catch (IOException e) {
            // Do nothing
        }
    }

    /**
     * Close underline stream on finalize().
     */
    @Override
    protected void finalize() {
    	try {
    		closeQuietly();
    		super.finalize();
    	} catch(Throwable e) {
            // Do nothing
    	}
	}
}
