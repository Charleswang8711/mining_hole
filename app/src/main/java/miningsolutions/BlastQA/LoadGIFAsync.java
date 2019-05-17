package miningsolutions.BlastQA;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.widget.ImageView;

public class LoadGIFAsync extends Thread {

    Activity activity;
    String filename;

    public LoadGIFAsync(Activity activity, String filename) {
        this.activity = activity;
    }

    @Override
    public void run() {

        // Template for threading

    }
}
