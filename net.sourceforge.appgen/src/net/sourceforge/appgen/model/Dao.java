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
import java.util.List;

import net.sourceforge.appgen.converter.FirstCharacterLowerCaseConverter;
import net.sourceforge.appgen.converter.StringConverter;
import net.sourceforge.appgen.util.ConventionUtils;
import net.sourceforge.appgen.util.FileUtils;

/**
 * @author Byeongkil Woo
 */
public class Dao implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static final StringConverter firstCharacterLowerCaseConverter = new FirstCharacterLowerCaseConverter();
	
	private Entity entity;
	
	public Dao(Entity entity) {
		this.entity = entity;
	}
	
	public String getInterfaceName() {
		return entity.getClassName() + "Dao";
	}
	
	public String getClassName() {
		return getInterfaceName() + "Impl";
	}
	
	public String getInstanceName() {
		return firstCharacterLowerCaseConverter.convert(getInterfaceName());
	}
	
	public String getInterfaceFullPackageName() {
		return entity.getPackageName() + ".dao";
	}
	
	public String getClassFullPackageName() {
		return entity.getPackageName() + ".dao.ibatis";
	}
	
	public List<String> getImportClassNameList(boolean concrete) {
		List<String> list = new ArrayList<String>();
		
		list.add(entity.getFullPackageName() + "." + entity.getClassName());
		
		Criteria criteria = new Criteria(entity);
		list.add(criteria.getFullPackageName() + "." + criteria.getClassName());
		
		list.add("java.util.List");
		
		if (concrete) {
			if ("Map".equals(getPrimaryKeyClassName())) {
				list.add("java.util.Map");
				list.add("java.util.HashMap");
			}
		}
		
		list.add("org.springframework.dao.DataAccessException");
		
		if (concrete) {
			list.add(getInterfaceFullPackageName() + "." + getInterfaceName());
			list.add("org.springframework.util.Assert");
			list.add("org.springframework.orm.ibatis.support.SqlMapClientDaoSupport");
			list.add(entity.getPackageName() + ".util.Paging");
			list.add(getInterfaceFullPackageName() + "." + getInterfaceName());
		}
		
		if (entity.getPrimaryKeyFieldList() != null) {
			for (Field field : entity.getPrimaryKeyFieldList()) {
				if (field.isCreate() && field.getFieldType().indexOf('.') > 0) {
					if (!field.getFieldType().equals("java.lang." + field.getSimpleFieldType())) {
						if (!list.contains(field.getFieldType())) {
							list.add(field.getFieldType());
						}
					}
				}
			}
		}
		
		return list;
	}

	public List<String> getImportDeclarations(boolean concrete) {
		List<String> list = new ArrayList<String>();
		list.addAll(getImportClassNameList(concrete));
		
		String exceptPackageName = getInterfaceFullPackageName();
		if (concrete) {
			exceptPackageName = getClassFullPackageName();
		}

		return ConventionUtils.getImportDeclarations(list, exceptPackageName);
	}
	
	public String getToPrimitiveOpen() {
		if (entity.getPrimaryKeyFieldList().size() == 1) {
			Field field = entity.getPrimaryKeyFieldList().get(0);
			
			if (field.isPrimitiveType()) {
				return "(";
			}
		}
		
		return "";
	}
	
	public String getToPrimitiveClose() {
		if (entity.getPrimaryKeyFieldList().size() == 1) {
			Field field = entity.getPrimaryKeyFieldList().get(0);
			
			if (field.isPrimitiveType()) {
				return "." + field.getToPrimitiveMethod();
			}
		}
		
		return "";
	}
	
	public String getPrimaryKeyParameters() {
		return getPrimaryKeyParameters(true);
	}
	
	public String getPrimaryKeyParameters(boolean includeType) {
		StringBuffer buffer = new StringBuffer();
		
		for (Field field : entity.getPrimaryKeyFieldList()) {
			if (buffer.length() > 0) {
				buffer.append(", ");
			}
			
			if (includeType) {
				buffer.append(field.getSimpleFieldType() + " ");
			}
			
			buffer.append(field.getFieldName());
		}
		
		return buffer.toString();
	}
	
	public String getPrimaryKeyClassName() {
		if (entity.getPrimaryKeyFieldList().size() == 1) {
			Field field = entity.getPrimaryKeyFieldList().get(0);
			
			return field.getSimpleObjectClassName();
		}
		
		if (entity.getPrimaryKeyFieldList().size() > 1) {
			return "Map";
		}
		
		return "FIXME:";
	}
	
	public String getPrimaryKeySnippet() {
		return getPrimaryKeySnippet("\t\t");
	}
	
	public String getPrimaryKeySnippet(String indent) {
		StringBuffer buffer = new StringBuffer();
		
		if ("Map".equals(getPrimaryKeyClassName())) {
			buffer.append(indent + "Map<String, Object> param = new HashMap<String, Object>(" + entity.getPrimaryKeyFieldList().size() + ");" + FileUtils.ln());
			
			for (Field field : entity.getPrimaryKeyFieldList()) {
				buffer.append(indent + "param.put(\"" + field.getFieldName() + "\", " + getInlineWrapperInstance(field) + ");" + FileUtils.ln());
			}
		}
		
		return buffer.toString();
	}
	
	public String getParameterVariableName() {
		if (entity.getPrimaryKeyFieldList().size() == 1) {
			Field field = entity.getPrimaryKeyFieldList().get(0);
			
			return getInlineWrapperInstance(field);
		}
		
		if (entity.getPrimaryKeyFieldList().size() >= 1) {
			return "param";
		}
		
		return "FIXME:";
	}
	
	public String getInlineWrapperInstance(Field field) {
		if (field.isPrimitiveType()) {
			return "new " + field.getSimpleObjectClassName() + "(" + field.getFieldName() + ")";
		}
		
		return field.getFieldName();
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}
	
}
