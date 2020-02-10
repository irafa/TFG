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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import controllers.ApplicationStructure;
import controllers.Clss;

/**
 * @author Rafael Armesilla S�nchez
 *
 */
public class PlantUMLWriter {

	private PrintWriter pw;
	
	public PlantUMLWriter(String plantUMLUrl){
		this.pw = new PrintWriter(new BufferedWriter(this.getFileWriter(plantUMLUrl)));
	}
	
	/**
	 * @param plantUMLUrl: ruta del fichero de texto donde se escribe el c�digo PlantUML
	 * @return clase FileWriter que sirve para escribir secuencias de caract�res en ficheros
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
	 * @param applicationStructure: estructura de una palicaci�n Java con la informacion de todas las clases que la componen.
	 * @param printPackagePublicClasses: indica si se ha de pintar las clases p�blicas en el diagrama de paquetes.
	 * 
	 * A partir de la estructura de una aplicaci�n Java, genera el c�digo PlantUML correspondiente a su diagrama de paquetes.
	 *
	 */
	public void generatePlantUMLPackageDiagram(ApplicationStructure applicationStructure, boolean printPackagePublicClasses) {
		Map<String, ArrayList<String>> packageRelationships = applicationStructure.getPackageRelationships();
		ArrayList<String> emptyPackages = applicationStructure.getEmptyPackages();
				
		// Pintamos el inicio del documento PlantUML:
		pw.println("@startuml");
		
		// Pintamos paquetes vac�os o no relacionados:
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
			
		// Recorremos los paquetes de la aplicaci�n Java que est�n relacionados
		// y vamos pintandolos junto con sus relaciones con otros paquetes

		// Pintamos el paquete:
		for (Map.Entry<String, ArrayList<String>> packageFolder : packageRelationships.entrySet()) {
			pw.print("package ");
			pw.print(packageFolder.getKey());
			pw.println(" <<Folder>> {");
			
			// Pintamos las clases p�blicas del paquete:
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
	}
	
	/**
	 * @param applicationStructure: estructura que almacena el conjunto de clases de una aplicaci�n Java
	 * @param packageName: nombre de un paquete de una aplicaci�n Java
	 */
	private void printPublicClasses(ApplicationStructure applicationStructure, String packageName){
		for (Map.Entry<String, Clss> packageClass : applicationStructure.getPackageClasses(packageName).entrySet()){
			if (Modifier.isPublic(packageClass.getValue().getClazz().getModifiers())){
				pw.println("class " + packageClass.getValue().getClazz().getSimpleName());
			}
		}
	}

	/**
	 * Cierra el ficheto de texto donde se escribe el c�digo PlantUML.
	 */
	public void closePlantUMLFile(){
		pw.close();
	}
	
	/**
	 * @param applicationStructure: estructura de una aplicaci�n Java con la informacion de 
	 * todas las clases que la componen.
	 * @param controllerClassName: nombre de clase controladora, si aplica; null en caso contrario.
	 * @param diagramClasses: conjunto de clases que conforman el diagrama a generar.
	 * @param diagramAdjustments: ajustes manuales para aplicar al diagrama de clases que se
	 * esta pintando.
	 *  
	 * Genera el c�digo PlantUML correspondiente al diagrama de clases que contiene las clases
	 * recibidas en el par�metro "diagramClasses" y las relaciones entre �stas.
	 *
	 */
	public void generatePlantUMLClassDiagram(String controllerClassName, Map <String, Clss> diagramClasses, ApplicationStructure applicationStructure, DiagramAdjustments diagramAdjustments) {
		pw.println("@startuml");
		// Recorremos todas las clases del diagrama
		for (Map.Entry<String, Clss> diagramClss : diagramClasses.entrySet()) {
			if (diagramAdjustments == null || !diagramAdjustments.isDeleteClass(diagramClss.getKey())){
				// Pintamos la clase
				if (controllerClassName != null && diagramClss.getKey().equals(controllerClassName)){
					this.printClass(diagramClss.getValue(), applicationStructure, diagramClasses, PrintingType.CONTROLLERCLASS, diagramAdjustments);
				}else{
					this.printClass(diagramClss.getValue(), applicationStructure, diagramClasses, PrintingType.STANDARDCLASS, diagramAdjustments);
				}
				// Pintamos las relaciones de la clase
				this.validateAndPrintClassRelationships(diagramClss.getValue(), diagramClasses, applicationStructure, diagramAdjustments);
			}
		}
		// A�adimos las clases nuevas definidas manualmente por el usuario y 
		// sus relaciones
		if (diagramAdjustments != null){
			this.printClassesFromDiagramAdjustments(diagramAdjustments, applicationStructure);			
		}
		pw.println("@enduml");
	}
	
	
	/**
	 * @param applicationStructure: estructura de una aplicaci�n Java con la 
	 * informacion de todas las clases que la componen.
	 * @param className: nombre de una clase Java
	 * @param diagramAdjustments: ajustes manuales para aplicar al diagrama de clases que se
	 * esta pintando.
	 * 
	 * Genera el c�digo PlantUML correspondiente al diagrama de contexto de la 
	 * clase Java cuyo nombre es el recibido en el par�metro "className".
	 *
	 */
	public void generatePlantUMLContextClassDiagram(String className, ApplicationStructure applicationStructure, DiagramAdjustments contextClassDiagramAdjustments) {
		pw.println("@startuml");
		
		Clss principalClss = applicationStructure.getClss(className);
		Map <String, Clss> classCallers = applicationStructure.getClassCallers(className);
		Map <String, Clss> calledClasses = applicationStructure.getCalledClasses(className);
		Map <String, Clss> diagramClasses = new HashMap<String, Clss>();
		diagramClasses.put(className, principalClss);
		diagramClasses.putAll(classCallers);
		diagramClasses.putAll(calledClasses);
		Map <String, Boolean> printedClasses = new HashMap<String, Boolean>();
		
		// Pintamos la clase principal del diagrama:
		pw.println("package PRINCIPAL <<Layout>> {");
		this.printClass(principalClss, applicationStructure, diagramClasses, PrintingType.STANDARDCLASS, contextClassDiagramAdjustments);
		printedClasses.put(className, Boolean.TRUE);
		pw.println("}");
		
		// Pintamos la clase de la que hereda la clase principal (Si existe)
		pw.println("package BASEOF <<Layout>> {");
		for (Map.Entry<String, Clss> calledClass  : calledClasses.entrySet()){
			if (principalClss.isRelated(calledClass.getKey(), Relationship.INHERITANCE)
					&& !printedClasses.containsKey(calledClass.getKey())
					&& (contextClassDiagramAdjustments == null || !contextClassDiagramAdjustments.isDeleteClass(calledClass.getKey()))){
				this.printClass(calledClass.getValue(), applicationStructure, diagramClasses, PrintingType.CONSUMEDCLASS, contextClassDiagramAdjustments);
				printedClasses.put(calledClass.getKey(), Boolean.TRUE);
			}
		}
	    pw.println("}");
		
		// Pintamos las clases que son asociadas de la clase principal (Si existen)
		pw.println("package ASSOCIATEOF <<Layout>> {");
		for (Map.Entry<String, Clss> calledClass  : calledClasses.entrySet()){
			if ((principalClss.isRelated(calledClass.getKey(), Relationship.ASSOCIATION) || principalClss.isRelated(calledClass.getKey(), Relationship.ASSOCIATION_MANY))
					&& !printedClasses.containsKey(calledClass.getKey())
					&& (contextClassDiagramAdjustments == null || !contextClassDiagramAdjustments.isDeleteClass(calledClass.getKey()))){
				this.printClass(calledClass.getValue(), applicationStructure, diagramClasses, PrintingType.CONSUMEDCLASS, contextClassDiagramAdjustments);
				printedClasses.put(calledClass.getKey(), Boolean.TRUE);
			}
		}
	    pw.println("}");
	    
		// Pintamos las clases que son parte de la clase principal (Si existen)
		pw.println("package PARTOF <<Layout>> {");
		for (Map.Entry<String, Clss> calledClass  : calledClasses.entrySet()){
			if ((principalClss.isRelated(calledClass.getKey(),Relationship.COMPOSITION) || principalClss.isRelated(calledClass.getKey(), Relationship.COMPOSITION_MANY))
					&& !printedClasses.containsKey(calledClass.getKey())
					&& (contextClassDiagramAdjustments == null || !contextClassDiagramAdjustments.isDeleteClass(calledClass.getKey()))){
				this.printClass(calledClass.getValue(), applicationStructure, diagramClasses, PrintingType.CONSUMEDCLASS, contextClassDiagramAdjustments);
				printedClasses.put(calledClass.getKey(), Boolean.TRUE);
			}
		}
	    pw.println("}");
	    
		// Pintamos las clases que son usadas por la clase principal (Si existen)
		pw.println("package USAGEBY <<Layout>> {");
		for (Map.Entry<String, Clss> calledClass  : calledClasses.entrySet()){
			if (principalClss.isRelated(calledClass.getKey(),Relationship.USAGE)
					&& !printedClasses.containsKey(calledClass.getKey())
					&& (contextClassDiagramAdjustments == null || !contextClassDiagramAdjustments.isDeleteClass(calledClass.getKey()))){
				this.printClass(calledClass.getValue(), applicationStructure, diagramClasses, PrintingType.CONSUMEDCLASS, contextClassDiagramAdjustments);
				printedClasses.put(calledClass.getKey(), Boolean.TRUE);
			}
		}
	    pw.println("}");

		// Pintamos las clases que estan compuestas de la clase principal (Si existen)
		pw.println("package ALLOF <<Layout>> {");
		for (Map.Entry<String, Clss> clss : classCallers.entrySet()) {
			if ((clss.getValue().isRelated(className, Relationship.COMPOSITION) 
							|| clss.getValue().isRelated(className, Relationship.COMPOSITION_MANY))
					&& !printedClasses.containsKey(clss.getKey())
					&& (contextClassDiagramAdjustments == null || !contextClassDiagramAdjustments.isDeleteClass(clss.getKey()))){
				this.printClass(clss.getValue(), applicationStructure, diagramClasses, PrintingType.CONSUMERCLASS, contextClassDiagramAdjustments);
				printedClasses.put(clss.getKey(), Boolean.TRUE);
			}
		}
	    pw.println("}");
		
		// Pintamos las clases que usan clase principal (Si existen)
		pw.println("package USE <<Layout>> {");
		for (Map.Entry<String, Clss> clss : classCallers.entrySet()) {
			if (clss.getValue().isRelated(className, Relationship.USAGE)
					&& !printedClasses.containsKey(clss.getKey())
					&& (contextClassDiagramAdjustments == null || !contextClassDiagramAdjustments.isDeleteClass(clss.getKey()))){
				this.printClass(clss.getValue(), applicationStructure, diagramClasses, PrintingType.CONSUMERCLASS, contextClassDiagramAdjustments);
				printedClasses.put(clss.getKey(), Boolean.TRUE);
			}
		}
	    pw.println("}");
		
		// Pintamos las clases que estan asociadas a la clase principal (Si existen)
		pw.println("package ASSOCIATETO <<Layout>> {");
		for (Map.Entry<String, Clss> clss : classCallers.entrySet()) {
			if ((clss.getValue().isRelated(className, Relationship.ASSOCIATION) 
							|| clss.getValue().isRelated(className, Relationship.ASSOCIATION_MANY))
					&& !printedClasses.containsKey(clss.getKey())
					&& (contextClassDiagramAdjustments == null || !contextClassDiagramAdjustments.isDeleteClass(clss.getKey()))){
				this.printClass(clss.getValue(), applicationStructure, diagramClasses, PrintingType.CONSUMERCLASS, contextClassDiagramAdjustments);
				printedClasses.put(clss.getKey(), Boolean.TRUE);
			}
		}
	    pw.println("}");

		// Pintamos las clases que extienden la clase principal (Si existen)
		pw.println("package EXTENDSFROM <<Layout>> {");
		for (Map.Entry<String, Clss> clss : classCallers.entrySet()) {
			if (clss.getValue().isRelated(className, Relationship.INHERITANCE)
					&& !printedClasses.containsKey(clss.getKey())
					&& (contextClassDiagramAdjustments == null || !contextClassDiagramAdjustments.isDeleteClass(clss.getKey()))){
				this.printClass(clss.getValue(), applicationStructure, diagramClasses, PrintingType.CONSUMERCLASS, contextClassDiagramAdjustments);
				printedClasses.put(clss.getKey(), Boolean.TRUE);
			}
		}
	    pw.println("}");
	    
	    // Pintamos las clases que ha a�adido el usuario manualmente y
	    // sus relaciones correspondientes:
		if (contextClassDiagramAdjustments != null) {
			this.printClassesFromDiagramAdjustments(contextClassDiagramAdjustments, applicationStructure);
		}
	    
	    //Pintamos el layout con el que se mostrarn las clases:
	    pw.println("skinparam shadowing false");
	    pw.println("skinparam package<<Layout>> {");
	    pw.println("borderColor Transparent");
	    pw.println("backgroundColor Transparent");
	    pw.println("fontColor Transparent");
	    pw.println("stereotypeFontColor Transparent");
	    pw.println("}");
	    
	    pw.println("ASSOCIATETO -r[hidden]-> PRINCIPAL");
	    pw.println("BASEOF -u[hidden]- ASSOCIATETO");
	    pw.println("ASSOCIATETO -u[hidden]- EXTENDSFROM");
	    pw.println("ALLOF -u[hidden]- PRINCIPAL");
	    pw.println("PRINCIPAL -u[hidden]- PARTOF");
	    pw.println("PRINCIPAL -r[hidden]- ASSOCIATEOF");
	    pw.println("USE -u[hidden]- ASSOCIATEOF");
	    pw.println("ASSOCIATEOF -u[hidden]- USAGEBY");
	    	    
		// Validamos y pintamos las relaciones de todas las clases del diagrama:
		for (Map.Entry<String, Clss> clss : diagramClasses.entrySet()) {
			this.validateAndPrintClassRelationships(clss.getValue(), diagramClasses, applicationStructure, contextClassDiagramAdjustments);
		}

		pw.println("@enduml");
	}
	
	
	/**
	 * @param diagramAdjustments: ajustes manuales para aplicar al diagrama de clases que se
	 * esta pintando. Contiene las clases nuevas definidas por el usuario.
	 * @param applicationStructure: estructura de una aplicaci�n Java con la 
	 * informaci�n de todas las clases que la componen.
	 */
	private void printClassesFromDiagramAdjustments(DiagramAdjustments diagramAdjustments, ApplicationStructure applicationStructure){
		for (String classToAdd : diagramAdjustments.getClassesToAdd()) {
			String[] classToAddDefinition = classToAdd.split("_");
			String classToAddName = classToAddDefinition[0];
			String classToAddPrintingType = classToAddDefinition[1];
			String classToAddType = classToAddDefinition[2];

			if (classToAddType.equals("interface")) {
				pw.print("interface " + classToAddName);
			} else if (classToAddType.equals("enum")) {
				pw.print("enum " + classToAddName);
			} else if (classToAddType.equals("abstract class")) {
				pw.print("abstract class " + classToAddName);
			} else {
				pw.print("class " + classToAddName);
			}

			if (classToAddPrintingType.equals("CONSUMEDCLASS")) {
				pw.print(" #red");
			} else if (classToAddPrintingType.equals("CONSUMERCLASS")
					|| classToAddPrintingType.equals("CONTROLLERCLASS")) {
				pw.print(" #green");
			}
			pw.println(" {");

			if (classToAddType.equals("enum")) {
				for (int i = 3; i < classToAddDefinition.length; i++) {
					pw.println(classToAddDefinition[i]);
				}
			} else if (!classToAddType.equals("interface")) {
				this.printFieldsFromDiagramAdjustments(classToAddName, diagramAdjustments);
			}
			if (!classToAddType.equals("enum")) {
				this.printMethodsFromDiagramAdjustments(classToAddName, diagramAdjustments);
			}
			pw.println("}");
			
			// Validamos y pintamos las relaciones de las clases nuevas
			// a�adidas manualmente por el usuario:
			this.printClassRelationshipsFromDiagramAdjustments(classToAddName, diagramAdjustments);
			pw.println("");
		}
	}
	
	/**
	 * @param clss: objeto de tipo Class<?> que representa una de las clases de una aplicaci�n Java.
	 * @param applicationStructure: estructura de una aplicaci�n Java con la 
	 * informacion de todas las clases que la componen.
	 * @param diagramClasses: conjunto de clases del diagrama de clases que se est� pintando.
	 * @param printingType: formato con el que se pinta la clase.
	 * @param diagramAdjustments: ajustes manuales para aplicar al diagrama de clases que se
	 * esta pintando.
	 * 
	 * Genera el c�digo PlantUML correspondiente a la clase recibida en el par�metro "clss". 
	 * A la hora de generar el c�digo PlanUML de la clase, se mostrar�n todos los atributos y m�todos de la clase, 
	 * reflejando su visibilidad, tipos, y par�metros en el caso de los m�todos.
	 */
	private void printClass(Clss clss, ApplicationStructure applicationStructure, Map <String, Clss> diagramClasses, PrintingType printingType, DiagramAdjustments diagramAdjustments){
		this.printClassHeader (clss.getClazz(), printingType);
		if (clss.getClazz().isEnum()){
			this.printEnumElements (clss.getClazz());
		} else if (!clss.getClazz().isInterface()){
			this.printClassFields (clss, applicationStructure, diagramClasses, diagramAdjustments);
			if (diagramAdjustments != null){
				this.printFieldsFromDiagramAdjustments(clss.getClazz().getName(), diagramAdjustments);
			}
		}
		if (!clss.getClazz().isEnum()){
			this.printClassMethods(clss, diagramClasses, diagramAdjustments);
			if (diagramAdjustments != null){
				this.printMethodsFromDiagramAdjustments(clss.getClazz().getName(), diagramAdjustments);
			}
		}
		this.printClassFooter();
	}
	
	
	/**
	 * @param className: nombre de una clase Java
	 * @param diagramAdjustments: ajustes manuales para aplicar al diagrama de clases que se
	 * esta pintando. Contiene los m�todos que el usuario quiere a�adir a la clase
	 * recibida como par�metro "className".
	 */
	private void printMethodsFromDiagramAdjustments(String className, DiagramAdjustments diagramAdjustments){
		ArrayList<String> methodsToAdd = diagramAdjustments.getClassMethodsToAdd(className);	
		for (String method : methodsToAdd){
			pw.print("{method} ");
			pw.print(method.substring(0, method.indexOf("_")));
			pw.println(method.substring(method.indexOf("_") + 1, method.length()));
		}
	}
	
	/**
	 * @param className: nombre de una clase Java
	 * @param diagramAdjustments: ajustes manuales para aplicar al diagrama de clases que se
	 * esta pintando. Contiene los atributos que el usuario quiere a�adir a la clase
	 * recibida como par�metro "className".
	 */
	private void printFieldsFromDiagramAdjustments(String className, DiagramAdjustments diagramAdjustments){
		ArrayList<String> fieldsToAdd = diagramAdjustments.getClassFieldsToAdd(className);
		for (String field : fieldsToAdd){
			pw.print("{field} ");
			pw.print(field.substring(0, field.indexOf("_")));
			pw.println(field.substring(field.indexOf("_") + 1, field.length()));
		}
	}
	
	
	
	/**
	 * @param clss: objeto de tipo Class<?> que representa una de las clases de una aplicaci�n Java.
	 * @param printingType: formato con el que se pinta la clase
	 * 
	 * Genera el c�digo PlantUML correspondiente a la cabecera de la clase Java que recibimos en el 
	 * par�metro "clss" diferenciando entre enumerados, interfaces, clases abstractas y clases usuales.
	 * 
	 */
	private void printClassHeader(Class<?> clss, PrintingType printingType){
		if (clss.isInterface()){
			pw.print("interface " + clss.getName());
		} else if (clss.isEnum()){
			pw.print("enum " + clss.getName());
		} else if (Modifier.isAbstract(clss.getModifiers())){
			pw.print("abstract class " + clss.getName());
		} else{
			pw.print("class " + clss.getName());
		}
		
		if (printingType.equals(PrintingType.CONSUMEDCLASS)){
			pw.print(" #red");
		}else if(printingType.equals(PrintingType.CONSUMERCLASS) || printingType.equals(PrintingType.CONTROLLERCLASS)){
			pw.print(" #green");
		}
		
		pw.println(" {");		
	}
	
	/**
	 * Escribe el c�digo PlantUML correspondiente al final de la definici�n de una clase Java.
	 */
	private void printClassFooter (){
		pw.println("}");
		pw.println();
	}
	
	/**
	 * @param packageClss: objeto de tipo Clss que representa una clase de una aplicaci�n Java.
	 * @param applicationStructure: estructura de una aplicaci�n Java con la 
	 * informacion de todas las clases que la componen.
	 * @param diagramClasses: conjunto de clases del diagrama de clases que se est� pintando.
	 * @param diagramAdjustments: ajustes manuales para aplicar al diagrama de clases que se
	 * esta pintando. Contiene los atributos que el usuario quiere eliminar de la clase
	 * recibida como par�metro "packageClss".
	 * 
	 * Genera el c�digo PlantUML correspondiente a los atributos de una clase.
	 * 
	 */
	private void printClassFields (Clss packageClss, ApplicationStructure applicationStructure, Map <String, Clss> diagramClasses, DiagramAdjustments diagramAdjustments){
		Class<?> clss = packageClss.getClazz();
		Field[] fields = clss.getDeclaredFields();
		for (Field field : fields){
			if (PrimitiveTypesAndString.isPrimitiveOrString(field.getType())
					|| this.printableField(packageClss, field, applicationStructure, diagramClasses)){
				if (!field.getName().equals("$assertionsDisabled")
						&& (diagramAdjustments == null || !diagramAdjustments.isDeleteAttribute(clss.getName(), field.getName()))){
					this.printField(packageClss, field);
				}
			}
		}
		pw.println();
	}

	
	/**
	 * @param clss: objeto de tipo Clss que representa una clase de una aplicaci�n Java.
	 * @param field: atributo de una clase de la aplicaci�n
	 * @param applicationStructure: estructura de una aplicaci�n Java con la 
	 * informacion de todas las clases que la componen.
	 * @param diagramClasses: conjunto de clases de un diagrama UML. Es informado como null
	 * cuando se esta pintando un diagrama de clases de un paquete.
	 * 
	 * @return True si alguna de las clases del conjunto que compone el atributo recibido
	 * como par�metro "field", no pertenecen al conjunto recibido en el par�metro "diagramClasses";
	 * false en caso contrario.
	 */
	private boolean printableField(Clss clss, Field field, ApplicationStructure applicationStructure, Map <String, Clss> diagramClasses){
		boolean printable = false;
		ArrayList<Class<?>> fieldClasses = clss.getFieldClasses(field);
		if (!fieldClasses.isEmpty()){
			for (Class<?> fieldClass : fieldClasses){
				if (!diagramClasses.containsKey(fieldClass.getName())){
					printable = true;
				}
			}
		} else {
			printable = true;
		}
		return printable;
	}
	
/*	private boolean printableField(Clss clss, Field field, ApplicationStructure applicationStructure, Map <String, Clss> diagramClasses){
		boolean printable = false;
		String classPackage = clss.getPackageName();
		ArrayList<Class<?>> fieldClasses = clss.getFieldClasses(field);
		if (!fieldClasses.isEmpty()){
			for (Class<?> fieldClass : fieldClasses){
				if (diagramClasses != null && !diagramClasses.containsKey(fieldClass.getName())){
					printable = true;
				}else if (!applicationStructure.isPartOfApplication(fieldClass.getName()) || !applicationStructure.getPackage(fieldClass.getName()).equals(classPackage)){
					printable = true;
				}
			}
		} else {
			printable = true;
		}
		return printable;
	}*/
	
	/**
	 * @param field: atributo de una clase Java
	 * 
	 * Genera el c�digo PlantUML correspondiente al atributo recibido en
	 * en el par�metro "field".
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
	 * @param field: atributo de una clase de una aplicaci�n
	 * 
	 * Genera el c�digo PlantUML correspondiente a la visibilidad del
	 * atributo pasado en el par�metro "field".
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
	 * @param clss: objeto de tipo Class<?> que representa una de las clases de una aplicaci�n Java.
	 * 
	 * Genera el c�digo PlantUML correspondiente a los valores definidos para un enumerado
	 * 
	 */
	private void printEnumElements (Class<?> clss){
		Object[] objects = clss.getEnumConstants();
		for(Object obj : objects){
			pw.println(obj);
		}
	}
	
	/**
	 * @param clss: objeto de tipo Class<?> que representa una de las clases de una aplicaci�n Java.
	 * @param diagramClasses: conjunto de clases de un diagrama de clases.
	 * @param diagramAdjustments: ajustes manuales para aplicar al diagrama de clases que se
	 * esta pintando. Contiene los m�todos que el usuario quiere eliminar de la clase
	 * recibida como par�metro "clss".
	 * 
	 * Genera el c�digo PlantUML correspondiente a los m�todos declarados en la
	 * clase pasada como par�metro "clss".
	 * 
	 */
	private void printClassMethods(Clss clss, Map <String, Clss> diagramClasses, DiagramAdjustments diagramAdjustments){
		Method[] methods = clss.getClazz().getDeclaredMethods();
		for (Method method : methods){
			if (!clss.isMethodOverridden(method, diagramClasses)
					&& ( diagramAdjustments == null || !diagramAdjustments.isDeleteMethod(clss.getClazz().getName(), method, clss))){
				pw.print("{method} ");
				this.printMethodVisibility(method);
				pw.print(method.getName() + "(");
				this.printMethodParameters(clss, method);
				pw.print("): ");
				this.printMethodReturnType(clss, method);
			}
		}
	}
	
	/**
	 * @param clss: objeto de tipo Class<?> que representa una de las clases de una aplicaci�n Java.
	 * @param method: m�todo de una clase Java
	 * 
	 * Genera el c�digo PlantUML correspondiente al objeto que devuelve el m�todo recibido en el 
	 * par�metro "method".
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
	 * @param clss: objeto de tipo Class<?> que representa una de las clases de una aplicaci�n Java.
	 * @param method: m�todo de una clase de una aplicaci�n Java.
	 * 
	 * Genera el c�digo PlantUML correspondiente a un m�todo de una clase Java.
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
	 * @param clss: objeto de tipo Class<?> que representa una de las clases de una aplicaci�n Java.
	 * @param parameter: parametro de uno de los m�todos de la clase recibida en el par�metro "parameter".
	 * 
	 * Genera el c�digo PlantUML correspondiente al tipo de un par�metro de un m�todo de una clase.
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
	
/*	*//**
	 * @param clss: objeto de tipo Clss que representa una de las clases de una aplicaci�n Java.
	 * @param applicationStructure: estructura de una aplicaci�n Java con la informacion de todas las clases que la componen.
	 * 
	 * Valida si las relaciones entre la clase recibida en el par�metro "clss" y otras clases, 
	 * se han de pintar en funci�n de:
	 * 
	 *  - La pertenencia de estas �ltimas a la aplicaci�n y al paquete que estamos tratando.
	 *  - Si la clase "clss" hereda de otra que tiene definido el mismo tipo de relaci�n
	 *    con la misma clase destino.
	 * 
	 *//*
	private void validateAndPrintClassRelationships(Clss clss, ApplicationStructure applicationStructure){
		for (Map.Entry<String, Relationship> relationship : clss.getRelationships().entrySet()){
			if (applicationStructure.isPartOfApplication(relationship.getKey()) 
					&& applicationStructure.getPackage(relationship.getKey()).equals(clss.getPackageName())
					&& !isInheritanceRelationship(clss, relationship, applicationStructure)){
				this.printClassRelationships(clss, relationship, applicationStructure);
			}
		}
	}*/
	
	
	/**
	 * @param clss: objeto de tipo Clss que representa una de las clases de una aplicaci�n Java.
	 * @param diagramClasses: conjunto de clases a mostrar en un diagrama de clases UML
	 * @param applicationStructure: estructura de una aplicaci�n Java con la informacion de todas las clases que la componen.
	 * @param diagramAdjustments: ajustes manuales para aplicar al diagrama de clases que se
	 * esta pintando. Contiene las relaciones que el usuario quiere a�adir o eliminar de la clase
	 * recibida como par�metro.
	 * 
	 * Valida si las relaciones entre la clase recibida en el par�metro "clss" y otras clases, 
	 * se han de pintar en funci�n de:
	 * 
	 *  - La pertenencia de estas �ltimas al conjunto de clases recibido en el par�metro 
	 *    "controllerDiagramClasses".
	 *  - Si la clase "clss" hereda de otra que tiene definido el mismo tipo de relaci�n
	 *    con la misma clase destino.
	 * 
	 */
	private void validateAndPrintClassRelationships (Clss clss, Map <String, Clss> diagramClasses, ApplicationStructure applicationStructure, DiagramAdjustments diagramAdjustments){
		for (Map.Entry<String, Relationship> relationship : clss.getRelationships().entrySet()){
			if (diagramClasses.containsKey(relationship.getKey())
					&& !isInheritanceRelationship(clss, relationship, applicationStructure)
					&& (diagramAdjustments == null || !diagramAdjustments.isDeleteRelationship(clss, relationship))
					&& (diagramAdjustments == null || !diagramAdjustments.isDeleteClass(relationship.getKey()))
					&& (diagramAdjustments == null || !diagramAdjustments.isDeleteClass(clss.getClazz().getName()))){
				this.printClassRelationships(clss.getClazz().getName(), relationship, applicationStructure);
			}
		}
		if (diagramAdjustments != null){
			this.printClassRelationshipsFromDiagramAdjustments(clss.getClazz().getName(), diagramAdjustments);		
		}
	}
	
	
	/**
	 * @param clss: objeto de tipo Clss que representa una de las clases de una aplicaci�n Java.
	 * @param diagramAdjustments: conjunto de cambios manuales que aplican a un diagrama de clases.
	 * @param applicationStructure: estructura de una aplicaci�n Java con la informacion de todas las clases que la componen.
	 */
	private void printClassRelationshipsFromDiagramAdjustments(String className, DiagramAdjustments diagramAdjustments){
		for (Map.Entry<String, Relationship> relationship : diagramAdjustments.getClassRelationshipsToAdd(className).entrySet()){
			this.printRelationship(className, relationship.getKey(), relationship.getValue());
		}
	}
	

	/**
	 * @param clss: objeto de tipo Clss que representa una de las clases de una aplicaci�n Java.
	 * @param relationship: tipo de relacion entre dos clases Java.
	 * @return true si existe una relaci�n heredada por la clase recibida en el par�metro "clss"
	 * con el mismo tipo de relaci�n recibido en el par�metro "relationship". Se devolver� false 
	 * en caso contrario.
	 */
	private boolean isInheritanceRelationship(Clss clss, Entry<String, Relationship> relationship, ApplicationStructure applicationStructure) {
		Class<?> currentClass = clss.getClazz().getSuperclass();
		boolean inherited = false;
		while (currentClass != Object.class && currentClass != null && !inherited) {

			if (applicationStructure.isPartOfApplication(currentClass.getName()) && applicationStructure
					.getClss(currentClass.getName()).isRelated(relationship.getKey(), relationship.getValue())) {
				inherited = true;
				break;
			}
			currentClass = currentClass.getSuperclass();
		}
		// Comprobamos la pertenencia de la clase que define la relaci�n 
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
	 * @param clss: objeto de tipo Clss que representa una de las clases de una aplicaci�n Java.
	 * @param relationship: relacion entre dos clases Java
	 * @param applicationStructure: estructura de una aplicaci�n Java con la informacion de todas las clases que la componen.
	 * 
	 * Genera el c�digo PlantUMLcorrespondiente a la relacion recibida en el par�metro "relationship" 
	 * 
	 */
	private void printClassRelationships (String className, Entry<String, Relationship> relationship, ApplicationStructure applicationStructure){
		if (relationship.getValue().equals(Relationship.ASSOCIATION) 
				|| relationship.getValue().equals(Relationship.ASSOCIATION_MANY)){
			this.printRelationship(className, relationship.getKey(), relationship.getValue(), applicationStructure);
		}else {
			this.printRelationship(className, relationship.getKey(), relationship.getValue());
		}
	}
		
	/**
	 * @param originClass: clase origen de una relaci�n entre clases.
	 * @param destinationClass: clase destino de una relaci�n entre clases.
	 * @param relationship: tipo de relaci�n y cardinalidad entre clases.
	 * @param applicationStructure: estructura de una aplicaci�n Java con la informacion de todas las clases que la componen.
	 * 
	 * Genera el c�digo PlantUML correspondiente a una relaci�n de asociaci�n entre clases, reflejando la cardinalidad.
	 * 
	 */
	@SuppressWarnings("incomplete-switch")
	private void printRelationship(String originClass, String destinationClass, Relationship relationship,
			ApplicationStructure applicationStructure) {
		boolean isBidirectional = applicationStructure.existAssociationRelationship(destinationClass, originClass);
		if (isBidirectional) {
			Relationship destinarionClassRelationship = applicationStructure.getClss(destinationClass)
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
	 * @param originClass: clase origen de una relaci�n entre clases.
	 * @param destinationClass: clase destino de una relaci�n entre clases.
	 * @param relationship: tipo de relaci�n y cardinalidad (dependiendo del tipo de relaci�n) entre clases.
	 * 
	 * Genera el c�digo PlantUML correspondiente a una relaci�n entre clases, reflejando la cardinalidad 
	 * cuando la relacion es de tipo asociaci�n o composici�n.
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
	 * @param method: m�todo de una clase de una aplicaci�n
	 * 
	 * Genera el c�digo PlantUML correspondiente a la visibilidad del
	 * m�todo pasado en el par�metro "method".
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
	
	//Para pruebas
	public PrintWriter getPrintWriter (){
		return this.pw;
	}
	
}