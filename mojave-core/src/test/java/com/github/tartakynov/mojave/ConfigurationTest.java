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
import org.mozilla.javascript.NativeObject;

import java.util.ArrayList;
import java.util.Map;

public class ConfigurationTest {
    private Configuration configuration;

    private static Configuration getConfiguration(Object configJsObj) {
        Map<String, String> map = Global.convertJsObjectToMap((NativeObject) configJsObj, "");
        return new Configuration(map);
    }

    @Before
    public void setUp() throws Exception {
        String script = ConfigurationTest.class.getClassLoader().getResource("config.js").getFile();
        Global global = new Global(Context.enter(), false);
        global.run(script);
        this.configuration = getConfiguration(global.get("config"));
    }

    @After
    public void tearDown() {
        Context.exit();
    }

    @Test
    public void testGetSection() {
        Configuration section = this.configuration.getSection("testSectionA");
        Assert.assertNotNull(section);
        Assert.assertNotNull(section.getSectionName());
    }

    @Test
    public void testContains() {
        Configuration config = this.configuration;
        Assert.assertTrue(config.contains("testSectionA"));
        Assert.assertTrue(config.contains("testSectionA.testPropertyAInt"));
        Assert.assertFalse(config.contains("FalseSection"));
    }

    @Test
    public void testGetSectionName() {
        Configuration sectionA = this.configuration.getSection("testSectionA");
        Configuration sectionB = sectionA.getSection("testSectionB");
        Assert.assertNull(this.configuration.getSectionName());
        Assert.assertEquals(sectionA.getSectionName(), "testSectionA");
        Assert.assertEquals(sectionB.getSectionName(), "testSectionB");
    }

    @Test
    public void testGetArray() {
        ArrayList<String> array = this.configuration.getArray("testSectionA.testPropertyAIntArray");
        Assert.assertNotNull(array);
        Assert.assertEquals(5, array.size());
        Assert.assertEquals("1", array.get(0));
    }

    @Test
    public void testGet() {
        Configuration config = this.configuration;
        Assert.assertNull(config.get("falseProperty"));
        Assert.assertNull(config.get("testSectionA.testPropertyAIntArray"));
        Assert.assertEquals("1", config.get("testSectionA.testPropertyAInt"));
        Assert.assertEquals("hello world", config.get("testSectionA.testPropertyAString"));
    }

    @Test
    public void testGetInt32() {
        Configuration config = this.configuration;
        Assert.assertEquals(1, config.getInt32("testSectionA.testPropertyAInt", 0));
    }

    @Test
    public void testGetInt64() {
        Configuration config = this.configuration;
        Assert.assertEquals(1, config.getInt64("testSectionA.testPropertyAInt", 0));
    }

    @Test
    public void testGetDouble() {
        Configuration config = this.configuration;
        Assert.assertEquals(1.0, config.getDouble("testSectionA.testPropertyAInt", 0.0), 0.001);
    }
}
