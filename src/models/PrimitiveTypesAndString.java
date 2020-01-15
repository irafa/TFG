/**
 * 
 */
package models;

import java.util.Arrays;

/**
 * @author Rafael Armesilla Sánchez
 *
 */
public enum PrimitiveTypesAndString {
	BYTE, BOOLEAN, SHORT, CHAR, INT, FLOAT, LONG, DOUBLE, STRING;

	private static final String ALL_TYPES_STRING = Arrays.toString(PrimitiveTypesAndString.values());

	/**
	 * @param clss: objeto de tipo Class<?> que representa el tipo de una variable
	 * @return true en caso de que el tipo sea primitivo o String, false
	 * en caso contrario.
	 */
	public static boolean isPrimitiveOrString(Class<?> clss) {
		String className = clss.getSimpleName().toUpperCase();
		if (ALL_TYPES_STRING.contains(className)) {
			return true;
		} else {
			return false;
		}
	}
}