package com.example.ktgk.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ktgk.R;
import com.example.ktgk.database.DatabaseHelper;
import com.example.ktgk.model.Book;
import com.example.ktgk.model.BorrowTicket;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Fragment cho phép thực hiện Mượn Sách
 */
public class BorrowFragment extends Fragment {

    private Spinner spAvailableBooks;
    private EditText etBorrowerName, etBorrowerPhone, etBorrowDate, etReturnDate;
    private MaterialButton btnSaveBorrowTicket;

    private DatabaseHelper dbHelper;
    private List<Book> availableBooks;
    private final Calendar calendarBorrow = Calendar.getInstance();
    private final Calendar calendarReturn = Calendar.getInstance();

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public BorrowFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_borrow, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DatabaseHelper(requireContext());

        spAvailableBooks = view.findViewById(R.id.spAvailableBooks);
        etBorrowerName = view.findViewById(R.id.etBorrowerName);
        etBorrowerPhone = view.findViewById(R.id.etBorrowerPhone);
        etBorrowDate = view.findViewById(R.id.etBorrowDate);
        etReturnDate = view.findViewById(R.id.etReturnDate);
        btnSaveBorrowTicket = view.findViewById(R.id.btnSaveBorrowTicket);

        // Đặt ngày mặc định: Ngày mượn = hôm nay, Ngày trả = 7 ngày sau
        calendarReturn.add(Calendar.DAY_OF_MONTH, 7);
        etBorrowDate.setText(dateFormat.format(calendarBorrow.getTime()));
        etReturnDate.setText(dateFormat.format(calendarReturn.getTime()));

        // Thiết lập chọn ngày bằng DatePickerDialog
        etBorrowDate.setOnClickListener(v -> showDatePicker(calendarBorrow, etBorrowDate));
        etReturnDate.setOnClickListener(v -> showDatePicker(calendarReturn, etReturnDate));

        btnSaveBorrowTicket.setOnClickListener(v -> saveBorrowTicket());
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAvailableBooks();
    }

    private void loadAvailableBooks() {
        availableBooks = dbHelper.getAvailableBooks();
        if (availableBooks.isEmpty()) {
            ArrayAdapter<String> emptyAdapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    new String[]{"(Không có sách nào sẵn có để mượn)"});
            spAvailableBooks.setAdapter(emptyAdapter);
            spAvailableBooks.setEnabled(false);
        } else {
            spAvailableBooks.setEnabled(true);
            ArrayAdapter<Book> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_dropdown_item, availableBooks);
            spAvailableBooks.setAdapter(adapter);
        }
    }

    private void showDatePicker(Calendar calendar, EditText editText) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    editText.setText(dateFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void saveBorrowTicket() {
        if (availableBooks == null || availableBooks.isEmpty()) {
            Toast.makeText(requireContext(), "Hiện không có sách nào sẵn có để mượn!", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedPosition = spAvailableBooks.getSelectedItemPosition();
        if (selectedPosition < 0 || selectedPosition >= availableBooks.size()) {
            Toast.makeText(requireContext(), "Vui lòng chọn cuốn sách cần mượn!", Toast.LENGTH_SHORT).show();
            return;
        }

        Book selectedBook = availableBooks.get(selectedPosition);

        // Kiểm tra xem sách có đang được mượn bởi người khác không (bảo vệ thêm)
        Book currentBookInfo = dbHelper.getBookByCode(selectedBook.getCode());
        if (currentBookInfo != null && Book.STATUS_BORROWED.equals(currentBookInfo.getStatus())) {
            Toast.makeText(requireContext(), "Sách này đang được người khác mượn!", Toast.LENGTH_SHORT).show();
            loadAvailableBooks();
            return;
        }

        String name = etBorrowerName.getText().toString().trim();
        String phone = etBorrowerPhone.getText().toString().trim();
        String borrowDate = etBorrowDate.getText().toString().trim();
        String returnDate = etReturnDate.getText().toString().trim();

        if (name.isEmpty()) {
            etBorrowerName.setError("Vui lòng nhập họ tên người mượn");
            etBorrowerName.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            etBorrowerPhone.setError("Vui lòng nhập số điện thoại");
            etBorrowerPhone.requestFocus();
            return;
        }

        BorrowTicket ticket = new BorrowTicket(
                selectedBook.getCode(),
                name,
                phone,
                borrowDate,
                returnDate,
                BorrowTicket.TICKET_STATUS_BORROWING
        );

        long id = dbHelper.insertBorrowTicket(ticket);
        if (id != -1) {
            Toast.makeText(requireContext(), "Tạo phiếu mượn sách thành công!", Toast.LENGTH_LONG).show();

            // Clear input
            etBorrowerName.setText("");
            etBorrowerPhone.setText("");
            loadAvailableBooks();
        } else {
            Toast.makeText(requireContext(), "Tạo phiếu mượn thất bại!", Toast.LENGTH_SHORT).show();
        }
    }
}
