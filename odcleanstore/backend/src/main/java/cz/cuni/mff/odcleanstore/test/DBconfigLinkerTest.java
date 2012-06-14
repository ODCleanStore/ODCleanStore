package cz.cuni.mff.odcleanstore.test;

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

	public static void main(String[] args) throws TransformerException {
		if (args.length == 0) {
            System.out.println("Pass a path to the directory with Silk configuration as a command line argument");
            return;
        }
		File transformerDirectory = new File(args[0]);
		Linker linker = new LinkerImpl();
		JDBCConnectionCredentials endpoint = new JDBCConnectionCredentials("jdbc:virtuoso://localhost:1111/UID=dba/PWD=dba", "dba", "dba");
		TransformationContext context = new TransformationContextTestImpl(transformerDirectory,"1",endpoint);
		TransformedGraph inputGraph = new TransformedGraphTestImpl("http://opendata.cz/data/namedGraph/1");
		linker.transformNewGraph(inputGraph, context);
	}
}
