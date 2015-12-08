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

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
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
import org.eclipse.che.ide.api.wizard.AbstractWizard;
import org.eclipse.che.ide.api.wizard.WizardPage;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.ext.openshift.client.OpenshiftServiceClient;
import org.eclipse.che.ide.ext.openshift.client.dto.NewServiceRequest;
import org.eclipse.che.ide.ext.openshift.shared.dto.Container;
import org.eclipse.che.ide.ext.openshift.shared.dto.DeploymentConfig;
import org.eclipse.che.ide.ext.openshift.shared.dto.EnvVar;
import org.eclipse.che.ide.ext.openshift.shared.dto.Service;
import org.eclipse.che.ide.ext.openshift.shared.dto.Template;

import javax.validation.constraints.NotNull;

import static org.eclipse.che.ide.ext.openshift.shared.OpenshiftProjectTypeConstants.OPENSHIFT_NAMESPACE_VARIABLE_NAME;

/**
 * Create datasource service wizard
 *
 * @author Alexander Andrienko
 */
public class CreateServiceWizard extends AbstractWizard<NewServiceRequest> {

    private final OpenshiftServiceClient client;
    private final AppContext                    appContext;
    private final DtoFactory                    dtoFactory;

    private String                              nameSpace;

    @Inject
    public CreateServiceWizard(@Assisted NewServiceRequest newServiceRequest,
                               OpenshiftServiceClient client,
                               AppContext appContext,
                               DtoFactory dtoFactory) {
        super(newServiceRequest);
        this.client = client;
        this.appContext = appContext;
        this.dtoFactory = dtoFactory;
    }

    public WizardPage<NewServiceRequest> getFirstPage() {
        return wizardPages.get(0);
    }

    @Override
    public void complete(@NotNull CompleteCallback callback) {
        
        ProjectConfigDto projectConfig = appContext.getCurrentProject().getRootProject();
        nameSpace = getAttributeValue(projectConfig, OPENSHIFT_NAMESPACE_VARIABLE_NAME);

        Template template = dataObject.getTemplate();

        client.processTemplate(nameSpace, template).then(processTemplate())
                                                   .then(onSuccess(callback))
                                                   .catchError(handleError(callback));
    }
    
     private String getAttributeValue(ProjectConfigDto projectConfig, String value) {
        List<String> attributes = projectConfig.getAttributes().get(value);
        if (attributes == null || attributes.isEmpty()) {
            return null;
        }
        return projectConfig.getAttributes().get(value).get(0);
    }

    private Function<Template, Promise<JsArrayMixed>> processTemplate() {
        return new Function<Template, Promise<JsArrayMixed>>() {
            @Override
            public Promise<JsArrayMixed> apply(Template template) throws FunctionException {
                List<Promise<?>> promiseList = new ArrayList<>();
                for (Object object : template.getObjects()) {
                    JSONObject json = (JSONObject)object;
                    String kind = ((JSONString)json.get("kind")).stringValue();
                    switch (kind) {
                        case "Service":
                            Service service = dtoFactory.createDtoFromJson(json.toString(), Service.class);
                            service.getMetadata().setNamespace(nameSpace);
                            promiseList.add(client.createService(service));
                            break;
                        case "DeploymentConfig":
                            final DeploymentConfig deploymentConfig = dtoFactory.createDtoFromJson(json.toString(), DeploymentConfig.class);
                            deploymentConfig.getMetadata().setNamespace(nameSpace);
                            Promise<JsArrayMixed> p = client.createDeploymentConfig(deploymentConfig)
                                                            .thenPromise(getApplicationDeploymentConfigs());
                            promiseList.add(p);
                            break;
                    }
                }
                Promise<?>[] promises = promiseList.toArray(new Promise<?>[promiseList.size()]);
                return Promises.all(promises);
            }
        };
    }

    private Function<DeploymentConfig, Promise<JsArrayMixed>> getApplicationDeploymentConfigs() {
        return new Function<DeploymentConfig, Promise<JsArrayMixed>>() {
            @Override
            public Promise<JsArrayMixed> apply(final DeploymentConfig serviceConfig) throws FunctionException {
                String applicationName = appContext.getCurrentProject().getRootProject().getName();
                return client.getDeploymentConfigs(nameSpace, applicationName)
                             .then(new Function<List<DeploymentConfig>, List<DeploymentConfig>>() {
                                 @Override
                                 public List<DeploymentConfig> apply(List<DeploymentConfig> configs) throws FunctionException {
                                     final String deploymentConfigName = serviceConfig.getMetadata().getName();
                                     final List<Container> containersForUpdate = serviceConfig.getSpec().getTemplate().getSpec()
                                             .getContainers();
                                     for (DeploymentConfig config : configs) {
                                         List<Container> containers = config.getSpec().getTemplate().getSpec().getContainers();

                                         if (containers == null) {
                                             config.getSpec().getTemplate().getSpec().setContainers(containersForUpdate);
                                             continue;//todo check it again
                                         }

                                         for (Container containerForUpdate : containersForUpdate) {
                                             for (Container container : containers) {
                                                 updateContainerEnv(container, containerForUpdate.getEnv());
                                             }
                                         }
                                         config.getMetadata().getLabels().put("database", deploymentConfigName);
                                     }
                                     return configs;
                                 }
                             }).thenPromise(updateApplicationDeploymentConfigsRecursive());
            }
        };
    }


    private Function<List<DeploymentConfig>, Promise<JsArrayMixed>> updateApplicationDeploymentConfigsRecursive() {
        return new Function<List<DeploymentConfig>, Promise<JsArrayMixed>>() {
            @Override
            public Promise<JsArrayMixed> apply(List<DeploymentConfig> configs) throws FunctionException {
                List<Promise<?>> promiseList = new ArrayList<>();
                for(DeploymentConfig config: configs) {
                    Promise<DeploymentConfig> promise = client.updateDeploymentConfig(config);
                    promiseList.add(promise);
                }
                Promise<?>[] promises = promiseList.toArray(new Promise<?>[promiseList.size()]);
                return Promises.all(promises);
            }
        };
    }

//    private Function<List<DeploymentConfig>, Promise<JsArrayMixed>> updateApplicationDeploymentConfigsRecursive() {
//        return new Function<List<DeploymentConfig>, Promise<JsArrayMixed>>() {
//            @Override
//            public Promise<JsArrayMixed> apply(List<DeploymentConfig> configs) throws FunctionException {
//                Promise<?>[] promises = new Promise[configs.size()];
//                int i = 0;
//                for(DeploymentConfig config: configs) {
//                    promises[i++] = client.updateDeploymentConfig(config);
//                }
//                return Promises.all(promises);
//            }
//        };
//    }

    private void updateContainerEnv(Container container, List<EnvVar> envVariables) {
        List<EnvVar> containerEnvs = container.getEnv();

        if (containerEnvs == null) {
            container.setEnv(envVariables);
            return;
        }

        for (final EnvVar envVarForEdit : envVariables) {
            Optional<EnvVar> foundedEnv = findEnvVarByName(envVarForEdit.getName(), containerEnvs);
            if (foundedEnv.isPresent()) {
                containerEnvs.remove(foundedEnv.get());
            }
            containerEnvs.add(envVarForEdit);
        }
    }

    private Optional<EnvVar> findEnvVarByName(final String name, List<EnvVar> envVars) {
        return Iterables.tryFind(envVars, new Predicate<EnvVar>() {
            @Override
            public boolean apply(EnvVar envVar) {
                return name.equals(envVar.getName());
            }
        });
    }

    private Operation<PromiseError> handleError(final CompleteCallback callback) {
        return new Operation<PromiseError>() {
            @Override
            public void apply(PromiseError promiseError) throws OperationException {
                callback.onFailure(promiseError.getCause());
            }
        };
    }

    private Operation<Promise<JsArrayMixed>> onSuccess(final CompleteCallback callback) {
        return new Operation<Promise<JsArrayMixed>>() {
            @Override
            public void apply(Promise<JsArrayMixed> arg) throws OperationException {    
                callback.onCompleted();
            }
        };
    }
}
