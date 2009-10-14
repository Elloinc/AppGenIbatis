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

package net.sourceforge.appgen.connector;

import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.util.List;
import java.util.Properties;

import net.sourceforge.appgen.converter.CamelCaseConverter;
import net.sourceforge.appgen.converter.StringConverter;
import net.sourceforge.appgen.model.ConnectionInformation;
import net.sourceforge.appgen.model.Entity;

/**
 * @author Byeongkil Woo
 */
public abstract class JdbcConnector {

	private ConnectionInformation connectionInformation;
	
	private StringConverter baseNameConverter = new CamelCaseConverter();
	
	private StringConverter fieldNameConverter = new CamelCaseConverter();

	public StringConverter getBaseNameConverter() {
		return baseNameConverter;
	}

	public void setBaseNameConverter(StringConverter baseNameConverter) {
		this.baseNameConverter = baseNameConverter;
	}

	public StringConverter getFieldNameConverter() {
		return fieldNameConverter;
	}

	public void setFieldNameConverter(StringConverter fieldNameConverter) {
		this.fieldNameConverter = fieldNameConverter;
	}

	public JdbcConnector(ConnectionInformation connectionInformation) {
		this.connectionInformation = connectionInformation;
	}

	public Connection getConnection() throws Exception {
		Connection connection = null;

		String driverFilePath = connectionInformation.getDriverFile().getPath();
		String driverClassName = connectionInformation.getDriverClassName();

		URL driverFileUrl = new URL("file:" + driverFilePath);
		
		URLClassLoader loader = new URLClassLoader(new URL[] { driverFileUrl }, getClass().getClassLoader());
		
		Driver driver = (Driver) loader.loadClass(driverClassName).newInstance();
		Properties info = new Properties();
		info.put("user", connectionInformation.getUser());
		info.put("password", connectionInformation.getPassword() != null ? connectionInformation.getPassword() : "");
		
		connection = driver.connect(connectionInformation.getUrl(), info);

		return connection;
	}

	public abstract List<Entity> getEntityList() throws Exception;

}
