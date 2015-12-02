/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.ext.openshift.client.service.add.wizard.page.configure;

import javax.inject.Inject;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.inject.Singleton;

import org.eclipse.che.ide.ext.openshift.client.OpenshiftLocalizationConstant;
import org.eclipse.che.ide.ext.openshift.client.OpenshiftResources;
import org.eclipse.che.ide.ext.openshift.client.deploy._new.KeyValue;
import org.eclipse.che.ide.ext.openshift.shared.dto.Parameter;
import org.eclipse.che.ide.ui.cellview.CellTableResources;
import org.eclipse.che.ide.ui.window.Window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class ConfigureServiceViewImpl extends Window implements ConfigureServiceView {

    interface ConfigureServiceViewUiBinder extends UiBinder<DockLayoutPanel, ConfigureServiceViewImpl> {
    }

    @UiField(provided = true)
    final OpenshiftResources resources;

    @UiField(provided = true)
    final OpenshiftLocalizationConstant locale;

    @UiField
    FlowPanel envVariablesPanel;

    @UiField
    FlowPanel labelsPanel;

    @UiField
    Button    addLabelButton;

    private ActionDelegate              delegate;

    private ListDataProvider<Parameter> envVariablesProvider;
    private CellTable<Parameter>        envVariablesTable;

    private ListDataProvider<KeyValue> labelsProvider;
    private CellTable<KeyValue>        labelsTable;

    @Inject
    public ConfigureServiceViewImpl(CellTableResources cellTableResources,
                                    OpenshiftLocalizationConstant locale,
                                    OpenshiftResources resources,
                                    ConfigureServiceViewUiBinder uiBinder) {
        this.resources = resources;
        this.locale = locale;

        ensureDebugId("create-service");
        setWidget(uiBinder.createAndBindUi(this));

        //todo
        envVariablesProvider = new ListDataProvider<>();
        envVariablesTable = createVariablesCellTable(cellTableResources);
        envVariablesPanel.add(envVariablesTable);

        labelsProvider = new ListDataProvider<>();
        labelsTable = createLabelsTable(cellTableResources);
        labelsPanel.add(labelsTable);
    }

    private CellTable<Parameter> createVariablesCellTable(CellTableResources tableResources) {
        envVariablesTable = new CellTable<>(50, tableResources);
        envVariablesTable.setTableLayoutFixed(true);
        envVariablesTable.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);
        envVariablesProvider.addDataDisplay(envVariablesTable);

        Column<Parameter, String> nameColumn = new Column<Parameter, String>(new TextCell()) {
            @Override
            public String getValue(Parameter parameter) {
                return parameter.getName();
            }
        };

        Column<Parameter, String> valueColumn = new Column<Parameter, String>(new TextInputCell()) {
            @Override
            public String getValue(Parameter parameter) {
                return parameter.getValue();
            }

//            @Override
//            public void render(Cell.Context context, Parameter parameter, SafeHtmlBuilder sb) {
//                if (parameter.getName().isEmpty()) {
//                    Log.info(getClass(), "***" + sb.toString());
//                }
//                super.render(context, parameter, sb);
//            }
        };
        valueColumn.setFieldUpdater(new FieldUpdater<Parameter, String>() {
            @Override
            public void update(int index, Parameter parameter, String value) {
                if (!value.isEmpty()) {
                    parameter.setValue(value);
                }
            }
        });

        envVariablesTable.addColumn(nameColumn);
        envVariablesTable.setColumnWidth(nameColumn, 20, Style.Unit.PCT);
        envVariablesTable.addColumn(valueColumn);
        envVariablesTable.setColumnWidth(valueColumn, 20, Style.Unit.PCT);
        return envVariablesTable;
    }

    private CellTable<KeyValue> createLabelsTable(CellTableResources tableResources) {
        labelsTable = new CellTable<>(50, tableResources);
        labelsTable.setTableLayoutFixed(true);
        labelsTable.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);
        labelsProvider.addDataDisplay(labelsTable);

        final Column<KeyValue, String> keyColumn = new Column<KeyValue, String>(new TextInputCell()) {
            @Override
            public String getValue(KeyValue keyValue) {
                return keyValue.getKey();
            }
        };
        keyColumn.setFieldUpdater(new FieldUpdater<KeyValue, String>() {
            @Override
            public void update(int index, KeyValue keyValue, String value) {
                if (!value.isEmpty()) {
                    keyValue.setKey(value);
                }
            }
        });

        Column<KeyValue, String> valueColumn = new Column<KeyValue, String>(new TextInputCell()) {
            @Override
            public String getValue(KeyValue keyValue) {
                return keyValue.getValue();
            }
        };
        valueColumn.setFieldUpdater(new FieldUpdater<KeyValue, String>() {
            @Override
            public void update(int index, KeyValue keyValue, String value) {
                if (!value.isEmpty()) {
                    keyValue.setValue(value);
                }
            }
        });

        Column<KeyValue, String> removeColumn = new Column<KeyValue, String>(new ButtonCell()) {
            @Override
            public String getValue(KeyValue value) {
                return "-";
            }

            @Override
            public void render(Cell.Context context, KeyValue keyValue, SafeHtmlBuilder sb) {
                Button removeButton = new Button();
                super.render(context, keyValue, sb.appendHtmlConstant(removeButton.getHTML()));
            }
        };
        removeColumn.setFieldUpdater(new FieldUpdater<KeyValue, String>() {
            @Override
            public void update(int index, KeyValue keyValue, String value) {
                labelsProvider.getList().remove(keyValue);
            }
        });

        labelsTable.addColumn(keyColumn);
        labelsTable.setColumnWidth(keyColumn, 35, Style.Unit.PCT);
        labelsTable.addColumn(valueColumn);
        labelsTable.setColumnWidth(valueColumn, 35, Style.Unit.PCT);
        labelsTable.addColumn(removeColumn);
        labelsTable.setColumnWidth(removeColumn, 5, Style.Unit.PCT);

        return labelsTable;
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public Widget asWidget() {
        return getWidget();
    }

    @Override
    public void setEnvironmentVariables(List<Parameter> parameters) {
        envVariablesProvider.setList(parameters);
        envVariablesProvider.refresh();
    }

    @Override
    public void setEnvironmentLabels(Map<String, String> labels) {
        List<KeyValue> list = new ArrayList<>();
        for (Map.Entry<String, String> map : labels.entrySet()) {
            list.add(new KeyValue(map.getKey(), map.getValue()));
        }
        labelsProvider.setList(list);
        labelsProvider.refresh();
    }

    @Override
    public Map<String, String> getEnvironmentLabels() {
        Map<String, String> mapLabels = new HashMap<>();
        for(KeyValue keyValue: labelsProvider.getList()) {
            mapLabels.put(keyValue.getKey(), keyValue.getValue());
        }
        return mapLabels;
    }

    @UiHandler("addLabelButton")
    public void onAddLabelButtonClicked(ClickEvent clickEvent) {
        labelsProvider.getList().add(new KeyValue("", ""));
    }
}
