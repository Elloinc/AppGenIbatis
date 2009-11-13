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
public class Service implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final StringConverter firstCharacterLowerCaseConverter = new FirstCharacterLowerCaseConverter();
	
	private Entity entity;
	
	public Service(Entity entity) {
		this.entity = entity;
	}
	
	public String getInterfaceName() {
		return entity.getClassName() + "Service";
	}
	
	public String getClassName() {
		return getInterfaceName() + "Impl";
	}
	
	public String getInstanceName() {
		return firstCharacterLowerCaseConverter.convert(getInterfaceName());
	}
	
	public String getInterfaceFullPackageName() {
		return entity.getPackageName() + ".service";
	}
	
	public String getClassFullPackageName() {
		return entity.getPackageName() + ".service.impl";
	}
	
	public String getSuperClassName() {
		return "Service";
	}
	
	public String getSuperClassFullPackageName() {
		return entity.getPackageName() + ".base";
	}
	
	public List<String> getImportClassNameList(boolean concrete) {
		List<String> list = new ArrayList<String>();
		
		if (concrete) {
			list.add(getSuperClassFullPackageName() + "." + getSuperClassName());
		}
		
		list.add(entity.getFullPackageName() + "." + entity.getClassName());
		
		Criteria criteria = new Criteria(entity);
		list.add(criteria.getFullPackageName() + "." + criteria.getClassName());
		
		list.add("java.util.List");
		
		if (concrete) {
			list.add(getInterfaceFullPackageName() + "." + getInterfaceName());
		}
		
		Dao dao = new Dao(entity);
		
		if (concrete) {
			list.add(dao.getInterfaceFullPackageName() + "." + dao.getInterfaceName());
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
		
		if (concrete) {
			for (Field field : entity.getFieldList()) {
				if (field.isCreate() && Field.FIELD_TYPE_ATTACH_FILE.equals(field.getFieldType())) {
					if (!list.contains(entity.getPackageName() + ".base." + Field.FIELD_TYPE_ATTACH_FILE)) {
						list.add(entity.getPackageName() + ".base." + Field.FIELD_TYPE_ATTACH_FILE);
					}
					if (!list.contains(entity.getPackageName() + ".base.AttachFilePersister")) {
						list.add(entity.getPackageName() + ".base.AttachFilePersister");
					}
					if (!list.contains("org.springframework.core.io.FileSystemResource")) {
						list.add("org.springframework.core.io.FileSystemResource");
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

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}
	
}
