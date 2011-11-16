/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.standalone.client.util;
import net.codjo.sql.server.ConnectionPool;
import net.codjo.utils.ConnectionManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
/**
 *
 */
public class ConnectionManagerAdapter extends ConnectionManager {
    private final ConnectionPool adaptee;


    public ConnectionManagerAdapter(ConnectionPool adaptee) {
        this.adaptee = adaptee;
    }


    @Override
    public synchronized void closeAllConnections() {
        adaptee.closeAllConnections();
    }


    @Override
    public String getCatalog() {
        return adaptee.getCatalog();
    }


    @Override
    public String getClassDriver() {
        return adaptee.getClassDriver();
    }


    @Override
    public synchronized Connection getConnection()
          throws SQLException {
        return adaptee.getConnection();
    }


    @Override
    public Properties getDbProps() {
        return adaptee.getDbProps();
    }


    @Override
    public String getDbUrl() {
        return adaptee.getDbUrl();
    }


    @Override
    public synchronized void releaseConnection(Connection con, Statement stmt)
          throws SQLException {
        adaptee.releaseConnection(con, stmt);
    }


    @Override
    public synchronized void releaseConnection(Connection con)
          throws SQLException {
        adaptee.releaseConnection(con);
    }


    @Override
    public String toString() {
        return adaptee.toString();
    }


    @Override
    public int getAllConnectionsSize() {
        return adaptee.getAllConnectionsSize();
    }


    @Override
    public void shutdown() {
        adaptee.shutdown();
    }


    @Override
    public int hashCode() {
        return adaptee.hashCode();
    }


    @Override
    public boolean equals(Object obj) {
        return adaptee.equals(obj);
    }
}
