# collectd-camel-plugin
Collectd plugin reading Camel route statistics exposed over JMX

Searches JMX server for Camel routes MBeans and exposes their statistics. Uses compination of Camel context id and route id to create metric name.
The generated metrix follow pattern of:
CamelPlugin-<context_name>.<route_if>.gauge-<attribute_name>
eg.
CamelPlugin-testContext.timerRoute.gauge-MeanProcessingTime

Configuration
=============

```
<Plugin "java">
        JVMArg "-verbose:jni"
        JVMArg "-Djava.class.path=/usr/share/collectd/camel/collectd-plugin-camel-1.0-jar-with-dependencies.jar:/usr/share/collectd/java/collectd-api.jar"

        LoadPlugin "com.spyrkob.CamelPlugin"
        <Plugin "CamelPlugin">
          JmxServiceUrl "service:jmx:remoting-jmx://localhost:9999"
          JmxUsername "admin"
          JmxPassword "password1;"
          UseSystemClassLoader true
          Hostname "test.te"
        </Plugin>
</Plugin>
```

**UseSystenClassLoader** - set to true if non-standard jmx protocol is used (and provided via java.class.path property). Defaults to false.
**Hostname**             - used to set the metrics hostname. Not used in connecting to JMX server. Defaults to localhost.
