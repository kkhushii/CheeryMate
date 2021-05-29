package hackOn2021.cheerymate.post;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import hackOn2021.cheerymate.R;
import hackOn2021.cheerymate.module.NewZoomableImageView;

public class PostImageOnClickZoom extends AppCompatActivity {

    private ScaleGestureDetector scaleGestureDetector;
    private float mScaleFactor = 1.0f;
    private NewZoomableImageView imageView;
    private ImageButton backButton;
    Toolbar toolbar;
    private ProgressBar progressBar;
    private String imageUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_image_on_click_zoom);

        imageView = findViewById(R.id.postImageZoomView);
        //  scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        toolbar = findViewById(R.id.postImageZoomToolbar);
        backButton = findViewById(R.id.postImageZoomBackButton);
        progressBar = findViewById(R.id.postImageZoomProgressBar);
        toolbar.setTitle(" ");
        setSupportActionBar(toolbar);
        setBackButton();
        Intent intent = getIntent();
        imageUrl = intent.getStringExtra("imageUrl");
        Glide.with(getApplicationContext()).load(imageUrl).into(imageView);
        progressBar.setVisibility(View.GONE);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.imageview_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.download_image) {
            try {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(imageUrl));
                startActivity(i);
            } catch (Exception e)
            {
                Toast.makeText(this, "No Image Available For Download", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }


        }

        return super.onOptionsItemSelected(item);
    }

    private void setBackButton() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



}