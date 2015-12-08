package org.eclipse.che.ide.ext.openshift.client.service;

import org.eclipse.che.ide.ext.openshift.client.deploy._new.KeyValue;

/**
 * Todo
 */
public class KeyValueWithGeneration extends KeyValue {

    private boolean isGenerated;

    public KeyValueWithGeneration(String key, String value, boolean isGenerated) {
        super(key, value);

        this.isGenerated = isGenerated;
    }

    public boolean isGenerated() {
        return isGenerated;
    }

    public void setGenerated(boolean generated) {
        isGenerated = generated;
    }
}