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
import javax.validation.constraints.NotNull;

import org.eclipse.che.ide.api.wizard.AbstractWizardPage;
import org.eclipse.che.ide.api.wizard.Wizard;
import org.eclipse.che.ide.ext.openshift.client.dto.NewServiceRequest;
import org.eclipse.che.ide.ext.openshift.shared.dto.Parameter;
import org.eclipse.che.ide.ext.openshift.shared.dto.Template;
import org.eclipse.che.ide.util.loging.Log;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Singleton;

import java.util.List;
import java.util.Map;

@Singleton
public class ConfigureServicePresenter extends AbstractWizardPage<NewServiceRequest> implements ConfigureServiceView.ActionDelegate {

    private final ConfigureServiceView view;

    @Inject
    public ConfigureServicePresenter(ConfigureServiceView view) {
        this.view = view;

        view.setDelegate(this);
    }

    @Override
    public boolean isCompleted() {
        Log.info(getClass(), "isCompleted");
        return super.isCompleted();//todo
    }

    @Override
    public void setUpdateDelegate(@NotNull Wizard.UpdateDelegate delegate) {
        Log.info(getClass(), "update delegate");
//
        super.setUpdateDelegate(delegate);
    }

    @Override
    public void init(NewServiceRequest dataObject) {
        super.init(dataObject);
//
        Log.info(getClass(), "init");
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

    //    Service service = dataObject.getService();
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
