package com.lloydtucker.bluebankv2;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.lloydtucker.bluebankv2.helpers.PaymentsSpinnerAdapter;

import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.lloydtucker.bluebankv2.MainActivity.accounts;
import static com.lloydtucker.bluebankv2.helpers.Constants.SEND_PAYMENT;

//TODO: When the account is changed, check the payment amount is less
//TODO: Start the submit payment process
public class PaymentsActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener{
    @BindView(R.id.activity_payments) RelativeLayout paymentsActivity;
    @BindView(R.id.paymentSpinner) Spinner spinner;
    @BindView(R.id.paymentsToolbar) Toolbar toolbar;
    @BindView(R.id.etSortCode) EditText etSortCode;
    @BindView(R.id.etAccountNumber) EditText etAccountNumber;
    @BindView(R.id.etPaymentReference) EditText etPaymentReference;
    @BindView(R.id.etPaymentAmount) EditText etPaymentAmount;
    @BindView(R.id.bMakePayment) Button bMakePayment;

    private String selectedAccountId = "";
    private int selectedAccount = -1;

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
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selectedAccountId = accounts.get(position).getId();
        selectedAccount = position;
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
        double accountBalance = accounts.get(selectedAccount).getAccountBalance();
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

    private boolean enableSubmit(EditText... editTexts) {
        for(EditText values : editTexts){
            if(values.getError() != null || values.getText().toString().length() == 0){
                return false;
            }
        }
        return true;
    }

    @OnClick(R.id.bMakePayment)
    void OnClick(View view){
        Snackbar.make(paymentsActivity, "Hello from Simple Snackbar", Snackbar.LENGTH_LONG).show();
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
            }
        }
    }
}
