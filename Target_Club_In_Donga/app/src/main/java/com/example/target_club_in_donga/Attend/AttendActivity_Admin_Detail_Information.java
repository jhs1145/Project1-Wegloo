package com.example.target_club_in_donga.Attend;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.target_club_in_donga.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.target_club_in_donga.MainActivity.clubName;

public class AttendActivity_Admin_Detail_Information extends AppCompatActivity {
    private Button activity_attend_detail_button_attend_state;
    private TextView activity_attend_detail_textview_attend, activity_attend_detail_textview;

    private PieChart activity_attend_piechart;
    private FirebaseDatabase database;
    private FirebaseAuth auth;

    private String userName, userPhone, userId, EditTardyTime;
    private int attendCount = 0, tardyCount = 0, unsentCount = 0, absentCount = 0, listSize = 0, menu_count = 0;

    private RecyclerView activity_attend_detail_recyclerview_main_list;
    private List<Attend_Information_Item> attendAdminItems = new ArrayList<>();
    private List<String> uidLists = new ArrayList<>();

    private ArrayList<String> listStartTime = new ArrayList<>();

    private String startTime, userFlag;

    private SlidingDrawer activity_attend_detail_slidingdrawer;

    final int[] MY_COLORS = {Color.rgb(89, 218, 80), Color.rgb(255, 187, 0), Color.rgb(189, 189, 189), Color.rgb(200, 0, 0)};
    ArrayList<Integer> colors = new ArrayList<Integer>();

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend_detail);

        Intent intent = getIntent();
        userName = intent.getExtras().getString("userName");
        userPhone = intent.getExtras().getString("userPhone");

        activity_attend_detail_button_attend_state = (Button) findViewById(R.id.activity_attend_detail_button_attend_state);
        activity_attend_detail_textview_attend = (TextView) findViewById(R.id.activity_attend_detail_textview_attend);
        activity_attend_detail_slidingdrawer = (SlidingDrawer) findViewById(R.id.activity_attend_detail_slidingdrawer);
        activity_attend_detail_textview = (TextView) findViewById(R.id.activity_attend_detail_textview);

        activity_attend_piechart = (PieChart) findViewById(R.id.activity_attend_piechart);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        activity_attend_detail_slidingdrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                menu_count++;
            }
        });

        activity_attend_detail_slidingdrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                menu_count--;
            }
        });

        activity_attend_detail_recyclerview_main_list = (RecyclerView) findViewById(R.id.activity_attend_detail_recyclerview_main_list);
        activity_attend_detail_recyclerview_main_list.setLayoutManager(new LinearLayoutManager(AttendActivity_Admin_Detail_Information.this));

        final AttendActivity_Admin_Detail_Information.AttendAdminDetailInformationActivity_RecyclerViewAdapter attendAdminDetailInformationActivity_recyclerViewAdapter = new AttendActivity_Admin_Detail_Information.AttendAdminDetailInformationActivity_RecyclerViewAdapter();

        activity_attend_detail_recyclerview_main_list.setAdapter(attendAdminDetailInformationActivity_recyclerViewAdapter);
        attendAdminDetailInformationActivity_recyclerViewAdapter.notifyDataSetChanged();

        database.getReference().child("EveryClub").child(clubName).child("realNameSystem").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue().toString().equals("true")) {
                    database.getReference().child("AppUser").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (snapshot.getValue() != null) {
                                    if (snapshot.child("name").getValue().equals(userName) && snapshot.child("phone").getValue().equals(userPhone)) {
                                        userId = snapshot.getKey();

                                        activity_attend_piechart.setUsePercentValues(true);
                                        activity_attend_piechart.getDescription().setEnabled(true);
                                        activity_attend_piechart.setExtraOffsets(5, 10, 5, 5);

                                        activity_attend_piechart.setDragDecelerationFrictionCoef(0.95f);

                                        activity_attend_piechart.setDrawHoleEnabled(true);
                                        activity_attend_piechart.setHoleColor(Color.WHITE);
                                        activity_attend_piechart.setTransparentCircleRadius(61f);

                                        final ArrayList<PieEntry> pieEntries = new ArrayList<>();

                                        database.getReference().child("EveryClub").child(clubName).child("Attend").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(final DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.getValue() != null) {
                                                    attendCount = 0;
                                                    tardyCount = 0;
                                                    unsentCount = 0;
                                                    absentCount = 0;
                                                    pieEntries.clear();
                                                    colors.clear();
                                                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                        if (snapshot.child("User_State").child(userId).child("attend_state").getValue(String.class) != null) {
                                                            if (snapshot.child("User_State").child(userId).child("attend_state").getValue(String.class).equals("출석")) {
                                                                attendCount++;
                                                            } else if (snapshot.child("User_State").child(userId).child("attend_state").getValue(String.class).equals("지각")) {
                                                                tardyCount++;
                                                            } else if (snapshot.child("User_State").child(userId).child("attend_state").getValue(String.class).equals("미출결")) {
                                                                unsentCount++;
                                                            } else {
                                                                absentCount++;
                                                            }
                                                        }
                                                    }

                                                    if (attendCount > 0) {
                                                        pieEntries.add(new PieEntry(attendCount, "출석"));
                                                        colors.add(MY_COLORS[0]);
                                                    }
                                                    if (tardyCount > 0) {
                                                        pieEntries.add(new PieEntry(tardyCount, "지각"));
                                                        colors.add(MY_COLORS[1]);
                                                    }
                                                    if (unsentCount > 0) {
                                                        pieEntries.add(new PieEntry(unsentCount, "미출결"));
                                                        colors.add(MY_COLORS[2]);
                                                    }
                                                    if (absentCount > 0) {
                                                        pieEntries.add(new PieEntry(absentCount, "결석"));
                                                        colors.add(MY_COLORS[3]);
                                                    }

                                                    Description description = new Description();
                                                    description.setText("출석률");
                                                    description.setTextSize(30f);
                                                    activity_attend_piechart.setDescription(description);

                                                    activity_attend_piechart.animateY(1000, Easing.EasingOption.EaseInOutCubic);
                                                    activity_attend_piechart.setRotationEnabled(false);

                                                    PieDataSet pieDataSet = new PieDataSet(pieEntries, "%");
                                                    pieDataSet.setSliceSpace(3f);
                                                    pieDataSet.setSelectionShift(4f);
//                                            pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
                                                    pieDataSet.setColors(colors);

                                                    PieData pieData = new PieData((pieDataSet));
                                                    pieData.setValueTextSize(20f);
                                                    pieData.setValueTextColor(Color.YELLOW);

                                                    activity_attend_piechart.setData(pieData);
                                                }

                                            }


                                            @Override
                                            public void onCancelled(final DatabaseError databaseError) {

                                            }
                                        });

                                        database.getReference().child("EveryClub").child(clubName).child("Attend").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(final DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.getValue() != null) {
                                                    attendAdminItems.clear();
                                                    uidLists.clear();
                                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                        if (snapshot.child("User_State").child(userId).child("attend_state").getValue(String.class) != null) {
                                                            if (snapshot.child("User_State").child(userId).child("attend_state").getValue().equals("출석")) {
                                                                Attend_Information_Item attendAdminChangeItem = snapshot.getValue(Attend_Information_Item.class);
                                                                attendAdminChangeItem.attend_state = snapshot.child("User_State").child(userId).child("attend_state").getValue().toString();
                                                                attendAdminChangeItem.attendTimeLimit = snapshot.child("startTime").getValue().toString();
                                                                String uidKey = snapshot.getKey();
                                                                attendAdminItems.add(0, attendAdminChangeItem);
                                                                uidLists.add(0, uidKey);
                                                            }
                                                            attendAdminDetailInformationActivity_recyclerViewAdapter.notifyDataSetChanged();
                                                        }
                                                    }

                                                    if (attendAdminItems.size() == 0) {
                                                        activity_attend_detail_textview.setText("현황이 없습니다");
                                                        activity_attend_detail_textview.setVisibility(View.VISIBLE);
                                                        activity_attend_detail_slidingdrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
                                                            @Override
                                                            public void onDrawerOpened() {
                                                                menu_count++;
                                                            }
                                                        });
                                                    } else {
                                                        activity_attend_detail_textview.setVisibility(View.GONE);
                                                    }
                                                }

                                            }

                                            @Override
                                            public void onCancelled(final DatabaseError databaseError) {

                                            }
                                        });

                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(final DatabaseError databaseError) {

                        }
                    });

                } else {
                    database.getReference().child("EveryClub").child(clubName).child("User").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {
                            for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (snapshot.child("name").getValue() != null && snapshot.child("name").getValue().equals(userName)) {
                                    userId = snapshot.getKey();

                                    activity_attend_piechart.setUsePercentValues(true);
                                    activity_attend_piechart.getDescription().setEnabled(true);
                                    activity_attend_piechart.setExtraOffsets(5, 10, 5, 5);

                                    activity_attend_piechart.setDragDecelerationFrictionCoef(0.95f);

                                    activity_attend_piechart.setDrawHoleEnabled(true);
                                    activity_attend_piechart.setHoleColor(Color.WHITE);
                                    activity_attend_piechart.setTransparentCircleRadius(61f);

                                    final ArrayList<PieEntry> pieEntries = new ArrayList<>();

                                    database.getReference().child("EveryClub").child(clubName).child("Attend").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(final DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getValue() != null) {
                                                attendCount = 0;
                                                tardyCount = 0;
                                                unsentCount = 0;
                                                absentCount = 0;
                                                pieEntries.clear();
                                                colors.clear();
                                                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                    if (snapshot.child("User_State").child(userId).child("attend_state").getValue(String.class) != null) {
                                                        if (snapshot.child("User_State").child(userId).child("attend_state").getValue(String.class).equals("출석")) {
                                                            attendCount++;
                                                        } else if (snapshot.child("User_State").child(userId).child("attend_state").getValue(String.class).equals("지각")) {
                                                            tardyCount++;
                                                        } else if (snapshot.child("User_State").child(userId).child("attend_state").getValue(String.class).equals("미출결")) {
                                                            unsentCount++;
                                                        } else {
                                                            absentCount++;
                                                        }
                                                    }
                                                }

                                                if (attendCount > 0) {
                                                    pieEntries.add(new PieEntry(attendCount, "출석"));
                                                    colors.add(MY_COLORS[0]);
                                                }
                                                if (tardyCount > 0) {
                                                    pieEntries.add(new PieEntry(tardyCount, "지각"));
                                                    colors.add(MY_COLORS[1]);
                                                }
                                                if (unsentCount > 0) {
                                                    pieEntries.add(new PieEntry(unsentCount, "미출결"));
                                                    colors.add(MY_COLORS[2]);
                                                }
                                                if (absentCount > 0) {
                                                    pieEntries.add(new PieEntry(absentCount, "결석"));
                                                    colors.add(MY_COLORS[3]);
                                                }

                                                Description description = new Description();
                                                description.setText("출석률");
                                                description.setTextSize(30f);
                                                activity_attend_piechart.setDescription(description);

                                                activity_attend_piechart.animateY(1000, Easing.EasingOption.EaseInOutCubic);
                                                activity_attend_piechart.setRotationEnabled(false);

                                                PieDataSet pieDataSet = new PieDataSet(pieEntries, "%");
                                                pieDataSet.setSliceSpace(3f);
                                                pieDataSet.setSelectionShift(4f);
//                                            pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
                                                pieDataSet.setColors(colors);

                                                PieData pieData = new PieData((pieDataSet));
                                                pieData.setValueTextSize(20f);
                                                pieData.setValueTextColor(Color.YELLOW);

                                                activity_attend_piechart.setData(pieData);
                                            }

                                        }

                                        @Override
                                        public void onCancelled(final DatabaseError databaseError) {

                                        }
                                    });

                                    database.getReference().child("EveryClub").child(clubName).child("Attend").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(final DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getValue() != null) {
                                                attendAdminItems.clear();
                                                uidLists.clear();
                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                    if (snapshot.child("User_State").child(userId).child("attend_state").getValue(String.class) != null) {
                                                        if (snapshot.child("User_State").child(userId).child("attend_state").getValue().equals("출석")) {
                                                            Attend_Information_Item attendAdminChangeItem = snapshot.getValue(Attend_Information_Item.class);
                                                            attendAdminChangeItem.attend_state = snapshot.child("User_State").child(userId).child("attend_state").getValue().toString();
                                                            attendAdminChangeItem.attendTimeLimit = snapshot.child("startTime").getValue().toString();
                                                            String uidKey = snapshot.getKey();
                                                            attendAdminItems.add(0, attendAdminChangeItem);
                                                            uidLists.add(0, uidKey);
                                                        }
                                                        attendAdminDetailInformationActivity_recyclerViewAdapter.notifyDataSetChanged();
                                                    }
                                                }

                                                if (attendAdminItems.size() == 0) {
                                                    activity_attend_detail_textview.setText("현황이 없습니다");
                                                    activity_attend_detail_textview.setVisibility(View.VISIBLE);
                                                    activity_attend_detail_slidingdrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
                                                        @Override
                                                        public void onDrawerOpened() {
                                                            menu_count++;
                                                        }
                                                    });
                                                } else {
                                                    activity_attend_detail_textview.setVisibility(View.GONE);
                                                }
                                            }

                                        }

                                        @Override
                                        public void onCancelled(final DatabaseError databaseError) {

                                        }
                                    });

                                }
                            }

                        }

                        @Override
                        public void onCancelled(final DatabaseError databaseError) {

                        }

                    });
                }
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {

            }
        });

        activity_attend_detail_button_attend_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final PopupMenu popup = new PopupMenu(AttendActivity_Admin_Detail_Information.this, v);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.attend_state_information_attend:

                                activity_attend_detail_textview_attend.setText("출석 현황");

                                database.getReference().child("EveryClub").child(clubName).child("Attend").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(final DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue() != null) {
                                            attendAdminItems.clear();
                                            uidLists.clear();
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                if (snapshot.child("User_State").child(userId).child("attend_state").getValue() != null && snapshot.child("User_State").child(userId).child("attend_state").getValue().equals("출석")) {
                                                    Attend_Information_Item attendAdminChangeItem = snapshot.getValue(Attend_Information_Item.class);
                                                    attendAdminChangeItem.attend_state = snapshot.child("User_State").child(userId).child("attend_state").getValue().toString();
                                                    attendAdminChangeItem.attendTimeLimit = snapshot.child("startTime").getValue().toString();
                                                    String uidKey = snapshot.getKey();
                                                    attendAdminItems.add(0, attendAdminChangeItem);
                                                    uidLists.add(0, uidKey);
                                                }
                                                attendAdminDetailInformationActivity_recyclerViewAdapter.notifyDataSetChanged();
                                            }

                                            if (attendAdminItems.size() == 0) {
                                                activity_attend_detail_textview.setText("현황이 없습니다");
                                                activity_attend_detail_textview.setVisibility(View.VISIBLE);
                                            } else {
                                                activity_attend_detail_textview.setVisibility(View.GONE);
                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(final DatabaseError databaseError) {

                                    }
                                });

                                popup.dismiss();

                                return true;

                            case R.id.attend_state_information_tardy:

                                activity_attend_detail_textview_attend.setText("지각 현황");

                                database.getReference().child("EveryClub").child(clubName).child("Attend").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(final DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue() != null) {
                                            attendAdminItems.clear();
                                            uidLists.clear();
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                if (snapshot.child("User_State").child(userId).child("attend_state").getValue() != null && snapshot.child("User_State").child(userId).child("attend_state").getValue().equals("지각")) {
                                                    Attend_Information_Item attendAdminChangeItem = snapshot.getValue(Attend_Information_Item.class);
                                                    attendAdminChangeItem.attend_state = snapshot.child("User_State").child(userId).child("attend_state").getValue().toString();
                                                    attendAdminChangeItem.attendTimeLimit = snapshot.child("startTime").getValue().toString();
                                                    if (snapshot.child("User_State").child(userId).child("late_time").getValue() != null) {
                                                        attendAdminChangeItem.late_time = snapshot.child("User_State").child(userId).child("late_time").getValue().toString();
                                                    }
                                                    String uidKey = snapshot.getKey();
                                                    attendAdminItems.add(0, attendAdminChangeItem);
                                                    uidLists.add(0, uidKey);
                                                }
                                                attendAdminDetailInformationActivity_recyclerViewAdapter.notifyDataSetChanged();
                                            }

                                            if (attendAdminItems.size() == 0) {
                                                activity_attend_detail_textview.setText("현황이 없습니다");
                                                activity_attend_detail_textview.setVisibility(View.VISIBLE);
                                            } else {
                                                activity_attend_detail_textview.setVisibility(View.GONE);
                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(final DatabaseError databaseError) {

                                    }
                                });

                                popup.dismiss();

                                return true;

                            case R.id.attend_state_information_absent:

                                activity_attend_detail_textview_attend.setText("결석 현황");

                                database.getReference().child("EveryClub").child(clubName).child("Attend").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(final DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue() != null) {
                                            attendAdminItems.clear();
                                            uidLists.clear();
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                if (snapshot.child("User_State").child(userId).child("attend_state").getValue() != null && snapshot.child("User_State").child(userId).child("attend_state").getValue().equals("결석")) {
                                                    Attend_Information_Item attendAdminChangeItem = snapshot.getValue(Attend_Information_Item.class);
                                                    attendAdminChangeItem.attend_state = snapshot.child("User_State").child(userId).child("attend_state").getValue().toString();
                                                    attendAdminChangeItem.attendTimeLimit = snapshot.child("startTime").getValue().toString();
                                                    String uidKey = snapshot.getKey();
                                                    attendAdminItems.add(0, attendAdminChangeItem);
                                                    uidLists.add(0, uidKey);
                                                }
                                                attendAdminDetailInformationActivity_recyclerViewAdapter.notifyDataSetChanged();
                                            }

                                            if (attendAdminItems.size() == 0) {
                                                activity_attend_detail_textview.setText("현황이 없습니다");
                                                activity_attend_detail_textview.setVisibility(View.VISIBLE);
                                            } else {
                                                activity_attend_detail_textview.setVisibility(View.GONE);
                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(final DatabaseError databaseError) {

                                    }
                                });

                                popup.dismiss();

                                return true;

                            default:
                                return false;
                        }
                        //return false;
                    }
                });

                popup.inflate(R.menu.attend_state_information_popup);

                popup.setGravity(Gravity.RIGHT); //오른쪽 끝에 뜨게
                popup.show();
            }
        });

    }

    class AttendAdminDetailInformationActivity_RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private class CustomViewHolder extends RecyclerView.ViewHolder {

            LinearLayout activity_attend_information_item_linearlayout;
            TextView activity_attend_information_item_textview_date;
            TextView activity_attend_information_item_textview_attend_state;
            TextView activity_attend_information_item_textview_tardy_time;


            public CustomViewHolder(View view) {
                super(view);

                activity_attend_information_item_linearlayout = (LinearLayout) view.findViewById(R.id.activity_attend_information_item_linearlayout);
                activity_attend_information_item_textview_date = (TextView) view.findViewById(R.id.activity_attend_information_item_textview_date);
                activity_attend_information_item_textview_attend_state = (TextView) view.findViewById(R.id.activity_attend_information_item_textview_attend_state);
                activity_attend_information_item_textview_tardy_time = (TextView) view.findViewById(R.id.activity_attend_information_item_textview_tardy_time);

            }

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.activity_attend_information_item, viewGroup, false);

            return new AttendActivity_Admin_Detail_Information.AttendAdminDetailInformationActivity_RecyclerViewAdapter.CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewholder, final int position) {
            final AttendActivity_Admin_Detail_Information.AttendAdminDetailInformationActivity_RecyclerViewAdapter.CustomViewHolder customViewHolder = ((AttendActivity_Admin_Detail_Information.AttendAdminDetailInformationActivity_RecyclerViewAdapter.CustomViewHolder) viewholder);
            customViewHolder.activity_attend_information_item_textview_attend_state.setGravity(Gravity.LEFT);
            customViewHolder.activity_attend_information_item_textview_date.setGravity(Gravity.LEFT);
            customViewHolder.activity_attend_information_item_textview_tardy_time.setGravity(Gravity.LEFT);

            customViewHolder.activity_attend_information_item_textview_attend_state.setText(attendAdminItems.get(position).attend_state);
            customViewHolder.activity_attend_information_item_textview_date.setText(attendAdminItems.get(position).attendTimeLimit);
            customViewHolder.activity_attend_information_item_textview_tardy_time.setText(attendAdminItems.get(position).late_time);

            customViewHolder.activity_attend_information_item_linearlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    final PopupMenu popup = new PopupMenu(AttendActivity_Admin_Detail_Information.this, v);

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()) {

                                case R.id.attend_state_information_attend:

                                    database.getReference().child("EveryClub").child(clubName).child("Attend").child(uidLists.get(position)).child("User_State").child(userId).child("attend_state").setValue("출석");
                                    if (activity_attend_detail_textview_attend.getText().toString().equals("지각 현황")) {
                                        database.getReference().child("EveryClub").child(clubName).child("Attend").child(uidLists.get(position)).child("User_State").child(userId).child("late_time").removeValue();
                                    }
                                    popup.dismiss();

                                    return true;

                                case R.id.attend_state_information_tardy:

                                    AlertDialog.Builder builder = new AlertDialog.Builder(AttendActivity_Admin_Detail_Information.this);

                                    View view = LayoutInflater.from(AttendActivity_Admin_Detail_Information.this)
                                            .inflate(R.layout.activity_attend_tardy_time_check, null, false);
                                    builder.setView(view);

                                    final Button activity_attend_tardy_time_check_button_confirm = (Button) view.findViewById(R.id.activity_attend_tardy_time_check_button_confirm);
                                    final Button activity_attend_tardy_time_check_button_cancel = (Button) view.findViewById(R.id.activity_attend_tardy_time_check_button_cancel);
                                    final TextView activity_attend_tardy_time_check_edittext_tardy_time = (TextView) view.findViewById(R.id.activity_attend_tardy_time_check_edittext_tardy_time);

                                    final AlertDialog dialog = builder.create();

                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                    activity_attend_tardy_time_check_button_confirm.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(final View v) {
                                            EditTardyTime = activity_attend_tardy_time_check_edittext_tardy_time.getText().toString();
                                            EditTardyTime.trim();
                                            if (EditTardyTime.getBytes().length > 0) {
                                                database.getReference().child("EveryClub").child(clubName).child("Attend").child(uidLists.get(position)).child("User_State").child(userId).child("attend_state").setValue("지각");
                                                database.getReference().child("EveryClub").child(clubName).child("Attend").child(uidLists.get(position)).child("User_State").child(userId).child("late_time").setValue("+" + EditTardyTime + "분");
                                                dialog.dismiss();
                                            } else {
                                                Toast.makeText(AttendActivity_Admin_Detail_Information.this, "지각시간을 다시 입력해주세요.", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });

                                    activity_attend_tardy_time_check_button_cancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(final View v) {
                                            dialog.dismiss();
                                        }
                                    });

                                    dialog.show();
                                    popup.dismiss();

                                    return true;

                                case R.id.attend_state_information_absent:

                                    database.getReference().child("EveryClub").child(clubName).child("Attend").child(uidLists.get(position)).child("User_State").child(userId).child("attend_state").setValue("결석");
                                    if (activity_attend_detail_textview_attend.getText().toString().equals("지각 현황")) {
                                        database.getReference().child("EveryClub").child(clubName).child("Attend").child(uidLists.get(position)).child("User_State").child(userId).child("late_time").removeValue();
                                    }
                                    popup.dismiss();

                                    return true;

                                default:
                                    return false;
                            }
                            //return false;
                        }
                    });

                    popup.inflate(R.menu.attend_state_information_popup);

                    popup.setGravity(Gravity.RIGHT); //오른쪽 끝에 뜨게
                    popup.show();

                }

            });

        }

        @Override
        public int getItemCount() {
            return attendAdminItems.size();
        }

    }

    @Override
    public void onBackPressed() {
        if (menu_count > 0) {
            activity_attend_detail_slidingdrawer.animateClose();
        } else {
            finish();
        }
    }

}