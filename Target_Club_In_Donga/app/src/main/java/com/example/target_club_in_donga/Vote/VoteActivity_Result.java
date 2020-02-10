package com.example.target_club_in_donga.Vote;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.target_club_in_donga.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

import static com.example.target_club_in_donga.MainActivity.clubName;
import static java.security.AccessController.getContext;

public class VoteActivity_Result extends AppCompatActivity {

    private String dbKey;
    private FirebaseDatabase database;
    private ArrayList<Vote_Item_Count> items;
    private VoteActivity_ResultListAdapter adapter;
    private ExpandableListView activityvote_result_listview;
    private TextView activityvote_result_textview_title;
    private TextView activityvote_result_textview_date;
    private TextView activityvote_result_textview_totalcount;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityvote_result);

        //activityvote_result_listview = (ListView)findViewById(R.id.activityvote_result_listview);
        activityvote_result_textview_title = findViewById(R.id.activityvote_result_textview_title);
        activityvote_result_textview_date = findViewById(R.id.activityvote_result_textview_date);
        activityvote_result_textview_totalcount = findViewById(R.id.activityvote_result_textview_totalcount);

        Intent intent = getIntent();
        dbKey = intent.getExtras().getString("key");

        database = FirebaseDatabase.getInstance();
        items = new ArrayList<>();

        database.getReference().child("EveryClub").child(clubName).child("Vote").child(dbKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Vote_Item last_item = dataSnapshot.getValue(Vote_Item.class); //큰거 하나!
                activityvote_result_listview = findViewById(R.id.activityvote_result_listview);
                //last_item.listItems 이게 그안에 이름 + 카운터수
                //starts가 투표한닝겐


                //Vector<ParentData> data = new Vector<>();
                List<Vote_Item_Count> data = new ArrayList<>();

                for(int i=0;i<last_item.listItems.size();i++){ //일단 부모 추가
                    final Vote_Item_Count parentData = new Vote_Item_Count();
                    parentData.setName(last_item.listItems.get(i).getName());
                    parentData.setCount(last_item.listItems.get(i).getCount());

                    for( String key : last_item.stars.keySet() ){ //그리고 그곳에 투표한 사용자 추가

                        if(last_item.stars.get(key) == i){ //uid가 같으면 이름받아오기

                            database.getReference().child("EveryClub").child(clubName).child("User").child(key).child("name").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String name = dataSnapshot.getValue(String.class);
                                    parentData.child.add(new Vote_Result_Child(name));
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    }
                    data.add(parentData);
                }


                adapter = new VoteActivity_ResultListAdapter(VoteActivity_Result.this,data);
                activityvote_result_listview.setAdapter(adapter);

                activityvote_result_textview_totalcount.setText("투표 수 : "+last_item.totalCount);

                activityvote_result_textview_title.setText(last_item.title);

                long unixTime = (long) last_item.timestamp;
                Date date = new Date(unixTime);
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                String time = simpleDateFormat.format(date);

                activityvote_result_textview_date.setText(time+" 까지");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public class VoteActivity_ResultListAdapter extends BaseExpandableListAdapter {

        private static final int PARENT_ROW = R.layout.vote_result_listview_item;
        private static final int CHILD_ROW = R.layout.vote_result_listview_item_child;

        private Context context;
        private List<Vote_Item_Count> data;
        private LayoutInflater inflater = null;

        public VoteActivity_ResultListAdapter(Context context, List<Vote_Item_Count> data){
            this.data = data;
            this.context = context;
            this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(PARENT_ROW, parent, false);
            }


            TextView itemName = convertView.findViewById(R.id.vote_result_listview_textview_name);
            TextView count = convertView.findViewById(R.id.vote_result_listview_textview_count);

            itemName.setText(data.get(groupPosition).getName());
            count.setText(data.get(groupPosition).getCount()+"");
            return convertView;
        }


        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(CHILD_ROW, parent, false);
            }

            TextView name = convertView.findViewById(R.id.vote_result_listview_textview_child_name);
            name.setText(data.get(groupPosition).child.get(childPosition).getName());

            return convertView;
        }

        @Override
        public int getGroupCount() {
            return data.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return data.get(groupPosition).child.size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return data.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return data.get(groupPosition).child.get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }
        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

}
