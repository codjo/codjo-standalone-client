package net.codjo.standalone.client;
import net.codjo.gui.ApplicationData;
import net.codjo.utils.JukeBox;
import java.io.IOException;
/**
 * @deprecated use StandaloneGuiCore
 */
@Deprecated
public class StandaloneClient extends StandaloneGuiCore {
    public StandaloneClient(ApplicationData applicationData, JukeBox jukeBox) throws IOException {
        super(applicationData, jukeBox);
    }
}
