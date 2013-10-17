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

import org.mozilla.javascript.*;
import org.mozilla.javascript.tools.shell.Environment;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JavaScript global object for Mojave.
 */
public class Global extends ImporterTopLevel {
    protected static final HashMap<File, Scriptable> modules = new HashMap<File, Scriptable>();
    protected final Context context;

    public Global(Context ctx, boolean sealed) throws Exception {
        String[] functionNames = {"version", "require"};
        this.initStandardObjects(ctx, sealed);
        this.defineFunctionProperties(functionNames, Global.class, DONTENUM);
        ScriptableObject.defineClass(this, Mojave.class);

        Environment.defineClass(this);
        Environment environment = new Environment(this);
        this.defineProperty(Properties.ENVIRONMENT.toString(), environment, DONTENUM);
        this.defineProperty(Properties.STDOUT.toString(), System.out, DONTENUM);
        this.defineProperty(Properties.STDERR.toString(), System.err, DONTENUM);
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
        String dir = (String) getProperty(thisObj, Properties.DIRECTORY_NAME.toString());
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
        scope.defineProperty(Properties.EXPORTS.toString(), exports, DONTENUM);

        // compile & execute the module
        runScriptFromFile(ctx, scope, file);

        // put module to the map and return
        modules.put(file, exports);
        return exports;
    }

    /**
     * Runs script from file in given context with specified scope.
     */
    protected static void runScriptFromFile(Context cx, ScriptableObject scope, File file)
            throws IOException {
        FileReader in = new FileReader(file);
        try {
            Script script = cx.compileReader(in, file.getName(), 1, null);
            scope.defineProperty(Properties.FILE_NAME.toString(), file.getName(), CONST | DONTENUM);
            scope.defineProperty(Properties.DIRECTORY_NAME.toString(), file.getParent(), CONST | DONTENUM);
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
    public static Map<String, String> convertJsObjectToMap(NativeObject obj, String prefix) {
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
     * Runs script from given file.
     */
    public void run(String file) throws IOException {
        runScriptFromFile(this.context, this, new File(file));
    }

    /**
     * Script's constant properties.
     */
    protected enum Properties {
        EXPORTS("exports"),

        ENVIRONMENT("environment"),

        STDOUT("stdout"),

        STDERR("stderr"),

        DIRECTORY_NAME("__dirname"),

        FILE_NAME("__filename");
        private final String propertyName;

        private Properties(final String propertyName) {
            this.propertyName = propertyName;
        }

        @Override
        public String toString() {
            return this.propertyName;
        }
    }
}