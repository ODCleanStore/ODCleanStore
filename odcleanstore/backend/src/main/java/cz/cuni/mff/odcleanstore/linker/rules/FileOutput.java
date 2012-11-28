package cz.cuni.mff.odcleanstore.linker.rules;

/**
 * Business entity representing file output in linkage rule.
 * @author Tomas Soukup
 */
public class FileOutput extends Output {
	
	String name;
	String format;
	
	/**
	 * @return output filename
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name output filename
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return output file format
	 */
	public String getFormat() {
		return format;
	}
	/**
	 * @param format output file format
	 */
	public void setFormat(String format) {
		this.format = format;
	}
}
