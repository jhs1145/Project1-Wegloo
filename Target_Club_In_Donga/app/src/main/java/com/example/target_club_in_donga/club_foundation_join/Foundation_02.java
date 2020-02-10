package com.example.target_club_in_donga.club_foundation_join;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.target_club_in_donga.Package_LogIn.AppLoginData;
import com.example.target_club_in_donga.R;
import com.example.target_club_in_donga.home_viewpager.HomeActivityView;
import com.example.target_club_in_donga.home_viewpager.MyClubSeletedItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.regex.Pattern;

import static com.example.target_club_in_donga.MainActivity.clubName;

public class Foundation_02 extends AppCompatActivity implements View.OnClickListener {

    private static final int GALLERY_CODE = 10;
    private String imagePath;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth firebaseAuth;
    private ImageButton foundation_02_button_back;
    private ImageView foundation_02_button_picture;
    private EditText foundation_02_edittext_content, foundation_02_edittext_name;
    private Button foundation_02_button_next;
    private RadioGroup foundation_02_radioGroup_01, foundation_02_radioGroup_02;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foundation_02);
        /*권한*/
        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
        progressDialog = new ProgressDialog(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        foundation_02_button_back = findViewById(R.id.foundation_02_button_back);
        foundation_02_button_picture = findViewById(R.id.foundation_02_button_picture);
        foundation_02_edittext_name = findViewById(R.id.foundation_02_edittext_name);
        foundation_02_edittext_content = findViewById(R.id.foundation_02_edittext_content);

        foundation_02_button_next = findViewById(R.id.foundation_02_button_next);
        foundation_02_radioGroup_01  = findViewById(R.id.foundation_02_radioGroup_01);
        foundation_02_radioGroup_02 = findViewById(R.id.foundation_02_radioGroup_02);

        foundation_02_button_back.setOnClickListener(this);
        foundation_02_button_next.setOnClickListener(this);
        foundation_02_button_picture.setOnClickListener(this);

        //foundation_02_radioGroup_01.getCheckedRadioButtonId();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.foundation_02_button_back:
                finish();
                break;
            case R.id.foundation_02_button_next:

                if(!Pattern.matches("^\\S{2,}$", foundation_02_edittext_name.getText().toString())){ //이름거르기
                    Toast.makeText(this, "모임 이름이 2자 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    foundationClubDialog();
                }
                break;
            case R.id.foundation_02_button_picture:
                /**
                 * 현재 뷰에는 원형으로 보이지만 결국 들어가는건 원본파일이 들어감
                 * Crop 활용해서 자른다음 올려줘야할듯 추후 추가예정
                 * https://github.com/igreenwood/SimpleCropView#cropmode
                 */
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent,GALLERY_CODE);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE) {
            try {
                imagePath = getPath(data.getData());
                File f = new File(imagePath);
                foundation_02_button_picture.setImageURI(Uri.fromFile(f));
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public String getPath(Uri uri){
        String [] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(this,uri,proj,null,null,null);

        Cursor cursor = cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(index);

    }
    private void foundationClubDialog(){
        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);

        final View view2 = LayoutInflater.from(this).inflate(R.layout.dialog_foundation_02_createclub, null, false);
        builder2.setView(view2);
        final AlertDialog dialog2 = builder2.create();
        TextView dialog_foundation_02_text = view2.findViewById(R.id.dialog_foundation_02_text);
        ImageView dialog_foundation_02_button_picture = view2.findViewById(R.id.dialog_foundation_02_button_picture);
        TextView dialog_foundation_02_realName = view2.findViewById(R.id.dialog_foundation_02_realName);
        TextView dialog_foundation_02_freeSign = view2.findViewById(R.id.dialog_foundation_02_freeSign);
        TextView dialog_foundation_02_content = view2.findViewById(R.id.dialog_foundation_02_content);
        Button dialog_foundation_02_confirmBtn = view2.findViewById(R.id.dialog_foundation_02_confirmBtn);

        dialog_foundation_02_content.setText(foundation_02_edittext_content.getText().toString());
        dialog_foundation_02_text.setText(foundation_02_edittext_name.getText().toString());
        Glide.with(view2).load(imagePath).into(dialog_foundation_02_button_picture);
        final boolean freeSign, realName;
        if(foundation_02_radioGroup_01.getCheckedRadioButtonId() == R.id.foundation_02_radio_realName_Btn){
            dialog_foundation_02_realName.setText("실명 모임");
            realName = true;
        }
        else{
            dialog_foundation_02_realName.setText("닉네임 모임");
            realName = false;
        }
        if(foundation_02_radioGroup_02.getCheckedRadioButtonId() == R.id.foundation_02_radio_freeSign_Btn){
            dialog_foundation_02_freeSign.setText("자유 가입");
            freeSign = true;
        }
        else{
            dialog_foundation_02_freeSign.setText("승인 가입");
            freeSign = false;
        }

        dialog_foundation_02_confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog2.dismiss();
                insertDB(imagePath,realName,freeSign);
            }
        });


        dialog2.show();
    }
    private void insertDB(String uri,final boolean realName,final boolean freeSign){
        progressDialog.setMessage("로딩중입니다...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        try{
            StorageReference storageRef = firebaseStorage.getReferenceFromUrl("gs://target-club-in-donga.appspot.com");

            final Uri file = Uri.fromFile(new File(uri));
            StorageReference riversRef = storageRef.child("EveryClub_ProfileImages/"+file.getLastPathSegment());
            UploadTask uploadTask = riversRef.putFile(file);

            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) { //이미지가 먼저 일단 올라가고 그게완료대면 디비에 패스올려줄꺼
                    @SuppressWarnings("VisibleForTests")
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

//                    ClubData clubData = new ClubData();
//                    clubData.setThisClubName(foundation_02_edittext_name.getText().toString());
//                    clubData.setClubIntroduce(foundation_02_edittext_content.getText().toString());
//                    clubData.setClubCreateTimestamp(System.currentTimeMillis());
//                    clubData.setRealNameSystem(realName);
//                    clubData.setFreeSign(freeSign);
//                    clubData.setClubImageUrl(downloadUrl.toString());
//                    clubData.setClubImageDeleteName(file.getLastPathSegment());

                    insertMyClub(freeSign,realName, downloadUrl.toString(), file.getLastPathSegment());

                }
            });
        }catch (NullPointerException e){ //프로필 안햇을경우

            insertMyClub(freeSign,realName,"None","None");

        }

    }
    private void insertMyClub(boolean freeSign,final boolean realName, String downloadUrl, String filePath){
        final String myUid = firebaseAuth.getCurrentUser().getUid();
        if(realName){
            final ClubData clubData = new ClubData();
            clubData.setThisClubName(foundation_02_edittext_name.getText().toString());
            clubData.setClubIntroduce(foundation_02_edittext_content.getText().toString());
            clubData.setClubCreateTimestamp(System.currentTimeMillis());
            clubData.setRealNameSystem(true);
            clubData.setFreeSign(freeSign);
            clubData.setClubImageUrl(downloadUrl);
            clubData.setClubImageDeleteName(filePath);

            firebaseDatabase.getReference().child("EveryClub").push().setValue(clubData, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    final String clubUid = databaseReference.getKey();
                    /**
                     * 회장은 어차피 승인이든 자유든 어차피 바로가입이지롱
                     * 실명 모임일경우!
                     */

                    firebaseDatabase.getReference().child("AppUser").child(myUid).child("recentClub").setValue(clubUid);
                    //firebaseDatabase.getReference().child("AppUser").child(myUid).child("signUpClub").child(clubUid).setValue(true);
                    MyClubSeletedItem myClubSeletedItem = new MyClubSeletedItem();
                    myClubSeletedItem.setApprovalCompleted(true);
                    myClubSeletedItem.setSignUpclubUid(clubUid);
                    myClubSeletedItem.setSignUpclubName(clubData.getThisClubName());
                    myClubSeletedItem.setSignUpclubProfile(clubData.getClubImageUrl());
                    myClubSeletedItem.setSignUpclubRealName(true);
                    firebaseDatabase.getReference().child("AppUser").child(myUid).child("signUpClub").child(clubUid).setValue(myClubSeletedItem);

                    firebaseDatabase.getReference().child("AppUser").child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            AppLoginData appLoginData = dataSnapshot.getValue(AppLoginData.class);
                            JoinData joinData = new JoinData();
                            joinData.setName(appLoginData.getName());
                            joinData.setPhone(appLoginData.getPhone());
                            joinData.setRealNameProPicDeleteName(appLoginData.getRealNameProPicDeleteName());
                            joinData.setRealNameProPicUrl(appLoginData.getRealNameProPicUrl());
                            joinData.setPushAlarmOnOff(true);
                            joinData.setAdmin(0);
                            joinData.setPushToken(FirebaseInstanceId.getInstance().getToken());
                            joinData.setApplicationDate(-1*System.currentTimeMillis()); //가입날짜 or 가입신청날짜
                            firebaseDatabase.getReference().child("EveryClub").child(clubUid).child("User").child(myUid).setValue(joinData);
                            clubName = clubUid;
                            /**
                             * 그클럽으로 인텐트
                             */
                            progressDialog.dismiss();
                            Toast.makeText(Foundation_02.this, "모임 만들기 완료!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Foundation_02.this, HomeActivityView.class);
                            intent.putExtra("isRecent",true);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });
        }
        else{
            progressDialog.dismiss();
            Intent intent = new Intent(Foundation_02.this, Join_02_nicName.class);
            intent.putExtra("isFoundation",true);
            intent.putExtra("thisClubName",foundation_02_edittext_name.getText().toString());
            intent.putExtra("clubIntroduce",foundation_02_edittext_content.getText().toString());
            intent.putExtra("clubDownloadUrl",downloadUrl);
            intent.putExtra("clubFilePath",filePath);
            intent.putExtra("clubFreeSign",freeSign);
            //intent.putExtra("foundationUid",clubUid);
            startActivity(intent);
            finish();
        }
    }
}
