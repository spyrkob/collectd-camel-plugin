package com.spyrkob;

class RouteAttribute {
    private final String route, name;
    private final Double value;

    public RouteAttribute(String route, String name, Double value) {
        this.route = route;
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Attribute{");
        sb.append("route='").append(route).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", value=").append(value);
        sb.append('}');
        return sb.toString();
    }
}
