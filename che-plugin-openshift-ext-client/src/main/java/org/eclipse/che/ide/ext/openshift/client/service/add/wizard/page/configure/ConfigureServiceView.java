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

import com.google.inject.ImplementedBy;

import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.ext.openshift.shared.dto.Parameter;

import java.util.List;
import java.util.Map;

/**
 * This class is view representation of configurable page for datasource service
 *
 * @author Alexander Andrienko
 */
@ImplementedBy(ConfigureServiceViewImpl.class)
public interface ConfigureServiceView extends View<ConfigureServiceView.ActionDelegate> {

    void setEnvironmentVariables(List<Parameter> parameters);

    void setEnvironmentLabels(Map<String, String> labels);

    Map<String, String> getEnvironmentLabels();

    void setEnabled(boolean enabled);

    interface ActionDelegate {
    }
}
