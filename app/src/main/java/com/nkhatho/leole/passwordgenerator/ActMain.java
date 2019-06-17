package com.nkhatho.leole.passwordgenerator;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ActMain extends AppCompatActivity {

    String word;
    float number;
    TextView tvEncryptedPassword;
    boolean isWordsRetrieved = false;
    String displayText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvEncryptedPassword = (TextView) findViewById(R.id.tvEncryptedPassword);
        new ProcessStream().execute();

    }

    public InputStream getInputStream(URL url){

        try{
            return  url.openConnection().getInputStream();
        }
        catch (IOException e){
            return null;
        }
    }

    public class ProcessStream extends AsyncTask<Boolean, Integer, Boolean>{

        ProgressDialog progressDialog;
        InternetConnection internet;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActMain.this);
            progressDialog.setMessage("Loading Word and Number for the day\n\n... please wait...");
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Boolean... params) {

            internet = new InternetConnection();
            if(internet.isConnected(ActMain.this)){
                try{
                    URL url = new URL("https://dl.dropbox.com/s/8hl7sxgwu7fh9c0/data.xml?dl=0");
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(false);

                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput(getInputStream(url), "UTF_8");

                    boolean insideItemTag = false;
                    int eventType = xpp.getEventType();

                    while(eventType != XmlPullParser.END_DOCUMENT){
                        if(eventType == XmlPullParser.START_TAG && xpp.getName().equalsIgnoreCase("item")){
                            insideItemTag = true;
                        }
                        else if(eventType == XmlPullParser.START_TAG && xpp.getName().equalsIgnoreCase("word")){
                            if(insideItemTag){
                                word = xpp.nextText();
                                isWordsRetrieved = true;
                            }
                        }
                        else if (eventType == XmlPullParser.START_TAG && xpp.getName().equalsIgnoreCase("number")){
                            if(insideItemTag){
                                number = Float.parseFloat(xpp.nextText());
                                isWordsRetrieved = true;
                            }
                        }
                        else if(eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")){
                            insideItemTag = false;
                        }
                        eventType = xpp.next();
                    }

                }
                catch (MalformedURLException e){
                    displayText = e.getMessage();
                    isWordsRetrieved = false;
                }
                catch (XmlPullParserException e){
                    displayText = e.getMessage();
                    isWordsRetrieved = false;
                }
                catch (IOException e){
                    displayText = e.getMessage();
                    isWordsRetrieved = false;
                }
            }
            else{
                //Toast.makeText(ActMain.this, "Error checking internet connection", Toast.LENGTH_SHORT).show();
                isWordsRetrieved = false;
            }


            return isWordsRetrieved;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);

            progressDialog.dismiss();

            if(s){

                progressDialog = new ProgressDialog(ActMain.this);
                progressDialog.setMessage("Encrypting password\n\n... please wait...");
                progressDialog.show();
                EncryptPassword encryptPassword = EncryptPassword.getInstance();
                encryptPassword.getEcryptedPasswordAsync(word, number, new AsyncCallback<Object>() {
                    @Override
                    public void handleResponse(Object response) {
                        progressDialog.dismiss();
                        displayText = (String) response;
                        tvEncryptedPassword.setText(displayText);
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        progressDialog.dismiss();
                        displayText = fault.getMessage();
                        tvEncryptedPassword.setText(displayText);
                    }
                });
            }
            else{
                tvEncryptedPassword.setText("NETWORK ERROR:\n\n...Please check your internet connections...");
            }

        }

    }
}
