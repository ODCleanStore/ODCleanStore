package cz.cuni.mff.odcleanstore.shared;

import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import virtuoso.jena.driver.VirtGraph;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class GraphLoader {
    public static String loadGrap(String graphName, String format, JDBCConnectionCredentials connectionCredentials) {
        VirtGraph graph = new VirtGraph(
                graphName,
                connectionCredentials.getConnectionString(),
                connectionCredentials.getUsername(),
                connectionCredentials.getPassword());
        Model model = ModelFactory.createModelForGraph(graph);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        model.write(stream, format);
        String result = null;
        try {
            result = stream.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO handle
        }
        return result;
    }
}
