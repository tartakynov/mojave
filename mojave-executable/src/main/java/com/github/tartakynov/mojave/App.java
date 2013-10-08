package com.github.tartakynov.mojave;


import org.mozilla.javascript.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Hello world!
 */
public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception {
        try {
            Context context = Context.enter();
            context.setOptimizationLevel(9);
            Global global = new Global(context, false);
            global.run("c:\\src\\test.js");
            log.debug("hello");
        } finally {
            Context.exit();
        }
    }
}
