package com.example.ktgk.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ktgk.R;
import com.example.ktgk.adapter.BorrowTicketAdapter;
import com.example.ktgk.database.DatabaseHelper;
import com.example.ktgk.model.BorrowTicket;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment hiển thị danh sách phiếu mượn đang còn hiệu lực và thực hiện Trả sách
 */
public class BorrowTicketListFragment extends Fragment implements BorrowTicketAdapter.OnTicketReturnListener {

    private RecyclerView rvBorrowTickets;
    private TextView tvEmptyTickets;

    private BorrowTicketAdapter adapter;
    private List<BorrowTicket> ticketList;
    private DatabaseHelper dbHelper;

    public BorrowTicketListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_borrow_ticket_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DatabaseHelper(requireContext());

        rvBorrowTickets = view.findViewById(R.id.rvBorrowTickets);
        tvEmptyTickets = view.findViewById(R.id.tvEmptyTickets);

        ticketList = new ArrayList<>();
        adapter = new BorrowTicketAdapter(requireContext(), ticketList, this);

        rvBorrowTickets.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvBorrowTickets.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTickets();
    }

    private void loadTickets() {
        ticketList = dbHelper.getActiveBorrowTickets();
        adapter.updateList(ticketList);

        if (ticketList.isEmpty()) {
            tvEmptyTickets.setVisibility(View.VISIBLE);
            rvBorrowTickets.setVisibility(View.GONE);
        } else {
            tvEmptyTickets.setVisibility(View.GONE);
            rvBorrowTickets.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onReturnBook(BorrowTicket ticket) {
        String bookName = ticket.getBookTitle() != null ? ticket.getBookTitle() : ticket.getBookCode();

        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận trả sách")
                .setMessage("Thực hiện trả sách '" + bookName + "' (Mã: " + ticket.getBookCode() + ") của người mượn " + ticket.getBorrowerName() + "?")
                .setPositiveButton("Trả sách", (dialog, which) -> {
                    boolean success = dbHelper.returnBook(ticket.getId(), ticket.getBookCode());
                    if (success) {
                        Toast.makeText(requireContext(), "Trả sách thành công! Trạng thái sách đã cập nhật về 'Có sẵn'.", Toast.LENGTH_LONG).show();
                        loadTickets();
                    } else {
                        Toast.makeText(requireContext(), "Thao tác trả sách thất bại!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
