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

package net.sourceforge.appgen.databinding;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

import net.sourceforge.appgen.util.ConventionUtils;

/**
 * @author Byeongkil Woo
 */
public class PackageNameValidator implements IValidator {

	public IStatus validate(Object value) {
		if (value instanceof String) {
			String s = (String) value;

			if (s == null || s.length() == 0) {
				return ValidationStatus.error("Enter the package name.");
			}

			if (s.startsWith(".") || s.endsWith(".")) {
				return ValidationStatus.error("Invalid package name. A package name connot start or end with a dot");
			}

			String patternExp = "^([_a-zA-Z]+[a-zA-Z0-9_]*)?$";
			Pattern pattern = Pattern.compile(patternExp);

			String[] packageNames = s.split("[.]");
			if (packageNames.length == 0) {
				packageNames = new String[] { s };
			}

			for (String packageName : packageNames) {
				if (packageName.length() == 0) {
					return ValidationStatus.error("Invalid package name. A package name must not contain two consecutive dots");
				}

				Matcher matcher = pattern.matcher(packageName);

				if (!matcher.matches()) {
					return ValidationStatus.error("Invalid package name. '" + packageName + "' is not a valid Java identifier");
				}
			}

			if ((s.equals("java") || s.startsWith("java.") || s.equals("javax") || s.startsWith("javax.")) 
					|| (s.equals("org") || s.equals("com")) 
					|| (s.equals("org.springframework") || s.startsWith("org.springframework.")) 
					|| (s.equals("org.apache") || s.startsWith("org.apache."))) {
				return ValidationStatus.error("Invalid package name. Already existing(using) package name.");
			}
			
			if (ConventionUtils.isReservedWord(s)) {
				return ValidationStatus.error("Invalid package name. Reserved words.");
			}

			return ValidationStatus.OK_STATUS;
		} else {
			throw new RuntimeException("Not supposed to be called for non-strings.");
		}
	}
}
