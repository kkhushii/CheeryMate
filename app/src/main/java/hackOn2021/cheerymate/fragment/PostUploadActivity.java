package hackOn2021.cheerymate.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import hackOn2021.cheerymate.R;
import hackOn2021.cheerymate.module.Post;

import static android.view.View.GONE;

public class PostUploadActivity extends AppCompatActivity {
    private CheckBox checkBox;
    private ImageButton addPhotoButton; private ImageView imageToUpload;
    private TextView postButton; private ImageButton closeButton;
    private EditText descriptionText; private TextView textCount;
    ProgressBar progressBar;
    private Toolbar toolbar;

    private Uri selectedImage; private UploadTask uploadTask;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_upload);

        checkBox = findViewById(R.id.checkbox);
        descriptionText = findViewById(R.id.description);
        textCount = findViewById(R.id.descriptionTextCount);
        closeButton = findViewById(R.id.buttonPostCancel);
        imageToUpload = findViewById(R.id.imageToPost);
        postButton = findViewById(R.id.buttonPostUpload);
        addPhotoButton = findViewById(R.id.addImageToPost);
        progressBar = findViewById(R.id.progressBarPostUpload);
        databaseReference = FirebaseDatabase.getInstance("https://hackon2021-cheerymate-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("post");
        toolbar = findViewById(R.id.postUploadActivityToolBar);
        setSupportActionBar(toolbar);
        buttonInitialized();

    }

    private void buttonInitialized()
    {
        descriptionText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = descriptionText.getText().toString().length();
                textCount.setText(length + "/120");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View v) {
                if(descriptionText.getText().toString().length()<=120)
                {
                    createPost();
                }
                else
                    Toast.makeText(PostUploadActivity.this, "Text limit exceeded.", Toast.LENGTH_SHORT).show();
            }
        });

        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null)
        {
            selectedImage = data.getData();
            imageToUpload.setImageURI(selectedImage);
            addPhotoButton.setImageResource(R.drawable.ic_image_replace);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void createPost() {
        progressBar.setVisibility(View.VISIBLE);
        Post newPost = new Post();
        if (imageToUpload.getDrawable() != null) {
            try {
                Bitmap bitmap = null;
                if (selectedImage != null) {
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), selectedImage);
                    bitmap = ImageDecoder.decodeBitmap(source);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
                    byte[] byteData = outputStream.toByteArray();

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReference();
                    final StorageReference imageReference = storageReference.child("post/" + selectedImage.getLastPathSegment());
                    uploadTask = imageReference.putBytes(byteData);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    uploadPost(uri);
                                }
                            });

                        }
                    });
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(GONE);
                            Toast.makeText(PostUploadActivity.this, "Image Upload Error, Try again later.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
            uploadPost(null);


    }

    private void uploadPost(Uri uri)
    {   Post post;

        if(descriptionText.getText().toString().isEmpty() && uri == null)
        {
            postButton.setEnabled(true);
            Toast.makeText(this, "Empty Post", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(GONE);
            return;
        }
        String postId = databaseReference.push().getKey();

        if(uri == null)
            post = new Post(FirebaseAuth.getInstance().getUid(), getTime(), descriptionText.getText().toString(), "", postId);
        else
            post = new Post(FirebaseAuth.getInstance().getUid(), getTime(), descriptionText.getText().toString(), uri.toString(), postId);

        if(checkBox.isChecked())
            post.setUid("");

        // databaseReference.push().setValue(post);

        databaseReference.child(postId).setValue(post);

        FirebaseDatabase.getInstance("https://hackon2021-cheerymate-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("CountRecord").child("post").child(postId).child("likeCount").setValue(0);
        FirebaseDatabase.getInstance("https://hackon2021-cheerymate-default-rtdb.asia-southeast1.firebasedatabase.app").getReference().child("CountRecord").child("post").child(postId).child("commentCount").setValue(0);

        Toast.makeText(PostUploadActivity.this, "Posted Successfully", Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(GONE);
        finish();
    }

    private String getTime()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpledateformat = new SimpleDateFormat("dd.MMM hh:mm aaa");
        String date = simpledateformat.format(calendar.getTime());
        Log.i("SERVER TIME", date);
        return date.toUpperCase();
    }



}