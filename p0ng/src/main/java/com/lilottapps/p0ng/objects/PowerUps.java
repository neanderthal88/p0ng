package com.lilottapps.p0ng.objects;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import com.lilottapps.p0ng.powerups.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by jason on 12/25/13.
 */
public class PowerUps {

    final String TAG = "p0ng";
    // Const: Base length time we can use a power-up
    final int BASE_USE_TIME = 10;
    // Const: Length of time between power-ups
    final int BASE_POWERUP_RECHARGE_TIME = 30;
    // Length of time we can use a power-up
    int useTime;
    // Which player owns this power-up
    int affectedPlayer;
    // Time before power-up is ready
    int timeLeftForPowerup;

    // height of window
    int height;
    // width of window
    int width;
    Canvas canvas;
    // Our paddles
    Paddle playerOne;
    Paddle playerTwo;
    // Ball
    Ball ball;
    // App context
    Context appContext;
    // Probably deprecated
    private List<String> powerUps = Arrays.asList("FasterBall", "FasterPaddle", "SlowerBall", "Wall", "WiderPaddle");
    // List of classes of powerups that are avaiable to draw
    private ArrayList<PowerUps> powerUpClasses = new ArrayList<PowerUps>();
    // flag to determine if a powerup is available to draw
    public boolean powerUpActive;
    // Our random number generator
    Random random = new Random();
    protected int x;
    protected int y;

    public PowerUps() {
        //this.powerUpClasses.add(new FasterBall());
        //this.powerUpClasses.add(new FasterPaddle());
        this.powerUpClasses.add(new SlowerBall());
        this.powerUpClasses.add(new SlowerPaddle());
        this.powerUpClasses.add(new Wall());
        this.powerUpClasses.add(new WiderPaddle());
        this.powerUpClasses.add(new HidePaddle());
        this.powerUpActive = false;
        this.setCircleVariables();
    }

    public PowerUps(int h, int w, Context c, Paddle p1, Paddle p2, Ball b) {
            this.width = w;
            this.height = h;
            this.appContext = c;
            this.playerOne = p1;
            this.playerTwo = p2;
            this.ball = b;
            //this.powerUpClasses.add(new FasterBall());
            //this.powerUpClasses.add(new FasterPaddle());
            //this.powerUpClasses.add(new SlowerBall());
            //this.powerUpClasses.add(new SlowerPaddle());
            //this.powerUpClasses.add(new Wall());
            //this.powerUpClasses.add(new WiderPaddle());
            //this.powerUpClasses.add(new HidePaddle());
            this.powerUpActive = false;
            this.setCircleVariables();
    }

    /** Timing control for power-up readiness **/
    public int getPowerUpTimer() {
        return (int) Math.ceil(this.random.nextInt(10) + BASE_POWERUP_RECHARGE_TIME);
    }
    /** End Timing control for power-up readiness **/

    public int powerUpUseTime() {
        // use this for 10-20 seconds
        return (int) Math.ceil(Math.random()*10) + BASE_USE_TIME * 1000;
    }

    public boolean activePowerUp() {
        return this.powerUpActive;
    }

    public void affectedPlayer(int i) {
        this.affectedPlayer = i;
    }

    // this should return a powerups
    public int getNewPowerUp() {
        if(this.powerUpActive) {
            // set this to false as we are getting a new powerup!
            this.powerUpActive = false;
            //return this.powerUpClasses.get(random.nextInt()%this.powerUpClasses.size());
            Log.d(TAG, "We are retrieving a power-up");
            return 1;
        } else {
            return 1;
        }
    }

    /**
     * To draw the new location of a power-up call this
     */
    public void setCircleVariables() {
        Log.d(TAG, "Setting the variables for our power-up circle");
        int radius = 10;
        this.x = (int)(Math.random() * ((this.height) + 1)) + radius;
        this.y = (int)(Math.random() * ((this.width) + 1)) + radius;
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawCircle(this.x, this.y, 10, paint);
    }

    public List<PowerUps> getAvailablePowerUpsClasses() {
        return this.powerUpClasses;
    }

    public List<String> getAvailablePowerUpsStrings() {
        return this.powerUps;
    }
}
