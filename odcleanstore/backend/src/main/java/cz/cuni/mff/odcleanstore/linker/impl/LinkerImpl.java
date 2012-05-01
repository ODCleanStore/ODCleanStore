package cz.cuni.mff.odcleanstore.linker.impl;

import java.io.File;
import java.sql.SQLException;
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
		String config = context.getTransformerConfiguration();
		if (config == null || config.isEmpty()) {
			linkByConfigFiles(context);
		} else {
			LinkerDao dao;
			try {
				dao = LinkerDao.getInstance(context.getCleanDatabaseEndpoint());
				List<String> rawRules = loadRules(context.getTransformerConfiguration(), dao);
				List<RDFprefix> prefixes = dao.loadPrefixes();
			
				File configFile = ConfigBuilder.createLinkConfigFile(rawRules, prefixes, inputGraph, context);
			
				Silk.executeFile(configFile, null, Silk.DefaultThreads(), true);
				
				configFile.delete();
			} catch (SQLException e) {
				throw new TransformerException(e);
			}
		}
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
	
	private List<String> loadRules(String transformerConfiguration, LinkerDao dao ) throws SQLException {
		String[] ruleGroupArray = transformerConfiguration.split(",");

		return dao.loadRules(ruleGroupArray);
	}
}
