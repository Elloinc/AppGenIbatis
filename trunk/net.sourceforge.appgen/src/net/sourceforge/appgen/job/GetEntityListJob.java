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

package net.sourceforge.appgen.job;

import java.util.List;

import net.sourceforge.appgen.connector.ProfileConnector;
import net.sourceforge.appgen.editor.MappingDataEditor;
import net.sourceforge.appgen.model.ConnectionInformation;
import net.sourceforge.appgen.model.Entity;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

/**
 * @author Byeongkil Woo
 */
public class GetEntityListJob extends Job {

	private MappingDataEditor editor;
	
	private ConnectionInformation connectionInformation;

	public GetEntityListJob(ConnectionInformation connectionInformation, MappingDataEditor editor, String text) {
		super(text);

		this.editor = editor;
		this.connectionInformation = connectionInformation;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Parse entity from table", 100 + 3);
		
		try {
			monitor.subTask("Connect to database: " + connectionInformation.getName());

			ProfileConnector connector = new ProfileConnector(connectionInformation);

			connector.connect();

			if (monitor.isCanceled()) {
				monitor.done();
				
				return Status.CANCEL_STATUS;
			}
			
			monitor.worked(1);

			int count = connector.getEntityListCount();

			if (monitor.isCanceled()) {
				monitor.done();
				
				return Status.CANCEL_STATUS;
			}
			
			monitor.worked(1);

			SubProgressMonitor subMonitor = new SubProgressMonitor(monitor, 100);
			subMonitor.beginTask("Starting parse table...", count);
			subMonitor.setTaskName("Parse table");

			final List<Entity> entityList = connector.getEntityList(subMonitor);
			
			if (monitor.isCanceled()) {
				monitor.done();
				
				return Status.CANCEL_STATUS;
			}
			
			subMonitor.done();
			
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					editor.onEntityListChanged(entityList);
				}
			});

			monitor.worked(1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
		}
		
		monitor.done();

		return Status.OK_STATUS;
	};

}
