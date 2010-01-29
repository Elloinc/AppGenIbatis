/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sourceforge.appgen.util;

import java.io.File;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import net.sourceforge.appgen.converter.FirstCharacterUpperCaseConverter;
import net.sourceforge.appgen.converter.StringConverter;

/**
 * @author Byeongkil Woo
 */
public abstract class ConventionUtils {

	private static StringConverter firstCharacterUpperCaseConverter = new FirstCharacterUpperCaseConverter();
	
	public static final String FIXME = "// FIXME: Entity generator.";

	public static final String[] RESERVED_WORDS = new String[] {
		// Java language -------------------------------------------------------------------------- //
		"abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "continue", "default", "do", "double", "else", 
		"enum", "extends", "false", "final", "finally", "float", "for", "if", "implements", "import", "instanceof", "int", "interface", 
		"long", "native", "new", "null", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super", 
		"switch", "synchronized", "this", "throw", "throws", "transient", "true", "try", "void", "volatile", "while", 
		"const", "goto", 
		// AppGen --------------------------------------------------------------------------------- //
		"serializable", "serialVersionUID", 
		"logger", "logFactory", 
		"obj", "result", "prime", 
		"assert", "errors", "validationUtils", 
		"dataAccessException", "actualRowsAffected", 
		"httpServletRequest", "httpServletResponse", 
		"modelAndView", "servletRequestUtils", "servletRequestUtils", 
		"servletRequestDataBinder", "binder", "bindException", "errors", "customDateEditor", 
		"sqlMapClientDaoSupport", 
		"list", "map", "linkedHashMap", "param", "date", "simpleDateFormat", 
		"controller", "simpleFormController", "service", "serviceImpl", "dao", "daoImpl", "criteria", "validator", "paging", 
		"request", "response", "mav", "mode", "totalResults", "command", 
		"attachFilePersister", "attachFilePropertyEditor", "fileSystemResource", "saveResource", 
		"attachFile"
	};

	public static boolean isReservedWord(String s) {
		for (String word : RESERVED_WORDS) {
			if (word.equals(s)) {
				return true;
			}
		}
		
		if (isJavaLangClass(s)) {
			return true;
		}
		
		return false;
	}
	
	public static boolean isJavaLangClass(String s) {
		if (s == null) {
			return false;
		}
		
		boolean exist = false;
		
		try {
			Class.forName("java.lang." + firstCharacterUpperCaseConverter.convert(s));
			exist = true;
		} catch (ClassNotFoundException e) {
		}
		
		return exist;
	}

	public static String getPath(String packageName) {
		if (packageName == null) {
			return packageName;
		}

		StringBuffer buffer = new StringBuffer();
		String[] packages = packageName.split("[.]");

		for (String s : packages) {
			if (buffer.length() > 0) {
				buffer.append(File.separator);
			}

			buffer.append(s);
		}

		return buffer.toString();
	}

	public static List<String> getImportDeclarations(List<String> classNameList, String exceptPackageName) {
		List<String> list = new ArrayList<String>();

		Collections.sort(classNameList, new ClassNameComparator());

		String previousFirstPackageName = null;
		for (String className : classNameList) {
			String firstPackageName = null;
			int firstIndex = className.indexOf('.');
			if (firstIndex > 0) {
				firstPackageName = className.substring(0, firstIndex);
			}

			String packageName = getPackageName(className);

			if (exceptPackageName != null && !exceptPackageName.equals(packageName)) {
				if (previousFirstPackageName != null && firstPackageName != null) {
					if (!firstPackageName.equals(previousFirstPackageName)) {
						list.add("");
					}
				}

				String importDeclaration = "import " + className + ";";
				if (!list.contains(importDeclaration)) {
					list.add("import " + className + ";");
				}
			}

			previousFirstPackageName = firstPackageName;
		}

		return list;
	}

	public static String getSimpleClassName(String className) {
		if (className == null || className.lastIndexOf('.') < 0 || className.lastIndexOf('.') + 1 > className.length()) {
			return className;
		}

		return className.substring(className.lastIndexOf('.') + 1);
	}

	public static String getPackageName(String className) {
		if (className == null) {
			return null;
		}

		int lastIndex = className.lastIndexOf('.');
		if (lastIndex > 0) {
			return className.substring(0, lastIndex);
		}

		return null;
	}

	public static boolean isLobColumn(int dataType) {
		boolean lob = false;
		
		switch (dataType) {
		case Types.BLOB:
			lob = true;
			break;
		case Types.CLOB:
			lob = true;
			break;
		default:
			break;
		}
		
		return lob;
	}
	
	public static String getJavaType(int dataType, int columnSize, int decimalDegit) {
		String fieldType = String.class.getName();
		
		switch (dataType) {
		case Types.ARRAY:
			break;
		case Types.BIGINT:
			fieldType = "long";
			break;
		case Types.BINARY:
			break;
		case Types.BIT:
			break;
		case Types.BLOB:
			break;
		case Types.BOOLEAN:
			fieldType = "boolean";
			break;
		case Types.CHAR:
			break;
		case Types.CLOB:
			break;
		case Types.DATALINK:
			break;
		case Types.DATE:
			fieldType = Date.class.getName();
			break;
		case Types.DECIMAL:
			if (decimalDegit > 0) {
				fieldType = "double";
			} else {
				fieldType = "int";
			}
			break;
		case Types.DISTINCT:
			break;
		case Types.DOUBLE:
			fieldType = "double";
			break;
		case Types.FLOAT:
			fieldType = "float";
			break;
		case Types.INTEGER:
			fieldType = "int";
			break;
		case Types.JAVA_OBJECT:
			break;
		case Types.LONGVARBINARY:
			break;
		case Types.LONGVARCHAR:
			break;
		case Types.NULL:
			break;
		case Types.NUMERIC:
			if (decimalDegit > 0) {
				fieldType = "double";
			} else {
				fieldType = "int";
			}
			break;
		case Types.OTHER:
			break;
		case Types.REAL:
			break;
		case Types.REF:
			break;
		case Types.SMALLINT:
			fieldType = "int";
			break;
		case Types.STRUCT:
			break;
		case Types.TIME:
			fieldType = Date.class.getName();
			break;
		case Types.TIMESTAMP:
			fieldType = Date.class.getName();
			break;
		case Types.TINYINT:
			fieldType = "int";
			break;
		case Types.VARBINARY:
			break;
		case Types.VARCHAR:
			break;
		default:
			break;
		}
		
		return fieldType;
	}
	
}
