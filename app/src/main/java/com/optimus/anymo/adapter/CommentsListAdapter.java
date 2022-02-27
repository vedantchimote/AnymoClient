package com.optimus.anymo.adapter;

import android.content.Context;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import com.optimus.anymo.R;
import com.optimus.anymo.app.App;
import com.optimus.anymo.constants.Constants;
import com.optimus.anymo.model.Comment;
import com.vanniktech.emoji.EmojiTextView;

public class CommentsListAdapter extends RecyclerView.Adapter<CommentsListAdapter.ViewHolder> implements Constants {

    private List<Comment> items = new ArrayList<>();

    private Context context;

    ImageLoader imageLoader = App.getInstance().getImageLoader();

    private OnItemMenuButtonClickListener onItemMenuButtonClickListener;

    public interface OnItemMenuButtonClickListener {

        void onItemClick(View view, Comment obj, int actionId, int position);
    }

    public void setOnMoreButtonClickListener(final OnItemMenuButtonClickListener onItemMenuButtonClickListener) {

        this.onItemMenuButtonClickListener = onItemMenuButtonClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView mItemAuthorPhoto, mItemAuthorIcon;
        public ImageView mItemMenuButton;
        public EmojiTextView mItemDescription;
        public TextView mItemTimeAgo, mItemLocation, mItemAuthor;
        public LinearLayout mLocationContainer;

        public ViewHolder(View v) {

            super(v);

            mItemAuthorPhoto = (CircleImageView) v.findViewById(R.id.itemAuthorPhoto);
            mItemAuthorIcon = (CircleImageView) v.findViewById(R.id.itemAuthorIcon);

            mItemDescription = (EmojiTextView) v.findViewById(R.id.itemDescription);

            mLocationContainer = (LinearLayout) v.findViewById(R.id.item_location_container);
            mItemLocation = (TextView) v.findViewById(R.id.item_location_text);

            mItemMenuButton = (ImageView) v.findViewById(R.id.itemMenuButton);
            mItemTimeAgo = (TextView) v.findViewById(R.id.itemTimeAgo);

            mItemAuthor = (TextView) v.findViewById(R.id.itemAuthor);
        }

    }

    public CommentsListAdapter(Context ctx, List<Comment> items) {

        this.context = ctx;
        this.items = items;

        if (imageLoader == null) {

            imageLoader = App.getInstance().getImageLoader();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_list_row, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final Comment p = items.get(position);

        holder.mItemAuthorIcon.setVisibility(View.GONE);

        holder.mItemAuthorPhoto.setVisibility(View.VISIBLE);

        holder.mItemAuthorPhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //
            }
        });

        if (p.getItemFromUserId() == p.getFromUserId()) {

            holder.mItemAuthorPhoto.setImageResource(R.drawable.ic_crown);

        } else {

            if (p.getIconUrl().length() != 0) {

                holder.mItemAuthorPhoto.setColorFilter(Color.parseColor(p.getColor()));

                imageLoader.get(p.getIconUrl(), ImageLoader.getImageListener(holder.mItemAuthorPhoto, R.drawable.profile_default_photo, R.drawable.profile_default_photo));

            } else {

                holder.mItemAuthorPhoto.setVisibility(View.VISIBLE);
                holder.mItemAuthorPhoto.setImageResource(R.drawable.profile_default_photo);
            }
        }

        //

        if (p.getText().length() != 0) {

            holder.mItemDescription.setVisibility(View.VISIBLE);
            holder.mItemDescription.setText(p.getText().replaceAll("<br>", "\n"));

        } else {

            holder.mItemDescription.setVisibility(View.GONE);
        }

        // Author title

        if (p.getItemFromUserId() == p.getFromUserId()) {

            holder.mItemAuthor.setText("Author");

        } else {

            holder.mItemAuthor.setText(p.getColorName() + " " + p.getIconName());
        }

        // Author badge

        if (p.getItemFromUserId() == p.getFromUserId()) {

            holder.mItemAuthorIcon.setVisibility(View.VISIBLE);

        } else {

            holder.mItemAuthorIcon.setVisibility(View.GONE);
        }

        // Time Ago

        holder.mItemTimeAgo.setVisibility(View.VISIBLE);
        holder.mItemTimeAgo.setText(p.getTimeAgo());

        // Location

        holder.mLocationContainer.setVisibility(View.GONE);

        if (getLocation(p).length() != 0) {

            holder.mLocationContainer.setVisibility(View.VISIBLE);
            holder.mItemLocation.setText(getLocation(p));
        }

        holder.mItemMenuButton.setVisibility(View.GONE);

        holder.mItemMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                onItemMenuButtonClick(view, p, position);
            }
        });
    }

    private void onItemMenuButtonClick(final View view, final Comment comment, final int position){

        PopupMenu popupMenu = new PopupMenu(context, view);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                onItemMenuButtonClickListener.onItemClick(view, comment, item.getItemId(), position);

                return true;
            }
        });

//        if (comment.getFromUserId() == App.getInstance().getAccount().getId()) {
//
//            // only delete option
//            popupMenu.inflate(R.menu.menu_comment_popup_1);
//
//        } else {
//
//            // reply option
//            popupMenu.inflate(R.menu.menu_comment_popup_3);
//        }
//
//        popupMenu.show();
    }

    private String getLocation(Comment item) {

        String location = "";

        if (item.getCountry().length() > 0) {

            location = item.getCountry();
        }

        if (item.getCity().length() > 0) {

            location = location + ", " + item.getCity();

        } else {

            if (item.getArea().length() != 0) {

                location = location + ", " + item.getArea();
            }
        }

        return location;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}