package com.release.stepzone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class ResetReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        int steps = intent.getIntExtra("steps", -98765431);
        Toast.makeText(context, "steps : " + steps, Toast.LENGTH_LONG).show();
        Log.wtf("Reset Receiver", "steps : " + steps);
    }
}
