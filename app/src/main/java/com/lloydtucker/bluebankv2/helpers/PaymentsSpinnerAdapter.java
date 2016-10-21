package com.lloydtucker.bluebankv2.helpers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lloydtucker.bluebankv2.R;
import com.lloydtucker.bluebankv2.pojos.Accounts;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.lloydtucker.bluebankv2.helpers.Constants.formatBalance;
import static com.lloydtucker.bluebankv2.helpers.Constants.getApiName;
import static com.lloydtucker.bluebankv2.helpers.Constants.getCurrencySymbol;

/**
 * Created by lloydtucker on 21/10/2016.
 */

public class PaymentsSpinnerAdapter extends ArrayAdapter<Accounts> {
    private static final String TAG = TransactionsAdapter.class.getSimpleName();
    private LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
            Context.LAYOUT_INFLATER_SERVICE);

    public PaymentsSpinnerAdapter(Context context, ArrayList<Accounts> accounts) {
        super(context, R.layout.payment_spinner_item, R.id.paymentAccount, accounts);
        setDropDownViewResource(R.layout.payment_spinner_item);
    }

    @NonNull
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent){
        return getSpinnerView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getSpinnerView(position, convertView, parent);
    }

    //single method to handle both overridden methods
    private View getSpinnerView(int position, View view, ViewGroup parent){
        Accounts account = getItem(position);

        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.payment_spinner_item, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        String accountType = "(" + getApiName(account.getApiAdapterType()) + ") "
                + account.getAccountFriendlyName();
        holder.txvAccount.setText(accountType);
        String balance = getCurrencySymbol(account.getAccountCurrency())
                + formatBalance(account.getAccountBalance());
        holder.txvBalance.setText(balance);

        return view;
    }

    static class ViewHolder{
        @BindView(R.id.paymentAccount) TextView txvAccount;
        @BindView(R.id.paymentBalance) TextView txvBalance;

        ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}
