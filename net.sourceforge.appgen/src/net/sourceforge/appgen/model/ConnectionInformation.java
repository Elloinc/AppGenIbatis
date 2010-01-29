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

package net.sourceforge.appgen.model;

import java.io.File;
import java.io.Serializable;
import java.util.Properties;

import net.sourceforge.appgen.connector.ProfileConnector;

/**
 * @author Byeongkil Woo
 */
public class ConnectionInformation extends ValueModifyModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;

	private String schema;

	public ConnectionInformation() {
		super();
	}
	
	public File getDriverFile() {
		ProfileConnector profileConnector = new ProfileConnector(this);
		
		Properties properties = profileConnector.getBaseProperties();
		
		return new File(properties.getProperty("jarList"));
	}
	
	public String getDriverClassName() {
		ProfileConnector profileConnector = new ProfileConnector(this);
		
		Properties properties = profileConnector.getBaseProperties();
		
		return properties.getProperty("org.eclipse.datatools.connectivity.db.driverClass");
	}
	
	public String getUrl() {
		ProfileConnector profileConnector = new ProfileConnector(this);
		
		Properties properties = profileConnector.getBaseProperties();
		
		return properties.getProperty("org.eclipse.datatools.connectivity.db.URL");
	}
	
	public String getUser() {
		ProfileConnector profileConnector = new ProfileConnector(this);
		
		Properties properties = profileConnector.getBaseProperties();
		
		return properties.getProperty("org.eclipse.datatools.connectivity.db.username");
	}
	
	public String getPassword() {
		ProfileConnector profileConnector = new ProfileConnector(this);
		
		Properties properties = profileConnector.getBaseProperties();
		
		return properties.getProperty("org.eclipse.datatools.connectivity.db.password");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		
		valueModified();
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;

		valueModified();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((schema == null) ? 0 : schema.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConnectionInformation other = (ConnectionInformation) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (schema == null) {
			if (other.schema != null)
				return false;
		} else if (!schema.equals(other.schema))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ConnectionInformation [name=" + name + ", schema=" + schema + "]";
	}

}
