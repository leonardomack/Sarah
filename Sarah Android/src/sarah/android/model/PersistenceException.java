package sarah.android.model;

public class PersistenceException extends Exception
{

	/**
	 * Creates a persistence exception with the given error message
	 * 
	 * @param message
	 *            The error message to display
	 */
	public PersistenceException(String message)
	{
		super(message);
	}

	/** Serialisation ID **/
	private static final long serialVersionUID = 5326458803268855071L;

}
