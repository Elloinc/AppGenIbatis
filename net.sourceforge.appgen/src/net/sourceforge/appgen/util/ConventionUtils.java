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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Byeongkil Woo
 */
public abstract class ConventionUtils {

	public static final String FIXME = "// FIXME: Entity generator.";

	public static final String[] RESERVED_WORDS = new String[] {
		// Java language.
		"abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "continue", "default", "do", "double", "else", 
		"enum", "extends", "false", "final", "finally", "float", "for", "if", "implements", "import", "instanceof", "int", "interface", 
		"long", "native", "new", "null", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super", 
		"switch", "synchronized", "this", "throw", "throws", "transient", "true", "try", "void", "volatile", "while", 
		"const", "goto", 
		// Entity generator.
		"httpServletRequest", "httpServletResponse", 
		"modelAndView", "servletRequestUtils", "servletRequestUtils", 
		"servletRequestDataBinder", "binder", "bindException", "errors", "customDateEditor", 
		"sqlMapClientDaoSupport", 
		"map", "linkedHashMap", "param", "date", "simpleDateFormat", 
		"controller", "service", "serviceImpl", "dao", "daoImpl", "criteria", "validator", "paging", 
		"request", "response", "mav", "mode", "totalResults"
	};

	public static boolean isReservedWord(String s) {
		for (String word : RESERVED_WORDS) {
			if (word.equals(s)) {
				return true;
			}
		}
		
		return false;
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

}
