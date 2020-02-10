/**
 * 
 */
package models;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import controllers.Clss;

/**
 * @author Rafael Armesilla Sánchez
 *
 */
public class DiagramAdjustments {
	private ArrayList<String> newClasses;
	private ArrayList<String> deleteClasses;
	private ArrayList<String> newAttributes;
	private ArrayList<String> deleteAttributes;
	private ArrayList<String> newMethods;
	private ArrayList<String> deleteMethods;
	private ArrayList<String> newCompositionRelationships;
	private ArrayList<String> deleteCompositionRelationships;
	private ArrayList<String> newAssociationRelationships;
	private ArrayList<String> deleteAssociationRelationships;
	private ArrayList<String> newUseRelationships;
	private ArrayList<String> deleteUseRelationships;
	private ArrayList<String> newImplementationRelationships;
	private ArrayList<String> deleteImplementationRelationships;
	private ArrayList<String> newInheritanceRelationships;
	private ArrayList<String> deleteInheritanceRelationships;
	
	public DiagramAdjustments (){
		this.newClasses = new ArrayList<String>();
		this.deleteClasses = new ArrayList<String>();
		this.newAttributes = new ArrayList<String>();
		this.deleteAttributes = new ArrayList<String>();
		this.newMethods = new ArrayList<String>();
		this.deleteMethods = new ArrayList<String>();
		this.newCompositionRelationships = new ArrayList<String>();
		this.deleteCompositionRelationships = new ArrayList<String>();
		this.newAssociationRelationships = new ArrayList<String>();
		this.deleteAssociationRelationships = new ArrayList<String>();
		this.newUseRelationships = new ArrayList<String>();
		this.deleteUseRelationships = new ArrayList<String>();
		this.newImplementationRelationships = new ArrayList<String>();
		this.deleteImplementationRelationships = new ArrayList<String>();
		this.newInheritanceRelationships = new ArrayList<String>();
		this.deleteInheritanceRelationships = new ArrayList<String>();
	}

	/**
	 * @param classInfo: nombre de la clase que se ha de añadir al diagrama
	 * 
	 * Añade la clase recibida como parámetro al listado de clases que se han 
	 * de añadir al diagrama vinculado.
	 */
	public void addClass(String classInfo){
		this.newClasses.add(classInfo);
	}
	
	/**
	 * @param classInfo: nombre de la clase que se ha de eliminar del diagrama
	 * 
	 * Añade la clase recibida como parámetro al listado de clases que se han 
	 * de eliminar del diagrama vinculado.
	 */
	public void deleteClass(String classInfo){
		this.deleteClasses.add(classInfo);
	}
	
	/**
	 * @param attributeInfo: visibilidad del atributo, nombre de la clase que lo declara,
	 * nombre del atributo y su tipo.
	 * 
	 * Añade el atributo recibido como parámetro al listado de atributos que se han 
	 * de añadir al diagrama vinculado.
	 */
	public void addAttribute(String attributeInfo){
		this.newAttributes.add(attributeInfo);
	}

	/**
	 * @param attributeInfo: nombre de la clase que lo declara y
	 * nombre del atributo.
	 * 
	 * Añade el atributo recibido como parámetro al listado de atributos que se han 
	 * de eliminar del diagrama vinculado.
	 */
	public void deleteAttribute(String attributeInfo){
		this.deleteAttributes.add(attributeInfo);
	}

	/**
	 * @param methodInfo: visibilidad del método, nombre de la clase que lo declara
	 * y definición del método.
	 * 
	 * Añade el método recibido como parámetro al listado de métodos que se han 
	 * de añadir al diagrama vinculado.
	 */
	public void addMethod(String methodInfo){
		this.newMethods.add(methodInfo);
	}

	/**
	 * @param methodInfo: nombre de la clase que lo declara
	 * y definición del método.
	 * 
	 * Añade el método recibido como parámetro al listado de métodos que se han 
	 * de eliminar del diagrama vinculado.
	 */
	public void deleteMethod(String methodInfo){
		this.deleteMethods.add(methodInfo);
	}
	
	/**
	 * @param relationshipInfo: nombre completo de la clase origen y destino
	 * de la relación
	 * 
	 * Añade la relación recibida como parámetro al listado de relaciones que se han 
	 * de añadir al diagrama vinculado.
	 */
	public void addCompositionRelationship (String relationshipInfo){
		this.newCompositionRelationships.add(relationshipInfo);
	}
	
	/**
	 * @param relationshipInfo: nombre completo de la clase origen y destino
	 * de la relación
	 * 
	 * Añade la relación recibida como parámetro al listado de relaciones que se han 
	 * de añadir al diagrama vinculado.
	 */
	public void deleteCompositionRelationship (String relationshipInfo){
		this.deleteCompositionRelationships.add(relationshipInfo);
	}
	
	/**
	 * @param relationshipInfo: nombre completo de la clase origen y destino
	 * de la relación
	 * 
	 * Añade la relación recibida como parámetro al listado de relaciones que se han 
	 * de añadir al diagrama vinculado.
	 */
	public void addAssociationRelationship (String relationshipInfo){
		this.newAssociationRelationships.add(relationshipInfo);
	}
	
	/**
	 * @param relationshipInfo: nombre completo de la clase origen y destino
	 * de la relación
	 * 
	 * Añade la relación recibida como parámetro al listado de relaciones que se han 
	 * de añadir al diagrama vinculado.
	 */
	public void deleteAssociationRelationship (String relationshipInfo){
		this.deleteAssociationRelationships.add(relationshipInfo);
	}
	
	/**
	 * @param relationshipInfo: nombre completo de la clase origen y destino
	 * de la relación
	 * 
	 * Añade la relación recibida como parámetro al listado de relaciones que se han 
	 * de añadir al diagrama vinculado.
	 */
	public void addUseRelationship (String relationshipInfo){
		this.newUseRelationships.add(relationshipInfo);
	}
	
	/**
	 * @param relationshipInfo: nombre completo de la clase origen y destino
	 * de la relación
	 * 
	 * Añade la relación recibida como parámetro al listado de relaciones que se han 
	 * de añadir al diagrama vinculado.
	 */
	public void deleteUseRelationship (String relationshipInfo){
		this.deleteUseRelationships.add(relationshipInfo);
	}
	
	/**
	 * @param relationshipInfo: nombre completo de la clase origen y destino
	 * de la relación
	 * 
	 * Añade la relación recibida como parámetro al listado de relaciones que se han 
	 * de añadir al diagrama vinculado.
	 */
	public void addImplementationRelationship (String relationshipInfo){
		this.newImplementationRelationships.add(relationshipInfo);
	}
	
	/**
	 * @param relationshipInfo: nombre completo de la clase origen y destino
	 * de la relación
	 * 
	 * Añade la relación recibida como parámetro al listado de relaciones que se han 
	 * de añadir al diagrama vinculado.
	 */
	public void deleteImplementationRelationship (String relationshipInfo){
		this.deleteImplementationRelationships.add(relationshipInfo);
	}
	
	/**
	 * @param relationshipInfo: nombre completo de la clase origen y destino
	 * de la relación
	 * 
	 * Añade la relación recibida como parámetro al listado de relaciones que se han 
	 * de añadir al diagrama vinculado.
	 */
	public void addInheritanceRelationship (String relationshipInfo){
		this.newInheritanceRelationships.add(relationshipInfo);
	}
	
	/**
	 * @param relationshipInfo: nombre completo de la clase origen y destino
	 * de la relación
	 * 
	 * Añade la relación recibida como parámetro al listado de relaciones que se han 
	 * de añadir al diagrama vinculado.
	 */
	public void deleteInheritanceRelationship (String relationshipInfo){
		this.deleteInheritanceRelationships.add(relationshipInfo);
	}
	
	
	/**
	 * @param className: nombre de una clase Java
	 * @return true si la clase recibida como parámetro esta en el listado de clases
	 * a eliminar del diagrama.
	 */
	public boolean isDeleteClass (String className){
		boolean deleteClass = false;
		for (String clazz : deleteClasses){
			if (clazz.equals(className)){
				deleteClass = true;
			}
		}
		return deleteClass;
	}
	
	/**
	 * @param className: nombre de una clase Java.
	 * @param fieldName: nombre de un atributo de una clase Java.
	 * @return true si el atributo recibido como parámetro esta en el listado de atributos
	 * a eliminar del diagrama.
	 */
	public boolean isDeleteAttribute (String className, String fieldName){
		boolean deleteAttribute = false;
		for (String classAndAttribute : deleteAttributes){
			String clazz = classAndAttribute.substring(0, classAndAttribute.indexOf("_"));
			String attribute = classAndAttribute.substring(classAndAttribute.indexOf("_") + 1, classAndAttribute.length());
			if (clazz.equals(className) && attribute.equals(fieldName)){
				deleteAttribute = true;
			}
		}
		return deleteAttribute;
	}
	
	/**
	 * @param className: nombre de una clase Java
	 * @param method: método de la clase recibida en el parámetro "className"
	 * @return true si el método recibido como parámetro esta en el listado de métodos
	 * a eliminar del diagrama; false en caso contrario.
	 */
	public boolean isDeleteMethod(String className, Method method, Clss clss){
		boolean deleteMethod = false;
		String methodDeclaration = method.getName() + "(";
		//Añadimos los parámetros a la declaración del método:
		Parameter[] parameters = method.getParameters();
		int i = 0;
		for (Parameter parameter : parameters){
		    if(i++ != parameters.length - 1){
		    	//Añadimos el tipo de cada parámetro a la declaración del método
				if (parameter.getParameterizedType().getClass().getSimpleName().equals("ParameterizedTypeImpl")){
					methodDeclaration = methodDeclaration + parameter.getType().getSimpleName() + "<";
					ArrayList<Class<?>> parameterClasses = clss.getParametrizedParameterClasses(parameter.getParameterizedType());
					for (Class<?> parameterClass : parameterClasses){
						methodDeclaration = methodDeclaration + parameterClass.getSimpleName() + " ";	
					}
					methodDeclaration = methodDeclaration + ">";
				}else{
					methodDeclaration = methodDeclaration + parameter.getType().getSimpleName();
				}
				//Añadimos el nombre de cada parámetro a la declaración del método
				methodDeclaration = methodDeclaration + " " + parameter.getName() + ", ";
		    }else{
		    	//Añadimos el tipo del último parámetro a la declaración del método
		    	if (parameter.getParameterizedType().getClass().getSimpleName().equals("ParameterizedTypeImpl")){
					methodDeclaration = methodDeclaration + parameter.getType().getSimpleName() + "<";
					ArrayList<Class<?>> parameterClasses = clss.getParametrizedParameterClasses(parameter.getParameterizedType());
					for (Class<?> parameterClass : parameterClasses){
						methodDeclaration = methodDeclaration + parameterClass.getSimpleName() + " ";	
					}
					methodDeclaration = methodDeclaration + ">";
				}else{
					methodDeclaration = methodDeclaration + parameter.getType().getSimpleName();
				}
				//Añadimos el nombre del último parámetro a la declaración del método
		    	methodDeclaration = methodDeclaration + " " + parameter.getName();
		    }
		}
		
		methodDeclaration = methodDeclaration + "): ";
		 
		//Añadimos el tipo que devuelve el método a su declaración:
		if (method.getGenericReturnType().getClass().getSimpleName().equals("ParameterizedTypeImpl")){
			methodDeclaration = methodDeclaration + method.getReturnType().getSimpleName() + "<";
			ArrayList<Class<?>> returnTypeClasses = clss.getParametrizedParameterClasses(method.getGenericReturnType());
			for (Class<?>  returnTypeClass : returnTypeClasses){
				methodDeclaration = methodDeclaration + returnTypeClass.getSimpleName() + " ";	
			}
			methodDeclaration = methodDeclaration + ">";
		} else{
			methodDeclaration = methodDeclaration + method.getReturnType().getSimpleName();
		}
		
		for (String classAndMethod : deleteMethods){
			String clazz = classAndMethod.substring(0, classAndMethod.indexOf("_"));
			String deleteMethodDeclaration = classAndMethod.substring(classAndMethod.indexOf("_") + 1, classAndMethod.length());
			if (clazz.equals(className) && deleteMethodDeclaration.equals(methodDeclaration)){
				deleteMethod = true;
			}
		}
		return deleteMethod;
	}
	
	
	/**
	 * @param clss: Objeto del tipo Clss correspondiente a una clase Java.
	 * @param relationship: relacion entre clases Java
	 * @return true si la relación indicada en el parámetro "relationship" 
	 * con la clase recibida en el parámetro "clss" esta en el listado de relaciones
	 * a eliminar del diagrama; false en caso contrario.
	 */
	public boolean isDeleteRelationship(Clss clss, Map.Entry<String, Relationship> relationship) {
		boolean deleteRelationship = false;
		switch (relationship.getValue()) {
		case ASSOCIATION:
			for (String originAndDestinyClass : deleteAssociationRelationships) {
				String originClass = originAndDestinyClass.substring(0, originAndDestinyClass.indexOf("_"));
				String destinyClass = originAndDestinyClass.substring(originAndDestinyClass.indexOf("_") + 1,
						originAndDestinyClass.length());
				if (originClass.equals(clss.getClazz().getName()) && destinyClass.equals(relationship.getKey())) {
					deleteRelationship = true;
				}
			}
			break;
		case ASSOCIATION_MANY:
			for (String originAndDestinyClass : deleteAssociationRelationships) {
				String originClass = originAndDestinyClass.substring(0, originAndDestinyClass.indexOf("_"));
				String destinyClass = originAndDestinyClass.substring(originAndDestinyClass.indexOf("_") + 1,
						originAndDestinyClass.length());
				if (originClass.equals(clss.getClazz().getName()) && destinyClass.equals(relationship.getKey())) {
					deleteRelationship = true;
				}
			}
			break;
		case COMPOSITION:
			for (String originAndDestinyClass : deleteCompositionRelationships) {
				String originClass = originAndDestinyClass.substring(0, originAndDestinyClass.indexOf("_"));
				String destinyClass = originAndDestinyClass.substring(originAndDestinyClass.indexOf("_") + 1,
						originAndDestinyClass.length());
				if (originClass.equals(clss.getClazz().getName()) && destinyClass.equals(relationship.getKey())) {
					deleteRelationship = true;
				}
			}
			break;
		case COMPOSITION_MANY:
			for (String originAndDestinyClass : deleteCompositionRelationships) {
				String originClass = originAndDestinyClass.substring(0, originAndDestinyClass.indexOf("_"));
				String destinyClass = originAndDestinyClass.substring(originAndDestinyClass.indexOf("_") + 1,
						originAndDestinyClass.length());
				if (originClass.equals(clss.getClazz().getName()) && destinyClass.equals(relationship.getKey())) {
					deleteRelationship = true;
				}
			}
			break;
		case USAGE:
			for (String originAndDestinyClass : deleteUseRelationships) {
				String originClass = originAndDestinyClass.substring(0, originAndDestinyClass.indexOf("_"));
				String destinyClass = originAndDestinyClass.substring(originAndDestinyClass.indexOf("_") + 1,
						originAndDestinyClass.length());
				if (originClass.equals(clss.getClazz().getName()) && destinyClass.equals(relationship.getKey())) {
					deleteRelationship = true;
				}
			}
			break;
		case INHERITANCE:
			for (String originAndDestinyClass : deleteInheritanceRelationships) {
				String originClass = originAndDestinyClass.substring(0, originAndDestinyClass.indexOf("_"));
				String destinyClass = originAndDestinyClass.substring(originAndDestinyClass.indexOf("_") + 1,
						originAndDestinyClass.length());
				if (originClass.equals(clss.getClazz().getName()) && destinyClass.equals(relationship.getKey())) {
					deleteRelationship = true;
				}
			}
			break;
		case IMPLEMENTATION:
			for (String originAndDestinyClass : deleteImplementationRelationships) {
				String originClass = originAndDestinyClass.substring(0, originAndDestinyClass.indexOf("_"));
				String destinyClass = originAndDestinyClass.substring(originAndDestinyClass.indexOf("_") + 1,
						originAndDestinyClass.length());
				if (originClass.equals(clss.getClazz().getName()) && destinyClass.equals(relationship.getKey())) {
					deleteRelationship = true;
				}
			}
			break;
		}
		return deleteRelationship;
	}
	
	
	/**
	 * @param className: nombre de una clase Java.
	 * @return mapa con las relaciones que hay que añadir en el diagrama de clases
	 * correspondiente; relaciones cuyo origen es la clase recibida como parámetro
	 * "className".
	 */
	public Map<String, Relationship> getClassRelationshipsToAdd(String className){
		Map<String, Relationship> relationships = new HashMap<>();
		for (String relationship : newCompositionRelationships){
			String originClass = relationship.substring(0, relationship.indexOf("_"));
			if (originClass.equals(className)){
				String destinyClass = relationship.substring(relationship.indexOf("_") + 1,
						relationship.length());
				relationships.put(destinyClass, Relationship.COMPOSITION);
			}
		}
		for (String relationship : newAssociationRelationships){
			String originClass = relationship.substring(0, relationship.indexOf("_"));
			if (originClass.equals(className)){
				String destinyClass = relationship.substring(relationship.indexOf("_") + 1,
						relationship.length());
				relationships.put(destinyClass, Relationship.ASSOCIATION);
			}
		}
		for (String relationship : newUseRelationships){
			String originClass = relationship.substring(0, relationship.indexOf("_"));
			if (originClass.equals(className)){
				String destinyClass = relationship.substring(relationship.indexOf("_") + 1,
						relationship.length());
				relationships.put(destinyClass, Relationship.USAGE);
			}
		}
		for (String relationship : newInheritanceRelationships){
			String originClass = relationship.substring(0, relationship.indexOf("_"));
			if (originClass.equals(className)){
				String destinyClass = relationship.substring(relationship.indexOf("_") + 1,
						relationship.length());
				relationships.put(destinyClass, Relationship.INHERITANCE);
			}
		}
		for (String relationship : newImplementationRelationships){
			String originClass = relationship.substring(0, relationship.indexOf("_"));
			if (originClass.equals(className)){
				String destinyClass = relationship.substring(relationship.indexOf("_") + 1,
						relationship.length());
				relationships.put(destinyClass, Relationship.IMPLEMENTATION);
			}
		}
		return relationships;
	}
	
	
	/**
	 * @param className: nombre de una clase Java.
	 * @return listado de atributos para añadir a la clase recibida como parámetro "className".
	 */
	public ArrayList<String> getClassFieldsToAdd(String className) {
		ArrayList<String> fieldsToAdd = new ArrayList<String>();
		for (String fieldToAdd : newAttributes) {
			String newAttributeClassName = fieldToAdd.substring(fieldToAdd.indexOf("_") + 1, fieldToAdd.lastIndexOf("_"));
			if (newAttributeClassName.equals(className)) {
				String fieldVisibility = fieldToAdd.substring(0, fieldToAdd.indexOf("_") + 1);
				String fieldNameAndType = fieldToAdd.substring(fieldToAdd.lastIndexOf("_") + 1, fieldToAdd.length());
				String field = fieldVisibility.concat(fieldNameAndType);
				fieldsToAdd.add(field);
			}
		}
		return fieldsToAdd;
	}
	
	
	/**
	 * @param className: nombre de una clase Java.
	 * @return listado de métodos para añadir a la clase recibida como parámetro "className".
	 */
	public ArrayList<String> getClassMethodsToAdd(String className){
		ArrayList<String> methodsToAdd = new ArrayList<String>();
		for (String methodToAdd : newMethods) {
			String newMethodClassName = methodToAdd.substring(methodToAdd.indexOf("_") + 1, methodToAdd.lastIndexOf("_"));
			if (newMethodClassName.equals(className)) {
				String fieldVisibility = methodToAdd.substring(0, methodToAdd.indexOf("_") + 1);
				String fieldNameAndType = methodToAdd.substring(methodToAdd.lastIndexOf("_") + 1, methodToAdd.length());
				String field = fieldVisibility.concat(fieldNameAndType);
				methodsToAdd.add(field);
			}
		}
		return methodsToAdd;
	}

	/**
	 * @return listado de clases para añadir al 
	 * diagrama de clases correspondiente.
	 */
	public ArrayList<String> getClassesToAdd(){
		return this.newClasses;
	}
}
