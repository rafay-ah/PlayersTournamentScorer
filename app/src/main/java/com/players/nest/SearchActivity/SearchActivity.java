package com.players.nest.SearchActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import com.players.nest.R;
import com.google.android.material.tabs.TabLayout;
import com.players.nest.HelperClasses.Constants;
import com.players.nest.HelperClasses.FirebaseHelperClass;

public class SearchActivity extends AppCompatActivity {

    searchFragmentInterface searchFragmentInterface;

    EditText searchBar;
    TabLayout tabLayout;
    ViewPager viewPager;
    TextWatcher textWatcher;
    ImageView backArrow, clearIcon;
    ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        backArrow = findViewById(R.id.imageView24);
        clearIcon = findViewById(R.id.imageView73);
        searchBar = findViewById(R.id.editTextTextPersonName);
        constraintLayout = findViewById(R.id.constraintLayout8);


        searchBar.requestFocus();

        backArrow.setOnClickListener(view -> finish());


        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().isEmpty()) {
                    searchFragmentInterface.emptyString();
                    clearIcon.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String search = editable.toString();
                if (!search.isEmpty()) {
                    clearIcon.setVisibility(View.VISIBLE);
                    searchFragmentInterface.onSearch(search);
                }
            }
        };

        clearIcon.setOnClickListener(v -> {
            searchBar.setText("");
            searchFragmentInterface.emptyString();
        });

        //Games Fragment TextWatcher
        searchBar.addTextChangedListener(textWatcher);

        setUpViewPager();
    }


    private void setUpViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(adapter);

        //Changing the editText when the View pager scroll to Games Fragment
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position != 0) {
                    searchBar.setHint("Search Games..");
                } else {
                    searchBar.setHint("Search Peoples..");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }


    public void passVal(searchFragmentInterface fragmentCommunicator) {
        this.searchFragmentInterface = fragmentCommunicator;
    }


    public interface searchFragmentInterface {
        void emptyString();

        void onSearch(String msg);
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseHelperClass.changeStatus(Constants.ONLINE);
    }


    @Override
    protected void onPause() {
        super.onPause();
        FirebaseHelperClass.changeStatus(Constants.OFFLINE);
    }
}