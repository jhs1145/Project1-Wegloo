package com.example.target_club_in_donga.club_foundation_join;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.target_club_in_donga.PushMessages.SendPushMessages;
import com.example.target_club_in_donga.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import static com.example.target_club_in_donga.MainActivity.clubName;
import static com.example.target_club_in_donga.home_viewpager.HomeFragment0.thisClubName;

public class Accept_request_expandAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private Activity activity;
    public static final int HEADER = 0;
    public static final int CHILD = 1;

    private List<Item> data;

    public Accept_request_expandAdapter(Activity activity, List<Item> data) {
        this.data = data;
        this.activity = activity;
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View view = null;
        Context context = parent.getContext();
        float dp = context.getResources().getDisplayMetrics().density;
        int subItemPaddingLeft = (int) (18 * dp);
        int subItemPaddingTopAndBottom = (int) (5 * dp);
        switch (type) {
            case HEADER:
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.item_accept_request, parent, false);
                ListHeaderViewHolder header = new ListHeaderViewHolder(view);
                return header;
            case CHILD:
                TextView itemTextView = new TextView(context);
                itemTextView.setPadding(subItemPaddingLeft, subItemPaddingTopAndBottom, 0, subItemPaddingTopAndBottom);
                itemTextView.setTextColor(0x88000000);
                itemTextView.setBackgroundColor(ContextCompat.getColor(context,R.color.colorWhite));
                itemTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                return new RecyclerView.ViewHolder(itemTextView) {
                };
        }
        return null;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Item item = data.get(position);
        switch (item.type) {
            case HEADER:
                final ListHeaderViewHolder itemController = (ListHeaderViewHolder) holder;

                itemController.refferalItem = item;
                itemController.item_request_name.setText(item.nameNic);
                itemController.item_request_timestamp.setText(item.applicationDate);
                if(!item.imageUrl.equals("None")){
                    Glide.with(activity).load(item.imageUrl).into(itemController.item_request_imageview);
                }

                if (item.invisibleChildren == null) {
                    itemController.item_request_toggle.setImageResource(R.drawable.ic_expand_less_24px);
                } else {
                    itemController.item_request_toggle.setImageResource(R.drawable.ic_expand_more_24px);
                }
                itemController.item_request_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.invisibleChildren == null) {
                            item.invisibleChildren = new ArrayList<Item>();
                            int count = 0;
                            int pos = data.indexOf(itemController.refferalItem);
                            while (data.size() > pos + 1 && data.get(pos + 1).type == CHILD) {
                                item.invisibleChildren.add(data.remove(pos + 1));
                                count++;
                            }
                            notifyItemRangeRemoved(pos + 1, count);
                            itemController.item_request_toggle.setImageResource(R.drawable.ic_expand_more_24px);
                        } else {
                            int pos = data.indexOf(itemController.refferalItem);
                            int index = pos + 1;
                            for (Item i : item.invisibleChildren) {
                                data.add(index, i);
                                index++;
                            }
                            notifyItemRangeInserted(pos + 1, index - pos - 1);
                            itemController.item_request_toggle.setImageResource(R.drawable.ic_expand_less_24px);
                            item.invisibleChildren = null;
                        }
                    }
                });

                itemController.item_request_false.show(false);
                itemController.item_request_true.show(false);

                itemController.item_request_true.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        firebaseDatabase.getReference().child("AppUser").child(item.uid).child("signUpClub").child(clubName)
                                .child("approvalCompleted").setValue(true);
                        //itemController.item_request_layout.setVisibility(View.INVISIBLE);
                        item.visCheck = true;
                        /**
                         * FCM 보내줘야해
                         */
                        SendPushMessages send = new SendPushMessages();
                        send.sendFcm(item.pushToken, thisClubName, "가입요청이 승인되었습니다.","AcceptRequest", clubName);
                        //notifyDataSetChanged();
                        //Log.e("uid",item.uid);
                    }
                });
                itemController.item_request_false.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        firebaseDatabase.getReference().child("AppUser").child(item.uid).child("signUpClub").child(clubName).removeValue();
                        item.visCheck = true;
                        /**
                         * 거절당했을땐 슬프니까 보내주지말자
                         */
                    }
                });
                if(item.visCheck){
                    itemController.item_request_layout.setVisibility(View.GONE);
                    notifyDataSetChanged();
                }

                break;
            case CHILD:
                TextView itemTextView = (TextView) holder.itemView;
                itemTextView.setText("자기소개 : \n"+data.get(position).resume);
                break;
        }
    }
    @Override
    public int getItemViewType(int position) {
        return data.get(position).type;
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    private static class ListHeaderViewHolder extends RecyclerView.ViewHolder {
//        public TextView header_title;
//        public TextView header_writer;
//        public TextView header_timedate;
//        public ImageView btn_expand_toggle;
//        public Item refferalItem;
//        public LinearLayout linearLayout;
        public ImageView item_request_imageview;
        public TextView item_request_name;
        public TextView item_request_timestamp;
        public FloatingActionButton item_request_true;
        public FloatingActionButton item_request_false;
        public LinearLayout item_request_layout;
        public ImageView item_request_toggle;
        public Item refferalItem;

        public ListHeaderViewHolder(View itemView) {
            super(itemView);
            item_request_imageview = itemView.findViewById(R.id.item_request_imageview);
            item_request_name = itemView.findViewById(R.id.item_request_name);
            item_request_timestamp = itemView.findViewById(R.id.item_request_timestamp);
            item_request_true = itemView.findViewById(R.id.item_request_true);
            item_request_false = itemView.findViewById(R.id.item_request_false);
            item_request_layout = itemView.findViewById(R.id.item_request_layout);
            item_request_toggle = itemView.findViewById(R.id.item_request_toggle);
        }
    }

    public static class Item {
        public int type;

//        public SpannableStringBuilder title;
//        public String content;
//        public String writer;
//        public String timedate;
        public String nameNic;
        public String applicationDate;
        public String resume;
        public String imageUrl;
        public String uid;
        public String pushToken;
        public boolean visCheck;

        public List<Item> invisibleChildren;

        public Item(int type, String uid, String nameNic, String applicationDate,String imageUrl, String resume, boolean visCheck, String pushToken) {
            this.type = type;
            this.nameNic = nameNic;
            this.applicationDate = applicationDate;
            this.resume = resume;
            this.imageUrl = imageUrl;
            this.uid = uid;
            this.visCheck = visCheck;
            this.pushToken = pushToken;
        }
    }
}
