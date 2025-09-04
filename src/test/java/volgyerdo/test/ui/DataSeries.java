/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package volgyerdo.test.ui;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;

/**
 *
 * @author zsolt
 */
public class DataSeries {

    private String name;
    private List<Point2D> points;
    private Color color;
    private boolean connected;
    private boolean bullets;

    public DataSeries(String name, List<Point2D> points, Color color) {
        this(name, points, color, false, true);
    }

    public DataSeries(String name, List<Point2D> points, Color color, boolean connected, boolean bullets) {
        this.name = name;
        this.points = points;
        this.color = color;
        this.connected = connected;
        this.bullets = connected ? bullets : true;
    }

    public String getName() {
        return name;
    }

    public List<Point2D> getPoints() {
        return points;
    }

    public Color getColor() {
        return color;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean hasBullets() {
        return bullets;
    }

    
}