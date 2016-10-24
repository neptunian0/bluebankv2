package com.lloydtucker.bluebankv2;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.lloydtucker.bluebankv2.helpers.PaymentStatus;
import com.lloydtucker.bluebankv2.helpers.PaymentsSpinnerAdapter;
import com.lloydtucker.bluebankv2.pojos.Accounts;
import com.lloydtucker.bluebankv2.pojos.Payments;

import java.io.IOException;
import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.lloydtucker.bluebankv2.MainActivity.accounts;
import static com.lloydtucker.bluebankv2.helpers.Constants.API_ADAPTERS;
import static com.lloydtucker.bluebankv2.helpers.Constants.SEND_PAYMENT;

//TODO: When the account is changed, check the payment amount is less
//TODO: Start the submit payment process
public class PaymentsActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener{
    private static final String TAG = PaymentsActivity.class.getSimpleName();

    @BindView(R.id.activity_payments) RelativeLayout paymentsActivity;
    @BindView(R.id.llPayment) LinearLayout llPayment;
    @BindView(R.id.llOtp) LinearLayout llOtp;
    @BindView(R.id.paymentSpinner) Spinner spinner;
    @BindView(R.id.paymentsToolbar) Toolbar toolbar;
    @BindView(R.id.etSortCode) EditText etSortCode;
    @BindView(R.id.etAccountNumber) EditText etAccountNumber;
    @BindView(R.id.etPaymentReference) EditText etPaymentReference;
    @BindView(R.id.etPaymentAmount) EditText etPaymentAmount;
    @BindView(R.id.etOtpCode) EditText etOtpCode;
    @BindView(R.id.bOtpCode) Button bOtpCode;
    @BindView(R.id.bMakePayment) Button bMakePayment;

    private Accounts selectedAccount = null;
    private Payments payment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);
        ButterKnife.bind(this);

        toolbar.setTitle(SEND_PAYMENT);
        toolbar.setTitleTextColor(Color.WHITE);
        PaymentsSpinnerAdapter spinnerAdapter = new PaymentsSpinnerAdapter(this, accounts);
        spinnerAdapter.setDropDownViewResource(R.layout.payment_spinner_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);

        etSortCode.addTextChangedListener(new PaymentTextWatcher(etSortCode));
        etAccountNumber.addTextChangedListener(new PaymentTextWatcher(etAccountNumber));
        etPaymentAmount.addTextChangedListener(new PaymentTextWatcher(etPaymentAmount));
        etOtpCode.addTextChangedListener(new PaymentTextWatcher(etOtpCode));
    }

    //assumes that the spinner index will match up with the account index in MainActivity
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedAccount = accounts.get(position);
//        Log.d("Account", selectedAccount);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    private void validateSortCode(Editable s) {
        int length = s.length();
        if (length == 0) {
            etSortCode.setError("Please enter a sort code");
        } else if(length == 6){
            etSortCode.setError(null);
        } else {
            etSortCode.setError("Sort code must have 6 digits");
        }

        //enable the submit button if possible
        boolean enabled = enableSubmit(etSortCode, etAccountNumber, etPaymentAmount);
        bMakePayment.setEnabled(enabled);
    }

    private void validateAccountNumber(Editable s) {
        int length = s.length();
        if (length == 0) {
            etAccountNumber.setError("Please enter an account number");
        } else if(length == 8){
            etAccountNumber.setError(null);
        } else {
            etAccountNumber.setError("Account number must have 8 digits");
        }

        //enable the submit button if possible
        boolean enabled = enableSubmit(etSortCode, etAccountNumber, etPaymentAmount);
        bMakePayment.setEnabled(enabled);
    }

    private void validatePaymentAmount(Editable s, TextWatcher textWatcher) {
        String current = "";
        double accountBalance = selectedAccount.getAccountBalance();
        if (!s.toString().equals(current)) {
            etPaymentAmount.removeTextChangedListener(textWatcher);

            String replaceable = String.format("[%s,.\\s]",
                    NumberFormat.getCurrencyInstance().getCurrency().getSymbol());
            String cleanString = s.toString().replaceAll(replaceable, "");

            double parsed;
            try {
                parsed = Double.parseDouble(cleanString);
            } catch (NumberFormatException e) {
                parsed = 0.00;
            }
            String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

            current = formatted;
            etPaymentAmount.setText(formatted);
            etPaymentAmount.setSelection(formatted.length());
            etPaymentAmount.addTextChangedListener(textWatcher);
        }
        Double amt = Double.parseDouble(etPaymentAmount.getText().
                toString().replaceAll("[£,]", ""));
        if(amt == 0.00){
            etPaymentAmount.setError("Please enter an amount greater than £0.00");
        } else if(amt > accountBalance){
            etPaymentAmount.setError("Please enter an amount less than your" +
                    " available balance");
        } else{
            etPaymentAmount.setError(null);
        }

        //enable the submit button if possible
        boolean enabled = enableSubmit(etSortCode, etAccountNumber, etPaymentAmount);
        bMakePayment.setEnabled(enabled);
    }

    private void validateOtpCode(Editable s){
        if (s.length() == 0) {
            etOtpCode.setError("Enter OTP Code sent via SMS to continue");
        } else{
            etOtpCode.setError(null);
        }
        //enable the submit button if possible
        bOtpCode.setEnabled(enableSubmit(etOtpCode));
    }

    private boolean enableSubmit(EditText... editTexts) {
        for(EditText values : editTexts){
            if(values.getError() != null || values.getText().toString().length() == 0){
                return false;
            }
        }
        return true;
    }

    @OnClick({R.id.bMakePayment, R.id.bOtpCode})
    void OnClick(View view) {
        switch (view.getId()) {
            case R.id.bMakePayment:
                submitPayment();
                break;
            case R.id.bOtpCode:
                submitOtp();
                break;
        }
    }

    private void submitPayment(){
        final String sortCode = etSortCode.getText().toString();
        final String accountNumber = etAccountNumber.getText().toString();
        final String paymentReference = etPaymentReference.getText().toString();
        //remove the £ symbol and commas from the text
        final String paymentAmount = etPaymentAmount.getText().toString().replaceAll("[£,]", "");

        //Set up and execute POST request
        final String fromAccountId = selectedAccount.getId();
        int apiIndex = selectedAccount.getApiAdapterType().getIndex();
        payment = new Payments(fromAccountId,
                accountNumber,
                sortCode,
                paymentReference,
                Double.parseDouble(paymentAmount));
        try {
            payment = API_ADAPTERS[apiIndex].postPayment(payment);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "IOException while sending payment");
        }

        //check the returned payment status and either close or prepare for OTP code
        paymentComplete(payment.getPaymentStatus());
    }

    private void submitOtp(){
        final String otpCode = etOtpCode.getText().toString();
        int apiIndex = payment.getApiAdapterType().getIndex();
        payment.setOtpCode(otpCode);

        try {
            payment = API_ADAPTERS[apiIndex].patchPayment(payment);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "IOException while sending OTP Code");
        }

        //check the returned payment status and either close or prepare for OTP code
        paymentComplete(payment.getPaymentStatus());
    }

    private void paymentComplete(PaymentStatus paymentStatus){
        switch(paymentStatus){
            case SUCCESS:
                //PAYMENT SUCCESSFUL
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setMessage("Payment successful!");
                alert.setPositiveButton("Done",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                AlertDialog dialog = alert.create();
                dialog.show();
                break;
            case TWO_FACTOR_AUTH:
                llPayment.setVisibility(View.GONE);
                llOtp.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    private class PaymentTextWatcher implements TextWatcher{
        private View view;

        private PaymentTextWatcher(View view){
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            switch(view.getId()){
                case R.id.etSortCode:
                    validateSortCode(s);
                    break;
                case R.id.etAccountNumber:
                    validateAccountNumber(s);
                    break;
                case R.id.etPaymentReference:
                    break;
                case R.id.etPaymentAmount:
                    validatePaymentAmount(s, this);
                    break;
                case R.id.etOtpCode:
                    validateOtpCode(s);
                    break;
            }
        }
    }
}
