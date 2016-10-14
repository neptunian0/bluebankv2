package com.lloydtucker.bluebankv2.helpers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lloydtucker.bluebankv2.R;
import com.lloydtucker.bluebankv2.pojos.Accounts;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;

/**
 * Created by lloydtucker on 14/10/2016.
 */

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.MyViewHolder> {
    private List<Accounts> mDataList;
    private LayoutInflater mInflater;

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
        View view = mInflater.inflate(R.layout.recycler_view_item, parent, false);
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
        @BindView(R.id.accountBalance)  TextView accountBalance;
        @BindView(R.id.accountDetails)  TextView accountDetails;
        @BindView(R.id.accountType)     TextView accountType;
        @BindView(R.id.accountImage)    ImageView accountImage;
        int position;
        Accounts current;

        MyViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setData(Accounts current, int position) {
            String accountType =    current.getAccountFriendlyName();
            String accountDetails = current.getAccountNumber() + "   |   " +
                                    formatSortCode(current.getSortCode());
            String accountBalance = getCurrencySymbol(current.getAccountCurrency()) +
                                    formatBalance(current.getAccountBalance());
            int accountImage =      getImage(accountType);

            this.accountType.setText(accountType);
            this.accountDetails.setText(accountDetails);
            this.accountBalance.setText(accountBalance);
            this.accountImage.setImageResource(accountImage);
            this.position = position;
            this.current = current;
        }

        @Override
        public void onClick(View v) {}

        void setListeners(){
//            imgDelete.setOnClickListener(MyViewHolder.this);
//            imgAdd.setOnClickListener(MyViewHolder.this);
//            imgThumb.setOnClickListener(MyViewHolder.this);
        }

        private int getImage(String accountType){
            int drawableSource;
            switch(accountType){
                case "Current Account":
                    drawableSource = R.mipmap.ic_payment_white_48dp;
                    break;
                case "Savings Account":
                    drawableSource = R.mipmap.ic_save_white_48dp;
                    break;
                case "John Smith - Current Account":
                    drawableSource = R.mipmap.ic_monetization_on_white_48dp;
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported account type: "
                            + accountType);
            }
            return drawableSource;
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
