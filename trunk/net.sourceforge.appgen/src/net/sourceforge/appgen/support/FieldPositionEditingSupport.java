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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import net.sourceforge.appgen.model.Entity;
import net.sourceforge.appgen.model.Field;

/**
 * @author Byeongkil Woo
 */
public class FieldPositionEditingSupport extends EditingSupport {

	private CellEditor editor;
	
	private boolean down;
	
	public FieldPositionEditingSupport(ColumnViewer viewer, boolean down) {
		super(viewer);
		
		this.down = down;
		
		editor = new CheckboxCellEditor(((TableViewer) viewer).getTable());
	}

	@Override
	protected boolean canEdit(Object element) {
		Field field = (Field) element;
		Entity entity = field.getEntity();
		
		int position0 = -1;
		int position1 = -1;
		
		List<Field> fieldList = entity.getFieldList();
		for (int i = 0; i < fieldList.size(); i++) {
			Field f = fieldList.get(i);
			
			if (f == field) {
				position0 = i;
				
				if (down) {
					position1 = i + 1;
				} else {
					position1 = i - 1;
				}
			}
		}
		
		return (position0 >= 0 && position0 < fieldList.size() && position1 >= 0 && position1 < fieldList.size());
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return editor;
	}

	@Override
	protected Object getValue(Object element) {
		return true;
	}

	@Override
	protected void setValue(Object element, Object value) {
		Field field = (Field) element;
		Entity entity = field.getEntity();
		
		int position0 = -1;
		int position1 = -1;
		
		List<Field> fieldList = entity.getFieldList();
		for (int i = 0; i < fieldList.size(); i++) {
			Field f = fieldList.get(i);
			
			if (f == field) {
				position0 = i;
				
				if (down) {
					position1 = i + 1;
				} else {
					position1 = i - 1;
				}
			}
		}
		
		if (position0 >= 0 && position0 < fieldList.size() && position1 >= 0 && position1 < fieldList.size()) {
			List<Field> tempFieldList = new ArrayList<Field>();
			tempFieldList.addAll(fieldList);
			
			List<Field> list = new ArrayList<Field>();
			for (int i = 0; i < tempFieldList.size(); i++) {
				if (i == position0) {
					list.add(tempFieldList.get(position1));
				}
				else if (i == position1) {
					list.add(tempFieldList.get(position0));
				}
				else {
					list.add(tempFieldList.get(i));
				}
			}
			
			entity.setFieldList(list);
			getViewer().setInput(entity.getFieldList());
			
			((TableViewer) getViewer()).getTable().setSelection(position1);
		}
	}

}
