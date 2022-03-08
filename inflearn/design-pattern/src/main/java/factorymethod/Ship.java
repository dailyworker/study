package factorymethod;

import abstract_factorymethod.WhiteAnchor;
import abstract_factorymethod.WhiteWheel;

public class Ship {
    private String name;
    private String logo;
    private String color;
    private WhiteAnchor ancor;
    private WhiteWheel wheel;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setAnchor(WhiteAnchor anchor) {
        this.ancor = anchor;
    }

    public void setWheel(WhiteWheel wheel) {
        this.wheel = wheel;
    }

    @Override
    public String toString() {
        return "Ship{" +
                "name='" + name + '\'' +
                ", logo='" + logo + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
