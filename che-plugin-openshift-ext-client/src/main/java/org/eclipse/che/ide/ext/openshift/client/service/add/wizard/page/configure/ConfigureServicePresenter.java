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

import org.eclipse.che.ide.api.wizard.AbstractWizardPage;
import org.eclipse.che.ide.ext.openshift.client.dto.NewServiceRequest;
import org.eclipse.che.ide.ext.openshift.shared.dto.Parameter;
import org.eclipse.che.ide.ext.openshift.shared.dto.Template;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Singleton;

import java.util.List;
import java.util.Map;

/**
 * Presenter for confederate datasource service
 *
 * @author Andrienko Alexander
 */
@Singleton
public class ConfigureServicePresenter extends AbstractWizardPage<NewServiceRequest> implements ConfigureServiceView.ActionDelegate {

    private final ConfigureServiceView view;

    @Inject
    public ConfigureServicePresenter(ConfigureServiceView view) {
        this.view = view;

        view.setDelegate(this);
    }

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);

        Template template = dataObject.getTemplate();
        List<Parameter> parameterList = template.getParameters();
        Map<String, String> labels = template.getLabels();
        view.setEnvironmentVariables(parameterList);
        view.setEnvironmentLabels(labels);
    }

    /**
     * Update labels in data object
     */
    public void updateData() {
        Template template = dataObject.getTemplate();
        template.setLabels(view.getEnvironmentLabels());
    }

}
