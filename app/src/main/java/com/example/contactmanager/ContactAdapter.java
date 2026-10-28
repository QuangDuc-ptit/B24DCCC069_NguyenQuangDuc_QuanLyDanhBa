package com.example.contactmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.contactmanager.model.Contact;

import java.util.ArrayList;

public class ContactAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final ArrayList<Contact> contacts;

    public ContactAdapter(Context context, ArrayList<Contact> contacts) {
        this.inflater = LayoutInflater.from(context);
        this.contacts = contacts;
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Contact getItem(int position) {
        return position >= 0 && position < contacts.size() ? contacts.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_contact, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Contact contact = getItem(position);
        if (contact != null) {
            holder.avatar.setText(getInitial(contact.getName()));
            holder.name.setText(contact.getName());
            holder.phone.setText(contact.getPhone());
            holder.group.setText(contact.getGroup());
            holder.favorite.setVisibility(contact.isFavorite() ? View.VISIBLE : View.INVISIBLE);
        }
        return convertView;
    }

    private String getInitial(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "?";
        }
        String[] parts = name.trim().split("\\s+");
        return parts[parts.length - 1].substring(0, 1).toUpperCase(java.util.Locale.getDefault());
    }

    private static class ViewHolder {
        final TextView avatar;
        final TextView name;
        final TextView phone;
        final TextView group;
        final ImageView favorite;

        ViewHolder(View view) {
            avatar = view.findViewById(R.id.tvAvatar);
            name = view.findViewById(R.id.tvName);
            phone = view.findViewById(R.id.tvPhone);
            group = view.findViewById(R.id.tvGroup);
            favorite = view.findViewById(R.id.imgFavorite);
        }
    }
}
