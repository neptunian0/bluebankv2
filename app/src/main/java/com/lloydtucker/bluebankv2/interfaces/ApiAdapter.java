package com.lloydtucker.bluebankv2.interfaces;

import com.lloydtucker.bluebankv2.pojos.Accounts;
import com.lloydtucker.bluebankv2.pojos.Customers;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.HttpUrl;
import okhttp3.Response;

/**
 * Created by lloydtucker on 07/10/2016.
 */

public interface ApiAdapter {
    //get the customer data
    ArrayList<Customers> getCustomers() throws IOException;

    //get the account data
    ArrayList<Accounts> getAccounts() throws IOException;

    //GET network request
    Response GET(HttpUrl url) throws IOException;

    //POST network request
    Response POST(HttpUrl url, String json) throws IOException;

    //PATCH network request
    Response PATCH(HttpUrl url, String json) throws IOException;
}
