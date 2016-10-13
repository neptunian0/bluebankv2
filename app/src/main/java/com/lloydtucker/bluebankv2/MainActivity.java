package com.lloydtucker.bluebankv2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.lloydtucker.bluebankv2.helpers.BlueBankApiAdapter;
import com.lloydtucker.bluebankv2.interfaces.ApiAdapter;
import com.lloydtucker.bluebankv2.pojos.Accounts;
import com.lloydtucker.bluebankv2.pojos.Customers;

import java.io.IOException;
import java.util.ArrayList;

import static com.lloydtucker.bluebankv2.helpers.Constants.numberApis;

public class MainActivity extends AppCompatActivity {
    ArrayList<Customers> customers = new ArrayList<>();
    ArrayList<Accounts> accounts = new ArrayList<>();
    String result = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ApiAdapter[] apiAdapter = new ApiAdapter[numberApis];
        apiAdapter[0] = new BlueBankApiAdapter();

        //Retrieve the customer data
        try{
            ArrayList<Customers> temp = apiAdapter[0].getCustomers();
            customers.addAll(temp);
        } catch(IOException e){
            e.printStackTrace();
        }

        //Retrieve the account data
        try{
            ArrayList<Accounts> temp = apiAdapter[0].getAccounts();
            accounts.addAll(temp);
        } catch(IOException e){
            e.printStackTrace();
        }

        //test it worked
        Log.d("MainActivity", customers.get(0).getGivenName());
        Log.d("MainActivity", accounts.get(0).getAccountNumber());
        Log.d("MainActivity", accounts.get(1).getAccountNumber());
    }
}
