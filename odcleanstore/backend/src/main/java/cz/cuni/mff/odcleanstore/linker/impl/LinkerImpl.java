package cz.cuni.mff.odcleanstore.linker.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cz.cuni.mff.odcleanstore.data.RDFprefix;
import cz.cuni.mff.odcleanstore.linker.Linker;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;
import de.fuberlin.wiwiss.silk.Silk;

public class LinkerImpl implements Linker {

	@Override
	public void transformNewGraph(TransformedGraph inputGraph, TransformationContext context) throws TransformerException {
		List<String> rawRules = new ArrayList<String>(); // TODO load rules from DB
		List<RDFprefix> prefixes = new ArrayList<RDFprefix>(); // TODO load prefixes from DB
		
		File configFile = ConfigBuilder.createLinkConfigFile(rawRules, prefixes, inputGraph, context);
		
		Silk.executeFile(configFile, null, Silk.DefaultThreads(), true);
	}

	@Override
	public void transformExistingGraph(TransformedGraph inputGraph, TransformationContext context) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
    public void shutdown() {
    }

	@Override
	public void linkCleanDatabase(TransformationContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void linkByConfigFiles(TransformationContext context) {
		File[] files = context.getTransformerDirectory().listFiles();
		for (File file:files) {
			if (file.getName().endsWith(".xml")) {
				Silk.executeFile(file, null, Silk.DefaultThreads(), true);
			}
		}
	}
}
