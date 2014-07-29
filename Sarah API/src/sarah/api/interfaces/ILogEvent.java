package sarah.api.interfaces;

import java.util.EventObject;

import sarah.api.handler.LogEventArgs;

public interface ILogEvent
{
	void onNewLogMessageAdded(EventObject sender, LogEventArgs e);
}
