/*
 * Copyright 2015, OpenRemote Inc.
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

package org.openremote.model.flow.catalog;

import org.openremote.model.flow.Node;
import org.openremote.model.flow.NodeColor;
import org.openremote.model.flow.Slot;
import org.openremote.model.value.ObjectValue;
import org.openremote.model.value.Values;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;


public abstract class NodeDescriptor {

    abstract public String getType();

    abstract public String getTypeLabel();

    public CatalogCategory getCatalogCategory() {
        return CatalogCategory.PROCESSORS;
    }

    public NodeColor getColor() {
        return NodeColor.DEFAULT;
    }

    public Node initialize(Node node, Supplier<String> idGenerator) {

        List<Slot> slots = new ArrayList<>();
        addSlots(slots, idGenerator);
        node.setSlots(slots.toArray(new Slot[slots.size()]));

        node.getEditorSettings().setTypeLabel(getTypeLabel());
        node.getEditorSettings().setNodeColor(getColor());

        List<String> editorComponents = new ArrayList<>();
        addEditorComponents(editorComponents);
        node.getEditorSettings().setComponents(editorComponents.toArray(new String[editorComponents.size()]));

        ObjectValue initialProperties = getInitialProperties();
        try {
            if (initialProperties != null) {
                node.setProperties(initialProperties.toJson());
            } else {
                ObjectValue properties = Values.createObject();
                configureInitialProperties(properties);
                if (properties.hasKeys()) {
                    node.setProperties(properties.toJson());
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error writing initial properties of: " + getType(), ex);
        }

        List<String> persistentPaths = new ArrayList<>();
        addPersistentPropertyPaths(persistentPaths);
        if (persistentPaths.size() > 0) {
            node.setPersistentPropertyPaths(persistentPaths.toArray(new String[persistentPaths.size()]));
        }

        return node;
    }

    public List<String> getPersistentPropertyPaths() {
        List<String> persistentPaths = new ArrayList<>();
        addPersistentPropertyPaths(persistentPaths);
        return persistentPaths;
    }

    public void addSlots(List<Slot> slots, Supplier<String> idGenerator) {
        // Subclass
    }

    public void addEditorComponents(List<String> editorComponents) {
        // Subclass
    }

    protected void configureInitialProperties(ObjectValue properties) {
        // Subclass
    }

    protected void addPersistentPropertyPaths(List<String> propertyPaths) {
        // Subclass
    }

    protected ObjectValue getInitialProperties() {
        return null;
    }
}