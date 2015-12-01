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

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Singleton;

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
    }

    //        Service service = dataObject.getService();
//        service.setApiVersion(template.getApiVersion());
//        service.setKind("Service");

    //String name = template.getMetadata().getName();
    //dataObject.getMetadata().setName(name);

//        Map<String, String> annotation = template.getMetadata().getAnnotations();
//        dataObject.getMetadata().setAnnotations(annotation);

//        for (Object object: template.getObjects()) {
//            JSONObject obj = (JSONObject)object;
//            ServiceSpec spec = dtoFactory.createDtoFromJson(obj.get("spec").toString(), ServiceSpec.class);
//            if (spec != null) {
//                Log.info(getClass(), "spac found " + spec.toString());
//                service.setSpec(spec);
//            }
//        }
// dataObject  template.getParameters() ?

}
