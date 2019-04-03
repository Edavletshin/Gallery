package com.example.testsport;

import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.testsport.adapters.TabsAdapter;
import com.example.testsport.fragments.FavouritesList;
import com.example.testsport.fragments.NoConnectionFragment;
import com.example.testsport.fragments.PhotosList;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class MainActivity extends AppCompatActivity {

    public static final String CLIENT_ID = "ef793db3b2157414b3881996b85c2dcc9b8b99ead01e7e976fdfaac5d6cbdef9";

    public static final String PREFERENCES = "list";
    public static final String PREFERENCES_LIST = "first_elem";

    private TabsAdapter tabsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = findViewById(R.id.viewpager);
        tabsAdapter = new TabsAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabsAdapter);

        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        if (!isOnline())
        {
            Toast.makeText(this, "Check your internet connection!", Toast.LENGTH_SHORT).show();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new NoConnectionFragment(), "CHECK_CONN").commit();
        }
    }

    public FavouritesList getFavouritesList()
    {
        return (tabsAdapter.getFavouritesList());
    }

    public PhotosList getPhotosList()
    {
        return (tabsAdapter.getPhotosList());
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }

}
