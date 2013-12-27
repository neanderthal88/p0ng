package com.lilottapps.p0ng.objects;

import android.content.Context;
import android.graphics.Canvas;
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
    public boolean powerUpReady;
    // Our random number generator
    Random random = new Random();

    public PowerUps() {
        //this.powerUpClasses.add(new FasterBall());
        //this.powerUpClasses.add(new FasterPaddle());
        this.powerUpClasses.add(new SlowerBall());
        this.powerUpClasses.add(new SlowerPaddle());
        this.powerUpClasses.add(new Wall());
        this.powerUpClasses.add(new WiderPaddle());
        this.powerUpClasses.add(new HidePaddle());
        this.setTimeLeft();
        this.powerUpReady = false;
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
            this.setTimeLeft();
            this.powerUpReady = false;
    }

    /** Timing control for power-up readiness **/
    public void setTimeLeft() {
        this.timeLeftForPowerup = (int) Math.ceil(this.random.nextInt(10) + BASE_POWERUP_RECHARGE_TIME);
    }

    public void decreaseTimeLeft() {
        this.timeLeftForPowerup--;
        if(this.timeLeftForPowerup <1) {
            this.powerUpReady = true;
        }
    }

    public int getTimeLeft() {
        return this.timeLeftForPowerup;
    }
    /** End Timing control for power-up readiness **/

    public void powerUpUseTime(int t) {
        // use this for 10-20 seconds
        this.useTime = (int) Math.ceil(Math.random()*10) + BASE_USE_TIME * 1000;
    }

    public void affectedPlayer(int i) {
        this.affectedPlayer = i;
    }

    // this should return a powerups
    public int getNewPowerUp() {
        if(this.powerUpReady) {
            // set this to false as we are getting a new powerup!
            this.powerUpReady = false;
            //return this.powerUpClasses.get(random.nextInt()%this.powerUpClasses.size());
            Log.d(TAG, "We are retrieving a power-up");
            return 1;
        } else {
            return 1;
        }
    }

    public void draw(Canvas c) {

    }

    public List<PowerUps> getAvailablePowerUpsClasses() {
        return this.powerUpClasses;
    }

    public List<String> getAvailablePowerUpsStrings() {
        return this.powerUps;
    }
}
