package com.BRP.routemanager;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.BRP.routemanager.activites.RouteManager;
import com.BRP.routemanager.app.rmApp;
import com.BRP.routemanager.utils.DbHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class UploadActivity extends Activity {

    public static Spinner city, route;
    public static ArrayList<String> cityList, routeList;
    public static ArrayList<String> stopList, latList, lonList;
    public static ArrayList<String> timeList;
    public static String City = "", Route = "", Corp = "DTC";

    public String ip_proj = "http://" + RouteManager.ip + "/Nav/public/index.php/";

    private String cityDef = "", routeDef = "";
    private Boolean def = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        city = (Spinner) findViewById(R.id.citySpinner);
        route = (Spinner) findViewById(R.id.routeSpinner);

        Intent intent = getIntent();
        cityDef = intent.getStringExtra(getString(R.string.cityKey));
        routeDef = intent.getStringExtra(getString(R.string.routeKey));

        if (savedInstanceState != null) {
            cityList = savedInstanceState.getStringArrayList("cityList");
            routeList = savedInstanceState.getStringArrayList("routeList");
            stopList = savedInstanceState.getStringArrayList("stopList");
            latList = savedInstanceState.getStringArrayList("latList");
            lonList = savedInstanceState.getStringArrayList("lonList");
            timeList = savedInstanceState.getStringArrayList("timeList");
        } else {
            stopList = new ArrayList<String>();
            latList = new ArrayList<String>();
            lonList = new ArrayList<String>();
            timeList = new ArrayList<String>();

            routeList = new ArrayList<String>();
            routeList.add("Please select city first!");
            cityList = new ArrayList<String>();
            cityList.add(getString(R.string.selectCity));

            File dir = new File(DbHelper.DATABASE_PATH);
            File files[] = dir.listFiles();
            int l;

            if (files != null)
                l = files.length;
            else
                l = 0;

            for (int i = 0; i < l; i++) {
                String name = files[i].getName();

                String temp = name.substring(0, name.indexOf("_Corp_"));
                name = temp + " (" + name.substring(name.indexOf("_Corp_") + 6) + ")";
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

        // if citydef in list: set city., update route list...then set route.
        if (cityList.contains(cityDef)) {
            city.setSelection(cityList.indexOf(cityDef));
            def = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upload, menu);
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("cityList", cityList);
        outState.putStringArrayList("routeList", routeList);
        outState.putStringArrayList("stopList", stopList);
        outState.putStringArrayList("latList", latList);
        outState.putStringArrayList("lonList", lonList);
        outState.putStringArrayList("timeList", timeList);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, RouteManager.class);
        startActivity(i);
        finish();
    }

    private AdapterView.OnItemSelectedListener cityListListener = new AdapterView.OnItemSelectedListener() {

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
                City = City.substring(0, City.lastIndexOf("_(")) + "_Corp_" + City.substring(City.lastIndexOf("(") + 1, City.length() - 1);

                DbHelper db = new DbHelper(rmApp.getAppContext(), City);
                Cursor c = db.getTables();

                if (c != null && c.getCount() > 0) {
                    c.moveToFirst();
                    while (!c.isAfterLast()) {
                        String temp = c.getString(0);

                        if (temp.contains("Route")) {
                            routeList.add(temp.substring(6).replaceAll("_", " "));
                        }
                        c.moveToNext();
                    }
                }
                db.closeDB();
            }

            Collections.sort(routeList.subList(1, routeList.size()));

            ArrayAdapter<String> routeAdapter = new ArrayAdapter<String>(UploadActivity.this, android.R.layout.simple_spinner_item, routeList);
            routeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            route.setAdapter(routeAdapter);

            if (City.equals(cityDef) && def) {
                String temp = routeDef.substring(6).replaceAll("_", " ");

                if (routeList.contains(temp)) {
                    route.setSelection(routeList.indexOf(temp));
                }

                def = false;
            }
        }

        public void onNothingSelected(AdapterView<?> parent) {
            City = "";

            routeList.clear();
            routeList.trimToSize();
            routeList.add("Please select city first!");

            ArrayAdapter<String> routeAdapter = new ArrayAdapter<String>(UploadActivity.this, android.R.layout.simple_spinner_item, routeList);
            routeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            route.setAdapter(routeAdapter);
        }
    };

    private AdapterView.OnItemSelectedListener routeListListener = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            if (pos == 0) {
                Route = "";
            } else {
                Route = "Route_" + routeList.get(pos).trim().replaceAll(" ", "_");
            }
        }

        public void onNothingSelected(AdapterView<?> parent) {
            Route = "";
        }
    };


    public void Upload(View view) {
        Intent intent = new Intent(this, ListFiles.class);
        intent.putExtra(getString(R.string.routeKey), Route);
        intent.putExtra(getString(R.string.cityKey), City);
        intent.putExtra(getString(R.string.parentKey), "create");
        startActivity(intent);
        finish();
        /*
        if (City.trim().length() == 0 ) {
            Toast.makeText(this, getString(R.string.cityNotSel), Toast.LENGTH_SHORT).show();
        }
        else if (Route.trim().length() == 0) {
            Toast.makeText(this,getString(R.string.routeNotSel),Toast.LENGTH_SHORT).show();
        }
        else{
                DbHelper db = new DbHelper(rmApp.getAppContext(), City, Route);
                Cursor c = db.showTable();

        stopList.clear();
	   	latList.clear();
		lonList.clear();
        timeList.clear();

		stopList.trimToSize();
		latList.trimToSize();
		lonList.trimToSize();
        timeList.trimToSize();

                if ( c != null && c.getCount() > 0 ) {
                    c.moveToFirst();
                    while ( !c.isAfterLast() ) {
                        stopList.add(c.getString(1));
                        latList.add(c.getString(2));
                        lonList.add(c.getString(3));
                        timeList.add(c.getString(4));
                        c.moveToNext();
                    }
                }

                db.closeDB();

		//new JSONParser(ip_proj+"create_route.php?city="+City+"&route="+Route,this, 0).execute();
		//new JSONParser(ip_proj+"add_route.php",this, 1).execute(City,Route);
        City = City.substring(0,City.indexOf("_Corp_"));
        Corp = City.substring(City.indexOf("_Corp_")+6);
        new JSONParser(ip_proj + "get_data",this,1).execute(City,Route,Corp);
        }	*/
    }

}
