package com.lloydtucker.bluebankv2.interfaces;

import com.lloydtucker.bluebankv2.pojos.Accounts;
import com.lloydtucker.bluebankv2.pojos.Customers;
import com.lloydtucker.bluebankv2.pojos.Payments;
import com.lloydtucker.bluebankv2.pojos.Transactions;

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

    //get the transactions data
    ArrayList<Transactions> getTransactions(String accountId) throws IOException;

    //post the payment data
    Payments postPayment(Payments payment) throws IOException;

    //patch the payment data
    Payments patchPayment(Payments payment) throws IOException;

    //GET network request
    Response GET(HttpUrl url) throws IOException;

    //POST network request
    Response POST(HttpUrl url, String json) throws IOException;

    //PATCH network request
    Response PATCH(HttpUrl url, String json) throws IOException;
}
