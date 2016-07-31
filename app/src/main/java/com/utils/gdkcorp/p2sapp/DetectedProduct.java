package com.utils.gdkcorp.p2sapp;

/**
 * Created by sysadmin on 30/7/16.
 */
public class DetectedProduct {

    private String category;
    private String sub_catagory;
    private String brand;
    private String deliveryTime;

    public String getBrand() {
        return brand;
    }

    public String getCategory() {
        return category;
    }

    public String getSub_catagory() {
        return sub_catagory;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setSub_catagory(String sub_catagory) {
        this.sub_catagory = sub_catagory;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String generateProduct(){
        StringBuilder str = new StringBuilder("");
        str.append("[\nCategory:"+category+"\nSub Category:"+sub_catagory+"\nBrand:"+brand+"\n]");
        return str.toString();
    }
}
