package com.example.ktgk.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ktgk.R;
import com.example.ktgk.model.Book;

import java.util.List;

/**
 * Adapter cho danh sách Sách trong RecyclerView
 */
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    public interface OnBookActionListener {
        void onEditBook(Book book);
        void onDeleteBook(Book book);
    }

    private final Context context;
    private List<Book> bookList;
    private final OnBookActionListener listener;

    public BookAdapter(Context context, List<Book> bookList, OnBookActionListener listener) {
        this.context = context;
        this.bookList = bookList;
        this.listener = listener;
    }

    public void updateList(List<Book> newList) {
        this.bookList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);

        holder.tvBookCode.setText("Mã: " + book.getCode());
        holder.tvBookTitle.setText(book.getTitle());
        holder.tvBookAuthorCategory.setText("Tác giả: " + book.getAuthor() + " | Thể loại: " + book.getCategory());
        holder.tvBookStatus.setText(book.getStatus());

        // Thay đổi màu sắc thẻ trạng thái
        if (Book.STATUS_AVAILABLE.equals(book.getStatus())) {
            holder.tvBookStatus.setBackgroundResource(R.drawable.bg_status_available);
            holder.tvBookStatus.setTextColor(ContextCompat.getColor(context, R.color.status_available));
        } else {
            holder.tvBookStatus.setBackgroundResource(R.drawable.bg_status_borrowed);
            holder.tvBookStatus.setTextColor(ContextCompat.getColor(context, R.color.status_borrowed));
        }

        // Sự kiện Sửa & Xóa
        holder.btnEditBook.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditBook(book);
            }
        });

        holder.btnDeleteBook.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteBook(book);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookList != null ? bookList.size() : 0;
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookCode, tvBookStatus, tvBookTitle, tvBookAuthorCategory;
        ImageButton btnEditBook, btnDeleteBook;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookCode = itemView.findViewById(R.id.tvBookCode);
            tvBookStatus = itemView.findViewById(R.id.tvBookStatus);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvBookAuthorCategory = itemView.findViewById(R.id.tvBookAuthorCategory);
            btnEditBook = itemView.findViewById(R.id.btnEditBook);
            btnDeleteBook = itemView.findViewById(R.id.btnDeleteBook);
        }
    }
}
