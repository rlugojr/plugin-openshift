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

import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class TextInputCellWithPlaceHolder extends TextInputCell {

    private String   placeHolder;
    private Template template;

    interface Template extends SafeHtmlTemplates {
        @Template("<input type=\"text\" value=\"{0}\" placeholder=\"{1}\" tabindex=\"-1\" ></input>")
        SafeHtml input(final String value, final String placeHolder);
    }

    public TextInputCellWithPlaceHolder(String placeHolder) {
        this.placeHolder = placeHolder;
        this.template = GWT.create(Template.class);
    }

    @Override
    public void render(Context context, String value, SafeHtmlBuilder sb) {
        if (value == null) {
            sb.append(template.input("", placeHolder));
        } else {
            sb.append(template.input(value, placeHolder));
        }
    }
}
