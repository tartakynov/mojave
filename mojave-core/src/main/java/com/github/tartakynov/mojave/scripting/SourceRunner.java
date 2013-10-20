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

import com.github.tartakynov.mojave.Source;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

/**
 *
 */
public class SourceRunner {
    protected Scriptable scope;
    protected Source source;

    public SourceRunner(Scriptable scope, Source source) {
        this.scope = scope;
        this.source = source;
    }

    public void start(final Function callback) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Context ctx = Context.enter();
                try {
                    Thread.sleep(1000);
                    callback.call(ctx, scope, null, new Object[]{1, 2});
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } finally {
                    Context.exit();
                }
            }
        });
        thread.start();
    }

    public void stop() {

    }
}
