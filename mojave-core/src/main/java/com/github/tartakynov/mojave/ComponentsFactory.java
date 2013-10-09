/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.github.tartakynov.mojave;

import com.github.tartakynov.mojave.exceptions.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComponentsFactory {
    private static final Logger log = LoggerFactory.getLogger(ComponentsFactory.class);

    /**
     * Creates an instance of {@see Source} by it's name.
     *
     * @param className of the component to create.
     * @return an instance of created {@see Source}.
     */
    public Source createSource(String className) throws ConfigurationException {
        log.debug("Creating the source: {}", className);
        return this.create(className);
    }

    /**
     * Creates an instance of {@see Sink} by it's name.
     *
     * @param className of the component to create.
     * @return an instance of created {@see Sink}.
     */
    public Sink createSink(String className) throws ConfigurationException {
        log.debug("Creating the sink: {}", className);
        return this.create(className);
    }

    /**
     * Creates an instance by it's class name.
     */
    protected <T extends Component> T create(String className) throws ConfigurationException {
        if (className == null) {
            throw new IllegalArgumentException("className");
        }

        Class<? extends T> component;
        try {
            component = (Class<? extends T>) Class.forName(className);
            return component.newInstance();
        } catch (Exception ex) {
            throw new ConfigurationException("Unable to load class: " + className);
        }
    }
}
