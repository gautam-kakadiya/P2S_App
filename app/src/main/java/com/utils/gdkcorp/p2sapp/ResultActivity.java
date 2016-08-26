package com.utils.gdkcorp.p2sapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {

    private String long_desc;
    private String category;
    private String sub_category;
    private String brand;
    private String paymentTerm;
    private TextView records_tv,categorytv,subCategorytv,brandtv,paymentTermtv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        records_tv = (TextView) findViewById(R.id.recordstv);
        categorytv = (TextView) findViewById(R.id.category);
        subCategorytv = (TextView) findViewById(R.id.subCategory);
        brandtv = (TextView) findViewById(R.id.brand);
        paymentTermtv = (TextView) findViewById(R.id.paymentTerm);


        Intent intent = getIntent();
        long_desc = intent.getStringExtra("longdesc");
        category = intent.getStringExtra("category");
        sub_category = intent.getStringExtra("sub_category");
        brand = intent.getStringExtra("brand");
        paymentTerm = intent.getStringExtra("paymentTerm");

        categorytv.setText("Category:"+category);
        subCategorytv.setText("SubCategory:"+sub_category);
        brandtv.setText("Brand:"+brand);
        paymentTermtv.setText("Payment-Term:"+paymentTerm);


        request_json(long_desc,category,sub_category,brand);

    }

    private void request_json(final String long_desc, final String category, final String sub_category,final String brands) {

        String tag_json_obj = "json_obj_req";

        String url = "http://www.power2sme.com/p2sapi/ws/v3/skuList?longdesc="+long_desc+"&category="+category+
                "&subcategory="+sub_category+"&brand="+brands;

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("responce", response.toString());
                        String record;
                        try {
                            record = (String) response.get("TotalRecord");
                            records_tv.setText(record);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("responce", "Error: " + error.getMessage());
                // hide the progress dialog
                pDialog.hide();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("longdesc", long_desc);
                params.put("category", category);
                params.put("sub category", sub_category);
                params.put("brand",brands);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                String creds = String.format("%s:%s","admin","admin");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                headers.put("Authorization", auth);
                return headers;
            }
        };



// Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }
}
