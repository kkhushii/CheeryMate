package hackOn2021.cheerymate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import hackOn2021.cheerymate.fragment.BottomNavExpertConnect;
import hackOn2021.cheerymate.fragment.BottomNavFeed;
import hackOn2021.cheerymate.fragment.BottomNavMentorFeed;
import hackOn2021.cheerymate.fragment.BottomNavProfile;

public class MentorFeed extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mentor_feed);

        toolbar = findViewById(R.id.drawer_layout_toolbar);
        setSupportActionBar(toolbar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavListener);
        bottomNavFragChange(-100);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tool_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.toolbar_chat) {
            Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    protected BottomNavigationView.OnNavigationItemSelectedListener bottomNavListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    bottomNavFragChange(item.getItemId());
                    return true;
                }
            };

    void bottomNavFragChange(int id)
    {
        Fragment selectedFragment = null;

        switch (id)
        {
            case R.id.bottom_nav_mentor_feed:
                selectedFragment = new BottomNavMentorFeed();
                break;

            case R.id.bottom_profile:
                selectedFragment = new BottomNavProfile();
                break;

        }

        if(id==-100)
            selectedFragment = new BottomNavMentorFeed();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
    }


}

