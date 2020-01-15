/**
 * 
 */
package models;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import controllers.ApplicationStructure;
import controllers.Clss;

/**
 * @author Rafael Armesilla Sánchez
 *
 */
public class PlantUMLWriter {

	private PrintWriter pw;
	
	public PlantUMLWriter(String plantUMLUrl){
		
		this.pw = new PrintWriter(new BufferedWriter(this.getFileWriter(plantUMLUrl)));
	}
	
	/**
	 * @param plantUMLUrl: ruta del fichero de texto donde se escribe el código PlantUML
	 * @return clase FileWriter que sirve para escribir secuencias de caractéres en ficheros
	 */
	private FileWriter getFileWriter(String plantUMLUrl){
		FileWriter fw = null;
		try {
			// Apertura del fichero PlantUML
			fw = new FileWriter(plantUMLUrl, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fw;
	}
	
	/**
	 * @param applicationStructure: estructura de una palicación Java con la informacion de todas las clases que la componen.
	 * @param printPackagePublicClasses: indica si se ha de pintar las clases públicas en el diagrama de paquetes.
	 * 
	 * A partir de la estructura de una aplicación Java, genera el código PlantUML correspondiente a su diagrama de paquetes.
	 *
	 */
	public void generatePlantUMLPackageDiagram(ApplicationStructure applicationStructure, boolean printPackagePublicClasses) {
		Map<String, ArrayList<String>> packageRelationships = applicationStructure.getPackageRelationships();
		ArrayList<String> emptyPackages = applicationStructure.getEmptyPackages();
				
		pw.println("-----------------------------------------------------------------------------------");
		pw.println("------------------------------Diagrama de Paquetes---------------------------------");
		// Pintamos el inicio del documento PlantUML:
		pw.println("@startuml");
		
		// Pintamos paquetes vacíos o no relacionados:
		pw.println("together {");
		for (String emptyPackage : emptyPackages){
			pw.print("package ");
			pw.print(emptyPackage);
			pw.println(" <<Folder>> {");
			pw.println("}");
		}
		for (Map.Entry<String, ArrayList<String>> packageFolder : packageRelationships.entrySet()) {
			if(packageFolder.getValue().isEmpty() && !applicationStructure.existDependentPackage(packageFolder.getKey())){
				pw.print("package ");
				pw.print(packageFolder.getKey());
				pw.println(" <<Folder>> {");
				pw.println("}");
			}
		}		
		pw.println("}");
			
		// Recorremos los paquetes de la aplicación Java que están relacionados
		// y vamos pintandolos junto con sus relaciones con otros paquetes

		// Pintamos el paquete:
		for (Map.Entry<String, ArrayList<String>> packageFolder : packageRelationships.entrySet()) {
			pw.print("package ");
			pw.print(packageFolder.getKey());
			pw.println(" <<Folder>> {");
			
			// Pintamos las clases públicas del paquete:
			if (printPackagePublicClasses){
				this.printPublicClasses(applicationStructure, packageFolder.getKey());
			}

			pw.println("}");
			// Pintamos sus relaciones:
			for (String relatedPackage : packageRelationships.get(packageFolder.getKey())) {
				pw.print(packageFolder.getKey() + " ..> ");
				pw.println(relatedPackage);
			}
			pw.println();
		}
		// Pintamos el final del documento PlantUML:
		pw.println("@enduml");
		pw.println("---------------------------------------FIN-----------------------------------------");
		pw.println("-----------------------------------------------------------------------------------");
		pw.println();

	}
	
	/**
	 * @param applicationStructure: estructura que almacena el conjunto de clases de una aplicación Java
	 * @param packageName: nombre de un paquete de una aplicación Java
	 */
	private void printPublicClasses(ApplicationStructure applicationStructure, String packageName){
		ArrayList<Clss> packageClasses = applicationStructure.getPackageClasses(packageName);
		for (Clss packageClass : packageClasses){
			if (Modifier.isPublic(packageClass.getClss().getModifiers())){
				pw.println("class " + packageClass.getClss().getSimpleName());
			}
		}
	}

	/**
	 * Cierra el ficheto de texto donde se escribe el código PlantUML.
	 */
	public void closePlantUMLFile(){
		pw.close();
	}
	
	/**
	 * @param applicationStructure: estructura de una palicación Java con la informacion de todas las clases que la componen.
	 *  
	 * A partir de la estructura de una aplicación Java, genera el código PlantUML correspondiente a un diagrama de clases
	 * por cada uno de los paquetes definidos.
	 *
	 */
	public void generatePlantUMLClassDiagrams(ApplicationStructure applicationStructure) {
		
		// Recorremos el listado de paquetes que conforman la aplicación Java
		ArrayList<String> applicationPackages = applicationStructure.getApplicationPackages();
		for (String packageName : applicationPackages){
			pw.println("-----------------------------------------------------------------------------------");
			pw.println("--------------- Diagrama de Clases del paquete: " + packageName + " ---------------");
			pw.println("@startuml");
			// Recorremos todas las clases de cada paquete
			ArrayList<Clss> packageClasses = applicationStructure.getPackageClasses(packageName);
			for (Clss packageClss : packageClasses){
				// Pintamos la clase
				this.printClass(packageClss, applicationStructure, null);
				// Pintamos las relaciones de la clase
				this.validateAndPrintClassRelationships(packageClss, applicationStructure);
			}
			pw.println("@enduml");
			pw.println("--------------- FIN Diagrama de Clases del paquete: " + packageName + " --------------");
			pw.println("--------------------------------------------------------------------------------------");
			pw.println();
		}
	}
	
	/**
	 * @param clss: objeto de tipo Class<?> que representa una de las clases de una aplicación Java
	 * 
	 * Partiendo de la clase recibida en el parámetro "clss", genera el código PlantUML correspondiente
	 * a la clase y a sus relaciones con otras clases del paquete que la contiene. A la hora de generar 
	 * el código PlanUML de la clase, se mostrarán todos los atributos y métodos de la clase, 
	 * reflejando su visibilidad, tipos, y parámetros en el caso de los métodos.
	 */
	private void printClass(Clss clss, ApplicationStructure applicationStructure, Map <String, Clss> controllerDiagramClasses){
		this.printClassHeader (clss.getClss());
		if (clss.getClss().isEnum()){
			this.printEnumElements (clss.getClss());
		} else if (!clss.getClss().isInterface()){
			this.printClassFields (clss, applicationStructure, controllerDiagramClasses);
		}
		if (!clss.getClss().isEnum()){
			this.printClassMethods(clss, controllerDiagramClasses);
		}
		this.printClassFooter();
	}
	
	
	/**
	 * @param clss: objeto de tipo Class<?> que representa una de las clases de una aplicación Java.
	 * 
	 * Genera el código PlantUML correspondiente a la cabecera de la clase Java que recibimos en el 
	 * parámetro "clss" diferenciando entre enumerados, interfaces, clases abstractas y clases usuales.
	 * 
	 */
	private void printClassHeader(Class<?> clss){
		if (clss.isInterface()){
			pw.println("interface " + clss.getName() + " {");
		} else if (clss.isEnum()){
			pw.println("enum " + clss.getName() + " {");
		} else if (Modifier.isAbstract(clss.getModifiers())){
			pw.println("abstract class " + clss.getName() + " {");
		} else{
			pw.println("class " + clss.getName() + " {");
		}
	}
	
	/**
	 * Escribe el código PlantUML correspondiente al final de la definición de una clase Java.
	 */
	private void printClassFooter (){
		pw.println("}");
	}
	
	/**
	 * @param packageClss: objeto de tipo Clss que representa una clase de una aplicación Java.
	 * 
	 * Genera el código PlantUML correspondiente a los atributos de una clase.
	 * 
	 */
	private void printClassFields (Clss packageClss, ApplicationStructure applicationStructure, Map <String, Clss> controllerDiagramClasses){
		Class<?> clss = packageClss.getClss();
		Field[] fields = clss.getDeclaredFields();
		for (Field field : fields){
			if (PrimitiveTypesAndString.isPrimitiveOrString(field.getType())					
					|| this.printableField(packageClss, field, applicationStructure, controllerDiagramClasses)){
				if (!field.getName().equals("$assertionsDisabled")){
					this.printField(packageClss, field);
				}
			}
		}
		pw.println();
	}

	
	/**
	 * @param packageClss: objeto de tipo Clss que representa una clase de una aplicación Java.
	 * @param field: atributo de una clase de la aplicación
	 * @return true si el conjunto de clases que componen el atributo recibido
	 * como parámetro "field", pertenece a un paquete distinto al que contiene
	 * la clase recibida en el parámetro "packageClss"; false en caso contrario.
	 */
	private boolean printableField(Clss clss, Field field, ApplicationStructure applicationStructure, Map <String, Clss> controllerDiagramClasses){
		boolean printable = false;
		String classPackage = clss.getPackageName();
		ArrayList<Class<?>> fieldClasses = clss.getFieldClasses(field);
		if (!fieldClasses.isEmpty()){
			for (Class<?> fieldClass : fieldClasses){
				if (controllerDiagramClasses != null && controllerDiagramClasses.containsKey(fieldClass.getName())){
					printable = true;		
				}else if (!applicationStructure.isPartOfApplication(fieldClass.getName()) || !applicationStructure.getPackage(fieldClass.getName()).equals(classPackage)){
					printable = true;
				}
			}
		} else {
			printable = true;
		}
		return printable;
	}
	
	
	/**
	 * @param field: atributo de una clase Java
	 * 
	 * Genera el código PlantUML correspondiente al atributo recibido en
	 * en el parámetro "field".
	 * 
	 */
	private void printField(Clss clss, Field field){
		pw.print("{field} ");
		this.printFieldVisibility(field);
		pw.print(field.getName() + " : ");	
		if (field.getGenericType().getClass().getSimpleName().equals("ParameterizedTypeImpl")) {
			ArrayList<Class<?>> fieldClasses = clss.getFieldClasses(field);
			pw.print(field.getType().getSimpleName() + " { ");
			for (Class<?> fieldClass : fieldClasses){
				pw.print(fieldClass.getSimpleName() + " ");
			}
			pw.println("}");
		} else {
			pw.println(field.getType().getSimpleName());
		}
	}
	
	
	/**
	 * @param field: atributo de una clase de una aplicación
	 * 
	 * Genera el código PlantUML correspondiente a la visibilidad del
	 * atributo pasado en el parámetro "field".
	 * 
	 */
	private void printFieldVisibility(Field field){
		if (Modifier.isStatic(field.getModifiers())) {
			pw.print("{static} ");
		}
		
		if (Modifier.isPrivate(field.getModifiers())){
			pw.print("-");
		}else if (Modifier.isPublic(field.getModifiers())){
			pw.print("+");
		}else if (Modifier.isProtected(field.getModifiers())){
			pw.print("#");
		}else{
			pw.print("~");
		}
		
		if (Modifier.isFinal(field.getModifiers())) {
			pw.print(" final ");
		}
	}
	
	/**
	 * @param clss: objeto de tipo Class<?> que representa una de las clases de una aplicación Java.
	 * 
	 * Genera el código PlantUML correspondiente a los valores definidos para un enumerado
	 * 
	 */
	private void printEnumElements (Class<?> clss){
		Object[] objects = clss.getEnumConstants();
		for(Object obj : objects){
			pw.println(obj);
		}
	}
	
	/**
	 * @param clss: objeto de tipo Class<?> que representa una de las clases de una aplicación Java.
	 * 
	 * Genera el código PlantUML correspondiente a los métodos declarados en la
	 * clase pasada como parámetro "clss".
	 * 
	 */
	private void printClassMethods(Clss clss, Map <String, Clss> controllerDiagramClasses){
		Method[] methods = clss.getClss().getDeclaredMethods();
		for (Method method : methods){
			if (!clss.isMethodOverridden(method, controllerDiagramClasses)){
				pw.print("{method} ");
				this.printMethodVisibility(method);
				pw.print(method.getName() + "(");
				this.printMethodParameters(clss, method);
				pw.print("): ");
				this.printMethodReturnType(clss, method);
			}
		}
		pw.println();
	}
	
	/**
	 * @param clss: objeto de tipo Class<?> que representa una de las clases de una aplicación Java.
	 * @param method: método de una clase Java
	 * 
	 * Genera el código PlantUML correspondiente al objeto que devuelve el método recibido en el 
	 * parámetro "method".
	 * 
	 */
	private void printMethodReturnType(Clss clss, Method method){
		if (method.getGenericReturnType().getClass().getSimpleName().equals("ParameterizedTypeImpl")){
			pw.print(method.getReturnType().getSimpleName() + "<");
			ArrayList<Class<?>> returnTypeClasses = clss.getParametrizedParameterClasses(method.getGenericReturnType());
			for (Class<?>  returnTypeClass : returnTypeClasses){
				pw.print(returnTypeClass.getSimpleName() + " ");	
			}
			pw.println(">");
		} else{
			pw.println(method.getReturnType().getSimpleName());
		}
	}
	
	
	/**
	 * @param clss: objeto de tipo Class<?> que representa una de las clases de una aplicación Java.
	 * @param method: método de una clase de una aplicación Java.
	 * 
	 * Genera el código PlantUML correspondiente a un método de una clase Java.
	 * 
	 */
	private void printMethodParameters(Clss clss, Method method){
		Parameter[] parameters = method.getParameters();
		int i = 0;
		for (Parameter parameter : parameters){
		    if(i++ != parameters.length - 1){
				this.printParameterType (clss, parameter);
				pw.print(" " + parameter.getName() + ", ");
		    }else{
				this.printParameterType (clss, parameter);
				pw.print(" " + parameter.getName());
		    }
		}
	}
	
	
	/**
	 * @param clss: objeto de tipo Class<?> que representa una de las clases de una aplicación Java.
	 * @param parameter: parametro de uno de los métodos de la clase recibida en el parámetro "parameter".
	 * 
	 * Genera el código PlantUML correspondiente al tipo de un parámetro de un método de una clase.
	 * 
	 */
	private void printParameterType (Clss clss, Parameter parameter){
		if (parameter.getParameterizedType().getClass().getSimpleName().equals("ParameterizedTypeImpl")){
			pw.print(parameter.getType().getSimpleName() + "<");
			ArrayList<Class<?>> parameterClasses = clss.getParametrizedParameterClasses(parameter.getParameterizedType());
			for (Class<?> parameterClass : parameterClasses){
				pw.print(parameterClass.getSimpleName() + " ");	
			}
			pw.print(">");
		}else{
			pw.print(parameter.getType().getSimpleName());
		}
	}
	
	/**
	 * @param clss: objeto de tipo Clss que representa una de las clases de una aplicación Java.
	 * @param applicationStructure: estructura de una aplicación Java con la informacion de todas las clases que la componen.
	 * 
	 * Valida si las relaciones entre la clase recibida en el parámetro "clss" y otras clases, 
	 * se han de pintar en función de:
	 * 
	 *  - La pertenencia de estas últimas a la aplicación y al paquete que estamos tratando.
	 *  - Si la clase "clss" hereda de otra que tiene definido el mismo tipo de relación
	 *    con la misma clase destino.
	 * 
	 */
	private void validateAndPrintClassRelationships(Clss clss, ApplicationStructure applicationStructure){
		Map<String, Relationship> classRelationships = clss.getRelationships();
		for (Map.Entry<String, Relationship> relationship : classRelationships.entrySet()){
			if (applicationStructure.isPartOfApplication(relationship.getKey()) 
					&& applicationStructure.getPackage(relationship.getKey()).equals(clss.getPackageName())
					&& !isInheritanceRelationship(clss, relationship, applicationStructure)){
				this.printClassRelationships(clss, relationship, applicationStructure);
			}
		}
	}
	
	
	/**
	 * @param clss: objeto de tipo Clss que representa una de las clases de una aplicación Java.
	 * @param controllerDiagramClasses: conjunto de clases relacionadas con una clase controladora
	 * @param applicationStructure: estructura de una aplicación Java con la informacion de todas las clases que la componen.
	 * 
	 * Valida si las relaciones entre la clase recibida en el parámetro "clss" y otras clases, 
	 * se han de pintar en función de:
	 * 
	 *  - La pertenencia de estas últimas al conjunto de clases recibido en el parámetro 
	 *    "controllerDiagramClasses".
	 *  - Si la clase "clss" hereda de otra que tiene definido el mismo tipo de relación
	 *    con la misma clase destino.
	 * 
	 */
	private void validateAndPrintClassRelationships (Clss clss, Map <String, Clss> controllerDiagramClasses, ApplicationStructure applicationStructure){
		Map<String, Relationship> classRelationships = clss.getRelationships();
		for (Map.Entry<String, Relationship> relationship : classRelationships.entrySet()){
			if (controllerDiagramClasses.containsKey(relationship.getKey())
					&& !isInheritanceRelationship(clss, relationship, applicationStructure)){
				this.printClassRelationships(clss, relationship, applicationStructure);
			}
		}
	}
	
	/**
	 * @param clss: objeto de tipo Clss que representa una de las clases de una aplicación Java.
	 * @param relationship: tipo de relacion entre dos clases Java.
	 * @return true si existe una relación heredada por la clase recibida en el parámetro "clss"
	 * con el mismo tipo de relación recibido en el parámetro "relationship". Se devolverá false 
	 * en caso contrario.
	 */
	private boolean isInheritanceRelationship(Clss clss, Entry<String, Relationship> relationship, ApplicationStructure applicationStructure) {
		Class<?> currentClass = clss.getClss().getSuperclass();
		boolean inherited = false;
		while (currentClass != Object.class && currentClass != null && !inherited) {

			if (applicationStructure.isPartOfApplication(currentClass.getName()) && applicationStructure
					.getClass(currentClass.getName()).isRelated(relationship.getKey(), relationship.getValue())) {
				inherited = true;
				break;
			}
			currentClass = currentClass.getSuperclass();
		}
		// Comprobamos la pertenencia de la clase que define la relación 
		// al paquete que estamos tratando o al conjunto de clases 
		// del diagrama de clases del controlador que estamos tratando:
/*		if (inherited 
				&& controllerDiagramClasses != null
				&& !controllerDiagramClasses.containsKey(currentClass.getName())){
			inherited = false;
		}else if (inherited 
				&& controllerDiagramClasses == null
				&& !clss.getClss().getPackage().getName().equals(currentClass.getPackage().getName())){
			inherited = false;
		}*/
		return inherited;
	}
	
	/**
	 * @param clss: objeto de tipo Clss que representa una de las clases de una aplicación Java.
	 * @param relationship: relacion entre dos clases Java
	 * @param applicationStructure: estructura de una aplicación Java con la informacion de todas las clases que la componen.
	 * 
	 * Genera el código PlantUMLcorrespondiente a la relacion recibida en el parámetro "relationship" 
	 * 
	 */
	private void printClassRelationships (Clss clss, Entry<String, Relationship> relationship, ApplicationStructure applicationStructure){
		if (relationship.getValue().equals(Relationship.ASSOCIATION) 
				|| relationship.getValue().equals(Relationship.ASSOCIATION_MANY)){
			this.printRelationship(clss.getClss().getName(), relationship.getKey(), relationship.getValue(), applicationStructure);
		}else {
			this.printRelationship(clss.getClss().getName(), relationship.getKey(), relationship.getValue());
		}
	}
		
	/**
	 * @param originClass: clase origen de una relación entre clases.
	 * @param destinationClass: clase destino de una relación entre clases.
	 * @param relationship: tipo de relación y cardinalidad entre clases.
	 * @param applicationStructure: estructura de una aplicación Java con la informacion de todas las clases que la componen.
	 * 
	 * Genera el código PlantUML correspondiente a una relación de asociación entre clases, reflejando la cardinalidad.
	 * 
	 */
	@SuppressWarnings("incomplete-switch")
	private void printRelationship(String originClass, String destinationClass, Relationship relationship,
			ApplicationStructure applicationStructure) {
		boolean isBidirectional = applicationStructure.existAssociationRelationship(destinationClass, originClass);
		if (isBidirectional) {
			Relationship destinarionClassRelationship = applicationStructure.getClass(destinationClass)
					.getRelationships().get(originClass);
			switch (relationship) {
			case ASSOCIATION:
				switch (destinarionClassRelationship) {
				case ASSOCIATION:
					pw.println(originClass + " \"1\" -- \"1\" " + destinationClass);
					break;
				case ASSOCIATION_MANY:
					pw.println(originClass + " \"*\" -- \"1\" " + destinationClass);
					break;
				}
			case ASSOCIATION_MANY:
				switch (destinarionClassRelationship) {
				case ASSOCIATION:
					pw.println(originClass + " \"1\" -- \"*\" " + destinationClass);
					break;
				case ASSOCIATION_MANY:
					pw.println(originClass + " \"*\" -- \"*\" " + destinationClass);
					break;
				}
			}
		} else {
			this.printRelationship(originClass, destinationClass, relationship);
		}
	}

	
	/**
	 * @param originClass: clase origen de una relación entre clases.
	 * @param destinationClass: clase destino de una relación entre clases.
	 * @param relationship: tipo de relación y cardinalidad (dependiendo del tipo de relación) entre clases.
	 * 
	 * Genera el código PlantUML correspondiente a una relación entre clases, reflejando la cardinalidad 
	 * cuando la relacion es de tipo asociación o composición.
	 * 
	 */
	private void printRelationship(String originClass, String destinationClass, Relationship relationship){
		switch (relationship)
	    {
	      case ASSOCIATION:
				pw.println(originClass + " \"*\" --> \"1\" " + destinationClass);
				break;
	      case ASSOCIATION_MANY:
				pw.println(originClass + " \"*\" --> \"*\" " + destinationClass);
				break;
	      case COMPOSITION:
				pw.println(originClass + " \"1\" *-- \"1\" " + destinationClass);
				break;
	      case COMPOSITION_MANY:
				pw.println(originClass + " \"1\" *-- \"*\" " + destinationClass);
				break;
	      case USAGE:
				pw.println(originClass + " ..> " + destinationClass);
				break;
	      case INHERITANCE:
				pw.println(destinationClass + " <|-down- " + originClass);
				break;
	      case IMPLEMENTATION:
				pw.println(destinationClass + " <|.down. " + originClass);
				break;
	    }
	}
	
	/**
	 * @param method: método de una clase de una aplicación
	 * 
	 * Genera el código PlantUML correspondiente a la visibilidad del
	 * método pasado en el parámetro "method".
	 * 
	 */
	private void printMethodVisibility(Method method) {
		if (Modifier.isAbstract(method.getModifiers())) {
			pw.print("{abstract} ");
		}
		
		if (Modifier.isStatic(method.getModifiers())) {
			pw.print("{static} ");
		}
		
		if (Modifier.isPrivate(method.getModifiers())) {
			pw.print("-");
		} else if (Modifier.isPublic(method.getModifiers())) {
			pw.print("+");
		} else if (Modifier.isProtected(method.getModifiers())) {
			pw.print("#");
		} else {
			pw.print("~");
		}
		
		if (Modifier.isFinal(method.getModifiers())) {
			pw.print(" final ");
		}
	}
	
	/**
	 * @param applicationStructure: estructura de una palicación Java con la informacion de todas las clases que la componen.
	 * 
	 * A partir de la estructura de una aplicación Java, genera el código PlantUML correspondiente a un diagrama de clases
	 * por cada controlador definido en la aplicación. Dichos diagramas contendrán todas las clases que consumen el controlador 
	 * y aquellas que son consumidas por el controlador. 
	 */
	public void generatePlantUMLControllersClassDiagrams(ApplicationStructure applicationStructure){
		for (Map.Entry<String, Clss> clss : applicationStructure.getStructure().entrySet()){
			if (!Modifier.isAbstract(clss.getValue().getClss().getModifiers()) && clss.getKey().endsWith("Controller")){
				this.generatePlantUMLControllerClassDiagram(clss.getKey(), applicationStructure);
			}
		}
	}
	
	/**
	 * @param controllerClassName: nombre de una clase controladora
	 * @param controllerDiagramClasses: conjunto de clases relacionadas con una clase controladora.
	 * 
	 * A partir del conjunto de clases relacionadas con una clase controladora (controllerDiagramClasses), 
	 * genera el código PlantUML correspondiente a un diagrama de clases
	 * 
	 */
	private void generatePlantUMLControllerClassDiagram(String controllerClassName, ApplicationStructure applicationStructure) {
		pw.println("-----------------------------------------------------------------------------------");
		pw.println("--------------- Diagrama de Clases del controlador: " + controllerClassName + " ---------------");
		pw.println("@startuml");
		pw.println("note \"Controlador: " + controllerClassName + "\" as N1");
		// Recorremos todas las clases relacionadas con la clase controladora
		Map <String, Clss> controllerDiagramClasses = applicationStructure.getControllerStructure(controllerClassName);
		for (Map.Entry<String, Clss> clss : controllerDiagramClasses.entrySet()) {
			// Pintamos la clase
			this.printClass(clss.getValue(), applicationStructure, controllerDiagramClasses);
			// Validamos y pintamos las relaciones de la clase 
			this.validateAndPrintClassRelationships(clss.getValue(), controllerDiagramClasses, applicationStructure);
		}
		pw.println("@enduml");
		pw.println("--------------- FIN Diagrama de Clases del controlador: " + controllerClassName + " --------------");
		pw.println("--------------------------------------------------------------------------------------");
		pw.println();
	}
}