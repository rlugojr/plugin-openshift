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
package org.eclipse.che.ide.ext.openshift.client.service.add.wizard.page.select;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;

import org.eclipse.che.api.promises.client.Function;
import org.eclipse.che.api.promises.client.FunctionException;
import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.wizard.AbstractWizardPage;
import org.eclipse.che.ide.ext.openshift.client.OpenshiftLocalizationConstant;
import org.eclipse.che.ide.ext.openshift.client.OpenshiftServiceClient;
import org.eclipse.che.ide.ext.openshift.client.dto.NewServiceRequest;
import org.eclipse.che.ide.ext.openshift.shared.dto.Template;
import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import static org.eclipse.che.ide.ext.openshift.shared.OpenshiftProjectTypeConstants.OPENSHIFT_NAMESPACE_VARIABLE_NAME;

@Singleton
public class SelectServicePresenter extends AbstractWizardPage<NewServiceRequest> implements SelectServiceView.ActionDelegate {

    private final SelectServiceView             view;
    private final OpenshiftServiceClient        client;
    private final NotificationManager           notificationManager;
    private final AppContext                    appContext;
    private final OpenshiftLocalizationConstant locale;

    public static final  String DEF_NAMESPACE = "openshift";//todo
    private static final String DATABASE_TAG  = "database";//todo
    
    private Template template;

    @Inject
    public SelectServicePresenter(SelectServiceView view,
                                  OpenshiftServiceClient client,
                                  NotificationManager notificationManager,
                                  AppContext appContext,
                                  OpenshiftLocalizationConstant locale) {
        this.view = view;
        this.client = client;
        this.notificationManager = notificationManager;
        this.appContext = appContext;
        this.locale = locale;
        
        view.setDelegate(this);
    }
    
    @Override
    public void init(NewServiceRequest dataObject) {
        super.init(dataObject);
        
        ProjectDescriptor projectDescriptor = appContext.getCurrentProject().getRootProject();
        final String nameSpace = getAttributeValue(projectDescriptor, OPENSHIFT_NAMESPACE_VARIABLE_NAME);

        view.showLoadingTemplates();

        client.getTemplates(DEF_NAMESPACE)
               .then(filterByCategory(DATABASE_TAG))
               .then(addTemplates())
               .catchError(handleError(DEF_NAMESPACE));

        client.getTemplates(nameSpace)
               .then(filterByCategory(DATABASE_TAG))
               .then(addTemplates())
               .catchError(handleError(nameSpace));
    } 
    
    //TODO create class categoryFilter
    private Function<List<Template>, List<Template>> filterByCategory(@NotNull final String category) {
        return new Function<List<Template>, List<Template>>() {
            @Override
            public List<Template> apply(List<Template> templates) throws FunctionException {
                List<Template> filteredTemplates = new ArrayList<>();
                for (final Template template : templates) {
                    final String tags = template.getMetadata().getAnnotations().get("tags");
                    for (String tag : tags.split(",")) {
                        if (category.equals(tag.trim())) {
                            filteredTemplates.add(template);
                        }
                    }
                }
                return filteredTemplates;
            }
        };
    }

    private Operation<List<Template>> addTemplates() {
        return new Operation<List<Template>>() {
            @Override
            public void apply(List<Template> templates) {
                view.setTemplates(templates);
            }
        };
    }
    
     private Operation<PromiseError> handleError(final String nameSpace) {
        return new Operation<PromiseError>() {
            @Override
            public void apply(PromiseError promiseError) throws OperationException {
                notificationManager.showError(locale.getListTemplatesFailed(nameSpace) + " " + promiseError.getMessage());
            }
        };
    }
    
    private String getAttributeValue(ProjectDescriptor projectDescriptor, String value) {
        List<String> attributes = projectDescriptor.getAttributes().get(value);
        if (attributes == null || attributes.isEmpty()) {
            return null;
        }
        return projectDescriptor.getAttributes().get(value).get(0);
    }
    
    @Override
    public boolean isCompleted() {
        return template != null;
    }

    @Override
    public void onTemplateSelected(Template template) {
        this.template = template;
        dataObject.setTemplate(template);
        updateDelegate.updateControls();
    }

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }
}
