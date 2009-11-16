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
public class OracleConnector extends JdbcConnector {

	private static final String FIELD_LIST_SQL = 
			"SELECT COLUMN_NAME, DATA_TYPE, DATA_LENGTH, NULLABLE, PK_POSITION, MIN(RN) RN FROM ("
			+ "SELECT COLUMN_NAME, DATA_TYPE, DATA_LENGTH, NULLABLE, ROWNUM RN, "
			+ "    (SELECT UCC.POSITION FROM USER_CONSTRAINTS UC, USER_CONS_COLUMNS UCC "
			+ "     WHERE UC.CONSTRAINT_NAME = UCC.CONSTRAINT_NAME AND UC.CONSTRAINT_TYPE = 'P' "
			+ "     AND UC.TABLE_NAME = A.TABLE_NAME "
			+ "     AND UCC.COLUMN_NAME = A.COLUMN_NAME) PK_POSITION "
			+ "FROM ALL_TAB_COLUMNS A WHERE TABLE_NAME = ?"
			+ ") GROUP BY COLUMN_NAME, DATA_TYPE, DATA_LENGTH, NULLABLE, PK_POSITION ORDER BY RN";

	public OracleConnector(ConnectionInformation connectionInformation) {
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

			ps = connection.prepareStatement("SELECT TABLE_NAME FROM TABS");

			rs = ps.executeQuery();

			while (rs.next()) {
				Entity entity = new Entity();
				String tableName = rs.getString("TABLE_NAME");
				entity.setTableName(tableName);
				entity.setBaseName(getBaseNameConverter().convert(tableName));
				entity.setCreate(false);
				entity.setAllFieldSelection(true);

				entity.setFieldList(getFieldList(connection, entity));

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
			ps = connection.prepareStatement(FIELD_LIST_SQL);
			ps.setString(1, entity.getTableName());

			rs = ps.executeQuery();

			while (rs.next()) {
				Field field = new Field(entity);
				String columnName = rs.getString("COLUMN_NAME");
				field.setColumnName(columnName);
				field.setFieldName(getFieldNameConverter().convert(columnName));
				String dataType = rs.getString("DATA_TYPE");
				field.setColumnType(dataType);
				field.setFieldType(getConventionFieldType(dataType));
				field.setColumnLength(rs.getInt("DATA_LENGTH"));
				field.setPkPosition(rs.getInt("PK_POSITION"));
				field.setNullable("Y".equals(rs.getString("NULLABLE")));
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
	
	private boolean isLob(String dataType) {
		return "LONG".equalsIgnoreCase(dataType) || "BLOB".equalsIgnoreCase(dataType) || "CLOB".equalsIgnoreCase(dataType);
	}
	
	private String getConventionFieldType(String dataType) {
		if ("NUMBER".equalsIgnoreCase(dataType)) {
			return "int";
		}
		if ("DATE".equalsIgnoreCase(dataType)) {
			return "java.util.Date";
		}
		
		return String.class.getName();
	}

}
