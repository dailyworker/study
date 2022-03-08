package factorymethod;

import abstract_factorymethod.Anchor;
import abstract_factorymethod.Wheel;
import abstract_factorymethod.WhiteAnchor;
import abstract_factorymethod.WhiteWheel;

public class Ship {
    private String name;
    private String logo;
    private String color;
    private Anchor ancor;
    private Wheel wheel;

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

    public void setAnchor(Anchor anchor) {
        this.ancor = anchor;
    }

    public void setWheel(Wheel wheel) {
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
