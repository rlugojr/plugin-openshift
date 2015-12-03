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
import com.google.inject.Singleton;

import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.wizard.Wizard;
import org.eclipse.che.ide.api.wizard.WizardPage;
import org.eclipse.che.ide.ext.openshift.client.OpenshiftLocalizationConstant;
import org.eclipse.che.ide.ext.openshift.client.WizardFactory;
import org.eclipse.che.ide.ext.openshift.client.dto.NewServiceRequest;
import org.eclipse.che.ide.ext.openshift.client.service.add.wizard.page.configure.ConfigureServicePresenter;
import org.eclipse.che.ide.ext.openshift.client.service.add.wizard.page.select.SelectServicePresenter;
import org.eclipse.che.ide.ext.openshift.shared.dto.Service;
import org.eclipse.che.ide.dto.DtoFactory;

import javax.validation.constraints.NotNull;

@Singleton
public class CreateServicePresenter implements Wizard.UpdateDelegate, CreateServiceWizardView.ActionDelegate  {

    private final NotificationManager           notificationManager;
    private final OpenshiftLocalizationConstant locale;
    private final WizardFactory                 wizardFactory;
    private final DtoFactory                    dtoFactory;
    private final CreateServiceWizardView       view;
    private final SelectServicePresenter        selectPage;
    private final ConfigureServicePresenter     configurePage;

    private CreateServiceWizard wizard;
    private WizardPage<NewServiceRequest>                 currentPage;


    @Inject
    public CreateServicePresenter(NotificationManager notificationManager,
                                  OpenshiftLocalizationConstant locale,
                                  WizardFactory wizardFactory,
                                  DtoFactory dtoFactory,
                                  CreateServiceWizardView view,
                                  SelectServicePresenter selectPage,
                                  ConfigureServicePresenter configurePage) {

        this.notificationManager = notificationManager;
        this.locale = locale;
        this.wizardFactory = wizardFactory;
        this.dtoFactory = dtoFactory;
        this.view = view;
        this.selectPage = selectPage;
        this.configurePage = configurePage;

        view.setDelegate(this);
    }

    public void createWizardAndShow() {
        wizard = wizardFactory.newServiceWizard(dtoFactory.createDto(NewServiceRequest.class));
        
        wizard.setUpdateDelegate(this);
        wizard.addPage(selectPage);
        wizard.addPage(configurePage);

        final WizardPage<NewServiceRequest> firstPage = wizard.navigateToFirst();
        if (firstPage != null) {
            showWizardPage(firstPage);
            view.showWizard();
        }
    }
    
    private void showWizardPage(@NotNull WizardPage<NewServiceRequest> wizardPage) {
        currentPage = wizardPage;
        updateControls();
        view.showPage(currentPage);
    }
    
    @Override
    public void updateControls() {
        view.setPreviousButtonEnabled(wizard.hasPrevious());
        view.setNextButtonEnabled(wizard.hasNext() && currentPage.isCompleted());
        view.setCreateButtonEnabled(wizard.canComplete());
    }

    @Override
    public void onNextClicked() {
        WizardPage<NewServiceRequest> nextPage = wizard.navigateToNext();
        if (nextPage != null) {
            showWizardPage(nextPage);
        }
    }

    @Override
    public void onPreviousClicked() {
        WizardPage<NewServiceRequest> previousPage = wizard.navigateToPrevious();
        if (previousPage != null) {
            showWizardPage(previousPage);
        }
    }

    @Override
    public void onCreateClicked() {
        //configProjectPage.setEnabled(false);//TODO
//        view.setPreviousButtonEnabled(false);
//        view.setNextButtonEnabled(false);
//        view.setCreateButtonEnabled(false);
//        view.animateCreateButton(true);
//        view.setBlocked(true);
        configurePage.updateData();

        wizard.complete(new Wizard.CompleteCallback() {
            @Override
            public void onCompleted() {
                //configProjectPage.setEnabled(true);//TODO
                updateControls();
//                view.animateCreateButton(false);
//                view.setBlocked(false);

                notificationManager.showInfo(locale.createFromTemplateSuccess());
                view.closeWizard();
            }

            @Override
            public void onFailure(Throwable e) {
                //configurePage.setEnabled(true);
                updateControls();
//                view.animateCreateButton(false);
//                view.setBlocked(false);

                String message = e.getMessage() != null ? e.getMessage() : locale.createFromTemplateFailed();
                notificationManager.showError(message);
            }
        });
    }
}
