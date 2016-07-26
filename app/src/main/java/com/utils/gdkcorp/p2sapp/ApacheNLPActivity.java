package com.utils.gdkcorp.p2sapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.InvalidFormatException;

public class ApacheNLPActivity extends AppCompatActivity {

    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apache_nlp);
        tv = (TextView) findViewById(R.id.tv);
        InputStream modelIn;
        try {
            modelIn = new FileInputStream("en-sent.bin");
            SentenceModel model = new SentenceModel(modelIn);
            SentenceDetectorME sentenceDetectorME = new SentenceDetectorME(model);
            String sentences[] = sentenceDetectorME.sentDetect("Hello Akshay. How are you?");
            tv.setText(sentences[0] + "|" + sentences[1]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("Filenotfound", "onCreate: ");
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
