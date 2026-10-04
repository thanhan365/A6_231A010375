package vn.edu.vhu.ltdd.a6controls;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // TODO: thay 2201234567 bằng MSSV của bạn
    private static final String TAG = "A6_2201234567";

    public static final String EXTRA_TOM_TAT = "extra_tom_tat";

    private EditText edtHoTen, edtMssv;
    private Spinner spKhoa, spMonHoc;
    private RadioGroup rgHeDaoTao;
    private CheckBox cbSang, cbChieu, cbToi;
    private TextView tvSoBuoiChon;
    private MaterialSwitch swThongBao;
    private ToggleButton tgUuTien;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        edtHoTen = findViewById(R.id.edtHoTen);
        edtMssv = findViewById(R.id.edtMssv);
        spKhoa = findViewById(R.id.spKhoa);
        spMonHoc = findViewById(R.id.spMonHoc);
        rgHeDaoTao = findViewById(R.id.rgHeDaoTao);
        cbSang = findViewById(R.id.cbSang);
        cbChieu = findViewById(R.id.cbChieu);
        cbToi = findViewById(R.id.cbToi);
        tvSoBuoiChon = findViewById(R.id.tvSoBuoiChon);
        swThongBao = findViewById(R.id.swThongBao);
        tgUuTien = findViewById(R.id.tgUuTien);
        Button btnXacNhan = findViewById(R.id.btnXacNhan);
        Button btnLamLai = findViewById(R.id.btnLamLai);

        // ==== BÀI NÂNG CAO NC1: Spinner phụ thuộc (Khoa -> Học phần) ====
        ArrayAdapter<CharSequence> adapterKhoa = ArrayAdapter.createFromResource(
                this, R.array.danh_sach_khoa, android.R.layout.simple_spinner_item);
        adapterKhoa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spKhoa.setAdapter(adapterKhoa);

        spKhoa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int arrayResId;
                switch (position) {
                    case 1:
                        arrayResId = R.array.mon_hoc_kinh_te;
                        break;
                    case 2:
                        arrayResId = R.array.mon_hoc_ngoai_ngu;
                        break;
                    case 0:
                    default:
                        arrayResId = R.array.mon_hoc_cntt;
                        break;
                }
                ArrayAdapter<CharSequence> adapterMonHoc = ArrayAdapter.createFromResource(
                        MainActivity.this, arrayResId, android.R.layout.simple_spinner_item);
                adapterMonHoc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spMonHoc.setAdapter(adapterMonHoc);
                Log.d(TAG, "Đã chọn Khoa: " + parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spMonHoc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Đã chọn học phần: " + parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // ==== BÀI NÂNG CAO NC2: Dùng chung 1 OnCheckedChangeListener để đếm số buổi ====
        CompoundButton.OnCheckedChangeListener buoiListener = (buttonView, isChecked) -> {
            int count = 0;
            if (cbSang.isChecked()) count++;
            if (cbChieu.isChecked()) count++;
            if (cbToi.isChecked()) count++;
            tvSoBuoiChon.setText(getString(R.string.so_buoi_format, count));
            Log.d(TAG, buttonView.getText() + ": " + (isChecked ? "chọn" : "bỏ chọn") + " -> Tổng số buổi: " + count);
        };

        cbSang.setOnCheckedChangeListener(buoiListener);
        cbChieu.setOnCheckedChangeListener(buoiListener);
        cbToi.setOnCheckedChangeListener(buoiListener);

        // ---- RadioGroup: chỉ chọn được một ----
        rgHeDaoTao.setOnCheckedChangeListener((group, checkedId) -> {
            String he = (checkedId == R.id.rbChinhQuy) ? getString(R.string.he_chinh_quy)
                    : getString(R.string.he_vlvh);
            Log.d(TAG, "Hệ đào tạo: " + he);
        });

        // ---- Switch ----
        swThongBao.setOnCheckedChangeListener((buttonView, isChecked) ->
                Log.d(TAG, "Nhận thông báo: " + isChecked));

        btnXacNhan.setOnClickListener(v -> xacNhan());
        btnLamLai.setOnClickListener(v -> lamLai());
    }

    /** Kiểm tra dữ liệu rồi chuyển sang màn hình xác nhận. */
    private void xacNhan() {
        String hoTen = edtHoTen.getText().toString().trim();
        String mssv = edtMssv.getText().toString().trim();

        if (hoTen.isEmpty()) {
            edtHoTen.setError(getString(R.string.err_empty));
            edtHoTen.requestFocus();
            return;
        }
        if (mssv.length() != 10) {
            edtMssv.setError(getString(R.string.err_mssv));
            edtMssv.requestFocus();
            return;
        }
        if (rgHeDaoTao.getCheckedRadioButtonId() == -1) {   // -1 = chưa chọn nút nào
            Toast.makeText(this, R.string.err_he, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> buoiHoc = new ArrayList<>();
        if (cbSang.isChecked()) buoiHoc.add(getString(R.string.buoi_sang));
        if (cbChieu.isChecked()) buoiHoc.add(getString(R.string.buoi_chieu));
        if (cbToi.isChecked()) buoiHoc.add(getString(R.string.buoi_toi));
        if (buoiHoc.isEmpty()) {
            Toast.makeText(this, R.string.err_buoi, Toast.LENGTH_SHORT).show();
            return;
        }

        String he = (rgHeDaoTao.getCheckedRadioButtonId() == R.id.rbChinhQuy)
                ? getString(R.string.he_chinh_quy) : getString(R.string.he_vlvh);

        String monHocVoiKhoa = spMonHoc.getSelectedItem().toString() + " (" + spKhoa.getSelectedItem().toString() + ")";

        String tomTat = getString(R.string.tom_tat_format,
                hoTen,
                mssv,
                monHocVoiKhoa,
                he,
                TextUtils.join(", ", buoiHoc),  // String.join() cần API 26 nên dùng TextUtils
                swThongBao.isChecked() ? getString(R.string.co) : getString(R.string.khong),
                tgUuTien.isChecked() ? getString(R.string.bat) : getString(R.string.tat));

        Intent intent = new Intent(this, ConfirmActivity.class);
        intent.putExtra(EXTRA_TOM_TAT, tomTat);
        startActivity(intent);
    }

    /** Đưa mọi control về trạng thái ban đầu. */
    private void lamLai() {
        edtHoTen.setText("");
        edtMssv.setText("");
        edtHoTen.setError(null);
        edtMssv.setError(null);
        spKhoa.setSelection(0);
        spMonHoc.setSelection(0);
        rgHeDaoTao.clearCheck();
        cbSang.setChecked(false);
        cbChieu.setChecked(false);
        cbToi.setChecked(false);
        tvSoBuoiChon.setText(getString(R.string.so_buoi_format, 0));
        swThongBao.setChecked(true);
        tgUuTien.setChecked(false);
        edtHoTen.requestFocus();
    }
}