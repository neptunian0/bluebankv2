package com.lloydtucker.bluebankv2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.lloydtucker.bluebankv2.helpers.Constants;
import com.lloydtucker.bluebankv2.helpers.TransactionsAdapter;
import com.lloydtucker.bluebankv2.pojos.Transactions;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.lloydtucker.bluebankv2.helpers.Constants.API_ADAPTERS;
import static com.lloydtucker.bluebankv2.helpers.Constants.BLUE_INDEX;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_ACCOUNT_BALANCE;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_ACCOUNT_DETAILS;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_ACCOUNT_FRIENDLY_NAME;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_ACCOUNT_NUMBER;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_CUSTOMER_ID;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_ID;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_SORT_CODE;

//TODO: Might want to pre-load transactions for each account - have an expiry time for them
public class TransactionsActivity extends AppCompatActivity {
    @BindView(R.id.accountHeader) CardView accountHeader;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;

    private ArrayList<Transactions> transactions = new ArrayList<>();
    private String customerId, accountNumber, sortCode, accountDetails,
            accountId, accountFriendlyName, accountBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        ButterKnife.bind(this);

        //bind the views within the card view header
        TextView txvAccountBalance = ButterKnife.findById(accountHeader, R.id.accountBalance);
        TextView txvAccountType = ButterKnife.findById(accountHeader, R.id.accountType);
        TextView txvAccountDetails = ButterKnife.findById(accountHeader, R.id.accountDetails);
        ImageView ivAccountImage = ButterKnife.findById(accountHeader, R.id.accountImage);

        //Unpack the bundle
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            accountFriendlyName = extras.getString(TAG_ACCOUNT_FRIENDLY_NAME);
            accountNumber = extras.getString(TAG_ACCOUNT_NUMBER);
            sortCode = extras.getString(TAG_SORT_CODE);
            accountId = extras.getString(TAG_ID);
            accountBalance = extras.getString(TAG_ACCOUNT_BALANCE);
            accountDetails = extras.getString(TAG_ACCOUNT_DETAILS);
            customerId = extras.getString(TAG_CUSTOMER_ID);
        }

        //set the header text values
        txvAccountBalance.setText(accountBalance);
        txvAccountDetails.setText(accountDetails);
        txvAccountType.setText(accountFriendlyName);
        ivAccountImage.setImageResource(Constants.getImage(accountFriendlyName));

        //Retrieve the transaction data
        try {
            ArrayList<Transactions> temp = API_ADAPTERS[BLUE_INDEX].getTransactions(accountId);
            transactions.addAll(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        TransactionsAdapter adapter = new TransactionsAdapter(this, transactions);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(this);
        mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLinearLayoutManagerVertical);
    }

    @OnClick(R.id.transactionFab)
    public void onClick(View v){
        Log.d("Clicked", "on FAB");
        Intent intent = new Intent(this, PaymentsActivity.class);
        this.startActivity(intent);
    }
}
