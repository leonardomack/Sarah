package sara.api.handler;

public class SaraCentralDiscoverPackageEventArgs
{
	// Properties
	private String ip;
	private Boolean isIpReachable;

	// Constructors
	public SaraCentralDiscoverPackageEventArgs(String ip, Boolean isIpReachable)
	{
		this.ip = ip;
		this.isIpReachable = isIpReachable;
	}

	// Getters
	public String getIp()
	{
		return ip;
	}

	public Boolean getIsIpReachable()
	{
		return isIpReachable;
	}
}
