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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

import net.sourceforge.appgen.model.ConnectionInformation;

/**
 * @author Byeongkil Woo
 */
public class DriverClassNameValidator implements IValidator {

	private ConnectionInformation connectionInformation;
	
	public DriverClassNameValidator(ConnectionInformation connectionInformation) {
		this.connectionInformation = connectionInformation;
	}
	
	public IStatus validate(Object value) {
		File file = connectionInformation.getDriverFile();
		
		if (value instanceof String) {
			if (file == null || !file.exists() || !file.isFile()) {
				return ValidationStatus.error("File does not exist. " + file);
			}
			
			String s = (String) value;
			
			try {
				URL driverFileUrl = new URL("file:" + file.getPath());
				
				URLClassLoader loader = new URLClassLoader(new URL[] { driverFileUrl }, getClass().getClassLoader());
				
				Object driver = loader.loadClass(s).newInstance();
				
				if (driver instanceof Driver) {
					return ValidationStatus.OK_STATUS;
				}
			} catch (MalformedURLException e) {
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			} catch (ClassNotFoundException e) {
			}
			
			return ValidationStatus.error("Invalid driver.");
		} else {
			throw new RuntimeException("Not supposed to be called for non-strings.");
		}
	}

}
