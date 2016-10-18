package com.lloydtucker.bluebankv2.helpers;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lloydtucker.bluebankv2.R;
import com.lloydtucker.bluebankv2.pojos.Transactions;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;

/**
 * Created by lloydtucker on 14/10/2016.
 */

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.MyViewHolder> {
    private List<Transactions> mDataList;
    private LayoutInflater mInflater;
    private Context context;

    public TransactionsAdapter(Context context, List<Transactions> data){
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
        View view = mInflater.inflate(R.layout.recycler_view_transaction_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder " + position);

        Transactions currentObj = mDataList.get(position);
        holder.setData(currentObj, position); //important for onClickListeners and buttons
        holder.setListeners();
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.transactionDate)         TextView txvTransactionDate;
        @BindView(R.id.transactionDescription)  TextView txvTransactionDescription;
        @BindView(R.id.transactionAmount)       TextView txvTransactionAmount;
        @BindView(R.id.transactionImage)        ImageView ivTransactionImage;
        int position;
        Transactions current;

        MyViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
        }

        void setData(Transactions current, int position) {
            String transactionDate = null;
            try {
                transactionDate = formatDate(current.getTransactionDateTime());
            } catch (ParseException e) {
                e.printStackTrace();
                Log.d("ParseException", "Error while parsing transaction date in " +
                        "TransactionsAdapter");
            }
            String transactionDescription = current.getTransactionDescription();
            double transactionAmount = current.getTransactionAmount();
            String formatTransactionAmount = formatAmount(transactionAmount);
            int transactionImage = getImage(transactionAmount); //based on transaction amount

            txvTransactionDate.setText(transactionDate);
            txvTransactionDescription.setText(transactionDescription);
            txvTransactionAmount.setText(formatTransactionAmount);
            ivTransactionImage.setImageResource(transactionImage);
            this.position = position;
            this.current = current;
        }

        //Assumes when the user clicks on an account, the only transition goes to Transactions
        @Override
        public void onClick(View v) {
            Log.d("Clicked", "at position " + position);
//            Intent intent = new Intent(context, TransactionsActivity.class);
//
//            //shared element (account) between the two activities
//            Pair<View, String> pairs = new Pair<>(v, TAG_ACCOUNT_SHARED);
//            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
//                    (Activity) context, pairs);
//
//            intent.putExtra(TAG_ID, current.getId());
//            intent.putExtra(TAG_ACCOUNT_FRIENDLY_NAME, current.getAccountFriendlyName());
//            intent.putExtra(TAG_ACCOUNT_NUMBER, current.getAccountNumber());
//            intent.putExtra(TAG_SORT_CODE, current.getSortCode());
//            intent.putExtra(TAG_ACCOUNT_DETAILS, accountDetails);
//            intent.putExtra(TAG_ACCOUNT_BALANCE, accountBalance);
//            intent.putExtra(TAG_CUSTOMER_ID, current.getCustId());
//            context.startActivity(intent, options.toBundle());
        }

        void setListeners(){
            itemView.setOnClickListener(MyViewHolder.this);
        }

        private String formatAmount(Double d){
            DecimalFormat formatter = new DecimalFormat("#,##0.00");
            if(d < 0){
                return "-" + formatter.format(d).replace("-", "£");
            }
            return "£" + formatter.format(d);
        }

        private String formatDate(String inputDate) throws ParseException {
            //TODO: Optimise this to use locale instead of being explicit
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(inputDate);
            return new SimpleDateFormat("dd MMM yyyy").format(date);
        }

        private int getImage(double amount){
            int imageSrc;
            //money paid out
            if(amount < 0){
                imageSrc = R.drawable.ic_export;
                setTransactionRed();
            }
            //money paid in
            else{
                imageSrc = R.drawable.ic_import;
            }
            return imageSrc;
        }

        private void setTransactionRed(){
            int red = ContextCompat.getColor(context, R.color.RBSCaledonianCrimson);
            txvTransactionAmount.setTextColor(red);
            txvTransactionDescription.setTextColor(red);
            txvTransactionDate.setTextColor(red);
            ivTransactionImage.setColorFilter(red);
        }
    }
}
