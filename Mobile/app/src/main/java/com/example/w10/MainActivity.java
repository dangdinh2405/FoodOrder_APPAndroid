package com.example.w10;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RouteListingPreference;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.w10.socket.NewsSocket;
import com.google.android.material.navigation.NavigationView;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.example.w10.databinding.ActivityMainBinding;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    public static ArrayList<String> orders = new ArrayList<>();
    public NewsSocket newsSocket;
    private final String baseUrl = "192.168.1.6:8080";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void openFeedbackActivity(MenuItem item) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:foodorder@cntt.io"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for Food Order App");
        startActivity(intent);
    }

    public void onClickOrder(View view) {
        if (orders.isEmpty())
            return;
        AsyncTask.execute(() -> {
            try {
                URL url = new URL("http://" + baseUrl + "/orderFood");
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setDoOutput(true);
                StringBuilder jsonInputString = new StringBuilder("{\"food\": [");
                for (String s: orders) {
                    Log.i("test", s);
                    jsonInputString.append("\"").append(s).append("\",");
                }
                jsonInputString.deleteCharAt(jsonInputString.length() - 1);
                jsonInputString.append("]}");
                String inputString = jsonInputString.toString();
                Log.i("test", inputString);
                try(OutputStream os = con.getOutputStream()) {
                    byte[] input = inputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println(response.toString());
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel("news", "NEWS", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Đây là thông báo mục tin tức");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
            // Bước trên để tạo config trong máy android về mục thông báo
        }
        this.newsSocket = new NewsSocket("ws://" + baseUrl + "/getFoodStatusUpdate");
        this.newsSocket.connect((msg) -> {
//            Log.i("ws", msg);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "news");
            builder.setSmallIcon(R.color.white);
            builder.setContentTitle("Thông báo tình trạng món ăn");
            builder.setContentText(msg);

            NotificationManagerCompat manager = NotificationManagerCompat.from(this);
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                manager.notify(1, builder.build());
            }
        });
    }
}