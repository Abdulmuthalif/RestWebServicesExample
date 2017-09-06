package com.jobs.khair.restwebservicesexample;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button GetServerData = (Button) findViewById(R.id.getServerData);

        GetServerData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serverURL = "http://androidexample.com/media/webservice/JsonReturn.php";

                new LongOperation().execute(serverURL);

            }
        });

    }

    private class LongOperation extends AsyncTask<String, Void, Void> {
        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(MainActivity.this);
        String data = "";

        TextView uiUpdate = (TextView) findViewById(R.id.output);
        TextView jsonParsed = (TextView) findViewById(R.id.jsonParsed);
        int sizeData = 0;
        EditText serverText = (EditText)findViewById(R.id.serverText);

        protected void onPreExecute() {
            Dialog.setMessage("Please wait...");
            Dialog.show();

            try {
                data +="&" + URLEncoder.encode("data", "UTP-8") + "="+serverText.getText();
            } catch(UnsupportedEncodingException e){
                e.printStackTrace();
            }
        }


        @Override
        protected Void doInBackground(String... urls) {

            BufferedReader reader = null;
            try
            {
                URL url = new URL(urls[0]);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = reader.readLine()) != null)
                {
                    sb.append(line + "");
                    Content = sb.toString();
                }
            }
            catch(Exception ex) {
                Error = ex.getMessage();
            }
            finally {
                try {
                    reader.close();
                }
                catch (Exception ex){};
            }
            return null;
        }

        protected void onPostExecute(Void unused){
            Dialog.dismiss();

            if(Error != null){
                uiUpdate.setText("Output : "+Error);
            } else {
                uiUpdate.setText(Content);
                String OutputData = "";
                JSONObject jsonResponse;
                try {
                    jsonResponse = new JSONObject(Content);
                    JSONArray jsonMainNode = jsonResponse.optJSONArray("Android");

                    int lengthJsonArr = jsonResponse.length();

                    for(int i = 0; i < lengthJsonArr; i++){
                        JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                        String name = jsonChildNode.optString("name").toString();
                        String number = jsonChildNode.optString("number").toString();
                        String date_added = jsonChildNode.optString("date_added").toString();

                        OutputData += " Name : "+ name +" " + "Number         : "+ number +" " + "Time           : "+ date_added +" " + "------------------------------- ";
                        jsonParsed.setText(OutputData);
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
