package com.digidevcloud.productcentral.productcentral.models;

/**
 * Created by usheruk on 04/12/2017.
 */

public class ProductModel {
    private String product_sku;
    private String product_ean;
    private String product_description;
    private String image_link;
    private double rrp;

    public String getProduct_sku() {
        return product_sku;
    }

    public void setProduct_sku(String product_sku) {
        this.product_sku = product_sku;
    }

    public String getProduct_ean() {
        return product_ean;
    }

    public void setProduct_ean(String product_ean) {
        this.product_ean = product_ean;
    }

    public String getImage_link() {
        return image_link;
    }

    public void setImage_link(String image_link) {
        this.image_link = image_link;
    }

    public String getProduct_decription() {
        return product_description;
    }

    public void setProduct_description(String product_description) {
        this.product_description = product_description;
    }

    public double getRrp() {
        return rrp;
    }

    public void setRrp(double rrp) {
        this.rrp = rrp;
    }
}
