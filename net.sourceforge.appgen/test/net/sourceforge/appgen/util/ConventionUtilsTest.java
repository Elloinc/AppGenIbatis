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

package net.sourceforge.appgen.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.appgen.converter.StringConverter;
import net.sourceforge.appgen.model.Entity;
import net.sourceforge.appgen.model.Field;

import org.junit.Test;

/**
 * @author Byeongkil Woo
 */
public class ConventionUtilsTest {
	
	@Test
	public void testIsReservedWord() {
		// Java language
		assertTrue(ConventionUtils.isReservedWord("public"));
		assertTrue(ConventionUtils.isReservedWord("class"));
		assertTrue(ConventionUtils.isReservedWord("extends"));
		assertTrue(ConventionUtils.isReservedWord("implements"));
		assertTrue(ConventionUtils.isReservedWord("void"));
		assertTrue(ConventionUtils.isReservedWord("throws"));
		assertTrue(ConventionUtils.isReservedWord("this"));
		assertTrue(ConventionUtils.isReservedWord("super"));
		
		// AppGen
		assertTrue(ConventionUtils.isReservedWord("serialVersionUID"));
		assertTrue(ConventionUtils.isReservedWord("httpServletRequest"));
		assertTrue(ConventionUtils.isReservedWord("httpServletResponse"));
		assertTrue(ConventionUtils.isReservedWord("request"));
		assertTrue(ConventionUtils.isReservedWord("response"));
		assertTrue(ConventionUtils.isReservedWord("mav"));
		assertTrue(ConventionUtils.isReservedWord("actualRowsAffected"));
		assertTrue(ConventionUtils.isReservedWord("attachFilePersister"));
		assertTrue(ConventionUtils.isReservedWord("attachFilePropertyEditor"));
		assertTrue(ConventionUtils.isReservedWord("attachFile"));
		
		// java.lang package
		assertTrue(ConventionUtils.isReservedWord("object"));
		assertTrue(ConventionUtils.isReservedWord("string"));
		assertTrue(ConventionUtils.isReservedWord("system"));
		assertTrue(ConventionUtils.isReservedWord("error"));
		assertTrue(ConventionUtils.isReservedWord("exception"));
		assertTrue(ConventionUtils.isReservedWord("throwable"));
		assertTrue(ConventionUtils.isReservedWord("thread"));
		assertTrue(ConventionUtils.isReservedWord("cloneable"));
		assertTrue(ConventionUtils.isReservedWord("runnable"));
		assertTrue(ConventionUtils.isReservedWord("comparable"));
	}

	@Test
	public void testGetPath() {
		assertNull(ConventionUtils.getPath(null));
		assertEquals("", ConventionUtils.getPath(""));
		assertEquals("net", ConventionUtils.getPath("net"));
		assertEquals("net" + File.separator + "sourceforge" + File.separator + "appgen" + File.separator + "core" + File.separator + "util", 
				ConventionUtils.getPath("net.sourceforge.appgen.core.util"));
	}

	@Test
	public void testGetImportDeclarations() {
		List<String> classNameList0 = new ArrayList<String>();
		List<String> classNameList1 = new ArrayList<String>();
		
		classNameList0.add(Entity.class.getName());
		classNameList1.add(getImportString(Entity.class.getName()));
		
		classNameList0.add(Field.class.getName());
		classNameList1.add(getImportString(Field.class.getName()));
		
		classNameList0.add(StringConverter.class.getName());
		
		String exceptPackageName = StringConverter.class.getPackage().getName();
		
		assertEquals(classNameList1, ConventionUtils.getImportDeclarations(classNameList0, exceptPackageName));
	}
	
	private String getImportString(String className) {
		return "import " + className + ";";
	}

	@Test
	public void testGetSimpleClassName() {
		assertNull(ConventionUtils.getSimpleClassName(null));
		assertEquals("", ConventionUtils.getSimpleClassName(""));
		assertEquals("Name", ConventionUtils.getSimpleClassName("abc.def.Name"));
		assertEquals("Name", ConventionUtils.getSimpleClassName("Name"));
	}

	@Test
	public void testGetPackageName() {
		assertNull(ConventionUtils.getPackageName("Name"));
		assertNull(ConventionUtils.getPackageName(""));
		assertEquals("abc.def", ConventionUtils.getPackageName("abc.def.Name"));
	}

}
