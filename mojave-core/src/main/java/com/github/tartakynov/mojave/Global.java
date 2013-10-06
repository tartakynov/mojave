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

import org.mozilla.javascript.*;
import org.mozilla.javascript.tools.shell.Environment;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * JavaScript global object for Mojave.
 */
public class Global extends ImporterTopLevel {

    protected static final HashMap<File, Scriptable> modules = new HashMap<File, Scriptable>();
    protected static final Stack<String> location = new Stack<String>();
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
        File file = new File(location.peek(), Context.toString(args[0]));

        // check if module is already loaded
        if (modules.containsKey(file)) {
            return modules.get(file);
        }

        // make new global object
        ScriptableObject scope = new ImporterTopLevel(ctx);
        scope.setPrototype(thisObj);

        // define the exports property
        ScriptableObject exports = (ScriptableObject) ctx.newObject(scope);
        scope.defineProperty("exports", exports, PERMANENT | DONTENUM);

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
        // convert javascript object to java map
        Map<String, String> map = convertJsObjectToMap((NativeObject) args[0], "");
        Configuration config = new Configuration(map);
        map.toString();
    }

    /**
     * Runs script from file in given context with specified scope.
     */
    protected static void runScriptFromFile(Context cx, ScriptableObject scope, File file)
            throws IOException {
        FileReader in = new FileReader(file);
        try {
            location.push(file.getParentFile().getAbsolutePath());
            Script script = cx.compileReader(in, file.getName(), 1, null);
            script.exec(cx, scope);
        } finally {
            in.close();
            location.pop();
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
                    result.put(prefix + key, Context.toString(value));
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
}