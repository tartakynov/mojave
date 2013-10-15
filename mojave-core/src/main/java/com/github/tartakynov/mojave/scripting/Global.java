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
import org.apache.log4j.PropertyConfigurator;
import org.mozilla.javascript.*;
import org.mozilla.javascript.tools.shell.Environment;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * JavaScript global object for Mojave.
 */
public class Global extends ImporterTopLevel {
    protected static final HashMap<File, Scriptable> modules = new HashMap<File, Scriptable>();
    protected static final ArrayList<ConfigurationListener> listeners = new ArrayList<ConfigurationListener>();
    protected final Context context;

    public Global(Context ctx, boolean sealed) {
        String[] functionNames = {"version", "require", "config"};
        this.initStandardObjects(ctx, sealed);
        this.defineFunctionProperties(functionNames, Global.class, DONTENUM);

        Environment.defineClass(this);
        Environment environment = new Environment(this);
        this.defineProperty("environment", environment, DONTENUM);
        this.defineProperty("stdout", System.out, DONTENUM);
        this.defineProperty("stderr", System.err, DONTENUM);
        this.context = ctx;
    }

    /**
     * Get and set the language version.
     * This method is defined as a JavaScript function.
     */
    public static double version(Context ctx, Scriptable thisObj, Object[] args, Function funObj) {
        double result = ctx.getLanguageVersion();
        if (args.length > 0) {
            double d = Context.toNumber(args[0]);
            ctx.setLanguageVersion((int) d);
        }
        return result;
    }

    /**
     * Node.js-style require method.
     * This method is defined as a JavaScript function.
     */
    public static Object require(Context ctx, Scriptable thisObj, Object[] args, Function funObj)
            throws IOException {
        String dir = (String) getProperty(thisObj, ConstantProperties.DIRECTORY_NAME.toString());
        File file = new File(dir, Context.toString(args[0]));

        // check if module is already loaded
        if (modules.containsKey(file)) {
            return modules.get(file);
        }

        // make new global object
        ScriptableObject scope = new ImporterTopLevel(ctx);
        scope.setPrototype(thisObj);

        // define the exports property
        ScriptableObject exports = (ScriptableObject) ctx.newObject(scope);
        scope.defineProperty("exports", exports, DONTENUM);

        // compile & execute the module
        runScriptFromFile(ctx, scope, file);

        // put module to the map and return
        modules.put(file, exports);
        return exports;
    }

    /**
     * Configures the application.
     * This method is defined as a JavaScript function.
     */
    public static void config(Context ctx, Scriptable thisObj, Object[] args, Function funObj) {
        // convert javascript object to configuration
        Configuration config = new Configuration(convertJsObjectToMap((NativeObject) args[0], ""));
        Properties properties = new Properties();
        properties.putAll(config.getProperties());
        PropertyConfigurator.configure(properties);
        notifyConfigurationListeners(config);
    }

    /**
     * Runs script from file in given context with specified scope.
     */
    protected static void runScriptFromFile(Context cx, ScriptableObject scope, File file)
            throws IOException {
        FileReader in = new FileReader(file);
        try {
            String currentLocation = file.getParentFile().getAbsolutePath();
            Script script = cx.compileReader(in, file.getName(), 1, null);
            scope.defineProperty(ConstantProperties.FILE_NAME.toString(), file.getName(), CONST | DONTENUM);
            scope.defineProperty(ConstantProperties.DIRECTORY_NAME.toString(), file.getParent(), CONST | DONTENUM);
            script.exec(cx, scope);
        } finally {
            in.close();
        }
    }

    /**
     * Converts JavaScript object to map of strings.
     *
     * @param obj    JavaScript object to convert.
     * @param prefix Recursive parameter. Leave empty.
     * @return Converted map.
     */
    protected static Map<String, String> convertJsObjectToMap(NativeObject obj, String prefix) {
        Map<String, String> result = new HashMap<String, String>();
        for (Map.Entry<Object, Object> property : obj.entrySet()) {
            String key = property.getKey().toString();
            Object value = property.getValue();
            if (value instanceof NativeObject) {
                result.putAll(convertJsObjectToMap((NativeObject) value, prefix + key + "."));
            } else {
                if (value instanceof List) {
                    int i = 0;
                    for (Object item : (List) value) {
                        result.put(prefix + key + "." + i++, Context.toString(item));
                    }
                } else {
                    result.put(key.equals("__") ? prefix.substring(0, prefix.length() - 1) : prefix + key, Context.toString(value));
                }
            }
        }
        return result;
    }

    /**
     * Adds configuration listener.
     *
     * @param listener to be added.
     */
    public static void addConfigurationListener(ConfigurationListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * Calls registered event listeners.
     */
    protected static void notifyConfigurationListeners(Configuration config) {
        synchronized (listeners) {
            for (ConfigurationListener listener : listeners) {
                listener.onConfig(config);
            }
        }
    }

    /**
     * Runs script from given file.
     */
    public void run(String file) throws IOException {
        runScriptFromFile(this.context, this, new File(file));
    }

    /**
     * Script's constant properties.
     */
    protected enum ConstantProperties {
        /**
         * Script directory absolute path.
         */
        DIRECTORY_NAME("__dirname"),

        /**
         * Script file name.
         */
        FILE_NAME("__filename");

        private final String propertyName;

        private ConstantProperties(final String propertyName) {
            this.propertyName = propertyName;
        }

        @Override
        public String toString() {
            return this.propertyName;
        }
    }

    /**
     * Used for receiving notifications from the Global object when config method is called.
     */
    public interface ConfigurationListener {
        /**
         * Called when config is called from JavaScript code.
         */
        void onConfig(Configuration config);
    }
}