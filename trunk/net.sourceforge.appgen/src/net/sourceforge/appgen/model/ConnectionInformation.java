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

/**
 * @author Byeongkil Woo
 */
public class ConnectionInformation extends ValueModifyModel implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String DATABASE_TYPE_ORACLE = "Oracle";

	public static final String DATABASE_TYPE_MYSQL = "MySql";
	
	public static final String DEFAULT_CONNECTION_URL_ORACLE = "jdbc:oracle:thin:@127.0.0.1:1521:YOURDATABASE";
	
	public static final String DEFAULT_CONNECTION_URL_MYSQL = "jdbc:mysql://127.0.0.1:3306/yourdatabase?characterEncoding=utf-8";
	
	public static final String DEFAULT_DRIVER_CLASS_NAME_ORACLE = "oracle.jdbc.driver.OracleDriver";
	
	public static final String DEFAULT_DRIVER_CLASS_NAME_MYSQL = "com.mysql.jdbc.Driver";

	private String databaseType;

	private String url;

	private String user;

	private String password;

	private File driverFile;

	private String driverClassName;
	
	public ConnectionInformation() {
		super();
	}

	public static String[] getDatabaseTypes() {
		return new String[] { DATABASE_TYPE_ORACLE, DATABASE_TYPE_MYSQL };
	}
	
	public static String[] getDefaultConnectionUrls() {
		return new String[] { DEFAULT_CONNECTION_URL_ORACLE, DEFAULT_CONNECTION_URL_MYSQL };
	}
	
	public static String[] getDefaultDriverClassNames() {
		return new String[] { DEFAULT_DRIVER_CLASS_NAME_ORACLE, DEFAULT_DRIVER_CLASS_NAME_MYSQL };
	}
	
	public static String getDefaultConnectionUrl(String databaseType) {
		for (int i = 0; i < getDatabaseTypes().length; i++) {
			String type = getDatabaseTypes()[i];
			if (type.equals(databaseType)) {
				return getDefaultConnectionUrls()[i];
			}
		}
		
		return null;
	}
	
	public static String getDefaultDriverClassName(String databaseType) {
		for (int i = 0; i < getDatabaseTypes().length; i++) {
			String type = getDatabaseTypes()[i];
			if (type.equals(databaseType)) {
				return getDefaultDriverClassNames()[i];
			}
		}
		
		return null;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(super.toString() + "(");
		builder.append("databaseType='" + databaseType + "'");
		builder.append(",");
		builder.append("url='" + url + "'");
		builder.append(",");
		builder.append("user='" + user + "'");
		builder.append(",");
		builder.append("password='" + "********" + "'");
		builder.append(",");
		builder.append("driverFile='" + driverFile + "'");
		builder.append(",");
		builder.append("driverClassName='" + driverClassName + "'");
		builder.append(")");

		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((databaseType == null) ? 0 : databaseType.hashCode());
		result = prime * result + ((driverClassName == null) ? 0 : driverClassName.hashCode());
		result = prime * result + ((driverFile == null) ? 0 : driverFile.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		if (databaseType == null) {
			if (other.databaseType != null)
				return false;
		} else if (!databaseType.equals(other.databaseType))
			return false;
		if (driverClassName == null) {
			if (other.driverClassName != null)
				return false;
		} else if (!driverClassName.equals(other.driverClassName))
			return false;
		if (driverFile == null) {
			if (other.driverFile != null)
				return false;
		} else if (!driverFile.equals(other.driverFile))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	public String getDatabaseType() {
		return databaseType;
	}

	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
		
		valueModified();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String connectionUrl) {
		this.url = connectionUrl;
		
		valueModified();
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
		
		valueModified();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
		
		valueModified();
	}

	public File getDriverFile() {
		return driverFile;
	}

	public void setDriverFile(File driverFile) {
		this.driverFile = driverFile;

		setDriverClassName(this.getDriverClassName());
		
		valueModified();
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
		
		valueModified();
	}

}
