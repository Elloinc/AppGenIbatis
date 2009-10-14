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
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.appgen.action.GenerateFileAction;
import net.sourceforge.appgen.action.SaveMappingAction;
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
import net.sourceforge.appgen.support.FieldCreateEditingSupport;
import net.sourceforge.appgen.support.FieldNameEditingSupport;
import net.sourceforge.appgen.support.FieldPkPositionEditingSupport;
import net.sourceforge.appgen.support.FieldPositionEditingSupport;
import net.sourceforge.appgen.support.FieldTableLabelProvider;
import net.sourceforge.appgen.support.FieldTypeEditingSupport;
import net.sourceforge.appgen.xml.XmlData;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
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
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

/**
 * Mpping file editor.
 * 
 * @author Byeongkil Woo
 */
public class MappingDataEditor extends EditorPart {

	private static final FieldDecoration DEC_ERROR = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);

	private static ImageRegistry imageRegistry = new ImageRegistry();

	public static final String CHECKED_IMAGE = "header_checked";
	public static final String UNCHECKED_IMAGE = "header_unchecked";

	static {
		String iconPath = "icon/";
		imageRegistry.put(CHECKED_IMAGE, ImageDescriptor.createFromFile(MappingDataEditor.class, iconPath + CHECKED_IMAGE + ".gif"));
		imageRegistry.put(UNCHECKED_IMAGE, ImageDescriptor.createFromFile(MappingDataEditor.class, iconPath + UNCHECKED_IMAGE + ".gif"));
	}

	MappingData loadMappingData;
	
	MappingData mappingData;
	
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

	private DataBindingContext dataBindingContext;
	
	private boolean dirty;

	public MappingDataEditor() {
		super();
		
		mappingData = new MappingData(new ConnectionInformation(), new GenerationInformation(), new ArrayList<Entity>());
		
		consoleManager = ConsolePlugin.getDefault().getConsoleManager();
		console = new MessageConsole("Entity generator", null);
		stream = console.newMessageStream();
		consoleManager.addConsoles(new IConsole[] { console });
		consoleManager.showConsoleView(console);
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		
		setPartName(input.getName());
		
		loadData();
	}
	
	@Override
	public void dispose() {
		super.dispose();

		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
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
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public void createPartControl(Composite parent) {
		final ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		final Composite contentComponent = new Composite(scrolledComposite, SWT.NONE);
		scrolledComposite.setContent(contentComponent);

		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		contentComponent.setLayout(layout);

		createConnectionInputPart(contentComponent);
		createConnectionButtonPart(contentComponent);
		createEntityTable(contentComponent);
		createFieldTable(contentComponent);
		createGenerationInputPart(contentComponent);
		createGenerationButtonPart(contentComponent);

		contentComponent.pack();

		entityTableViewer.setInput(mappingData.getEntityList());
		
		bindValues();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		XmlData xmlData = new XmlData(mappingData);
		FileEditorInput fileEditorInput = (FileEditorInput) getEditorInput();
		URI uri = fileEditorInput.getURI();
		File file = new File(uri);
		
		SaveMappingAction action = new SaveMappingAction(file, xmlData);
		
		try {
			action.run();
			
			dirty = false;
			firePropertyChange(PROP_DIRTY);
		} catch (Exception e) {
			MessageDialog.openError(getSite().getShell(), "error", e.getMessage());
		}
	}

	@Override
	public void doSaveAs() {
		SaveAsDialog saveAsDialog = new SaveAsDialog(getSite().getWorkbenchWindow().getShell());
		saveAsDialog.setOriginalFile(((FileEditorInput) getEditorInput()).getFile());
		saveAsDialog.open();
		
		IPath path = saveAsDialog.getResult();
		
		if (path != null) {
			IPath rootPath = ResourcesPlugin.getWorkspace().getRoot().getLocation();
			File rootFile = rootPath.toFile();
			File saveAsFile = new File(rootFile, path.toPortableString());
			
			XmlData xmlData = new XmlData(mappingData);
			
			SaveMappingAction action = new SaveMappingAction(saveAsFile, xmlData);
			
			try {
				action.run();
				
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
				FileEditorInput input = new FileEditorInput(file);
				setInputWithNotify(new FileEditorInput(file));
				setPartName(input.getName());
				
				dirty = false;
				firePropertyChange(PROP_DIRTY);
				
				RefreshAction refreshAction = new RefreshAction(getEditorSite());
				refreshAction.refreshAll();
			} catch (Exception e) {
				MessageDialog.openError(getSite().getShell(), "error", e.getMessage());
			}
		}
	}

	@Override
	public void setFocus() {
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}
	
	public void loadData() {
		FileEditorInput fileEditorInput = (FileEditorInput) super.getEditorInput();
		File file = new File(fileEditorInput.getURI());
		
		XmlData xmlData = new XmlData();
		
		try {
			xmlData.loadFromXml(file);
			
			mappingData = xmlData.getMappingData();
			mappingData.addValueModifyListener(new DataModifyListener());
		} catch (Exception e) {
			MessageDialog.openError(getSite().getShell(), "error", e.getMessage());
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

				try {
					contentComponent.setEnabled(false);

					JdbcConnector connector = JdbcConnectorFactory.createConnector(connectionInformation);

					try {
						List<Entity> entityList = connector.getEntityList();
						
						if (entityList != null) {
							for (Entity entity : entityList) {
								entity.addValueModifyListener(new DataModifyListener());
							}
						}
						
						mappingData.setEntityList(entityList);
						showEntityList(contentComponent, mappingData.getEntityList());
						
						dirty = true;
						firePropertyChange(PROP_DIRTY);
					} catch (Exception e) {
						mappingData.setEntityList(null);
						MessageDialog.openError(getSite().getShell(), "error", e.getMessage());
					}
				} finally {
					contentComponent.setEnabled(true);
				}
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
			if (i == 2) {
				column.setEditingSupport(new EntityBaseNameEditingSupport(entityTableViewer));
			}
		}
		
		entityTableViewer.setLabelProvider(new EntityTableLabelProvider());
		entityTableViewer.setContentProvider(new ArrayContentProvider());

		gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.horizontalSpan = 3;
		gridData.heightHint = 150;
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

		String[] columnNames = new String[] { "", "Column name", "Column type", "Field name", "Field type", "PK", "", "" };
		int[] columnWidths = new int[] { 40, 160, 115, 130, 130, 50, 25, 25 };
		int[] columnAlignments = new int[] { SWT.CENTER, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.RIGHT, SWT.CENTER, SWT.CENTER };

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
			if (i == 3) {
				column.setEditingSupport(new FieldNameEditingSupport(fieldTableViewer));
			}
			if (i == 4) {
				column.setEditingSupport(new FieldTypeEditingSupport(fieldTableViewer));
			}
			if (i == 5) {
				column.setEditingSupport(new FieldPkPositionEditingSupport(fieldTableViewer));
			}
			if (i == 6) {
				tableColumn.setResizable(false);
				column.setEditingSupport(new FieldPositionEditingSupport(fieldTableViewer, false));
			}
			if (i == 7) {
				tableColumn.setResizable(false);
				column.setEditingSupport(new FieldPositionEditingSupport(fieldTableViewer, true));
			}

			changeAllFieldSelection();
		}

		fieldTableViewer.setLabelProvider(new FieldTableLabelProvider());
		fieldTableViewer.setContentProvider(new ArrayContentProvider());

		gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.horizontalSpan = 3;
		gridData.heightHint = 200;
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
		
		for (Entity entity : entityList) {
			if (entity.isCreate()) {
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

				if (!entity.isValidBaseName()) {
					MessageDialog.openError(getSite().getShell(), "Invalid", "Base name '" + entity.getBaseName() + "' is invalid.");

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
						if (!field.isValidFieldName() || !field.isValidFieldType()) {
							if (!field.isValidFieldName()) {
								MessageDialog.openError(getSite().getShell(), "Invalid", "Field name '" + field.getFieldName() + "' is invalid.");
							} else {
								MessageDialog.openError(getSite().getShell(), "Invalid", "Field type '" + field.getFieldType() + "' is invalid.");
							}

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

		return true;
	}
	
	private void showEntityList(Composite parent, List<Entity> entityList) {
		if (entityList != null) {
			entityTableViewer.setInput(entityList.toArray());
		} else {
			entityTableViewer.setInput(null);
		}

		currentEntity = null;
		fieldTableViewer.setInput(null);
		changeAllFieldSelection();
	}

	/*
	private void openError(String title, String message) {
		MessageDialog.openError(null, title, message);
	}
	*/

	public class DataModifyListener implements ValueModifyListener {
		public void valueModified() {
			dirty = true;
			firePropertyChange(PROP_DIRTY);
		}
	}
	
}
