package cz.cuni.mff.odcleanstore.test;

import java.io.File;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ConfigurationException;
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
	public static void main(String[] args) throws ConfigurationException {
		if (args.length < 2) {
            System.out.println("Pass a path to the directory with Silk configuration as a command line argument");
            return;
        }
		File transformerDirectory = new File(args[0]);
		ConfigLoader.loadConfig(args[1]);
		Linker linker = new LinkerImpl(ConfigLoader.getConfig().getObjectIdentificationConfig());
		TransformationContext context = new TransformationContextTestImpl(transformerDirectory, null, null);
		linker.linkByConfigFiles(context);
	}
}
