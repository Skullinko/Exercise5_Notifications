package maxian.milos.countdowntimer;

import android.app.Notification;
import android.app.NotificationManager;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private int visibility, notification_id;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotifyBuilder;

    private CountDownTimer countDownTimer;

    private NumberPicker npHours, npMinutes, npSeconds;
    private TextView textView;
    private Button btnStartPause, btnCancel;
    private boolean isWorking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        visibility = Notification.VISIBILITY_PUBLIC;
        notification_id = 1;
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyBuilder = getMyNotifyBuilder();

        countDownTimer = null;

        npHours = (NumberPicker) findViewById(R.id.npHours);
        npHours.setMinValue(0);
        npHours.setMaxValue(99);
        npMinutes = (NumberPicker) findViewById(R.id.npMinutes);
        npMinutes.setMinValue(0);
        npMinutes.setMaxValue(59);
        npSeconds = (NumberPicker) findViewById(R.id.npSeconds);
        npSeconds.setMinValue(0);
        npSeconds.setMaxValue(59);

        textView = (TextView) findViewById(R.id.textView1);

        btnStartPause = (Button) findViewById(R.id.btnStartPause);
        btnStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isWorking) {
                    isWorking = false;
                    btnStartPause.setText(R.string.btnStart);
                    pauseTimer();
                } else {
                    int millis = getMillis();
                    if (millis != 0) {
                        isWorking = true;
                        btnStartPause.setText(R.string.btnPause);
                        startTimer(millis);
                    }
                }
            }
        });
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNotificationManager.cancel(notification_id);
                finnish();
                pauseTimer();
                countDownTimer = null;
            }
        });
    }

    @Override
    protected void onDestroy() {
        pauseTimer();
        super.onDestroy();
    }

    public NotificationCompat.Builder getMyNotifyBuilder() {
        return (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setCategory(Notification.CATEGORY_PROMO)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(getText(R.string.ntfTitle))
                .setAutoCancel(true)
                .setVisibility(visibility);
    }

    public void updateToLastNotification() {
        mNotifyBuilder.setContentText(getText(R.string.ntfTextEnd))
                .setVibrate(new long[]{1000, 1000})
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
    }

    private int getMillis() {
        int hours = npHours.getValue();
        int minutes = npMinutes.getValue();
        int seconds = npSeconds.getValue();
        return ((((hours * 60) + minutes) * 60) + seconds) * 1000;
    }

    public void startTimer(int millis) {
        enableNumberPickers(false);

        countDownTimer = new CountDownTimer(millis, 1000) {
            public void onTick(long millisUntilFinished) {
                long millis = millisUntilFinished;
                int hours = (int) TimeUnit.MILLISECONDS.toHours(millis);
                millis -= TimeUnit.HOURS.toMillis(hours);
                int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(millis);
                millis -= TimeUnit.MINUTES.toMillis(minutes);
                int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(millis);

                npHours.setValue(hours);
                npMinutes.setValue(minutes);
                npSeconds.setValue(seconds);

                String text = getText(R.string.txtTime) + ": " + String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
                textView.setText(text);
                mNotifyBuilder.setContentText(text);
                mNotificationManager.notify(notification_id, mNotifyBuilder.build());
            }

            public void onFinish() {
                updateToLastNotification();
                mNotificationManager.notify(notification_id, mNotifyBuilder.build());
                finnish();
            }
        };
        countDownTimer.start();
    }

    public void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public void finnish() {
        isWorking = false;
        npHours.setValue(0);
        npMinutes.setValue(0);
        npSeconds.setValue(0);
        enableNumberPickers(true);
        textView.setText("");
        btnStartPause.setText(R.string.btnStart);
        mNotifyBuilder = getMyNotifyBuilder();
    }

    private void enableNumberPickers(boolean bool) {
        npHours.setEnabled(bool);
        npMinutes.setEnabled(bool);
        npSeconds.setEnabled(bool);
    }
}