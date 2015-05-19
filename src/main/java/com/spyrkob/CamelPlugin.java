package com.spyrkob;

import org.collectd.api.*;

import java.util.List;


/**
 * Created by spyrkob on 22/04/2015.
 */
public class CamelPlugin implements
        CollectdConfigInterface,
        CollectdInitInterface,
        CollectdReadInterface,
        CollectdShutdownInterface {

    public static final int CONFIG_ERROR = -1;
    public static final int OK = 0;
    private String jmxServiceURL;
    private String jmxUsername;
    private String jmxPassword;
    private String monitoredHostname = "localhost";
    private boolean useSystemClassLoader = false;

    public CamelPlugin() {
        Collectd.registerConfig("CamelPlugin", this);
        Collectd.registerInit("CamelPlugin", this);
        Collectd.registerRead("CamelPlugin", this);
        Collectd.registerShutdown("CamelPlugin", this);
    }
    @Override
    public int config(OConfigItem oConfigItem) {
        List<OConfigItem> children = oConfigItem.getChildren();
        for (OConfigItem child : children) {
            String key = child.getKey();
            if ("JmxServiceUrl".equals(key)){
                jmxServiceURL = getStringValue(child);
            }
            if ("JMXUsername".equals(key)) {
                jmxUsername = getStringValue(child);
            }
            if ("JMXPassword".equals(key)) {
                jmxPassword = getStringValue(child);
            }
            if ("Hostname".equals(key)) {
                monitoredHostname = getStringValue(child);
            }
            if ("UseSystemClassLoader".equals(key)) {
                useSystemClassLoader = getBooleanValue(child);
            }

            if (jmxServiceURL == null || jmxServiceURL.trim().equals("")) {
                Collectd.logError("JmxServiceUrl property needs to be provided");
                return CONFIG_ERROR;
            }
        }

        return OK;
    }

    private boolean getBooleanValue(OConfigItem child) {
        List<OConfigValue> values = child.getValues();
        if (values.size() != 1){
            Collectd.logError("Expected single value for property: " + child.getKey());
            return false;
        }
        return values.get(0).getBoolean();
    }

    private String getStringValue(OConfigItem child) {
        List<OConfigValue> values = child.getValues();
        if (values.size() != 1){
            Collectd.logError("Expected single value for property: " + child.getKey());
            return null;
        }
        return values.get(0).getString();
    }

    @Override
    public int init() {
        return 0;
    }

    @Override
    public int read() {
        if (useSystemClassLoader) {
            Thread.currentThread().setContextClassLoader(ClassLoader.getSystemClassLoader());
        }

        try(JmxConnector con = new JmxConnector(jmxServiceURL, jmxUsername, jmxPassword)){
            List<RouteAttribute> attributes = con.camelAttrs();
            for (RouteAttribute attribute : attributes) {
                ValueList v = new ValueList();
                v.setHost(monitoredHostname);
                v.setPlugin("CamelPlugin");
                v.setPluginInstance(attribute.route);
                v.setType("gauge");
                v.setTypeInstance(attribute.name);
                v.addValue(attribute.value);
                Collectd.dispatchValues(v);

                v.clearValues();
            }
        } catch (Exception e) {
            Collectd.logError(e.getMessage());
        }
        return 0;
    }

    @Override
    public int shutdown() {
        return 0;
    }
}
