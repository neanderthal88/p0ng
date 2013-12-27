package com.lilottapps.p0ng.objects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.Random;

/**
 * Created by jason on 12/9/13.
 */
public class Ball {

    public static final String TAG = "p0ng";

    private static final int DEFAULT_COLOR = Color.WHITE;
    private static final int DEFAULT_HEIGHT = 2;
    private static final int DEFAULT_WIDTH = 2;
    public float DEFAULT_SPEED = 10;
    public static final double BOUND = Math.PI / 9;
    public static final float SPEED = 10;
    public static final int RADIUS = 4;

    public static final double SALT = 4 * Math.PI / 9;
    public float x,y, xp, yp, vx, vy;

    protected double angle;
    protected boolean nextPoint = false;
    protected int counter;

    private int windowHeight;
    private int windowWidth;
    private float speed;
    private Color color;
    private Paint paint;
    private Random RNG = new Random();

    public Ball() {
        this.speed = DEFAULT_SPEED;
        this.paint = new Paint();
        this.paint.setColor(Color.WHITE);
        this.paint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.findVector();
    }

    public Ball(int h, int w) {
        this.paint = new Paint();
        this.paint.setColor(Color.WHITE);
        this.paint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.speed = DEFAULT_SPEED;
        this.windowHeight = h;
        this.windowWidth = w;
        this.findVector();
    }

    public Ball(int h, int w, int s) {
        this.windowHeight = h;
        this.windowWidth = w;
        if (s < DEFAULT_SPEED) {
            this.speed = DEFAULT_SPEED;
        } else {
            this.speed = s;
        }
        this.findVector();
    }

    public Ball(Ball b, int h, int w) {
        this.windowHeight = h;
        this.windowWidth = w;
        if(b.speed < DEFAULT_SPEED) {
            this.speed = DEFAULT_SPEED;
        } else {
            this.speed = b.speed;
        }
        this.x = b.x;
        this.y = b.y;
        this.xp = b.xp;
        this.yp = b.yp;
        this.vx = b.vx;
        this.vy = b.vy;
        this.angle = b.angle;
        this.color = b.color;
        if((b.paint.getColor() != Color.WHITE)) {
            this.paint = new Paint();
            this.paint.setColor(Color.WHITE);
            this.paint.setStyle(Paint.Style.FILL);
        } else {
            this.paint = b.paint;
        }
    }

    public void setWindowSize(int h, int w) {
        this.windowHeight = h;
        this.windowWidth = w;
    }

    public void findVector() {
        this.vx = (float) (this.speed * Math.cos(this.angle));
        this.vy = (float) (this.speed * Math.sin(this.angle));
    }
/* */
    /*
    public direction findDirection(int type) {
        return direction.findDirection(this.angle, type);
    }
    */
    public double getAngle() {
        return this.angle;
    }

    public boolean serving() {
        return counter > 0;
    }

    public void pause() {
        counter = 60;
    }

    public void move() {
        if(counter <= 0) {
            x = keepX(x + vx);
            y += vy;
        }
        else {
            counter--;
        }
    }

    public void randomAngle() {
        setAngle( Math.PI / 2 + RNG.nextInt(2) * Math.PI + Math.PI / 2 * RNG.nextGaussian() );
    }

    public void setAngle(double angle) {
        this.angle = angle % (2 * Math.PI);
        this.angle = boundAngle(this.angle);
        findVector();
    }

    public void draw(Canvas canvas) {
        if((this.counter / 10) % 2 == 1 || this.counter == 0){
                canvas.drawCircle(this.x, this.y, RADIUS, this.paint);
        }
    }

    public boolean collides(Paddle p) {
        return p.collides(this);
    }

    public void bouncePaddle(Paddle p) {
        double angle;
        // up-right case
        if(this.angle >= Math.PI) {
            angle = 4 * Math.PI - this.angle;
        }
        // down-left case
        else {
            angle = 2 * Math.PI - this.angle;
        }

        angle %= (2 * Math.PI);
        angle = this.salt(angle, p);
	    // normalize(p);
        setAngle(angle);
    }

    public void bounceWall() {
        setAngle(3 * Math.PI - this.angle);
    }

    protected double salt(double angle, Paddle paddle) {
        int cx = paddle.centerX();
        double halfWidth = paddle.getWidth() / 2;
        double change = 0.0;

        //if(findDirection(0) == direction.NORTH ||)
        if(isNorthBound()){
                change = SALT * ((cx - x) / halfWidth);
           }
        else {
                change = SALT * ((x - cx) / halfWidth);
         }

        return boundAngle(angle, change);
    }

    /**
     * Bounds sum of <code>angle</code> and <code>angleChange</code> to the side of the
     * unit circle that <code>angle</code> is on.
     * @param angle The initial angle.
     * @param angleChange Amount to add to angle.
     * @return bounded angle sum
     */
    protected double boundAngle(double angle, double angleChange) {
        return boundAngle(angle + angleChange, angle >= Math.PI);
    }

    protected double boundAngle(double angle) {
        return boundAngle(angle, angle >= Math.PI);
    }

    /**
     * Bounds an angle in radians to a subset of the top
     * or bottom part of the unit circle.
     * @param angle The angle in radians to bound.
     * @param top Flag which indicates if we should bound to the top or not.
     * @return the bounded angle
     */
    protected double boundAngle(double angle, boolean top) {
        if(top) {
            return Math.max(Math.PI + BOUND, Math.min(2 * Math.PI - BOUND, angle));
        }

        return Math.max(BOUND, Math.min(Math.PI - BOUND, angle));
    }

    /**
     * Given it a coordinate, it transforms it into a proper x-coordinate for the ball.
     * @param x, the x-coord to transform
     * @return
     */
    protected float keepX(float x) {
        return bound(x, Ball.RADIUS, this.windowWidth - Ball.RADIUS);
    }

    protected float bound(float x, float low, float hi) {
        return Math.max(low, Math.min(x, hi));
    }

    /**
     * Set the speed of a ball, this allows variablility during game time
     * @param s, what the new speed is of a ball
     */
    public void setSpeed(float s) {
        this.speed = s;
    }

    /**
     * Set the speed of the ball
     * @return float speed at twhich the ball is moving
     */
    public float getSpeed() {
        return this.speed;
    }

    /**
     * Check the north/south direction of the ball
     * @return boolean answer for question
     */
    public boolean isNorthBound() {
       return this.angle >= Math.PI;
    }

    /**
     * CHeck the East/West direction of the ball
     * @return boolean answer for question
     */
    public boolean isEastBound() {
        return this.angle <= 3 * Math.PI / 2 && this.angle > Math.PI / 2;
    }

}