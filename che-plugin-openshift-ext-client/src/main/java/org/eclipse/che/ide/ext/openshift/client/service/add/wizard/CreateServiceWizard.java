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
package org.eclipse.che.ide.ext.openshift.client.service.add.wizard;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.eclipse.che.ide.api.wizard.AbstractWizard;
import org.eclipse.che.ide.ext.openshift.client.dto.NewServiceRequest;
import org.eclipse.che.ide.ext.openshift.shared.dto.Service;

import javax.validation.constraints.NotNull;

/**
 * //
 *
 * @author Alexander Andrienko
 */
//todo maybe we can do it like singleton ?
public class CreateServiceWizard extends AbstractWizard<NewServiceRequest> {

    @Inject
    public CreateServiceWizard(@Assisted NewServiceRequest newServiceRequest) {
        super(newServiceRequest);
    }

    @Override
    public void complete(@NotNull CompleteCallback callback) {
        //todo
    }
}
