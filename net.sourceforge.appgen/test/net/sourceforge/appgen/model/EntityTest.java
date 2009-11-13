package net.sourceforge.appgen.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class EntityTest {

	@Test
	public void testGetToStringFieldList() {
		Entity entity = new Entity();
		
		Field field0 = new Field(entity);
		field0.setCreate(true);
		field0.setPkPosition(1);
		entity.getFieldList().add(field0);
		assertArrayEquals(new Field[] { field0 }, entity.getToStringFieldList().toArray());
		
		Field field1 = new Field(entity);
		field1.setCreate(true);
		field1.setPkPosition(0);
		entity.getFieldList().add(field1);
		assertArrayEquals(new Field[] { field0 }, entity.getToStringFieldList().toArray());
		
		Field field2 = new Field(entity);
		field2.setCreate(false);
		field2.setPkPosition(0);
		entity.getFieldList().add(field2);
		assertArrayEquals(new Field[] { field0 }, entity.getToStringFieldList().toArray());
		
		Field field3 = new Field(entity);
		field3.setCreate(true);
		field3.setPkPosition(2);
		entity.getFieldList().add(field3);
		assertArrayEquals(new Field[] { field0, field3 }, entity.getToStringFieldList().toArray());
		
		Field field4 = new Field(entity);
		field4.setCreate(true);
		field4.setPkPosition(4);
		entity.getFieldList().add(field4);
		assertArrayEquals(new Field[] { field0, field3, field4 }, entity.getToStringFieldList().toArray());
		
		Field field5 = new Field(entity);
		field5.setCreate(true);
		field5.setPkPosition(3);
		entity.getFieldList().add(field5);
		assertArrayEquals(new Field[] { field0, field3, field5, field4 }, entity.getToStringFieldList().toArray());
		
		Field field6 = new Field(entity);
		field6.setCreate(false);
		field6.setPkPosition(0);
		field6.setFieldName("AttachFile");
		entity.getFieldList().add(field6);
		assertArrayEquals(new Field[] { field0, field3, field5, field4 }, entity.getToStringFieldList().toArray());
		
		Field field7 = new Field(entity);
		field7.setCreate(true);
		field7.setPkPosition(0);
		field7.setFieldType("AttachFile");
		entity.getFieldList().add(field7);
		
		System.out.println("entity.getToStringFieldList():" + entity.getToStringFieldList());
		
		assertArrayEquals(new Field[] { field0, field3, field5, field4, field7 }, entity.getToStringFieldList().toArray());
	}
	
	@Test
	public void testGetPrimaryKeyFieldList() {
		Entity entity = new Entity();
		
		Field field0 = new Field(entity);
		field0.setCreate(true);
		field0.setPkPosition(1);
		entity.getFieldList().add(field0);
		assertArrayEquals(new Field[] { field0 }, entity.getPrimaryKeyFieldList().toArray());
		
		Field field1 = new Field(entity);
		field1.setCreate(true);
		field1.setPkPosition(0);
		entity.getFieldList().add(field1);
		assertArrayEquals(new Field[] { field0 }, entity.getPrimaryKeyFieldList().toArray());
		
		Field field2 = new Field(entity);
		field2.setCreate(false);
		field2.setPkPosition(0);
		entity.getFieldList().add(field2);
		assertArrayEquals(new Field[] { field0 }, entity.getPrimaryKeyFieldList().toArray());
		
		Field field3 = new Field(entity);
		field3.setCreate(true);
		field3.setPkPosition(2);
		entity.getFieldList().add(field3);
		assertArrayEquals(new Field[] { field0, field3 }, entity.getPrimaryKeyFieldList().toArray());
		
		Field field4 = new Field(entity);
		field4.setCreate(true);
		field4.setPkPosition(4);
		entity.getFieldList().add(field4);
		assertArrayEquals(new Field[] { field0, field3, field4 }, entity.getPrimaryKeyFieldList().toArray());
		
		Field field5 = new Field(entity);
		field5.setCreate(true);
		field5.setPkPosition(3);
		entity.getFieldList().add(field5);
		assertArrayEquals(new Field[] { field0, field3, field5, field4 }, entity.getPrimaryKeyFieldList().toArray());
	}
	
	@Test
	public void testHasAttachFileField() {
		Entity entity = new Entity();
		
		Field field0 = new Field(entity);
		field0.setCreate(true);
		field0.setPkPosition(1);
		entity.getFieldList().add(field0);
		assertTrue(!entity.hasAttachFileField());
		
		Field field1 = new Field(entity);
		field1.setCreate(true);
		field1.setPkPosition(0);
		entity.getFieldList().add(field1);
		assertTrue(!entity.hasAttachFileField());
		
		Field field2 = new Field(entity);
		field2.setCreate(false);
		field2.setPkPosition(0);
		field2.setFieldType("AttachFile");
		entity.getFieldList().add(field2);
		assertTrue(!entity.hasAttachFileField());
		
		Field field3 = new Field(entity);
		field3.setCreate(true);
		field3.setPkPosition(2);
		field3.setFieldType("AttachFile");
		entity.getFieldList().add(field3);
		assertTrue(entity.hasAttachFileField());
		
		Field field4 = new Field(entity);
		field4.setCreate(true);
		field4.setPkPosition(4);
		field4.setFieldType("AttachFile");
		entity.getFieldList().add(field4);
		assertTrue(entity.hasAttachFileField());
	}
	
	@Test
	public void testGetAttachFileFieldList() {
		Entity entity = new Entity();
		
		Field field0 = new Field(entity);
		field0.setCreate(true);
		field0.setPkPosition(1);
		entity.getFieldList().add(field0);
		assertArrayEquals(new Field[] {}, entity.getAttachFileFieldList().toArray());
		
		Field field1 = new Field(entity);
		field1.setCreate(true);
		field1.setPkPosition(0);
		entity.getFieldList().add(field1);
		assertArrayEquals(new Field[] {}, entity.getAttachFileFieldList().toArray());
		
		Field field2 = new Field(entity);
		field2.setCreate(false);
		field2.setPkPosition(0);
		field2.setFieldType("AttachFile");
		entity.getFieldList().add(field2);
		assertArrayEquals(new Field[] {}, entity.getAttachFileFieldList().toArray());
		
		Field field3 = new Field(entity);
		field3.setCreate(true);
		field3.setPkPosition(2);
		field3.setFieldType("AttachFile");
		entity.getFieldList().add(field3);
		assertArrayEquals(new Field[] { field3 }, entity.getAttachFileFieldList().toArray());
		
		Field field4 = new Field(entity);
		field4.setCreate(true);
		field4.setPkPosition(4);
		field4.setFieldType("AttachFile");
		entity.getFieldList().add(field4);
		assertArrayEquals(new Field[] { field3, field4 }, entity.getAttachFileFieldList().toArray());
	}

	@Test
	public void testGetFieldListExceptLob() {
		Entity entity = new Entity();
		
		Field field0 = new Field(entity);
		field0.setCreate(true);
		entity.getFieldList().add(field0);
		assertArrayEquals(new Field[] { field0 }, entity.getFieldListExceptLob().toArray());
		
		Field field1 = new Field(entity);
		field1.setCreate(false);
		entity.getFieldList().add(field1);
		assertArrayEquals(new Field[] { field0 }, entity.getFieldListExceptLob().toArray());
		
		Field field2 = new Field(entity);
		field2.setCreate(false);
		field2.setLob(true);
		entity.getFieldList().add(field2);
		assertArrayEquals(new Field[] { field0 }, entity.getFieldListExceptLob().toArray());
		
		Field field3 = new Field(entity);
		field3.setCreate(true);
		field3.setLob(true);
		entity.getFieldList().add(field3);
		assertArrayEquals(new Field[] { field0 }, entity.getFieldListExceptLob().toArray());
		
		Field field4 = new Field(entity);
		field4.setCreate(true);
		entity.getFieldList().add(field4);
		assertArrayEquals(new Field[] { field0, field4 }, entity.getFieldListExceptLob().toArray());
	}

	@Test
	public void testGetInstanceName() {
		Entity entity = new Entity();
		
		entity.setBaseName("baseName");
		assertEquals("baseName", entity.getInstanceName());
		
		entity.setBaseName("BaseName");
		assertEquals("baseName", entity.getInstanceName());
		
		entity.setBaseName("AttachFile");
		assertEquals("attachFile", entity.getInstanceName());
	}

	@Test
	public void testGetClassName() {
		Entity entity = new Entity();
		
		assertNull(entity.getClassName());
		
		entity.setBaseName("domain");
		assertEquals("Domain", entity.getClassName());
	}

	@Test
	public void testGetFullPackageName() {
		Entity entity = new Entity();
		
		entity.setPackageName("com.mydomain");
		assertEquals("com.mydomain.domain", entity.getFullPackageName());
	}

	@Test
	public void testGetImportClassNameList() {
		Entity entity = new Entity();
		entity.setPackageName("com.mydomain");
		
		assertArrayEquals(new String[] { "java.io.Serializable" }, entity.getImportClassNameList().toArray());
		
		Field field0 = new Field(entity);
		field0.setFieldType("java.util.Date");
		entity.getFieldList().add(field0);
		assertArrayEquals(new String[] { "java.io.Serializable" }, entity.getImportClassNameList().toArray());
		
		Field field1 = new Field(entity);
		field1.setCreate(true);
		field1.setFieldType("java.util.Date");
		entity.getFieldList().add(field1);
		assertArrayEquals(new String[] { "java.io.Serializable", "java.util.Date" }, entity.getImportClassNameList().toArray());
		
		Field field2 = new Field(entity);
		field2.setFieldType("AttachFile");
		entity.getFieldList().add(field2);
		assertArrayEquals(new String[] { "java.io.Serializable", "java.util.Date", }, entity.getImportClassNameList().toArray());
		
		Field field3 = new Field(entity);
		field3.setCreate(true);
		field3.setFieldType("AttachFile");
		entity.getFieldList().add(field3);
		assertArrayEquals(new String[] { "java.io.Serializable", "java.util.Date", "com.mydomain.base.AttachFile" }, entity.getImportClassNameList().toArray());
		
		Field field4 = new Field(entity);
		field4.setCreate(true);
		field4.setFieldType("AttachFile");
		entity.getFieldList().add(field4);
		assertArrayEquals(new String[] { "java.io.Serializable", "java.util.Date", "com.mydomain.base.AttachFile" }, entity.getImportClassNameList().toArray());
	}

	@Test
	public void testHasLob() {
		Entity entity = new Entity();
		
		Field field0 = new Field(entity);
		field0.setCreate(true);
		entity.getFieldList().add(field0);
		assertTrue(!entity.hasLob());
		
		Field field1 = new Field(entity);
		field1.setCreate(false);
		entity.getFieldList().add(field1);
		assertTrue(!entity.hasLob());
		
		Field field2 = new Field(entity);
		field2.setCreate(false);
		field2.setLob(true);
		entity.getFieldList().add(field2);
		assertTrue(!entity.hasLob());
		
		Field field3 = new Field(entity);
		field3.setCreate(true);
		field3.setLob(true);
		entity.getFieldList().add(field3);
		assertTrue(entity.hasLob());
		
		Field field4 = new Field(entity);
		field4.setCreate(true);
		entity.getFieldList().add(field4);
		assertTrue(entity.hasLob());
	}

	@Test
	public void testIsValidBaseName() {
		Entity entity = new Entity();
		
		entity.setBaseName("domain");
		assertTrue(entity.isValidBaseName());
	}

	@Test
	public void testHasDuplicateFieldName() {
		Entity entity = new Entity();
		
		Field field0 = new Field(entity);
		field0.setCreate(true);
		field0.setFieldName("abc");
		entity.getFieldList().add(field0);
		assertTrue(!entity.hasDuplicateFieldName());
		
		Field field1 = new Field(entity);
		field1.setCreate(false);
		field1.setFieldName("abc");
		entity.getFieldList().add(field1);
		assertTrue(!entity.hasDuplicateFieldName());
		
		Field field2 = new Field(entity);
		field2.setCreate(true);
		field2.setFieldName("abc");
		entity.getFieldList().add(field2);
		assertTrue(entity.hasDuplicateFieldName());
	}

}
