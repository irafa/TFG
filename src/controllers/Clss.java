/**
 * 
 */
package controllers;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import models.PrimitiveTypesAndString;
import models.Relationship;

/**
 * @author Rafael Armesilla S�nchez
 *
 */
public class Clss {
	private Class<?> clss;
	private String packageName;
	private Map<String, Relationship> relationships;
	
	public Clss (Class<?> clss, String packageName){
		this.clss = clss;
		this.packageName = packageName;
		this.relationships = new HashMap<String, Relationship>();
	}
	
	/**
	 * @return el objeto de tipo Class que representa esta clase
	 */
	public Class<?> getClazz(){
		return this.clss;
	}
	
	//M�todo de prueba para borrar
	public void pruebaPintarRelaciones(){
		
		for (Map.Entry<String, Relationship> relationship : this.relationships.entrySet()) {
			System.out.println("Relacion de la clase " + this.clss.getSimpleName() + " con: " + relationship.getKey() + " tipo: " + relationship.getValue()); 
        }
	}
	
	
	/**
	 * Identifica todas las relaciones de esta clase Java.
	 */
	public void calculateRelationships() {

		if (!this.clss.isEnum()) {
			if (!this.clss.isInterface()) {

				Class<?> superClass = this.clss.getSuperclass();
				if (!superClass.getSimpleName().equals("Object")) {
					this.relationships.put(superClass.getName(), Relationship.INHERITANCE);
				}

				this.insertAssociationAndCompositionFieldsClassesRelationships();
				
				for (Class<?> extendedInterface : this.clss.getInterfaces()){
					this.relationships.put(extendedInterface.getName(), Relationship.IMPLEMENTATION);
				}
				
			} else{
				for (Class<?> extendedInterface : this.clss.getInterfaces()){
					this.relationships.put(extendedInterface.getName(), Relationship.INHERITANCE);
				}
			}

			Method[] methods = clss.getDeclaredMethods();
			for (int i = 0; i < methods.length; i++) {
				methods[i].setAccessible(true);

				Parameter[] parameters = methods[i].getParameters();
				for (int j = 0; j < parameters.length && !methods[i].getName().equals("main")
						&& !this.isSetter(methods[i]); j++) {
					ArrayList<Class<?>> parameterClasses = this.getParameterClasses(parameters[j]);
					this.insertRelationShip(parameterClasses,  Relationship.USAGE);
				}
				methods[i].setAccessible(false);
			}
		} else {
			for (Class<?> extendedInterface : this.clss.getInterfaces()){
				this.relationships.put(extendedInterface.getName(), Relationship.IMPLEMENTATION);
			}
		}
	}
	
	/**
	 *  
	 * Define relaciones de asociaci�n con aquellas clases que conforman atributos 
	 * inicializados a trav�s de los constructores definidos para esta clase, o por m�todos setter.
	 * 
	 * Para el resto de clases que definen atributos no incializalizados de la forma
	 * anterioemente descrita, se establecer�n relaciones de composici�n.
	 * 
	 */
	private void insertAssociationAndCompositionFieldsClassesRelationships() {
		// Recorremos todos los atributos de la clase que estamos analizando.
		for (Field field : this.clss.getDeclaredFields()) {
			field.setAccessible(true);
			ArrayList<Class<?>> fieldClasses = this.getFieldClasses(field);
			if (this.isValidType(field) && this.existAsocciationRelationship(field)) {
				// Insertamos relaci�n de asociaci�n
				if ((field.getType().isArray() 
						&& !PrimitiveTypesAndString.isPrimitiveOrString(field.getType().getComponentType())) 
						|| field.getGenericType().getClass().getSimpleName().equals("ParameterizedTypeImpl")){
					this.insertRelationShip(fieldClasses, Relationship.ASSOCIATION_MANY);
				}else{
					this.insertRelationShip(fieldClasses, Relationship.ASSOCIATION);
				}
			} else if (this.isValidType(field) && !this.existAsocciationRelationship(field)) {
				// Insertamos relaci�n de composici�n
				if ((field.getType().isArray()
						&& !PrimitiveTypesAndString.isPrimitiveOrString(field.getType().getComponentType())) 
						|| field.getGenericType().getClass().getSimpleName().equals("ParameterizedTypeImpl")){
					this.insertRelationShip(fieldClasses, Relationship.COMPOSITION_MANY);
				}else{
					this.insertRelationShip(fieldClasses, Relationship.COMPOSITION);
				}
			}
			field.setAccessible(false);
		}
	}

	/**
	 * @param relatedClasses: clases con las que se quiere establecer una relaci�n.
	 * @param relationship: tipo de relation entre esta clase y las clases que conforman el atributo
	 * "field" recibido como par�metro.
	 */
	private void insertRelationShip(ArrayList<Class<?>> relatedClasses, Relationship relationship) {

		if (relationship.equals(Relationship.ASSOCIATION) || relationship.equals(Relationship.ASSOCIATION_MANY)) {
			for (Class<?> clss : relatedClasses) {
				if (clss.isArray() && !this.isRelated(clss.getComponentType().getName())) {
					this.relationships.put(clss.getComponentType().getName(), relationship);
				} else if (!this.isRelated(clss.getName())) {
					this.relationships.put(clss.getName(), relationship);
				}
			}
		} else if (relationship.equals(Relationship.COMPOSITION) || relationship.equals(Relationship.COMPOSITION_MANY)) {
			for (Class<?> clss : relatedClasses) {
				if (clss.isArray()) {
					this.relationships.put(clss.getComponentType().getName(), relationship);
				} else {
					this.relationships.put(clss.getName(), relationship);
				}
			}
		} else if (relationship.equals(Relationship.USAGE)) {
			for (Class<?> clss : relatedClasses) {
				if (clss.isArray() 
						&& !this.isRelated(clss.getComponentType().getName())
						&& !this.clss.getName().equals(clss.getComponentType().getName())) {
					this.relationships.put(clss.getComponentType().getName(), relationship);
				} else if (!this.isRelated(clss.getName())
						&& !this.clss.getName().equals(clss.getName())) {
					this.relationships.put(clss.getName(), relationship);
				}
			}
		}
	}
	

	/**
	 * @param field: atributo cuyas clases son examinadas para determinar si se pueden establecer 
	 * relaciones de asociaci�n o de composici�n con ellas.
	 * 
	 * Filtramos:
	 * - Tipos primitivos.
	 * - String.
	 * - Arrays de tipos primitivos o de Strings.
	 * - Atributos parametrizados que se componen unicamente de tipos primitivos o String. 
	 *   Si alguno de los par�metros del tipo gen�rico no es primitivo o String, el tipo ser� considerado como v�lido y
	 *   comprobaremos el tipo de relaci�n existente entre la clase que estamos analizando y la clase del par�metro 
	 *   que forma parte de este atributo.
	 *   
	 * @return true si el atributo esta definido con alguna clase con la que se puede establecer una relaci�n
	 * o false en caso contrario.
	 */
	private boolean isValidType(Field field) {
		boolean valid = false;
		if (field.getType().isArray() // comentar en la memoria que los arrays no pueden contener tipos gen�ricos
				&& !PrimitiveTypesAndString.isPrimitiveOrString(field.getType().getComponentType())) {
			valid = true;

		} else if (field.getGenericType().getClass().getSimpleName().equals("ParameterizedTypeImpl")) {
			valid = this.isValidParametrizedType(field.getGenericType());

		} else if (!PrimitiveTypesAndString.isPrimitiveOrString(field.getType())) {
			valid = true;
		}
		return valid;
	}
	
	/**
	 * @param type: tipo gen�rico
	 * 	 Si alguno de los par�metros del tipo gen�rico no es primitivo o String, el tipo ser� considerado como v�lido.
	 * 
	 * @return true si el tipo esta definido con alguna clase que no se corresponde con un tipo primitivo o String
	 */
	private boolean isValidParametrizedType(Type pType) {
		boolean valid = false;
		ParameterizedType parametrizedType = (ParameterizedType) pType;
		
		Type[] types = parametrizedType.getActualTypeArguments();
		for (int i=0; i < types.length && !valid; i++) {
			if (types[i].getClass().getSimpleName().equals("ParameterizedTypeImpl")) {
				valid = this.isValidParametrizedType(types[i]);
			} else {
				Class<?> classParametrizedType = (Class<?>) types[i];
				if (classParametrizedType.isArray() 
						&& !PrimitiveTypesAndString.isPrimitiveOrString(classParametrizedType.getComponentType())) {
					valid = true;
				
				} else if (!PrimitiveTypesAndString.isPrimitiveOrString(classParametrizedType)) {
					valid = true;
				}
			}
		}
		return valid;
	}
	
	
	/**
	 * @param field: atributo de la clase
	 * @return true si existe relaci�n de asociaci�n entre esta clase 
	 * y las clases que conforman este atributo.
	 */
	private boolean existAsocciationRelationship(Field field){
		boolean existAsocciationRelationship = false;
		ArrayList<Class<?>> fieldClasses = this.getFieldClasses(field);
		Map<String, ArrayList<Class<?>>> constructorsParameters = this.getConstructorsParameters();
		for(Map.Entry<String, ArrayList<Class<?>>> entry : constructorsParameters.entrySet()){
			if (this.equalParameterAndField(entry.getValue(), fieldClasses)){
				existAsocciationRelationship = true;
			}
		}
		Map<String, ArrayList<Class<?>>> settersParameters = this.getSettersParameters();
		for(Map.Entry<String, ArrayList<Class<?>>> entry : settersParameters.entrySet()){
			if (this.equalParameterAndField(entry.getValue(), fieldClasses)){
				existAsocciationRelationship = true;
			}
		}
		return existAsocciationRelationship;
	}
	
	/**
	 * @param parameter: Par�metro de un constructor o m�todo
	 * @param field: atributo de una clase
	 * @return true si con las clases del par�metro recibido se puede instanciar un objeto del atributo recibido
	 */
	private boolean equalParameterAndField (ArrayList<Class<?>> parameter, ArrayList<Class<?>> field){
		boolean equal = true;
		boolean equalClass;
		for (Class<?> fieldClass : field){
			equalClass = false;
			for (Class<?> parameterClass : parameter){
				if (fieldClass.isAssignableFrom(parameterClass)){
					equalClass = true;
				}
			}
			if (!equalClass){
				equal = false;
			}
		}
		return equal;
	}
	
	/**
	 * @param field: Atributo de la clase del que se quiere calcular las clases que lo componen.
	 * @return listado de clases que componen el atributo recibido como par�metro.
	 */
	public ArrayList<Class<?>> getFieldClasses (Field field){
		ArrayList<Class<?>> fieldClasses = new ArrayList<Class<?>>();
		if (field.getType().isArray() 
				&& !PrimitiveTypesAndString.isPrimitiveOrString(field.getType().getComponentType())) {
			fieldClasses.add(field.getType().getComponentType());
		} else if (field.getGenericType().getClass().getSimpleName().equals("ParameterizedTypeImpl")) {
			this.addClasses (fieldClasses, this.getParametrizedFieldClasses(field.getGenericType()));
		} else if (!PrimitiveTypesAndString.isPrimitiveOrString(field.getType())) {
			fieldClasses.add(field.getType());
		}
		return fieldClasses;
	}
	
	
	/**
	 * @param pType: tipo de un atributo del que queremos calcular las clases que lo componen.
	 * @return listado de clases que componen el tipo recibido como par�metro.
	 */
	private ArrayList<Class<?>> getParametrizedFieldClasses(Type pType){
		ArrayList<Class<?>> parametrizedFieldClasses = new ArrayList<Class<?>>();
		ParameterizedType parametrizedType = (ParameterizedType) pType;
		
		Type[] types = parametrizedType.getActualTypeArguments();
		for (int i=0; i < types.length; i++) {
			if (types[i].getClass().getSimpleName().equals("ParameterizedTypeImpl")) {
				this.addClasses (parametrizedFieldClasses, this.getParametrizedFieldClasses(types[i]));
			} else {
				Class<?> classParametrizedType = (Class<?>) types[i];
				if (classParametrizedType.isArray() 
						&& !PrimitiveTypesAndString.isPrimitiveOrString(classParametrizedType.getComponentType())
						&& !existClassInList(parametrizedFieldClasses, classParametrizedType.getComponentType())) {
					parametrizedFieldClasses.add(classParametrizedType.getComponentType());
				
				} else if (!PrimitiveTypesAndString.isPrimitiveOrString(classParametrizedType)
						&& !existClassInList(parametrizedFieldClasses, classParametrizedType)) {
					parametrizedFieldClasses.add(classParametrizedType);
				}
			}
		}
		return parametrizedFieldClasses;
	}
	
	/**
	 * @param classesList: Listado de clase a la que se quiere a�adir las clases contenidas
	 * en el listado de clases "classesToAdd" recibido como par�metro.
	 * @param classesToAdd: Listado de clases a a�adir en el listado "classesList" recibido como par�metro
	 */
	private void addClasses (ArrayList<Class<?>> classesList, ArrayList<Class<?>> classesToAdd){
		for(Class<?> clss : classesToAdd){
			if (!existClassInList(classesList, clss)){
				classesList.add(clss);
			}
		}
	}
			
	/**
	 * @return mapa con los par�metros de todos los constructores de la clase
	 */
	private Map<String, ArrayList<Class<?>>> getConstructorsParameters() {
		Map<String, ArrayList<Class<?>>> constructorsParameters = new HashMap<String, ArrayList<Class<?>>>();
		Constructor<?>[] constructors = this.clss.getDeclaredConstructors();
		for (int i = 0; i < constructors.length; i++) {
			constructors[i].setAccessible(true);
			Parameter[] parameters = constructors[i].getParameters();
			for (int j = 0; j < parameters.length; j++) {
				ArrayList<Class<?>> parameterClasses= this.getParameterClasses(parameters[j]);			
				if (!parameterClasses.isEmpty() && !existParameter(constructorsParameters, parameterClasses)) {
					constructorsParameters.put("Constructor: " + i + " - Parameter: " + j + " - " + parameters[j].getName(), parameterClasses);
				}
			}
			constructors[i].setAccessible(false);
		}
		return constructorsParameters;
	}

	
	/**
	 * @return mapa con los par�metros de todos los m�todos setter de la clase
	 */
	private Map<String, ArrayList<Class<?>>> getSettersParameters() {
		Map<String, ArrayList<Class<?>>> settersParameters = new HashMap<String, ArrayList<Class<?>>>();
		Method[] methods = this.clss.getDeclaredMethods();
		for (Method method : methods) {
			method.setAccessible(true);
			if (this.isSetter(method)) {
				ArrayList<Class<?>> parameterClasses = this.getParameterClasses(method.getParameters()[0]);
				if (!parameterClasses.isEmpty() && !existParameter(settersParameters, parameterClasses)) {
					settersParameters.put(method.getName(), parameterClasses);
				}
			}
			method.setAccessible(false);
		}
		return settersParameters;
	}
	
	/**
	 * @param method: m�todo para el que comprobamos si se trata de un m�todo setter
	 * @return true si es un m�todo setter y false en caso contrario
	 */
	private boolean isSetter(Method method) {
		   return Modifier.isPublic(method.getModifiers()) &&
		      method.getReturnType().equals(void.class) &&
		         method.getParameterTypes().length == 1 &&
		            method.getName().matches("^set[A-Z].*");
	}
	
	/**
	 * @param parameter: par�metro del que queremos identificar el conjunto de clases
	 * que lo componen.
	 * 
	 * @return listado con las clases que conforman el par�metro recibido como par�metro.
	 */
	private ArrayList<Class<?>> getParameterClasses(Parameter parameter){
		ArrayList<Class<?>> parameterClasses = new ArrayList<Class<?>>();
		if (parameter.getType().isArray() 
				&& !PrimitiveTypesAndString.isPrimitiveOrString(parameter.getType().getComponentType())) {
			parameterClasses.add(parameter.getType().getComponentType());

		} else if (parameter.getParameterizedType().getClass().getSimpleName().equals("ParameterizedTypeImpl")) {
			this.addClasses (parameterClasses, this.getParametrizedParameterClasses(parameter.getParameterizedType()));

		} else if (!PrimitiveTypesAndString.isPrimitiveOrString(parameter.getType())) {
			parameterClasses.add(parameter.getType());
		}
		return parameterClasses;
	}
	
	/**
	 * @param type: representa el tipo parametrizado de un par�metro
	 * @return listado de clases que conforman el tipo parametrizado
	 * recibido como par�metro.
	 */
	public ArrayList<Class<?>> getParametrizedParameterClasses(Type pType){
		ArrayList<Class<?>> parametrizedParameterClasses = new ArrayList<Class<?>>();
		ParameterizedType parametrizedType = (ParameterizedType) pType;
		
		Type[] types = parametrizedType.getActualTypeArguments();
		for (int i=0; i < types.length; i++) {
			if (types[i].getClass().getSimpleName().equals("ParameterizedTypeImpl")) {
				this.addClasses (parametrizedParameterClasses, this.getParametrizedParameterClasses(types[i]));
			} else {
				Class<?> classParametrizedType = (Class<?>) types[i];
				if (classParametrizedType.isArray() 
						&& !PrimitiveTypesAndString.isPrimitiveOrString(classParametrizedType.getComponentType())
						&& !existClassInList(parametrizedParameterClasses, classParametrizedType.getComponentType())) {
					parametrizedParameterClasses.add(classParametrizedType.getComponentType());
				
				} else if (!PrimitiveTypesAndString.isPrimitiveOrString(classParametrizedType)
						&& !existClassInList(parametrizedParameterClasses, classParametrizedType)) {
					parametrizedParameterClasses.add(classParametrizedType);
				}
			}
		}
		return parametrizedParameterClasses;
	}
	
	
	/**
	 * @param parametersMap: Mapa de par�metros y clases que los conforman
	 * @param parameterClasses: clases que conforman un par�metro
	 * @return true si existe en el mapa recibido como par�mtro "parametersMap"
	 * un conjunto de clases para un par�metro igual al recibido en el 
	 * par�metro "parameterClasses".
	 */
	private boolean existParameter (Map<String, ArrayList<Class<?>>> parametersMap, ArrayList<Class<?>> parameterClasses){
		boolean existParameter = false;
		for(Map.Entry<String, ArrayList<Class<?>>> entry : parametersMap.entrySet()){
			if (this.equalParameterClasses(entry.getValue(), parameterClasses)){
				existParameter = true;
			}
		}
		return existParameter;
	}
	
	/**
	 * @param parameterAClasses: Conjunto de clases A
	 * @param parameterBClasses: COnjunto de clases B
	 * @return true si ambos conjuntos de clases son iguales y false en 
	 * caso contrario
	 */
	private boolean equalParameterClasses (ArrayList<Class<?>> parameterAClasses, ArrayList<Class<?>> parameterBClasses){
		boolean equals = true;
		if (!(parameterAClasses.size() == parameterBClasses.size())){
			equals = false;
		}else{
			for(Class<?> clss : parameterAClasses){
				if (!existClassInList(parameterBClasses, clss)){
					equals = false;
				}
			}
		}
		return equals;
	}
	
	/**
	 * @param classesList: Lista de clases
	 * @param clss: clase
	 * @return true si la clase recibida en el par�metro "clss" esta contenida en el listado
	 * "classesList"
	 */
	private boolean existClassInList(ArrayList<Class<?>> classesList, Class<?> clss){
		boolean exist = false;
		for (Class<?> listClss : classesList){
			if (listClss.getSimpleName().equals(clss.getSimpleName())){
				exist = true;
			}
		}
		return exist;
	}
	
	/**
	 * @param className: nombre de una clase de la aplicaci�n
	 * @return true si la clase recibida en el par�metro "className"
	 * esta relacionada con esta clase; false en caso contrario.
	 */
	public boolean isRelated (String className){
		return this.relationships.containsKey(className);
	}
	
	/**
	 * @param className: nombre de una clase de la aplicaci�n
	 * @param relationship: tipo de relaci�n entre clases Java.
	 * @return true si la clase recibida en el par�metro "className"
	 * esta relacionada con esta clase y con el mismo tipo de relaci�n 
	 * que el recibido en el par�metro "relationship"; false en caso contrario.
	 */
	public boolean isRelated (String className, Relationship relationship){
		return (this.relationships.containsKey(className) && this.relationships.get(className).equals(relationship));
	}
	
	/**
	 * @return la estructura de relaciones de la clase 
	 */
	public Map<String, Relationship> getRelationships(){
		return this.relationships;
	}
	
	/**
	 * @return el nombre del paquete al que pertenece esta clase Java
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * @param method: un m�todo de la clase
	 * @param diagramClasses: conjunto de clases de un diagrama de clases
	 * 
	 * @return true si el m�todo se est� sobreescribiendo y la clase abstracta que los declara 
	 * pertenece al mismo paquete que esta clase, false en caso contrario.
	 */
	public boolean isMethodOverridden(Method method, Map <String, Clss> diagramClasses) {
		Class<?> currentClass = this.getClazz().getSuperclass();

		boolean overwritten = false;
		while (currentClass != Object.class 
				&& currentClass != null
				&& !overwritten) {
			
			try {
				Method m = currentClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
				overwritten = true;
				break;
			} catch (NoSuchMethodException e) {
				// El m�todo no existe
				
			} catch (SecurityException e) {
				// Security exception
			}
			currentClass = currentClass.getSuperclass();
		}
		// Comprobamos la pertenencia del metodo a una clase del 
		// conjunto de clases del diagrama que estamos generando:
		if (overwritten 
				&& !diagramClasses.containsKey(currentClass.getName())){
			overwritten = false;
		}
		return overwritten;
	}
	
	
/*	public boolean isMethodOverridden(Method method, Map <String, Clss> diagramClasses) {
		Class<?> currentClass = this.getClazz().getSuperclass();

		boolean overwritten = false;
		while (currentClass != Object.class 
				&& currentClass != null
				&& !overwritten) {
			
			try {
				Method m = currentClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
				overwritten = true;
				break;
			} catch (NoSuchMethodException e) {
				// El m�todo no existe
				
			} catch (SecurityException e) {
				// Security exception
			}
			currentClass = currentClass.getSuperclass();
		}
		// Comprobamos la pertenencia del metodo a una clase del paquete
		// que estamos tratando o al conjunto de clases del diagrama de 
		// clases del controlador que estamos tratando:
		if (overwritten 
				&& diagramClasses != null
				&& !diagramClasses.containsKey(currentClass.getName())){
			overwritten = false;
		}else if (overwritten 
				&& diagramClasses == null
				&& !this.getClazz().getPackage().getName().equals(currentClass.getPackage().getName())){
			overwritten = false;
		}
		return overwritten;
	}*/
	
}

// C�digo para identificar las relaciones de una clase mediante la instanciaci�n
// de objetos de esta clase y la revisi�n de la inicializaci�n de sus atributos (Descartado por su complejidad)

/*public void calculateRelationships (){
	
	// 1 - Comprobamos si esta clase hereda de otra:
	Class<?> superClass = this.clss.getSuperclass();
	if (!superClass.getSimpleName().equals("Object")){
		this.relationships.put(superClass.getSimpleName(), Relationship.INHERITANCE);
	}

	// 2 - Calculamos las relaciones de composici�n y de asociaci�n
	
	// Obtenemos todos los constructores de la clase que vamos a
	// analizar, filtrando previamente las clases Abstractas:
	if (!Modifier.isAbstract(this.clss.getModifiers()) && !this.clss.isEnum()) {

		Constructor<?>[] constructors = this.clss.getDeclaredConstructors();

		try {
			for (int i = 0; i < constructors.length; i++) {
				// Para cada uno de los constructores, instanciamos un
				// objeto
				Object classObject = this.getInstance(constructors[i]);
				Parameter[] parameters = constructors[i].getParameters();

				// Comprobamos si sus atributos est�n o no inicializados en
				// el objeto instanciado.
				for (Field field : clss.getDeclaredFields()) {
					field.setAccessible(true);
					Object value = field.get(classObject);

					// Filtramos los tipos primitivos, String y los
					// enumerados
					// Si el valor del atributo es distinto de "null" en el
					// objeto instanciado, es debido a que se ha instanciado
					// en el
					// constructor.
					// Lo que supone una relaci�n de composici�n o
					// Asociaci�n entre la clase que estamos analizando y la
					// correspondiente
					// a este atributo

					if (!PrimitiveTypes.isPrimitive(field.getType()) && value != null) {

						// Si el atributo es de tipo parametrizado (TreeMap,
						// HashMap, LinkedHashMap o ArrayList):
						if (field.getGenericType().getClass().getSimpleName().equals("ParameterizedTypeImpl")) {
							ParameterizedType parametrizedType = (ParameterizedType) field.getGenericType();
							for (Type types : parametrizedType.getActualTypeArguments()) {
								Class<?> classParametrizedType = (Class<?>) types;

								// Filtrar tipos primitivos, Strings y
								// enumerados:
								if (!PrimitiveTypes.isPrimitive(classParametrizedType)
										&& !classParametrizedType.isEnum()) {
									this.insertRelationship(classParametrizedType, parameters, field.getType(),
											true, false);
								}
							}
						} else {

							if (field.getType().isArray()) {// Si el
															// atributo es
															// de tipo Array
								if (!field.getType().getComponentType().isEnum()) {
									this.insertRelationship(field.getType().getComponentType(), parameters,
											field.getType(), false, true);
								}
							} else {// Si el atributo es una Clase definida
									// por el usuario.
								this.insertRelationship(field.getType(), parameters, field.getType(), false, false);
							}
						}
					}
					field.setAccessible(false);
				}
			}
			// Identificamos y definimos las relaciones de asociaci�n entre
			// la
			// clase que estamos analizando
			// y las clases correspondientes a los atributos que no son
			// inicializados con los constructores
			this.insertUninitializedFieldsRelationships();
		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}
	
	// 3 - Calculamos las relaciones de uso:

	Method[] methods = clss.getDeclaredMethods();
	for (int i = 0; i < methods.length; i++) {
		methods[i].setAccessible(true);
		
		Parameter[] parameters = methods[i].getParameters();
		for (int j = 0; j < parameters.length && !methods[i].getName().equals("main"); j++) {

			if (parameters[j].getParameterizedType().getClass().getSimpleName().equals("ParameterizedTypeImpl")) {
				// Si se trata de un tipo parametrizado
				ParameterizedType parametrizedType = (ParameterizedType) parameters[j].getParameterizedType();
				for (Type type : parametrizedType.getActualTypeArguments()) {
					Class<?> typeArgument = (Class<?>) type;

						// Filtrar tipos primitivos, Strings y
					// enumerados. Comprobamos que no hay una relaci�n
					// existente.
					if (!PrimitiveTypes.isPrimitive(typeArgument) && !typeArgument.isEnum()
							&& !this.isRelated(typeArgument.getSimpleName())) {

						// Establecemos la relaci�n de uso
						this.relationships.put(typeArgument.getSimpleName(), Relationship.USAGE);
					}
				}
			} else if (parameters[j].getType().isArray() // Si se trata de un
														// array
					&& !PrimitiveTypes.isPrimitive(parameters[j].getType().getComponentType())
					&& !parameters[j].getType().getComponentType().isEnum()
					&& !this.isRelated(parameters[j].getType().getComponentType().getSimpleName())) {
				this.relationships.put(parameters[j].getType().getComponentType().getSimpleName(), Relationship.USAGE);

			} else if (!PrimitiveTypes.isPrimitive(parameters[j].getType())
					&& !parameters[j].getType().isEnum() 
					&& !this.isRelated(parameters[j].getType().getSimpleName())) {
				// Si el atributo es una Clase definida por el usuario.

				this.relationships.put(parameters[j].getType().getSimpleName(), Relationship.USAGE);
			}
		}
		methods[i].setAccessible(false);
	}
}

public Object getInstance(Constructor<?> constructor) {

	Object instance = null;
	constructor.setAccessible(true);

	try {
		final List<Object> params = new ArrayList<Object>();
		for (Class<?> pType : constructor.getParameterTypes()) {
			if (pType.isPrimitive() || pType.getSimpleName().equals("String")) {
				params.add(Defaults.defaultValue(pType));
			} else if (pType.getSimpleName().equals("TreeMap") || pType.getSimpleName().equals("HashMap")
					|| pType.getSimpleName().equals("LinkedHashMap") || pType.getSimpleName().equals("ArrayList")) {
				params.add(pType.newInstance());
			} else if (pType.isArray() && !pType.getComponentType().isPrimitive()
					&& !pType.getComponentType().getSimpleName().equals("String")) {
				params.add(Array.newInstance(pType.getComponentType(), 1));
			} else if (pType.isEnum()) { // Solo necesitamos crear la
											// instancia de la clase, aunque sea con el campo correspondiente a este Enum a "null". Cuando se revisan
											// los campos de cada clase, se
											// detectan las relaciones con
											// enumerados
				params.add(null);
			} else {
				Constructor<?>[] parameterConstructor = pType.getDeclaredConstructors();
				System.out.println("Instanciando pType:" + pType.isInterface());
				// Nos vale una instancia cualquiera de la clase para este
				// par�metro
				params.add(this.getInstance(parameterConstructor[0]));
			}

		}
		System.out.println("Instanciando :" + constructor.getName());
		instance = constructor.newInstance(params.toArray());
		constructor.setAccessible(false);
		return instance;

	} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
			| InvocationTargetException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return instance;
	}
	
}

*/

/**
 *  
 * Define relaciones de Asociaci�n con aquellas clases que conforman atributos que no 
 * son inicializados mediante los constructores definidos para esta clase.
 * 
 * Se trata de la �ltima parte necesaria para establecer todas
 * las relaciones de Asociaci�n existentes con la clase que estamos analizando.
 * 
 *//*
public void insertUninitializedFieldsRelationships() {

	// Recorremos todos los atributos de la clase que estamos analizando.
	// Si no encontramos una relaci�n establecida entre esta clase y la/s que conforman 
	// cada atributo, se inserta una relaci�n de Asociaci�n entre dichas clases
	for (Field field : this.clss.getDeclaredFields()) {
		field.setAccessible(true);

		if (field.getType().isArray()){
			// Filtramos los tipos primitivos, string y los enumerados. Comprobamos si existe relaci�n establecida entre la clase
			// que estamos analizando y el tipo del array.
			if (!PrimitiveTypes.isPrimitive(field.getType().getComponentType()) && !field.getType().getComponentType().isEnum() && !this.isRelated(field.getType().getComponentType().getSimpleName())){
				this.relationships.put(field.getType().getComponentType().getSimpleName(),
						Relationship.ASSOCIATION);
			}
		
		} else if (field.getGenericType().getClass().getSimpleName().equals("ParameterizedTypeImpl")) {
			ParameterizedType parametrizedType = (ParameterizedType) field.getGenericType();
			for (Type types : parametrizedType.getActualTypeArguments()) {
				Class<?> classParametrizedType = (Class<?>) types;

				// Filtrar tipos primitivos, Strings y enumerados. Comprobamos si existe relaci�n establecida entre la clase
				// que estamos analizando y el tipo del par�metro.
				if (!PrimitiveTypes.isPrimitive(classParametrizedType) && !classParametrizedType.isEnum() && !this.isRelated(classParametrizedType.getSimpleName())) {
					this.relationships.put(classParametrizedType.getSimpleName(), Relationship.ASSOCIATION);
				}
			}
			
		} else if (!this.isRelated(field.getType().getSimpleName()) && !PrimitiveTypes.isPrimitive(field.getType()) && !field.getType().isEnum()) {
			// Filtrar tipos primitivos, Strings y enumerados. Comprobamos si existe relaci�n establecida entre la clase
			// que estamos analizando y el tipo del atributo.
			this.relationships.put(field.getType().getSimpleName(), Relationship.ASSOCIATION);
		}
	}
}

public void insertRelationship(Class<?> classField, Parameter[] parameters,
		Class<?> parameterizedTypeClass, Boolean parameterizedType, Boolean arrayType) {

	Boolean associationRelationship = false;

	for (int i = 0; i < parameters.length && !associationRelationship; i++) {

		// Comprobamos si el parametro es de tipo parametrizado y si es una
		// clase o subclase compatible con la del atributo
		if (parameterizedType
				&& parameters[i].getType().getSimpleName().equals(parameterizedTypeClass.getSimpleName())) {
			ParameterizedType parametrizedType = (ParameterizedType) parameters[i].getParameterizedType();
			for (Type types : parametrizedType.getActualTypeArguments()) {
				Class<?> classParametrizedType = (Class<?>) types;
				if (classField.isAssignableFrom(classParametrizedType)) {
					associationRelationship = true;
				}
			}

			// Comprobamos si el parametro es de tipo array y si es una
			// clase o subclase compatible con la del atributo
		} else if (arrayType && parameters[i].getType().isArray()
				&& classField.isAssignableFrom(parameters[i].getType().getComponentType())) {
			associationRelationship = true;

		} else if (classField.isAssignableFrom(parameters[i].getType())) {
			// Si no es de tipo parametrizado o array, es decir, se trata de una Clase definida por el usuario, 
			// tambi�n comprobamos si el tipo se corresponde con una clase o subclase compatible con la del atributo
			associationRelationship = true;
		}
	}

	if (associationRelationship) {
		if (classField.isArray() && !this.isRelated(classField.getComponentType().getSimpleName())) {
			this.relationships.put(classField.getComponentType().getSimpleName(), Relationship.ASSOCIATION);
		} else if (!this.isRelated(classField.getSimpleName())) {
			this.relationships.put(classField.getSimpleName(), Relationship.ASSOCIATION);
		}
	} else {
		this.relationships.put(classField.getSimpleName(), Relationship.COMPOSITION);
	}
}*/
