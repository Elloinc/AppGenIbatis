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
import net.sourceforge.appgen.model.Field;

/**
 * @author Byeongkil Woo
 */
public class FieldTableLabelProvider extends LabelProvider implements ITableLabelProvider {
	
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
			Field field = (Field) element;
			
			return field.isCreate() ? imageRegistry.get(CHECKED_IMAGE) : imageRegistry.get(UNCHECKED_IMAGE);
		}
		
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		Field field = (Field) element;
		Entity entity = field.getEntity();
		
		switch (columnIndex) {
		case 0:
			return "";
		case 1:
			return field.getColumnName();
		case 2:
			return field.getColumnType();
		case 3:
			return String.valueOf(field.getColumnLength());
		case 4:
			return field.getPkPosition() <= 0 ? "" : String.valueOf(field.getPkPosition());
		case 5:
			return String.valueOf(field.isNullable());
		case 6:
			return String.valueOf(field.isLob());
		case 7:
			return field.getFieldName();
		case 8:
			return field.getFieldType();

		case 9:
			if (entity.getFieldList().size() > 0 && entity.getFieldList().get(0) == field) {
				return "";
			}
			return "¡ü";
		case 10:
			if (entity.getFieldList().size() > 0 && entity.getFieldList().get(entity.getFieldList().size() - 1) == field) {
				return "";
			}
			return "¡ý";
		default:
			throw new RuntimeException("column index out of range. " + columnIndex);
		}
	}

}
