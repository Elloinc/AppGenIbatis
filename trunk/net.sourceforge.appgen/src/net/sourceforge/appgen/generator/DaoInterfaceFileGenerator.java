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

import net.sourceforge.appgen.model.Dao;
import net.sourceforge.appgen.model.Entity;
import net.sourceforge.appgen.model.GenerationInformation;
import net.sourceforge.appgen.util.ConventionUtils;

/**
 * @author Byeongkil Woo
 */
public class DaoInterfaceFileGenerator extends FileGenerator {

	public static final String TEMPLATE = "dao.vm";
	
	public DaoInterfaceFileGenerator(GenerationInformation generationInformation) {
		super(generationInformation);
	}
	
	@Override
	public File getFile(Entity entity) {
		Dao dao = new Dao(entity);
		
		return new File(getDirectory(), dao.getInterfaceName() + ".java");
	}

	@Override
	public File getDirectory() {
		Entity entity = new Entity();
		entity.setPackageName(packageName);
		Dao dao = new Dao(entity);
		
		return new File(outputDir.getPath() + File.separator + "src" + File.separator + ConventionUtils.getPath(dao.getInterfaceFullPackageName()));
	}

	@Override
	public String getTemplate() {
		return TEMPLATE;
	}
	
}
