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

package net.sourceforge.appgen.xml;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.appgen.model.ConnectionInformation;
import net.sourceforge.appgen.model.Entity;
import net.sourceforge.appgen.model.Field;
import net.sourceforge.appgen.model.GenerationInformation;
import net.sourceforge.appgen.model.MappingData;

/**
 * @author Byeongkil Woo
 */
public class XmlDataTest {

	public static String XSD_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema";

	private ConnectionInformation createConnectionInformation() {
		ConnectionInformation connectionInformation = new ConnectionInformation();
		connectionInformation.setName("TEST_DB");
		connectionInformation.setSchema("TEST_SCHEMA");
		
		return connectionInformation;
	}
	
	private GenerationInformation createGenerationInformation() {
		File file = new File(System.getProperty("java.io.tmpdir"));
		
		GenerationInformation generationInformation = new GenerationInformation();
		generationInformation.setOutputDir(file);
		generationInformation.setPackageName("com.mydomain.myproject");
		
		return generationInformation;
	}
	
	private List<Entity> createEntityList() {
		List<Entity> entityList = new ArrayList<Entity>();

		for (int i = 0; i < 5; i++) {
			Entity entity = new Entity();
			entity.setTableName("tableName" + i);
			entity.setBaseName("baseName" + i);
			entity.setCreate(i % 2 == 0);

			for (int j = 0; j < 5; j++) {
				Field field = new Field(entity);
				field.setColumnName("columnName" + i + "" + j);

				switch (j) {
				case 0:
					field.setColumnType("NUMBER");
					field.setColumnSize(10);
					field.setFieldType("int");
					break;
				case 1:
					field.setColumnType("DATE");
					field.setColumnSize(8);
					field.setFieldType("java.util.Date");
					break;
				default:
					field.setColumnType("VARCHAR2");
					field.setColumnSize(20);
					field.setFieldType("java.lang.String");
				}

				if (j < 2) {
					field.setPkPosition(j + 1);
				}

				field.setLob(j == 4);
				field.setFieldName("field" + i + "" + j);

				entity.getFieldList().add(field);
			}

			entityList.add(entity);
		}
		
		return entityList;
	}
	
	public XmlData createXmlData() {
		ConnectionInformation connectionInformation = createConnectionInformation();
		GenerationInformation generationInformation = createGenerationInformation();
		List<Entity> entityList = createEntityList();
		
		MappingData mappingData = new MappingData(connectionInformation, generationInformation, entityList);
		
		XmlData xmlData = new XmlData(mappingData);
		
		return xmlData;
	}
	
	@Test
	public void saveToXml() throws Exception {
		File file = new File(System.getProperty("java.io.tmpdir"), "appgen-mapping.xml");
		System.out.println(getClass().getName() + "." + "saveToXml()" + ":" + file);

		XmlData xmlData = createXmlData();

		xmlData.saveToXml(file);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(file);
		
		Element root = document.getDocumentElement();
		Assert.assertEquals(XmlData.ROOT_ELEMENT_TAG_NAME, root.getTagName());

		SchemaFactory schemaFactory = SchemaFactory.newInstance(XSD_NAMESPACE_URI);
		Schema schema = schemaFactory.newSchema(ClassLoader.getSystemResource("net/sourceforge/appgen/xml/appgen-mapping-1.0.0.xsd"));
		
		Validator validator = schema.newValidator();
		validator.validate(new StreamSource(new FileInputStream(file)));
	}
	
	@Test
	public void loadFromXml() throws Exception {
		saveToXml();
		
		File file = new File(System.getProperty("java.io.tmpdir"), "appgen-mapping.xml");
		System.out.println(getClass().getName() + "." + "loadFromXml()" + ":" + file);

		XmlData xmlData = new XmlData();
		xmlData.loadFromXml(file);
		
		MappingData mappingData = xmlData.getMappingData();
		
		ConnectionInformation connectionInformation = mappingData.getConnectionInformation();
		GenerationInformation generationInformation = mappingData.getGenerationInformation();
		List<Entity> entityList = mappingData.getEntityList();
		
		Assert.assertEquals(createConnectionInformation(), connectionInformation);
		Assert.assertEquals(createGenerationInformation(), generationInformation);
		Assert.assertEquals(createEntityList(), entityList);
	}

}
