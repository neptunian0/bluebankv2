package com.lloydtucker.bluebankv2.interfaces;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Created by lloydtucker on 07/10/2016.
 */

public interface ApiAdapter {
    void getCustomers() throws IOException;

    //GET network request
    Request GET(HttpUrl url) throws IOException;

    //POST network request
    Request POST(HttpUrl url, String json) throws IOException;

    //PATCH network request
    Request PATCH(HttpUrl url, String json) throws IOException;
}
