package com.hcodestudio.smstimer.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.hcodestudio.smstimer.R;

import java.util.List;
import java.util.Locale;

import static com.hcodestudio.smstimer.R.id.floatingActionButton;
import static com.hcodestudio.smstimer.R.id.saveFab;
import static com.hcodestudio.smstimer.data.TimerContract.TIMER_CONTENT_URI;
import static com.hcodestudio.smstimer.data.TimerContract.TimerEntry.DATE;
import static com.hcodestudio.smstimer.data.TimerContract.TimerEntry.MESSAGE;
import static com.hcodestudio.smstimer.data.TimerContract.TimerEntry.NAME;
import static com.hcodestudio.smstimer.data.TimerContract.TimerEntry.PHONENO;
import static com.hcodestudio.smstimer.data.TimerContract.TimerEntry.TIME;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ScheduleMessageActivity extends AppCompatActivity {
    private static final String TAG = "ScheduleMessageActivity";
    private TextInputEditText textInputEditText;
    private static TextInputEditText phoneEditText;
    private TextInputEditText timeEditText;
    private static TextInputEditText messageEditText;
    SimpleDateFormat simpleDateFormat;
    static String phone;
    static String message;
    String date;
    String time;
    String name;
    String nums = "";
    TextView sentInfo, deliveredInfo;
    private static Calendar calendar;
    public static String s;
    public static final String PREF_NAME = "MyPref";

    static final int PICK_CONTACT_REQUEST = 1;
    public BroadcastReceiver sentStatusReceiver, deliveredStatusReceiver;

    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
    Calendar compareCalendar = Calendar.getInstance();
    final String dat = dateFormat.format(compareCalendar.getTime());
    static String restorePhone;
    static String restoreMessage;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

         sentInfo = (TextView) findViewById(R.id.sentInfo);
        deliveredInfo = (TextView) findViewById(R.id.deliveredInfo);
        phoneEditText = (TextInputEditText) findViewById(R.id.phoneNumber);
        messageEditText = (TextInputEditText) findViewById(R.id.message);
        setupDateEditText();
        setupTimeEditText();
        addContact();
        getContent();

//        date = textInputEditText.getText().toString();
//        time = timeEditText.getText().toString();
//        message = messageEditText.getText().toString();
//        phone = phoneEditText.getText().toString();

        FloatingActionButton floatingActionButton =
                (FloatingActionButton) findViewById(R.id.saveFab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Store messages and phone  numbers
                saveContent();
                //sendMessage();
                scheduleTask(calendar.getTimeInMillis());
                 // save(name, phone, message, date, time);
                finish();
                Toast.makeText(ScheduleMessageActivity.this, "Message Scheduled, go Relax", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getContent();

    }

    @Override
    protected void onStop() {
        super.onStop();
        saveContent();
    }

    public void saveContent(){
        SharedPreferences.Editor editor = getSharedPreferences("sendMessage", MODE_PRIVATE).edit();
        editor.putString("phone", phone);
        editor.putString("msg", message);
        editor.apply();
    }

    public void getContent(){
        SharedPreferences sharedPreferences = getSharedPreferences("sendMessage", MODE_PRIVATE);
        restorePhone = sharedPreferences.getString("phone", " ");
        restoreMessage = sharedPreferences.getString("msg", " ");
    }

    private void scheduleTask(long time) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent  = new Intent(this, MessageScheduler.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.setExact(AlarmManager.RTC, time, pendingIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setupDateEditText() {
        final Calendar today = Calendar.getInstance();
        final Calendar calendar = Calendar.getInstance();

        textInputEditText = (TextInputEditText) findViewById(R.id.date);
        final DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);

                String myFormat = "dd/MM/yy";
                simpleDateFormat = new SimpleDateFormat(myFormat, Locale.ENGLISH);


                if (calendar.before(today)){
                    textInputEditText.setText("");
                    timeEditText.setText("");
                    Toast.makeText(ScheduleMessageActivity.this, "Please, choose a current or future date",
                            Toast.LENGTH_SHORT).show();
                } else {
                    textInputEditText.setText(simpleDateFormat.format(calendar.getTimeInMillis()));
                    timeEditText.setText("");
                }
            }
        };

        textInputEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(ScheduleMessageActivity.this, onDateSetListener, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setupTimeEditText() {
        final Calendar today = Calendar.getInstance();
         calendar = Calendar.getInstance();

        timeEditText = (TextInputEditText) findViewById(R.id.time);
        final TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {

                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);

                String AM_PM;
                if(hour < 12){
                    AM_PM = "AM";
                }else {
                    AM_PM = "PM";
                }

                String myFormat = "hh:mm";
                simpleDateFormat = new SimpleDateFormat(myFormat, Locale.US);


                if (dat.equals(textInputEditText.getText().toString()) && calendar.getTime().before(today.getTime())) {
                    timeEditText.setText("");
                    Toast.makeText(ScheduleMessageActivity.this, "Please, choose future time",
                            Toast.LENGTH_SHORT).show();
                } else {
                    timeEditText.setText(simpleDateFormat.format(calendar.getTimeInMillis()) + " " + AM_PM);
                }
            }

        };

        timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(ScheduleMessageActivity.this, onTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), true).show();
                time = timeEditText.getText().toString();

            }
        });
    }

    private void addContact() {
        AppCompatImageButton appCompatImageButton = (AppCompatImageButton) findViewById(R.id.addcontact);
        appCompatImageButton.setOnClickListener(    new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, PICK_CONTACT_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST && resultCode< 0 ) {
//            if (RESULT_OK == -1) {
                Uri contactList = data.getData();
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

                Cursor cursor = getContentResolver().query(contactList, projection, null, null, null);

                name = "";

                if (cursor.moveToFirst()) {
                    do {
                        int n = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        int nam = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                        nums = cursor.getString(n);
                        name = cursor.getString(nam);
                    } while (cursor.moveToNext());
                }
                if (phoneEditText.getText().toString().isEmpty()) {
                    phoneEditText.setText(nums);
                } else{
                    phoneEditText.getText().append(", " + nums );
                }
            //}
            Toast.makeText(this, phone, Toast.LENGTH_SHORT).show();
        }
    }


    public static void sendMessage(Context context) {
        //try {
        List<String> messages;
        String [] split;
        SmsManager smsManager = SmsManager.getDefault();
        message = messageEditText.getText().toString();
        phone = phoneEditText.getText().toString();


        if(phone != null && message != null){
             split = phone.split(",");
            //Divide message when too long
            messages = smsManager.divideMessage(message);
        }
        else {
            split = restorePhone.split(",");
            //Divide message when too long
            messages = smsManager.divideMessage(restoreMessage);
        }


        for (String msg : messages){

            for(String fone : split){
                PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent("SMS_SENT"), 0);
                PendingIntent deliveredIntent = PendingIntent.getBroadcast(context, 0, new Intent("SMS_DELIVERED"), 0);
                smsManager.sendTextMessage(fone, null, msg, sentIntent, deliveredIntent);

                //Toast.makeText(ScheduleMessageActivity.this, "Sent", Toast.LENGTH_SHORT).show();
//       }catch
// +(Exception e){
//            Toast.makeText(this, "Message Not sent", Toast.LENGTH_SHORT).show();
//
            }
        }
    }

    public void onResume() {
        super.onResume();
        sentStatusReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
               s = "Unknown Error";

                switch (getResultCode()){
                    case Activity.RESULT_OK:
                        s = "Message Sent Successfully!!";
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        s = "Generic Failure Error";
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        s = "No Service Available";
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        s = "Error: Null PDU";
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        s = "Error : Radio is off";
                        break;
                    default:
                        break;
                }

                SharedPreferences.Editor editor = getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit();
                editor.putString("sent", s);
                editor.apply();

                sentInfo.setText(s);

            }
        };

        deliveredStatusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s = "Message not Delivered";
                switch (getResultCode()){
                    case Activity.RESULT_OK:
                        s = "Message Delivered Successfully";
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                deliveredInfo.setText(s);
            }
        };

        registerReceiver(sentStatusReceiver, new IntentFilter("SMS_SENT"));
        registerReceiver(deliveredStatusReceiver, new IntentFilter("SMS_DELIVERED"));
    }

    public void onPause() {
        super.onPause();
        unregisterReceiver(sentStatusReceiver);
        unregisterReceiver(deliveredStatusReceiver);
    }

    private void save(String name, String phoneNo, String message, String date, String time){
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, name);
        contentValues.put(PHONENO, phoneNo);
        contentValues.put(MESSAGE, message);
        contentValues.put(DATE, date);
        contentValues.put(TIME, time);
        getContentResolver().insert(TIMER_CONTENT_URI, contentValues);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("message", messageEditText.getText().toString());
        outState.putString("phone", phoneEditText.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        messageEditText.setText(savedInstanceState.getString("message"));
        phoneEditText.setText(savedInstanceState.getString("phone"));
    }
}



