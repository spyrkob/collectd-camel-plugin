package com.spyrkob;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by spyrkob on 23/04/2015.
 */
public class JmxConnector implements AutoCloseable {
    private JMXConnector connector;
    private MBeanServerConnection connection;
    private static final String[] supportedAttributes = {"CamelId", "RouteId", "ExchangesTotal", "InflightExchanges", "Load01", "Load05", "Load15", "MaxProcessingTime", "MeanProcessingTime", "MinProcessingTime"};

    public JmxConnector(String serviceURL, String username, String password) throws Exception{
        JMXServiceURL jmxServiceURL = new JMXServiceURL(serviceURL);

        String[]  credentials = new String[] {username, password};
        HashMap   environment = new HashMap<>();
        environment.put (JMXConnector.CREDENTIALS, credentials);
        connector = JMXConnectorFactory.connect(jmxServiceURL, environment);
        connection = connector.getMBeanServerConnection();
    }

    public List<RouteAttribute> camelAttrs() throws Exception {

        Set<ObjectName> objectNames = connection.queryNames(new ObjectName("org.apache.camel:context=*/*,type=routes,name=\"*\""), null);

        ArrayList<RouteAttribute> res = new ArrayList<>();
        for (ObjectName objectName : objectNames) {
            AttributeList attrList = connection.getAttributes(objectName, supportedAttributes);
            String routeName = getCamelName(attrList) + "." + getRouteName(attrList);
            for (Object o : attrList) {
                javax.management.Attribute attr = (javax.management.Attribute) o;
                String name =  attr.getName();
                if (!name.equals("CamelId") && !name.equals("RouteId")) {
                    Double value = Double.parseDouble(attr.getValue().toString());
                    RouteAttribute a = new RouteAttribute(routeName, name, value);
                    res.add(a);
                }
            }
        }

        return res;
    }



    private String getCamelName(AttributeList attrList) {
        for (Object o : attrList) {
            String name = ((javax.management.Attribute) o).getName();
            String value = (String)((javax.management.Attribute) o).getValue();

            if (name.equals("CamelId")) {
                return value;
            }
        }
        return null;
    }

    private String getRouteName(AttributeList attrList) {
        for (Object o : attrList) {
            String name = ((javax.management.Attribute) o).getName();
            String value = (String)((javax.management.Attribute) o).getValue();

            if (name.equals("RouteId")) {
                return value;
            }
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        connector.close();
    }

}
