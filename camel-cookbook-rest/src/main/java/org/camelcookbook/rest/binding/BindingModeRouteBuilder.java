/*
 * Copyright (C) Scott Cranton, Jakub Korab, and Christian Posta
 * https://github.com/CamelCookbook
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.camelcookbook.rest.binding;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

/**
 * Simple REST DSL example
 */
public class BindingModeRouteBuilder extends RouteBuilder {
    private int port1;

    public BindingModeRouteBuilder() {
    }

    public BindingModeRouteBuilder(int port) {
        this.port1 = port;
    }

    public void setPort1(int port1) {
        this.port1 = port1;
    }

    @Override
    public void configure() throws Exception {
        restConfiguration()
            .component("undertow").port(port1).bindingMode(RestBindingMode.json_xml);

        rest()
            .get("/items").outType(Item[].class).to("bean:itemService?method=getItems")
            .get("/items/{id}").outType(Item.class).to("bean:itemService?method=getItem(${header.id})")
            .get("/items/{id}/xml").outType(Item.class).bindingMode(RestBindingMode.xml).to("bean:itemService?method=getItem(${header.id})")
            .get("/items/{id}/json").outType(Item.class).bindingMode(RestBindingMode.json).to("bean:itemService?method=getItem(${header.id})")
            .put("/items/{id}").type(Item.class).to("bean:itemService?method=setItem(${header.id}, ${body})");
    }
}
