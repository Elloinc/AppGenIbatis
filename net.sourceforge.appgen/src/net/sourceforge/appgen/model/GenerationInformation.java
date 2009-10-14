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

import java.io.File;
import java.io.Serializable;

/**
 * @author Byeongkil Woo
 */
public class GenerationInformation extends ValueModifyModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean specifyTemplateDir;

	private File templateDir;

	private File outputDir;

	private String packageName;

	private String superClassName;
	
	public GenerationInformation() {
		super();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(super.toString() + "(");
		builder.append("specifyTemplateDir='" + specifyTemplateDir + "'");
		builder.append(",");
		builder.append("templateDir='" + templateDir + "'");
		builder.append(",");
		builder.append("outputDir='" + outputDir + "'");
		builder.append(",");
		builder.append("packageName='" + packageName + "'");
		builder.append(",");
		builder.append("superClassName='" + superClassName + "'");
		builder.append(")");

		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((outputDir == null) ? 0 : outputDir.hashCode());
		result = prime * result + ((packageName == null) ? 0 : packageName.hashCode());
		result = prime * result + (specifyTemplateDir ? 1231 : 1237);
		result = prime * result + ((superClassName == null) ? 0 : superClassName.hashCode());
		result = prime * result + ((templateDir == null) ? 0 : templateDir.hashCode());
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
		GenerationInformation other = (GenerationInformation) obj;
		if (outputDir == null) {
			if (other.outputDir != null)
				return false;
		} else if (!outputDir.equals(other.outputDir))
			return false;
		if (packageName == null) {
			if (other.packageName != null)
				return false;
		} else if (!packageName.equals(other.packageName))
			return false;
		if (specifyTemplateDir != other.specifyTemplateDir)
			return false;
		if (superClassName == null) {
			if (other.superClassName != null)
				return false;
		} else if (!superClassName.equals(other.superClassName))
			return false;
		if (templateDir == null) {
			if (other.templateDir != null)
				return false;
		} else if (!templateDir.equals(other.templateDir))
			return false;
		return true;
	}

	public boolean isSpecifyTemplateDir() {
		return specifyTemplateDir;
	}

	public void setSpecifyTemplateDir(boolean specifyTemplateDir) {
		this.specifyTemplateDir = specifyTemplateDir;
		
		valueModified();
	}

	public File getTemplateDir() {
		if (!specifyTemplateDir) {
			return null;
		}
		
		return templateDir;
	}

	public void setTemplateDir(File templateDir) {
		this.templateDir = templateDir;
		
		valueModified();
	}

	public File getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(File outputDir) {
		this.outputDir = outputDir;
		
		valueModified();
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
		
		valueModified();
	}

	public String getSuperClassName() {
		return superClassName;
	}

	public void setSuperClassName(String superClassName) {
		this.superClassName = superClassName;
		
		valueModified();
	}

}
