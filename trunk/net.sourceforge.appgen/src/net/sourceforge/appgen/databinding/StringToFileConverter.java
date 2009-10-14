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

package net.sourceforge.appgen.databinding;

import java.io.File;

import org.eclipse.core.databinding.conversion.IConverter;

/**
 * @author Byeongkil Woo
 */
public class StringToFileConverter implements IConverter {

	public Object convert(Object fromObject) {
		return new File(fromObject.toString());
	}

	public Object getFromType() {
		return String.class;
	}

	public Object getToType() {
		return File.class;
	}

}
