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

package net.sourceforge.appgen.converter;

/**
 * @author Byeongkil Woo
 */
public class FirstCharacterUpperCaseConverter implements StringConverter {

	public String convert(String origin) {
		if (origin == null || origin.length() == 0) {
			return origin;
		}

		StringBuffer buf = new StringBuffer(origin);
		buf.setCharAt(0, Character.toUpperCase(origin.charAt(0)));

		return buf.toString();
	}

}
