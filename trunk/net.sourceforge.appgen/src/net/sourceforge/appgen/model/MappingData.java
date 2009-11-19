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

/**
 * Represents the mapping data.
 * 
 * @author Byeongkil Woo
 */
public class MappingData implements Serializable {

	private static final long serialVersionUID = 1L;

	private ConnectionInformation connectionInformation;

	private GenerationInformation generationInformation;

	private List<Entity> entityList;

	public MappingData() {
		this(new ConnectionInformation(), new GenerationInformation(), new ArrayList<Entity>());
	}
	
	public MappingData(ConnectionInformation connectionInformation, GenerationInformation generationInformation, List<Entity> entityList) {
		this.connectionInformation = connectionInformation;
		this.generationInformation = generationInformation;
		this.entityList = entityList;
	}
	
	public void addValueModifyListener(ValueModifyListener listener) {
		if (connectionInformation != null) {
			connectionInformation.addValueModifyListener(listener);
		}
		
		if (generationInformation != null) {
			generationInformation.addValueModifyListener(listener);
		}
		
		if (entityList != null) {
			for (Entity entity : entityList) {
				entity.addValueModifyListener(listener);
				
				List<Field> fieldList = entity.getFieldList();
				if (fieldList != null) {
					for (Field field : fieldList) {
						field.addValueModifyListener(listener);
					}
				}
			}
		}
	}

	public ConnectionInformation getConnectionInformation() {
		return connectionInformation;
	}

	public void setConnectionInformation(ConnectionInformation connectionInformation) {
		this.connectionInformation = connectionInformation;
	}

	public GenerationInformation getGenerationInformation() {
		return generationInformation;
	}

	public void setGenerationInformation(GenerationInformation generationInformation) {
		this.generationInformation = generationInformation;
	}

	public List<Entity> getEntityList() {
		return entityList;
	}

	public void setEntityList(List<Entity> entityList) {
		this.entityList = entityList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((connectionInformation == null) ? 0 : connectionInformation.hashCode());
		result = prime * result + ((entityList == null) ? 0 : entityList.hashCode());
		result = prime * result + ((generationInformation == null) ? 0 : generationInformation.hashCode());
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
		MappingData other = (MappingData) obj;
		if (connectionInformation == null) {
			if (other.connectionInformation != null)
				return false;
		} else if (!connectionInformation.equals(other.connectionInformation))
			return false;
		if (entityList == null) {
			if (other.entityList != null)
				return false;
		} else if (!entityList.equals(other.entityList))
			return false;
		if (generationInformation == null) {
			if (other.generationInformation != null)
				return false;
		} else if (!generationInformation.equals(other.generationInformation))
			return false;
		return true;
	}
	
}
