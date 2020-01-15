package controllers;
/**
 * 
 */

import models.PlantUMLWriter;

/**
 * @author Rafael Armesilla S�nchez
 *
 */
public class PlantUML {

	/**
	 * @param args: 
	 * 1 - Ruta origen de la aplicaci�n Java
	 * 2 - Ruta destino del fichero donde se escribe el c�digo PlantUML.
	 */
	public static void main(String[] args) {

		if (args.length == 2){
			ApplicationStructure applicationStructure;
			PlantUMLWriter plantUMLFile = new PlantUMLWriter (args[1]);

			try {
				// Creamos la estructura de la aplicaci�n Java partiendo de la ruta indicada por el usuario:
				applicationStructure = new ApplicationStructure(args[0]);
				
				// Rellenamos la estructura con todas las clases de la aplicaci�n Java y sus dependencias correspondientes:
				applicationStructure.fillStructure();

				// Escribimos en el fichero PlantUML el c�digo correspondiente al diagrama de paquetes:
				plantUMLFile.generatePlantUMLPackageDiagram(applicationStructure, false);

				// Escribimos en el fichero PlantUML el c�digo correspondiente al diagrama de paquetes
				// pintando las clases p�blicas de cada uno de ellos:
				plantUMLFile.generatePlantUMLPackageDiagram(applicationStructure, true);
				
				// Escribimos en el fichero PlantUML el c�digo correspondiente a los diagramas de clases
				// de los distintos paquetes de la aplicaci�n Java:
				plantUMLFile.generatePlantUMLClassDiagrams(applicationStructure);

				// Escribimos en el fichero PlantUML el c�digo correspondiente al diagrama de clases
				// de cada controlador definido en la aplicaci�n Java:
				plantUMLFile.generatePlantUMLControllersClassDiagrams(applicationStructure);

				// Cerramos el fichero PlantUML
				plantUMLFile.closePlantUMLFile();			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			System.out.println("Por favor, introduzca la ruta origen de la aplicaci�n Java que desea analizar y la ruta del fichero destino donde quiere escribir el c�digo PlantUML");
			System.out.println("Por ejemplo: ");
			System.out.println("java -jar puml.jar C:/Users/rarmesil/Desktop/TFG/Ejemplos/klondike/bin c:/Users/rarmesil/Desktop/TFG/plantuml.txt");
		}
	}
}
