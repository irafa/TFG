package controllers;
/**
 * 
 */

import java.io.File;

import models.AdjustmentsManager;
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
			AdjustmentsManager adjustmentsManager;

			try {
				// Si existe el fichero de ajustes en la carpeta destino definida por el usuario, 
				// lo procesamos y lo cargamos en memoria; si no existe, lo generamos:
				File plantUMLAdjustmentsFile = new File(String.valueOf(args[1] + "/PlantUMLAdjustments.txt"));
				if (plantUMLAdjustmentsFile.exists()){
					adjustmentsManager = new AdjustmentsManager (args[1] + "/PlantUMLAdjustments.txt");
					adjustmentsManager.loadCommands();
				}else{
					adjustmentsManager = new AdjustmentsManager (args[1] + "/PlantUMLAdjustments.txt");
					adjustmentsManager.generateAdjustmentsManagerFile();
				}
				
				// Creamos en memoria la estructura de la aplicación Java que es objeto de análisis.
				// Partimos de la ruta indicada por el usuario:
				applicationStructure = new ApplicationStructure(args[0]);
				
				// Rellenamos la estructura con todas las clases de la aplicación Java y sus dependencias correspondientes:
				applicationStructure.fillStructure();

				// Creamos la carpeta donde se guardarán todos los ficheros PlanUML generados:
				File plantUMLdirectory = new File(String.valueOf(args[1] + "/PlantUML"));
				plantUMLdirectory.mkdir();
				
				// Generamos el fichero PlantUML correspondiente al diagrama de arquitectura:
				PlantUMLWriter architectureDiagramFile = new PlantUMLWriter (args[1] + "/PlantUML/architectureDiagram.txt");
				architectureDiagramFile.generatePlantUMLPackageDiagram(applicationStructure, false);
				architectureDiagramFile.closePlantUMLFile();

				// Generamos el fichero PlantUML correspondiente al diagrama de arquitectura,
				// pintando las clases públicas de cada uno de los paquetes:
				PlantUMLWriter publicClassesArchitectureDiagramFile = new PlantUMLWriter (args[1] + "/PlantUML/publicClassesArchitectureDiagram.txt");
				publicClassesArchitectureDiagramFile.generatePlantUMLPackageDiagram(applicationStructure, true);
				publicClassesArchitectureDiagramFile.closePlantUMLFile();
				
				// Creamos la carpeta donde se guardarán los ficheros PlantUML 
				// correspondientes a los diagramas de clases de los controladores 
				File controllersClassDiagramsdirectory = new File(String.valueOf(args[1] + "/PlantUML/ControllersClassDiagrams"));
				controllersClassDiagramsdirectory.mkdir();
				// Generamos los ficheros PlantUML correspondientes a los diagramas de clases
				// de cada controlador definido en la aplicación Java:
				applicationStructure.generatePlantUMLControllersClassDiagrams(args[1], adjustmentsManager);
				
				// Creamos la estructura de carpetas con los ficheros PlantUML
				// correspondientes a los diagramas de clases de los distintos paquetes
				// de la aplicación Java:
				applicationStructure.generatePackagePlantUMLClassDiagrams(args[1], adjustmentsManager);


			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
		}else{
			System.out.println("Por favor, introduzca la ruta origen de la aplicación Java que desea analizar y "
					+ "la ruta de la carpeta destino donde quiere que se generen los ficheros PlantUML\n");
			System.out.println("Por ejemplo: \n");
			System.out.println("java -jar puml.jar C:/Users/rarmesil/Desktop/TFG/Ejemplos/klondike/bin c:/Users/rarmesil/Desktop/TFG/Resultado\n");
			System.out.println("En la ruta destino encontrará el fichero \"PlantUMLAdjustments\" que le permitirá hacer cambios manuales"
					+ "en los diagramas de clases generados.");
		}
	}
}
