package com.example.contactmanager;

import android.os.Bundle;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.contactmanager.model.Contact;

public class ContactDetailActivity extends AppCompatActivity {
    private TextView tvAvatar;
    private TextView tvName;
    private TextView tvPhone;
    private TextView tvGroupGender;
    private TextView tvNote;
    private CheckBox chkFavorite;
    private Button btnCall;
    private Button btnBack;
    private Button btnDelete;
    private Contact contact;
    private int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);
        tvAvatar = findViewById(R.id.tvAvatar);
        tvName = findViewById(R.id.tvName);
        tvPhone = findViewById(R.id.tvPhone);
        tvGroupGender = findViewById(R.id.tvGroupGender);
        tvNote = findViewById(R.id.tvNote);
        chkFavorite = findViewById(R.id.chkFavorite);
        btnCall = findViewById(R.id.btnCall);
        btnBack = findViewById(R.id.btnBack);
        btnDelete = findViewById(R.id.btnDelete);

        Object value = getIntent().getSerializableExtra("contact");
        if (!(value instanceof Contact)) {
            finish();
            return;
        }
        contact = (Contact) value;
        position = getIntent().getIntExtra("position", -1);
        if (position < 0) {
            finish();
            return;
        }
        bindContact();
        chkFavorite.setOnCheckedChangeListener((buttonView, isChecked) -> contact.setFavorite(isChecked));
        btnCall.setOnClickListener(v -> {
            if (contact.getPhone() != null && !contact.getPhone().trim().isEmpty()) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contact.getPhone())));
            }
        });
        btnBack.setOnClickListener(v -> finishWithUpdate());
        btnDelete.setOnClickListener(v -> confirmDelete());
    }

    private void bindContact() {
        tvAvatar.setText(getInitial(contact.getName()));
        tvName.setText(contact.getName());
        tvPhone.setText(contact.getPhone());
        tvGroupGender.setText(contact.getGroup() + " • " + contact.getGender());
        String note = contact.getNote();
        tvNote.setText(note == null || note.trim().isEmpty() ? getString(R.string.no_note) : note);
        chkFavorite.setChecked(contact.isFavorite());
    }

    private String getInitial(String name) {
        if (name == null || name.trim().isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        return parts[parts.length - 1].substring(0, 1).toUpperCase(java.util.Locale.getDefault());
    }

    private void finishWithUpdate() {
        // Trả trạng thái yêu thích hiện tại về MainActivity khi rời màn hình.
        Intent result = new Intent();
        result.putExtra("action", "update");
        result.putExtra("position", position);
        result.putExtra("favorite", contact.isFavorite());
        setResult(RESULT_OK, result);
        finish();
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.delete_contact_confirmation)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete_contact, (dialog, which) -> {
                    Intent result = new Intent();
                    result.putExtra("action", "delete");
                    result.putExtra("position", position);
                    setResult(RESULT_OK, result);
                    finish();
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        finishWithUpdate();
    }
}
