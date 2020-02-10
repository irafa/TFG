/**
 * 
 */
package controllers;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import models.AdjustmentsManager;
import models.DiagramAdjustments;
import models.JavaApplicationReader;
import models.PlantUMLWriter;
import models.Relationship;

/**
 * @author Rafael Armesilla Sánchez
 *
 */
public class ApplicationStructure {
	//Creamos los filtros con los que nos ayudaremos para recorrer la estructura de la apliacaión Java
	private final static DirectoriesFileFilter directoriesFilter = new DirectoriesFileFilter();	
	private final static ClassFilesFileFilter classesFilter = new ClassFilesFileFilter();
	
	private JavaApplicationReader javaApplicationFile;
	private Map<String, Clss> structure;
	private ArrayList<String> emptyPackages;


	public ApplicationStructure(String javaApplicationDirectory){
	    // Archivo de la aplicación Java sobre la que queremos trabajar.
		this.javaApplicationFile = new JavaApplicationReader (javaApplicationDirectory);
		this.structure = new HashMap<String, Clss>();
		this.emptyPackages = new ArrayList<String>();
	}

	/**
	 * @param directory: Inicialmente se trata de la ruta a la carpeta "bin" informada por el usuario. Posteriormente toma el valor de cada una
	 * de las carpetas que conforman la aplicación Java.
	 * 
	 */
	private void fillStructure (File directory){
		//Obtenemos un listado de las clases del directorio que recibimos como parámetro y las introducimos en la estructura de la aplicación
		File[] classes = directory.listFiles(classesFilter);
		if (classes.length > 0){
			this.fillClasses(classes);
		}else if (!directory.getName().equals("bin")){//Guardamos los paquetes que nos encontramos vacíos
			String packageName = directory.getPath().substring(this.javaApplicationFile.getApplicationRootFile().getAbsolutePath().length() + 1);
			this.emptyPackages.add(packageName.replace("\\", "."));
		}
		
		//Obtenemos un listado de los directorios contenidos en el directorio que recibimos como parámetro
		File[] directories = directory.listFiles(directoriesFilter);
		
		//Procesamos cada uno de los directorios para instanciar y procesar aquellas 
		//clases Java correspondientes a los ficheros .class que identifiquemos
		for (int i = 0; i < directories.length; i++){
			this.fillStructure(directories[i]);
		}
	}
	
	
	/**
	 * Este método ejecuta el método "fillStructure (File directory)" pero sobre la ruta raíz de
	 * la aplicación. Se utiliza sólo una vez, al iniciarse el programa.
	 */
	public void fillStructure (){
		this.fillStructure(this.javaApplicationFile.getApplicationRootFile());
		this.javaApplicationFile.close();
	}
	
	
	/**
	 * @param classes: conjunto de clases contenidas en un directorio
	 * @param directoryName: nombre del directorio (Nombre del paquete)
	 * 
	 * Carga e instancia todas las clases contenidas en el directorio "directoryName";
	 * posteriormente calcula sus dependencias y las almacena en la estructura "structure".
	 * 
	 */
	private void fillClasses(File[] classes) {
		// Para cada clase encontrada en la aplicación Java:
		for (int i = 0; i < classes.length; i++) {

			// 1 - Cargamos la clase Java desde su fichero .class
			// Calculamos la ruta a la clase Java desde la carpeta "bin"
			// informada por el usuario:
			String classPath = classes[i].getAbsolutePath()
					.substring(this.javaApplicationFile.getApplicationRootFile().getAbsolutePath().length() + 1);
			classPath = classPath.substring(0, classPath.indexOf("."));
			Class<?> clssFromFile = this.javaApplicationFile.getClassFromFile(classPath.replace("\\", "."));

			// 2 - Instanciamos la clase Java:
			Clss clss = new Clss(clssFromFile, clssFromFile.getPackage().getName());

			// 3 - Calculamos las dependencias de la clase:
			clss.calculateRelationships();

			// 4 - Introducimos en la estructura de la aplicación el par
			// "nombreClase" - "clss" (que la representa):
			structure.put(clssFromFile.getName(), clss);
		}
	}
	
	/**
	 * @return mapa con las relaciones existentes entre los paquetes que conforman la aplicación.
	 */
	public Map<String, ArrayList<String>> getPackageRelationships() {
		Map<String, ArrayList<String>> packageRelationships = new HashMap<String, ArrayList<String>>();
		// Recorremos todas las Clases de la aplicación Java
		for (Map.Entry<String, Clss> entry : this.structure.entrySet()) {
			String packageName = this.getPackage(entry.getKey());
			// Identificamos cualquier tipo de relación con clases
			// que pertenecen a un paquete distinto al de la clase que estamos
			// analizando.
			ArrayList<String> relatedPackages = this.getRelatedPackages(entry.getValue().getRelationships(),
					this.getPackage(entry.getKey()));

			// Si no se han identificado las relaciones para el paquete de esta
			// clase, añadimos una entrada para dicho paquete en la estructura de
			// relaciones entre paquetes.
			if (packageRelationships.get(packageName) == null) {
				packageRelationships.put(packageName, new ArrayList<String>());
			}

			// Si el registro ya existía, recorremos los paquetes relacionados
			// añadiendo en la estructura de relaciones entre paquetes,
			// aquellos que no estabajan incluidos en el listado de paquetes
			// relacionados con el paquete de la clase que estamos analizando:
			for (String relatedPackage : relatedPackages) {
				if (!existRelatedPackage(packageRelationships.get(packageName), relatedPackage)) {
					packageRelationships.get(packageName).add(relatedPackage);
				}
			}
		}
		return packageRelationships;
	}

	
	/**
	 * @param relatedPackages: listado de nombres de paquetes
	 * @param relatedPackage: nombre de un paquete
	 * @return true si el paquete recibido en el parámetro "relatedPackage"
	 * se encuentra en el listado recibido en el parámetro "relatedPackages".
	 */
	private boolean existRelatedPackage (ArrayList<String> relatedPackages, String relatedPackage) {
		boolean exist = false;
		for (String relPackage : relatedPackages){
			if (relatedPackage.equals(relPackage)){
				exist = true;
			}
		}
		return exist;
	}
	
	
	/**
	 * @param relatedClasses: mapa con las relaciones de una clase
	 * @param packageName: paquete al que pertenece la clase cuyas relaciones recibimos en el 
	 * parámetro "relatedClasses".
	 * @return listado de paquetes relacionados con el paquete recibido como parámtro "packageName".
	 */
	private ArrayList<String> getRelatedPackages(Map<String, Relationship> relatedClasses, String packageName){
		ArrayList<String> relatedPackages = new ArrayList<String>();
		
		//Recorremos todas las clases relacionadas:
		for (Map.Entry<String,Relationship> entry : relatedClasses.entrySet()){
			//Recuperamos el nombre del paquete al que pertenece la clase relacionada:
			String relatedPackageName = this.getPackage(entry.getKey());
			//Comprobamos que el paquete que la contiene sea distinto al 
			//recibido como parámetro en "packageName":
			if (relatedPackageName != null // La clase relacionada forma parte del proyecto
					&& !relatedPackageName.equals(packageName) // El paquete de la clase relacionada no es el mismo que el recibido por parámetro
					&& !this.isRelatedPackage(relatedPackages, relatedPackageName)){
				//Insertamos el nombre del paquete relacionado en el listado de paquetes relacionados que devolveremos:
				relatedPackages.add(relatedPackageName);
			}
		}
		return relatedPackages;
	}
	
	/**
	 * @param relatedPackages: listado de paquetes
	 * @param relatedPackageName: nombre de un paquete 
	 * @return true si el paquete recibido en el parámetro "relatedPackageName"
	 * esta contenido en el listado recibido en el parámetro "relatedPackages"; 
	 * se devuelve false en caso contrario.
	 */
	private boolean isRelatedPackage (ArrayList<String> relatedPackages, String relatedPackageName){
		boolean isRelatedPackage = false;
		for (String relatedPackage : relatedPackages){
			if (relatedPackage.equals(relatedPackageName)){
				return true;
			}
		}
		return isRelatedPackage;
	}
	
	/**
	 * @param packageName: nombre de un paquete de la aplicación Java
	 * @return true si existe en la aplicación un paquete que use el
	 * paquete pasado por parámetro "packageName".
	 */
	public boolean existDependentPackage(String packageName){
		
		boolean exist = false;
		for (Map.Entry<String, ArrayList<String>> entry : this.getPackageRelationships().entrySet()){
			if (!entry.getKey().equals(packageName) && entry.getValue().contains(packageName)){
				return true;
			}
		}
		return exist;
	}
	
	
	/**
	 * @param className: nombre de la clase
	 * @return el nombre del paquete que contiene la clase informada como parámetro.
	 * 
	 * Este método se utiliza para identificar las relaciones entre paquetes de la aplicación
	 * Java que se está analizando.
	 */
	public String getPackage (String className){
		if (this.isPartOfApplication(className)){
			return structure.get(className).getPackageName();
		}else{
			return null;
		}
	}
	
	/**
	 * @return listado de paquetes que conforman la aplicación Java que estamos analizando.
	 */
	public ArrayList<String> getApplicationPackages(){
		ArrayList<String> packagesList = new ArrayList<String>();
		for(Map.Entry<String, ArrayList<String>> entry : this.getPackageRelationships().entrySet()){
			packagesList.add(entry.getKey());
		}
		return packagesList;
	}
	
	
	/**
	 * @param packageName: Nombre de un paquete de una aplicación Java
	 * @return listado de clases del paquete "packageName" pasado como parámetro.
	 */
	public Map <String, Clss> getPackageClasses(String packageName){
		Map <String, Clss> packageClasses = new HashMap<String, Clss>();
		for (Map.Entry<String,Clss> clssEntry : this.structure.entrySet()){
			if (this.getPackage(clssEntry.getKey()).equals(packageName)){
				packageClasses.put(clssEntry.getKey(), clssEntry.getValue());
			}
		}
		return packageClasses;
	}

	/**
	 * @param className: nombre de una clase Java
	 * @return true si la clase pertenece a la aplicación Java que representa
	 * esta estructura
	 */
	public boolean isPartOfApplication (String className){
		return this.structure.containsKey(className);
	}
	

	/**
	 * @return listado de paquetes vacíos de la aplicación
	 */
	public ArrayList<String> getEmptyPackages(){
		return this.emptyPackages;
	}
	
	/**
	 * @param className: nombre de una clase
	 * @return el objeto de tipo Clss correspondiente con la
	 * clase cuyo nombre es el recibido en el parámetro "className".
	 */
	public Clss getClss (String className){
		return this.structure.get(className);
	}
	
	/**
	 * @param originClass: clase origen de una relación entre clases
	 * @param destinationClass: clase destino de una relación entre clases
	 * @param applicationStructure: estructura de una aplicación Java con la 
	 * información de todas las clases que la componen.
	 * @return true si existe una relación de asociación entre la clase origen 
	 * recibida en el parámetro "originClass" y la clase destino recibida en el 
	 * parámetro "destinationClass".
	 */
	public boolean existAssociationRelationship(String originClass, String destinationClass){
		boolean exist = false;
		Map<String, Relationship> classRelationships = this.getClss(originClass).getRelationships();
		for (Map.Entry<String, Relationship> relationship : classRelationships.entrySet()){
			if ((relationship.getKey().equals(destinationClass) && relationship.getValue().equals(Relationship.ASSOCIATION)) 
					|| (relationship.getKey().equals(destinationClass) && relationship.getValue().equals(Relationship.ASSOCIATION_MANY))){
				exist = true;
			}
		}
		return exist;		
	}
	
	/**
	 * @return la estructura de la aplicación con la información de todas las clases que la componen.
	 */
	public Map<String, Clss> getStructure (){
		return this.structure;
	}

	
	/**
	 * @param principalClassName: nombre de una clase Java
	 * @return mapa que contiene la clase Java recibida en el parámetro 
	 * "principalClassName", las clases que la consumen y las clases consumidas por esta.
	 */
	public Map <String, Clss> getContextClasses(String principalClassName){
		Map <String, Clss> contextClasses = new HashMap<String, Clss>();
		contextClasses.put(principalClassName, this.getClss(principalClassName));
		
		for (Map.Entry<String, Relationship> relationship : this.getClss(principalClassName).getRelationships().entrySet()){
			if (this.isPartOfApplication(relationship.getKey())){
				contextClasses.put(relationship.getKey(), this.getClss(relationship.getKey()));
			}
		}
		
		for (Map.Entry<String, Clss> clss : this.structure.entrySet()){
			if (clss.getValue().isRelated(principalClassName)){
				contextClasses.put(clss.getKey(), clss.getValue());
			}
		}
		return contextClasses;
	}
	
	/**
	 * @param className: nombre de una clase Java
	 * @return mapa con las clases que consumen la clase recibida como parámetro,
	 * a excepción de las clases que puedan implementar la clase recibida como 
	 * parámetro.
	 */
	public Map <String, Clss> getClassCallers(String className){
		Map <String, Clss> classCallers = new HashMap<String, Clss>();
		
		for (Map.Entry<String, Clss> clss : this.structure.entrySet()){
			if (clss.getValue().isRelated(className) && !clss.getValue().isRelated(className, Relationship.IMPLEMENTATION)){
				classCallers.put(clss.getKey(), clss.getValue());
			}
		}
		return classCallers;
	}
	
	
	/**
	 * @param className: nombre de una clase Java
	 * @return mapa con las clases consumidas por la recibida como parámetro,
	 * a excepción de las clases que puedan ser implementadas por la clase 
	 * recibida como parámetro, y aquellas clases externas que no forman 
	 * parte de la aplicación Java.
	 */
	public Map <String, Clss> getCalledClasses(String className){
		Map <String, Clss> calledClasses = new HashMap<String, Clss>();
		
		for (Map.Entry<String, Relationship> relationship : this.getClss(className).getRelationships().entrySet()){
			if (this.isPartOfApplication(relationship.getKey()) 
					&& !relationship.getValue().equals(Relationship.IMPLEMENTATION)){
				calledClasses.put(relationship.getKey(), this.getClss(relationship.getKey()));
			}
		}
		return calledClasses;
	}
	
	
	/**
	 * @param destinationPath: ruta a la carpeta destino donde se almacenan los ficheros PlantUML generados.
	 * 
	 * Identifica las clases controladoras de la aplicación, y genera el fichero PlantUML correspondiente 
	 * a un diagrama de clases por cada una de ellas. Dichos diagramas contendrán todas
	 * las clases que consumen el controlador y aquellas que son consumidas por el controlador.
	 */
	public void generatePlantUMLControllersClassDiagrams(String destinationPath, AdjustmentsManager adjustmentsManager){
		for (Map.Entry<String, Clss> clss : this.structure.entrySet()){
			if (!Modifier.isAbstract(clss.getValue().getClazz().getModifiers()) && clss.getKey().endsWith("Controller")){
				String controllerClassDiagramName = clss.getValue().getClazz().getSimpleName() + "ClassDiagram";
				PlantUMLWriter controllerClassDiagramFile = new PlantUMLWriter (destinationPath + "/PlantUML/ControllersClassDiagrams/" + controllerClassDiagramName + ".txt");
				Map <String, Clss> controllerDiagramClasses = this.getContextClasses(clss.getKey());
				DiagramAdjustments diagramAdjustments = adjustmentsManager.getDiagramAdjustments(controllerClassDiagramName);
				controllerClassDiagramFile.generatePlantUMLClassDiagram(clss.getKey(), controllerDiagramClasses, this, diagramAdjustments);
				controllerClassDiagramFile.closePlantUMLFile();
			}
		}
	}

	/**
	 * @param destinationPath: ruta a la carpeta destino donde se almacenan los ficheros 
	 * PlantUML generados.
	 * 
	 * Genera la estructura de carpetas con los ficheros PlantUML
	 * correspondientes a los diagramas de clases de los distintos paquetes
	 * de la aplicación Java.
	 */
	/**
	 * @param destinationPath
	 */
	public void generatePackagePlantUMLClassDiagrams(String destinationPath, AdjustmentsManager adjustmentsManager) {
		ArrayList<String> applicationPackages = this.getApplicationPackages();
		for (String packageName : applicationPackages){
			Map <String, Clss> packageClasses = this.getPackageClasses(packageName);
			String packagePath = packageName.replace(".", "/");
			String packageSimpleName = packageName.substring(packageName.lastIndexOf(".") + 1, packageName.length());
			
			// Creamos la estructura de carpetas del paquete
			File packageClassDiagramsDirectory = new File(String.valueOf(destinationPath + "/PlantUML/" + packagePath));
			packageClassDiagramsDirectory.getParentFile().mkdirs();
			packageClassDiagramsDirectory.mkdir();
			
			// Generamos el diagrama de clases del paquete
			String packageClassDiagramName = packageSimpleName + "PackageClassDiagram";
			PlantUMLWriter packageClassDiagramFile = new PlantUMLWriter (destinationPath + "/PlantUML/" + packagePath + "/" + packageClassDiagramName + ".txt");
			DiagramAdjustments packageClassDiagramAdjustments = adjustmentsManager.getDiagramAdjustments(packageClassDiagramName);
			packageClassDiagramFile.generatePlantUMLClassDiagram(null, packageClasses, this, packageClassDiagramAdjustments);
			packageClassDiagramFile.closePlantUMLFile();
			
			// Generamos el diagrama de contexto del paquete
			String contextPackageDiagramName = packageSimpleName + "ContextPackageDiagram";
			PlantUMLWriter contextPackageDiagramFile = new PlantUMLWriter (destinationPath + "/PlantUML/" + packagePath + "/" + contextPackageDiagramName + ".txt");
			Map <String, Clss> contextPackageDiagramClasses = new HashMap<String, Clss>();
			for (Map.Entry<String, Clss> packageClass : packageClasses.entrySet()){
				contextPackageDiagramClasses.putAll(this.getContextClasses(packageClass.getKey()));
			}
			DiagramAdjustments contextPackageDiagramAdjustments = adjustmentsManager.getDiagramAdjustments(contextPackageDiagramName);
			contextPackageDiagramFile.generatePlantUMLClassDiagram(null, contextPackageDiagramClasses, this, contextPackageDiagramAdjustments);
			contextPackageDiagramFile.closePlantUMLFile();
			
			// Generamos los diagramas de contexto de las classes contenidas en el paquete
			for (Map.Entry<String, Clss> clss : this.getPackageClasses(packageName).entrySet()){
				String contextClassDiagramName= clss.getValue().getClazz().getSimpleName() + "ContextClassDiagram";
				PlantUMLWriter contextClassDiagramFile = new PlantUMLWriter (destinationPath + "/PlantUML/" + packagePath + "/" + contextClassDiagramName + ".txt");
				DiagramAdjustments contextClassDiagramAdjustments = adjustmentsManager.getDiagramAdjustments(contextClassDiagramName);
				contextClassDiagramFile.generatePlantUMLContextClassDiagram(clss.getValue().getClazz().getName(), this, contextClassDiagramAdjustments);
				contextClassDiagramFile.closePlantUMLFile();
				
			}
		}
	}
}
