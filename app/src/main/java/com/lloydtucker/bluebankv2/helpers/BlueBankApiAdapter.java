package com.lloydtucker.bluebankv2.helpers;

import android.util.Log;

import com.lloydtucker.bluebankv2.Customers;
import com.lloydtucker.bluebankv2.interfaces.ApiAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.lloydtucker.bluebankv2.helpers.Constants.BEARER_TOKEN;
import static com.lloydtucker.bluebankv2.helpers.Constants.BEARER_URI;
import static com.lloydtucker.bluebankv2.helpers.Constants.BLUE_API;
import static com.lloydtucker.bluebankv2.helpers.Constants.BLUE_URI;
import static com.lloydtucker.bluebankv2.helpers.Constants.BLUE_VERSION;
import static com.lloydtucker.bluebankv2.helpers.Constants.HEADER;
import static com.lloydtucker.bluebankv2.helpers.Constants.HEADER_BEARER;
import static com.lloydtucker.bluebankv2.helpers.Constants.HTTPS;
import static com.lloydtucker.bluebankv2.helpers.Constants.JSON;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_ADDRESS_1;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_BEARER;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_CUSTOMERS;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_FAMILY_NAME;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_GIVEN_NAME;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_ID;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_POST_CODE;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_TOWN;
import static com.lloydtucker.bluebankv2.helpers.Constants.blue_primary;
import static com.lloydtucker.bluebankv2.helpers.Constants.client;

/**
 * Created by lloydtucker on 05/08/2016.
 */
public class BlueBankApiAdapter implements ApiAdapter {
    private static final String TAG = BlueBankApiAdapter.class.getSimpleName();
    private static String bearer;
    private volatile ArrayList<Customers> customers;

    public BlueBankApiAdapter(){
        customers = new ArrayList<Customers>();
    }

    /*
    * Gathering data methods
    */
    @Override
    public ArrayList<Customers> getCustomers() throws IOException {
        ArrayList<Customers> customers = new ArrayList<>();
        //Retrieve the bearer token
        if (bearer == null) {
            bearer = GET_BEARER();
        }

        //Retrieve the customer data, then parse it
        Response response = GET(getCustomersUri());
        String stringResponse = response.body().string();
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(stringResponse);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonCustomer = jsonArray.getJSONObject(i);
                Customers customer = new Customers();

                customer.setId(jsonCustomer.getString(TAG_ID));
                customer.setGivenName(jsonCustomer.getString(TAG_GIVEN_NAME));
                customer.setFamilyName(jsonCustomer.getString(TAG_FAMILY_NAME));
                customer.setAddress1(jsonCustomer.getString(TAG_ADDRESS_1));
                customer.setTown(jsonCustomer.getString(TAG_TOWN));
                customer.setPostCode(jsonCustomer.getString(TAG_POST_CODE));

                customers.add(customer);
                Log.d("BlueBankApiAdapter", customer.getGivenName());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "JSONException while parsing customer data");
        }
        return customers;
    }


    //GET Bearer network request
    public String GET_BEARER() throws IOException{
        //Build the bearer request
        Request request = new Request.Builder()
                //primary key held below
                .header(HEADER, blue_primary)
                .url(getBearerUri())
                .build();

        //Make the API Request and retrieve the result
        Response response = null;
        try {
            response = new APIRequest().execute(request).get();
//            Log.d("BlueBankApiAdapter", "SUCCESS");
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d(TAG, "InterruptedException while retrieving bearer token");
        } catch (ExecutionException e) {
            e.printStackTrace();
            Log.d(TAG, "ExecutionException while retrieving bearer token");
        }

        //Parse the JSON object returned to return the bearer token
        JSONObject jsonObject;
        String bearer = "";
        try {
            jsonObject = new JSONObject(response.body().string());
            bearer = jsonObject.getString(TAG_BEARER);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "JSONException while parsing bearer token");
        }
        return bearer;
    }

    //GET network request
    @Override
    public Response GET(HttpUrl url) throws IOException {
        Request request = new Request.Builder()
	    //primary key held below
	    .header(HEADER, blue_primary)
        .header(HEADER_BEARER, bearer)
	    .url(url)
	    .build();

        Response response = null;
        try {
            response = new APIRequest().execute(request).get();
//            Log.d("BlueBankApiAdapter", "SUCCESS");
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d(TAG, "InterruptedException while retrieving customer data");
        } catch (ExecutionException e) {
            e.printStackTrace();
            Log.d(TAG, "ExecutionException while retrieving customer data");
        }

        return response;
    }

    //POST network request
    @Override
    public Response POST(HttpUrl url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
	    //primary key held below
	    .header(HEADER, blue_primary)
        .header(HEADER_BEARER, bearer)
	    .url(url)
	    .post(body)
	    .build();
        Response response = client.newCall(request).execute();
        return null;
    }

    @Override
    public Response PATCH(HttpUrl url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                //primary key held below
                .header(HEADER, blue_primary)
                .header(HEADER_BEARER, bearer)
                .url(url)
                .patch(body)
                .build();
        Response response = client.newCall(request).execute();
        return null;
    }

    public HttpUrl getCustomersUri(){
        return new HttpUrl.Builder()
                .scheme(HTTPS)
                .host(BLUE_URI)
                .addPathSegment(BLUE_API)
                .addPathSegment(BLUE_VERSION)
                .addPathSegment(TAG_CUSTOMERS)
                .build();
    }

    public HttpUrl getBearerUri(){
        return new HttpUrl.Builder()
                .scheme(HTTPS)
                .host(BEARER_URI)
                .addPathSegment(BEARER_TOKEN)
                .build();
    }
}
