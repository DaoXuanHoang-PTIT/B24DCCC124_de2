package com.example.ktgk;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.ktgk.fragment.BookListFragment;
import com.example.ktgk.fragment.BorrowFragment;
import com.example.ktgk.fragment.BorrowTicketListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Activity chính chứa BottomNavigationView điều hướng 3 chức năng chính
 */
public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Mặc định hiển thị Màn hình Danh sách Sách
        if (savedInstanceState == null) {
            loadFragment(new BookListFragment());
        }

        // Sự kiện chuyển Tab trên BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_books) {
                selectedFragment = new BookListFragment();
            } else if (itemId == R.id.nav_borrow) {
                selectedFragment = new BorrowFragment();
            } else if (itemId == R.id.nav_tickets) {
                selectedFragment = new BorrowTicketListFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragmentContainer, fragment)
                .commit();
    }
}
