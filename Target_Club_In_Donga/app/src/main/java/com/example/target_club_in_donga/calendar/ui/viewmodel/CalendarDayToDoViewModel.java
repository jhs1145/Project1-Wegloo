package com.example.target_club_in_donga.calendar.ui.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.target_club_in_donga.calendar.data.TSLiveData;
import com.example.target_club_in_donga.calendar.utils.DateFormat;


public class CalendarDayToDoViewModel extends ViewModel {
    public TSLiveData<String> mTodoTitleData = new TSLiveData<>();
    public TSLiveData<Boolean> mIsWorkChecked = new TSLiveData<>();
    public TSLiveData<String> mTimeStampString = new TSLiveData<>();

    public void setTodoTitleData(String mTodoTitleData) {
        this.mTodoTitleData.setValue(mTodoTitleData);
    }
    public void setIsWorkChecked(Boolean mIsWorkChecked){
        this.mIsWorkChecked.setValue(mIsWorkChecked);
    }

    public void setTimeStampString(long time){
        this.mTimeStampString.setValue(DateFormat.getDate(time, DateFormat.CALENDAR_DAY_FORMAT));
    }

    public boolean getIsWorkChecked() {
        return this.mIsWorkChecked.getValue();
    }
}
