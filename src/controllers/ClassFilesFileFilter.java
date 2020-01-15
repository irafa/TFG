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
public class ClassFilesFileFilter implements FileFilter {

	private final String[] okFileExtensions = new String[] {"class"};

	  /* (non-Javadoc)
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File file)
	  {
	    for (String extension : okFileExtensions)
	    {
	      if (file.getName().toLowerCase().endsWith(extension))
	      {
	        return true;
	      }
	    }
	    return false;
	  }
	
}
