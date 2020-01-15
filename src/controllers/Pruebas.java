package controllers;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.google.common.base.Defaults;


public class Pruebas {

	

	

	

	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	    try {
//	    	String clase = "C:/Users/rarmesil/Desktop/TFG/Ejemplos/klondike/bin/klondike/Klondike";
//			File classFile= new File (clase);
//			//System.out.println(classFile.getName());
//			System.out.println(classFile.getAbsolutePath());
//			String classPath = clase.substring(raiz.length() + 1);
//			System.out.println(classPath.replace("/", "."));	    	
	    	

/*	    	URLClassLoader loader = new URLClassLoader(new URL[] { new URL("https://github.com/irafa/pruebaurl/blob/master/klondikeJar.jar") });
	    	Class<?> clss = loader.loadClass("Klondike");   */
	    	
	    	String raiz = "C:/Users/rarmesil/Desktop/TFG/Ejemplos/klondike/bin";
			File classFile2= new File (raiz);
			URL[] parentFolderPath={classFile2.toURI().toURL()};
			URLClassLoader urlcl = new URLClassLoader(parentFolderPath);
			Class<?> clss = urlcl.loadClass("klondike.Klondike");
			Clss classPrueba = new Clss(clss,"paqueteklondike");
			System.out.println(clss.getSimpleName());
			Field [] fields = clss.getDeclaredFields();// devuelve todos los atributos, no solo los públicos.
			System.out.println("Atributos de la clase......................");
			for (int i = 0; i < fields.length; i++){
				System.out.println(fields[i].getName());
			}
			System.out.println("Atributos de la clase......................FIN");
			
			classPrueba.calculateRelationships();
			classPrueba.pruebaPintarRelaciones();
			
			//relaciones de composición con método "getConstructors(); nada / con método getDeclaredConstructors() coge los parámetros, no lo definido con new en su interior"
/*			Constructor<?> [] constructor = clss.getDeclaredConstructors();
			for (int i = 0; i < constructor.length; i++){
				
				System.out.println(constructor[i].getName());
				System.out.println(constructor[i].getParameterCount());
				Type[] types= constructor[i].getGenericParameterTypes();
				for (int j = 0; j < types.length; j++){
					System.out.println("Pintando el Type");
					System.out.println(types[j].getTypeName().getClass().getSimpleName());
				}
				Parameter[] parameters = constructor[i].getParameters();
				for (int j = 0; j < parameters.length; j++){
					System.out.println("Pintando un parametro - INICIO");
					System.out.println(parameters[j].getName());
					System.out.println(parameters[j].getType());
					System.out.println(parameters[j].getType().getSimpleName());
					System.out.println(parameters[j].getParameterizedType());
					System.out.println("Pintando un parametro - FIN");
				}	
				Class<?> [] parameterTypes = constructor[i].getParameterTypes();
				for (int j = 0; j < parameterTypes.length; j++){
					System.out.println(parameterTypes[j].getName());
					System.out.println(parameterTypes[j].getSimpleName());				
					System.out.println(parameterTypes[j].getTypeName());
					System.out.println(parameterTypes[j].isPrimitive());
				}		
			}*/

			/*            for (Method meth : clss.getDeclaredMethods()) {
            meth.setAccessible(true);
        }*/
	
			/*	public String getRootPackage(){
			
			String rootPackage= null;
			for (Map.Entry<String, ArrayList<String>> entry : this.packageRelationships.entrySet()){
				if (!existDependentPackage(entry.getKey())){
					rootPackage = entry.getKey();
				}
			}
			return rootPackage;
		}*/
			
			
/*			Object defaultObject = null;
            for (Constructor<?> constructor3 : clss.getDeclaredConstructors()) {
                constructor3.setAccessible(true);
                // Tratar de instanciar un objeto de clase con atributo controller pero inicializado por parámetro
                // si se recorren todos los constructores para ver el tipo de todos los parámetros de todos los
                //construcores y comprarlos con el tipo de los atributos de la clase y los que casen son asociaciones.
                //
                defaultObject = constructor3.newInstance("stringParameter");
                constructor3.setAccessible(false);
            }
            
	        for (final Field field : clss.getDeclaredFields()) {
              field.setAccessible(true);
	          final Object value = field.get(defaultObject);
	          if ( !field.getType().isPrimitive() & value != null) {
	        	  System.out.println(field.getName() + " del tipo " + field.getType().getSimpleName() + " esta definida en el constructor (Composición)");
	        	  System.out.println(value.getClass().getSimpleName());
	          }
              field.setAccessible(false);
	        }*/
        
			
			/*	        private <T> T instantiate(Class<T> cls, Map<String, ? extends Object> args) throws Exception
	        {
	            // Create instance of the given class
	            final Constructor<T> constr = (Constructor<T>) cls.getConstructors()[0];
	            final List<Object> params = new ArrayList<Object>();
	            for (Class<?> pType : constr.getParameterTypes())
	            {
	                params.add((pType.isPrimitive()) ? ClassUtils.primitiveToWrapper(pType).newInstance() : null);
	            }
	            final T instance = constr.newInstance(params.toArray());

	            // Set separate fields
	            for (Map.Entry<String, ? extends Object> arg : args.entrySet()) {
	                Field f = cls.getDeclaredField(arg.getKey());
	                f.setAccessible(true);
	                f.set(instance, arg.getValue());
	            }

	            return instance;
	        }*/
			
			
/*			System.out.println("Numero de parámetros del constructor: " + constructor.getParameterCount());
			Parameter[] parameters = constructor.getParameters();
			for (int j = 0; j < parameters.length; j++) {
				System.out.println("Parametro - " + (j + 1) + " : " + parameters[j].getName() + " - "
						+ parameters[j].getType().getSimpleName());
			}

			Type[] types = constructor.getGenericParameterTypes();
			for (int j = 0; j < types.length; j++) {
				System.out.println("Type del parametro: " + (j + 1) + " " + types[j].getTypeName());
			}*/
			
					
			
			//............................................
			//............................................
			

			
			
/*			for (Class<?> iface : this.getClass().getInterfaces()) {
				try {
					iface.getMethod(method.getName(), method.getParameterTypes());
					overwritten = true;
				} catch (NoSuchMethodException ignored) {
					// El método no existe
				}
			}*/
			
			
			


			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
	}

}
