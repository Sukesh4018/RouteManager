package com.BRP.routemanager.activites;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.BRP.routemanager.events.LocationChangedEvent;
import com.BRP.routemanager.models.Stop;
import com.BRP.routemanager.utils.CommonUtils;
import com.BRP.routemanager.utils.Constants;
import com.BRP.routemanager.utils.DbHelper;
import com.BRP.routemanager.R;
import com.BRP.routemanager.app.rmApp;
import com.BRP.routemanager.utils.LocationUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;

public class RouteCreatorHome extends FragmentActivity
        implements OnItemSelectedListener {
    /**
     * Called when the activity is first created.
     */

    public static String Route, City, Corp;
    private ArrayList<String> spinnerText, corpSpinText;
    private ArrayList<Stop> stops;

    private GoogleMap mMap;

    private static final String ROUTE = "ROUTE";
    private static final String SPINTEXT = "SPINTEXT";

    private LatLng location;

    private EditText route, city, corp, dest, src;
    private Spinner spinner, corpSpinner;
    private TextView cityLabel, corpLabel, latituteTxt, longituteTxt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creatorhome);

        route = (EditText) findViewById(R.id.route_no);
        city = (EditText) findViewById(R.id.addCity);
        corp = (EditText) findViewById(R.id.addCorp);
        /*src = (EditText) findViewById(R.id.src);
        dest = (EditText) findViewById(R.id.dest);*/
        cityLabel = (TextView) findViewById(R.id.addCityLabel);
        corpLabel = (TextView) findViewById(R.id.addCorpLabel);
        latituteTxt = (TextView) findViewById(R.id.latitute_txt);
        longituteTxt = (TextView) findViewById(R.id.longitute_txt);
        stops = new ArrayList<>();

        if (savedInstanceState != null) {
            route.setText(savedInstanceState.getString(ROUTE));
            city.setText(savedInstanceState.getString("CITY"));
            corp.setText(savedInstanceState.getString("CORP"));
            src.setText(savedInstanceState.getString("SRC"));
            dest.setText(savedInstanceState.getString("DEST"));
            stops = (ArrayList<Stop>) savedInstanceState.getSerializable("STOPS");
            spinnerText = savedInstanceState.getStringArrayList(SPINTEXT);
        } else {
            route.setText("");
            city.setText("");
            corp.setText("");
//            src.setText("");
//            dest.setText("");

            File dir = new File(DbHelper.DATABASE_PATH);
            dir.mkdirs();
            File files[] = dir.listFiles();
            int l;

            if (files != null) l = files.length;
            else l = 0;

            spinnerText = new ArrayList<String>();
            spinnerText.add(getString(R.string.selectCity));
            spinnerText.add(getString(R.string.newCity));

            corpSpinText = new ArrayList<String>();
            corpSpinText.add("Please select city first");
            corpSpinText.add(getString(R.string.newCorp));

            for (int i = 0; i < l; i++) {
                String name = files[i].getName();

                //String temp = name.substring(0, name.indexOf("_Corp_"));

                //temp.replaceAll("_", " ");

                if (name.contains("journal") == false) {
                    spinnerText.add(name);
                }
            }
        }

        spinner = (Spinner) findViewById(R.id.city_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerText);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        corpSpinner = (Spinner) findViewById(R.id.corp_spinner);
        ArrayAdapter<String> corpAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, corpSpinText);

        corpAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        corpSpinner.setAdapter(corpAdapter);
        corpSpinner.setOnItemSelectedListener(corpListen);
        if (LocationUtil.checkLocationPermission() && LocationUtil.isGPSOn()) {
            rmApp.getLocationUtil().startLocationUpdates();
            setupMapIfRequired();
        } else if (!LocationUtil.checkLocationPermission()) {
            rmApp.getLocationUtil().askLocationPermission(this);
        } else {
            rmApp.getLocationUtil().checkLocationSettings(this);
            setupMapIfRequired();
        }
        CommonUtils.toast("Getting your location. Please wait...");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LocationUtil.REQ_PERMISSIONS_REQUEST_ACCESS_FILE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    rmApp.getLocationUtil().checkLocationSettings(this);
                    setupMapIfRequired();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LocationUtil.REQUEST_CHECK_SETTINGS:
                rmApp.getLocationUtil().dialogClosed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    public void setupMapIfRequired() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
            mMap.setMyLocationEnabled(true);
        }
    }

    @Subscribe
    public void onLocationChangedEvent(LocationChangedEvent event) {
        this.location = event.getLocation();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location,
                        Constants.ZOOM));
                latituteTxt.setText(location.latitude + "");
                longituteTxt.setText(location.longitude + "");
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, RouteManager.class);
        startActivity(i);
        finish();
    }

    public OnItemSelectedListener corpListen = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (corpSpinText.size() == 1) {
                corp.setVisibility(View.VISIBLE);
                corpLabel.setVisibility(View.VISIBLE);
            } else {
                switch (i) {
                    case 0:
                        Corp = "";
                        corp.setVisibility(View.GONE);
                        corpLabel.setVisibility(View.GONE);
                        break;
                    case 1:
                        Corp = "";
                        corp.setVisibility(View.VISIBLE);
                        corpLabel.setVisibility(View.VISIBLE);
                        break;
                    default:
                        Corp = corpSpinText.get(i);
                        corp.setVisibility(View.GONE);
                        corpLabel.setVisibility(View.GONE);
                        break;
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            corp.setVisibility(View.GONE);
            corpLabel.setVisibility(View.GONE);
            Corp = "";
        }
    };

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        corpSpinText.clear();
        corpSpinText.trimToSize();

        switch (position) {
            case 0:
                City = "";
                corpSpinText.add("Please select city first");
                city.setVisibility(View.GONE);
                cityLabel.setVisibility(View.GONE);
                break;
            case 1:
                City = "";
                city.setVisibility(View.VISIBLE);
                cityLabel.setVisibility(View.VISIBLE);
                break;
            default:
                City = spinnerText.get(position);
                corpSpinText.add(getString(R.string.selectCorp));
                city.setVisibility(View.GONE);
                cityLabel.setVisibility(View.GONE);
                break;
        }

        corpSpinText.add(getString(R.string.newCorp));

        /*if (position > 1) {
            File dir = new File(DbHelper.DATABASE_PATH);
            dir.mkdirs();
            File files[] = dir.listFiles();
            int l;

            if (files != null) l = files.length;
            else l = 0;

            for (int i = 0; i < l; i++) {
                String name = files[i].getName();

                String temp = name.substring(0, name.indexOf("_Corp_"));

                temp.replaceAll("_", " ");

                if (temp.equalsIgnoreCase(City) && name.contains("journal") == false) {
                    corpSpinText.add(name.substring(name.indexOf("_Corp_") + 6).replaceAll("_", " "));
                }
            }
        }*/

        ArrayAdapter<String> corpAdapter = new ArrayAdapter<String>(RouteCreatorHome.this, android.R.layout.simple_spinner_item, corpSpinText);
        corpAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        corpSpinner.setAdapter(corpAdapter);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        city.setVisibility(View.GONE);
        cityLabel.setVisibility(View.GONE);
        City = "";

        corpSpinText.clear();
        corpSpinText.trimToSize();
        corpSpinText.add("Please select city first!");
        corpSpinText.add(getString(R.string.newCorp));

        ArrayAdapter<String> corpAdapter = new ArrayAdapter<String>(RouteCreatorHome.this, android.R.layout.simple_spinner_item, corpSpinText);
        corpAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        corpSpinner.setAdapter(corpAdapter);
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ROUTE, route.getText().toString());
        outState.putString("CITY", city.getText().toString());
        //outState.putString("SRC", src.getText().toString());
        //outState.putString("DEST", dest.getText().toString());
        outState.putStringArrayList(SPINTEXT, spinnerText);
    }

    protected void openSettings() {
    }

    private void reset() {
        route.setText("");
        city.setText("");
        corp.setText("");
        //src.setText("");
        //dest.setText("");
        spinner.setSelection(0);
    }

    public void getLocation(View view) {
        if (LocationUtil.checkLocationPermission() && LocationUtil.isGPSOn()) {
            rmApp.getLocationUtil().startLocationUpdates();
            setupMapIfRequired();
        } else if (!LocationUtil.checkLocationPermission()) {
            rmApp.getLocationUtil().askLocationPermission(this);
        } else {
            rmApp.getLocationUtil().checkLocationSettings(this);
            setupMapIfRequired();
        }
        CommonUtils.toast("Getting your location. Please wait...");
    }

    public void reset(View view) {
        reset();
    }

    public void create(View view) {
        //Route = "Route_" + route.getText().toString().trim() + "_From_" + src.getText().toString().trim() + "_Towards_" + dest.getText().toString().trim();
        Route = route.getText().toString().trim();
        if (valid()) {
            //City = City + "_Corp_" + Corp;
            //RVnC();
            findViewById(R.id.route_info_layout).setVisibility(View.GONE);
            findViewById(R.id.stop_handler_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.create_route).setVisibility(View.GONE);
            findViewById(R.id.reset_form).setVisibility(View.GONE);
        }
    }

    private boolean valid() {
        Route = Route.trim();
        City = City.trim();
        Corp = Corp.trim();

        if (spinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, getString(R.string.cityEmpty), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (spinner.getSelectedItemPosition() == 1) {
            City = city.getText().toString().trim();
            if (City.length() == 0) {
                Toast.makeText(this, getString(R.string.addCityEmpty), Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (corpSpinText.get(corpSpinner.getSelectedItemPosition()).equalsIgnoreCase(getString(R.string.selectCorp)) ||
                corpSpinText.get(corpSpinner.getSelectedItemPosition()).contains("please select")) {
            Toast.makeText(this, getString(R.string.corpEmpty), Toast.LENGTH_SHORT).show();
        }

        if (corpSpinText.get(corpSpinner.getSelectedItemPosition()).equalsIgnoreCase(getString(R.string.newCorp))) {
            Corp = corp.getText().toString().trim();
            if (Corp.length() == 0) {
                Toast.makeText(this, getString(R.string.addCorpEmpty), Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (route.getText().toString().trim().length() == 0) {
            Toast.makeText(this, getString(R.string.routeEmpty), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private AlertDialog stopInfoDialog;
    private int nextPos = 1;

    public void addStop(View view) {
        if (stopInfoDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(R.layout.stop_info_dialog_layout);
            builder.setTitle("Enter Stop Info");
            stopInfoDialog = builder.create();
            stopInfoDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Add Stop", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText name = (EditText) stopInfoDialog.findViewById(R.id.stop_name);
                    EditText pos = (EditText) stopInfoDialog.findViewById(R.id.stop_number);
                    stops.add(new Stop(name.getText().toString(), (pos.getText().toString()),
                            location.latitude + "", location.longitude + ""));
                    stopInfoDialog.dismiss();
                }
            });
        }
        stopInfoDialog.show();
        ((EditText) stopInfoDialog.findViewById(R.id.stop_name)).setText("");
        ((EditText) stopInfoDialog.findViewById(R.id.stop_number)).setText(nextPos + "");
    }

    public void saveData(View view) {
        DbHelper helper = new DbHelper(this, City, "route_" + Route + "_" + Corp);
        helper.setTable(stops.toArray(new Stop[]{}));
        finish();
    }

    public void upload(View view) {

    }

    private void routeAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Route already exists!");

        // Setting Dialog Message
        alertDialog.setMessage("The route you are trying to create, already exists.\n Do you wish to edit this route, override existing route or create another route?");

        alertDialog.setPositiveButton("Override", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                DbHelper db = new DbHelper(rmApp.getAppContext(), City, Route);
                db.delTable();
                db.closeDB();

                Intent i = new Intent(RouteCreatorHome.this, RouteCreator.class);
                i.putExtra(getString(R.string.routeKey), Route);
                i.putExtra(getString(R.string.cityKey), City);
                i.putExtra(getString(R.string.parentKey), "create");
                startActivity(i);
                finish();
            }
        });

        alertDialog.setNegativeButton("Create Another", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
}