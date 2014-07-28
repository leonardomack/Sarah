package sara.api.tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

import sara.api.Sara;
import sara.api.handler.LogEventArgs;
import sara.api.interfaces.ILogEvent;

public class SaraLog extends EventObject
{
	private static final long serialVersionUID = 4772075099592107434L;
	private List<ILogEvent> eventLogListeners;
	private StringBuilder log;

	public SaraLog(Object sender)
	{
		super(sender);

		eventLogListeners = new ArrayList<ILogEvent>();
		log = new StringBuilder();
	}

	public void add(String message)
	{
		// Creating the record message
		String logMessage = new Date() + ": " + message;
		log.append(logMessage);
		log.append(System.getProperty("line.separator"));

		// System print just for console view
		System.out.println(logMessage);

		// Call the event
		onNewLogMessageAddedEventHandler(new LogEventArgs(logMessage));
	}

	public String getLog()
	{
		return log.toString();
	}

	public synchronized void addLogEventsListener(ILogEvent listener)
	{
		eventLogListeners.add(listener);
	}

	public synchronized void removeLogEventsListener(ILogEvent listener)
	{
		eventLogListeners.remove(listener);
	}

	private synchronized void onNewLogMessageAddedEventHandler(LogEventArgs e)
	{
		SaraLog sender = this;// was new SaraLog(this); Need to understand in what situation that's really necessary 
		Iterator<ILogEvent> i = eventLogListeners.iterator();
		while (i.hasNext())
		{
			((ILogEvent) i.next()).onNewLogMessageAdded(sender, e);
		}
	}
}
