package com.example.ktgk;

import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ktgk.database.DatabaseHelper;
import com.example.ktgk.model.Book;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

/**
 * Activity Thêm hoặc Sửa thông tin Sách
 */
public class AddEditBookActivity extends AppCompatActivity {

    private EditText etBookCode, etBookTitle, etBookAuthor, etBookCategory;

    private DatabaseHelper dbHelper;
    private Book editingBook = null; // Nếu null là Thêm mới, khác null là Sửa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_book);

        // Xử lý System Bar Insets để căn vừa đẹp khung màn hình thiết bị
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainAddEdit), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);

        MaterialToolbar toolbar = findViewById(R.id.toolbarAddEditBook);
        etBookCode = findViewById(R.id.etBookCode);
        etBookTitle = findViewById(R.id.etBookTitle);
        etBookAuthor = findViewById(R.id.etBookAuthor);
        etBookCategory = findViewById(R.id.etBookCategory);
        MaterialButton btnSaveBook = findViewById(R.id.btnSaveBook);

        // Sự kiện nút quay lại trên Toolbar
        toolbar.setNavigationOnClickListener(v -> finish());

        // Kiểm tra xem là Thêm mới hay Sửa
        if (getIntent().hasExtra("EXTRA_BOOK")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                editingBook = getIntent().getSerializableExtra("EXTRA_BOOK", Book.class);
            } else {
                editingBook = (Book) getIntent().getSerializableExtra("EXTRA_BOOK");
            }
        }

        if (editingBook != null) {
            // Chế độ Sửa
            toolbar.setTitle("Chỉnh Sửa Thông Tin Sách");
            etBookCode.setText(editingBook.getCode());
            etBookTitle.setText(editingBook.getTitle());
            etBookAuthor.setText(editingBook.getAuthor());
            etBookCategory.setText(editingBook.getCategory());
        } else {
            // Chế độ Thêm mới
            toolbar.setTitle("Thêm Sách Mới");
        }

        btnSaveBook.setOnClickListener(v -> saveBook());
    }

    private void saveBook() {
        String code = etBookCode.getText().toString().trim();
        String title = etBookTitle.getText().toString().trim();
        String author = etBookAuthor.getText().toString().trim();
        String category = etBookCategory.getText().toString().trim();

        // 1. Kiểm tra các trường bắt buộc
        if (code.isEmpty()) {
            etBookCode.setError("Vui lòng nhập mã sách");
            etBookCode.requestFocus();
            return;
        }

        if (title.isEmpty()) {
            etBookTitle.setError("Vui lòng nhập tên sách");
            etBookTitle.requestFocus();
            return;
        }

        if (author.isEmpty()) {
            etBookAuthor.setError("Vui lòng nhập tên tác giả");
            etBookAuthor.requestFocus();
            return;
        }

        if (category.isEmpty()) {
            etBookCategory.setError("Vui lòng nhập thể loại sách");
            etBookCategory.requestFocus();
            return;
        }

        if (editingBook == null) {
            // 2. Chế độ THÊM MỚI -> Kiểm tra trùng mã sách
            if (dbHelper.isBookCodeExists(code)) {
                etBookCode.setError("Mã sách đã tồn tại trong thư viện!");
                etBookCode.requestFocus();
                Toast.makeText(this, "Không thể thêm: Mã sách đã bị trùng!", Toast.LENGTH_SHORT).show();
                return;
            }

            Book newBook = new Book(code, title, author, category, Book.STATUS_AVAILABLE);
            long result = dbHelper.insertBook(newBook);
            if (result != -1) {
                Toast.makeText(this, "Thêm sách thành công!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Lỗi khi thêm sách!", Toast.LENGTH_SHORT).show();
            }

        } else {
            // 3. Chế độ CHỈNH SỬA -> Kiểm tra trùng mã sách với các sách khác
            if (dbHelper.isBookCodeExistsForOther(code, editingBook.getId())) {
                etBookCode.setError("Mã sách đã tồn tại ở cuốn sách khác!");
                etBookCode.requestFocus();
                Toast.makeText(this, "Không thể lưu: Mã sách đã bị trùng!", Toast.LENGTH_SHORT).show();
                return;
            }

            editingBook.setCode(code);
            editingBook.setTitle(title);
            editingBook.setAuthor(author);
            editingBook.setCategory(category);

            int rows = dbHelper.updateBook(editingBook);
            if (rows > 0) {
                Toast.makeText(this, "Cập nhật thông tin sách thành công!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
