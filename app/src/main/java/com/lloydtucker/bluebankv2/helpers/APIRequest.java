package com.lloydtucker.bluebankv2.helpers;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

import static com.lloydtucker.bluebankv2.helpers.Constants.client;

/**
 * Created by lloydtucker on 11/10/2016.
 */

public class APIRequest extends AsyncTask<Request, Void, Response>{
    private static final String TAG = APIRequest.class.getSimpleName();

    @Override
    protected Response doInBackground(Request... params) {
        Response result = null;
        try {
            for(Request r : params) {
                result = client.newCall(r).execute();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "ERROR: IOException");
        }
        return result;
    }
}
