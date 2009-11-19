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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sourceforge.appgen.model.ConnectionInformation;
import net.sourceforge.appgen.model.Entity;
import net.sourceforge.appgen.model.Field;
import net.sourceforge.appgen.model.GenerationInformation;
import net.sourceforge.appgen.model.MappingData;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Byeongkil Woo
 */
public class XmlData {

	public static final String ROOT_ELEMENT_TAG_NAME = "appgen-mapping";

	public static final String CONNECTION_INFORMATION_TAG_NAME = "connectionInformation";

	public static final String DATABASE_TYPE_TAG_NAME = "databaseType";
	public static final String URL_TAG_NAME = "url";
	public static final String USER_TAG_NAME = "user";
	public static final String PASSWORD_TAG_NAME = "password";
	public static final String DRIVER_FILE_TAG_NAME = "driverFile";
	public static final String DRIVER_CLASS_NAME_TAG_NAME = "driverClassName";

	public static final String GENERATION_INFORMATION_TAG_NAME = "generationInformation";

	public static final String OUTPUT_DIR_TAG_NAME = "outputDir";
	public static final String PACKAGE_NAME_TAG_NAME = "packageName";

	public static final String ENTITY_LIST_TAG_NAME = "entityList";

	public static final String ENTITY_TAG_NAME = "entity";

	public static final String TABLE_NAME_ATTR_NAME = "tableName";
	public static final String BASE_NAME_ATTR_NAME = "baseName";
	public static final String CREATE_ATTR_NAME = "create";

	public static final String FIELD_LIST_TAG_NAME = "fieldList";

	public static final String FIELD_TAG_NAME = "field";

	public static final String COLUMN_NAME_ATTR_NAME = "columnName";
	public static final String COLUMN_TYPE_ATTR_NAME = "columnType";
	public static final String COLUMN_LENGTH_ATTR_NAME = "columnLength";
	public static final String PK_POSITION_ATTR_NAME = "pkPosition";
	public static final String LOB_ATTR_NAME = "lob";
	public static final String NULLABLE_ATTR_NAME = "nullable";
	public static final String FIELD_NAME_ATTR_NAME = "fieldName";
	public static final String FIELD_TYPE_ATTR_NAME = "fieldType";

	public static final String XSI_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema-instance";
	public static final String NAMESPACE_URI = "http://appgen.sourceforge.net/appgen-mapping";
	public static final String XSI_SCHEMA_LOCATION = "xsi:schemaLocation";
	public static final String SCHEMA_LOCATION = "http://cdnetworks-kr-1.dl.sourceforge.net/project/appgen/appgen-mapping-0.0.2.xsd";

	private MappingData mappingData;
	
	public XmlData() {
		this(null);
	}
	
	public XmlData(MappingData mappingData) {
		this.mappingData = mappingData;
	}

	public void saveToXml(File file) throws XmlDataException {
		try {
			Document document = createDocument();
			
			createXmlFile(file, document);
		} catch (Exception e) {
			throw new XmlDataException(e);
		}
	}
	
	public String getXmlText() throws XmlDataException {
		try {
			Document document = createDocument();
			
			return getXmlText(document);
		} catch (Exception e) {
			throw new XmlDataException(e);
		}
	}

	private Document createDocument() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		DOMImplementation dom = builder.getDOMImplementation();

		Document document = dom.createDocument(NAMESPACE_URI, ROOT_ELEMENT_TAG_NAME, null);
		Element root = document.getDocumentElement();
		root.setAttributeNS(XSI_NAMESPACE_URI, XSI_SCHEMA_LOCATION, NAMESPACE_URI + " " + SCHEMA_LOCATION);

		root.appendChild(createConnectionInformationElement(document));
		root.appendChild(createGenerationInformationElement(document));
		root.appendChild(createEntityListElement(document));

		return document;
	}

	private void createXmlFile(File file, Document document) throws IOException, TransformerException {
		Transformer transformer = newTransformer();
		
		FileWriter writer = new FileWriter(file);
		
		transformer.transform(new DOMSource(document), new StreamResult(writer));
	}
	
	private String getXmlText(Document document) throws IOException, TransformerException {
		Transformer transformer = newTransformer();
		
		ByteArrayOutputStream writer = new ByteArrayOutputStream();
		
		transformer.transform(new DOMSource(document), new StreamResult(writer));
		
		return writer.toString();
	}
	
	private Transformer newTransformer() throws TransformerConfigurationException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		transformerFactory.setAttribute("indent-number", new Integer(4));
		
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "4");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		
		return transformer;
	}

	private Element createConnectionInformationElement(Document document) {
		ConnectionInformation connectionInformation = mappingData.getConnectionInformation();
		
		Element element = document.createElement(CONNECTION_INFORMATION_TAG_NAME);

		Element databaseType = document.createElement(DATABASE_TYPE_TAG_NAME);
		databaseType.appendChild(document.createTextNode(connectionInformation.getDatabaseType() == null ? "" : connectionInformation.getDatabaseType()));
		element.appendChild(databaseType);

		Element url = document.createElement(URL_TAG_NAME);
		url.appendChild(document.createTextNode(connectionInformation.getUrl() == null ? "" : connectionInformation.getUrl()));
		element.appendChild(url);

		Element user = document.createElement(USER_TAG_NAME);
		user.appendChild(document.createTextNode(connectionInformation.getUser() == null ? "" : connectionInformation.getUser()));
		element.appendChild(user);

		Element password = document.createElement(PASSWORD_TAG_NAME);
		password.appendChild(document.createTextNode(connectionInformation.getPassword() == null ? "" : connectionInformation.getPassword()));
		element.appendChild(password);

		Element driverFile = document.createElement(DRIVER_FILE_TAG_NAME);
		driverFile.appendChild(document.createTextNode(connectionInformation.getDriverFile() == null ? "" : connectionInformation.getDriverFile().getPath()));
		element.appendChild(driverFile);

		Element driverClassName = document.createElement(DRIVER_CLASS_NAME_TAG_NAME);
		driverClassName.appendChild(document.createTextNode(connectionInformation.getDriverClassName() == null ? "" : connectionInformation.getDriverClassName()));
		element.appendChild(driverClassName);

		return element;
	}

	private Element createGenerationInformationElement(Document document) {
		GenerationInformation generationInformation = mappingData.getGenerationInformation();
		
		Element element = document.createElement(GENERATION_INFORMATION_TAG_NAME);

		Element outputDir = document.createElement(OUTPUT_DIR_TAG_NAME);
		outputDir.appendChild(document.createTextNode(generationInformation.getOutputDir() == null ? "" : generationInformation.getOutputDir().getPath()));
		element.appendChild(outputDir);

		Element packageName = document.createElement(PACKAGE_NAME_TAG_NAME);
		packageName.appendChild(document.createTextNode(generationInformation.getPackageName() == null ? "" : generationInformation.getPackageName()));
		element.appendChild(packageName);

		return element;
	}

	private Element createEntityListElement(Document document) {
		List<Entity> entityList = mappingData.getEntityList();
		
		Element element = document.createElement(ENTITY_LIST_TAG_NAME);

		if (entityList != null) {
			for (Entity entity : entityList) {
				Element entityElement = document.createElement(ENTITY_TAG_NAME);
	
				entityElement.setAttribute(TABLE_NAME_ATTR_NAME, entity.getTableName() == null ? "" : entity.getTableName());
				entityElement.setAttribute(BASE_NAME_ATTR_NAME, entity.getBaseName() == null ? "" : entity.getBaseName());
				entityElement.setAttribute(CREATE_ATTR_NAME, String.valueOf(entity.isCreate()));
	
				entityElement.appendChild(createFieldListElement(document, entity.getFieldList()));
	
				element.appendChild(entityElement);
			}
		}

		if (element.getChildNodes().getLength() == 0) {
			element.appendChild(document.createTextNode(""));
		}

		return element;
	}

	private Element createFieldListElement(Document document, List<Field> fieldList) {
		Element element = document.createElement(FIELD_LIST_TAG_NAME);

		for (Field field : fieldList) {
			Element fieldElement = document.createElement(FIELD_TAG_NAME);

			fieldElement.setAttribute(COLUMN_NAME_ATTR_NAME, field.getColumnName() == null ? "" : field.getColumnName());
			fieldElement.setAttribute(COLUMN_TYPE_ATTR_NAME, field.getColumnType() == null ? "" : field.getColumnType());
			fieldElement.setAttribute(COLUMN_LENGTH_ATTR_NAME, String.valueOf(field.getColumnLength()));
			fieldElement.setAttribute(PK_POSITION_ATTR_NAME, String.valueOf(field.getPkPosition()));
			fieldElement.setAttribute(LOB_ATTR_NAME, String.valueOf(field.isLob()));
			fieldElement.setAttribute(NULLABLE_ATTR_NAME, String.valueOf(field.isNullable()));
			fieldElement.setAttribute(FIELD_NAME_ATTR_NAME, field.getFieldName() == null ? "" : field.getFieldName());
			fieldElement.setAttribute(FIELD_TYPE_ATTR_NAME, field.getFieldType() == null ? "" : field.getFieldType());
			fieldElement.setAttribute(CREATE_ATTR_NAME, String.valueOf(field.isCreate()));

			element.appendChild(fieldElement);
		}

		if (element.getChildNodes().getLength() == 0) {
			element.appendChild(document.createTextNode(""));
		}

		return element;
	}

	public void loadFromXml(File file) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(file);

		loadMappingData(document);
	}
	
	public void loadFromXml(String text) throws Exception {
		InputStream in = new ByteArrayInputStream(text.getBytes());
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(in);

		loadMappingData(document);
	}
	
	private void loadMappingData(Document document) {
		Element root = document.getDocumentElement();
		Element connectionInformaitonElement = (Element) root.getElementsByTagName(XmlData.CONNECTION_INFORMATION_TAG_NAME).item(0);
		Element generationInformationElement = (Element) root.getElementsByTagName(XmlData.GENERATION_INFORMATION_TAG_NAME).item(0);
		Element entityListElement = (Element) root.getElementsByTagName(XmlData.ENTITY_LIST_TAG_NAME).item(0);

		ConnectionInformation connectionInformation = parseConnectionInformationElement(connectionInformaitonElement);
		GenerationInformation generationInformation = parseGenerationInformationElement(generationInformationElement);
		List<Entity> entityList = parseEntityListElement(entityListElement);
		
		this.mappingData = new MappingData(connectionInformation, generationInformation, entityList);
	}

	private ConnectionInformation parseConnectionInformationElement(Element connectionInformaitonElement) {
		ConnectionInformation connectionInformation = new ConnectionInformation();

		String databaseType = connectionInformaitonElement.getElementsByTagName(XmlData.DATABASE_TYPE_TAG_NAME).item(0).getTextContent();
		String url = connectionInformaitonElement.getElementsByTagName(XmlData.URL_TAG_NAME).item(0).getTextContent();
		String user = connectionInformaitonElement.getElementsByTagName(XmlData.USER_TAG_NAME).item(0).getTextContent();
		String password = connectionInformaitonElement.getElementsByTagName(XmlData.PASSWORD_TAG_NAME).item(0).getTextContent();
		String driverFilePath = connectionInformaitonElement.getElementsByTagName(XmlData.DRIVER_FILE_TAG_NAME).item(0).getTextContent();
		File driverFile = null;
		if (driverFilePath != null && driverFilePath.length() > 0) {
			driverFile = new File(driverFilePath);
		}
		String driverClassName = connectionInformaitonElement.getElementsByTagName(XmlData.DRIVER_CLASS_NAME_TAG_NAME).item(0).getTextContent();

		connectionInformation.setDatabaseType(databaseType);
		connectionInformation.setUrl(url);
		connectionInformation.setUser(user);
		connectionInformation.setPassword(password);
		connectionInformation.setDriverFile(driverFile);
		connectionInformation.setDriverClassName(driverClassName);

		return connectionInformation;
	}

	private GenerationInformation parseGenerationInformationElement(Element generationInformationElement) {
		GenerationInformation generationInformation = new GenerationInformation();

		String outputDirPath = generationInformationElement.getElementsByTagName(XmlData.OUTPUT_DIR_TAG_NAME).item(0).getTextContent();
		File outputDir = null;
		if (outputDirPath != null && outputDirPath.length() > 0) {
			outputDir = new File(outputDirPath);
		}
		String packageName = generationInformationElement.getElementsByTagName(XmlData.PACKAGE_NAME_TAG_NAME).item(0).getTextContent();

		generationInformation.setOutputDir(outputDir);
		generationInformation.setPackageName(packageName);

		return generationInformation;
	}

	private List<Entity> parseEntityListElement(Element entityListElement) {
		List<Entity> list = new ArrayList<Entity>();

		NodeList entityNodeList = entityListElement.getElementsByTagName(XmlData.ENTITY_TAG_NAME);

		for (int i = 0; i < entityNodeList.getLength(); i++) {
			Element entityElement = (Element) entityNodeList.item(i);

			Entity entity = new Entity();

			String tableName = entityElement.getAttribute(XmlData.TABLE_NAME_ATTR_NAME);
			String baseName = entityElement.getAttribute(XmlData.BASE_NAME_ATTR_NAME);
			boolean create = "true".equals(entityElement.getAttribute(XmlData.CREATE_ATTR_NAME));

			entity.setTableName(tableName);
			entity.setBaseName(baseName);
			entity.setCreate(create);

			Element fieldListElement = (Element) entityElement.getElementsByTagName(XmlData.FIELD_LIST_TAG_NAME).item(0);
			List<Field> fieldList = parseFieldListElement(fieldListElement, entity);
			entity.setFieldList(fieldList);

			list.add(entity);
		}

		return list;
	}

	private List<Field> parseFieldListElement(Element fieldListElement, Entity entity) {
		List<Field> list = new ArrayList<Field>();

		NodeList fieldNodeList = fieldListElement.getElementsByTagName(XmlData.FIELD_TAG_NAME);

		for (int i = 0; i < fieldNodeList.getLength(); i++) {
			Element fieldElement = (Element) fieldNodeList.item(i);

			Field field = new Field(entity);

			String columnName = fieldElement.getAttribute(COLUMN_NAME_ATTR_NAME);
			String columnType = fieldElement.getAttribute(COLUMN_TYPE_ATTR_NAME);
			int columnLength = 0;
			try {
				columnLength = Integer.parseInt(fieldElement.getAttribute(COLUMN_LENGTH_ATTR_NAME));
			} catch (Exception e) {
			}
			int pkPosition = 0;
			try {
				pkPosition = Integer.parseInt(fieldElement.getAttribute(PK_POSITION_ATTR_NAME));
			} catch (Exception e) {
			}
			boolean lob = "true".equals(fieldElement.getAttribute(LOB_ATTR_NAME));
			boolean nullable = "true".equals(fieldElement.getAttribute(NULLABLE_ATTR_NAME));
			String fieldName = fieldElement.getAttribute(FIELD_NAME_ATTR_NAME);
			String fieldType = fieldElement.getAttribute(FIELD_TYPE_ATTR_NAME);
			boolean create = "true".equals(fieldElement.getAttribute(XmlData.CREATE_ATTR_NAME));

			field.setColumnName(columnName);
			field.setColumnType(columnType);
			field.setColumnLength(columnLength);
			field.setPkPosition(pkPosition);
			field.setLob(lob);
			field.setNullable(nullable);
			field.setFieldName(fieldName);
			field.setFieldType(fieldType);
			field.setCreate(create);

			list.add(field);
		}

		return list;
	}

	public MappingData getMappingData() {
		return mappingData;
	}

	public void setMappingData(MappingData mappingData) {
		this.mappingData = mappingData;
	}

}
