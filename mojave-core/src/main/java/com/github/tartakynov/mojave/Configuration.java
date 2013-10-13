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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents an application's configuration.
 */
public final class Configuration {
    private final Map<String, String> values;
    private final String name;

    public Configuration(Map<String, String> values) {
        this.name = null;
        this.values = values;
    }

    public Configuration(String name, Map<String, String> values) {
        this.name = name;
        this.values = values;
    }

    /**
     * Gets properties stored in the map.
     *
     * @return properties.
     */
    public Map<String, String> getProperties() {
        return new HashMap<String, String>(this.values);
    }

    /**
     * Gets a section mapped to key, returning empty section if unmapped.
     *
     * @param name to be found
     * @return value associated with key
     */
    public Configuration getSection(String name) {
        Map<String, String> section = new HashMap<String, String>();
        for (String key : this.values.keySet()) {
            if (key.startsWith(name + ".")) {
                section.put(key.substring(name.length() + 1), this.values.get(key));
            }
        }
        return new Configuration(name, section);
    }

    /**
     * Determines whether a configuration contains a specified key.
     *
     * @param key to find in the configuration.
     * @return true if the source sequence contains a key that has the specified value; otherwise, false.
     */
    public boolean contains(String key) {
        return this.values.containsKey(key);
    }

    /**
     * Gets the name of the current section.
     *
     * @return the name associated with the current section if the section is named; otherwise, null.
     */
    public String getSectionName() {
        return this.name;
    }

    /**
     * Gets an array mapped to key, returning empty array if unmapped.
     *
     * @param key to be found
     * @return value associated with key
     */
    public ArrayList<String> getArray(String key) {
        ArrayList<String> result = new ArrayList<String>();
        int i = 0;
        String item;
        while ((item = this.values.get(key + "." + i++)) != null) {
            result.add(item);
        }
        return result;
    }

    /**
     * Gets value mapped to key, returning null if unmapped.
     *
     * @param key to be found
     * @return value associated with key
     */
    public String get(String key) {
        return this.values.get(key);
    }

    /**
     * Gets value mapped to key, returning defaultValue if unmapped.
     *
     * @param key          to be found
     * @param defaultValue returned if key is unmapped
     * @return value associated with key
     */
    public int getInt32(String key, int defaultValue) {
        String value = this.values.get(key);
        if (value != null) {
            return Integer.parseInt(value);
        }
        return defaultValue;
    }

    /**
     * Gets value mapped to key, returning defaultValue if unmapped.
     *
     * @param key          to be found
     * @param defaultValue returned if key is unmapped
     * @return value associated with key
     */
    public long getInt64(String key, long defaultValue) {
        String value = this.values.get(key);
        if (value != null) {
            return Long.parseLong(value);
        }
        return defaultValue;
    }

    /**
     * Gets value mapped to key, returning defaultValue if unmapped.
     *
     * @param key          to be found
     * @param defaultValue returned if key is unmapped
     * @return value associated with key
     */
    public double getDouble(String key, double defaultValue) {
        String value = this.values.get(key);
        if (value != null) {
            return Double.parseDouble(value);
        }
        return defaultValue;
    }
}
