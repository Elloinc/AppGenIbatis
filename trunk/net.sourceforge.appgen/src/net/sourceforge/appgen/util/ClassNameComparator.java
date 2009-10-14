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

import java.util.Comparator;

/**
 * @author Byeongkil Woo
 */
public class ClassNameComparator implements Comparator<String> {

	public int compare(String o1, String o2) {
		int c = 0;
		
		c = compareTopPackage("java.", o1, o2);
		if (c != 0) {
			return c;
		}
		c = compareTopPackage("javax.", o1, o2);
		if (c != 0) {
			return c;
		}
		c = compareTopPackage("org", o1, o2);
		if (c != 0) {
			return c;
		}
		c = comparePackage(o1, o2);
		if (c != 0) {
			return c;
		}
		
		return o1.compareTo(o2);
	}
	
	private int compareTopPackage(String packageName, String o1, String o2) {
		boolean inPackage1 = o1.startsWith(packageName);
		boolean inPackage2 = o2.startsWith(packageName);
		
		if (inPackage1 && !inPackage2) {
			return -1;
		}
		if (!inPackage1 && inPackage2) {
			return 1;
		}
		
		return 0;
	}
	
	private int comparePackage(String o1, String o2) {
		String packageName1 = ConventionUtils.getPackageName(o1);
		String packageName2 = ConventionUtils.getPackageName(o2);
		
		if (!packageName1.equals(packageName2)) {
			return packageName1.compareTo(packageName2);
		}
		
		return 0;
	}

}
