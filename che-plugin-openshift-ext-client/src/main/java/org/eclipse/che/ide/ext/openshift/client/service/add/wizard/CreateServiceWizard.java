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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.eclipse.che.api.promises.client.Function;
import org.eclipse.che.api.promises.client.FunctionException;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.api.promises.client.js.Promises;
import org.eclipse.che.api.workspace.shared.dto.ProjectConfigDto;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.wizard.AbstractWizard;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.ext.openshift.client.OpenshiftServiceClient;
import org.eclipse.che.ide.ext.openshift.client.dto.NewServiceRequest;
import org.eclipse.che.ide.ext.openshift.shared.dto.BuildConfig;
import org.eclipse.che.ide.ext.openshift.shared.dto.DeploymentConfig;
import org.eclipse.che.ide.ext.openshift.shared.dto.Parameter;
import org.eclipse.che.ide.ext.openshift.shared.dto.Service;
import org.eclipse.che.ide.ext.openshift.shared.dto.Template;
import org.eclipse.che.ide.util.loging.Log;

import javax.validation.constraints.NotNull;

import static org.eclipse.che.ide.ext.openshift.shared.OpenshiftProjectTypeConstants.OPENSHIFT_NAMESPACE_VARIABLE_NAME;
import static org.eclipse.che.ide.ext.openshift.shared.OpenshiftProjectTypeConstants.OPENSHIFT_APPLICATION_VARIABLE_NAME;;

/**
 * //
 *
 * @author Alexander Andrienko
 */
//todo maybe we can do it like singleton ? but hot this is create inside factory ....
public class CreateServiceWizard extends AbstractWizard<NewServiceRequest> {

    private final OpenshiftServiceClient openshiftServiceClient;
    private final AppContext appContext;
    private final NotificationManager notificationManager;
    private final DtoFactory dtoFactory;
    
    private String nameSpace;

    @Inject
    public CreateServiceWizard(@Assisted NewServiceRequest newServiceRequest,
                               OpenshiftServiceClient openshiftServiceClient, 
                               AppContext appContext,
                               NotificationManager notificationManager,
                               DtoFactory dtoFactory) {
        super(newServiceRequest);
        this.openshiftServiceClient = openshiftServiceClient;
        this.appContext = appContext;
        this.notificationManager = notificationManager;
        this.dtoFactory = dtoFactory;
    }

    @Override
    public void complete(@NotNull CompleteCallback callback) {
        ProjectConfigDto projectConfig = appContext.getCurrentProject().getRootProject();
        nameSpace = getAttributeValue(projectConfig, OPENSHIFT_NAMESPACE_VARIABLE_NAME);
        
        Template template = dataObject.getTemplate();
        Log.info(getClass(), template.toString());
        
        openshiftServiceClient.processTemplate(nameSpace, template)
                              .thenPromise(processTemplate())
                              .catchError(handleError(nameSpace));
    }    
    
     private String getAttributeValue(ProjectConfigDto projectConfig, String value) {
        List<String> attributes = projectConfig.getAttributes().get(value);
        if (attributes == null || attributes.isEmpty()) {
            return null;
        }
        return projectConfig.getAttributes().get(value).get(0);
    }
    
    private Operation<PromiseError> handleError(final String nameSpace) {
        return new Operation<PromiseError>() {
            @Override
            public void apply(PromiseError promiseError) throws OperationException {
                notificationManager.showError("" + promiseError.getMessage());//TODO need message!!!!
            }
        };
    }

    private Function<Template, Promise<JsArrayMixed>> processTemplate() {
        return new Function<Template, Promise<JsArrayMixed>>() {
            @Override
            public Promise<JsArrayMixed> apply(Template template) throws FunctionException {
                List<Promise<?>> promiseList = new ArrayList<>();
                for (Object object: template.getObjects()) {
                    JSONObject json = (JSONObject)object;
                    String kind = ((JSONString)json.get("kind")).stringValue();
                    switch (kind) {
                        case "Service":
                            Service service = dtoFactory.createDtoFromJson(json.toString(), Service.class);
                            promiseList.add(openshiftServiceClient.createService(nameSpace, service));
                            break;
                        case "DeploymentConfig":
                            DeploymentConfig deploymentConfig = dtoFactory.createDtoFromJson(json.toString(), DeploymentConfig.class);
                            promiseList.add(openshiftServiceClient.createDeploymentConfig(nameSpace, deploymentConfig));
                            break;
                    }
                }
                Promise<?>[] promises = promiseList.toArray(new Promise<?>[promiseList.size()]);
                return Promises.all(promises);
            }
        };
    }
}
