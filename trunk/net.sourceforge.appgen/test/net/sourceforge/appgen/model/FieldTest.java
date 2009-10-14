package net.sourceforge.appgen.model;

import static org.junit.Assert.*;

import net.sourceforge.appgen.model.Field;

import org.junit.Test;

/**
 * @author Byeongkil Woo
 */
public class FieldTest {
	
	public static final String[] PRIMITIVE_TYPES = new String[] { "byte", "short", "int", "long", "float", "double", "char", "boolean" };
	
	public static final String[] PRIMITIVE_WRAPPER_TYPES = new String[] { Byte.class.getName(), Short.class.getName(), Integer.class.getName(), Long.class.getName(), Float.class.getName(), Double.class.getName(), Character.class.getName(), Boolean.class.getName() };
	
	public static final String[] SIMPLE_PRIMITIVE_WRAPPER_TYPES = new String[] { Byte.class.getSimpleName(), Short.class.getSimpleName(), Integer.class.getSimpleName(), Long.class.getSimpleName(), Float.class.getSimpleName(), Double.class.getSimpleName(), Character.class.getSimpleName(), Boolean.class.getSimpleName() };

	@Test
	public void testGetFirstCapFieldName() {
		Field field = new Field(null);

		field.setFieldName(null);
		assertNull(field.getFirstCapFieldName());

		field.setFieldName("");
		assertEquals("", field.getFirstCapFieldName());

		field.setFieldName("abc");
		assertEquals("Abc", field.getFirstCapFieldName());
	}

	@Test
	public void testGetSetterMethodName() {
		Field field = new Field(null);

		field.setFieldName("field");

		assertEquals("setField", field.getSetterMethodName());
	}

	@Test
	public void testGetGetterMethodName() {
		Field field = new Field(null);

		field.setFieldName("field");
		field.setFieldType(String.class.getName());

		assertEquals("getField", field.getGetterMethodName());

		field.setFieldType("boolean");
		assertEquals("isField", field.getGetterMethodName());
	}

	@Test
	public void testGetSimpleFieldType() {
		Field field = new Field(null);

		field.setFieldType(String.class.getName());
		assertEquals("String", field.getSimpleFieldType());
		
		field.setFieldType("Type");
		assertEquals("Type", field.getSimpleFieldType());
		
		for (int i = 0; i < PRIMITIVE_TYPES.length; i++) {
			field.setFieldType(PRIMITIVE_TYPES[i]);
			assertEquals(PRIMITIVE_TYPES[i], PRIMITIVE_TYPES[i], field.getSimpleFieldType());
		}
	}

	@Test
	public void testHasNullValue() {
		Field field = new Field(null);
		
		field.setFieldType(String.class.getName());
		
		assertTrue(!field.hasNullValue());
		
		for (int i = 0; i < PRIMITIVE_TYPES.length; i++) {
			field.setFieldType(PRIMITIVE_TYPES[i]);
			
			if ("char".equals(PRIMITIVE_TYPES[i])) {
				assertTrue(PRIMITIVE_TYPES[i], !field.hasNullValue());
			} else {
				assertTrue(PRIMITIVE_TYPES[i], field.hasNullValue());
			}
		}
	}

	@Test
	public void testGetNullValue() {
		Field field = new Field(null);
		
		assertNull(field.getNullValue());
		
		field.setFieldType(String.class.getName());
		
		assertNull(field.getNullValue());
		
		for (int i = 0; i < PRIMITIVE_TYPES.length; i++) {
			field.setFieldType(PRIMITIVE_TYPES[i]);
			
			if ("char".equals(PRIMITIVE_TYPES[i])) {
				assertNull(PRIMITIVE_TYPES[i], field.getNullValue());
			} else if ("boolean".equals(PRIMITIVE_TYPES[i])) {
				assertEquals(PRIMITIVE_TYPES[i], "false", field.getNullValue());
			} else {
				assertEquals(PRIMITIVE_TYPES[i], "0", field.getNullValue());
			}
		}
		
		for (int i = 0; i < PRIMITIVE_WRAPPER_TYPES.length; i++) {
			field.setFieldType(PRIMITIVE_WRAPPER_TYPES[i]);
			
			if (Character.class.getName().equals(PRIMITIVE_WRAPPER_TYPES[i])) {
				assertNull(PRIMITIVE_WRAPPER_TYPES[i], field.getNullValue());
			} else if (Boolean.class.getName().equals(PRIMITIVE_WRAPPER_TYPES[i])) {
				assertEquals(PRIMITIVE_WRAPPER_TYPES[i], "false", field.getNullValue());
			} else {
				assertEquals(PRIMITIVE_WRAPPER_TYPES[i], "0", field.getNullValue());
			}
		}
	}

	@Test
	public void testIsPrimitiveType() {
		Field field = new Field(null);
		
		assertTrue(!field.isPrimitiveType());
		
		field.setFieldType(String.class.getName());
		assertTrue(!field.isPrimitiveType());
		
		for (int i = 0; i < PRIMITIVE_TYPES.length; i++) {
			field.setFieldType(PRIMITIVE_TYPES[i]);
			assertTrue(PRIMITIVE_TYPES[i], field.isPrimitiveType());
		}
		
		for (int i = 0; i < PRIMITIVE_WRAPPER_TYPES.length; i++) {
			field.setFieldType(PRIMITIVE_WRAPPER_TYPES[i]);
			assertTrue(PRIMITIVE_WRAPPER_TYPES[i], !field.isPrimitiveType());
		}
	}

	@Test
	public void testGetSimpleObjectClassName() {
		Field field = new Field(null);
		
		assertNull(field.getSimpleObjectClassName());
		
		field.setFieldType(String.class.getName());
		assertEquals(String.class.getSimpleName(), field.getSimpleObjectClassName());
		
		for (int i = 0; i < PRIMITIVE_TYPES.length; i++) {
			field.setFieldType(PRIMITIVE_TYPES[i]);
			assertEquals(PRIMITIVE_TYPES[i], SIMPLE_PRIMITIVE_WRAPPER_TYPES[i], field.getSimpleObjectClassName());
		}
		
		for (int i = 0; i < PRIMITIVE_WRAPPER_TYPES.length; i++) {
			field.setFieldType(PRIMITIVE_WRAPPER_TYPES[i]);
			assertEquals(PRIMITIVE_WRAPPER_TYPES[i], SIMPLE_PRIMITIVE_WRAPPER_TYPES[i], field.getSimpleObjectClassName());
		}
	}

	@Test
	public void testGetToPrimitiveMethod() {
		Field field = new Field(null);
		
		assertNull(field.getToPrimitiveMethod());
		
		field.setFieldType(String.class.getName());
		assertNull(field.getToPrimitiveMethod());
		
		for (int i = 0; i < PRIMITIVE_TYPES.length; i++) {
			field.setFieldType(PRIMITIVE_TYPES[i]);
			assertEquals(PRIMITIVE_TYPES[i], PRIMITIVE_TYPES[i] + "Value()", field.getToPrimitiveMethod());
		}
		
		for (int i = 0; i < PRIMITIVE_WRAPPER_TYPES.length; i++) {
			field.setFieldType(PRIMITIVE_WRAPPER_TYPES[i]);
			assertNull(PRIMITIVE_WRAPPER_TYPES[i], field.getToPrimitiveMethod());
		}
	}

	@Test
	public void testIsValidFieldName() {
		Field field = new Field(null);
		
		assertTrue(!field.isValidFieldName());
		
		field.setFieldName("");
		assertTrue(!field.isValidFieldName());
		
		field.setFieldName("_abc");
		assertTrue(!field.isValidFieldName());
		
		field.setFieldName("__abc");
		assertTrue(!field.isValidFieldName());
		
		field.setFieldName("aBc");
		assertTrue(!field.isValidFieldName());
		
		field.setFieldName("1a");
		assertTrue(!field.isValidFieldName());
		
		field.setFieldName("abc.");
		assertTrue(!field.isValidFieldName());
		
		field.setFieldName("abc.def");
		assertTrue(!field.isValidFieldName());
		
		field.setFieldName("abc-");
		assertTrue(!field.isValidFieldName());
		
		field.setFieldName("abc-def");
		assertTrue(!field.isValidFieldName());
		
		field.setFieldName("a");
		assertTrue(field.isValidFieldName());
		
		field.setFieldName("a1");
		assertTrue(field.isValidFieldName());
		
		field.setFieldName("abc");
		assertTrue(field.isValidFieldName());
		
		field.setFieldName("abcdefghijklmnopqrstuvwxyz_ABCDEFGHIJKLMNOPQRSTUVWXYZ_0123456789");
		assertTrue(field.isValidFieldName());
	}

	@Test
	public void testIsValidFieldType() {
		Field field = new Field(null);
		
		assertTrue(!field.isValidFieldType());
		
		field.setFieldType("");
		assertTrue(!field.isValidFieldType());
		
		field.setFieldType("_abc");
		assertTrue(!field.isValidFieldType());
		
		field.setFieldType(String.class.getName());
		assertTrue(field.isValidFieldType());
		
		for (int i = 0; i < PRIMITIVE_TYPES.length; i++) {
			field.setFieldType(PRIMITIVE_TYPES[i]);	
			assertTrue(PRIMITIVE_TYPES[i], field.isValidFieldType());
		}
		
		for (int i = 0; i < PRIMITIVE_WRAPPER_TYPES.length; i++) {
			field.setFieldType(PRIMITIVE_WRAPPER_TYPES[i]);
			assertTrue(PRIMITIVE_WRAPPER_TYPES[i], field.isValidFieldType());
		}
	}

}
