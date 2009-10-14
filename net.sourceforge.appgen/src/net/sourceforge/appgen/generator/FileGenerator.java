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

package net.sourceforge.appgen.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.Collections;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;

import net.sourceforge.appgen.Activator;
import net.sourceforge.appgen.model.BaseCriteria;
import net.sourceforge.appgen.model.BaseService;
import net.sourceforge.appgen.model.Controller;
import net.sourceforge.appgen.model.Criteria;
import net.sourceforge.appgen.model.Dao;
import net.sourceforge.appgen.model.Entity;
import net.sourceforge.appgen.model.FormController;
import net.sourceforge.appgen.model.GenerationInformation;
import net.sourceforge.appgen.model.Jstl;
import net.sourceforge.appgen.model.Paging;
import net.sourceforge.appgen.model.Service;
import net.sourceforge.appgen.model.SqlMap;
import net.sourceforge.appgen.model.Validator;
import net.sourceforge.appgen.model.WebXml;

/**
 * @author Byeongkil Woo
 */
public abstract class FileGenerator {
	
	protected Properties properties;
	
	protected File templateDir;
	
	protected File outputDir;
	
	protected String packageName;
	
	public FileGenerator(GenerationInformation generationInformation) {
		this.properties = new Properties();
		this.templateDir = generationInformation.getTemplateDir();
		this.outputDir = generationInformation.getOutputDir();
		this.packageName = generationInformation.getPackageName();
		
		if (!generationInformation.isSpecifyTemplateDir()) {
			Bundle bundle = Activator.getDefault().getBundle();
			Path path = new Path("template");
			URL url = FileLocator.find(bundle, path, Collections.EMPTY_MAP);
			URL fileUrl = null;
			
			try {
				fileUrl = FileLocator.toFileURL(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			templateDir = new File(fileUrl.getPath());
		}
		
		properties.setProperty("file.resource.loader.path", templateDir.getPath());
	}
	
	public boolean existFile(Entity entity) {
		return getFile(entity).exists();
	}

	public void generateDirectory() {
		if (!getDirectory().exists()) {
			getDirectory().mkdirs();
		}
	}
	
	public File generate(Entity entity) throws Exception {
		return generateFile(entity, getTemplate(), getFile(entity));
	}
	
	public abstract File getFile(Entity entity);
	
	public abstract File getDirectory();
	
	public abstract String getTemplate();
	
	public File generateFile(Entity entity, String template, File file) throws Exception {
		Velocity.init(properties);
		
		Template modelTemplate = Velocity.getTemplate(template);
		
		VelocityContext context = new VelocityContext();
		context.put("baseService", new BaseService(packageName));
		context.put("baseCriteria", new BaseCriteria(packageName));
		context.put("paging", new Paging(packageName));
		context.put("entity", entity);
		context.put("criteria", new Criteria(entity));
		context.put("validator", new Validator(entity));
		context.put("dao", new Dao(entity));
		context.put("sqlMap", new SqlMap(entity));
		context.put("service", new Service(entity));
		context.put("controller", new Controller(entity));
		context.put("formController", new FormController(entity));
		
		context.put("webXml", new WebXml(entity));
		context.put("jstl", new Jstl(entity));
		
		Writer writer = null;
		
		try {
			writer = new FileWriter(file);
			
			modelTemplate.merge(context, writer);
		} catch (Exception e) {
			throw e;
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {
				}
			}
		}
		
		return file;
	}
	
}
