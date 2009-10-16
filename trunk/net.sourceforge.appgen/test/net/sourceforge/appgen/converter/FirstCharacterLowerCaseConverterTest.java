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

package net.sourceforge.appgen.converter;

import static org.junit.Assert.*;

import net.sourceforge.appgen.converter.FirstCharacterLowerCaseConverter;

import org.junit.Test;

/**
 * @author Byeongkil Woo
 */
public class FirstCharacterLowerCaseConverterTest {

	@Test
	public void testConvert() {
		FirstCharacterLowerCaseConverter converter = new FirstCharacterLowerCaseConverter();
		
		assertNull(converter.convert(null));
		assertEquals("", converter.convert(""));
		assertEquals("aBCDEF", converter.convert("ABCDEF"));
		assertEquals("abcdef", converter.convert("abcdef"));
		assertEquals("abc_def", converter.convert("abc_def"));
		assertEquals("_abc_def", converter.convert("_abc_def"));
	}

}
