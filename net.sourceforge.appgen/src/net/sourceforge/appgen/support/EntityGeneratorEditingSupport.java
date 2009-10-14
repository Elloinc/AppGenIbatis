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

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import net.sourceforge.appgen.model.Entity;

/**
 * @author Byeongkil Woo
 */
public class EntityGeneratorEditingSupport extends EditingSupport {

	private CellEditor editor;
	
	public EntityGeneratorEditingSupport(ColumnViewer viewer) {
		super(viewer);
		
		editor = new CheckboxCellEditor(((TableViewer) viewer).getTable());
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
		Entity entity = (Entity) element;
		
		return entity.isCreate();
	}

	@Override
	protected void setValue(Object element, Object value) {
		Entity entity = (Entity) element;
		
		entity.setCreate((Boolean) value);
		
		getViewer().update(element, null);
	}

}
