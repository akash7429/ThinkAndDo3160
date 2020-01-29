package com.example.loginauth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.loginauth.util.BottomNavigationViewHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    String names[]={"Profile","Inspirational Feed","Friends","Next Plan"};
    CircleMenu circleMenu;

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.search2)
    ImageView search2;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.framelayout)
    FrameLayout framelayout;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        circleMenu = (CircleMenu)findViewById(R.id.circleMenu);

        circleMenu.setMainMenu(Color.parseColor("#CDCDCD"),R.drawable.icon_friends,R.drawable.icon_home)
                .addSubMenu(Color.parseColor("#258CFF"),R.drawable.icon_checkin)
                .addSubMenu(Color.parseColor("#258CFF"),R.drawable.icon_create)
                .addSubMenu(Color.parseColor("#258CFF"),R.drawable.icon_image)
                .addSubMenu(Color.parseColor("#258CFF"),R.drawable.icon_posts)



                .setOnMenuSelectedListener(new OnMenuSelectedListener() {
                    @Override
                    public void onMenuSelected(int i) {
                        Toast.makeText(getApplicationContext(),"You selected = "+names[i], Toast.LENGTH_SHORT).show();
                    }
                });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        bottomNavigation.inflateMenu(R.menu.bottom_navigation_main);
        bottomNavigation.setItemBackgroundResource(R.color.colorPrimary);
        bottomNavigation.setItemTextColor(ContextCompat.getColorStateList(bottomNavigation.getContext(), R.color.nav_item_colors));
        bottomNavigation.setItemIconTintList(ContextCompat.getColorStateList(bottomNavigation.getContext(), R.color.nav_item_colors));
        BottomNavigationViewHelper.removeShiftMode(bottomNavigation);

    }

    public void logout(View view) {

        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
        //not finished();
    }
}
