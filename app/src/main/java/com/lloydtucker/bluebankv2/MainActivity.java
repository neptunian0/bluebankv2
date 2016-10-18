package com.lloydtucker.bluebankv2;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lloydtucker.bluebankv2.helpers.AccountsAdapter;
import com.lloydtucker.bluebankv2.helpers.BlueBankApiAdapter;
import com.lloydtucker.bluebankv2.interfaces.ApiAdapter;
import com.lloydtucker.bluebankv2.pojos.Accounts;
import com.lloydtucker.bluebankv2.pojos.Customers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.lloydtucker.bluebankv2.helpers.Constants.BLUE_INDEX;
import static com.lloydtucker.bluebankv2.helpers.Constants.API_ADAPTERS;
import static com.lloydtucker.bluebankv2.helpers.Constants.NUMBER_APIS;

public class MainActivity extends AppCompatActivity {
    //    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.collapsingToolbar) CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;

    ArrayList<Customers> customers = new ArrayList<>();
    ArrayList<Accounts> accounts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setUpApiAdapters();

        //Retrieve the customer data
        try {
            ArrayList<Customers> temp = API_ADAPTERS[BLUE_INDEX].getCustomers();
            customers.addAll(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Retrieve the account data
        try {
            ArrayList<Accounts> temp = API_ADAPTERS[BLUE_INDEX].getAccounts();
            accounts.addAll(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //set the toolbar to include the greeting and the customer name
        String greeting = getGreeting() + customers.get(0).getGivenName();
        collapsingToolbar.setTitle(greeting);

        setUpRecyclerView();
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

    private void setUpRecyclerView() {
        AccountsAdapter adapter = new AccountsAdapter(this, accounts);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(this);
        mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManagerVertical);

        //even if we don't use this, it will use the default animation
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void setUpApiAdapters(){
        API_ADAPTERS = new ApiAdapter[NUMBER_APIS];
        API_ADAPTERS[BLUE_INDEX] = new BlueBankApiAdapter();
    }
}
