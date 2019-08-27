package com.concretepage.android;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {

	private static int timeHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
	private static int timeMinute = Calendar.getInstance().get(Calendar.MINUTE);
	TextView alarmTimeTextView;
	private Button setAlarmButton;
	private Button cancelAlarmButton;
	private static TextView alarmAlertTextView;

	//To get the instance of Alarm Time TextView
	public static TextView getAlarmAlertTextView() {
		return alarmAlertTextView;
	}

	AlarmManager alarmManager;
	private PendingIntent pendingIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mylayout);
		alarmTimeTextView = (TextView)findViewById(R.id.msg1);
		alarmTimeTextView.setText(timeHour + ":" + timeMinute);
		alarmAlertTextView = (TextView)findViewById(R.id.alarmAlertTextView);
		
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent myIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, 0);

		// Listener for set AlarmButton
        OnClickListener setAlarmButtonListener = new OnClickListener() {
			public void onClick(View view) {
				alarmAlertTextView.setText("");
				Bundle bundle = new Bundle();
				bundle.putInt(MyConstants.HOUR, timeHour);
				bundle.putInt(MyConstants.MINUTE, timeMinute);
				MyDialogFragment fragment = new MyDialogFragment(new MyHandler());
				fragment.setArguments(bundle);
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.add(fragment, MyConstants.TIME_PICKER);
                transaction.commit();
			}
         };
        
         this.setAlarmButton = (Button)findViewById(R.id.setAlarmButton);
         this.setAlarmButton.setOnClickListener(setAlarmButtonListener);

		 //Listener for CancelAlarmButton
         OnClickListener cancelAlarmButtonListener = new OnClickListener() {
			public void onClick(View view) {
				alarmAlertTextView.setText("");
				cancelAlarm();
			}
         };
         this.cancelAlarmButton = (Button)findViewById(R.id.cancelAlarmButton);
         this.cancelAlarmButton.setOnClickListener(cancelAlarmButtonListener);
    }

    class MyHandler extends Handler {
    	@Override
    	public void handleMessage (Message msg){
    		Bundle bundle = msg.getData();
    		timeHour = bundle.getInt(MyConstants.HOUR);
    		timeMinute = bundle.getInt(MyConstants.MINUTE);
    		alarmTimeTextView.setText(timeHour + ":" + timeMinute);
    		setAlarm();
    	}
    }

	//Sets the Alarm, sets the TextView of AlarmTime
    private void setAlarm(){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, timeHour);
	    calendar.set(Calendar.MINUTE, timeMinute);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

	// Cancel the Alarm and stop the Ringtone
    private void cancelAlarm() {
        if (alarmManager!= null) {
			AlarmReceiver.stopRingtone();
        	alarmManager.cancel(pendingIntent);
        }
    }
} 