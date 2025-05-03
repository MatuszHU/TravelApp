package hu.matusz.travelapp;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import hu.matusz.travelapp.util.database.models.User;

public class UserActivity extends AppCompatActivity {
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.user_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView profileImage = findViewById(R.id.profile_image);
        TextView name = findViewById(R.id.name);
        TextView email = findViewById(R.id.email);
        TextView country = findViewById(R.id.country);

        user = (User) getIntent().getSerializableExtra("user");
        if(user.getPhotoURI()!=""){
            Glide.with(this).load(user.getPhotoURI()).circleCrop().into(profileImage);
        }

        name.setText(user.getName());
        email.setText(user.getEmail());
        country.setText(user.getCountryOfOriginCode());

    }
}