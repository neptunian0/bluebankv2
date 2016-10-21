package com.lloydtucker.bluebankv2;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;

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
import butterknife.OnClick;

import static com.lloydtucker.bluebankv2.R.id.accountBalance;
import static com.lloydtucker.bluebankv2.R.id.accountDetails;
import static com.lloydtucker.bluebankv2.helpers.Constants.API_ADAPTERS;
import static com.lloydtucker.bluebankv2.helpers.Constants.BLUE_INDEX;
import static com.lloydtucker.bluebankv2.helpers.Constants.NUMBER_APIS;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_ACCOUNTS_SIZE;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_ACCOUNT_BALANCE;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_ACCOUNT_DETAILS;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_ACCOUNT_FRIENDLY_NAME;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_ID;
import static com.lloydtucker.bluebankv2.helpers.Constants.paymentSpinner;

public class MainActivity extends AppCompatActivity {
    //    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.collapsingToolbar) CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.mainFAB) FloatingActionButton mainFab;

    ArrayList<Customers> customers = new ArrayList<>();
    static ArrayList<Accounts> accounts = new ArrayList<>();

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

        //after the content is loaded, and the accounts populated, load payments spinner
        paymentSpinner = (Spinner) findViewById(R.id.paymentSpinner);
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

    @OnClick(R.id.mainFAB)
    public void onClick(View v){
        Log.d("Clicked", "on FAB");
        Intent intent = new Intent(this, PaymentsActivity.class);

        int accountsSize = accounts.size();
        intent.putExtra(TAG_ACCOUNTS_SIZE, accountsSize);
        for(int i = 0; i < accountsSize; i++) {
            intent.putExtra(TAG_ID + i, accounts.get(i).getId());
            intent.putExtra(TAG_ACCOUNT_FRIENDLY_NAME + i, accounts.get(i).getAccountFriendlyName());
            intent.putExtra(TAG_ACCOUNT_DETAILS + i, accountDetails);
            intent.putExtra(TAG_ACCOUNT_BALANCE + i, accountBalance);
        }
        this.startActivity(intent);
    }
}
