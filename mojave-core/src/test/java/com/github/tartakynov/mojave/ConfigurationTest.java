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

import com.github.tartakynov.mojave.scripting.Global;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mozilla.javascript.Context;

public class ConfigurationTest {
    private Configuration configuration;

    @Before
    public void setUp() throws Exception {
        String script = ConfigurationTest.class.getClassLoader().getResource("config.js").getFile();
        Global global = new Global(Context.enter(), false);
        Global.addConfigurationListener(new Global.ConfigurationListener() {
            @Override
            public void onConfig(Configuration config) {
                ConfigurationTest.this.configuration = config;
            }
        });
        global.run(script);
    }

    @After
    public void tearDown() {
        Context.exit();
    }

    @Test
    public void testGetSection() throws Exception {
        Configuration section = this.configuration.getSection("sources");
        Assert.assertNotNull(section);
        Assert.assertNotNull(section.getSectionName());
    }

    @Test
    public void testContains() throws Exception {

    }

    @Test
    public void testGetSectionName() throws Exception {

    }
}
