package data;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

public abstract class Ship implements Serializable {
    private int length;
    private ArrayList<Point2D> position;
    private String name;

    public Ship(String name, int length, ArrayList<Point2D> position) {
        this.name = name;
        this.length = length;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public ArrayList<Point2D> getPosition() {
        return position;
    }

    public void setPosition(ArrayList<Point2D> position) {
        this.position = position;
    }


}
