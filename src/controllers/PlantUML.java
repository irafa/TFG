package controllers;
/**
 * 
 */

import models.PlantUMLWriter;

/**
 * @author Rafael Armesilla Sánchez
 *
 */
public class PlantUML {

	/**
	 * @param args: 
	 * 1 - Ruta origen de la aplicación Java
	 * 2 - Ruta destino del fichero donde se escribe el código PlantUML.
	 */
	public static void main(String[] args) {

		if (args.length == 2){
			ApplicationStructure applicationStructure;
			PlantUMLWriter plantUMLFile = new PlantUMLWriter (args[1]);

			try {
				// Creamos la estructura de la aplicación Java partiendo de la ruta indicada por el usuario:
				applicationStructure = new ApplicationStructure(args[0]);
				
				// Rellenamos la estructura con todas las clases de la aplicación Java y sus dependencias correspondientes:
				applicationStructure.fillStructure();

				// Escribimos en el fichero PlantUML el código correspondiente al diagrama de paquetes:
				plantUMLFile.generatePlantUMLPackageDiagram(applicationStructure, false);

				// Escribimos en el fichero PlantUML el código correspondiente al diagrama de paquetes
				// pintando las clases públicas de cada uno de ellos:
				plantUMLFile.generatePlantUMLPackageDiagram(applicationStructure, true);
				
				// Escribimos en el fichero PlantUML el código correspondiente a los diagramas de clases
				// de los distintos paquetes de la aplicación Java:
				plantUMLFile.generatePlantUMLClassDiagrams(applicationStructure);

				// Escribimos en el fichero PlantUML el código correspondiente al diagrama de clases
				// de cada controlador definido en la aplicación Java:
				plantUMLFile.generatePlantUMLControllersClassDiagrams(applicationStructure);

				// Cerramos el fichero PlantUML
				plantUMLFile.closePlantUMLFile();			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			System.out.println("Por favor, introduzca la ruta origen de la aplicación Java que desea analizar y la ruta del fichero destino donde quiere escribir el código PlantUML");
			System.out.println("Por ejemplo: ");
			System.out.println("java -jar puml.jar C:/Users/rarmesil/Desktop/TFG/Ejemplos/klondike/bin c:/Users/rarmesil/Desktop/TFG/plantuml.txt");
		}
	}
}
