package com.example.loginauth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.loginauth.Fragments.FriendsFragment;
import com.example.loginauth.Fragments.NewsFeedFragment;
import com.example.loginauth.Fragments.NotificationFragment;
import com.example.loginauth.Fragments.ProfileFragment;
import com.example.loginauth.util.BottomNavigationViewHelper;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;

import butterknife.BindView;
import butterknife.ButterKnife;



// Checking the github working .. :)--(:
public class HomeActivity extends AppCompatActivity {

    String names[] = {"Profile", "Inspirational Feed", "Friends", "Next Plan"};
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

    //Creating the instances of fragments
    NewsFeedFragment newsFeedFragment;
    ProfileFragment profileFragment;
    NotificationFragment notificationFragment;
    FriendsFragment friendsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        circleMenu = (CircleMenu) findViewById(R.id.circleMenu);

        circleMenu.setMainMenu(Color.parseColor("#CDCDCD"), R.drawable.icon_friends, R.drawable.icon_home)
                .addSubMenu(Color.parseColor("#258CFF"), R.drawable.icon_checkin)
                .addSubMenu(Color.parseColor("#258CFF"), R.drawable.icon_create)
                .addSubMenu(Color.parseColor("#258CFF"), R.drawable.icon_image)
                .addSubMenu(Color.parseColor("#258CFF"), R.drawable.icon_posts)


                .setOnMenuSelectedListener(new OnMenuSelectedListener() {
                    @Override
                    public void onMenuSelected(int i) {
                        Toast.makeText(getApplicationContext(), "You selected = " + names[i], Toast.LENGTH_SHORT).show();
                    }
                });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        bottomNavigation.inflateMenu(R.menu.bottom_navigation_main);
        bottomNavigation.setItemBackgroundResource(R.color.colorPrimary);
        bottomNavigation.setItemTextColor(ContextCompat.getColorStateList(bottomNavigation.getContext(), R.color.nav_item_colors));
        bottomNavigation.setItemIconTintList(ContextCompat.getColorStateList(bottomNavigation.getContext(), R.color.nav_item_colors));
        BottomNavigationViewHelper.removeShiftMode(bottomNavigation);

        //initialize the instances
        newsFeedFragment = new NewsFeedFragment();
        notificationFragment = new NotificationFragment();
        friendsFragment = new FriendsFragment();
        profileFragment = new ProfileFragment();

        setFragment(newsFeedFragment);
       bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
           @Override
           public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
               switch (menuItem.getItemId()) {
                   case R.id.newsfeed_fragment:
                       setFragment(newsFeedFragment);
                       break;

                   case R.id.profile_fragment:
                       startActivity(new Intent(HomeActivity.this, ProfileActivity.class).putExtra("aid", FirebaseAuth.getInstance().getCurrentUser().getUid()));
                       break;

                   case R.id.profile_friends:
                       setFragment(friendsFragment);
                       break;

                   case R.id.profile_notification:
                       setFragment(notificationFragment);
                       break;

               }
               return true;
           }
       });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this , UploadActivity.class));
            }
        });
    }

    public void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.framelayout,fragment);
        fragmentTransaction.commit();

    }

    public void logout(final View view) {

        Toast.makeText(HomeActivity.this,"Clciked",Toast.LENGTH_SHORT).show();
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();

        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
        //not finished();

        GoogleSignIn.getClient(this,new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()).signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                startActivity(new Intent(view.getContext(), MainActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HomeActivity.this,"Google Signout Failed",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
