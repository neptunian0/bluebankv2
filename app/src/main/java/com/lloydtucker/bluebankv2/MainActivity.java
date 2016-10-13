package com.lloydtucker.bluebankv2;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;

import com.lloydtucker.bluebankv2.helpers.BlueBankApiAdapter;
import com.lloydtucker.bluebankv2.interfaces.ApiAdapter;
import com.lloydtucker.bluebankv2.pojos.Accounts;
import com.lloydtucker.bluebankv2.pojos.Customers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.lloydtucker.bluebankv2.helpers.Constants.numberApis;

public class MainActivity extends AppCompatActivity {
//    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.collapsingToolbar) CollapsingToolbarLayout collapsingToolbar;

    ArrayList<Customers> customers = new ArrayList<>();
    ArrayList<Accounts> accounts = new ArrayList<>();
    String result = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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

        //set the toolbar to include the greeting and the customer name
        String greeting = getGreeting() + customers.get(0).getGivenName();
        collapsingToolbar.setTitle(greeting);
    }

    //Determine which greeting to display in the toolbar
    public String getGreeting() {
        //set the greeting text to good morning, good afternoon,
        //or good evening <name> and the date
        String greeting = "";
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        if (timeOfDay >= 0 && timeOfDay < 12) {
            greeting += "Good morning, ";
        } else if (timeOfDay >= 12 && timeOfDay < 17) {
            greeting += "Good afternoon, ";
        } else if (timeOfDay >= 17 && timeOfDay < 24) {
            greeting += "Good evening, ";
        }
        return greeting;
    }
}
