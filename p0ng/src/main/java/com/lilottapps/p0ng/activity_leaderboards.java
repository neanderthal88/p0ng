package com.lilottapps.p0ng;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by jason on 12/9/13.
 */
public class activity_leaderboards extends Activity{

    Context appContext;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.appContext = this;
    }

    public void onStart() {
        super.onStart();
        Toast.makeText(this.appContext, "Starting leaderboards", Toast.LENGTH_SHORT).show();
    }
}
