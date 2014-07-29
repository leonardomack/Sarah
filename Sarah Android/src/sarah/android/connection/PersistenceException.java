package sarah.android.connection;

public class PersistenceException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -409873624356956275L;

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

}