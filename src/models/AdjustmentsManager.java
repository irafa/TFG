/**
 * 
 */
package models;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Rafael Armesilla Sánchez
 *
 */
public class AdjustmentsManager {

	private String adjustmentsFileUrl;
	private Map <String, DiagramAdjustments> diagramsAdjustments;

	public AdjustmentsManager(String adjustmentsFileUrl){
		this.adjustmentsFileUrl = adjustmentsFileUrl;
		this.diagramsAdjustments = new HashMap<String, DiagramAdjustments>();
	}

	/**
	 * @param adjustmentsFileUrl: ruta del fichero de texto donde se escriben los comandos
	 * @return clase FileWriter que sirve para escribir secuencias de caractéres
	 * en ficheros
	 */
	private PrintWriter getFileWriter() {
		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			// Apertura del fichero de ajustes
			fw = new FileWriter(this.adjustmentsFileUrl, true);
			pw = new PrintWriter(new BufferedWriter(fw));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pw;
	}
	
	/**
	 * Genera un fichero de texto donde el usuario puede
	 * definir comandos para modificar cualquiera de los diagramas de clases
	 * generados en la última ejecución de esta aplicación.
	 */
	public void generateAdjustmentsManagerFile(){
		PrintWriter pw = this.getFileWriter();
		
		pw.println("*----------------------------------------_*");
		pw.println("*--------------INICIO COMANDOS-----------_*");
		pw.println("*----------------------------------------_*");
		pw.println("Borre esta línea e introduzca un comando por línea_");
		pw.println("*----------------------------------------_*");
		pw.println("*---------------FIN COMANDOS-------------_*");
		pw.println("*----------------------------------------_*");
		pw.println("INSTRUCCIONES DE USO:");
		pw.println();
		pw.println("Se ha de introducir un comando por línea en el apartado de \"COMANDOS\".");
		pw.println();
		pw.println("Para modificar un diagrama de clases, el primer comando ha de ser");
		pw.println("el nombre del fichero PlantUML correspondiente al diagrama de clases que");
		pw.println("queremos modificar, tal y como se genera en la carpeta \"PlantUML\",");
		pw.println("empezando por el carácter \">\" y seguido de dos puntos (\":\"). Por ejemplo:");
		pw.println();
		pw.println("\">MoveControllerClassDiagram:\"");
		pw.println();
		pw.println("A continuación se han de definir los comandos que modifican dicho diagrama.");
		pw.println("Los posibles comandos son:");
		pw.println();
		pw.println("+class_NC_PT_T_ENUM1_ENUM2_ETC (Añadir una clase)");
		pw.println();
		pw.println("Donde:");
		pw.println();
		pw.println("NC: el nombre completo de la clase");
		pw.println("PT: printing Type, color de fondo de la clase (CONSUMERCLASS/CONSUMEDCLASS/STANDARDCLASS/CONTROLLERCLASS)");
		pw.println("T: tipo de la clase (interface/enum/abstract class/class)");
		pw.println("ENUMX: valores del enumerado");
		pw.println();
		pw.println("-class_NC (Eliminar una clase)");
		pw.println();
		pw.println("Donde:");
		pw.println();
		pw.println("NC: el nombre completo de la clase");
		pw.println();
		pw.println("+attribute_V_NC_NA (Añadir un atributo)");
		pw.println();
		pw.println("Donde:");
		pw.println();
		pw.println("V: visibilidad (-/#/~/+)");
		pw.println("NC: el nombre completo de la clase");
		pw.println("NA: nuevo atributo con el formato \"nombreAtributo : Tipo\".");
		pw.println("Al comienzo del nombre del atributo se pueden incluir los");
		pw.println("modificadores \"{static}\" y \"final\".");
		pw.println();
		pw.println("-attribute_NC_NA (Eliminar un atributo)");
		pw.println();
		pw.println("Donde:");
		pw.println();
		pw.println("NC: el nombre completo de la clase");
		pw.println("NA: atributo con el formato \"nombreAtributo\".");
		pw.println();
		pw.println("+method_V_NC_NM  (Añadir/Eliminar un método)");
		pw.println();
		pw.println("Donde:");
		pw.println();
		pw.println("V: visibilidad (-/#/~/+)");
		pw.println("NC: el nombre completo de la clase");
		pw.println("NM: nuevo método con el formato ");
		pw.println("\"nombreMétodo(ParamTipo ParamNombre, Param2Tipo Param2Nombre, etc): TipoDevuelto\".");
		pw.println();
		pw.println("-method_NC_NM  (Eliminar un método)");
		pw.println();
		pw.println("Donde:");
		pw.println();
		pw.println("NC: el nombre completo de la clase");
		pw.println("NM: método con el formato ");
		pw.println("\"nombreMétodo(ParamTipo ParamNombre, Param2Tipo Param2Nombre, etc): TipoDevuelto\".");
		pw.println();
		pw.println("+/-composition_NCO_NCD (Añadir/Eliminar una relación de composición)");
		pw.println();
		pw.println("Donde:");
		pw.println();
		pw.println("NCO: nombre completo de la clase origen de la relación");
		pw.println("NCD: nombre completo de la clase destino de la relación");
		pw.println();
		pw.println("+/-association_NCO_NCD (Añadir/Eliminar una relación de asociación)");
		pw.println();
		pw.println("Donde:");
		pw.println();
		pw.println("NCO: nombre completo de la clase origen de la relación");
		pw.println("NCD: nombre completo de la clase destino de la relación");
		pw.println();
		pw.println("+/-use_NCO_NCD (Añadir/Eliminar una relación de uso)");
		pw.println();
		pw.println("Donde:");
		pw.println();
		pw.println("NCO: nombre completo de la clase origen de la relación");
		pw.println("NCD: nombre completo de la clase destino de la relación");
		pw.println();
		pw.println("+/-implementation_NCO_NCD (Añadir/Eliminar una relación de implementación)");
		pw.println();
		pw.println("Donde:");
		pw.println();
		pw.println("NCO: nombre completo de la clase origen de la relación, ");
		pw.println("la clase que implementa la interfaz.");
		pw.println("NCD: nombre completo de la clase destino de la relación");
		pw.println();
		pw.println("+/-inheritance_NCO_NCD (Añadir/Eliminar una relación de herencia)");
		pw.println();
		pw.println("Donde:");
		pw.println();
		pw.println("NCO: nombre completo de la clase origen de la relación, ");
		pw.println("la clase hija de la herencia.");
		pw.println("NCD: nombre completo de la clase destino de la relación");
		pw.println();
		pw.println("---------------------------------------------------------------------");
		pw.println("---------------------------------------------------------------------");
		pw.println("Ejemplo de modificación de dos diagramas:");
		pw.println();
		pw.println(">MoveControllerClassDiagram:");
		pw.println("+class_klondike.controllers.PruebaController_CONSUMERCLASS_class");
		pw.println("+class_klondike.controllers.PruebaAbstract_CONSUMEDCLASS_abstract class");
		pw.println("+class_klondike.controllers.Pruebainterface_STANDARDCLASS_interface");
		pw.println("+class_klondike.controllers.Pruebaenum_CONTROLLERCLASS_enum_Valor1_Valor 2_valor3");
		pw.println("-class_klondike.controllers.Controller");
		pw.println("+attribute_-_klondike.controllers.PruebaController_atributoPrueba : ArrayList<Session>");
		pw.println("+attribute_-_klondike.controllers.PruebaController_atributoPrueba2 : String");
		pw.println("-attribute_klondike.controllers.PlayController_redoController");
		pw.println("+method_+_klondike.controllers.MoveController_testMethodName(ParamTipo ParamNombre, Param2Tipo Param2Nombre): Error");
		pw.println("-method_klondike.controllers.MoveController_moveFromWasteToStock(): Error");
		pw.println(">utilsPackageClassDiagram:");
		pw.println("+composition_klondike.utils.YesNoDialog_klondike.utils.Command");
		pw.println("-composition_klondike.utils.Menu_klondike.utils.Command");
		pw.println("+association_klondike.utils.IO_klondike.utils.Command");
		pw.println("+use_klondike.utils.IO_klondike.utils.Menu");
		pw.println("-use_klondike.utils.IO_klondike.utils.CloseInterval");
		pw.println("+implementation_klondike.utils.Command_klondike.utils.Menu");
		pw.println("+inheritance_klondike.utils.CloseInterval_klondike.utils.IO");
		
		pw.close();
	}
	
	
	/**
	 * Carga en memoria los comandos definidos por el usuario
	 * para alterar los diagramas de clases generados en la 
	 * anterior ejecución de la aplicación.
	 * @throws Exception 
	 */
	public void loadCommands() throws Exception {
		BufferedReader reader;
		DiagramAdjustments diagramAdjustments = null;
		int i = 0;
		try {
			reader = new BufferedReader(new FileReader(this.adjustmentsFileUrl));
			String line = reader.readLine();
			while (!line.equals("*---------------FIN COMANDOS-------------_*")) {
				i++;
				if (line.startsWith(">")){
					diagramAdjustments = new DiagramAdjustments();
					String diagramName = line.substring(1, line.indexOf(":"));
					this.diagramsAdjustments.put(diagramName, diagramAdjustments);
				} else {
					String adjustment = line.substring(0, line.indexOf("_"));
					switch (adjustment) {
					case "+class":
						diagramAdjustments.addClass(line.substring(line.indexOf("_") + 1, line.length()));
						break;
					case "-class":
						diagramAdjustments.deleteClass(line.substring(line.indexOf("_") + 1, line.length()));
						break;
					case "+attribute":
						diagramAdjustments.addAttribute(line.substring(line.indexOf("_") + 1, line.length()));
						break;
					case "-attribute":
						diagramAdjustments.deleteAttribute(line.substring(line.indexOf("_") + 1, line.length()));
						break;
					case "+method":
						diagramAdjustments.addMethod(line.substring(line.indexOf("_") + 1, line.length()));
						break;
					case "-method":
						diagramAdjustments.deleteMethod(line.substring(line.indexOf("_") + 1, line.length()));
						break;
					case "+composition":
						diagramAdjustments.addCompositionRelationship(line.substring(line.indexOf("_") + 1, line.length()));
						break;
					case "-composition":
						diagramAdjustments.deleteCompositionRelationship(line.substring(line.indexOf("_") + 1, line.length()));
						break;
					case "+association":
						diagramAdjustments.addAssociationRelationship(line.substring(line.indexOf("_") + 1, line.length()));
						break;
					case "-association":
						diagramAdjustments.deleteAssociationRelationship(line.substring(line.indexOf("_") + 1, line.length()));
						break;
					case "+use":
						diagramAdjustments.addUseRelationship(line.substring(line.indexOf("_") + 1, line.length()));
						break;
					case "-use":
						diagramAdjustments.deleteUseRelationship(line.substring(line.indexOf("_") + 1, line.length()));
						break;
					case "+implementation":
						diagramAdjustments.addImplementationRelationship(line.substring(line.indexOf("_") + 1, line.length()));
						break;
					case "-implementation":
						diagramAdjustments.deleteImplementationRelationship(line.substring(line.indexOf("_") + 1, line.length()));
						break;
					case "+inheritance":
						diagramAdjustments.addInheritanceRelationship(line.substring(line.indexOf("_") + 1, line.length()));
						break;
					case "-inheritance":
						diagramAdjustments.deleteInheritanceRelationship(line.substring(line.indexOf("_") + 1, line.length()));
						break;
					}
				}
				line = reader.readLine();
			}
			reader.close();
		} catch (Exception e) {
			throw new Exception ("Se ha producido un error al leer la línea " + i + " del fichero de ajustes manuales" , e);
		}
	}
	
	
	/**
	 * @param diagramName: nombre de un diagrama de clases UML
	 * @return objeto de tipo DiagramAdjustments con los ajustes a aplicar 
	 * al diagrama de clases recibido en el parámetro "diagramName".
	 */
	public DiagramAdjustments getDiagramAdjustments(String diagramName){
		return this.diagramsAdjustments.get(diagramName);
	}
	
	

	
}
