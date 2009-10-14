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

import java.io.Serializable;

import net.sourceforge.appgen.util.FileUtils;

/**
 * @author Byeongkil Woo
 */
public class SqlMap implements Serializable {

	private static final long serialVersionUID = 1L;

	private Entity entity;
	
	public SqlMap(Entity entity) {
		this.entity = entity;
	}
	
	public String getFullPackageName() {
		return entity.getPackageName() + ".dao.ibatis.maps";
	}
	
	public String getResultMappingList() {
		return getResultMappingList("\t\t", false);
	}
	
	public String getResultMappingListExceptLob() {
		return getResultMappingList("\t\t", true);
	}
	
	public String getResultMappingList(String indent, boolean exceptLob) {
		StringBuffer buffer = new StringBuffer();
		
		for (Field field : entity.getFieldList()) {
			if (field.isCreate()) {
				if (!exceptLob || !field.isLob()) {
					if (buffer.length() > 0) {
						buffer.append(FileUtils.ln());
					}
					
					buffer.append(indent + "<result " + "column=\"" + field.getColumnName() + "\"" + " property=\"" + field.getFieldName() + "\"");
					
					if ("BLOB".equalsIgnoreCase(field.getColumnType())) {
						buffer.append(" jdbcType=\"BLOB\"");
					}
					if ("CLOB".equalsIgnoreCase(field.getColumnType())) {
						buffer.append(" jdbcType=\"CLOB\"");
					}
					
					String nullValue = field.getNullValue();
					if (nullValue != null) {
						buffer.append(" nullValue=\"" + nullValue + "\"");
					}
					
					buffer.append("/>");
				}
			}
		}
		
		return buffer.toString();
	}
	
	public String getPrimaryKeyParameterClassName() {
		if (entity.getPrimaryKeyFieldList().size() == 1) {
			String fieldType = entity.getPrimaryKeyFieldList().get(0).getFieldType();
			
			if (String.class.getName().equals(fieldType)) {
				return "string";
			}
			
			return fieldType;
		}
		
		if (entity.getPrimaryKeyFieldList().size() >= 1) {
			return "java.util.Map";
		}
		
		return "FIXME:";
	}
	
	public String getInsert() {
		return getInsert("\t\t");
	}
	
	public String getInsert(String indent) {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(indent + "INSERT INTO " + entity.getTableName() + FileUtils.ln());
		buffer.append(indent + "\t" + "(" + getAllColumnNames("", false) + ")" + FileUtils.ln());
		buffer.append(indent + "VALUES" + FileUtils.ln());
		buffer.append(indent + "\t" + "(" + getAllColumnVariables() + ")");
		
		return buffer.toString();
	}
	
	public String getUpdate() {
		return getUpdate("\t\t");
	}
	
	public String getUpdate(String indent) {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(indent + "UPDATE" + FileUtils.ln());
		buffer.append(indent + "\t" + entity.getTableName() + FileUtils.ln());
		buffer.append(indent + "SET" + FileUtils.ln());
		buffer.append(getAllColumnNamesAndVariablesExceptPrimaryKey(indent + "\t") + FileUtils.ln());
		buffer.append(indent + "WHERE" + FileUtils.ln());
		buffer.append(getPrimaryKeyConjuction(indent + "\t"));
		
		return buffer.toString();		
	}
	
	public String getDelete() {
		return getDelete("\t\t");
	}
	
	public String getDelete(String indent) {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(indent + "DELETE FROM" + FileUtils.ln());
		buffer.append(indent + "\t" + entity.getTableName() + FileUtils.ln());
		buffer.append(indent + "WHERE" + FileUtils.ln());
		buffer.append(getPrimaryKeyConjuction(indent + "\t"));
		
		return buffer.toString();		
	}
	
	public String getSelect() {
		return getSelect("\t\t");
	}
	
	public String getSelect(String indent) {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(indent + "SELECT" + FileUtils.ln());
		buffer.append(getAllColumnNames(indent + "\t") + FileUtils.ln());
		buffer.append(indent + "FROM" + FileUtils.ln());
		buffer.append(indent + "\t" + entity.getTableName() + FileUtils.ln());
		buffer.append(indent + "WHERE" + FileUtils.ln());
		
		buffer.append(getPrimaryKeyConjuction(indent + "\t"));
		
		return buffer.toString();
	}
	
	public String getSelectList() {
		return getSelectList("\t\t");
	}
	
	public String getSelectList(String indent) {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(indent + "SELECT" + FileUtils.ln());
		buffer.append(getAllColumnNamesExceptLOB(indent + "\t") + FileUtils.ln());
		buffer.append(indent + "FROM" + FileUtils.ln());
		buffer.append(indent + "\t" + entity.getTableName());
		// buffer.append(indent + "\t" + entity.getTableName() + FileUtils.ln());
		// buffer.append(indent + "ORDER BY" + FileUtils.ln());
		// buffer.append(getOrderByClause(indent + "\t"));
		
		return buffer.toString();
	}
	
	public String getSelectListOrderByClause() {
		return getSelectListOrderByClause("\t\t");
	}
	
	public String getSelectListOrderByClause(String indent) {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(indent + "ORDER BY" + FileUtils.ln());
		buffer.append(getOrderByClause(indent + "\t"));
		
		return buffer.toString();
	}
	
	public String getSelectCount() {
		return getSelectCount("\t\t");
	}
	
	public String getSelectCount(String indent) {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(indent + "SELECT" + FileUtils.ln());
		buffer.append(indent + "\t" + "COUNT(*)" + FileUtils.ln());
		buffer.append(indent + "FROM" + FileUtils.ln());
		buffer.append(indent + "\t" + entity.getTableName());
		
		return buffer.toString();
	}
	
	private String getAllColumnNamesExceptLOB(String indent) {
		StringBuffer buffer = new StringBuffer();
		
		for (Field field : entity.getFieldListExceptLob()) {
			if (buffer.length() > 0) {
				buffer.append("," + FileUtils.ln());
			}
				
			buffer.append(indent + field.getColumnName());
		}
		
		return buffer.toString();
	}
	
	private String getAllColumnNames(String indent) {
		return getAllColumnNames(indent, true);
	}
	
	private String getAllColumnNames(String indent, boolean ln) {
		StringBuffer buffer = new StringBuffer();
		
		for (Field field : entity.getFieldList()) {
			if (field.isCreate()) {
				if (buffer.length() > 0) {
					buffer.append(",");
					if (ln) {
						buffer.append(FileUtils.ln());
					} else {
						buffer.append(" ");
					}
				}
				
				buffer.append(indent + field.getColumnName());
			}
		}
		
		return buffer.toString();
	}

	private String getAllColumnVariables() {
		StringBuffer buffer = new StringBuffer();
		
		for (Field field : entity.getFieldList()) {
			if (field.isCreate()) {
				if (buffer.length() > 0) {
					buffer.append(", ");
				}
				
				buffer.append("#" + field.getFieldName() + "#");
			}
		}
		
		return buffer.toString();
	}
	
	private String getAllColumnNamesAndVariablesExceptPrimaryKey(String indent) {
		StringBuffer buffer = new StringBuffer();
		
		for (Field field : entity.getFieldList()) {
			if (field.isCreate() && field.getPkPosition() == 0) {
				if (buffer.length() > 0) {
					buffer.append("," + FileUtils.ln());
				}
				
				buffer.append(indent + field.getColumnName() + " = " + "#" + field.getFieldName() + "#");
			}
		}
		
		return buffer.toString();
	}
	
	private String getPrimaryKeyConjuction(String indent) {
		StringBuffer buffer = new StringBuffer();
		
		boolean first = true;
		for (Field field : entity.getPrimaryKeyFieldList()) {
			if (!first) {
				buffer.append(FileUtils.ln());
			}
			buffer.append(indent);
			if (!first) {
				buffer.append("AND" + " ");
			}
			buffer.append(field.getColumnName() + " = " + "#" + field.getFieldName() + "#");
			first = false;
		}
		
		return buffer.toString();
	}
	
	private String getOrderByClause(String indent) {
		StringBuffer buffer = new StringBuffer();
		
		for (Field field : entity.getPrimaryKeyFieldList()) {
			if (buffer.length() > 0) {
				buffer.append("," + FileUtils.ln());
			}
			
			buffer.append(indent + field.getColumnName() + " ASC");
		}
		
		return buffer.toString();
	}
	
	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

}
