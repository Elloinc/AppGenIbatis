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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.appgen.converter.FirstCharacterUpperCaseConverter;
import net.sourceforge.appgen.converter.StringConverter;
import net.sourceforge.appgen.util.ConventionUtils;

/**
 * Represents the Java class's field and the database table's column.
 * 
 * @author Byeongkil Woo
 */
public class Field extends ValueModifyModel implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String FIELD_TYPE_ATTACH_FILE = "AttachFile";
	
	private static final StringConverter firstCharacterCaptitalizeConverter = new FirstCharacterUpperCaseConverter();
	
	private static final Pattern VALID_FIELD_NAME_PATTERN = Pattern.compile("^([a-z]+([a-z0-9_]+[a-zA-Z0-9_]*)?)$");
	
	private static final Pattern VALID_FIELD_TYPE_PATTERN = Pattern.compile("^((([a-z]+(a-zA-Z0-9)*)?[.]{1})+[a-zA-Z]+([a-zA-Z0-9]*)?)$");
	
	private Entity entity;
	
	private String columnName;

	private String fieldName;

	private String columnType;

	private String fieldType;

	private boolean create;

	private int columnLength;

	private int pkPosition;
	
	private boolean nullable;
	
	private boolean lob;
	
	public Field(final Entity entity) {
		super();
		
		this.entity = entity;
	}
	
	public String getFirstCapFieldName() {
		return firstCharacterCaptitalizeConverter.convert(fieldName);
	}
	
	public String getSetterMethodName() {
		return "set" + firstCharacterCaptitalizeConverter.convert(fieldName);
	}
	
	public String getGetterMethodName() {
		if ("boolean".equals(fieldType)) {
			return "is" + firstCharacterCaptitalizeConverter.convert(fieldName);
		}
		
		return "get" + firstCharacterCaptitalizeConverter.convert(fieldName);
	}
	
	public String getSimpleFieldType() {
		return ConventionUtils.getSimpleClassName(fieldType);
	}
	
	public boolean hasNullValue() {
		return getNullValue() != null;
	}
	
	public String getNullValue() {
		if ("byte".equals(fieldType) || Byte.class.getName().equals(fieldType)) {
			return "0";
		}
		if ("short".equals(fieldType) || Short.class.getName().equals(fieldType)) {
			return "0";
		}
		if ("int".equals(fieldType) || Integer.class.getName().equals(fieldType)) {
			return "0";
		}
		if ("long".equals(fieldType) || Long.class.getName().equals(fieldType)) {
			return "0";
		}
		if ("float".equals(fieldType) || Float.class.getName().equals(fieldType)) {
			return "0";
		}
		if ("double".equals(fieldType) || Double.class.getName().equals(fieldType)) {
			return "0";
		}
		if ("char".equals(fieldType) || Character.class.getName().equals(fieldType)) {
			return null;
		}
		if ("boolean".equals(fieldType) || Boolean.class.getName().equals(fieldType)) {
			return "false";
		}
		
		return null;
	}
	
	public boolean isPrimitiveType() {
		if ("byte".equals(fieldType)) {
			return true;
		}
		if ("short".equals(fieldType)) {
			return true;
		}
		if ("int".equals(fieldType)) {
			return true;
		}
		if ("long".equals(fieldType)) {
			return true;
		}
		if ("float".equals(fieldType)) {
			return true;
		}
		if ("double".equals(fieldType)) {
			return true;
		}
		if ("char".equals(fieldType)) {
			return true;
		}
		if ("boolean".equals(fieldType)) {
			return true;
		}
		
		return false;
	}
	
	public boolean isAttachFileType() {
		return FIELD_TYPE_ATTACH_FILE.equals(fieldType);
	}
	
	public String getSimpleObjectClassName() {
		if ("byte".equals(fieldType)) {
			return Byte.class.getSimpleName();
		}
		if ("short".equals(fieldType)) {
			return Short.class.getSimpleName();
		}
		if ("int".equals(fieldType)) {
			return Integer.class.getSimpleName();
		}
		if ("long".equals(fieldType)) {
			return Long.class.getSimpleName();
		}
		if ("float".equals(fieldType)) {
			return Float.class.getSimpleName();
		}
		if ("double".equals(fieldType)) {
			return Double.class.getSimpleName();
		}
		if ("char".equals(fieldType)) {
			return Character.class.getSimpleName();
		}
		if ("boolean".equals(fieldType)) {
			return Boolean.class.getSimpleName();
		}
		
		return getSimpleFieldType();
	}
	
	public String getToPrimitiveMethod() {
		if (isPrimitiveType()) {
			return getFieldType() + "Value()";
		}
		
		return null;		
	}
	
	public boolean isValidColumnName() {
		String columnName = getColumnName();
		
		if (columnName != null & columnName.length() == 0) {
			return false;
		}
		
		if (columnName.indexOf(" ") >= 0) {
			return false;
		}
		
		return true;
	}
	
	public boolean isValidColumnType() {
		String columnType = getColumnType();
		
		if (columnType != null & columnType.length() == 0) {
			return false;
		}
		
		if (columnType.indexOf(" ") >= 0) {
			return false;
		}
		
		return true;
	}
	
	public boolean isValidColumnLength() {
		return getColumnLength() >= 0;
	}
	
	public boolean isValidNullable() {
		if (getPkPosition() > 0 && isNullable()) {
			return false;
		}
		
		return true;
	}
	
	public boolean isValidFieldName() {
		if (getFieldName() == null) {
			return false;
		}
		
		Matcher m = VALID_FIELD_NAME_PATTERN.matcher(getFieldName());
		
		if (!m.matches()) {
			return false;
		}
		
		if (ConventionUtils.isReservedWord(getFieldName())) {
			return false;
		}
		
		return true;
	}
	
	public boolean isValidFieldType() {
		if (getFieldType() == null) {
			return false;
		}
		
		Matcher m = VALID_FIELD_TYPE_PATTERN.matcher(getFieldType());
		
		if (m.matches()) {
			return true;
		}
		
		if (isPrimitiveType()) {
			return true;
		}
		
		if (isAttachFileType()) {
			if (pkPosition > 0) {
				return false;
			} else {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + columnLength;
		result = prime * result + ((columnName == null) ? 0 : columnName.hashCode());
		result = prime * result + ((columnType == null) ? 0 : columnType.hashCode());
		result = prime * result + (create ? 1231 : 1237);
		result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
		result = prime * result + ((fieldType == null) ? 0 : fieldType.hashCode());
		result = prime * result + (lob ? 1231 : 1237);
		result = prime * result + (nullable ? 1231 : 1237);
		result = prime * result + pkPosition;
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
		Field other = (Field) obj;
		if (columnLength != other.columnLength)
			return false;
		if (columnName == null) {
			if (other.columnName != null)
				return false;
		} else if (!columnName.equals(other.columnName))
			return false;
		if (columnType == null) {
			if (other.columnType != null)
				return false;
		} else if (!columnType.equals(other.columnType))
			return false;
		if (create != other.create)
			return false;
		if (fieldName == null) {
			if (other.fieldName != null)
				return false;
		} else if (!fieldName.equals(other.fieldName))
			return false;
		if (fieldType == null) {
			if (other.fieldType != null)
				return false;
		} else if (!fieldType.equals(other.fieldType))
			return false;
		if (lob != other.lob)
			return false;
		if (nullable != other.nullable)
			return false;
		if (pkPosition != other.pkPosition)
			return false;
		return true;
	}

	public Entity getEntity() {
		return entity;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
		
		valueModified();
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
		
		valueModified();
	}

	public String getColumnType() {
		return columnType;
	}

	public void setColumnType(String columnType) {
		this.columnType = columnType;
		
		valueModified();
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
		
		valueModified();
	}

	public boolean isCreate() {
		return create;
	}

	public void setCreate(boolean create) {
		this.create = create;
		
		valueModified();
	}

	public int getColumnLength() {
		return columnLength;
	}

	public void setColumnLength(int columnLength) {
		this.columnLength = columnLength;
		
		valueModified();
	}

	public int getPkPosition() {
		return pkPosition;
	}

	public void setPkPosition(int pkPosition) {		
		this.pkPosition = pkPosition;
		
		valueModified();
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}
	
	public boolean isLob() {
		return lob;
	}

	public void setLob(boolean lob) {
		this.lob = lob;
		
		valueModified();
	}

}
