package com.example.minoru.myapplication;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import static android.provider.Settings.*;
import static android.widget.Toast.*;

public class MainActivity extends Activity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //private GoogleApiClient client;
    final Integer MinTime = 15 * 1000;
    final Integer MaxTime = 30 * 60 * 1000;
    private Integer mTimeOut = MinTime;
    private StringBuilder mTimeOutMessage = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        switchTimeOut();

        makeTimeOutMessage();

        showTimeOutMessageToToast();

        notifyTimeOut();

        this.finish();
    }

    private void notifyTimeOut() {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);

        setNotificationIcon(notificationBuilder);
        setNotificationText(notificationBuilder);

        setNotificationForever(notificationBuilder);

        setApplicationToPushNotification(notificationBuilder);

        NotificationManager notificationManager =
                (NotificationManager)getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build());
    }

    private void setApplicationToPushNotification(NotificationCompat.Builder notificationBuilder) {
        PendingIntent pending = PendingIntent.getActivity(this,
                0,
                new Intent(this, MainActivity.class),
                0);
        notificationBuilder.setContentIntent(pending);
    }

    private void setNotificationForever(NotificationCompat.Builder notificationBuilder) {
        notificationBuilder.setOngoing(true);
    }

    private void setNotificationText(NotificationCompat.Builder notificationBuilder) {
        notificationBuilder.setContentTitle("点灯時間");
        notificationBuilder.setContentText(mTimeOutMessage);
        notificationBuilder.setTicker(mTimeOutMessage);
    }

    private void setNotificationIcon(NotificationCompat.Builder notificationBuilder) {
        if(mTimeOut.equals(MinTime)){
            notificationBuilder.setSmallIcon(R.drawable.ic_stat_light_time_short);
        }else{
            notificationBuilder.setSmallIcon(R.drawable.ic_stat_light_time_long);
        }
    }

    private void makeTimeOutMessage() {
        mTimeOutMessage.append(mTimeOut/1000);
        mTimeOutMessage.append(getString(R.string.setting_message));
    }

    private void showTimeOutMessageToToast() {
        makeText(getApplicationContext(), mTimeOutMessage.toString(), LENGTH_SHORT).show();
    }

    private void switchTimeOut() {
        ContentResolver cr = getContentResolver();
        try {
            mTimeOut = Settings.System.getInt(cr, Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        if(mTimeOut.equals(MinTime)){
            Log.w("timeOut", "set to max");
            mTimeOut = MaxTime;
        }else{
            Log.w("timeOut", "set to min ###################### ");
            mTimeOut = MinTime;
        }
        Settings.System.putInt(cr, Settings.System.SCREEN_OFF_TIMEOUT, mTimeOut);
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
