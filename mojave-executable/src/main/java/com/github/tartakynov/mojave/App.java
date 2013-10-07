package com.github.tartakynov.mojave;


import org.apache.log4j.BasicConfigurator;
import org.mozilla.javascript.Context;


/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws Exception {
        try {
            BasicConfigurator.configure();
            Context context = Context.enter();
            context.setOptimizationLevel(9);
            Global global = new Global(context, false);
            global.run("c:\\src\\test.js");
        } finally {
            Context.exit();
        }
    }
}
