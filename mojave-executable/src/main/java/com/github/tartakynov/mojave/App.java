package com.github.tartakynov.mojave;


import com.github.tartakynov.mojave.scripting.Global;
import org.apache.log4j.PropertyConfigurator;
import org.mozilla.javascript.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;


/**
 * Hello world!
 */
public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception {
        try {
            Context context = Context.enter();
            context.setOptimizationLevel(9);
            Global.addConfigurationListener(new Global.ConfigurationListener() {
                @Override
                public void onConfig(Configuration config) {
                    Properties properties = new Properties();
                    properties.putAll(config.getProperties());
                    PropertyConfigurator.configure(properties);
                }
            });

            Global global = new Global(context, false);
            global.run("c:\\src\\test.js");
            log.debug("hello");
        } finally {
            Context.exit();
        }
    }
}
