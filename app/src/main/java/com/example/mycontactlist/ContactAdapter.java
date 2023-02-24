package com.example.mycontactlist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter {
    private ArrayList<Contact> contactData;
    private View.OnClickListener mOnItemClickListener;
    private boolean isDeleting;
    private Context parentContext;
    private ContactAdapter adapter;


    public class ContactViewHolder extends RecyclerView.ViewHolder{

        public TextView textViewContact;
        public TextView textPhone;
        public TextView textAddress;
        public TextView textState;
        public TextView textCity;
        public TextView textZip;

        public Button deleteButton;
        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewContact = itemView.findViewById(R.id.textContactName);
            textPhone = itemView.findViewById(R.id.textPhoneNumber);
            deleteButton = itemView.findViewById(R.id.buttonDeleteContact);
            textAddress = itemView.findViewById(R.id.textContactAddress);
            textState = itemView.findViewById(R.id.textContactState);
            textCity = itemView.findViewById(R.id.textContactCity);
            textZip = itemView.findViewById(R.id.textContactZip);
            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
            itemView.findViewById(R.id.buttonDeleteContact).setOnClickListener(view -> {
                adapter.contactData.remove(getAdapterPosition());
                adapter.notifyItemRemoved(getAdapterPosition());
            });
        }

        public TextView getPhoneTextView() {
            return textPhone;
        }

        public Button getDeleteButton() {
            return deleteButton;
        }
        public TextView getContactTextView() {
            return textViewContact;
        }

        public TextView getAddressTextView() {
            return textAddress;
        }

        public TextView getStateTextView() {
            return textState;
        }

        public TextView getCityTextView() {
            return textCity;
        }

        public TextView getZipTextView() {
            return textZip;
        }
    }

    public ContactAdapter(ArrayList<Contact> arrayList, Context context) {
        contactData = arrayList;
        parentContext = context;
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ContactViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ContactViewHolder cvh = (ContactViewHolder) holder;
        cvh.getContactTextView().setText(contactData.get(position).getContactName());
        cvh.getPhoneTextView().setText(contactData.get(position).getPhoneNumber());
        cvh.getAddressTextView().setText(contactData.get(position).getStreetAddress());
        cvh.getStateTextView().setText(contactData.get(position).getState());
        cvh.getCityTextView().setText(contactData.get(position).getCity());
        cvh.getZipTextView().setText(contactData.get(position).getZipCode());

        if(position %2 == 0)
        {
            ((ContactViewHolder) holder).textViewContact.setTextColor(Color.parseColor("#FF0000"));
        }
        else
        {
            ((ContactViewHolder) holder).textViewContact.setTextColor(Color.parseColor("#0000FF"));
        }

            if (isDeleting) {
                cvh.getDeleteButton().setVisibility(View.VISIBLE);
                cvh.getDeleteButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteItem(position);
                    }
                });
            }
            else {
                cvh.getDeleteButton().setVisibility(View.INVISIBLE);
            }

    }

    @Override
    public int getItemCount() {
        return contactData.size();
    }


    private void deleteItem (int position) {
        Contact contact = contactData.get(position);
        ContactDataSource ds = new ContactDataSource(parentContext);
        try {
            ds.open();
            boolean didDelete = ds.deleteContact(contact.getContactId());
            ds.close();
            if (didDelete) {
                contactData.remove(position);
                notifyDataSetChanged();
                Toast.makeText(parentContext, "Delete Successful", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(parentContext, "Delete Failed!", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e) {
        }
    }

    public void setDelete (boolean b) {
        isDeleting = b;
    }

}
