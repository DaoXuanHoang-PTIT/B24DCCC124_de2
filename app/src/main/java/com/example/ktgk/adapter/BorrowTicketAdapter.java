package com.example.ktgk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ktgk.R;
import com.example.ktgk.model.BorrowTicket;
import com.google.android.material.button.MaterialButton;

import java.util.List;

/**
 * Adapter hiển thị danh sách phiếu mượn đang còn hiệu lực
 */
public class BorrowTicketAdapter extends RecyclerView.Adapter<BorrowTicketAdapter.TicketViewHolder> {

    public interface OnTicketReturnListener {
        void onReturnBook(BorrowTicket ticket);
    }

    private final Context context;
    private List<BorrowTicket> ticketList;
    private final OnTicketReturnListener listener;

    public BorrowTicketAdapter(Context context, List<BorrowTicket> ticketList, OnTicketReturnListener listener) {
        this.context = context;
        this.ticketList = ticketList;
        this.listener = listener;
    }

    public void updateList(List<BorrowTicket> newList) {
        this.ticketList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_borrow_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        BorrowTicket ticket = ticketList.get(position);

        String title = ticket.getBookTitle() != null ? ticket.getBookTitle() : "Mã sách: " + ticket.getBookCode();
        holder.tvTicketBookTitle.setText(title);
        holder.tvTicketBookCode.setText("Mã sách: " + ticket.getBookCode());
        holder.tvBorrowerName.setText("Người mượn: " + ticket.getBorrowerName());
        holder.tvBorrowerPhone.setText("SĐT: " + ticket.getBorrowerPhone());
        holder.tvBorrowDates.setText("Ngày mượn: " + ticket.getBorrowDate() + "  ->  Dự kiến trả: " + ticket.getReturnDate());
        holder.tvTicketStatus.setText(ticket.getStatus());

        holder.btnReturnBook.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReturnBook(ticket);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ticketList != null ? ticketList.size() : 0;
    }

    static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView tvTicketBookTitle, tvTicketBookCode, tvBorrowerName, tvBorrowerPhone, tvBorrowDates, tvTicketStatus;
        MaterialButton btnReturnBook;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTicketBookTitle = itemView.findViewById(R.id.tvTicketBookTitle);
            tvTicketBookCode = itemView.findViewById(R.id.tvTicketBookCode);
            tvBorrowerName = itemView.findViewById(R.id.tvBorrowerName);
            tvBorrowerPhone = itemView.findViewById(R.id.tvBorrowerPhone);
            tvBorrowDates = itemView.findViewById(R.id.tvBorrowDates);
            tvTicketStatus = itemView.findViewById(R.id.tvTicketStatus);
            btnReturnBook = itemView.findViewById(R.id.btnReturnBook);
        }
    }
}
