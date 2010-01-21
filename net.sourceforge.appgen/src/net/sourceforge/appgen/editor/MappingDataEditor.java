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

package net.sourceforge.appgen.editor;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.sourceforge.appgen.action.GenerateFileAction;
import net.sourceforge.appgen.connector.JdbcConnector;
import net.sourceforge.appgen.connector.JdbcConnectorFactory;
import net.sourceforge.appgen.databinding.ClassNameValidator;
import net.sourceforge.appgen.databinding.DatabaseTypeValidator;
import net.sourceforge.appgen.databinding.DirectoryValidator;
import net.sourceforge.appgen.databinding.FileValidator;
import net.sourceforge.appgen.databinding.PackageNameValidator;
import net.sourceforge.appgen.databinding.PasswordValidator;
import net.sourceforge.appgen.databinding.StringToFileConverter;
import net.sourceforge.appgen.databinding.UrlValidator;
import net.sourceforge.appgen.databinding.UserValidator;
import net.sourceforge.appgen.model.ConnectionInformation;
import net.sourceforge.appgen.model.Entity;
import net.sourceforge.appgen.model.Field;
import net.sourceforge.appgen.model.GenerationInformation;
import net.sourceforge.appgen.model.MappingData;
import net.sourceforge.appgen.model.ValueModifyListener;
import net.sourceforge.appgen.support.EntityBaseNameEditingSupport;
import net.sourceforge.appgen.support.EntityGeneratorEditingSupport;
import net.sourceforge.appgen.support.EntityTableLabelProvider;
import net.sourceforge.appgen.support.EntityTableNameEditingSupport;
import net.sourceforge.appgen.support.FieldColumnLengthEditingSupport;
import net.sourceforge.appgen.support.FieldColumnNameEditingSupport;
import net.sourceforge.appgen.support.FieldColumnTypeEditingSupport;
import net.sourceforge.appgen.support.FieldCreateEditingSupport;
import net.sourceforge.appgen.support.FieldLobEditingSupport;
import net.sourceforge.appgen.support.FieldNameEditingSupport;
import net.sourceforge.appgen.support.FieldNullableEditingSupport;
import net.sourceforge.appgen.support.FieldPkPositionEditingSupport;
import net.sourceforge.appgen.support.FieldPositionEditingSupport;
import net.sourceforge.appgen.support.FieldTableLabelProvider;
import net.sourceforge.appgen.support.FieldTypeEditingSupport;
import net.sourceforge.appgen.xml.XmlData;
import net.sourceforge.appgen.xml.XmlDataException;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.RefreshAction;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;

/**
 * Mpping file editor.
 * 
 * @author Byeongkil Woo
 */
public class MappingDataEditor extends MultiPageEditorPart {

	private static final FieldDecoration DEC_ERROR = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);

	private ImageRegistry imageRegistry;

	public static final String CHECKED_IMAGE = "header_checked";
	public static final String UNCHECKED_IMAGE = "header_unchecked";

	MappingData loadMappingData;
	
	MappingData mappingData;
	
	private TextEditor textEditor;
	
	private Entity currentEntity;
	private boolean allEntitySelection = false;

	private MessageConsole console;
	private MessageConsoleStream stream;
	private IConsoleManager consoleManager;

	private Combo databaseTypeCombo;
	private Text urlText;
	private Text userText;
	private Text passwordText;
	private Text driverFileText;
	private Text driverClassNameText;

	private ControlDecoration databaseTypeControlDecoration;
	private IValidator databaseTypeValidator;

	private ControlDecoration urlControlDecoration;
	private IValidator urlValidator;

	private ControlDecoration userControlDecoration;
	private IValidator userValidator;

	private ControlDecoration passwordControlDecoration;
	private IValidator passwordValidator;

	private ControlDecoration driverFileControlDecoration;
	private IValidator driverFileValidator;

	private ControlDecoration driverClassNameControlDecoration;
	private IValidator driverClassNameValidator;

	private TableViewer entityTableViewer;
	private TableViewer fieldTableViewer;

	private Text outputDirText;
	private Text packageNameText;

	private ControlDecoration outputDirControlDecoration;
	private IValidator outputDirValidator;

	private ControlDecoration packageNameControlDecoration;
	private IValidator packageNameValidator;

	private Button connectionButton;
	private Button generationButton;
	
	private Button addEntityButton;
	private Button removeEntityButton;
	private Button addFieldButton;
	private Button removeFieldButton;	

	private DataBindingContext dataBindingContext;
	
	private boolean dirty;

	public MappingDataEditor() {
		super();
		
		mappingData = new MappingData();
		
		consoleManager = ConsolePlugin.getDefault().getConsoleManager();
		console = new MessageConsole("AppGen", null);
		stream = console.newMessageStream();
		consoleManager.addConsoles(new IConsole[] { console });
		consoleManager.showConsoleView(console);
		
		initImageRegistry();
	}
	
	private void initImageRegistry() {
		imageRegistry = new ImageRegistry();
		
		String iconPath = "icon/";
		imageRegistry.put(CHECKED_IMAGE, ImageDescriptor.createFromFile(MappingDataEditor.class, iconPath + CHECKED_IMAGE + ".gif"));
		imageRegistry.put(UNCHECKED_IMAGE, ImageDescriptor.createFromFile(MappingDataEditor.class, iconPath + UNCHECKED_IMAGE + ".gif"));
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		
		setPartName(input.getName());
		
		loadData(input);
	}
	
	@Override
	public void dispose() {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
			}
		}

		if (consoleManager != null) {
			consoleManager.removeConsoles(new IConsole[] { console });
		}
		
		if (dataBindingContext != null) {
			dataBindingContext.dispose();
		}
		
		if (imageRegistry != null) {
			imageRegistry.dispose();
		}
		
		super.dispose();
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}
	
	public MessageConsole getConsole() {
		return console;
	}
	
	public void loadData(IEditorInput input) {
		FileEditorInput fileEditorInput = (FileEditorInput) input;
		File file = new File(fileEditorInput.getURI());
		
		XmlData xmlData = new XmlData();
		
		try {
			xmlData.loadFromXml(file);
			
			mappingData = xmlData.getMappingData();
			
			mappingData.addValueModifyListener(new DataModifyListener());
		} catch (Exception e) {
			MessageDialog.openError(getSite().getShell(), "Error - loadData", e.getMessage());
		}
	}

	private void createConnectionInputPart(final Composite contentComponent) {
		GridData gridData;

		Label heading = new Label(contentComponent, SWT.LEFT | SWT.BOLD);
		heading.setText("Connection info");
		gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.horizontalSpan = 3;
		heading.setLayoutData(gridData);

		Label databseTypeLabel = new Label(contentComponent, SWT.LEFT);
		databseTypeLabel.setText("Database Type:");

		createDatabaseTypeCombo(contentComponent);
		gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.horizontalSpan = 2;
		databaseTypeCombo.setLayoutData(gridData);

		Label urlLabel = new Label(contentComponent, SWT.LEFT);
		urlLabel.setText("URL:");

		createUrlText(contentComponent);
		gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.horizontalSpan = 2;
		urlText.setLayoutData(gridData);

		Label userLabel = new Label(contentComponent, SWT.LEFT);
		userLabel.setText("User:");

		createUserText(contentComponent);
		gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.horizontalSpan = 2;
		userText.setLayoutData(gridData);

		Label passwordLabel = new Label(contentComponent, SWT.LEFT);
		passwordLabel.setText("Password:");

		createPasswordText(contentComponent);
		gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.horizontalSpan = 2;
		passwordText.setLayoutData(gridData);

		Label driverFileLabel = new Label(contentComponent, SWT.LEFT);
		driverFileLabel.setText("Driver file:");

		createDriverFileText(contentComponent);
		gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		driverFileText.setLayoutData(gridData);

		Button driverFileButton = new Button(contentComponent, SWT.BUTTON1);
		driverFileButton.setText("Browse...");

		driverFileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(driverFileText.getShell(), SWT.OPEN);
				fileDialog.setFilterExtensions(new String[] { "*.jar", "*.zip" });
				String selected = fileDialog.open();
				driverFileText.setText(selected);
			}
		});

		Label driverClassNameLabel = new Label(contentComponent, SWT.LEFT);
		driverClassNameLabel.setText("Driver class name:");

		createDriverClassNameText(contentComponent);
		gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.horizontalSpan = 2;
		driverClassNameText.setLayoutData(gridData);
	}

	private void createConnectionButtonPart(final Composite contentComponent) {
		GridData gridData;

		connectionButton = new Button(contentComponent, SWT.PUSH);
		connectionButton.setText("Initialize entities from tables");
		gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		gridData.horizontalSpan = 3;
		connectionButton.setLayoutData(gridData);

		connectionButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				ConnectionInformation connectionInformation = mappingData.getConnectionInformation();

				if (!validateConnectionInformation()) {
					return;
				}

				List<Entity> entityList = null;
				
				try {
					contentComponent.setEnabled(false);

					JdbcConnector connector = JdbcConnectorFactory.createConnector(connectionInformation);

					try {
						entityList = connector.getEntityList();
					} catch (Exception e) {
						MessageDialog.openError(getSite().getShell(), "Error - widgetSelected", e.toString());
					}
				} finally {
					contentComponent.setEnabled(true);
				}
				
				mappingData.setEntityList(entityList);
				
				mappingData.addValueModifyListener(new DataModifyListener());
				
				showEntityList(mappingData.getEntityList());
				
				dirty = true;
				firePropertyChange(PROP_DIRTY);
			}
		});
	}
	
	private void createEntityButtonPart(final Composite contentComponent) {
		GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		gridData.horizontalSpan = 3;
		
		final Composite composite = new Composite(contentComponent, SWT.NONE);
		composite.setLayoutData(gridData);
		composite.setLayout(new FillLayout());

		addEntityButton = new Button(composite, SWT.PUSH);
		addEntityButton.setText("Add entity");

		addEntityButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				Entity entity = new Entity();
				entity.setCreate(allEntitySelection);
				mappingData.getEntityList().add(entity);
				
				mappingData.addValueModifyListener(new DataModifyListener());
				
				entityTableViewer.setInput(mappingData.getEntityList());
				entityTableViewer.getTable().setSelection(mappingData.getEntityList().size() - 1);
				
				fieldTableViewer.setInput(entity.getFieldList());
				
				currentEntity = entity;
				
				dirty = true;
				firePropertyChange(PROP_DIRTY);
			}
		});
		
		removeEntityButton = new Button(composite, SWT.PUSH);
		removeEntityButton.setText("Remove entity");
		
		removeEntityButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (currentEntity == null) {
					MessageDialog.openError(getSite().getShell(), "Invalid", "Select entity.");
					return;
				}
				
				boolean confirm = MessageDialog.openConfirm(getSite().getShell(), "confirm", "Delete selected entity?");
				
				if (!confirm) {
					return;
				}
				
				Table entityTable = entityTableViewer.getTable();

				TableItem[] tableItems = entityTable.getItems();
				for (int i = 0; i < tableItems.length; i++) {
					Entity tableEntity = (Entity) tableItems[i].getData();

					if (currentEntity == tableEntity) {
						mappingData.getEntityList().remove(i);
						break;
					}
				}
				
				entityTableViewer.setInput(mappingData.getEntityList());
				fieldTableViewer.setInput(null);
				
				entityTableViewer.getTable().setSelection(- 1);
				fieldTableViewer.getTable().setSelection(-1);
				
				currentEntity = null;
				
				dirty = true;
				firePropertyChange(PROP_DIRTY);
			}
		});
	}
	
	private void createFieldButtonPart(final Composite contentComponent) {
		GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		gridData.horizontalSpan = 3;
		
		final Composite composite = new Composite(contentComponent, SWT.NONE);
		composite.setLayoutData(gridData);
		composite.setLayout(new FillLayout());

		addFieldButton = new Button(composite, SWT.PUSH);
		addFieldButton.setText("Add field");

		addFieldButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (currentEntity == null) {
					MessageDialog.openError(getSite().getShell(), "Invalid", "Select entity.");
					return;
				}

				Field field = new Field(currentEntity);
				field.setCreate(currentEntity.isAllFieldSelection());
				currentEntity.getFieldList().add(field);
				
				mappingData.addValueModifyListener(new DataModifyListener());
				
				fieldTableViewer.setInput(currentEntity.getFieldList());
				fieldTableViewer.getTable().setSelection(currentEntity.getFieldList().size() - 1);
				
				dirty = true;
				firePropertyChange(PROP_DIRTY);
			}
		});
		
		removeFieldButton = new Button(composite, SWT.PUSH);
		removeFieldButton.setText("Remove field");
		
		removeFieldButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				Field currentField = null;
				
				TableItem[] selectedTableItems = fieldTableViewer.getTable().getSelection();
				if (selectedTableItems != null) {
					currentField = (Field) selectedTableItems[0].getData();
				}
				
				if (currentField == null) {
					MessageDialog.openError(getSite().getShell(), "Invalid", "Select field.");
					return;
				}
				
				TableItem[] tableItems = fieldTableViewer.getTable().getItems();
				for (int i = 0; i < tableItems.length; i++) {
					if (tableItems[i].getData() == currentField) {
						currentField.getEntity().getFieldList().remove(i);
						break;
					}
				}
				
				fieldTableViewer.setInput(currentField.getEntity().getFieldList());
				
				fieldTableViewer.getTable().setSelection(-1);
				
				dirty = true;
				firePropertyChange(PROP_DIRTY);
			}
		});
	}

	private void createEntityTable(Composite parent) {
		GridData gridData;

		entityTableViewer = new TableViewer(parent, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		final Table entityTable = entityTableViewer.getTable();
		entityTable.setHeaderVisible(true);
		entityTable.setLinesVisible(true);

		String[] columnNames = new String[] { "", "Table name", "Base name" };
		int[] columnWidths = new int[] { 40, 310, 310 };
		int[] columnAlignments = new int[] { SWT.LEFT, SWT.LEFT, SWT.LEFT };

		for (int i = 0; i < columnNames.length; i++) {
			TableViewerColumn column = new TableViewerColumn(entityTableViewer, SWT.NONE);

			TableColumn tableColumn = column.getColumn();
			tableColumn.setText(columnNames[i]);
			tableColumn.setWidth(columnWidths[i]);
			tableColumn.setAlignment(columnAlignments[i]);

			if (i == 0) {
				tableColumn.setResizable(false);

				tableColumn.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						allEntitySelection = !allEntitySelection;
						changeAllEntitySelection();
					}
				});

				column.setEditingSupport(new EntityGeneratorEditingSupport(entityTableViewer));
			}
			if (i == 1) {
				column.setEditingSupport(new EntityTableNameEditingSupport(entityTableViewer));
			}
			if (i == 2) {
				column.setEditingSupport(new EntityBaseNameEditingSupport(entityTableViewer));
			}
		}
		
		entityTableViewer.setLabelProvider(new EntityTableLabelProvider());
		entityTableViewer.setContentProvider(new ArrayContentProvider());

		gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.horizontalSpan = 3;
		gridData.heightHint = 100;
		entityTable.setLayoutData(gridData);

		entityTable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem tableItem = (TableItem) e.item;

				TableItem[] tableItems = entityTable.getItems();
				for (int i = 0; i < tableItems.length; i++) {
					if (tableItems[i] == tableItem) {
						currentEntity = mappingData.getEntityList().get(i);
						fieldTableViewer.setInput(currentEntity.getFieldList());
					}
				}

				TableColumn tableColumn = fieldTableViewer.getTable().getColumn(0);
				tableColumn.setImage(currentEntity != null && currentEntity.isAllFieldSelection() ? imageRegistry.get(CHECKED_IMAGE) : imageRegistry.get(UNCHECKED_IMAGE));
			}
		});
		
		TableColumn tableColumn = entityTableViewer.getTable().getColumn(0);
		tableColumn.setImage(allEntitySelection ? imageRegistry.get(CHECKED_IMAGE) : imageRegistry.get(UNCHECKED_IMAGE));
	}

	private void createFieldTable(Composite parent) {
		GridData gridData;

		fieldTableViewer = new TableViewer(parent, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		Table fieldTable = fieldTableViewer.getTable();
		fieldTable.setHeaderVisible(true);
		fieldTable.setLinesVisible(true);

		String[] columnNames = new String[] { "", "Column name", "Column type", "Column length", "PK", "Nullable", "Lob", "Field name", "Field type", "", "" };
		int[] columnWidths = new int[] { 40, 150, 100, 100, 50, 70, 60, 130, 130, 25, 25 };
		int[] columnAlignments = new int[] { SWT.CENTER, SWT.LEFT, SWT.LEFT, SWT.RIGHT, SWT.RIGHT, SWT.RIGHT, SWT.RIGHT, SWT.LEFT, SWT.LEFT, SWT.CENTER, SWT.CENTER };

		for (int i = 0; i < columnNames.length; i++) {
			TableViewerColumn column = new TableViewerColumn(fieldTableViewer, SWT.NONE);

			TableColumn tableColumn = column.getColumn();
			tableColumn.setText(columnNames[i]);
			tableColumn.setWidth(columnWidths[i]);
			tableColumn.setAlignment(columnAlignments[i]);

			if (i == 0) {
				tableColumn.setResizable(false);

				tableColumn.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						if (currentEntity != null) {
							currentEntity.setAllFieldSelection(!currentEntity.isAllFieldSelection());

							changeAllFieldSelection();
						}
					}
				});

				column.setEditingSupport(new FieldCreateEditingSupport(fieldTableViewer));
			}
			if (i == 1) {
				column.setEditingSupport(new FieldColumnNameEditingSupport(fieldTableViewer));
			}
			if (i == 2) {
				column.setEditingSupport(new FieldColumnTypeEditingSupport(fieldTableViewer));
			}
			if (i == 3) {
				column.setEditingSupport(new FieldColumnLengthEditingSupport(fieldTableViewer));
			}
			if (i == 4) {
				column.setEditingSupport(new FieldPkPositionEditingSupport(fieldTableViewer));
			}
			if (i == 5) {
				column.setEditingSupport(new FieldNullableEditingSupport(fieldTableViewer));
			}
			if (i == 6) {
				column.setEditingSupport(new FieldLobEditingSupport(fieldTableViewer));
			}
			if (i == 7) {
				column.setEditingSupport(new FieldNameEditingSupport(fieldTableViewer));
			}
			if (i == 8) {
				column.setEditingSupport(new FieldTypeEditingSupport(fieldTableViewer));
			}
			if (i == 9) {
				tableColumn.setResizable(false);
				column.setEditingSupport(new FieldPositionEditingSupport(fieldTableViewer, false));
			}
			if (i == 10) {
				tableColumn.setResizable(false);
				column.setEditingSupport(new FieldPositionEditingSupport(fieldTableViewer, true));
			}

			changeAllFieldSelection();
		}

		fieldTableViewer.setLabelProvider(new FieldTableLabelProvider());
		fieldTableViewer.setContentProvider(new ArrayContentProvider());

		gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.horizontalSpan = 3;
		gridData.heightHint = 150;
		fieldTable.setLayoutData(gridData);
	}

	private void createGenerationInputPart(final Composite contentComponent) {
		GridData gridData;

		Label outputDirLabel = new Label(contentComponent, SWT.LEFT);
		outputDirLabel.setText("Output files dir:");

		createOutputDirText(contentComponent);
		gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		outputDirText.setLayoutData(gridData);

		Button outputDirFileButton = new Button(contentComponent, SWT.BUTTON1);
		outputDirFileButton.setText("Browse...");

		outputDirFileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog directoryDialog = new DirectoryDialog(outputDirText.getShell(), SWT.OPEN | SWT.SELECTED);
				String selected = directoryDialog.open();
				outputDirText.setText(selected);
			}
		});

		Label projectPackageNameLabel = new Label(contentComponent, SWT.LEFT);
		projectPackageNameLabel.setText("Project package:");

		createPackageNameText(contentComponent);
		gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.horizontalSpan = 2;
		packageNameText.setLayoutData(gridData);
	}

	private void createDatabaseTypeCombo(final Composite contentComponent) {
		databaseTypeCombo = new Combo(contentComponent, SWT.READ_ONLY);
		databaseTypeCombo.setItems(ConnectionInformation.getDatabaseTypes());

		databaseTypeControlDecoration = new ControlDecoration(databaseTypeCombo, SWT.LEFT | SWT.TOP);
		databaseTypeControlDecoration.setImage(DEC_ERROR.getImage());

		databaseTypeValidator = new DatabaseTypeValidator();

		addModifyListener(contentComponent, databaseTypeCombo, databaseTypeControlDecoration, databaseTypeValidator);
		
		databaseTypeCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				IStatus status = databaseTypeValidator.validate(databaseTypeCombo.getText());
				if (status.isOK()) {
					String databaseType = databaseTypeCombo.getText();
					
					String defaultConnectionUrl = ConnectionInformation.getDefaultConnectionUrl(databaseType);
					String defaultDriverClassName = ConnectionInformation.getDefaultDriverClassName(databaseType);
					
					// String url = urlText.getText();
					// String driverClassName = driverClassNameText.getText();
					
					// boolean updateUrl = (url == null || url.length() == 0);
					// boolean updateDriverClassName = (driverClassName == null || driverClassName.length() == 0);
					
					boolean updateUrl = true;
					boolean updateDriverClassName = true;
					
					if (updateUrl) {
						if (defaultConnectionUrl != null) {
							urlText.setText(defaultConnectionUrl);
						}
					}
					if (updateDriverClassName) {
						if (defaultDriverClassName != null) {
							driverClassNameText.setText(defaultDriverClassName);
						}
					}
				}
			}
		});
	}

	private void createUrlText(final Composite contentComponent) {
		urlText = new Text(contentComponent, SWT.SINGLE | SWT.BORDER);

		urlControlDecoration = new ControlDecoration(urlText, SWT.LEFT | SWT.TOP);
		urlControlDecoration.setImage(DEC_ERROR.getImage());

		urlValidator = new UrlValidator();

		addModifyListener(contentComponent, urlText, urlControlDecoration, urlValidator);
	}

	private void createUserText(final Composite contentComponent) {
		userText = new Text(contentComponent, SWT.SINGLE | SWT.BORDER);

		userControlDecoration = new ControlDecoration(userText, SWT.LEFT | SWT.TOP);
		userControlDecoration.setImage(DEC_ERROR.getImage());

		userValidator = new UserValidator();

		addModifyListener(contentComponent, userText, userControlDecoration, userValidator);
	}

	private void createPasswordText(final Composite contentComponent) {
		passwordText = new Text(contentComponent, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);

		passwordControlDecoration = new ControlDecoration(passwordText, SWT.LEFT | SWT.TOP);
		passwordControlDecoration.setImage(DEC_ERROR.getImage());

		passwordValidator = new PasswordValidator();

		addModifyListener(contentComponent, passwordText, passwordControlDecoration, passwordValidator);
	}

	private void createDriverFileText(final Composite contentComponent) {
		driverFileText = new Text(contentComponent, SWT.SINGLE | SWT.BORDER);

		driverFileControlDecoration = new ControlDecoration(driverFileText, SWT.LEFT | SWT.TOP);
		driverFileControlDecoration.setImage(DEC_ERROR.getImage());

		driverFileValidator = new FileValidator();

		addModifyListener(contentComponent, driverFileText, driverFileControlDecoration, driverFileValidator);
	}

	private void createDriverClassNameText(final Composite contentComponent) {
		driverClassNameText = new Text(contentComponent, SWT.SINGLE | SWT.BORDER);

		driverClassNameControlDecoration = new ControlDecoration(driverClassNameText, SWT.LEFT | SWT.TOP);
		driverClassNameControlDecoration.setImage(DEC_ERROR.getImage());

		driverClassNameValidator = new ClassNameValidator();

		addModifyListener(contentComponent, driverClassNameText, driverClassNameControlDecoration, driverClassNameValidator);
	}

	private void createOutputDirText(final Composite contentComponent) {
		outputDirText = new Text(contentComponent, SWT.SINGLE | SWT.BORDER);

		outputDirControlDecoration = new ControlDecoration(outputDirText, SWT.LEFT | SWT.TOP);
		outputDirControlDecoration.setImage(DEC_ERROR.getImage());

		outputDirValidator = new DirectoryValidator();

		addModifyListener(contentComponent, outputDirText, outputDirControlDecoration, outputDirValidator);
	}

	private void createPackageNameText(final Composite contentComponent) {
		packageNameText = new Text(contentComponent, SWT.SINGLE | SWT.BORDER);

		packageNameControlDecoration = new ControlDecoration(packageNameText, SWT.LEFT | SWT.TOP);
		packageNameControlDecoration.setImage(DEC_ERROR.getImage());

		packageNameValidator = new PackageNameValidator();

		addModifyListener(contentComponent, packageNameText, packageNameControlDecoration, packageNameValidator);
	}

	private void addModifyListener(final Composite contentComponent, final Combo combo, final ControlDecoration controlDecoration, final IValidator validator) {
		Assert.isNotNull(combo);
		Assert.isNotNull(controlDecoration);
		Assert.isNotNull(validator);

		combo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				IStatus status = validator.validate(combo.getText());
				if (status.isOK()) {
					controlDecoration.hide();
				} else {
					controlDecoration.setDescriptionText(status.getMessage());
					controlDecoration.show();
				}
			}
		});
	}

	private void addModifyListener(final Composite contentComponent, final Text text, final ControlDecoration controlDecoration, final IValidator validator) {
		Assert.isNotNull(text);
		Assert.isNotNull(controlDecoration);
		Assert.isNotNull(validator);

		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				IStatus status = validator.validate(text.getText());
				if (status.isOK()) {
					controlDecoration.hide();
				} else {
					controlDecoration.setDescriptionText(status.getMessage());
					controlDecoration.show();
				}
			}
		});
	}

	private void createGenerationButtonPart(final Composite contentComponent) {
		GridData gridData;

		generationButton = new Button(contentComponent, SWT.PUSH);
		generationButton.setText("Generate source files");
		gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		gridData.horizontalSpan = 3;
		generationButton.setLayoutData(gridData);

		generationButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evnet) {
				if (!validateGenerationInformation()) {
					return;
				}

				try {
					contentComponent.setEnabled(false);
					
					Action GenerateFileAction = new GenerateFileAction(mappingData, MappingDataEditor.this, "Generate file...");
					GenerateFileAction.run();
				
					RefreshAction refreshAction = new RefreshAction(getEditorSite());
					refreshAction.refreshAll();
				} finally {
					contentComponent.setEnabled(true);
				}
			}
		});

	}

	private void bindValues() {
		dataBindingContext = new DataBindingContext();

		UpdateValueStrategy fileUpdateValueStrategy = new UpdateValueStrategy();
		fileUpdateValueStrategy.setConverter(new StringToFileConverter());

		ConnectionInformation connectionInformation = mappingData.getConnectionInformation();
		GenerationInformation generationInformation = mappingData.getGenerationInformation();

		dataBindingContext.bindValue(SWTObservables.observeSelection(databaseTypeCombo), BeansObservables.observeValue(connectionInformation, "databaseType"), null, null);
		dataBindingContext.bindValue(SWTObservables.observeText(urlText, SWT.Modify), BeansObservables.observeValue(connectionInformation, "url"), null, null);
		dataBindingContext.bindValue(SWTObservables.observeText(userText, SWT.Modify), BeansObservables.observeValue(connectionInformation, "user"), null, null);
		dataBindingContext.bindValue(SWTObservables.observeText(passwordText, SWT.Modify), BeansObservables.observeValue(connectionInformation, "password"), null, null);
		dataBindingContext.bindValue(SWTObservables.observeText(driverFileText, SWT.Modify), BeansObservables.observeValue(connectionInformation, "driverFile"), fileUpdateValueStrategy, null);
		dataBindingContext.bindValue(SWTObservables.observeText(driverClassNameText, SWT.Modify), BeansObservables.observeValue(connectionInformation, "driverClassName"), null, null);

		dataBindingContext.bindValue(SWTObservables.observeText(outputDirText, SWT.Modify), BeansObservables.observeValue(generationInformation, "outputDir"), fileUpdateValueStrategy, null);
		dataBindingContext.bindValue(SWTObservables.observeText(packageNameText, SWT.Modify), BeansObservables.observeValue(generationInformation, "packageName"), null, null);
	}

	private void changeAllEntitySelection() {
		TableColumn tableColumn = entityTableViewer.getTable().getColumn(0);
		tableColumn.setImage(allEntitySelection ? imageRegistry.get(CHECKED_IMAGE) : imageRegistry.get(UNCHECKED_IMAGE));

		List<Entity> entityList = mappingData.getEntityList();
		
		if (entityList != null) {
			for (Entity entity : entityList) {
				entity.setCreate(allEntitySelection);
			}

			entityTableViewer.setInput(entityList);
		}
	}

	private void changeAllFieldSelection() {
		TableColumn tableColumn = fieldTableViewer.getTable().getColumn(0);
		tableColumn.setImage(currentEntity != null && currentEntity.isAllFieldSelection() ? imageRegistry.get(CHECKED_IMAGE) : imageRegistry.get(UNCHECKED_IMAGE));

		if (currentEntity != null && currentEntity.getFieldList() != null) {
			for (Field field : currentEntity.getFieldList()) {
				field.setCreate(currentEntity.isAllFieldSelection());
			}

			fieldTableViewer.setInput(currentEntity.getFieldList());
		}
	}

	private boolean validateConnectionInformation() {
		IStatus status;

		status = databaseTypeValidator.validate(databaseTypeCombo.getText());
		if (!status.isOK()) {
			MessageDialog.openError(getSite().getShell(), "Invalid", status.getMessage());
			databaseTypeCombo.setFocus();
			return false;
		}

		status = urlValidator.validate(urlText.getText());
		if (!status.isOK()) {
			MessageDialog.openError(getSite().getShell(), "Invalid", status.getMessage());
			urlText.setFocus();
			return false;
		}

		status = userValidator.validate(userText.getText());
		if (!status.isOK()) {
			MessageDialog.openError(getSite().getShell(), "Invalid", status.getMessage());
			userText.setFocus();
			return false;
		}

		status = passwordValidator.validate(passwordText.getText());
		if (!status.isOK()) {
			MessageDialog.openError(getSite().getShell(), "Invalid", status.getMessage());
			passwordText.setFocus();
			return false;
		}

		status = driverFileValidator.validate(driverFileText.getText());
		if (!status.isOK()) {
			MessageDialog.openError(getSite().getShell(), "Invalid", status.getMessage());
			driverFileText.setFocus();
			return false;
		}

		status = driverClassNameValidator.validate(driverClassNameText.getText());
		if (!status.isOK()) {
			MessageDialog.openError(getSite().getShell(), "Invalid", status.getMessage());
			driverClassNameText.setFocus();
			return false;
		}

		return true;
	}

	private boolean validateGenerationInformation() {
		IStatus status;

		List<Entity> entityList = mappingData.getEntityList();
		
		boolean generate = false;
		if (entityList != null) {
			for (Entity entity : entityList) {
				if (entity.isCreate()) {
					generate = true;
					break;
				}
			}
		}

		if (!generate) {
			MessageDialog.openError(getSite().getShell(), "Invalid", "Select entities.");
			return false;
		}
		
		for (Entity entity : entityList) {
			if (entity.isCreate()) {
				boolean validEntity = true;
				
				if (validEntity && !entity.isValidTableName()) {
					MessageDialog.openError(getSite().getShell(), "Invalid", "Table name '" + entity.getTableName() + "' is invalid.");
					validEntity = false;
				}
				if (validEntity && !entity.isValidBaseName()) {
					MessageDialog.openError(getSite().getShell(), "Invalid", "Base name '" + entity.getBaseName() + "' is invalid.");
					validEntity = false;
				}
				
				if (!validEntity) {
					Table entityTable = entityTableViewer.getTable();

					TableItem[] tableItems = entityTable.getItems();
					for (int i = 0; i < tableItems.length; i++) {
						Entity tableEntity = (Entity) tableItems[i].getData();

						if (entity == tableEntity) {
							entityTable.setSelection(i);

							currentEntity = entityList.get(i);
							fieldTableViewer.setInput(currentEntity.getFieldList());
						}
					}

					return false;
				}
				
				if (entity.getPrimaryKeyFieldList().size() == 0) {
					MessageDialog.openError(getSite().getShell(), "Invalid", "Table " + entity.getTableName() + "'s primary key required.");

					Table entityTable = entityTableViewer.getTable();

					TableItem[] tableItems = entityTable.getItems();
					for (int i = 0; i < tableItems.length; i++) {
						Entity tableEntity = (Entity) tableItems[i].getData();

						if (entity == tableEntity) {
							entityTable.setSelection(i);

							currentEntity = entityList.get(i);
							fieldTableViewer.setInput(currentEntity.getFieldList());
						}
					}

					return false;
				}

				for (Entity e : entityList) {
					if ((entity != e) && (entity.isCreate() && e.isCreate()) && (entity.getBaseName().equals(e.getBaseName()))) {
						MessageDialog.openError(getSite().getShell(), "Invalid", "Duplicate base name.");

						Table entityTable = entityTableViewer.getTable();

						TableItem[] tableItems = entityTable.getItems();
						for (int i = 0; i < tableItems.length; i++) {
							Entity tableEntity = (Entity) tableItems[i].getData();

							if (entity == tableEntity) {
								entityTable.setSelection(i);

								currentEntity = entityList.get(i);
								fieldTableViewer.setInput(currentEntity.getFieldList());
							}
						}

						return false;
					}
				}

				for (Field field : entity.getFieldList()) {
					if (field.isCreate()) {
						boolean validField = true;
						
						if (validField && !field.isValidColumnName()) {
							MessageDialog.openError(getSite().getShell(), "Invalid", "Column name '" + field.getColumnName() + "' is invalid.");
							validField = false;
						}
						if (validField && !field.isValidColumnType()) {
							MessageDialog.openError(getSite().getShell(), "Invalid", "Column type '" + field.getColumnType() + "' is invalid.");
							validField = false;
						}
						if (validField && !field.isValidColumnLength()) {
							MessageDialog.openError(getSite().getShell(), "Invalid", "Column length '" + field.getColumnLength() + "' is invalid.");
							validField = false;							
						}
						if (validField && !field.isValidNullable()) {
							MessageDialog.openError(getSite().getShell(), "Invalid", "Nullable '" + field.isNullable() + "' is invalid.");
							validField = false;							
						}
						if (validField && !field.isValidFieldName()) {
							MessageDialog.openError(getSite().getShell(), "Invalid", "Field name '" + field.getFieldName() + "' is invalid.");
							validField = false;							
						}
						if (validField && !field.isValidFieldType()) {
							MessageDialog.openError(getSite().getShell(), "Invalid", "Field type '" + field.getFieldType() + "' is invalid.");
							validField = false;
						}

						if (!validField) {
							Table entityTable = entityTableViewer.getTable();
	
							TableItem[] tableItems = entityTable.getItems();
							for (int i = 0; i < tableItems.length; i++) {
								Entity tableEntity = (Entity) tableItems[i].getData();
	
								if (entity == tableEntity) {
									entityTable.setSelection(i);
	
									currentEntity = entityList.get(i);
									fieldTableViewer.setInput(currentEntity.getFieldList());
	
									Table fieldTable = fieldTableViewer.getTable();
	
									TableItem[] fieldTableItems = fieldTable.getItems();
									for (int j = 0; j < fieldTableItems.length; j++) {
										Field tableField = (Field) fieldTableItems[j].getData();
	
										if (field == tableField) {
											fieldTable.setSelection(j);
										}
									}
								}
							}
	
							return false;
						}
					}
				}

				if (entity.hasDuplicateFieldName()) {
					MessageDialog.openError(getSite().getShell(), "Invalid", "Duplicate field name.");

					Table entityTable = entityTableViewer.getTable();

					TableItem[] tableItems = entityTable.getItems();
					for (int i = 0; i < tableItems.length; i++) {
						Entity tableEntity = (Entity) tableItems[i].getData();

						if (entity == tableEntity) {
							entityTable.setSelection(i);

							currentEntity = entityList.get(i);
							fieldTableViewer.setInput(currentEntity.getFieldList());
						}
					}

					return false;
				}
			}
		}

		status = outputDirValidator.validate(outputDirText.getText());
		if (!status.isOK()) {
			MessageDialog.openError(getSite().getShell(), "Invalid", status.getMessage());
			outputDirText.setFocus();
			return false;
		}

		status = packageNameValidator.validate(packageNameText.getText());
		if (!status.isOK()) {
			MessageDialog.openError(getSite().getShell(), "Invalid", status.getMessage());
			packageNameText.setFocus();
			return false;
		}

		return true;
	}
	
	private void showEntityList(List<Entity> entityList) {
		if (entityList != null) {
			entityTableViewer.setInput(entityList.toArray());
		} else {
			entityTableViewer.setInput(null);
		}
		
		entityTableViewer.getTable().setSelection(-1);
		
		currentEntity = null;
		
		fieldTableViewer.setInput(null);
		changeAllFieldSelection();
	}

	public class DataModifyListener implements ValueModifyListener {
		public void valueModified() {
			dirty = true;
			firePropertyChange(PROP_DIRTY);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof DataModifyListener) {
				return true;
			}
			
			return false;
		}
	}

	@Override
	protected void createPages() {
		createMappingPage();
		createSourcePage();
	}
	
	public void createMappingPage() {
		Composite parent = getContainer();
		
		final ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		final Composite contentComponent = new Composite(scrolledComposite, SWT.NONE);
		scrolledComposite.setContent(contentComponent);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		contentComponent.setLayout(layout);
		
		createConnectionInputPart(contentComponent);
		createConnectionButtonPart(contentComponent);
		createEntityTable(contentComponent);
		createEntityButtonPart(contentComponent);
		createFieldTable(contentComponent);
		createFieldButtonPart(contentComponent);
		createGenerationInputPart(contentComponent);
		createGenerationButtonPart(contentComponent);

		contentComponent.pack();

		entityTableViewer.setInput(mappingData.getEntityList());
		
		bindValues();
		
		int index = addPage(scrolledComposite);
		setPageText(index, "Mapping");
	}
	
	public void createSourcePage() {
		try {
			textEditor = new TextEditor();
			int index = addPage(textEditor, getEditorInput());
			
			setPageText(index, "Source");
		} catch (PartInitException e) {
		}
	}
	
	@Override
	public boolean isDirty() {
		return dirty || super.isDirty();
	}
	
	@Override
	public void doSave(IProgressMonitor monitor) {
		if (getActivePage() == 0) {
			updateSourceFromMapping();
		}
		if (getActivePage() == 1) {
			updateMappingFromSource();
		}
		
		textEditor.doSave(monitor);
		
		dirty = false;
		firePropertyChange(PROP_DIRTY);
	}

	@Override
	public void doSaveAs() {
		textEditor.doSaveAs();
		
		setInput(textEditor.getEditorInput());
		setPartName(textEditor.getEditorInput().getName());
		
		dirty = false;
		firePropertyChange(PROP_DIRTY);
	}
	
	@Override
	public void setFocus() {
		if (getActivePage() == 0 && !isDirty()) {
			updateMappingFromSource();
		}
	}
	
	@Override
	protected void pageChange(int newPageIndex) {
		switch (newPageIndex) {
		case 0:
			if (isDirty()) {
				updateMappingFromSource();
			}
			break;
		case 1:
			if (isDirty()) {
				updateSourceFromMapping();
			}
			break;
		}
		
		super.pageChange(newPageIndex);
	}
	
	private void updateMappingFromSource() {
		String text = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput()).get();
		
		XmlData xmlData = new XmlData();
		
		mappingData = new MappingData();
		
		try {
			xmlData.loadFromXml(text);
			
			mappingData = xmlData.getMappingData();
		} catch (Exception e) {
			// MessageDialog.openError(getSite().getShell(), "Error - updateMappingFromSource", e.getMessage());
		}
		
		mappingData.addValueModifyListener(new DataModifyListener());

		bindValues();
		
		showEntityList(mappingData.getEntityList());
	}
	
	private void updateSourceFromMapping() {
		XmlData xmlData = new XmlData(mappingData);
		
		String text = "";
		
		try {
			text = xmlData.getXmlText();
		} catch (XmlDataException e) {
			// MessageDialog.openError(getSite().getShell(), "Error - updateSourceFromMapping", e.getMessage());
		}
		
		textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput()).set(text);
	}
	
}
