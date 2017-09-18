package emergensor.server.test002;

public class EmergensorEvent
{

	public static class AddMarker extends EmergensorEvent
	{

		public final Marker marker;

		public AddMarker(Marker marker)
		{
			this.marker = marker;
		}

	}

}
