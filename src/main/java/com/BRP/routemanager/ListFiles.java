package com.BRP.routemanager;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.BRP.routemanager.activites.EditSavedHome;
import com.BRP.routemanager.activites.RouteCreatorHome;
import com.BRP.routemanager.activites.RouteManager;
import com.BRP.routemanager.app.rmApp;
import com.BRP.routemanager.utils.DbHelper;

import java.util.ArrayList;

public class ListFiles extends Activity {
    private DbHelper dbHelper;
    private String routeName, cityName, parent;
    private ArrayList<String> stopList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filelister);

        Intent i = getIntent();

        routeName = i.getStringExtra(getString(R.string.routeKey));
        cityName = i.getStringExtra(getString(R.string.cityKey));
        parent = i.getStringExtra(getString(R.string.parentKey));

        Button btn = (Button) findViewById(R.id.buttonAnother);

        if (parent.equals("create"))
            btn.setText(getString(R.string.button_createAnother));
        else if (parent.equals("edit") || parent.equals("editSave"))
            btn.setText(getString(R.string.button_editAnother));

        dbHelper = new DbHelper(rmApp.getAppContext(), cityName, routeName);

        TextView directory = (TextView) findViewById(R.id.dir);
        String dir = dbHelper.getDbPath() + "/" + cityName + "/" + routeName;
        directory.setText(dir);

        ListView showFile = (ListView) findViewById(R.id.showFiles);

        stopList = new ArrayList<String>();

        Cursor cursor = dbHelper.showTable();

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
//		content = content + cursor.getString(0) + "\n";
                String content = cursor.getString(1) + ": ";
                content = content + cursor.getString(2) + ", ";
                content = content + cursor.getString(3);
                stopList.add(content);
                cursor.moveToNext();
            }
        } else {
            stopList.add("No data! ");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, stopList);
        showFile.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbHelper.closeDB();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, RouteManager.class);
        startActivity(i);
        finish();
    }

    public void moreRoutes(View view) {
        dbHelper.closeDB();
        Intent intent;
        if (parent.equals("create"))
            intent = new Intent(this, RouteCreatorHome.class);
        else if (parent.equals("editSave"))
            intent = new Intent(this, EditSavedHome.class);
        else
            intent = new Intent(this, RouteManager.class);
        startActivity(intent);
        finish();
    }

    public void home(View view) {
        dbHelper.closeDB();
        Intent intent = new Intent(this, RouteManager.class);
        startActivity(intent);
        finish();
    }
}