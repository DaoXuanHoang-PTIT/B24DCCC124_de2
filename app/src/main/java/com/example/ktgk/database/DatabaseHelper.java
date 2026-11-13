package com.example.ktgk.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ktgk.model.Book;
import com.example.ktgk.model.BorrowTicket;

import java.util.ArrayList;
import java.util.List;

/**
 * Lớp làm việc với cơ sở dữ liệu SQLite quản lý thư viện
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "LibraryManagement.db";
    private static final int DATABASE_VERSION = 1;

    // Bảng Sách (books)
    private static final String TABLE_BOOKS = "books";
    private static final String KEY_BOOK_ID = "id";
    private static final String KEY_BOOK_CODE = "code";
    private static final String KEY_BOOK_TITLE = "title";
    private static final String KEY_BOOK_AUTHOR = "author";
    private static final String KEY_BOOK_CATEGORY = "category";
    private static final String KEY_BOOK_STATUS = "status";

    // Bảng Phiếu Mượn (borrow_tickets)
    private static final String TABLE_BORROW = "borrow_tickets";
    private static final String KEY_BORROW_ID = "id";
    private static final String KEY_BORROW_BOOK_CODE = "book_code";
    private static final String KEY_BORROW_NAME = "borrower_name";
    private static final String KEY_BORROW_PHONE = "borrower_phone";
    private static final String KEY_BORROW_DATE = "borrow_date";
    private static final String KEY_RETURN_DATE = "return_date";
    private static final String KEY_BORROW_STATUS = "status";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng sách
        String createBooksTable = "CREATE TABLE " + TABLE_BOOKS + " ("
                + KEY_BOOK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_BOOK_CODE + " TEXT UNIQUE NOT NULL, "
                + KEY_BOOK_TITLE + " TEXT NOT NULL, "
                + KEY_BOOK_AUTHOR + " TEXT NOT NULL, "
                + KEY_BOOK_CATEGORY + " TEXT NOT NULL, "
                + KEY_BOOK_STATUS + " TEXT NOT NULL DEFAULT '" + Book.STATUS_AVAILABLE + "')";

        // Tạo bảng phiếu mượn
        String createBorrowTable = "CREATE TABLE " + TABLE_BORROW + " ("
                + KEY_BORROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_BORROW_BOOK_CODE + " TEXT NOT NULL, "
                + KEY_BORROW_NAME + " TEXT NOT NULL, "
                + KEY_BORROW_PHONE + " TEXT NOT NULL, "
                + KEY_BORROW_DATE + " TEXT NOT NULL, "
                + KEY_RETURN_DATE + " TEXT NOT NULL, "
                + KEY_BORROW_STATUS + " TEXT NOT NULL DEFAULT '" + BorrowTicket.TICKET_STATUS_BORROWING + "')";

        db.execSQL(createBooksTable);
        db.execSQL(createBorrowTable);

        // Chèn một số dữ liệu mẫu
        insertSampleData(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        db.execSQL("INSERT INTO " + TABLE_BOOKS + " ("
                + KEY_BOOK_CODE + ", " + KEY_BOOK_TITLE + ", " + KEY_BOOK_AUTHOR + ", " + KEY_BOOK_CATEGORY + ", " + KEY_BOOK_STATUS + ") VALUES "
                + "('MS001', 'Lập trình Android với Java', 'Nguyễn Văn A', 'Công nghệ thông tin', 'Có sẵn'), "
                + "('MS002', 'Dế Mèn Phiêu Lưu Ký', 'Tô Hoài', 'Văn học', 'Có sẵn'), "
                + "('MS003', 'Nhà Giả Kim', 'Paulo Coelho', 'Tiểu thuyết', 'Có sẵn'), "
                + "('MS004', 'Giải Thuật Và Lập Trình', 'Lê Minh Hoàng', 'Giáo trình', 'Có sẵn')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BORROW);
        onCreate(db);
    }

    // ==========================================
    // QUẢN LÝ SÁCH (BOOK FUNCTIONS)
    // ==========================================

    /**
     * Kiểm tra mã sách đã tồn tại hay chưa
     */
    public boolean isBookCodeExists(String code) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BOOKS, new String[]{KEY_BOOK_ID},
                KEY_BOOK_CODE + "=?", new String[]{code}, null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return exists;
    }

    /**
     * Kiểm tra mã sách bị trùng với sách khác khi sửa
     */
    public boolean isBookCodeExistsForOther(String code, int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BOOKS, new String[]{KEY_BOOK_ID},
                KEY_BOOK_CODE + "=? AND " + KEY_BOOK_ID + "!=?",
                new String[]{code, String.valueOf(id)}, null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return exists;
    }

    /**
     * Thêm sách mới
     */
    public long insertBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_BOOK_CODE, book.getCode());
        values.put(KEY_BOOK_TITLE, book.getTitle());
        values.put(KEY_BOOK_AUTHOR, book.getAuthor());
        values.put(KEY_BOOK_CATEGORY, book.getCategory());
        values.put(KEY_BOOK_STATUS, book.getStatus() != null ? book.getStatus() : Book.STATUS_AVAILABLE);

        return db.insert(TABLE_BOOKS, null, values);
    }

    /**
     * Cập nhật thông tin sách
     */
    public int updateBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_BOOK_CODE, book.getCode());
        values.put(KEY_BOOK_TITLE, book.getTitle());
        values.put(KEY_BOOK_AUTHOR, book.getAuthor());
        values.put(KEY_BOOK_CATEGORY, book.getCategory());
        values.put(KEY_BOOK_STATUS, book.getStatus());

        return db.update(TABLE_BOOKS, values, KEY_BOOK_ID + "=?",
                new String[]{String.valueOf(book.getId())});
    }

    /**
     * Cập nhật trạng thái sách (Có sẵn / Đang được mượn)
     */
    public void updateBookStatus(String bookCode, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_BOOK_STATUS, status);
        db.update(TABLE_BOOKS, values, KEY_BOOK_CODE + "=?", new String[]{bookCode});
    }

    /**
     * Xóa sách theo ID
     */
    public int deleteBook(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_BOOKS, KEY_BOOK_ID + "=?", new String[]{String.valueOf(id)});
    }

    /**
     * Lấy tất cả danh sách sách
     */
    public List<Book> getAllBooks() {
        List<Book> bookList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_BOOKS + " ORDER BY " + KEY_BOOK_ID + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Book book = new Book();
                book.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_BOOK_ID)));
                book.setCode(cursor.getString(cursor.getColumnIndexOrThrow(KEY_BOOK_CODE)));
                book.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_BOOK_TITLE)));
                book.setAuthor(cursor.getString(cursor.getColumnIndexOrThrow(KEY_BOOK_AUTHOR)));
                book.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(KEY_BOOK_CATEGORY)));
                book.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(KEY_BOOK_STATUS)));

                bookList.add(book);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bookList;
    }

    /**
     * Lấy danh sách sách có trạng thái "Có sẵn"
     */
    public List<Book> getAvailableBooks() {
        List<Book> bookList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BOOKS, null, KEY_BOOK_STATUS + "=?",
                new String[]{Book.STATUS_AVAILABLE}, null, null, KEY_BOOK_TITLE + " ASC");

        if (cursor.moveToFirst()) {
            do {
                Book book = new Book();
                book.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_BOOK_ID)));
                book.setCode(cursor.getString(cursor.getColumnIndexOrThrow(KEY_BOOK_CODE)));
                book.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_BOOK_TITLE)));
                book.setAuthor(cursor.getString(cursor.getColumnIndexOrThrow(KEY_BOOK_AUTHOR)));
                book.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(KEY_BOOK_CATEGORY)));
                book.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(KEY_BOOK_STATUS)));

                bookList.add(book);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bookList;
    }

    /**
     * Tìm kiếm sách theo tên sách hoặc tên tác giả
     */
    public List<Book> searchBooks(String keyword) {
        List<Book> bookList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BOOKS + " WHERE "
                + KEY_BOOK_TITLE + " LIKE ? OR "
                + KEY_BOOK_AUTHOR + " LIKE ? OR "
                + KEY_BOOK_CODE + " LIKE ? ORDER BY " + KEY_BOOK_ID + " DESC";

        String searchPattern = "%" + keyword + "%";
        Cursor cursor = db.rawQuery(query, new String[]{searchPattern, searchPattern, searchPattern});

        if (cursor.moveToFirst()) {
            do {
                Book book = new Book();
                book.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_BOOK_ID)));
                book.setCode(cursor.getString(cursor.getColumnIndexOrThrow(KEY_BOOK_CODE)));
                book.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_BOOK_TITLE)));
                book.setAuthor(cursor.getString(cursor.getColumnIndexOrThrow(KEY_BOOK_AUTHOR)));
                book.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(KEY_BOOK_CATEGORY)));
                book.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(KEY_BOOK_STATUS)));

                bookList.add(book);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bookList;
    }

    /**
     * Lấy sách theo Mã sách
     */
    public Book getBookByCode(String code) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BOOKS, null, KEY_BOOK_CODE + "=?",
                new String[]{code}, null, null, null);
        Book book = null;
        if (cursor != null && cursor.moveToFirst()) {
            book = new Book(
                    cursor.getInt(cursor.getColumnIndexOrThrow(KEY_BOOK_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_BOOK_CODE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_BOOK_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_BOOK_AUTHOR)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_BOOK_CATEGORY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_BOOK_STATUS))
            );
            cursor.close();
        }
        return book;
    }

    // ==========================================
    // QUẢN LÝ PHIẾU MƯỢN / TRẢ SÁCH (BORROW / RETURN)
    // ==========================================

    /**
     * Thêm phiếu mượn sách mới và cập nhật trạng thái sách sang "Đang được mượn"
     */
    public long insertBorrowTicket(BorrowTicket ticket) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        long result = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_BORROW_BOOK_CODE, ticket.getBookCode());
            values.put(KEY_BORROW_NAME, ticket.getBorrowerName());
            values.put(KEY_BORROW_PHONE, ticket.getBorrowerPhone());
            values.put(KEY_BORROW_DATE, ticket.getBorrowDate());
            values.put(KEY_RETURN_DATE, ticket.getReturnDate());
            values.put(KEY_BORROW_STATUS, BorrowTicket.TICKET_STATUS_BORROWING);

            result = db.insert(TABLE_BORROW, null, values);

            if (result != -1) {
                // Cập nhật trạng thái của sách sang "Đang được mượn"
                ContentValues bookValues = new ContentValues();
                bookValues.put(KEY_BOOK_STATUS, Book.STATUS_BORROWED);
                db.update(TABLE_BOOKS, bookValues, KEY_BOOK_CODE + "=?", new String[]{ticket.getBookCode()});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return result;
    }

    /**
     * Trả sách: Cập nhật trạng thái phiếu mượn sang "Đã trả" và trạng thái sách sang "Có sẵn"
     */
    public boolean returnBook(int ticketId, String bookCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            // 1. Cập nhật phiếu mượn
            ContentValues ticketValues = new ContentValues();
            ticketValues.put(KEY_BORROW_STATUS, BorrowTicket.TICKET_STATUS_RETURNED);
            int rowsTicket = db.update(TABLE_BORROW, ticketValues, KEY_BORROW_ID + "=?", new String[]{String.valueOf(ticketId)});

            // 2. Cập nhật trạng thái sách về "Có sẵn"
            ContentValues bookValues = new ContentValues();
            bookValues.put(KEY_BOOK_STATUS, Book.STATUS_AVAILABLE);
            int rowsBook = db.update(TABLE_BOOKS, bookValues, KEY_BOOK_CODE + "=?", new String[]{bookCode});

            if (rowsTicket > 0 && rowsBook > 0) {
                db.setTransactionSuccessful();
                return true;
            }
        } finally {
            db.endTransaction();
        }
        return false;
    }

    /**
     * Lấy danh sách các phiếu mượn đang còn hiệu lực (Đang mượn)
     */
    public List<BorrowTicket> getActiveBorrowTickets() {
        List<BorrowTicket> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Join bảng borrow_tickets và books để lấy được tên sách
        String query = "SELECT b.*, k." + KEY_BOOK_TITLE
                + " FROM " + TABLE_BORROW + " b"
                + " LEFT JOIN " + TABLE_BOOKS + " k ON b." + KEY_BORROW_BOOK_CODE + " = k." + KEY_BOOK_CODE
                + " WHERE b." + KEY_BORROW_STATUS + " = ?"
                + " ORDER BY b." + KEY_BORROW_ID + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{BorrowTicket.TICKET_STATUS_BORROWING});

        if (cursor.moveToFirst()) {
            do {
                BorrowTicket ticket = new BorrowTicket();
                ticket.setId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_BORROW_ID)));
                ticket.setBookCode(cursor.getString(cursor.getColumnIndexOrThrow(KEY_BORROW_BOOK_CODE)));
                ticket.setBookTitle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_BOOK_TITLE)));
                ticket.setBorrowerName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_BORROW_NAME)));
                ticket.setBorrowerPhone(cursor.getString(cursor.getColumnIndexOrThrow(KEY_BORROW_PHONE)));
                ticket.setBorrowDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_BORROW_DATE)));
                ticket.setReturnDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_RETURN_DATE)));
                ticket.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(KEY_BORROW_STATUS)));

                list.add(ticket);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}
