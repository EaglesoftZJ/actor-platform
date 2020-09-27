package im.actor.sdk.controllers.activity.base;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

public abstract class Controller<T extends AppCompatActivity> {

    private T activity;

    protected Controller(T activity) {
        this.activity = activity;
    }

    public Resources getResources() {
        return activity.getResources();
    }

    public Intent getIntent() {
        return activity.getIntent();
    }

    public View findViewById(int id) {
        return activity.findViewById(id);
    }

    public void startActivity(Intent intent) {
        activity.startActivity(intent);
    }

    public T getActivity() {
        return activity;
    }

    public void setContentView(int id) {
        activity.setContentView(id);
    }

    public FragmentManager getFragmentManager() {
        return activity.getSupportFragmentManager();
    }

    public MenuInflater getMenuInflater() {
        return activity.getMenuInflater();
    }

    public ActionBar getActionBar() {
        return activity.getSupportActionBar();
    }

    public void onCreate(Bundle savedInstance) {

    }

    public void onResume() {

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    public boolean onBackPressed() {
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    public void onConfigurationChanged(Configuration newConfig) {

    }

    public void onPause() {

    }


    public void onNewIntent(Intent intent) {
    }

    public void onSaveInstanceState(Bundle outState) {

    }
}
