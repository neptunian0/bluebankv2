package com.lloydtucker.bluebankv2.helpers;

import android.util.Log;

import com.lloydtucker.bluebankv2.interfaces.ApiAdapter;
import com.lloydtucker.bluebankv2.pojos.Accounts;
import com.lloydtucker.bluebankv2.pojos.Customers;
import com.lloydtucker.bluebankv2.pojos.Payments;
import com.lloydtucker.bluebankv2.pojos.Transactions;

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

import static com.lloydtucker.bluebankv2.helpers.ApiAdapterType.BLUE;
import static com.lloydtucker.bluebankv2.helpers.Constants.ACCOUNTS;
import static com.lloydtucker.bluebankv2.helpers.Constants.BEARER_TOKEN;
import static com.lloydtucker.bluebankv2.helpers.Constants.BEARER_URI;
import static com.lloydtucker.bluebankv2.helpers.Constants.BLUE_API;
import static com.lloydtucker.bluebankv2.helpers.Constants.BLUE_PRIMARY;
import static com.lloydtucker.bluebankv2.helpers.Constants.BLUE_URI;
import static com.lloydtucker.bluebankv2.helpers.Constants.BLUE_VERSION;
import static com.lloydtucker.bluebankv2.helpers.Constants.HEADER;
import static com.lloydtucker.bluebankv2.helpers.Constants.HEADER_BEARER;
import static com.lloydtucker.bluebankv2.helpers.Constants.HTTPS;
import static com.lloydtucker.bluebankv2.helpers.Constants.JSON;
import static com.lloydtucker.bluebankv2.helpers.Constants.PAYMENTS;
import static com.lloydtucker.bluebankv2.helpers.Constants.SORT_ORDER;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_ACCOUNTS;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_ACCOUNT_BALANCE;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_ACCOUNT_CURRENCY;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_ACCOUNT_FRIENDLY_NAME;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_ACCOUNT_ID;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_ACCOUNT_NUMBER;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_ACCOUNT_TYPE;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_ADDRESS_1;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_BEARER;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_CUSTOMERS;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_CUSTOMER_ID;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_FAMILY_NAME;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_GIVEN_NAME;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_ID;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_OTP_CODE;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_PAYMENT_AMOUNT;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_PAYMENT_REFERENCE;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_PAYMENT_STATUS;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_POST_CODE;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_SORT_CODE;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_TOWN;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_TO_ACCOUNT_NUMBER;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_TO_SORT_CODE;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_TRANSACTIONS;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_TRANSACTION_AMOUNT;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_TRANSACTION_DATE;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_TRANSACTION_DESCRIPTION;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_TRANSACTION_TYPE;
import static com.lloydtucker.bluebankv2.helpers.Constants.TRANSACTION_DATE_TIME_DESC;
import static com.lloydtucker.bluebankv2.helpers.PaymentStatus.FAILED;
import static com.lloydtucker.bluebankv2.helpers.PaymentStatus.SUCCESS;
import static com.lloydtucker.bluebankv2.helpers.PaymentStatus.TWO_FACTOR_AUTH;

/**
 * Created by lloydtucker on 05/08/2016.
 */
public class BlueBankApiAdapter implements ApiAdapter {
    private static final String TAG = BlueBankApiAdapter.class.getSimpleName();
    private static String bearer;

    //Store the retrieved data locally to enable greater
    //modularity in travering the respective APIs
    private ArrayList<Customers> customers;
    private ArrayList<Accounts> accounts;
    private ArrayList<Transactions> transactions;

    //Empty constructor
    public BlueBankApiAdapter(){
        customers = new ArrayList<>();
        accounts = new ArrayList<>();
        transactions = new ArrayList<>();
    }

    /*
    * GATHERING DATA METHODS
    */
    //assumes there will only be a single Customer object
    @Override
    public ArrayList<Customers> getCustomers() throws IOException {
        //Retrieve the bearer token
        if (bearer == null) {
            bearer = GET_BEARER();
        }

        //Retrieve the customer data, then parse it
        Response response = GET(getCustomersUri());
        String stringResponse = response.body().string();
        try {
            JSONArray jsonArray = new JSONArray(stringResponse);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonCustomer = jsonArray.getJSONObject(i);
                Customers customer = new Customers();

                customer.setId(jsonCustomer.getString(TAG_ID));
                customer.setGivenName(jsonCustomer.getString(TAG_GIVEN_NAME));
                customer.setFamilyName(jsonCustomer.getString(TAG_FAMILY_NAME));
                customer.setAddress1(jsonCustomer.getString(TAG_ADDRESS_1));
                customer.setTown(jsonCustomer.getString(TAG_TOWN));
                customer.setPostCode(jsonCustomer.getString(TAG_POST_CODE));
                customer.setApiAdapterType(BLUE);

                customers.add(customer);
                Log.d("BlueBankApiAdapter", customer.getGivenName());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "JSONException while parsing customer data");
        }
        return customers;
    }

    @Override
    public ArrayList<Accounts> getAccounts() throws IOException{
        //check the dependencies for retrieving the account objects
        if(bearer == null){
            bearer = GET_BEARER();
        }
        if(customers.isEmpty()){
            //don't need to store the returned customers list
            //they're stored in the ApiAdapter object
            getCustomers();
        }

        //Retrieve the accounts data, then parse it
        String customerId = customers.get(0).getId(); //assumes only one customer exists
        Response response = GET(getAccountsUri(customerId));
        String stringResponse = response.body().string();
        try {
            JSONArray jsonArray = new JSONArray(stringResponse);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonAccount = jsonArray.getJSONObject(i);
                Accounts account = new Accounts();

                account.setId(jsonAccount.getString(TAG_ID));
                account.setSortCode(jsonAccount.getString(TAG_SORT_CODE));
                account.setAccountType(jsonAccount.getString(TAG_ACCOUNT_TYPE));
                account.setAccountFriendlyName(jsonAccount.getString(TAG_ACCOUNT_FRIENDLY_NAME));
                account.setAccountBalance(jsonAccount.getDouble(TAG_ACCOUNT_BALANCE));
                account.setAccountCurrency(jsonAccount.getString(TAG_ACCOUNT_CURRENCY));
                account.setCustId(jsonAccount.getString(TAG_CUSTOMER_ID));
                account.setAccountNumber(jsonAccount.getString(TAG_ACCOUNT_NUMBER));
                account.setApiAdapterType(BLUE);

                accounts.add(account);
                Log.d("BlueBankApiAdapter", account.getAccountNumber());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "JSONException while parsing account data");
        }
        return accounts;
    }

    @Override
    public ArrayList<Transactions> getTransactions(String accountId) throws IOException{
        //check the dependencies for retrieving the transaction objects
        if(bearer == null){
            bearer = GET_BEARER();
        }

        transactions.clear();

        //Retrieve the transactions data, then parse it
        Response response = GET(getTransactionsUri(accountId));
        String stringResponse = response.body().string();
        try {
            JSONArray jsonArray = new JSONArray(stringResponse);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonAccount = jsonArray.getJSONObject(i);
                Transactions transaction = new Transactions();

                transaction.setId(jsonAccount.getString(TAG_ID));
                transaction.setAccountId(jsonAccount.getString(TAG_ACCOUNT_ID));
                transaction.setTransactionDateTime(jsonAccount.getString(TAG_TRANSACTION_DATE));
                transaction.setTransactionAmount(jsonAccount.getDouble(TAG_TRANSACTION_AMOUNT));
                transaction.setAccountBalance(jsonAccount.getDouble(TAG_ACCOUNT_BALANCE));
                transaction.setTransactionType(jsonAccount.getString(TAG_TRANSACTION_TYPE));
                transaction.setTransactionDescription(jsonAccount.getString(
                        TAG_TRANSACTION_DESCRIPTION));
                transaction.setApiAdapterType(BLUE);

                transactions.add(transaction);
//                Log.d("BlueBankApiAdapter", "" + transaction.getTransactionAmount());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "JSONException while parsing transaction data");
        }
        return transactions;
    }

    @Override
    public Payments postPayment(Payments payment) throws IOException{
        String jsonPayment = paymentJson(payment);
        Response response = POST(postPaymentsUri(payment.getFromAccountId()), jsonPayment);
        String stringResponse = response.body().string();

        try {
            //extract the status and the paymentId from the response
            JSONObject jsonObject = new JSONObject(stringResponse);
            String paymentStatus = jsonObject.getString(TAG_PAYMENT_STATUS);
            String paymentId = jsonObject.getString(TAG_ID);
            payment.setPaymentStatus(paymentStatus(paymentStatus));
            payment.setId(paymentId);
            payment.setApiAdapterType(BLUE);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "JSONException while parsing payment response");
        }
        return payment;
    }

    @Override
    public Payments patchPayment(Payments payment) throws IOException{
        String otpCode = payment.getOtpCode();
        if(otpCode == null){
            return null;
        }
        String jsonOtp = otpJson(otpCode);
        String fromAccountId = payment.getFromAccountId(),
               paymentId = payment.getId();
        Response response = PATCH(patchPaymentsUri(fromAccountId, paymentId), jsonOtp);
        String stringResponse = response.body().string();

        try {
            JSONObject jsonObject = new JSONObject(stringResponse);
            PaymentStatus paymentStatus = paymentStatus(jsonObject.getString(TAG_PAYMENT_STATUS));
            payment.setPaymentStatus(paymentStatus);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "JSONException while parsing OTP Code response");
        }
        return payment;
    }

    //GET Bearer network request
    private String GET_BEARER() throws IOException{
        //Build the bearer request
        Request request = new Request.Builder()
                //primary key held below
                .header(HEADER, BLUE_PRIMARY)
                .url(getBearerUri())
                .build();

        //Make the API Request and retrieve the result
        Response response = executeRequest(request);

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
	    .header(HEADER, BLUE_PRIMARY)
        .header(HEADER_BEARER, bearer)
	    .url(url)
	    .build();

        return executeRequest(request);
    }

    //POST network request
    @Override
    public Response POST(HttpUrl url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
	    //primary key held below
	    .header(HEADER, BLUE_PRIMARY)
        .header(HEADER_BEARER, bearer)
	    .url(url)
	    .post(body)
	    .build();

        return executeRequest(request);
    }

    //PATCH network request (notably for patching a payment)
    @Override
    public Response PATCH(HttpUrl url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                //primary key held below
                .header(HEADER, BLUE_PRIMARY)
                .header(HEADER_BEARER, bearer)
                .url(url)
                .patch(body)
                .build();

        return executeRequest(request);
    }

    private Response executeRequest(Request request){
        Response response = null;
        try {
            response = new APIRequest().execute(request).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d(TAG, "InterruptedException while retrieving customer data");
        } catch (ExecutionException e) {
            e.printStackTrace();
            Log.d(TAG, "ExecutionException while retrieving customer data");
        }
        return response;
    }

    /*
    * GET URI METHODS
    */
    private HttpUrl getBearerUri(){
        return new HttpUrl.Builder()
                .scheme(HTTPS)
                .host(BEARER_URI)
                .addPathSegment(BEARER_TOKEN)
                .build();
    }

    private HttpUrl getCustomersUri(){
        return new HttpUrl.Builder()
                .scheme(HTTPS)
                .host(BLUE_URI)
                .addPathSegment(BLUE_API)
                .addPathSegment(BLUE_VERSION)
                .addPathSegment(TAG_CUSTOMERS)
                .build();
    }

    private HttpUrl getAccountsUri(String id) {
        return new HttpUrl.Builder()
                .scheme(HTTPS)
                .host(BLUE_URI)
                .addPathSegment(BLUE_API)
                .addPathSegment(BLUE_VERSION)
                .addPathSegment(TAG_CUSTOMERS)
                .addPathSegment(id)
                .addPathSegment(TAG_ACCOUNTS)
                .build();
    }

    private HttpUrl getTransactionsUri(String id) {
        return new HttpUrl.Builder()
                .scheme(HTTPS)
                .host(BLUE_URI)
                .addPathSegment(BLUE_API)
                .addPathSegment(BLUE_VERSION)
                .addPathSegment(ACCOUNTS)
                .addPathSegment(id)
                .addPathSegment(TAG_TRANSACTIONS)
                .addQueryParameter(SORT_ORDER, TRANSACTION_DATE_TIME_DESC)
                .build();
    }

    private HttpUrl postPaymentsUri(String id) {
        return new HttpUrl.Builder()
                .scheme(HTTPS)
                .host(BLUE_URI)
                .addPathSegment(BLUE_API)
                .addPathSegment(BLUE_VERSION)
                .addPathSegment(ACCOUNTS)
                .addPathSegment(id)
                .addPathSegment(PAYMENTS)
                .build();
    }

    public static HttpUrl patchPaymentsUri(String id, String paymentId){
        return new HttpUrl.Builder()
                .scheme(HTTPS)
                .host(BLUE_URI)
                .addPathSegment(BLUE_API)
                .addPathSegment(BLUE_VERSION)
                .addPathSegment(ACCOUNTS)
                .addPathSegment(id)
                .addPathSegment(PAYMENTS)
                .addPathSegment(paymentId)
                .build();
    }

    private String paymentJson(Payments payment){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TAG_TO_ACCOUNT_NUMBER, payment.getToAccountNumber());
            jsonObject.put(TAG_TO_SORT_CODE, payment.getToSortCode());
            jsonObject.put(TAG_PAYMENT_REFERENCE, payment.getPaymentReference());
            jsonObject.put(TAG_PAYMENT_AMOUNT, payment.getPaymentAmount());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "JSONException while building payment request");
        }
        return jsonObject.toString();
    }

    private String otpJson(String otp){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TAG_OTP_CODE, otp);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "JSONException while building OTP request");
        }
        return jsonObject.toString();
    }

    private PaymentStatus paymentStatus(String response){
        PaymentStatus status = null;
        switch (response){
            case "Pending":
                status = SUCCESS;
                break;
            case "2FA required":
                status = TWO_FACTOR_AUTH;
                break;
            default:
                status = FAILED;
                break;
        }
        return status;
    }
}
