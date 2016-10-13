package com.lloydtucker.bluebankv2.helpers;

import android.util.Log;

import com.lloydtucker.bluebankv2.Customers;
import com.lloydtucker.bluebankv2.interfaces.ApiAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.lloydtucker.bluebankv2.helpers.Constants.BEARER_TOKEN;
import static com.lloydtucker.bluebankv2.helpers.Constants.BEARER_URI;
import static com.lloydtucker.bluebankv2.helpers.Constants.HEADER;
import static com.lloydtucker.bluebankv2.helpers.Constants.HEADER_BEARER;
import static com.lloydtucker.bluebankv2.helpers.Constants.HTTPS;
import static com.lloydtucker.bluebankv2.helpers.Constants.JSON;
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
    public void getCustomers() throws IOException{
        GET_BEARER();
//        try{
//            jsonObject = new JSONObject(result);
//            bearer = jsonObject.getString(TAG_BEARER);
//        } catch(JSONException e){
//            e.printStackTrace();
//            Log.d("JSONException", "JSON Exception while parsing bearer token");
//        }
//        Log.d("Bearer", bearer);

        /*try{
            result = new APIRequest().execute(request).get();
        } catch(InterruptedException e){
            e.printStackTrace();
            Log.d(TAG, "ERROR: Interrupted Exception");
        }
        catch(ExecutionException e){
            e.printStackTrace();
            Log.d(TAG, "ERROR: Execution Exception");
        }

        //TODO: USE THIS METHOD ABOVE TO RETRIEVE DATA IN THE FUTURE
        Log.d("Response", "Before Customers");
        request = new Request().Builder()
                .header()
                .url()
                .build();
        try {
            result = new APIRequest().execute().get();
        } catch(InterruptedException e){
            e.printStackTrace();
            Log.d(TAG, "ERROR: Interrupted Exception");
        }
        catch(ExecutionException e){
            e.printStackTrace();
            Log.d(TAG, "ERROR: Execution Exception");
        }
        Log.d("response", "Success!!!");
        try {
            JSONArray jsonArr = new JSONArray(result.body().string());
            Log.d("jsonArr", jsonArr.toString());

            // looping through All Contacts
            //should be i < jsonArr.length(), but only want 10 customers
            for (int i = 0; i < jsonArr.length() && i < 10; i++) {
                JSONObject c = jsonArr.getJSONObject(i);
                Log.d("Customers", c.toString());
                Customers cus = new Customers();

                cus.setId(c.getString(TAG_ID));
                cus.setGivenName(c.getString(TAG_GIVEN_NAME));
                cus.setFamilyName(c.getString(TAG_FAMILY_NAME));
                cus.setAddress1(c.getString(TAG_ADDRESS_1));
                cus.setTown(c.getString(TAG_TOWN));
                cus.setPostCode(c.getString(TAG_POST_CODE));

                customers.add(cus);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "ERROR: JSONException");
        }
        catch (IOException e){
            e.printStackTrace();
            Log.d(TAG, "ERROR: IOException");
        }*/
        //Log.d("Response", "size customers " + customers.size());
        //return customers;
    }

    /*
    * Network request methods
    */
    /*public static void GET_BEARER() throws IOException {
        Request request = new Request.Builder()
	    //primary key held below
	    .header(HEADER, blue_primary)
	    .url(new HttpUrl.Builder()
                .scheme(HTTPS)
                .host(BEARER_URI)
                .addPathSegment(BEARER_TOKEN)
                .build())
	    .build();

        String response = client.newCall(request).execute().body().string();

        try {
            JSONObject jsonObj = new JSONObject(response);
            bearer = jsonObj.getString(TAG_BEARER);
        }
        catch(JSONException e){
            e.printStackTrace();
            Log.d("JSONException", "Exception parsing bearer token");
        }
    }*/

    //GET Bearer network request
    public void GET_BEARER(){
        Request request = new Request.Builder()
                //primary key held below
                .header(HEADER, blue_primary)
                .url(getBearerUri())
                .build();

        APIRequest apiRequest = new APIRequest();
        //apiRequest.delegate = this;
        Log.d("BlueBankApiAdapter", "Before");
        try {
            Response response = apiRequest.execute(request).get();
            Log.d("BlueBankApiAdapter", "SUCCESS");
            Log.d("BlueBankApiAdapter", response.body().string());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //GET network request
    @Override
    public Request GET(HttpUrl url) throws IOException {
        if(bearer == null) {
            GET_BEARER();
        }

        Request request = new Request.Builder()
	    //primary key held below
	    .header(HEADER, blue_primary)
        .header(HEADER_BEARER, bearer)
	    .url(url)
	    .build();
        Response response = client.newCall(request).execute();
        return null;
    }

    //POST network request
    @Override
    public Request POST(HttpUrl url, String json) throws IOException {
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
    public Request PATCH(HttpUrl url, String json) throws IOException {
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
        return null;
    }

    public HttpUrl getBearerUri(){
        return new HttpUrl.Builder()
                .scheme(HTTPS)
                .host(BEARER_URI)
                .addPathSegment(BEARER_TOKEN)
                .build();
    }

//    @Override
//    public void customerFinish(String output) {
//        Log.d("BlueBankApiAdapter", "SUCCESS!");
//    }
}
