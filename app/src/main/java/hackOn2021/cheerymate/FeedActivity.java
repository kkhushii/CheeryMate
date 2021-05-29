package hackOn2021.cheerymate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.collection.LLRBNode;

import hackOn2021.cheerymate.fragment.BottomNavExpertConnect;
import hackOn2021.cheerymate.fragment.BottomNavFeed;
import hackOn2021.cheerymate.fragment.BottomNavProfile;

public class FeedActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        toolbar = findViewById(R.id.drawer_layout_toolbar);
        toolbar.setTitle("CheeryMate");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavListener);
        bottomNavFragChange(-100);

    }


    /** Bottom Nav Menu Section **/

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavListener =
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
            case R.id.bottom_nav_feed:
                selectedFragment = new BottomNavFeed();
                break;

            case R.id.bottom_expert_connect:
                selectedFragment = new BottomNavExpertConnect();
                break;

            case R.id.bottom_profile:
                selectedFragment = new BottomNavProfile();
                break;

        }

        if(id==-100)
            selectedFragment = new BottomNavFeed();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
    }


    /** ToolBar Menu ***/

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



}