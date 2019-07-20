package manzooralam.funchswitch.com.fetchdatafromjson;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TabHost;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TabActivity extends ListActivity {
    // Connection detector
    ConnectionDetector cd;

    // Alert dialog manager
    AlertDialogManager alert = new AlertDialogManager();

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jsonParser = new JSONParser();

    ArrayList<HashMap<String, String>> albumsList;
    ArrayList<HashMap<String, String>> completed_albumsList;

    // albums JSONArray
    JSONArray albums = null;

    // albums JSON url
    private static final String URL_ALBUMS = "http://www.mocky.io/v2/582695f5100000560464ca40";

    // ALL JSON node names
    private static final String DESCRIPOTION = "description";
    private static final String SCHEDULEDATE = "scheduledDate";
    private static final String STATUS = "status";

  TabHost host;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        host =(TabHost)findViewById(R.id.tabHost);
        host.setup();
        //Tab 1
        TabHost.TabSpec spec= host.newTabSpec("Tab One");
        spec.setContent(R.id.tab1);
        spec.setIndicator("TODAY");
        host.addTab(spec);
        // start disply data from Aysc task
        cd = new ConnectionDetector(getApplicationContext());

        // Check for internet connection
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            alert.showAlertDialog(TabActivity.this, "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            // stop executing code by return
            return;
        }

        // Hashmap for ListView
        albumsList = new ArrayList<HashMap<String, String>>();
        completed_albumsList = new ArrayList<HashMap<String, String>>();

        // Loading Albums JSON in Background Thread
        new LoadAlbums().execute();


        // end tab 1

        //Tab 2
        spec = host.newTabSpec("Tab Two");
        spec.setContent(R.id.tab2);
        spec.setIndicator("LATER");
        host.addTab(spec);
    }
    class LoadAlbums extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TabActivity.this);
            pDialog.setMessage("Listing Albums ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting Albums JSON
         */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            // getting JSON string from URL
            JSONArray json = jsonParser.makeHttpRequest(URL_ALBUMS, "GET",params);
            // Check your log cat for JSON reponse
            Log.i("Albums JSON: ",json.toString());

            try {
                //albums = new JSONArray(json);
                // albums =json.getJSONObject();
                if (json != null) {
                    // looping through All albums
                    for (int i = 0; i < json.length(); i++) {
                        JSONObject c = json.getJSONObject(i);

                        // Storing each json item values in variable
                        String desc = c.getString(DESCRIPOTION);
                        String schD = c.getString(SCHEDULEDATE);
                        String sts = c.getString(STATUS);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(DESCRIPOTION, desc);
                        map.put(SCHEDULEDATE, schD);
                        map.put(STATUS, sts);

                        /*// adding HashList to ArrayList
                        albumsList.add(map);*/
                        if (sts.equals("COMPLETED")){
                            completed_albumsList.add(map);
                        }
                        else{
                            albumsList.add(map);
                        }
                    }
                } else {
                    Log.d("Albums: ", "null");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all albums
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            TabActivity.this, albumsList,
                            R.layout.item_row_disply, new String[]{DESCRIPOTION,
                            SCHEDULEDATE, STATUS}, new int[]{
                            R.id.album_id, R.id.album_name, R.id.songs_count});

                    // updating listview
                    setListAdapter(adapter);
                }
            });
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ListAdapter pendAdapter= new SimpleAdapter(TabActivity.this,completed_albumsList,
                            R.layout.item_row_disply, new String[]{DESCRIPOTION, SCHEDULEDATE,
                            STATUS}, new int[]{R.id.album_id, R.id.album_name, R.id.songs_count});
                    //setListAdapter(pendAdapter);
                }
            });
        }
    }
}
