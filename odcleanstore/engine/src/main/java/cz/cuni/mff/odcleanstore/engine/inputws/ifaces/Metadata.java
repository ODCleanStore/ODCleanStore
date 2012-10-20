package cz.cuni.mff.odcleanstore.engine.inputws.ifaces;

import java.io.Serializable;

/**
 *  @author Petr Jerman
 */
public class Metadata implements Serializable {
	
	private static final long serialVersionUID = 2014030287306465839L;
	
	public String uuid;
	public String[] publishedBy;
	public String[] source;
	public String[] license;
	public String dataBaseUrl;
	public String provenance;
	public String pipelineName;
	public String updateTag;
}
