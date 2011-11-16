package net.codjo.standalone.client;
import net.codjo.agent.AgentContainer;
import net.codjo.agent.ContainerConfiguration;
import net.codjo.plugin.common.ApplicationPlugin;
import net.codjo.sql.server.ConnectionPool;
/**
 *
 */
public abstract class StandalonePlugin implements ApplicationPlugin {

    public void initContainer(ContainerConfiguration containerConfiguration) throws Exception {
    }


    public void start(AgentContainer agentContainer) throws Exception {
    }


    public void initGui(ConnectionPool cm) throws Exception {
    }


    public void displayGui(String serverBase, String serverName) throws Exception {

    }


    public void stop() throws Exception {
    }
}
