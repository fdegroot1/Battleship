package data;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

public abstract class Ship implements Serializable {
    private int length;
    private ArrayList<Point2D> position;
    private String name;
    private boolean vertical;

    public Ship(String name, int length, Point2D position, boolean vertical) {
        this.name = name;
        this.length = length;
        this.position   = new ArrayList<>();
        this.position.add(position);
        this.vertical = vertical;
        if(vertical){
            for (int i=1; i<length; i++){
                this.position.add(new Point2D.Double(position.getX(),position.getY()+i));
            }
        }
        else {
            for (int i=1; i<length; i++){
                this.position.add(new Point2D.Double(position.getX()+i,position.getY()));
            }
        }
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
        return this.position;
    }

}
