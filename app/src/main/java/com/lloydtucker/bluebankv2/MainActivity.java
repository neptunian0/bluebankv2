package com.lloydtucker.bluebankv2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lloydtucker.bluebankv2.helpers.BlueBankApiAdapter;
import com.lloydtucker.bluebankv2.interfaces.ApiAdapter;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<Customers> customers = new ArrayList<>();
    String result = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ApiAdapter apiAdapter = new BlueBankApiAdapter();
        try{
            apiAdapter.getCustomers();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
