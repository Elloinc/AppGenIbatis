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
import java.io.Writer;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import net.sourceforge.appgen.model.Entity;
import net.sourceforge.appgen.model.GenerationInformation;

/**
 * @author Byeongkil Woo
 */
public class ResourcePropertiesFileGenerator extends OnceFileGenerator {

	public static final String TEMPLATE = "resourceProperties.vm";
	
	private boolean aleradyGenerate = false;
	
	public ResourcePropertiesFileGenerator(GenerationInformation generationInformation) {
		super(generationInformation);
	}

	@Override
	public File generateFile(Entity entity, String template, File file) throws Exception {
		Velocity.init(properties);
		
		Template modelTemplate = Velocity.getTemplate(template);
		
		VelocityContext context = new VelocityContext();
		context.put("outputDir", outputDir);
		context.put("packageName", packageName);
		
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
	
	@Override
	public File generate(Entity entity) throws Exception {
		if (!aleradyGenerate) {
			aleradyGenerate = true;
			
			return this.generateFile(entity, getTemplate(), getFile(entity));
		}
		
		return null;
	}
	
	@Override
	public File getFile(Entity entity) {
		return new File(getDirectory(), "resource.properties");
	}

	@Override
	public File getDirectory() {		
		return new File(outputDir.getPath() + File.separator + "WebContent" + File.separator + "WEB-INF" + File.separator + "properties");
	}

	@Override
	public String getTemplate() {
		return TEMPLATE;
	}

}
