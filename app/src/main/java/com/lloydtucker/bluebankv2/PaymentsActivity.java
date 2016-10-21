package com.lloydtucker.bluebankv2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Spinner;

import com.lloydtucker.bluebankv2.helpers.PaymentsSpinnerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.lloydtucker.bluebankv2.MainActivity.accounts;

public class PaymentsActivity extends AppCompatActivity {
    @BindView(R.id.paymentSpinner) Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);
        ButterKnife.bind(this);

        PaymentsSpinnerAdapter spinnerAdapter = new PaymentsSpinnerAdapter(this, accounts);
        spinnerAdapter.setDropDownViewResource(R.layout.payment_spinner_item);
        spinner.setAdapter(spinnerAdapter);
    }
}
