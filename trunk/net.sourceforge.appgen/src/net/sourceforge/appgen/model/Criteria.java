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

/**
 * @author Byeongkil Woo
 */
public class Criteria implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static final StringConverter firstCharacterLowerCaseConverter = new FirstCharacterLowerCaseConverter();
	
	private Entity entity;
	
	public Criteria(Entity entity) {
		this.entity = entity;
	}

	public String getClassName() {
		return entity.getClassName() + "Criteria";
	}
	
	public String getInstanceName() {
		return firstCharacterLowerCaseConverter.convert(getClassName());
	}
	
	public String getFullPackageName() {
		return entity.getPackageName() + ".criteria";
	}
	
	public String getSuperClassName() {
		return "Criteria";
	}
	
	public String getSuperClassFullPackageName() {
		return entity.getPackageName() + ".base";
	}

	public List<String> getImportClassNameList() {
		List<String> list = new ArrayList<String>();

		list.add(getSuperClassFullPackageName() + "." + getSuperClassName());

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
	
	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

}
