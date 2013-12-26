package com.lilottapps.p0ng.objects;

/**
 * Created by jason on 12/25/13.
 */
public interface PowerUps {

    //int useTime;
    //int affectedPlayer;

    public int timeLeft();
    public void powerUpUseTime(int t);
    public void affectedPlayer(int i);
}
