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

import net.sourceforge.appgen.model.Entity;
import net.sourceforge.appgen.model.GenerationInformation;

/**
 * @author Byeongkil Woo
 */
public class WebXmlFileGenerator extends FileGenerator {

	public static final String TEMPLATE = "webXml.vm";
	
	private boolean aleradyGenerate = false;
	
	public WebXmlFileGenerator(GenerationInformation generationInformation) {
		super(generationInformation);
	}
	
	@Override
	public File generate(Entity entity) throws Exception {
		if (!aleradyGenerate) {
			aleradyGenerate = true;
			
			return super.generate(entity);
		}
		
		return null;
	}
	
	@Override
	public boolean existFile(Entity entity) {
		if (!aleradyGenerate) {
			return super.existFile(entity);
		}
		
		return false;
	}
	
	@Override
	public File getFile(Entity entity) {
		return new File(getDirectory(), "web.xml");
	}

	@Override
	public File getDirectory() {		
		return new File(outputDir.getPath() + File.separator + "WebContent" + File.separator + "WEB-INF");
	}

	@Override
	public String getTemplate() {
		return TEMPLATE;
	}
	
}
