package com.example.minoru.myapplication;

import android.app.Activity;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContentResolverCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import static android.widget.Toast.*;

public class MainActivity extends Activity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContentResolver cr = getContentResolver();
        Integer timeOut = null;
        try {
            timeOut = Settings.System.getInt(cr, Settings.System.SCREEN_OFF_TIMEOUT);
            //timeOut = 30 * 1000;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        final Integer MinTime = 15 * 1000;
        final Integer MaxTime = 30 * 60 * 1000;
        Integer newTimeOut = null;
        if(timeOut == MinTime){
            newTimeOut = MaxTime;
        }else{
            newTimeOut = MinTime;
        }
        //Settings.System.putInt(cr, Settings.System.SCREEN_OFF_TIMEOUT, newTimeOut);

        StringBuilder sb = new StringBuilder();
        sb.append("Screen off time out from: ");
        sb.append(timeOut/1000);
        sb.append(" to: ");
        sb.append(newTimeOut/1000);
        sb.append(".");

        makeText(getApplicationContext(), sb.toString(), LENGTH_SHORT).show();

        this.finish();


        //setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client.connect();
        //Action viewAction = Action.newAction(
         //       Action.TYPE_VIEW, // TODO: choose an action type.
          //      "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
           //     Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
            //    Uri.parse("android-app://com.example.minoru.myapplication/http/host/path")
        //);
        //AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //Action viewAction = Action.newAction(
         //       Action.TYPE_VIEW, // TODO: choose an action type.
          //      "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
           //     Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
            //    Uri.parse("android-app://com.example.minoru.myapplication/http/host/path")
        //);
        //AppIndex.AppIndexApi.end(client, viewAction);
        //client.disconnect();
    }
}
