package com.BRP.routemanager.activites;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.BRP.routemanager.EditNames;
import com.BRP.routemanager.R;
import com.BRP.routemanager.app.rmApp;
import com.BRP.routemanager.utils.DbHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class EditSavedHome extends Activity {
    private Spinner city, route;
    private ArrayList<String> cityList, routeList;
    private String City = "", Route = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editsavedhome);

        city = (Spinner) findViewById(R.id.savedCitySpinner);
        route = (Spinner) findViewById(R.id.savedRouteSpinner);

        if (savedInstanceState != null) {
            cityList = savedInstanceState.getStringArrayList("cityList");
            routeList = savedInstanceState.getStringArrayList("routeList");
        } else {
            routeList = new ArrayList<String>();
            routeList.add("Please select city first!");
            cityList = new ArrayList<String>();
            cityList.add(getString(R.string.selectCity));

            File dir = new File(DbHelper.DATABASE_PATH);
            File files[] = dir.listFiles();
            int l;
            if (files != null) l = files.length;
            else l = 0;

            for (int i = 0; i < l; i++) {
                String name = files[i].getName();

                //String temp = name.substring(0, name.indexOf("_Corp_"));
                //name = temp + " (" + name.substring(name.indexOf("_Corp_") + 6) + ")";
                name.replaceAll("_", " ");
                if (name.contains("journal") == false) {
                    cityList.add(name);
                }
            }
        }

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cityList);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city.setAdapter(cityAdapter);
        city.setOnItemSelectedListener(cityListListener);

        ArrayAdapter<String> routeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, routeList);
        routeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        route.setAdapter(routeAdapter);
        route.setOnItemSelectedListener(routeListListener);
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("cityList", cityList);
        outState.putStringArrayList("routeList", routeList);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, RouteManager.class);
        startActivity(i);
        finish();
    }

    private OnItemSelectedListener cityListListener = new OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            routeList.clear();
            routeList.trimToSize();
            if (pos == 0) {
                City = "";
                routeList.add("Please select city first!");
            } else {
                routeList.add("Please select route!");

                City = cityList.get(pos);
                City = City.replaceAll(" ", "_");
//                City = City.substring(0, City.lastIndexOf("_(")) + "_Corp_" + City.substring(City.lastIndexOf("(") + 1, City.length() - 1);

                DbHelper db = new DbHelper(rmApp.getAppContext(), City);
                Cursor c = db.getTables();

                if (c != null && c.getCount() > 0) {
                    c.moveToFirst();
                    while (!c.isAfterLast()) {
                        String temp = c.getString(0);

                        if (temp.contains("route_")) {
                            routeList.add(temp.substring(6).replaceAll("_", " "));
                        }
                        c.moveToNext();
                    }
                }
                db.closeDB();
            }

            Collections.sort(routeList.subList(1, routeList.size()));

            ArrayAdapter<String> routeAdapter = new ArrayAdapter<String>(EditSavedHome.this, android.R.layout.simple_spinner_item, routeList);
            routeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            route.setAdapter(routeAdapter);
        }

        public void onNothingSelected(AdapterView<?> parent) {
            City = "";

            routeList.clear();
            routeList.trimToSize();
            routeList.add("Please select city first!");

            ArrayAdapter<String> routeAdapter = new ArrayAdapter<String>(EditSavedHome.this, android.R.layout.simple_spinner_item, routeList);
            routeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            route.setAdapter(routeAdapter);
        }
    };

    private OnItemSelectedListener routeListListener = new OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            if (pos == 0) {
                Route = "";
            } else {
                Route = "route_" + routeList.get(pos).trim().replaceAll(" ", "_");
            }
        }

        public void onNothingSelected(AdapterView<?> parent) {
            Route = "";
        }
    };

    public void edit(View view) {
        if (City.trim().length() == 0) {
            Toast.makeText(this, getString(R.string.cityNotSel), Toast.LENGTH_SHORT).show();
        } else if (Route.trim().length() == 0) {
            Toast.makeText(this, getString(R.string.routeNotSel), Toast.LENGTH_SHORT).show();
        } else {
            Intent i = new Intent(this, EditNames.class);
            i.putExtra(getString(R.string.cityKey), City);
            i.putExtra(getString(R.string.routeKey), "route_" + Route);
            i.putExtra(getString(R.string.parentKey), "saved");
            startActivity(i);
            finish();
        }
    }
}