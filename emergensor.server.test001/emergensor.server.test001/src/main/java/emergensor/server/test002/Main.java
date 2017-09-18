package emergensor.server.test002;

import java.io.File;
import java.io.IOException;

import groovy.lang.GroovyShell;

public class Main
{

	public static void main(String[] args) throws IOException
	{
		File propertyFile = new File("emergensor.groovy");

		try {
			int i = 0;
			while (i < args.length) {
				if (args[i].equals("-p")) {
					i++;
					propertyFile = new File(args[i]);
				} else {
					throw null;
				}
			}
		} catch (RuntimeException e) {
			System.err.println("USAGE: [-p propertyFile]");
		}

		new GroovyShell().run(propertyFile, new String[0]);
	}

}
