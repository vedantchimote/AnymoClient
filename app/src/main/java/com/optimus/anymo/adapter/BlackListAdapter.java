package com.optimus.anymo.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.optimus.anymo.R;
import com.optimus.anymo.constants.Constants;
import com.optimus.anymo.model.BlackListItem;
import com.optimus.anymo.model.Chat;

import java.util.List;

public class BlackListAdapter extends RecyclerView.Adapter<BlackListAdapter.ViewHolder> implements Constants {

    private Context ctx;
    private List<BlackListItem> items;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {

        void onItemClick(View view, BlackListItem item, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {

        this.mOnItemClickListener = mItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView reason, time;
        public LinearLayout parent;

        public ViewHolder(View view) {

            super(view);

            parent = (LinearLayout) view.findViewById(R.id.parent);

            reason = (TextView) view.findViewById(R.id.title);
            time = (TextView) view.findViewById(R.id.time);
        }
    }

    public BlackListAdapter(Context mContext, List<BlackListItem> items) {

        this.ctx = mContext;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_black_list_row, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final BlackListItem item = items.get(position);

        holder.reason.setText(item.getReason());
        holder.time.setText(item.getTimeAgo());

        holder.parent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (mOnItemClickListener != null) {

                    mOnItemClickListener.onItemClick(v, items.get(position), position);
                }
            }
        });
    }

    public BlackListItem getItem(int position) {

        return items.get(position);
    }

    @Override
    public int getItemCount() {

        return items.size();
    }

    public interface OnClickListener {

        void onItemClick(View view, Chat item, int pos);
    }
}