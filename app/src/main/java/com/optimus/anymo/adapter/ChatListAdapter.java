package com.optimus.anymo.adapter;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;

import java.util.List;

import com.optimus.anymo.PhotoViewActivity;
import com.optimus.anymo.R;
import com.optimus.anymo.app.App;
import com.optimus.anymo.constants.Constants;
import com.optimus.anymo.model.ChatItem;
import com.optimus.anymo.view.ResizableImageView;
import com.vanniktech.emoji.EmojiTextView;

public class ChatListAdapter extends BaseAdapter implements Constants {

	private Activity activity;
	private LayoutInflater inflater;
	private List<ChatItem> dialogList;

    ImageLoader imageLoader = App.getInstance().getImageLoader();

	public ChatListAdapter(Activity activity, List<ChatItem> dialogList) {

		this.activity = activity;
		this.dialogList = dialogList;
	}

	@Override
	public int getCount() {

		return dialogList.size();
	}

	@Override
	public Object getItem(int location) {

		return dialogList.get(location);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}
	
	static class ViewHolder {

        public ResizableImageView mLeft_Img;
        public ResizableImageView mRight_Img;

        public TextView mLeft_TimeAgo;
        public EmojiTextView mLeft_Message;

        public TextView mRight_TimeAgo;
        public EmojiTextView mRight_Message;

        public LinearLayout mLeftItem;
        public LinearLayout mRightItem;

        public ImageView mSeenIcon;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder = null;

		final ChatItem chatItem = dialogList.get(position);

		if (inflater == null) {

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

		if (convertView == null) {

            convertView = inflater.inflate(R.layout.chat_left_item_list_row, null);
			
			viewHolder = new ViewHolder();

            viewHolder.mLeft_Img = (ResizableImageView) convertView.findViewById(R.id.left_img);
            viewHolder.mRight_Img = (ResizableImageView) convertView.findViewById(R.id.right_img);

            viewHolder.mLeftItem = (LinearLayout) convertView.findViewById(R.id.leftItem);
            viewHolder.mRightItem = (LinearLayout) convertView.findViewById(R.id.rightItem);

            viewHolder.mLeft_Message = (EmojiTextView) convertView.findViewById(R.id.left_message);
			viewHolder.mLeft_TimeAgo = (TextView) convertView.findViewById(R.id.left_timeAgo);

            viewHolder.mRight_Message = (EmojiTextView) convertView.findViewById(R.id.right_message);
            viewHolder.mRight_TimeAgo = (TextView) convertView.findViewById(R.id.right_timeAgo);

            viewHolder.mSeenIcon = (ImageView) convertView.findViewById(R.id.seenIcon);

            convertView.setTag(viewHolder);

		} else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}

        if (imageLoader == null) {

            imageLoader = App.getInstance().getImageLoader();
        }

        viewHolder.mRight_Img.setTag(position);
        viewHolder.mLeft_Img.setTag(position);

        viewHolder.mLeft_TimeAgo.setTag(position);
        viewHolder.mLeft_Message.setTag(position);

        viewHolder.mRight_TimeAgo.setTag(position);
        viewHolder.mRight_Message.setTag(position);

        viewHolder.mLeftItem.setTag(position);
        viewHolder.mRightItem.setTag(position);

        viewHolder.mSeenIcon.setTag(position);

        if (App.getInstance().getAccount().getId() == chatItem.getFromUserId()) {

            viewHolder.mLeftItem.setVisibility(View.GONE);

            viewHolder.mRightItem.setVisibility(View.VISIBLE);

            if (chatItem.getStickerId() != 0) {

                viewHolder.mRight_Img.getLayoutParams().width = 256;
                viewHolder.mRight_Img.requestLayout();

                viewHolder.mRight_Img.setOnClickListener(null);

                imageLoader.get(chatItem.getStickerImgUrl(), ImageLoader.getImageListener(viewHolder.mRight_Img, R.drawable.img_loading, R.drawable.img_loading));
                viewHolder.mRight_Img.setVisibility(View.VISIBLE);

            } else {

                if (chatItem.getImgUrl().length() != 0) {

                    viewHolder.mRight_Img.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                    viewHolder.mRight_Img.requestLayout();

                    viewHolder.mRight_Img.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            Intent i = new Intent(activity, PhotoViewActivity.class);
                            i.putExtra("imgUrl", chatItem.getImgUrl());
                            activity.startActivity(i);
                        }
                    });

                    imageLoader.get(chatItem.getImgUrl(), ImageLoader.getImageListener(viewHolder.mRight_Img, R.drawable.img_loading, R.drawable.img_loading));
                    viewHolder.mRight_Img.setVisibility(View.VISIBLE);

                } else {

                    viewHolder.mRight_Img.setVisibility(View.GONE);
                }
            }

            if (chatItem.getMessage().length() > 0) {

                viewHolder.mRight_Message.setVisibility(View.VISIBLE);

            } else {

                viewHolder.mRight_Message.setVisibility(View.GONE);
            }

            viewHolder.mRight_Message.setText(chatItem.getMessage().replaceAll("<br>", "\n"));

            if (chatItem.getSeenAt() > 0) {

                viewHolder.mSeenIcon.setVisibility(View.VISIBLE);

            } else {

                viewHolder.mSeenIcon.setVisibility(View.GONE);
            }

            viewHolder.mRight_TimeAgo.setText(chatItem.getTimeAgo());

            viewHolder.mRight_Message.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {

                    ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("msg", chatItem.getMessage().replaceAll("<br>", "\n"));
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(activity, activity.getString(R.string.msg_copied_to_clipboard), Toast.LENGTH_SHORT).show();

                    return false;
                }
            });

        } else {

            viewHolder.mRightItem.setVisibility(View.GONE);

            viewHolder.mLeftItem.setVisibility(View.VISIBLE);

            if (chatItem.getImgUrl().length() != 0) {

                if (chatItem.getStickerId() == 0) {

                    viewHolder.mRight_Img.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                    viewHolder.mRight_Img.requestLayout();

                    viewHolder.mLeft_Img.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            Intent i = new Intent(activity, PhotoViewActivity.class);
                            i.putExtra("imgUrl", chatItem.getImgUrl());
                            activity.startActivity(i);
                        }
                    });

                } else {

                    viewHolder.mRight_Img.setOnClickListener(null);

                    viewHolder.mLeft_Img.getLayoutParams().width = 256;
                    viewHolder.mLeft_Img.requestLayout();
                }

                imageLoader.get(chatItem.getImgUrl(), ImageLoader.getImageListener(viewHolder.mLeft_Img, R.drawable.img_loading, R.drawable.img_loading));
                viewHolder.mLeft_Img.setVisibility(View.VISIBLE);

            } else {

                viewHolder.mLeft_Img.setVisibility(View.GONE);
            }

            if (chatItem.getMessage().length() > 0) {

                viewHolder.mLeft_Message.setVisibility(View.VISIBLE);

            } else {

                viewHolder.mLeft_Message.setVisibility(View.GONE);
            }

            viewHolder.mLeft_Message.setText(chatItem.getMessage().replaceAll("<br>", "\n"));
            viewHolder.mLeft_TimeAgo.setText(chatItem.getTimeAgo());

            viewHolder.mLeft_Message.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {

                    ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("msg", chatItem.getMessage().replaceAll("<br>", "\n"));
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(activity, activity.getString(R.string.msg_copied_to_clipboard), Toast.LENGTH_SHORT).show();

                    return false;
                }
            });
        }

		return convertView;
	}
}