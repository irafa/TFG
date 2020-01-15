/**
 * 
 */
package controllers;

import java.io.File;
import java.io.FileFilter;

/**
 * @author Rafael Armesilla Sánchez
 * Una clase que implementa la interfaz Java FileFilter.
 *
 */
public class DirectoriesFileFilter implements FileFilter{

	/* (non-Javadoc)
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File file) {
		return file.isDirectory();
	}

}
