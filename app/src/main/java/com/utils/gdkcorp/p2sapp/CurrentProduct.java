package com.utils.gdkcorp.p2sapp;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by sysadmin on 29/7/16.
 */
public class CurrentProduct {
    ArrayList<String> categories = new ArrayList<String>();
    ArrayList<String> sub_catagories = new ArrayList<String>();
    ArrayList<String> brands = new ArrayList<String>();

    public String genrateProduct(){
        StringBuilder buffer = new StringBuilder("");
        for(int i=0;i<categories.size();++i){
            String catagory = categories.get(i);
            buffer.append("["+"\n"+"Category:"+catagory);
            if(i<sub_catagories.size()) {
                String sub_catagory = sub_catagories.get(i);
                buffer.append("\n" + "Sub Category:" + sub_catagory);
            }
            if(i<brands.size()) {
                String brand = brands.get(i);
                buffer.append("\n" + "Brand:" + brand + "\n" + "]" + "\n");
            }else{
                buffer.append("\n" + "]"+"\n");
            }
        }
        Log.d("product", "buffer " + buffer.toString());

        return buffer.toString();
    }
}
