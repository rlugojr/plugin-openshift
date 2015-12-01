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

import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Singleton;

import org.eclipse.che.ide.ext.openshift.client.OpenshiftLocalizationConstant;
import org.eclipse.che.ide.ext.openshift.client.OpenshiftResources;
import org.eclipse.che.ide.ext.openshift.client.deploy._new.KeyValue;
import org.eclipse.che.ide.ui.window.Window;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class ConfigureServiceViewImpl extends Window implements ConfigureServiceView {

    interface ConfigureServiceViewUiBinder extends UiBinder<DockLayoutPanel, ConfigureServiceViewImpl> {
    }

    @UiField(provided = true)
    OpenshiftResources resources;

    @UiField(provided = true)
    OpenshiftLocalizationConstant locale;

    @UiField
    CellTable<KeyValue> environmentVariables;

    @UiField
    CellTable<KeyValue> environmentLabels;

    private ActionDelegate delegate;
    private List<KeyValue> variablesList;
    private List<KeyValue> environmentList;

    @Inject
    public ConfigureServiceViewImpl(OpenshiftLocalizationConstant locale,
                                    OpenshiftResources resources,
                                    ConfigureServiceViewUiBinder uiBinder) {
        this.resources = resources;
        this.locale = locale;

        ensureDebugId("create-service");
        setWidget(uiBinder.createAndBindUi(this));

        variablesList = new ArrayList<KeyValue>();
        environmentList = new ArrayList<KeyValue>();
    }

    private void createCellTable() {

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
    public void setEnvironmentVariables(List<KeyValue> keyValueList) {
        variablesList = keyValueList;
    }

    @Override
    public void setEnvironmentLabels(List<KeyValue> keyValueList) {
        environmentList = keyValueList;
    }
}
