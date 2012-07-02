package cz.cuni.mff.odcleanstore.linker.rules;

public class FileOutput extends Output {
	String name;
	String format;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
}
