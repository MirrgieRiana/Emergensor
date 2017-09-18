package emergensor.server.test002;

import java.util.Hashtable;

public class IdentifierTable
{

	private int next = 0;
	private Hashtable<Object, Integer> table = new Hashtable<>();

	public int get(Object identifier)
	{
		Integer id = table.get(identifier);
		if (id == null) {
			id = next;
			next++;
			table.put(identifier, id);
		}
		return id;
	}

}
