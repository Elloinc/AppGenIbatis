package net.sourceforge.appgen.util;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.appgen.converter.StringConverter;
import net.sourceforge.appgen.model.Entity;
import net.sourceforge.appgen.model.Field;
import net.sourceforge.appgen.util.ConventionUtils;

import org.junit.Test;

/**
 * @author Byeongkil Woo
 */
public class ConventionUtilsTest {

	@Test
	public void testIsReservedWord() {
		assertTrue(ConventionUtils.isReservedWord("class"));
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
