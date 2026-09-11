package com.helmi.incomestruct;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView txtCashflowHarian, txtPendapatan, txtPengeluaran, txtTabungan, txtDanaDarurat;
    private EditText etJumlah, etKeterangan;
    private Button btnSimpan, btnPendapatan, btnPengeluaran, btnTabungan, btnDanaDarurat;
    private String tipeTerpilih = "Pendapatan";
    private ArrayList<Transaction> listTransaksi;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtCashflowHarian = findViewById(R.id.txtCashflowHarian);
        txtPendapatan = findViewById(R.id.txtPendapatan);
        txtPengeluaran = findViewById(R.id.txtPengeluaran);
        txtTabungan = findViewById(R.id.txtTabungan);
        txtDanaDarurat = findViewById(R.id.txtDanaDarurat);

        etJumlah = findViewById(R.id.etJumlah);
        etKeterangan = findViewById(R.id.etKeterangan);
        btnSimpan = findViewById(R.id.btnSimpan);

        btnPendapatan = findViewById(R.id.btnPendapatan);
        btnPengeluaran = findViewById(R.id.btnPengeluaran);
        btnTabungan = findViewById(R.id.btnTabungan);
        btnDanaDarurat = findViewById(R.id.btnDanaDarurat);

        sharedPreferences = getSharedPreferences("IncomeStructPrefs", MODE_PRIVATE);
        listTransaksi = new ArrayList<>();
        muatDataTransaksi();

        btnPendapatan.setOnClickListener(v -> {
            tipeTerpilih = "Pendapatan";
            perbaruiWarnaTombolKategori();
        });

        btnPengeluaran.setOnClickListener(v -> {
            tipeTerpilih = "Pengeluaran";
            perbaruiWarnaTombolKategori();
        });

        btnTabungan.setOnClickListener(v -> {
            tipeTerpilih = "Tabungan";
            perbaruiWarnaTombolKategori();
        });

        btnDanaDarurat.setOnClickListener(v -> {
            tipeTerpilih = "Dana Darurat";
            perbaruiWarnaTombolKategori();
        });

        btnSimpan.setOnClickListener(v -> simpanTransaksiBaru());

        // Navigasi Bawah
        findViewById(R.id.navBeranda).setOnClickListener(v -> {
            // Sudah di beranda
        });

        findViewById(R.id.navSaldo).setOnClickListener(v -> {
            // Logika menu saldo jika ada
        });

        findViewById(R.id.navTransaksi).setOnClickListener(v -> {
            // Logika menu riwayat transaksi
        });

        findViewById(R.id.navRekap).setOnClickListener(v -> {
            // Logika menu rekap
        });

        findViewById(R.id.navPengaturan).setOnClickListener(v -> {
            // Logika menu pengaturan
        });
    }

    private void perbaruiWarnaTombolKategori() {
        // Reset atau ubah gaya tombol aktif di sini jika diperlukan
    }

    private void simpanTransaksiBaru() {
        String jumlahStr = etJumlah.getText().toString().trim();
        String keterangan = etKeterangan.getText().toString().trim();

        if (jumlahStr.isEmpty()) {
            Toast.makeText(this, "Masukkan jumlah nominal!", Toast.LENGTH_SHORT).show();
            return;
        }

        double jumlah = Double.parseDouble(jumlahStr);
        String tanggalHariIni = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());

        Transaction t = new Transaction(keterangan, tanggalHariIni, jumlah, tipeTerpilih);
        listTransaksi.add(t);
        simpanDataKeSharedPreferences();
        perbaruiTampilanKeuangan();

        etJumlah.setText("");
        etKeterangan.setText("");
        Toast.makeText(this, "Transaksi berhasil disimpan", Toast.LENGTH_SHORT).show();
    }

    private void perbaruiTampilanKeuangan() {
        String tanggalHariIniSaja = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        double totalHarian = 0;
        double totalPendapatan = 0;
        double totalPengeluaran = 0;
        double totalTabungan = 0;
        double totalDanaDarurat = 0;

        for (Transaction t : listTransaksi) {
            // Tab 1 (Cashflow Harian): Hanya menghitung transaksi yang tanggalnya cocok dengan hari ini
            if (t.getTanggal() != null && t.getTanggal().startsWith(tanggalHariIniSaja)) {
                if (t.getTipe().equalsIgnoreCase("Pendapatan")) {
                    totalHarian += t.getJumlah();
                } else if (t.getTipe().equalsIgnoreCase("Pengeluaran")) {
                    totalHarian -= t.getJumlah();
                }
            }

            // Tab 2 & Lainnya: Akumulasi keseluruhan kategori
            if (t.getTipe().equalsIgnoreCase("Pendapatan")) {
                totalPendapatan += t.getJumlah();
            } else if (t.getTipe().equalsIgnoreCase("Pengeluaran")) {
                totalPengeluaran += t.getJumlah();
            } else if (t.getTipe().equalsIgnoreCase("Tabungan")) {
                totalTabungan += t.getJumlah();
            } else if (t.getTipe().equalsIgnoreCase("Dana Darurat")) {
                totalDanaDarurat += t.getJumlah();
            }
        }

        txtCashflowHarian.setText("Rp" + String.format(Locale.GERMAN, "%.0f", totalHarian));
        txtPendapatan.setText("Rp" + String.format(Locale.GERMAN, "%.0f", totalPendapatan));
        txtPengeluaran.setText("Rp" + String.format(Locale.GERMAN, "%.0f", totalPengeluaran));
        txtTabungan.setText("Rp" + String.format(Locale.GERMAN, "%.0f", totalTabungan));
        txtDanaDarurat.setText("Rp" + String.format(Locale.GERMAN, "%.0f", totalDanaDarurat));
    }

    private void simpanDataKeSharedPreferences() {
        try {
            JSONArray jsonArray = new JSONArray();
            for (Transaction t : listTransaksi) {
                JSONObject obj = new JSONObject();
                obj.put("keterangan", t.getKeterangan());
                obj.put("tanggal", t.getTanggal());
                obj.put("jumlah", t.getJumlah());
                obj.put("tipe", t.getTipe());
                jsonArray.put(obj);
            }
            sharedPreferences.edit().putString("transaksi_list", jsonArray.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void muatDataTransaksi() {
        listTransaksi.clear();
        String jsonStr = sharedPreferences.getString("transaksi_list", null);
        if (jsonStr != null) {
            try {
                JSONArray jsonArray = new JSONArray(jsonStr);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    String ket = obj.getString("keterangan");
                    String tgl = obj.getString("tanggal");
                    double jml = obj.getDouble("jumlah");
                    String tp = obj.getString("tipe");
                    listTransaksi.add(new Transaction(ket, tgl, jml, tp));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        perbaruiTampilanKeuangan();
    }
}

class Transaction {
    private String keterangan, tanggal, tipe;
    private double jumlah;

    public Transaction(String keterangan, String tanggal, double jumlah, String tipe) {
        this.keterangan = keterangan;
        this.tanggal = tanggal;
        this.jumlah = jumlah;
        this.tipe = tipe;
    }

    public String getKeterangan() { return keterangan; }
    public String getTanggal() { return tanggal; }
    public double getJumlah() { return jumlah; }
    public String getTipe() { return tipe; }
}
