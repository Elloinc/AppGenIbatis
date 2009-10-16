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
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import net.sourceforge.appgen.util.FileUtils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Byeongkil Woo
 */
public class FileUtilsTest {

	public static final String FILE_NAME = "FileUtilsTestFile.txt";

	@Test
	public void testLn() {
		assertEquals(System.getProperty("line.separator", "\n"), FileUtils.ln());
	}

	@Test
	public void testCopyFileFile() {
		try {
			File in = new File(ClassLoader.getSystemResource("net/sourceforge/appgen/util/" + FILE_NAME).toURI());
			File out = new File(System.getProperty("java.io.tmpdir"), FILE_NAME);

			FileUtils.copy(in, out);

			assertTrue(out.exists());
			assertTrue(in.length() == out.length());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testCopyInputStreamOutputStream() {
		try {
			File in = new File(ClassLoader.getSystemResource("net/sourceforge/appgen/util/" + FILE_NAME).toURI());
			File out = new File(System.getProperty("java.io.tmpdir"), FILE_NAME);

			FileUtils.copy(new FileInputStream(in), new FileOutputStream(out));

			assertTrue(out.exists());
			assertTrue(in.length() == out.length());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

}
