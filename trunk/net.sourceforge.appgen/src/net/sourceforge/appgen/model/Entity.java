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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.appgen.converter.FirstCharacterLowerCaseConverter;
import net.sourceforge.appgen.converter.FirstCharacterUpperCaseConverter;
import net.sourceforge.appgen.converter.StringConverter;
import net.sourceforge.appgen.util.ConventionUtils;
import net.sourceforge.appgen.util.FileUtils;

/**
 * Represents the Java class(entity, domain, dto, vo) and the database table.
 * 
 * @author Byeongkil Woo
 */
public class Entity extends ValueModifyModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final StringConverter firstCharacterUpperCaseConverter = new FirstCharacterUpperCaseConverter();

	private static final StringConverter firstCharacterLowerCaseConverter = new FirstCharacterLowerCaseConverter();

	private static final Comparator<Field> primaryKeyFieldComparator = new PrimaryKeyFieldComparator();

	private String tableName;

	private String baseName;

	private String packageName;

	private boolean create;

	private boolean allFieldSelection;

	private List<Field> fieldList;

	public Entity() {
		super();
		
		fieldList = new ArrayList<Field>();
	}

	public List<Field> getToStringFieldList() {
		List<Field> list = new ArrayList<Field>();

		for (Field field : fieldList) {
			if (field.isCreate() && (field.getPkPosition() > 0 || Field.FIELD_TYPE_ATTACH_FILE.equals(field.getFieldType()))) {
				list.add(field);
			}
		}

		Collections.sort(list, primaryKeyFieldComparator);

		return list;
	}
	
	public List<Field> getPrimaryKeyFieldList() {
		List<Field> list = new ArrayList<Field>();

		for (Field field : fieldList) {
			if (field.isCreate() && field.getPkPosition() > 0) {
				list.add(field);
			}
		}

		Collections.sort(list, primaryKeyFieldComparator);

		return list;
	}
	
	public boolean hasAttachFileField() {
		List<Field> list = getAttachFileFieldList();
		
		return list.size() > 0;
	}
	
	public List<Field> getAttachFileFieldList() {
		List<Field> list = new ArrayList<Field>();

		for (Field field : fieldList) {
			if (field.isCreate() && Field.FIELD_TYPE_ATTACH_FILE.equals(field.getFieldType())) {
				list.add(field);
			}
		}

		return list;
	}

	public List<Field> getFieldListExceptLob() {
		List<Field> list = new ArrayList<Field>();

		for (Field field : fieldList) {
			if (field.isCreate() && !field.isLob()) {
				list.add(field);
			}
		}

		return list;
	}
	
	public String getToStringStatement() {
		return getToStringStatement("\t\t");
	}
	
	public String getToStringStatement(String indent) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(indent + "return getClass().getName() + \"@\" + Integer.toHexString(hashCode()) + " + FileUtils.ln());
		buffer.append(indent + "\t" + "\"(\" + " + FileUtils.ln());
		boolean first = true;
		for (Field field : getPrimaryKeyFieldList()) {
			if (!first) {
				buffer.append("\", \" + " + FileUtils.ln());
			}
			buffer.append(indent + "\t" + "\"" + field.getFieldName() + "=\" + \"'\" + " + field.getFieldName() + " + \"'\" + ");
			first = false;
		}
		buffer.append(FileUtils.ln());
		buffer.append(indent + "\t" + "\")\";");
		
		return buffer.toString();
	}

	public String getInstanceName() {
		return firstCharacterLowerCaseConverter.convert(baseName);
	}

	public String getClassName() {
		return firstCharacterUpperCaseConverter.convert(baseName);
	}

	public String getFullPackageName() {
		return packageName + ".domain";
	}

	public List<String> getImportClassNameList() {
		List<String> list = new ArrayList<String>();

		list.add("java.io.Serializable");

		if (fieldList != null) {
			for (Field field : fieldList) {
				if (field.isCreate() && field.getFieldType().indexOf('.') > 0) {
					if (!field.getFieldType().equals("java.lang." + field.getSimpleFieldType())) {
						if (!list.contains(field.getFieldType())) {
							list.add(field.getFieldType());
						}
					}
				}
				if (field.isCreate() && Field.FIELD_TYPE_ATTACH_FILE.equals(field.getFieldType())) {
					if (!list.contains(getPackageName() + ".base." + Field.FIELD_TYPE_ATTACH_FILE)) {
						list.add(getPackageName() + ".base." + Field.FIELD_TYPE_ATTACH_FILE);
					}
				}
			}
		}

		return list;
	}

	public List<String> getImportDeclarations() {
		List<String> list = new ArrayList<String>();
		list.addAll(getImportClassNameList());

		return ConventionUtils.getImportDeclarations(list, packageName);
	}

	public boolean hasLob() {
		for (Field field : fieldList) {
			if (field.isCreate() && field.isLob()) {
				return true;
			}
		}

		return false;
	}

	public boolean isValidTableName() {
		String tableName = getTableName();
		
		if (tableName != null & tableName.length() == 0) {
			return false;
		}
		
		if (tableName.indexOf(" ") >= 0) {
			return false;
		}
		
		return true;
	}
	
	public boolean isValidBaseName() {
		if (getBaseName() == null) {
			return false;
		}

		Pattern p = Pattern.compile("^([a-z]+([a-z0-9_]+[a-zA-Z0-9_]*)?)$");
		Matcher m = p.matcher(getBaseName());

		if (!m.matches()) {
			return false;
		}

		if (ConventionUtils.isReservedWord(getBaseName())) {
			return false;
		}

		return m.matches();
	}

	public boolean hasDuplicateFieldName() {
		for (Field f0 : fieldList) {
			for (Field f1 : fieldList) {
				if ((f0 != f1) && (f0.isCreate() && f1.isCreate()) && f0.getFieldName().equals(f1.getFieldName())) {
					return true;
				}
			}
		}

		return false;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (allFieldSelection ? 1231 : 1237);
		result = prime * result + ((baseName == null) ? 0 : baseName.hashCode());
		result = prime * result + (create ? 1231 : 1237);
		result = prime * result + ((fieldList == null) ? 0 : fieldList.hashCode());
		result = prime * result + ((packageName == null) ? 0 : packageName.hashCode());
		result = prime * result + ((tableName == null) ? 0 : tableName.hashCode());
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
		Entity other = (Entity) obj;
		if (allFieldSelection != other.allFieldSelection)
			return false;
		if (baseName == null) {
			if (other.baseName != null)
				return false;
		} else if (!baseName.equals(other.baseName))
			return false;
		if (create != other.create)
			return false;
		if (fieldList == null) {
			if (other.fieldList != null)
				return false;
		} else if (!fieldList.equals(other.fieldList))
			return false;
		if (packageName == null) {
			if (other.packageName != null)
				return false;
		} else if (!packageName.equals(other.packageName))
			return false;
		if (tableName == null) {
			if (other.tableName != null)
				return false;
		} else if (!tableName.equals(other.tableName))
			return false;
		return true;
	}
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
		
		valueModified();
	}

	public String getBaseName() {
		return baseName;
	}

	public void setBaseName(String baseName) {
		this.baseName = baseName;
		
		valueModified();
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
		
		// valueModified();
	}

	public boolean isCreate() {
		return create;
	}

	public void setCreate(boolean create) {
		this.create = create;
		
		valueModified();
	}

	public boolean isAllFieldSelection() {
		return allFieldSelection;
	}

	public void setAllFieldSelection(boolean allFieldSelection) {
		this.allFieldSelection = allFieldSelection;
		
		valueModified();
	}

	public List<Field> getFieldList() {
		return fieldList;
	}

	public void setFieldList(List<Field> fieldList) {
		this.fieldList = fieldList;
		
		valueModified();
	}

}
