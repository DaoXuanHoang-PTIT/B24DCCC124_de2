package com.example.ktgk.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ktgk.AddEditBookActivity;
import com.example.ktgk.R;
import com.example.ktgk.adapter.BookAdapter;
import com.example.ktgk.database.DatabaseHelper;
import com.example.ktgk.model.Book;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment hiển thị và tìm kiếm danh sách sách
 */
public class BookListFragment extends Fragment implements BookAdapter.OnBookActionListener {

    private RecyclerView rvBooks;
    private EditText etSearchBook;
    private TextView tvEmptyBooks;
    private FloatingActionButton btnAddBookFab;

    private BookAdapter bookAdapter;
    private List<Book> bookList;
    private DatabaseHelper dbHelper;

    public BookListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_book_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DatabaseHelper(requireContext());

        rvBooks = view.findViewById(R.id.rvBooks);
        etSearchBook = view.findViewById(R.id.etSearchBook);
        tvEmptyBooks = view.findViewById(R.id.tvEmptyBooks);
        btnAddBookFab = view.findViewById(R.id.btnAddBookFab);

        bookList = new ArrayList<>();
        bookAdapter = new BookAdapter(requireContext(), bookList, this);

        rvBooks.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvBooks.setAdapter(bookAdapter);

        // Chức năng Thêm sách mới
        btnAddBookFab.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddEditBookActivity.class);
            startActivity(intent);
        });

        // Chức năng Tìm kiếm sách theo Tên hoặc Tác giả
        etSearchBook.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchBooks(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBooks();
    }

    private void loadBooks() {
        String keyword = etSearchBook.getText().toString().trim();
        if (keyword.isEmpty()) {
            bookList = dbHelper.getAllBooks();
        } else {
            bookList = dbHelper.searchBooks(keyword);
        }
        updateUi();
    }

    private void searchBooks(String keyword) {
        if (keyword.isEmpty()) {
            bookList = dbHelper.getAllBooks();
        } else {
            bookList = dbHelper.searchBooks(keyword);
        }
        updateUi();
    }

    private void updateUi() {
        bookAdapter.updateList(bookList);
        if (bookList.isEmpty()) {
            tvEmptyBooks.setVisibility(View.VISIBLE);
            rvBooks.setVisibility(View.GONE);
        } else {
            tvEmptyBooks.setVisibility(View.GONE);
            rvBooks.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onEditBook(Book book) {
        Intent intent = new Intent(requireContext(), AddEditBookActivity.class);
        intent.putExtra("EXTRA_BOOK", book);
        startActivity(intent);
    }

    @Override
    public void onDeleteBook(Book book) {
        // Không cho phép xóa sách đang mượn
        if (Book.STATUS_BORROWED.equals(book.getStatus())) {
            Toast.makeText(requireContext(), "Không thể xóa sách đang được mượn!", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa sách '" + book.getTitle() + "' (Mã: " + book.getCode() + ") không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    int rows = dbHelper.deleteBook(book.getId());
                    if (rows > 0) {
                        Toast.makeText(requireContext(), "Đã xóa sách thành công", Toast.LENGTH_SHORT).show();
                        loadBooks();
                    } else {
                        Toast.makeText(requireContext(), "Xóa sách thất bại", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
