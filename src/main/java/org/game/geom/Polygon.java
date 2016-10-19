package org.game.geom;

import java.util.ArrayList;
import java.util.List; 
import org.game.math.Matrix2D;
import org.game.math.Point2D;
import org.game.math.Vector2D;

public class Polygon implements Shape {

    protected List<Point2D> mPoints = new ArrayList<>();
    protected List<Vector2D> mEdges = new ArrayList<>();
    protected List<Vector2D> mNormals = new ArrayList<>();

    public Polygon() {
    }

    public Polygon(List<Point2D> p) {
        mPoints.addAll(p);
        
        for (int n = 0; n < getPoints().size(); n++) {
            Point2D p1 = getPoint(n);
            Point2D p2 = getPoint(n + 1);
            
            Vector2D v = new Vector2D(p2).sub(p1);
            
            mEdges.add(v);
            mNormals.add(new Vector2D(v).perp().norm());
        }
    }

    public int[] getXPoints() {
        return Point2D.getXPoints(mPoints);
    }

    public int[] getYPoints() {
        return Point2D.getYPoints(mPoints);
    }

    public List<Point2D> getPoints() {
        return mPoints;
    }

    public void transform(Matrix2D m) {
        transform(m, getPosition());
    }

    public void transform(Matrix2D m, Point2D c) {
        m = Matrix2D.translate(c.getX(), c.getY())
                    .concat(m)
                    .concat(Matrix2D.translate(-c.getX(), -c.getY()));

        for (Point2D p : getPoints()) {
            double x = p.getX();
            double y = p.getY();

            p.setX((int) (x * m.getA() + y * m.getC() + m.getE()));
            p.setY((int) (x * m.getB() + y * m.getD() + m.getF()));
        }
    }

    public Point2D getPosition() {
        int len = mPoints.size();

        Vector2D v = new Vector2D();

        for (int n = 0; n < len; ++n) {
            v = v.add(new Vector2D(mPoints.get(n).getX(), mPoints.get(n).getY()));
        }

        int x = (int) (v.getX() / len);
        int y = (int) (v.getY() / len);

        return new Point2D(x, y);
    }

    public Point2D getPoint(int n) {
        int c = mPoints.size();

        return mPoints.get(n < 0 ? 
                                n % c + c : 
                                n % c);
    }
    
    public Vector2D getEdge(int n) {
        int c = mEdges.size();

        return new Vector2D(mEdges.get(n < 0 ? 
                                n % c + c : 
                                n % c));
    }
    
    public void add(Point2D p) {
        mPoints.add(p);
    }
    
    public java.awt.Polygon toPolygon() {
        return new java.awt.Polygon(getXPoints(), getYPoints(), getPoints().size());
    }
}
