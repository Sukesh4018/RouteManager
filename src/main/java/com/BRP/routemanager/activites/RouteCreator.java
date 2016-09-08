package com.BRP.routemanager.activites;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.DialogInterface;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.BRP.routemanager.utils.DbHelper;
import com.BRP.routemanager.ListFiles;
import com.BRP.routemanager.R;
import com.BRP.routemanager.UploadActivity;
import com.BRP.routemanager.app.rmApp;

import java.lang.System;

public class RouteCreator extends Activity
        implements LocationListener, GpsStatus.Listener {
    private LocationManager lM;
    private Location CurrLocation;
    private boolean isGPSFix;
    private long mLastLocationMillis;

    private TextView showLoc;
    private EditText stop, stopLat, stopLon;

    public static String routeName, cityName;
    public DbHelper dbHelper;

    public static int ctr;

    public void onGpsStatusChanged(int event) {
        switch (event) {
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                if (CurrLocation != null)
                    isGPSFix = (SystemClock.elapsedRealtime() - mLastLocationMillis) < 5000;

                if (!isGPSFix) {
                    if (CurrLocation == null)
                        showLoc.setText("Location Unavailable!");
                    else
                        showLoc.setText("Location lost...");
                }

                break;
            default:
                break;
        }
    }

    @Override
    public void onLocationChanged(Location loc) {
        CurrLocation = loc;
        updateUI();
        if (loc != null)
            mLastLocationMillis = SystemClock.elapsedRealtime();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, getString(R.string.gps_enabled), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, getString(R.string.gps_disabled), Toast.LENGTH_SHORT).show();
        showSettingsAlert();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (provider == LocationManager.GPS_PROVIDER) {
            switch (status) {
                case LocationProvider.OUT_OF_SERVICE:
                    Toast.makeText(this, getString(R.string.gps_unav), Toast.LENGTH_SHORT).show();
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Toast.makeText(this, getString(R.string.gps_temp_unav), Toast.LENGTH_SHORT).show();
                    break;
                case LocationProvider.AVAILABLE:
                    Toast.makeText(this, getString(R.string.gps_av), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creatormain);
        Intent i = getIntent();

        routeName = i.getStringExtra(getString(R.string.routeKey));
        cityName = i.getStringExtra(getString(R.string.cityKey));

        showLoc = (TextView) findViewById(R.id.showLoc);
        stop = (EditText) findViewById(R.id.stopName);
        stopLat = (EditText) findViewById(R.id.stopLat);
        stopLon = (EditText) findViewById(R.id.stopLon);

        if (savedInstanceState != null) {
            showLoc.setText(savedInstanceState.getString("showLoc"));
            stop.setText(savedInstanceState.getString("stop"));
            stopLat.setText(savedInstanceState.getString("stopLat"));
            stopLon.setText(savedInstanceState.getString("stopLon"));
            isGPSFix = savedInstanceState.getBoolean("isGPSFix");
            ctr = savedInstanceState.getInt("ctr");
            mLastLocationMillis = savedInstanceState.getLong("mLLM");
        } else {
            ctr = 1;
            stop.setText(routeName.substring(routeName.indexOf("_From_") + 6, routeName.indexOf("_Towards_")).replaceAll("_", " "));
            isGPSFix = false;
            mLastLocationMillis = 0;
        }

        lM = (LocationManager) getSystemService(LOCATION_SERVICE);
        lM.addGpsStatusListener(this);

        dbHelper = new DbHelper(rmApp.getAppContext(), cityName, routeName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        lM.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        lM.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        lM.removeUpdates(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("showLoc", showLoc.getText().toString());
        outState.putString("stop", stop.getText().toString());
        outState.putString("stopLat", stopLat.getText().toString());
        outState.putString("stopLon", stopLon.getText().toString());
        outState.putBoolean("isGPSFix", isGPSFix);
        outState.putInt("ctr", ctr);
        outState.putLong("mLLM", mLastLocationMillis);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbHelper.closeDB();
    }

    @Override
    public void onBackPressed() {
        dbHelper.delTable();
        dbHelper.closeDB();
        Intent intent = new Intent(this, RouteCreatorHome.class);
        startActivity(intent);
        finish();
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

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is disabled!");

        // Setting Dialog Message
        alertDialog.setMessage("Please enable GPS to use this app!");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
  
 /*       // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        }); */

        // Showing Alert Message
        alertDialog.show();
    }

    public void showNotCompleteAlert(final Class act) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("Route may not be Complete!");

        alertDialog.setMessage("Route's last stop doesn't match the destination.\nAre you sure you wish to continue?");

        alertDialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dbHelper.closeDB();
                Intent intent = new Intent(RouteCreator.this, act);
                intent.putExtra(getString(R.string.routeKey), routeName);
                intent.putExtra(getString(R.string.cityKey), cityName);
                intent.putExtra(getString(R.string.parentKey), "create");
                startActivity(intent);
                finish();
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    protected void updateUI() {
        String coord = getResources().getString(R.string.loc_unav);

        if (CurrLocation != null) {
            String lat = String.valueOf(CurrLocation.getLatitude());
            String lon = String.valueOf(CurrLocation.getLongitude());
            String txt = getResources().getString(R.string.loc_av);
            String acc = getResources().getString(R.string.loc_acc);
            String acc_val = String.valueOf(CurrLocation.getAccuracy());
            coord = txt + "\n" + lat + " , " + lon + "\n" + acc + "\n" + acc_val + " m.";
        }

        showLoc.setText(coord);
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public void CopyLoc(View view) {
        if (CurrLocation != null) {
            stopLat.setText(String.valueOf(CurrLocation.getLatitude()));
            stopLon.setText(String.valueOf(CurrLocation.getLongitude()));
        }
    }


    private boolean valid() {
        if (stop.getText().toString().trim().length() == 0) {
            Toast.makeText(this, getString(R.string.stopEmpty), Toast.LENGTH_SHORT).show();
            return false;
        } else if (stopLat.getText().toString().trim().length() == 0) {
            Toast.makeText(this, getString(R.string.latEmpty_error), Toast.LENGTH_SHORT).show();
            return false;
        } else if (stopLon.getText().toString().trim().length() == 0) {
            Toast.makeText(this, getString(R.string.lonEmpty_error), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Double lat = Double.parseDouble(stopLat.getText().toString());
            Double lon = Double.parseDouble(stopLon.getText().toString());

            if (lat > 90 || lat < -90) {
                Toast.makeText(this, getString(R.string.latRange_error), Toast.LENGTH_SHORT).show();
                return false;
            } else if (lon > 180 || lon < -180) {
                Toast.makeText(this, getString(R.string.lonRange_error), Toast.LENGTH_SHORT).show();
                return false;
            } else
                return true;
        }
    }

    private void reset() {
        stop.setText(getResources().getString(R.string.stop) + String.valueOf(ctr));
        stopLat.setText("");
        stopLon.setText("");
    }

    public void AddStop(View view) {
        if (valid()) {
            dbHelper.addStop(stop.getText().toString().trim(), stopLat.getText().toString(), stopLon.getText().toString(), String.valueOf(System.currentTimeMillis()));
            ctr++;
            reset();
            Toast.makeText(this, getString(R.string.add_success), Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadRoute(View view) {
        /*String lastStop = dbHelper.getLastStop();
        String dest = routeName.substring(routeName.indexOf("_Towards_") + 9);

        if (lastStop == "") {
            Toast.makeText(this,"You haven't added any stop to this route!",Toast.LENGTH_SHORT).show();
            return;
        }

        else if ( lastStop.toLowerCase().equals(dest.toLowerCase()) == false ) {
            showNotCompleteAlert(UploadActivity.class);
        }

        else {
            dbHelper.closeDB();
            Intent intent = new Intent(this,UploadActivity.class);
            intent.putExtra(getString(R.string.routeKey),routeName);
            intent.putExtra(getString(R.string.cityKey),cityName);
            startActivity(intent);
            finish();
        }*/
    }

    public void save(View view) {
       /* String lastStop = dbHelper.getLastStop();
        String dest = routeName.substring(routeName.indexOf("_Towards_") + 9);

        if (lastStop == "") {
            Toast.makeText(this, "You haven't added any stop to this route!", Toast.LENGTH_SHORT).show();
            return;
        } else if (lastStop.toLowerCase().equals(dest.toLowerCase()) == false) {
            showNotCompleteAlert(ListFiles.class);
        } else {
            dbHelper.closeDB();
            Intent intent = new Intent(this, ListFiles.class);
            intent.putExtra(getString(R.string.routeKey), routeName);
            intent.putExtra(getString(R.string.cityKey), cityName);
            intent.putExtra(getString(R.string.parentKey), "create");
            startActivity(intent);
            finish();
        }*/
    }

    public void cancel(View view) {
        dbHelper.delTable();
        dbHelper.closeDB();
        Intent intent = new Intent(this, RouteCreatorHome.class);
        startActivity(intent);
        finish();
    }
}