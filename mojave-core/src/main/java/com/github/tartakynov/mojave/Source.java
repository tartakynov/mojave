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

/**
 * Source connects to an external source and reads messages.
 */
public abstract class Source implements Component {
    private boolean configured;
    private String name;
    private int concurrencyLevel = 0;

    /**
     * Gets the number of threads consuming current {@see Source}.
     *
     * @return Current concurrency level.
     */
    public int getConcurrencyLevel() {
        return this.concurrencyLevel;
    }

    /**
     * Tries to return the next message from an external source.
     *
     * @return Message as a string.
     */
    public abstract String take();

    @Override
    public void configure(Configuration config) throws ConfigurationException {
        if (!this.configured) {
            this.validateConfiguration(config);
            this.name = config.getSectionName();
            this.configured = true;
        }
    }

    /**
     * Gets current {@see Source} name.
     *
     * @throws ConfigurationException
     */
    @Override
    public String getName() throws ConfigurationException {
        if (!this.configured) {
            throw new ConfigurationException("Not configured");
        }

        return this.name;
    }

    protected void validateConfiguration(Configuration config) throws ConfigurationException {
        String name = config.getSectionName();
        if (name == null || name.length() == 0) {
            throw new ConfigurationException("The name is not provided.");
        }
    }
}
