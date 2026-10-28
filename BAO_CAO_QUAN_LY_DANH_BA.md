# BÁO CÁO BÀI THỰC HÀNH

## Ứng dụng quản lý danh bạ liên hệ

| Thông tin | Nội dung |
|---|---|
| Nền tảng | Android |
| Ngôn ngữ | Java |
| Giao diện | XML View system |
| Thư viện giao diện | Material Components |
| Package | `com.example.contactmanager` |
| Dữ liệu | `ArrayList<Contact>` trong bộ nhớ |

> **Lưu ý khi nộp báo cáo:** Các ảnh dưới đây là vị trí chèn ảnh chụp từ emulator. Sau khi chụp giao diện thực tế, đặt file ảnh vào thư mục `images/` với đúng tên được ghi trong báo cáo. Không dùng ảnh thiết kế thay thế cho ảnh ứng dụng đang chạy.

## 1. Mục tiêu và phạm vi thực hiện

Ứng dụng được xây dựng để quản lý danh bạ liên hệ ở mức cơ bản. Các chức năng chính gồm:

- Đăng nhập bằng tài khoản cố định.
- Hiển thị danh sách liên hệ mẫu.
- Hiển thị số lượng liên hệ và số liên hệ yêu thích.
- Thêm liên hệ mới.
- Kiểm tra họ tên, định dạng số điện thoại và số điện thoại trùng.
- Xem chi tiết liên hệ.
- Đánh dấu hoặc bỏ đánh dấu yêu thích.
- Mở ứng dụng quay số bằng `Intent.ACTION_DIAL`.
- Xóa liên hệ sau khi xác nhận.

Ứng dụng không sử dụng SQLite, Room, Firebase hoặc API. Dữ liệu chỉ tồn tại trong `ArrayList<Contact>` trong thời gian ứng dụng đang chạy.

## 2. Cấu trúc mã nguồn chính

| Thành phần | File | Vai trò |
|---|---|---|
| Model | [`Contact.java`](app/src/main/java/com/example/contactmanager/model/Contact.java) | Lưu thông tin liên hệ, implements `Serializable` |
| Adapter | [`ContactAdapter.java`](app/src/main/java/com/example/contactmanager/ContactAdapter.java) | Đổ dữ liệu `Contact` vào `item_contact.xml` |
| Đăng nhập | [`LoginActivity.java`](app/src/main/java/com/example/contactmanager/LoginActivity.java) | Xử lý đăng nhập |
| Danh sách | [`MainActivity.java`](app/src/main/java/com/example/contactmanager/MainActivity.java) | Quản lý danh sách, thống kê, điều hướng |
| Thêm liên hệ | [`AddContactActivity.java`](app/src/main/java/com/example/contactmanager/AddContactActivity.java) | Nhập và kiểm tra dữ liệu |
| Chi tiết | [`ContactDetailActivity.java`](app/src/main/java/com/example/contactmanager/ContactDetailActivity.java) | Hiển thị, gọi điện, yêu thích và xóa |
| Manifest | [`AndroidManifest.xml`](app/src/main/AndroidManifest.xml) | Khai báo 4 Activity và khóa màn hình dọc |

`LoginActivity` được khai báo là màn hình `LAUNCHER`. Cả bốn Activity đều đặt `android:screenOrientation="portrait"`.

## 3. Mô tả giao diện và chức năng

### 3.1. Màn hình đăng nhập - LoginActivity

#### Hình ảnh

![Màn hình đăng nhập](images/login.png)

*Hình 1. Màn hình đăng nhập với các trường tên đăng nhập, mật khẩu và nút Đăng nhập.*

![Lỗi đăng nhập](images/login-error.png)

*Hình 2. Màn hình đăng nhập khi thông tin bỏ trống hoặc không chính xác.*

#### Mô tả nội dung đã làm

Giao diện được định nghĩa trong [`activity_login.xml`](app/src/main/res/layout/activity_login.xml), gồm:

- `EditText` có ID `edtUsername` để nhập tên đăng nhập.
- `EditText` có ID `edtPassword` để nhập mật khẩu, sử dụng `textPassword`.
- `TextView` có ID `tvError` để hiển thị thông báo lỗi.
- `Button` có ID `btnLogin` để thực hiện đăng nhập.
- Logo người dùng sử dụng vector drawable `ic_person`.

Trong [`LoginActivity.java`](app/src/main/java/com/example/contactmanager/LoginActivity.java), tài khoản được kiểm tra trực tiếp trong hàm `login()`:

```java
String username = edtUsername.getText().toString().trim();
String password = edtPassword.getText().toString().trim();

if (username.isEmpty() || password.isEmpty()) {
    showError(getString(R.string.login_empty_error));
    return;
}

if (!"admin".equals(username) || !"123456".equals(password)) {
    showError(getString(R.string.login_invalid_error));
    return;
}
```

Tài khoản hợp lệ là:

- Tên đăng nhập: `admin`
- Mật khẩu: `123456`

Khi đăng nhập thành công, ứng dụng chuyển sang `MainActivity` bằng `Intent` và gọi `finish()`:

```java
startActivity(new Intent(this, MainActivity.class));
finish();
```

Ngoài ra, `TextWatcher` được gắn cho hai ô nhập liệu. Khi người dùng gõ lại, `tvError` được ẩn để giao diện không còn hiển thị lỗi cũ.

---

### 3.2. Danh sách liên hệ - MainActivity

#### Hình ảnh

![Danh sách liên hệ](images/main-contacts.png)

*Hình 3. Danh sách ba liên hệ mẫu và khu vực thống kê tổng số, số yêu thích.*

#### Mô tả nội dung đã làm

Giao diện được định nghĩa trong [`activity_main.xml`](app/src/main/res/layout/activity_main.xml), gồm:

- `MaterialCardView` có ID `tvStats` làm khối thống kê.
- `TextView` `tvTotal` hiển thị tổng số liên hệ.
- `TextView` `tvFavorite` hiển thị số liên hệ yêu thích.
- `ListView` `lvContacts` hiển thị danh sách.
- `Button` `btnAdd` để mở màn hình thêm liên hệ.

`MainActivity` sử dụng:

```java
private final ArrayList<Contact> contacts = new ArrayList<>();
private ContactAdapter adapter;
```

Ba dữ liệu mẫu được tạo trong hàm `seedContacts()`:

```java
contacts.add(new Contact(
        "Nguyễn Văn An", "0901234567", "Bạn bè",
        "Nam", "Bạn học cũ", false));

contacts.add(new Contact(
        "Trần Thị Bình", "0912345678", "Gia đình",
        "Nữ", "Chị họ", true));

contacts.add(new Contact(
        "Lê Minh Châu", "0987654321", "Công việc",
        "Nam", "Đồng nghiệp", false));
```

`ContactAdapter` là lớp kế thừa `BaseAdapter`, sử dụng ViewHolder để ánh xạ các view trong [`item_contact.xml`](app/src/main/res/layout/item_contact.xml):

- `tvAvatar`: chữ cái đầu của từ cuối trong họ tên.
- `tvName`: họ tên.
- `tvPhone`: số điện thoại.
- `tvGroup`: nhóm liên hệ.
- `imgFavorite`: biểu tượng sao, `VISIBLE` khi liên hệ được yêu thích và `INVISIBLE` khi không yêu thích.

Ví dụ, tên `Nguyễn Văn An` được hiển thị avatar là `A`. Nếu tên rỗng, adapter sử dụng ký tự `?`.

Thống kê được cập nhật trong hàm `updateStats()`:

```java
tvTotal.setText(String.format(Locale.getDefault(), "%d", contacts.size()));
tvFavorite.setText(String.format(Locale.getDefault(), "%d", favorites));
```

Khi nhấn `btnAdd`, ứng dụng mở `AddContactActivity` và truyền danh sách các số điện thoại đang tồn tại qua extra `existing_phones`. Khi nhấn một dòng trong `ListView`, ứng dụng mở `ContactDetailActivity`, truyền đối tượng `Contact` và vị trí phần tử.

Việc nhận kết quả từ hai màn hình con được thực hiện bằng Activity Result API:

```java
registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(), ...
);
```

Sau khi thêm, xóa hoặc cập nhật trạng thái yêu thích, adapter được gọi `notifyDataSetChanged()` và hàm `updateStats()` được gọi lại.

---

### 3.3. Màn hình thêm liên hệ - AddContactActivity

#### Hình ảnh

![Màn hình thêm liên hệ](images/add-contact.png)

*Hình 4. Màn hình nhập thông tin liên hệ mới.*

![Lỗi nhập liên hệ](images/add-contact-error.png)

*Hình 5. Màn hình hiển thị lỗi khi họ tên rỗng, số điện thoại sai hoặc bị trùng.*

![Thêm liên hệ thành công](images/add-contact-success.png)

*Hình 6. Danh sách sau khi lưu liên hệ mới thành công.*

#### Mô tả nội dung đã làm

Giao diện được định nghĩa trong [`activity_add_contact.xml`](app/src/main/res/layout/activity_add_contact.xml) và được bọc bởi `ScrollView` để không bị che khi bàn phím xuất hiện.

Các thành phần đã thêm:

- `EditText` `edtName`: nhập họ và tên.
- `EditText` `edtPhone`: nhập số điện thoại, giới hạn 10 ký tự.
- `Spinner` `spnGroup`: chọn nhóm liên hệ.
- `RadioGroup` `rgGender` gồm `rbMale`, `rbFemale`, `rbOther`.
- `CheckBox` `chkFavorite`: thêm vào yêu thích.
- `EditText` `edtNote`: nhập ghi chú nhiều dòng.
- `Button` `btnSave`: lưu liên hệ.
- `ImageButton` `btnBack`: quay lại màn hình trước.

Spinner được cấu hình trong `onCreate()` bằng `ArrayAdapter` với các nhóm:

- Gia đình
- Bạn bè
- Công việc
- Khác

Hai layout [`spinner_item.xml`](app/src/main/res/layout/spinner_item.xml) và [`spinner_dropdown_item.xml`](app/src/main/res/layout/spinner_dropdown_item.xml) được dùng để hiển thị chữ màu `green_900`.

Hàm `saveContact()` kiểm tra dữ liệu theo thứ tự:

```java
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
```

Khi dữ liệu hợp lệ, thông tin được dùng để tạo một đối tượng `Contact`:

```java
Contact contact = new Contact(
        name, phone, group, gender, note,
        chkFavorite.isChecked());
```

Đối tượng được trả về cho `MainActivity` bằng extra `new_contact`. Sau khi lưu, ứng dụng hiển thị Toast `Đã thêm liên hệ`.

---

### 3.4. Chi tiết liên hệ - ContactDetailActivity

#### Hình ảnh

![Chi tiết liên hệ](images/contact-detail.png)

*Hình 7. Màn hình hiển thị chi tiết một liên hệ.*

![Màn hình quay số](images/dialer.png)

*Hình 8. Màn hình quay số hệ thống sau khi nhấn nút Gọi điện.*

![Xác nhận xóa liên hệ](images/delete-dialog.png)

*Hình 9. Hộp thoại xác nhận trước khi xóa liên hệ.*

#### Mô tả nội dung đã làm

Giao diện được định nghĩa trong [`activity_contact_detail.xml`](app/src/main/res/layout/activity_contact_detail.xml), gồm:

- `tvAvatar`: avatar chữ cái đầu.
- `tvName`: tên liên hệ.
- `tvPhone`: số điện thoại.
- `tvGroupGender`: nhóm và giới tính.
- `tvNote`: ghi chú.
- `chkFavorite`: trạng thái yêu thích.
- `btnCall`: gọi điện.
- `btnBack`: quay lại.
- `btnDelete`: xóa liên hệ.

Trong `onCreate()`, Activity nhận đối tượng `Contact` từ Intent:

```java
Object value = getIntent().getSerializableExtra("contact");
if (!(value instanceof Contact)) {
    finish();
    return;
}

contact = (Contact) value;
position = getIntent().getIntExtra("position", -1);
```

Nếu dữ liệu không tồn tại hoặc vị trí không hợp lệ, Activity kết thúc an toàn bằng `finish()` để tránh crash.

Hàm `bindContact()` gán dữ liệu lên giao diện. Khi ghi chú rỗng, nội dung hiển thị là `Không có ghi chú`.

Khi người dùng thay đổi `chkFavorite`, trạng thái của đối tượng được cập nhật:

```java
chkFavorite.setOnCheckedChangeListener(
        (buttonView, isChecked) -> contact.setFavorite(isChecked));
```

Nút `btnCall` sử dụng `Intent.ACTION_DIAL`, không yêu cầu quyền `CALL_PHONE`:

```java
startActivity(new Intent(
        Intent.ACTION_DIAL,
        Uri.parse("tel:" + contact.getPhone())));
```

Nút `btnBack` và nút Back hệ thống gọi `finishWithUpdate()`. Hàm này trả về:

- `action = "update"`
- `position`
- `favorite`

`MainActivity` nhận kết quả và cập nhật lại danh sách cũng như thống kê.

Nút `btnDelete` hiển thị `AlertDialog` với nội dung `Xóa liên hệ này?`. Nếu người dùng chọn Xóa, Activity trả về:

```java
result.putExtra("action", "delete");
result.putExtra("position", position);
```

Sau đó `MainActivity` xóa phần tử tương ứng khỏi `ArrayList`, gọi `notifyDataSetChanged()` và cập nhật thống kê.

## 4. Luồng dữ liệu và điều hướng

```text
LoginActivity
    |
    | Đăng nhập đúng: admin / 123456
    v
MainActivity
    |-- btnAdd + existing_phones
    v
AddContactActivity
    |-- new_contact
    v
MainActivity: thêm Contact, cập nhật adapter và thống kê

MainActivity
    |-- Chọn một dòng: contact + position
    v
ContactDetailActivity
    |-- action = update / delete
    v
MainActivity: cập nhật yêu thích hoặc xóa Contact
```

Các extra chính được sử dụng:

| Extra | Hướng truyền | Ý nghĩa |
|---|---|---|
| `existing_phones` | `MainActivity` → `AddContactActivity` | Danh sách số điện thoại đã có |
| `new_contact` | `AddContactActivity` → `MainActivity` | Liên hệ vừa tạo |
| `contact` | `MainActivity` → `ContactDetailActivity` | Liên hệ cần xem |
| `position` | Hai chiều | Vị trí liên hệ trong danh sách |
| `action` | `ContactDetailActivity` → `MainActivity` | `update` hoặc `delete` |
| `favorite` | `ContactDetailActivity` → `MainActivity` | Trạng thái yêu thích mới |

## 5. Kiểm thử chức năng

| Chức năng | Kịch bản kiểm thử | Kết quả mong đợi |
|---|---|---|
| Đăng nhập bỏ trống | Nhấn Đăng nhập khi chưa nhập dữ liệu | Hiển thị `Vui lòng nhập đầy đủ thông tin` |
| Đăng nhập sai | Nhập sai username hoặc password | Hiển thị `Sai tên đăng nhập hoặc mật khẩu` |
| Đăng nhập đúng | Nhập `admin` và `123456` | Mở `MainActivity`, Back không quay lại Login |
| Danh sách ban đầu | Đăng nhập thành công | Hiển thị 3 liên hệ, tổng 3, yêu thích 1 |
| Tên rỗng | Trong màn hình thêm, để trống họ tên | `edtName` hiển thị lỗi |
| Số điện thoại sai | Nhập số không đủ 10 chữ số hoặc không bắt đầu bằng 0 | `edtPhone` hiển thị lỗi định dạng |
| Số điện thoại trùng | Nhập `0901234567` | Hiển thị `Số điện thoại đã tồn tại` |
| Thêm thành công | Nhập dữ liệu hợp lệ rồi lưu | Quay lại danh sách, số lượng tăng |
| Đổi yêu thích | Vào chi tiết, bật/tắt Yêu thích rồi quay lại | Sao và thống kê được cập nhật |
| Gọi điện | Nhấn Gọi điện | Mở ứng dụng quay số với đúng số điện thoại |
| Xóa | Nhấn Xóa liên hệ và xác nhận | Liên hệ bị xóa, thống kê giảm |

## 6. Kết luận

Ứng dụng đã hoàn thiện các chức năng cơ bản của bài thực hành quản lý danh bạ bằng Java và XML. Giao diện sử dụng Material Components, dữ liệu được quản lý trong bộ nhớ, các màn hình trao đổi dữ liệu bằng Intent và Activity Result API. Các trường hợp dữ liệu thiếu, sai định dạng, trùng số điện thoại và Intent thiếu dữ liệu đều được kiểm tra để hạn chế lỗi khi chạy.

Build debug đã được kiểm tra bằng:

```powershell
.\gradlew.bat assembleDebug --no-daemon
```

Kết quả build: `BUILD SUCCESSFUL`.
