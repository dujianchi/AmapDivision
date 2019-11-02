package core;

import java.util.List;

public class Entity {
    public Object citycode;
    public String adcode, name, center, level;
    public List<Entity> districts;

    @Override
    public String toString() {
        return "Entity{" +
                "citycode=" + citycode +
                ", adcode='" + adcode + '\'' +
                ", name='" + name + '\'' +
                ", center='" + center + '\'' +
                ", level='" + level + '\'' +
                ", districts=" + districts +
                '}';
    }
}
