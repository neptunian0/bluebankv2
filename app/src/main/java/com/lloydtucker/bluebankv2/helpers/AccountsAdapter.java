package com.lloydtucker.bluebankv2.helpers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lloydtucker.bluebankv2.R;
import com.lloydtucker.bluebankv2.pojos.Accounts;

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
        View view = mInflater.inflate(R.layout.scroll_view_item, parent, false);
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
        @BindView(R.id.accountHeader) TextView accountHeader;
        @BindView(R.id.accountBody) TextView accountBody;
        int position;
        Accounts current;

        void setData(Accounts current, int position) {
            this.accountHeader.setText(current.getAccountFriendlyName());
            this.accountBody.setText(current.getAccountNumber());
            this.position = position;
            this.current = current;
        }

        MyViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v) {

        }

        void setListeners(){
//            imgDelete.setOnClickListener(MyViewHolder.this);
//            imgAdd.setOnClickListener(MyViewHolder.this);
//            imgThumb.setOnClickListener(MyViewHolder.this);
        }
    }
}
