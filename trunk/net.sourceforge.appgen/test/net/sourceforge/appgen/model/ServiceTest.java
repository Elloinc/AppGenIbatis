package net.sourceforge.appgen.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class ServiceTest {

	@Test
	public void testGetInterfaceName() {
		Entity entity = new Entity();
		entity.setPackageName("com.mydomain");
		entity.setBaseName("domain");
		
		Service service = new Service(entity);
		assertEquals("DomainService", service.getInterfaceName());
	}

	@Test
	public void testGetClassName() {
		Entity entity = new Entity();
		entity.setPackageName("com.mydomain");
		entity.setBaseName("domain");
		
		Service service = new Service(entity);
		assertEquals("DomainServiceImpl", service.getClassName());
	}

	@Test
	public void testGetInstanceName() {
		Entity entity = new Entity();
		entity.setPackageName("com.mydomain");
		entity.setBaseName("domain");
		
		Service service = new Service(entity);
		assertEquals("domainService", service.getInstanceName());
	}

	@Test
	public void testGetInterfaceFullPackageName() {
		Entity entity = new Entity();
		entity.setPackageName("com.mydomain");
		entity.setBaseName("domain");
		
		Service service = new Service(entity);
		assertEquals("com.mydomain.service", service.getInterfaceFullPackageName());
	}

	@Test
	public void testGetClassFullPackageName() {
		Entity entity = new Entity();
		entity.setPackageName("com.mydomain");
		entity.setBaseName("domain");
		
		Service service = new Service(entity);
		assertEquals("com.mydomain.service.impl", service.getClassFullPackageName());
	}

	@Test
	public void testGetSuperClassName() {
		Entity entity = new Entity();
		entity.setPackageName("com.mydomain");
		entity.setBaseName("domain");
		
		Service service = new Service(entity);
		assertEquals("Service", service.getSuperClassName());
	}

	@Test
	public void testGetSuperClassFullPackageName() {
		Entity entity = new Entity();
		entity.setPackageName("com.mydomain");
		entity.setBaseName("domain");
		
		Service service = new Service(entity);
		assertEquals("com.mydomain.base", service.getSuperClassFullPackageName());
	}

	@Test
	public void testGetImportClassNameList() {
		Entity entity = new Entity();
		entity.setPackageName("com.mydomain");
		
		Service service = new Service(entity);
		
		assertTrue(service.getImportClassNameList(false).contains(entity.getFullPackageName() + "." + entity.getClassName()));
		assertTrue(service.getImportClassNameList(true).contains(entity.getFullPackageName() + "." + entity.getClassName()));
		
		assertTrue(service.getImportClassNameList(false).contains("java.util.List"));
		assertTrue(service.getImportClassNameList(true).contains("java.util.List"));
		
		Field field0 = new Field(entity);
		field0.setCreate(true);
		field0.setFieldType("AttachFile");
		entity.getFieldList().add(field0);
		assertTrue(!service.getImportClassNameList(false).contains("com.mydomain.base.AttachFile"));
		assertTrue(!service.getImportClassNameList(false).contains("com.mydomain.base.AttachFilePersister"));
		assertTrue(service.getImportClassNameList(true).contains("com.mydomain.base.AttachFile"));
		assertTrue(service.getImportClassNameList(true).contains("com.mydomain.base.AttachFilePersister"));
	}

}
