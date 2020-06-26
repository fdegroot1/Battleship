package data;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Battleship extends Ship {
    public Battleship(int length, ArrayList<Point2D> position) {
        super("Battleship", length, position);
    }
}
