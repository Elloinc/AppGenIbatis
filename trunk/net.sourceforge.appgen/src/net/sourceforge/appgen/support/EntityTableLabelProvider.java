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

package net.sourceforge.appgen.support;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import net.sourceforge.appgen.editor.MappingDataEditor;
import net.sourceforge.appgen.model.Entity;

/**
 * @author Byeongkil Woo
 */
public class EntityTableLabelProvider extends LabelProvider implements ITableLabelProvider {
	
	private static ImageRegistry imageRegistry = new ImageRegistry();
	
	public static final String CHECKED_IMAGE = "checked";
	public static final String UNCHECKED_IMAGE  = "unchecked";
	
	static {
		String iconPath = "icon/";
		imageRegistry.put(CHECKED_IMAGE, ImageDescriptor.createFromFile(MappingDataEditor.class, iconPath + CHECKED_IMAGE + ".gif"));
		imageRegistry.put(UNCHECKED_IMAGE, ImageDescriptor.createFromFile(MappingDataEditor.class, iconPath + UNCHECKED_IMAGE + ".gif"));
	}
	
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			Entity entity = (Entity) element;
			
			return entity.isCreate() ? imageRegistry.get(CHECKED_IMAGE) : imageRegistry.get(UNCHECKED_IMAGE);
		}
		
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		Entity entity = (Entity) element;
		
		switch (columnIndex) {
		case 0:
			return "";
		case 1:
			return entity.getTableName();
		case 2:
			return entity.getBaseName();
		default:
			throw new RuntimeException("column index out of range. " + columnIndex);
		}
	}

}
