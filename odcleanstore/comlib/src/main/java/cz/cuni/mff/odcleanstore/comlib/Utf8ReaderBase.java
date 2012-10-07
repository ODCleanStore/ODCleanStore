package cz.cuni.mff.odcleanstore.comlib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Base for Reader helper classes.
 * 
 * @author Petr Jerman
 */
public abstract class Utf8ReaderBase {

	protected BufferedReader reader;
	
	/** 
	 * Create reader with underline buffered writer.
	 * 
	 * @param stream inputstream for reading.
	 * @throws UnsupportedEncodingException 
	 */
	protected Utf8ReaderBase(InputStream stream) throws UnsupportedEncodingException {
		reader = new BufferedReader(new InputStreamReader(stream, "utf-8"));
	}
		
	/**
     * Close underline stream without throwing an exception.
     */
    public void closeQuietly() {
       	try {
       		if (reader != null) {
               reader.close();
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
