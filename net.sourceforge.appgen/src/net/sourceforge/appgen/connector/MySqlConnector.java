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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.appgen.model.ConnectionInformation;
import net.sourceforge.appgen.model.Entity;
import net.sourceforge.appgen.model.Field;

/**
 * @author Byeongkil Woo
 */
public class MySqlConnector extends JdbcConnector {
	
	public MySqlConnector(ConnectionInformation connectionInformation) {
		super(connectionInformation);
	}

	@Override
	public List<Entity> getEntityList() throws Exception {
		List<Entity> entityList = new ArrayList<Entity>();

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			connection = getConnection();

			ps = connection.prepareStatement("SHOW TABLES");

			rs = ps.executeQuery();

			while (rs.next()) {
				Entity entity = new Entity();
				String tableName = rs.getString(1);
				entity.setTableName(tableName);
				entity.setBaseName(getBaseNameConverter().convert(tableName));
				entity.setCreate(false);
				entity.setAllFieldSelection(true);

				entity.setFieldList(getFieldList(connection, entity));
				setPkPosition(connection, entity);

				entityList.add(entity);
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
			if (ps != null) {
				try {
					ps.close();
				} catch (Exception e) {
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception e) {
				}
			}
		}

		return entityList;
	}

	private List<Field> getFieldList(Connection connection, Entity entity) throws Exception {
		List<Field> fieldList = new ArrayList<Field>();

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = connection.prepareStatement("SHOW COLUMNS FROM " + entity.getTableName());
			
			rs = ps.executeQuery();

			while (rs.next()) {
				Field field = new Field(entity);
				String columnName = rs.getString("Field");
				field.setColumnName(columnName);
				field.setFieldName(getFieldNameConverter().convert(columnName));
				String type = rs.getString("Type");
				String dataType = type;
				int columnLength = 0;
				if (type != null) {
					int lastIndexOfOpen = type.lastIndexOf('(');
					int lastIndexOfClose = type.lastIndexOf(')');
					
					if (lastIndexOfOpen > 0 && lastIndexOfClose == type.length() - 1 && lastIndexOfOpen + 1 < lastIndexOfClose) {
						dataType = type.substring(0, lastIndexOfOpen);
						
						try {
							columnLength = Integer.parseInt(type.substring(lastIndexOfOpen + 1, lastIndexOfClose));
						} catch (Exception e) {
						}
					}
				}
				
				field.setColumnType(dataType);
				field.setFieldType(getConventionFieldType(dataType));
				field.setColumnLength(columnLength);
				
				// field.setPkPosition(rs.getInt("PK_POSITION"));
				field.setNullable("YES".equals(rs.getString("Null")));
				field.setLob(isLob(dataType));
				field.setCreate(true);

				fieldList.add(field);
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
			if (ps != null) {
				try {
					ps.close();
				} catch (Exception e) {
				}
			}
		}

		return fieldList;
	}
	
	private void setPkPosition(Connection connection, Entity entity) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = connection.prepareStatement("SHOW INDEX FROM " + entity.getTableName());
			
			rs = ps.executeQuery();

			while (rs.next()) {
				String keyName = rs.getString("Key_name");
				String columnName = rs.getString("Column_name");
				int seq = rs.getInt("Seq_in_index");
				if ("PRIMARY".equals(keyName)) {
					for (Field field : entity.getFieldList()) {
						if (field.getColumnName() != null && field.getColumnName().equals(columnName)) {
							field.setPkPosition(seq);
						}
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
			if (ps != null) {
				try {
					ps.close();
				} catch (Exception e) {
				}
			}
		}
	}

	private boolean isLob(String dataType) {
		return "TEXT".equalsIgnoreCase(dataType);
	}
	
	private String getConventionFieldType(String dataType) {
		if (dataType != null && dataType.startsWith("int")) {
			return "int";
		}
		if ("datetime".equalsIgnoreCase(dataType)) {
			return "java.util.Date";
		}
		if ("timestamp".equalsIgnoreCase(dataType)) {
			return "java.util.Date";
		}

		return String.class.getName();
	}

}
