package com.lloydtucker.bluebankv2.interfaces;

import com.lloydtucker.bluebankv2.Customers;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.HttpUrl;
import okhttp3.Response;

/**
 * Created by lloydtucker on 07/10/2016.
 */

public interface ApiAdapter {
    ArrayList<Customers> getCustomers() throws IOException;

    //GET network request
    Response GET(HttpUrl url) throws IOException;

    //POST network request
    Response POST(HttpUrl url, String json) throws IOException;

    //PATCH network request
    Response PATCH(HttpUrl url, String json) throws IOException;
}
