package com.utils.gdkcorp.p2sapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class ChildActivity extends AppCompatActivity {

    ChatFragment chatFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        chatFragment = ChatFragment.newInstance("", "");
        getSupportFragmentManager().beginTransaction().add(R.id.container_view, chatFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_child, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.end_chat:
                DetectedProduct prod = chatFragment.current_product;
                Intent intent = new Intent(this, ResultActivity.class);
                intent.putExtra("longdesc", prod.getSub_catagory());
                intent.putExtra("category", prod.getCategory());
                intent.putExtra("sub_category", prod.getSub_catagory());
                intent.putExtra("brand",prod.getBrand());
                intent.putExtra("paymentTerm",prod.getPayment_term());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
