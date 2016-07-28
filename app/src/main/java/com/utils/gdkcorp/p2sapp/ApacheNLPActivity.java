package com.utils.gdkcorp.p2sapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

public class ApacheNLPActivity extends AppCompatActivity {

    TextView tv;
    EditText edt1;
    Button parsebtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apache_nlp);
        tv = (TextView) findViewById(R.id.tv);
        edt1 = (EditText)findViewById(R.id.edt_test);
        parsebtn = (Button) findViewById(R.id.parse_button);
        //String sentences[] = new String[]{"I","want","aluminium","sheet","and","lead","stearate","."};
        //new NameFinderTask().execute(sentences);
        parsebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sentence = edt1.getText().toString();
                new ParseTask().execute(sentence);
            }
        });

    }

    public class NameFinderTask extends AsyncTask<String[],Span[],Void>{

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

    public class ParseTask extends AsyncTask<String,Parse[],Void>{

        InputStream modelIn;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(String... strings) {

            try {
                modelIn = getAssets().open("en-parser-chunking.bin");
                ParserModel model = new ParserModel(modelIn);
                Parser parser = ParserFactory.create(model);
                Parse topParses[] = ParserTool.parseLine(strings[0], parser, 1);
                publishProgress(topParses);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Parse[]... values) {
            Parse parses[] = values[0];
            tv.setText(parses[0].toString());
        }
    }
}
