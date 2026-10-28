package com.example.contactmanager;

import android.os.Bundle;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.contactmanager.model.Contact;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class AddContactActivity extends AppCompatActivity {
    private EditText edtName;
    private EditText edtPhone;
    private Spinner spnGroup;
    private RadioGroup rgGender;
    private RadioButton rbMale;
    private RadioButton rbFemale;
    private RadioButton rbOther;
    private CheckBox chkFavorite;
    private EditText edtNote;
    private Button btnSave;
    private ImageButton btnBack;
    private ArrayList<String> existingPhones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        spnGroup = findViewById(R.id.spnGroup);
        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        rbOther = findViewById(R.id.rbOther);
        chkFavorite = findViewById(R.id.chkFavorite);
        edtNote = findViewById(R.id.edtNote);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        existingPhones = getIntent().getStringArrayListExtra("existing_phones");
        if (existingPhones == null) existingPhones = new ArrayList<>();

        String[] groups = getResources().getStringArray(R.array.contact_groups);
        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(
                this, R.layout.spinner_item, Arrays.asList(groups));
        groupAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spnGroup.setAdapter(groupAdapter);
        rbMale.setChecked(true);
        btnSave.setOnClickListener(v -> saveContact());
        btnBack.setOnClickListener(v -> finish());
    }

    private void saveContact() {
        // Validate tuần tự để báo đúng lỗi đầu tiên cho người dùng.
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        if (name.isEmpty()) {
            edtName.setError(getString(R.string.name_required_error));
            edtName.requestFocus();
            return;
        }
        if (!Pattern.matches("^0\\d{9}$", phone)) {
            edtPhone.setError(getString(R.string.phone_format_error));
            edtPhone.requestFocus();
            return;
        }
        if (existingPhones.contains(phone)) {
            edtPhone.setError(getString(R.string.phone_exists_error));
            edtPhone.requestFocus();
            return;
        }
        int selectedId = rgGender.getCheckedRadioButtonId();
        RadioButton selectedGender = findViewById(selectedId);
        String gender = selectedGender == null ? getString(R.string.male) : selectedGender.getText().toString();
        String group = spnGroup.getSelectedItem() == null
                ? getString(R.string.other) : spnGroup.getSelectedItem().toString();
        String note = edtNote.getText().toString().trim();
        Contact contact = new Contact(name, phone, group, gender, note, chkFavorite.isChecked());
        Intent result = new Intent();
        result.putExtra("new_contact", contact);
        setResult(RESULT_OK, result);
        Toast.makeText(this, R.string.contact_added, Toast.LENGTH_SHORT).show();
        finish();
    }
}
