package com.lilottapps.p0ng.objects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.lilottapps.p0ng.views.P0ngView;

import java.util.Random;

/**
 * Created by jason on 12/9/13.
 */
public class Paddle {

    private static final int DEFAULT_SIZE = 10;
    private static final int DEFAULT_SPEED = 30;

    public static final String TAG = "p0ng";

    private int windowHeight;
    private int windowWidth;
    private int pos = 0;
    private Rect paddle;

    protected int color;
    protected Rect rect;
    protected Rect touch;
    protected int handicap = 0;
    protected int speed = 30;
    protected int lives = 1;
    protected Canvas canvas;

    /** Thickness of the paddle */
    public static final int PADDLE_THICKNESS = 10;

    /** Width of the paddle */
    private static final int PADDLE_WIDTH = 40;

    protected Paint paint;

    public boolean player = false;

    public int destination;

    public Random RNG = new Random();

    public Paddle(int c, int y, int height, int width) {
        if(c < 1) {
            this.color = Color.WHITE;
        } else {
            this.color = c;
        }
        this.paint = new Paint();
        this.paint.setColor(this.color);
        this.paint.setStyle(Paint.Style.FILL_AND_STROKE);
        // Height of the window
        this.windowHeight = height;
        // Width of the window
        this.windowWidth = width;
        // Mid field of the plane
        int mid = this.windowWidth / 2;
        // Draw a new rectangle, bottom, left, right, top
        this.rect = new Rect(mid - PADDLE_WIDTH, y,
                mid + PADDLE_WIDTH, y + PADDLE_THICKNESS);
        this.destination = mid;
        this.setLives(3);
    }

    public void move() {
        move(this.speed);
    }

    public void move(boolean handicapped) {
        move((handicapped) ? this.speed - this.handicap : this.speed);
    }

    public void move(int s) {
        int dx = (int) Math.abs(this.rect.centerX() - destination);

        if(destination < this.rect.centerX()) {
            this.rect.offset( (dx > s) ? -s : -dx, 0);
        }
        else if(destination > this.rect.centerX()) {
            this.rect.offset( (dx > s) ? s : dx, 0);
        }
    }

    public void setLives(int lives) {
        this.lives = Math.max(0, lives);
    }

    public void setPosition(int x) {
        this.rect.offset(x - this.rect.centerX(), 0);
    }

    public void setTouchbox(Rect r) {
        this.touch = r;
    }

    public void setSpeed(int s) {
        this.speed = (s > 0) ? s : this.speed;
    }

    public void setHandicap(int h) {
        this.handicap = (h >= 0 && h < this.speed) ? h : this.handicap;
    }

    public boolean inTouchbox(int x, int y) {
        return this.touch.contains(x, y);
    }

    public void loseLife() {
        this.lives = Math.max(0, this.lives - 1);
    }

    public boolean living() {
        return this.lives > 0;
    }

    public int getWidth() {
        return Paddle.PADDLE_WIDTH;
    }

    public int getTop() {
        return this.rect.top;
    }

    public int getBottom() {
        return this.rect.bottom;
    }

    public int centerX() {
        return this.rect.centerX();
    }

    public int centerY() {
        return this.rect.centerY();
    }

    public int getLeft() {
        return this.rect.left;
    }

    public int getRight() {
        return this.rect.right;
    }

    public int touchCenterY() {
        return this.touch.centerY();
    }

    public int getLives() {
        return this.lives;
    }

    public void draw(Canvas canvas) {
        //try {
            //this.paint.setColor(this.color);
            //this.paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(this.rect, this.paint);
        //} catch(NullPointerException e) {
        //    Log.d(TAG, "canvas is null!?");
        //}
    }

    public void drawTouchbox(Canvas canvas) {
        //try {
        this.paint.setColor(this.color);
        this.paint.setStyle(Paint.Style.STROKE);
        //} catch (NullPointerException e) {
        //    Log.d(TAG, "our touchbox is null!?");
        //}
        // Heuristic for deciding which line to paint:
        // draw the one closest to middle
        int mid = this.windowHeight / 2;
        int top = Math.abs(this.touch.top - mid), bot = Math.abs(this.touch.bottom - mid);
        float y = (top < bot) ? this.touch.top : this.touch.bottom;
        //try {
        canvas.drawLine(this.touch.left, y, this.touch.right, y, this.paint);
        //} catch (NullPointerException e) {
        //    Log.d(TAG, "we can't drawLine because paint is null!?");
        //}
    }

    public boolean collides(Ball b) {
        boolean t = b.x >= this.rect.left && b.x <= this.rect.right &&
                b.y >= this.rect.top - Ball.RADIUS && b.y <= this.rect.bottom + Ball.RADIUS;
        Log.d(TAG, "Collides: " + Boolean.toString(t));
        return t;
    }

    public int getPaddleThickness() {
        return this.PADDLE_THICKNESS;
    }
    public int getPaddleWidth() {
        return this.PADDLE_WIDTH;
    }
}