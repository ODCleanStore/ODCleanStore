package cz.cuni.mff.odcleanstore.test;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ConfigurationException;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.linker.Linker;
import cz.cuni.mff.odcleanstore.linker.impl.LinkerImpl;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;

import java.io.File;

/**
 * Testovaci trida pro prototyp linkeru.
 *
 * Linkuje podle pravidel nactenych z Virtuosa.
 * Insert skript pro pravidla je na Bitbucketu v adresari design/rel_db
 *
 * Je v nem potreba upravit:
 * - cestu pro vystupni soubor (element Output)
 * - pripadne URI na Sparql endpoint (element DataSource/Param[@name = "endpointURI"])
 *
 * Dale je potreba mit ve Virtuosu nejaka RDF data, ukazkova konfigurace linkuje data od Tomase K.
 *
 * @author Tomas Soukup
 */
public class DBconfigLinkerTest {

	public static void main(String[] args) throws TransformerException, ConfigurationException {
		if (args.length < 2) {
            System.out.println("Pass paths to the directories with Silk configuration and ODCS configuration as command line arguments");
            return;
        }
		File transformerDirectory = new File(args[0]);

		ConfigLoader.loadConfig(args[1]);
		Linker linker = new LinkerImpl(ConfigLoader.getConfig().getObjectIdentificationConfig());
		JDBCConnectionCredentials cleanCredentials = new JDBCConnectionCredentials("jdbc:virtuoso://localhost:1111/UID=dba/PWD=dba", "dba", "dba");
		JDBCConnectionCredentials dirtyCredentials = new JDBCConnectionCredentials("jdbc:virtuoso://localhost:1112/UID=dba/PWD=dba", "dba", "dba");
		TransformationContext context = new TransformationContextTestImpl(transformerDirectory,"1",cleanCredentials, dirtyCredentials);
		TransformedGraph inputGraph = new TransformedGraphTestImpl("http://opendata.cz/data/namedGraph/1");
		linker.transformExistingGraph(inputGraph, context);
	}
}
