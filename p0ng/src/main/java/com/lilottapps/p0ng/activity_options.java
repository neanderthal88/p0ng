package com.lilottapps.p0ng;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

/**
 * Created by jason on 12/9/13.
 */
public class activity_options extends Activity {

    Context appContext;
    SeekBar music;
    SeekBar effects;
    Button save;

    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_options);
        this.appContext = this;

        this.music = (SeekBar) findViewById(R.id.sbMusicVolume);
        this.effects = (SeekBar) findViewById(R.id.sbEffectsVolume);
        this.save = (Button) findViewById(R.id.buttonOptionsSave);
        //this.save.setOnClickListener(this);

        /*
            Our OnClickListener for our seekbar
         */
        this.music.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b) {
                    savePreferences("musicVolume", i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        /*
            Our OnClickListener for our seekbar
         */
        this.effects.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b) {
                    savePreferences("effectsVolume", i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        loadSavedPreferences();
    }

    public void onStart() {
        super.onStart();
    }

    private void loadSavedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Get our shared preference values
        int musicVolume = sharedPreferences.getInt("musicVolume", 50);
        int effectsVolume = sharedPreferences.getInt("effectsVolume", 50);
        // set our seekbars with our values
        this.music.setProgress(musicVolume);
        this.effects.setProgress(effectsVolume);
    }

    private void savePreferences(String key, int value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

/*
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        savePreferences("CheckBox_Value", checkBox.isChecked());
        if (checkBox.isChecked())
        savePreferences("storedName", editText.getText().toString());
        finish();
    }
    */
}
