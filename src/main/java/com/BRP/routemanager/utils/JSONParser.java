package com.BRP.routemanager.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.BRP.routemanager.R;
import com.BRP.routemanager.UploadActivity;
import com.BRP.routemanager.app.rmApp;
import com.BRP.routemanager.utils.DbHelper;

public class JSONParser extends AsyncTask<String, Void, String> {

    private ProgressDialog pD;
    private Context context;
    private String link;
    private int flag;

    //flag 0 means get and 1 means post.(By default it is get.) Removed this for now. Trying only with GET method
    public JSONParser(String url, Context context, int flag) {
        this.context = context;

        this.link = url;

        this.flag = flag;
    }

    protected void onPreExecute() {
        pD = ProgressDialog.show(context, context.getString(R.string.Loading), context.getString(R.string.wait), true);
    }

    @Override
    protected String doInBackground(String... arg0) {
    /*if (flag == 0) {
             try{

                URL url = new URL(link);
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));
                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuffer sb = new StringBuffer("");
		String line = "";

		while ( (line = in.readLine()) != null ) {
			if ( line .trim().length() > 0 ) {
				sb.append(line);
				sb.append("\n");
			}
		}

		sb.setLength(sb.length() - 1);

                in.close();

                return sb.toString();
            }

            catch(Exception e){
                return new String("Exception: " + e.getMessage());
	    }
            
        }

	else if (flag == 1) {
	    try {
		List<NameValuePair> nvp = new ArrayList<NameValuePair>();

		nvp.add(new BasicNameValuePair("city",arg0[0]));
		nvp.add(new BasicNameValuePair("route",arg0[1]));
        nvp.add(new BasicNameValuePair("corp",arg0[2]));

		for (String stop : UploadActivity.stopList) {
			nvp.add(new BasicNameValuePair("stopList[]", stop));
		}
		for (String lat : UploadActivity.latList) {
			nvp.add(new BasicNameValuePair("latList[]", lat));
		}
		for (String lon : UploadActivity.lonList) {
			nvp.add(new BasicNameValuePair("lonList[]", lon));
		}
        for (String time: UploadActivity.timeList) {
            nvp.add(new BasicNameValuePair("timeList[]", time));
        }

		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(link);
		httpPost.setEntity(new UrlEncodedFormEntity(nvp));
		
		HttpResponse response = client.execute(httpPost);

                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                StringBuffer sb = new StringBuffer("");
		String line = "";

		while ( (line = in.readLine()) != null ) {
			if ( line .trim().length() > 0 ) {
				sb.append(line);
				sb.append("\n");
			}
		}

		sb.setLength(sb.length() - 1);

                in.close();

                return sb.toString();

	    }
	    catch(Exception e) {
		return new String("Exception: " + e.getMessage());
	    }
	}*/
        return "FAILED!";
    }

    @Override
    protected void onPostExecute(String result) {
        pD.dismiss();

        if (result.contains("Exception: ")) {
            Toast.makeText(context, "Connection Failed!", Toast.LENGTH_SHORT).show();
        } else if (result.contains("<") && result.contains(">")) {
            Toast.makeText(context, "PHP Error!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, result, Toast.LENGTH_LONG).show();
            Log.d("FRODO", result);

            if (flag == 1) {
                DbHelper db = new DbHelper(rmApp.getAppContext(), UploadActivity.City, UploadActivity.Route);
                db.delTable();
                db.closeDB();
                UploadActivity.city.setSelection(0);
            }
        }
    }
}