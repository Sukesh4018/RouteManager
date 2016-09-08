package com.BRP.routemanager.activites;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.BRP.routemanager.R;
import com.BRP.routemanager.UploadActivity;


public class RouteManager extends Activity {
    public static String ip = "10.192.45.7";
    private EditText ipET;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager);
        ipET = (EditText) findViewById(R.id.ip);
        ipET.setText(ip);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void openSettings() {
    }

    public void createEnd2End(View view) {
        Intent i = new Intent(RouteManager.this, RouteCreatorHome.class);
        startActivity(i);
        finish();
    }

    public void editSaved(View view) {
        Intent i = new Intent(this, EditSavedHome.class);
        startActivity(i);
        finish();
    }

    public void uploadSaved(View view) {
        Intent i = new Intent(this, UploadActivity.class);
        startActivity(i);
        finish();
    }

    public void changeIP(View view) {
        String temp = ipET.getText().toString().trim();

        if (temp.length() > 0) {
            ip = temp;
            Toast.makeText(this, "IP changed to: " + ip, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Empty IP?", Toast.LENGTH_SHORT).show();
        }
    }
}
