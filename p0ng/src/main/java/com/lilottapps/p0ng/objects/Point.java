package com.lilottapps.p0ng.objects;

/**
 * Created by jason on 12/27/13.
 */
public class Point {

    private int x, y;
    Point() {
        x = 0; y = 0;
    }

    Point(int x, int y) {
        this.x = x; this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y ; }
    public void set(double d, double e) { this.x = (int) d; this.y = (int) e; }

    public void translate(int i, int j) { this.x += i; this.y += j; }

    @Override
    public String toString() {
        return "Point: (" + x + ", " + y + ")";
    }
}