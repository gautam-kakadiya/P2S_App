package com.utils.gdkcorp.p2sapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

public class ApacheNLPActivity extends AppCompatActivity {

    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apache_nlp);
        tv = (TextView) findViewById(R.id.tv);
        String sentences[] = new String[]{"I","want","aluminium","sheet","and","lead","stearate","."};
        new MyAsyncTask().execute(sentences);

    }

    public class MyAsyncTask extends AsyncTask<String[],Span[],Void>{

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(String[]... strings) {
            InputStream modelIn;
            try {
                modelIn = getAssets().open("en-ner-product.bin");
                TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
                NameFinderME namefinder = new NameFinderME(model);
                Log.d("string[0]_length", "doInBackground: "+strings[0].length);
                Span span[] = namefinder.find(strings[0]);
                publishProgress(span);
            } catch (InvalidFormatException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Span[]... values) {

            Span span[] = values[0];
            for(int i =0 ;i<span.length;++i) {
                Log.d("Span_values", "onProgressUpdate: " + span[i].toString() + span[i].getType());
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }
}
