package de.ipvs.fachstudie.graphpartitioning.util;

import java.io.File;

/**
 * 
 * @author Christian Mayer
 * @author Heiko Geppert
 * @author Larissa Laich
 * @author Lukas Rieger
 *
 *         Validation validates different parameters e.g the path to a file.
 */

public class Validation {
	public static boolean validatePath(String path) {
		File file = new File(path);
		if (file.exists()) {
			return true;
		}
		return false;
	}
}
