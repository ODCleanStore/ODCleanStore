/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.ws.user;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 * @author jermanp
 *
 */
public class HelloWorldResource extends ServerResource {

    @Get
    public String represent() {
        return "Kuku";
    }
}