package cz.cuni.mff.odcleanstore.engine.db.model;

public class PipelineCommand {

	public String jarPath;
	public String fullClassName;
	public String workDirPath;
	public String configuration;
	public boolean runOnCleanDB;
	public int transformerInstanceID;
	
	public static PipelineCommand[] deepClone(PipelineCommand[] src) {
		if ( src == null) {
			return new PipelineCommand[0];
		}
		
		PipelineCommand[] dst = new PipelineCommand[src.length];
		for(int i=0; i<src.length;i++) {
			PipelineCommand mbr = new PipelineCommand();
			mbr.jarPath = src[i].jarPath;
			mbr.fullClassName = src[i].fullClassName;
			mbr.workDirPath = src[i].workDirPath;
			mbr.configuration = src[i].configuration;
			mbr.runOnCleanDB = src[i].runOnCleanDB;
			mbr.transformerInstanceID = src[i].transformerInstanceID;
			dst[i] = mbr; 
		}
		return dst;
	}
}
