package sarah.android.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.paho.android.service.MqttAndroidClient;

import android.content.Context;

public class Connections 
{
	/** Singleton instance of <code>Connections</code>**/
private static Connections instance = null;

/** List of {@link Connection} objects**/
private HashMap<String, Connection> connections = null;

/** {@link Persistence} object used to save, delete and restore connections**/
private Persistence persistence = null;

/**
 * Create a Connections object
 * @param context Applications context
 */
private Connections(Context context)
{
  connections = new HashMap<String, Connection>();

  //attempt to restore state
  persistence = new Persistence(context);
  try {
    List<Connection> l = persistence.restoreConnections(context);
    for (Connection c : l) {
      connections.put(c.handle(), c);
    }
  }
  catch (PersistenceException e) {
    e.printStackTrace();
  }

}

/**
 * Returns an already initialised instance of <code>Connections</code>, if Connections has yet to be created, it will
 * create and return that instance
 * @param context The applications context used to create the <code>Connections</code> object if it is not already initialised
 * @return Connections instance
 */
public synchronized static Connections getInstance(Context context)
{
  if (instance == null) {
    instance = new Connections(context);
  }

  return instance;
}

/**
 * Finds and returns a connection object that the given client handle points to
 * @param handle The handle to the <code>Connection</code> to return
 * @return a connection associated with the client handle, <code>null</code> if one is not found
 */
public Connection getConnection(String handle)
{

  return connections.get(handle);
}

/**
 * Adds a <code>Connection</code> object to the collection of connections associated with this object
 * @param connection connection to add
 */
public void addConnection(Connection connection)
{
  connections.put(connection.handle(), connection);
  try {
    persistence.persistConnection(connection);
  }
  catch (PersistenceException e)
  {
    //error persisting well lets just swallow this
    e.printStackTrace();
  }
}

/**
 * Create a fully initialised <code>MqttAndroidClient</code> for the parameters given
 * @param context The Applications context
 * @param serverURI The ServerURI to connect to
 * @param clientId The clientId for this client
 * @return new instance of MqttAndroidClient
 */
public MqttAndroidClient createClient(Context context, String serverURI, String clientId)
{
  MqttAndroidClient client = new MqttAndroidClient(context, serverURI, clientId);
  return client;
}

/**
 * Get all the connections associated with this <code>Connections</code> object.
 * @return <code>Map</code> of connections
 */
public Map<String, Connection> getConnections()
{
  return connections;
}

/**
 * Removes a connection from the map of connections
 * @param connection connection to be removed
 */
public void removeConnection(Connection connection) {
  connections.remove(connection.handle());
  persistence.deleteConnection(connection);
}

}
