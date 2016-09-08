/*
 * Copyright 2016, OpenRemote Inc.
 *
 * See the CONTRIBUTORS.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openremote.agent.controller2.model;

/**
 * Used to track write requests which occur whilst connect/reconnect is occurring
 */
public class WriteResourceRequest {

    protected String deviceUri;
    protected String resourceUri;
    protected Object resourceValue;

    public WriteResourceRequest() {
    }

    public WriteResourceRequest(String deviceUri, String resourceUri, Object resourceValue) {
        this.deviceUri = deviceUri;
        this.resourceUri = resourceUri;
        this.resourceValue = resourceValue;
    }

    public String getDeviceUri() {
        return deviceUri;
    }

    public void setDeviceUri(String deviceUri) {
        this.deviceUri = deviceUri;
    }

    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
    }

    public Object getResourceValue() {
        return resourceValue;
    }

    public void setResourceValue(Object resourceValue) {
        this.resourceValue = resourceValue;
    }
}