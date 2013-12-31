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
    private List<String> powerUpsList = Arrays.asList("FasterBall", "FasterPaddle", "HidePaddle", "SlowerBall", "SlowerPaddle", "Wall", "WiderPaddle");
    // List of classes of powerups that are avaiable to draw
    //private ArrayList<PowerUps> powerUpClasses = new ArrayList<PowerUps>();
    // flag to determine if a powerup is available to draw
    public boolean powerUpActive;
    // Our random number generator
    Random random = new Random();
    public int x;
    public int y;
    private PowerUps powerUp = null;
    // Lets us know which player controls the power up
    public int whichPlayer = 0;

    public PowerUps() {
        this.powerUpActive = false;
        this.setCircleVariables();
        this.powerUp = this.getNewPowerUp();
    }

    public PowerUps(int h, int w, Context c, Paddle p1, Paddle p2, Ball b) {
        this.width = w;
        this.height = h;
        this.appContext = c;
        this.playerOne = p1;
        this.playerTwo = p2;
        this.ball = b;
        this.powerUpActive = false;
        this.setCircleVariables();
        this.powerUp = this.getNewPowerUp();
    }

    private PowerUps getNewPowerUp() {
        Paddle p = null;
        if(this.whichPlayer == 1) {
            p = this.playerOne;
        } else {
            p = this.playerTwo;
        }
        switch((int)Math.ceil(this.random.nextInt(this.powerUpsList.size()))) {
            case 0:
                return new FasterBall();
            case 1:
                return new FasterPaddle();
            case 2:
                return new HidePaddle();
            case 3:
                return new SlowerBall();
            case 4:
                return new SlowerPaddle();
            case 5:
                return new Wall();
            case 6:
                return new WiderPaddle();
            default:
                return new FasterBall();
        }
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

    public void setWhichPlayer(int d) {
        this.whichPlayer = d;
    }
    /**
     * To draw the new location of a power-up call this
     */
    public void setCircleVariables() {
        Log.d(TAG, "Setting the variables for our power-up circle");
        int radius = 10;
        /**
         * TODO: subtract out the radius so we cannot draw offscreen
         */
        this.x = (int)(Math.random() * ((this.height) + 1)) + radius;
        this.y = (int)(Math.random() * ((this.width) + 1)) + radius;
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawCircle(this.x, this.y, 10, paint);
    }

    public List<String> getAvailablePowerUpsStrings() {
        return this.powerUpsList;
    }

    public void activatePowerUp() {

    }
}
