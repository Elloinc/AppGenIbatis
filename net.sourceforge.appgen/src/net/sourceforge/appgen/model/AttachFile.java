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

/**
 * @author Byeongkil Woo
 */
public class AttachFile implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String packageName;
	
	public AttachFile(String packageName) {
		this.packageName = packageName;
	}

	public String getClassName() {
		return "AttachFile";
	}
	
	public String getFullPackageName() {
		return packageName + ".base";
	}
	
}
