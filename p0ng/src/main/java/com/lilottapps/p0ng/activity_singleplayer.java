package com.lilottapps.p0ng;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.lilottapps.p0ng.views.P0ngView;
//import com.lilottapps.p0ng.views.PongView;

/**
 * Created by jason on 12/9/13.
 */
public class activity_singleplayer extends Activity {

    private P0ngView pongView;
    // Probably will not use
    private AlertDialog mAboutBox;
    protected PowerManager.WakeLock mWakeLock;

    public static String
        PLAYER_ONE = "player-one-is-player",
        PLAYER_TWO = "player-two-is-player";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Inflate our view
        setContentView(R.layout.activity_singleplayer);

        this.pongView = (P0ngView) findViewById(R.id.p0ngView);

        this.pongView.setPlayerControl(true, false);
        try {
            this.pongView.update();
        } catch(NullPointerException e) {
            Log.d("p0ng", e.toString());
        }
        //this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        final PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "p0ng");
        mWakeLock.acquire();
    }

    public void onStart() {
        super.onStart();
        Toast.makeText(this, "Starting single player game", Toast.LENGTH_SHORT).show();
    }

    protected void onStop() {
        super.onStop();
        //mPongView.stop();
    }

    protected void onResume() {
        super.onResume();
        //mPongView.resume();
    }

    protected void onDestroy() {
        super.onDestroy();
        //this.pongView.release();
        mWakeLock.release();
    }
    public void bound() {

    }
}
