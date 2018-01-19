package com.hcodestudio.smstimer.adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hcodestudio.smstimer.R;

import static com.hcodestudio.smstimer.data.TimerContract.TimerEntry.DATE;
import static com.hcodestudio.smstimer.data.TimerContract.TimerEntry.MESSAGE;
import static com.hcodestudio.smstimer.data.TimerContract.TimerEntry.NAME;
import static com.hcodestudio.smstimer.data.TimerContract.TimerEntry.PHONENO;
import static com.hcodestudio.smstimer.data.TimerContract.TimerEntry.TIME;

/**
 * Created by hassan on 8/14/2017.
 */

public class TimerAdapter extends RecyclerView.Adapter<TimerAdapter.TimerHolder> {

    // Class variables for the Cursor that holds task data and the Context
    private Cursor mCursor;
    private Context mContext;
    private  RecyclerViewClickListener itemListener;

    public TimerAdapter(Context mContext, RecyclerViewClickListener itemListener) {
        this.mContext = mContext;
        this.itemListener = itemListener;
    }

    public interface RecyclerViewClickListener
    {

        void recyclerViewListClicked(View v, int position);
    }


    @Override
    public TimerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.schedule_list_item, parent, false);

        return new TimerHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(TimerHolder holder, final int position) {

//        try {
        mCursor.moveToNext();

        String name = mCursor.getString(
                mCursor.getColumnIndex(NAME));

        String phone = mCursor.getString(
                        mCursor.getColumnIndex(PHONENO));

        String message = mCursor.getString(
                    mCursor.getColumnIndex(MESSAGE));

            String date = mCursor.getString(
                    mCursor.getColumnIndex(DATE));

        String time = mCursor.getString(
                mCursor.getColumnIndex(TIME));

            int pos = message.indexOf(20);
            if (pos != -1) {
                message = message.substring(0, pos) + " ...";
            }

            holder.tvName.setText(name + "(" + phone + ")");
            holder.tvMessage.setText(message);
            holder.tvDate.setText(date + " " + time);

    }


    @Override
    public int getItemCount() {

        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    public void swapCursor (Cursor c){
        if(c != null){
            mCursor = c;

            notifyDataSetChanged();
        }

    }


    public class TimerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvName, tvMessage;
        TextView tvDate;
        public TimerHolder(View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.namePhone);
            tvMessage = (TextView) itemView.findViewById(R.id.message);
            tvDate = (TextView) itemView.findViewById(R.id.date);
           itemView.setOnClickListener(this);

        }

       @Override
        public void onClick(View view) {
            itemListener.recyclerViewListClicked(view, getAdapterPosition());

        }

    }
}
