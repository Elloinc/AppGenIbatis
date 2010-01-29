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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.sourceforge.appgen.converter.CamelCaseConverter;
import net.sourceforge.appgen.converter.StringConverter;
import net.sourceforge.appgen.model.ConnectionInformation;
import net.sourceforge.appgen.model.Entity;
import net.sourceforge.appgen.model.Field;
import net.sourceforge.appgen.util.ConventionUtils;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.IManagedConnection;
import org.eclipse.datatools.connectivity.ProfileManager;

/**
 * @author Byeongkil Woo
 */
public class ProfileConnector {

	private StringConverter baseNameConverter = new CamelCaseConverter();

	private StringConverter fieldNameConverter = new CamelCaseConverter();
	
	private ConnectionInformation connectionInformation;

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
	
	public ConnectionInformation getConnectionInformation() {
		return connectionInformation;
	}

	public void setConnectionInformation(ConnectionInformation connectionInformation) {
		this.connectionInformation = connectionInformation;
	}

	public ProfileConnector(ConnectionInformation connectionInformation) {
		setConnectionInformation(connectionInformation);
	}
	
	public static String[] getProfileNames() {
		ProfileManager profileManager = ProfileManager.getInstance();
		IConnectionProfile[] profiles = profileManager.getProfiles();
		
		List<String> profileNames = new ArrayList<String>();
		
		if (profiles != null) {
			for (IConnectionProfile profile : profiles) {
				if (profile.getCategory().getId().equals("org.eclipse.datatools.connectivity.db.category")) {
					profileNames.add(profile.getName());
				}
			}
		}
		
		return profileNames.toArray(new String[profileNames.size()]);
	}
	
	public IConnectionProfile getProfile() {
		ProfileManager profileManager = ProfileManager.getInstance();
		IConnectionProfile profile = profileManager.getProfileByName(connectionInformation.getName());
		
		return profile;
	}
	
	public Properties getBaseProperties() {
		IConnectionProfile profile = getProfile();
		
		return profile.getBaseProperties();
	}
	
	public IStatus connect() {
		IConnectionProfile profile = getProfile();
		
		if (profile.getConnectionState() == IConnectionProfile.CONNECTED_STATE) {
			return Status.OK_STATUS;
		}
		
		if (profile == null) {
			throw new RuntimeException("Can't find profile by name: " + connectionInformation.getName());
		}
		
		return profile.connect();
	}
	
	public IStatus disconnect() {
		IConnectionProfile profile = getProfile();
		
		if (profile.getConnectionState() == IConnectionProfile.DISCONNECTED_STATE) {
			return Status.OK_STATUS;
		}
		
		if (profile == null) {
			throw new RuntimeException("Can't find profile by name: " + connectionInformation.getName());
		}
		
		return profile.disconnect();
	}
	
	public Connection getRawConnection(IConnectionProfile profile) {
		if (profile.getConnectionState() != IConnectionProfile.CONNECTED_STATE) {
			connect();
		}
		
		IManagedConnection managedConnection = null;
		
		managedConnection = profile.getManagedConnection("java.sql.Connection");
		
		Connection connection = (Connection) managedConnection.getConnection().getRawConnection();
		
		return connection;
	}
	
	public List<Entity> getEntityList(IProgressMonitor monitor) throws Exception {
		List<Entity> entityList = new ArrayList<Entity>();

		IConnectionProfile profile = getProfile();
		
		if (profile == null) {
			throw new RuntimeException("Can't find profile by name: " + connectionInformation.getName());
		}
		
		Connection connection = null;

		DatabaseMetaData databaseMetadata = null;

		try {
			connection = getRawConnection(profile);
			
			databaseMetadata = connection.getMetaData();
				
			String catalog = connection.getCatalog();
			String schema = connectionInformation.getSchema();
				
			ResultSet rs = null;
				
			if (schema != null && schema.length() == 0) {
				schema = null;
			}

			rs = databaseMetadata.getTables(catalog, schema, "%", new String[] { "TABLE", "VIEW" });
			
			try {
				while (rs.next()) {
					String tableCat = rs.getString("TABLE_CAT");
					String tableSchem = rs.getString("TABLE_SCHEM");
					String tableName = rs.getString("TABLE_NAME");
						
					Entity entity = new Entity();
					entity.setTableName(tableName);
					entity.setBaseName(getBaseNameConverter().convert(tableName));
					entity.setCreate(false);
					entity.setAllFieldSelection(true);

					entity.setFieldList(getFieldList(databaseMetadata, tableCat, tableSchem, entity));
					
					entityList.add(entity);
					
					if (monitor.isCanceled()) {
						break;
					}
					
					monitor.subTask(tableName);
					monitor.worked(1);
				}
			} catch (Exception e1) {
				throw e1;
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (Exception e) {
					}
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
		}

		return entityList;
	}
	
	public int getEntityListCount() throws Exception {
		int count = 0;

		IConnectionProfile profile = getProfile();
		
		if (profile == null) {
			throw new RuntimeException("Can't find profile by name: " + connectionInformation.getName());
		}
		
		Connection connection = null;

		DatabaseMetaData databaseMetadata = null;

		try {
			connection = getRawConnection(profile);
			
			databaseMetadata = connection.getMetaData();
				
			String catalog = connection.getCatalog();
			String schema = connectionInformation.getSchema();
				
			ResultSet rs = null;
				
			if (schema != null && schema.length() == 0) {
				schema = null;
			}

			rs = databaseMetadata.getTables(catalog, schema, "%", new String[] { "TABLE", "VIEW" });
			
			try {
				while (rs.next()) {
					count++;
				}
			} catch (Exception e1) {
				throw e1;
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (Exception e) {
					}
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
		}

		return count;
	}
	
	private List<Field> getFieldList(DatabaseMetaData databaseMetaData, String catalog, String schema, Entity entity) throws Exception {
		List<Field> fieldList = new ArrayList<Field>();
		
		ResultSet rs = null;
		ResultSet pkRs = null;

		try {
			rs = databaseMetaData.getColumns(catalog, schema, entity.getTableName(), "%");

			while (rs.next()) {
				String columnName = rs.getString("COLUMN_NAME");
				String typeName = rs.getString("TYPE_NAME");
				int dataType = rs.getInt("DATA_TYPE");
				int columnSize = rs.getInt("COLUMN_SIZE");
				int decimalDegit = rs.getInt("DECIMAL_DIGITS");
				boolean nullable = (rs.getShort("NULLABLE") == DatabaseMetaData.typeNullable);
				String fieldType = ConventionUtils.getJavaType(dataType, columnSize, decimalDegit);
				boolean lob = ConventionUtils.isLobColumn(dataType);
				
				Field field = new Field(entity);
				
				field.setColumnName(columnName);
				field.setFieldName(getFieldNameConverter().convert(columnName));
				field.setColumnType(typeName);
				field.setFieldType(fieldType);
				field.setLob(lob);
				field.setColumnSize(columnSize);
				field.setNullable(nullable);
				field.setCreate(true);
				
				fieldList.add(field);
			}
			
			pkRs = databaseMetaData.getPrimaryKeys(catalog, schema, entity.getTableName());
			
			int pkPosition = 1;
			while (pkRs.next()) {
				String pkColumnName = pkRs.getString("COLUMN_NAME");
				for (Field field : fieldList) {
					if (field.getColumnName().equals(pkColumnName)) {
						field.setPkPosition(pkPosition++);
					}
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			if (pkRs != null) {
				try {
					pkRs.close();
				} catch (Exception e) {
				}
			}
		}

		return fieldList;
	}
	
	public String[] getSchemas() {
		IConnectionProfile profile = getProfile();
		
		Connection connection = null;
		
		DatabaseMetaData databaseMetadata = null;
		
		try {
			connection = getRawConnection(profile);
			
			databaseMetadata = connection.getMetaData();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
		}
		
		return getSchemas(databaseMetadata);
	}
	
	public String[] getSchemas(DatabaseMetaData databaseMetadata) {
		List<String> schemas = new ArrayList<String>();
		
		ResultSet rs = null;
		
		try {
			rs = databaseMetadata.getSchemas();
			
			while (rs.next()) {
				String schema = rs.getString("TABLE_SCHEM");
				schemas.add(schema);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
		}
		
		return schemas.toArray(new String[schemas.size()]);
	}

}
