package manzooralam.funchswitch.com.fetchdatafromjson;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FullscreenActivity extends AppCompatActivity {

    public class DownloadTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            String result= "";
            URL url;
            HttpURLConnection urlConnection=null;
            try{
                url= new URL(urls[0]);
                urlConnection = (HttpURLConnection)url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data= reader.read();

                while (data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("JSON", s);//work fine

            try {

               /* JSONObject jsonObject = new JSONObject(s);
                //JSONObject description= jsonObject.getJSONObject("description");
                String description1 = jsonObject.getString("description");
                Log.i("Weather content", description1);

                JSONArray arr = new JSONArray(description1);
                for(int i= 0; i<arr.length() ; i++){
                    JSONObject jsonPart= arr.getJSONObject(i);

                    Log.i("main",jsonPart.getString("description"));
                    Log.i("description", jsonPart.getString("description"));
                }*/

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        DownloadTask task = new DownloadTask();
        task.execute("http://www.mocky.io/v2/582695f5100000560464ca40");

    }
}
