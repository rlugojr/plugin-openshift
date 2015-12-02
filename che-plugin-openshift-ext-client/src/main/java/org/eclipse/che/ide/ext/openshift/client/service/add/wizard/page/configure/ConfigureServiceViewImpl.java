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

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.inject.Singleton;

import org.eclipse.che.ide.ext.openshift.client.OpenshiftLocalizationConstant;
import org.eclipse.che.ide.ext.openshift.client.OpenshiftResources;
import org.eclipse.che.ide.ext.openshift.shared.dto.Parameter;
import org.eclipse.che.ide.ui.cellview.CellTableResources;
import org.eclipse.che.ide.ui.window.Window;

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

    private ActionDelegate delegate;
    private ListDataProvider<Parameter> variablesProvider;
    private CellTable<Parameter> envVariablesTable;
    private ListDataProvider<Map<String, String>> labelsProvider;
    private CellTable<Map<String, String>> labelsTabel;

    @Inject
    public ConfigureServiceViewImpl(CellTableResources cellTableResources,
                                    OpenshiftLocalizationConstant locale,
                                    OpenshiftResources resources,
                                    ConfigureServiceViewUiBinder uiBinder) {
        this.resources = resources;
        this.locale = locale;

        ensureDebugId("create-service");
        setTitle("todo");//todo
        setWidget(uiBinder.createAndBindUi(this));

        //todo
        variablesProvider = new ListDataProvider<>();
        envVariablesTable = createVariablesCellTable(cellTableResources, variablesProvider);
        envVariablesPanel.add(envVariablesTable);

        labelsProvider = new ListDataProvider<>();
        labelsTabel = createLabelsTable(cellTableResources, labelsProvider);
        labelsPanel.add(labelsTabel);
    }

    private CellTable<Parameter> createVariablesCellTable(CellTableResources tableResources,
                                                          final ListDataProvider<Parameter> dataProvider) {
        envVariablesTable = new CellTable<>(50, tableResources);
        envVariablesTable.setTableLayoutFixed(true);
        //envVariablesTable.setWidth("100%");
        //envVariablesTable.addStyleDependentName(resources.css().);
        envVariablesTable.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.DISABLED);
        dataProvider.addDataDisplay(envVariablesTable);

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
        };
        valueColumn.setFieldUpdater(new FieldUpdater<Parameter, String>() {
            @Override
            public void update(int index, Parameter parameter, String value) {
                parameter.setValue(value);
            }
        });

        envVariablesTable.addColumn(nameColumn);
        envVariablesTable.setColumnWidth(nameColumn, 35, Style.Unit.PCT);
        envVariablesTable.addColumn(valueColumn);
        envVariablesTable.setColumnWidth(valueColumn, 35, Style.Unit.PCT);
        return envVariablesTable;
    }

    private CellTable<Map<String, String>> createLabelsTable(CellTableResources tableResources,
                                                             ListDataProvider<Map<String, String>> dataProvider) {
        labelsTabel = new CellTable<>(50, tableResources);
        //labelsTabel.
        return null;
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
        variablesProvider.setList(parameters);
        variablesProvider.refresh();
    }

    @Override
    public void setEnvironmentLabels(Map<String, String> labels) {

    }
}
