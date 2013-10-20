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
package com.github.tartakynov.mojave.scripting;

import com.github.tartakynov.mojave.Configuration;
import org.mozilla.javascript.*;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSGetter;

import java.util.Map;

/**
 *
 */
public class Mojave extends ScriptableObject {
    /**
     * The zero-argument constructor used by Rhino runtime to create instances.
     */
    public Mojave() {
    }

    /**
     * The JavaScript constructor.
     *
     * @param configJsObj configuration.
     */
    @JSConstructor
    public Mojave(NativeObject configJsObj) {
        Map<String, String> map = Global.convertJsObjectToMap(configJsObj);
        Configuration config = new Configuration(map);
    }

    @Override
    public String getClassName() {
        return "Mojave";
    }

    /**
     * This method defines 'sources' property.
     *
     * @return a javascript object with the map of sources.
     */
    // The method getCount defines the count property.
    @JSGetter
    public Object getSources() {
        final Scriptable scope = this.getParentScope();
        return new NativeObject() {{
            defineProperty("a", new SourceRunner(scope, null), READONLY);
            defineProperty("b", 2, READONLY);
        }};
    }
}
