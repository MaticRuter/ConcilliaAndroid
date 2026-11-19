
package concilia;

import android.app.Service;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;

import si.uni_lj.fe.tnuv.concilliumandroid.R;

public class MusicService extends Service {

    private MediaPlayer mediaPlayer;

    private static final String CHANNEL_ID = "ConciliaMusicChannel";
    private static final int NOTIFICATION_ID = 1;

    // Število vseh glasbenih datotek
    private int totalSongs = 3;

    // Trenutna skladba
    private int currentSong = 0;

    // Seznam glasbenih datotek
    private int[] songs = {
            R.raw.background_music,
            R.raw.background_music1,
            R.raw.background_music2,
           // R.raw.background_music3
    };

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        Notification notification;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("Concilia")
                    .setContentText("Glasba se predvaja")
                    .setSmallIcon(android.R.drawable.ic_media_play)
                    .setOngoing(true)
                    .build();
        } else {
            notification = new Notification.Builder(this)
                    .setContentTitle("Concilia")
                    .setContentText("Glasba se predvaja")
                    .setSmallIcon(android.R.drawable.ic_media_play)
                    .setOngoing(true)
                    .build();
        }

        startForeground(NOTIFICATION_ID, notification);

        playSong(currentSong);
    }

    private void playSong(int songIndex) {

        // Sprostimo prejšnji MediaPlayer
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        mediaPlayer = MediaPlayer.create(this, songs[songIndex]);

        if (mediaPlayer != null) {

            mediaPlayer.setLooping(false);
            mediaPlayer.setVolume(1.0f, 1.0f);

            mediaPlayer.setOnCompletionListener(mp -> {

                // Naslednja skladba
                currentSong++;

                // Če pridemo do konca seznama, začnemo znova
                if (currentSong >= totalSongs) {
                    currentSong = 0;
                }

                playSong(currentSong);
            });

            mediaPlayer.start();

            android.util.Log.d("ConciliaMusic",
                    "Predvaja se skladba: " + songIndex);

        } else {
            android.util.Log.e("ConciliaMusic",
                    "MediaPlayer je NULL!");
        }
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Concilia Music",
                    NotificationManager.IMPORTANCE_LOW
            );

            NotificationManager manager =
                    getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && "STOP".equals(intent.getAction())) {
            stopSelf();
            return START_NOT_STICKY;
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
