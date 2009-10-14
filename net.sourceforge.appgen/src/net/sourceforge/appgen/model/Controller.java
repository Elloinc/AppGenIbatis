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

import net.sourceforge.appgen.converter.FirstCharacterUpperCaseConverter;
import net.sourceforge.appgen.converter.StringConverter;
import net.sourceforge.appgen.util.ConventionUtils;
import net.sourceforge.appgen.util.FileUtils;

/**
 * @author Byeongkil Woo
 */
public class Controller implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private static final StringConverter firstCharacterUpperCaseConverter = new FirstCharacterUpperCaseConverter();
	
	private Entity entity;
	
	public Controller(Entity entity) {
		this.entity = entity;
	}
	
	public String getClassName() {
		return entity.getClassName() + "Controller";
	}
	
	public String getFullPackageName() {
		return entity.getPackageName() + ".web.controller";
	}
	
	public String getSuperClassName() {
		return "MultiActionController";
	}
	
	public String getSuperClassFullPackageName() {
		return "org.springframework.web.servlet.mvc.multiaction";
	}
	
	public List<String> getImportClassNameList() {
		List<String> list = new ArrayList<String>();
		
		list.add(getSuperClassFullPackageName() + "." + getSuperClassName());
		
		list.add(entity.getFullPackageName() + "." + entity.getClassName());
		
		Criteria criteria = new Criteria(entity);
		list.add(criteria.getFullPackageName() + "." + criteria.getClassName());
		
		Service service = new Service(entity);
		list.add(service.getInterfaceFullPackageName() + "." + service.getInterfaceName());
		
		list.add("java.util.List");
		
		list.add("javax.servlet.http.HttpServletRequest");
		list.add("javax.servlet.http.HttpServletResponse");
		list.add("org.springframework.web.servlet.ModelAndView");
		list.add("org.springframework.web.bind.ServletRequestUtils");
		
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

	public List<String> getImportDeclarations() {
		List<String> list = new ArrayList<String>();
		list.addAll(getImportClassNameList());
		
		return ConventionUtils.getImportDeclarations(list, getFullPackageName());
	}
	
	public String getParseRequestParameterSnippet() {
		return getParseRequestParameterSnippet("\t\t");
	}
	
	public String getParseRequestParameterSnippet(String indent) {
		StringBuffer buffer = new StringBuffer();
		
		for (Field field : entity.getPrimaryKeyFieldList()) {
			if (field.getFieldType().equals("char")) {
				buffer.append(indent + ConventionUtils.FIXME + FileUtils.ln());
				buffer.append(indent + field.getFieldType() + " " + field.getFieldName() + " = " + "'';" + FileUtils.ln());
			} else if (field.isPrimitiveType() || String.class.getName().equals(field.getFieldType())) {
				buffer.append(indent + field.getSimpleFieldType() + " " + field.getFieldName() + " = " + "ServletRequestUtils.getRequired" + firstCharacterUpperCaseConverter.convert(field.getSimpleFieldType()) + "Parameter(request, \"" + field.getFieldName() + "\");" + FileUtils.ln());
			} else {
				buffer.append(indent + ConventionUtils.FIXME + FileUtils.ln());
				buffer.append(indent + field.getSimpleFieldType() + " " + field.getFieldName() + " = " + "null;" + FileUtils.ln());
			}
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
