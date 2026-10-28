package com.example.contactmanager;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;
import com.example.contactmanager.model.Contact;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView tvTotal;
    private TextView tvFavorite;
    private ListView lvContacts;
    private Button btnAdd;
    private MaterialCardView tvStats;
    private final ArrayList<Contact> contacts = new ArrayList<>();
    private ContactAdapter adapter;
    private ActivityResultLauncher<Intent> addLauncher;
    private ActivityResultLauncher<Intent> detailLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvTotal = findViewById(R.id.tvTotal);
        tvFavorite = findViewById(R.id.tvFavorite);
        tvStats = findViewById(R.id.tvStats);
        lvContacts = findViewById(R.id.lvContacts);
        btnAdd = findViewById(R.id.btnAdd);

        seedContacts();
        adapter = new ContactAdapter(this, contacts);
        lvContacts.setAdapter(adapter);
        updateStats();

        addLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() != RESULT_OK || result.getData() == null) return;
                    Object value = result.getData().getSerializableExtra("new_contact");
                    if (value instanceof Contact) {
                        contacts.add((Contact) value);
                        adapter.notifyDataSetChanged();
                        updateStats();
                    }
                });
        detailLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() != RESULT_OK || result.getData() == null) return;
                    Intent data = result.getData();
                    int position = data.getIntExtra("position", -1);
                    if (position < 0 || position >= contacts.size()) return;
                    String action = data.getStringExtra("action");
                    if ("delete".equals(action)) {
                        contacts.remove(position);
                    } else if ("update".equals(action)) {
                        contacts.get(position).setFavorite(data.getBooleanExtra("favorite", false));
                    } else {
                        return;
                    }
                    adapter.notifyDataSetChanged();
                    updateStats();
                });
        btnAdd.setOnClickListener(v -> openAddContact());
        lvContacts.setOnItemClickListener((parent, view, position, id) -> {
            if (position < 0 || position >= contacts.size()) return;
            Intent intent = new Intent(this, ContactDetailActivity.class);
            intent.putExtra("contact", contacts.get(position));
            intent.putExtra("position", position);
            detailLauncher.launch(intent);
        });
    }

    private void seedContacts() {
        contacts.clear();
        contacts.add(new Contact("Nguyễn Văn An", "0901234567", "Bạn bè", "Nam", "Bạn học cũ", false));
        contacts.add(new Contact("Trần Thị Bình", "0912345678", "Gia đình", "Nữ", "Chị họ", true));
        contacts.add(new Contact("Lê Minh Châu", "0987654321", "Công việc", "Nam", "Đồng nghiệp", false));
    }

    private void openAddContact() {
        Intent intent = new Intent(this, AddContactActivity.class);
        ArrayList<String> phones = new ArrayList<>();
        for (Contact contact : contacts) {
            if (contact != null && contact.getPhone() != null) phones.add(contact.getPhone());
        }
        intent.putStringArrayListExtra("existing_phones", phones);
        addLauncher.launch(intent);
    }

    private void updateStats() {
        // Đồng bộ số lượng liên hệ và số liên hệ yêu thích trên thẻ thống kê.
        int favorites = 0;
        for (Contact contact : contacts) {
            if (contact != null && contact.isFavorite()) favorites++;
        }
        tvTotal.setText(String.format(Locale.getDefault(), "%d", contacts.size()));
        tvFavorite.setText(String.format(Locale.getDefault(), "%d", favorites));
    }
}
