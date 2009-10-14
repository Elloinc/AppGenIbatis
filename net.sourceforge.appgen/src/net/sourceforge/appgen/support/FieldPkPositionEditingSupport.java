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

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;

import net.sourceforge.appgen.model.Entity;
import net.sourceforge.appgen.model.Field;

/**
 * @author Byeongkil Woo
 */
public class FieldPkPositionEditingSupport extends EditingSupport {

	private CellEditor editor;

	public FieldPkPositionEditingSupport(ColumnViewer viewer) {
		super(viewer);

		editor = new TextCellEditor(((TableViewer) viewer).getTable(), SWT.RIGHT);
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return editor;
	}

	@Override
	protected Object getValue(Object element) {
		Field field = (Field) element;
		
		return (field.getPkPosition() == 0) ? "" : String.valueOf(field.getPkPosition());
	}

	@Override
	protected void setValue(Object element, Object value) {
		Field field = (Field) element;
		
		if (value == null || String.valueOf(value).length() == 0) {
			field.setPkPosition(0);
		}

		try {
			int pkPosition = Integer.parseInt(String.valueOf(value));
			
			if (pkPosition == 0) {
			}
			if (pkPosition < 0) {
				return;
			}
			if (pkPosition > 0) {	
				Entity entity = field.getEntity();
				if (field.getEntity() != null) {
					List<Field> fieldList = entity.getFieldList();
					if (fieldList != null) {
						for (Field f : fieldList) {
							int p = f.getPkPosition();
							if (!field.equals(f) && p == pkPosition) {
								return;
							}
						}
					}
				}
			}

			field.setPkPosition(pkPosition);
		} catch (NumberFormatException e) {
		}

		getViewer().update(element, null);
	}

}
