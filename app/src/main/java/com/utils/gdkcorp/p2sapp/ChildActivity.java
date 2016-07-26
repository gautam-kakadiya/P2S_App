package com.utils.gdkcorp.p2sapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ChildActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);
        ChatFragment chatFragment = ChatFragment.newInstance("","");
        getSupportFragmentManager().beginTransaction().add(R.id.container_view,chatFragment).commit();
    }
}
