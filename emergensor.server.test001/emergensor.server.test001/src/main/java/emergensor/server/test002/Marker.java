package emergensor.server.test002;

public class Marker
{

	public int markerId;
	public int userId;
	public String userName;
	public long time;
	public String text;
	public double lat;
	public double lon;

	public Marker()
	{

	}

	public Marker(int markerId, int userId, String userName, long time, String text, double lat, double lon)
	{
		this.markerId = markerId;
		this.userId = userId;
		this.userName = userName;
		this.time = time;
		this.text = text;
		this.lat = lat;
		this.lon = lon;
	}

}
