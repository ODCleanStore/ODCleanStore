package cz.cuni.mff.odcleanstore.test;

import java.io.File;

import cz.cuni.mff.odcleanstore.linker.Linker;
import cz.cuni.mff.odcleanstore.linker.impl.LinkerImpl;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;

/**
 * Testovaci trida pro prototyp linkeru.
 *
 * Linkuje podle pravidel nactenych ze vsech XML souboru v adresari, ktery dostane jako argument na prikazove radce.
 * Ukazka konfiguracniho souboru je na Bitbucketu na adrese: documents\analysis\testing\config.xml
 * 
 * Je v nem potreba upravit:
 * - cestu pro vystupni soubor (element Output)
 * - pripadne URI na Sparql endpoint (element DataSource/Param[@name = "endpointURI"])
 * 
 * Dale je potreba mit ve Virtuosu nejaka RDF data, ukazkova konfigurace linkuje data od Tomase K.
 *
 * @author Tomas Soukup
 */
public class FileConfigLinkerTest {
	public static void main(String[] args) {
		if (args.length == 0) {
            System.out.println("Pass a path to the directory with Silk configuration as a command line argument");
            return;
        }
		File transformerDirectory = new File(args[0]);
		Linker linker = new LinkerImpl();
		TransformationContext context = new TransformationContextTestImpl(transformerDirectory);
		linker.linkByConfigFiles(context);
	}
}
