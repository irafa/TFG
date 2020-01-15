/**
 * 
 */
package models;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author Rafael Armesilla Sánchez
 *
 */
public class JavaApplicationReader {

	File applicationRootFile;
	URLClassLoader urlClassLoader;

	public JavaApplicationReader (String url){
		this.applicationRootFile = new File(url);
		this.setURLClassLoader();
	}
	
	/**
	 * @return el archivo (File) que apunta a la 
	 * carpeta raíz de una aplicación Java.
	 */
	public File getApplicationRootFile() {
		return applicationRootFile;
	}
	
	
	/**
	 * Partiendo del archivo de una aplicación (applicationRootFile), 
	 * crea y asigna el URLClassLoader correspondiente.
	 * 
	 */
	private void setURLClassLoader (){
		try {
			URL[] parentFolderPath={this.applicationRootFile.toURI().toURL()};
			this.urlClassLoader = new URLClassLoader(parentFolderPath);
		} catch (IOException e){// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @param classPath: Ruta a una clase Java desde la carpeta "bin" informada por el usuario (applicationRootFile).
	 * @return 
	 * 
	 * Este método devuelve un objeto de tipo Class<?>. Dicha clase se corresponde con la contenida 
	 * en el fichero .class cuya ruta viene definida por "applicationRootFile" + "classPath".
	 */
	public Class<?> getClassFromFile(String classPath){

		Class<?> clss = null;
		try {
			clss = this.urlClassLoader.loadClass(classPath);
		} catch (ClassNotFoundException e){// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return clss;
	}

	
	/**
	 * Cierra el URLClassLoader de este objeto.
	 */
	public void close(){
		try {
			this.urlClassLoader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}
