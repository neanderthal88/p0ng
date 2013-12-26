package com.lilottapps.p0ng.objects;

import android.graphics.Canvas;
import android.util.Log;

import java.io.File;

/**
 * Created by jason on 12/25/13.
 */
public abstract class PowerUps {

    final String TAG = "p0ng";
    final int BASE_USE_TIME = 10;
    int useTime;
    int affectedPlayer;
    int height;
    int width;

    public void setDimensions(int h, int w) {
        this.height = h;
        this.width = w;
    }

    public int timeLeft() {
        return 0;
    }
    public void powerUpUseTime(int t) {
        // use this for 10-20 seconds
        this.useTime = (int) Math.ceil(Math.random()*10) + BASE_USE_TIME;
    }
    public void affectedPlayer(int i) {
        this.affectedPlayer = i;
    }
    public void draw(Canvas c) {

    }

    public void getAvailablePowerUps(final File folder) {
            for (final File fileEntry : folder.listFiles()) {
                if (fileEntry.isDirectory()) {
                    this.getAvailablePowerUps(fileEntry);
                } else {
                    Log.d(TAG, fileEntry.getName());
            }
        }
    }
}
