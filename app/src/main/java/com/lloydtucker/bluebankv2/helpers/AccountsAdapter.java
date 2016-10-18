package com.lloydtucker.bluebankv2.helpers;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lloydtucker.bluebankv2.R;
import com.lloydtucker.bluebankv2.TransactionsActivity;
import com.lloydtucker.bluebankv2.pojos.Accounts;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_ACCOUNT_BALANCE;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_ACCOUNT_DETAILS;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_ACCOUNT_FRIENDLY_NAME;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_ACCOUNT_NUMBER;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_ACCOUNT_SHARED;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_CUSTOMER_ID;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_ID;
import static com.lloydtucker.bluebankv2.helpers.Constants.TAG_SORT_CODE;

/**
 * Created by lloydtucker on 14/10/2016.
 */

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.MyViewHolder> {
    private List<Accounts> mDataList;
    private LayoutInflater mInflater;
    private Context context;

    public AccountsAdapter(Context context, List<Accounts> data){
        this.mDataList = data;
        mInflater = LayoutInflater.from(context);
    }

    /*
    * This gets called only when the ViewHolder is first created
    * You'll notice it'll only create as many Views as required for the screen
    */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        View view = mInflater.inflate(R.layout.recycler_view_account_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder " + position);

        Accounts currentObj = mDataList.get(position);
        holder.setData(currentObj, position); //important for onClickListeners and buttons
        holder.setListeners();
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.accountBalance)  TextView txvAccountBalance;
        @BindView(R.id.accountDetails)  TextView txvAccountDetails;
        @BindView(R.id.accountType)     TextView txvAccountType;
        @BindView(R.id.accountImage)    ImageView ivAccountImage;
        int position;
        Accounts current;

        //variables for passing to the Transactions activity
        String accountBalance, accountDetails;

        MyViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
        }

        void setData(Accounts current, int position) {
            String accountType =    current.getAccountFriendlyName();
            accountDetails = current.getAccountNumber() + "   |   " +
                                    formatSortCode(current.getSortCode());
            accountBalance = getCurrencySymbol(current.getAccountCurrency()) +
                                    formatBalance(current.getAccountBalance());
            int accountImage =      Constants.getImage(accountType);

            txvAccountType.setText(accountType);
            txvAccountDetails.setText(accountDetails);
            txvAccountBalance.setText(accountBalance);
            ivAccountImage.setImageResource(accountImage);
            this.position = position;
            this.current = current;
        }

        //Assumes when the user clicks on an account, the only transition goes to Transactions
        @Override
        public void onClick(View v) {
            Log.d("Clicked", "at position " + position);
            Intent intent = new Intent(context, TransactionsActivity.class);

            //shared element (account) between the two activities
            Pair<View, String> pairs = new Pair<>(v, TAG_ACCOUNT_SHARED);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                    (Activity) context, pairs);

            intent.putExtra(TAG_ID, current.getId());
            intent.putExtra(TAG_ACCOUNT_FRIENDLY_NAME, current.getAccountFriendlyName());
            intent.putExtra(TAG_ACCOUNT_NUMBER, current.getAccountNumber());
            intent.putExtra(TAG_SORT_CODE, current.getSortCode());
            intent.putExtra(TAG_ACCOUNT_DETAILS, accountDetails);
            intent.putExtra(TAG_ACCOUNT_BALANCE, accountBalance);
            intent.putExtra(TAG_CUSTOMER_ID, current.getCustId());
            context.startActivity(intent, options.toBundle());
        }

        void setListeners(){
            itemView.setOnClickListener(MyViewHolder.this);
        }

        private String formatBalance(Double balance){
            DecimalFormat formatter = new DecimalFormat("#,##0.00");
            return formatter.format(balance);
        }

        private String formatSortCode(String sortCode){
            String formatted = "";
            if(sortCode.length() % 2 != 0){
                throw new IllegalArgumentException("Invalid Sort Code: "
                        + sortCode);
            }
            for(int i = 0; i < sortCode.length(); i+=2){
                formatted += sortCode.charAt(i);
                formatted += sortCode.charAt(i + 1);
                if(i+2 < sortCode.length()){
                    formatted += "\u2013";
                }
            }
            return formatted;
        }

        private String getCurrencySymbol(String currency){
            String symbol;
            switch(currency){
                case "GBP":
                    symbol = "£";
                    break;
                case "EUR":
                    symbol = "€";
                    break;
                case "CAD":
                case "USD":
                    symbol = "$";
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported currency: " + currency);
            }
            return symbol;
        }
    }
}
