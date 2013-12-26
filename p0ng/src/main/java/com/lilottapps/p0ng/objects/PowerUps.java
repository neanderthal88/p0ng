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
    final int BASE_USE_TIME = 10;
    final int BASE_POWERUP_RECHARGE_TIME = 30;
    int useTime;
    int affectedPlayer;
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
            this.powerUpReady = false;
    }

    public int timeLeft(int timePassed) {
        return Math.abs(timePassed - BASE_POWERUP_RECHARGE_TIME);
    }

    public void powerUpUseTime(int t) {
        // use this for 10-20 seconds
        this.useTime = (int) Math.ceil(Math.random()*10) + BASE_USE_TIME;
    }

    public void affectedPlayer(int i) {
        this.affectedPlayer = i;
    }

    public PowerUps getNewPowerUp() {
        if(this.powerUpReady) {
            // set this to false as we are getting a new powerup!
            this.powerUpReady = false;
            return this.powerUpClasses.get(random.nextInt()%this.powerUpClasses.size());
        } else {
            return null;
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
