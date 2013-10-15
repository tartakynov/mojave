config({
    sources: {
        'ZeroMQSource' : {
            's1' : {
                endpoint: 'tcp://eu.host:1234',
                concurrencylevel: 2
            },
            's2' : {
                endpoint: 'tcp://na.host:1234',
                concurrencyLevel: 2
            }
        },
    },
    sinks: {
        'SqlSink' : {
            'warehouse' : {
                connectionString: 'Server=myWarehouseServerAddress;Database=myDataBase;Uid=myUsername;Pwd=myPassword;',
                timeout: Infinity
            },
            'archive' : {
                connectionString: 'Server=myArchiveserverAddress;Database=myDataBase;Uid=myUsername;Pwd=myPassword;',
                timeout: Infinity
            }
        }
    },
    log4j: {
        appender : {
            console : {
                __      : 'org.apache.log4j.ConsoleAppender',
                target  : 'System.out',
                layout  : {
                    __  : 'org.apache.log4j.PatternLayout',
                    ConversionPattern : '%d (%t) [%p - %l] %m%n'
                }
            }
        },
        rootLogger : 'DEBUG,console'
    }
});
