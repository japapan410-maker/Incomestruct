package com.helmi.incomestruct;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BerandaFragment extends Fragment {

    private TextView tvDailyAmount;
    private TextView tvGridPendapatan;
    private TextView tvGridPengeluaran;
    private TextView tvGridTabungan;
    private TextView tvGridDarurat;

    private EditText etNominal;
    private Button btnSimpan;

    private static final String PREF_NAME = "IncomeStructPrefs";

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_beranda,
                container,
                false
        );

        tvDailyAmount = view.findViewById(R.id.tv_daily_amount);
        tvGridPendapatan = view.findViewById(R.id.tv_grid_pendapatan);
        tvGridPengeluaran = view.findViewById(R.id.tv_grid_pengeluaran);
        tvGridTabungan = view.findViewById(R.id.tv_grid_tabungan);
        tvGridDarurat = view.findViewById(R.id.tv_grid_darurat);

        etNominal = view.findViewById(R.id.et_nominal);
        btnSimpan = view.findViewById(R.id.btn_simpan_transaksi);

        checkAndResetDailyData();
        loadDailyData();

        btnSimpan.setOnClickListener(v -> {
            String nominalStr = etNominal.getText().toString().trim();

            if (!nominalStr.isEmpty()) {
                try {
                    float nominal = Float.parseFloat(nominalStr);

                    if (nominal > 0) {
                        saveTransaction(nominal);
                    } else {
                        Toast.makeText(
                                getContext(),
                                "Nominal harus lebih dari 0",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                } catch (NumberFormatException e) {
                    Toast.makeText(
                            getContext(),
                            "Nominal tidak valid",
                            Toast.LENGTH_SHORT
                    ).show();
                }

            } else {
                Toast.makeText(
                        getContext(),
                        "Masukkan nominal terlebih dahulu",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        return view;
    }

    private void checkAndResetDailyData() {

        SharedPreferences prefs =
                requireActivity().getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE
                );

        String currentDate =
                new SimpleDateFormat(
                        "yyyyMMdd",
                        Locale.getDefault()
                ).format(new Date());

        String lastResetDate =
                prefs.getString("last_reset_date", "");

        if (!currentDate.equals(lastResetDate)) {

            SharedPreferences.Editor editor = prefs.edit();

            editor.putFloat("daily_pendapatan", 0f);
            editor.putFloat("daily_pengeluaran", 0f);
            editor.putFloat("daily_tabungan", 0f);
            editor.putFloat("daily_darurat", 0f);

            editor.putString(
                    "last_reset_date",
                    currentDate
            );

            editor.apply();
        }
    }

    private void loadDailyData() {

        SharedPreferences prefs =
                requireActivity().getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE
                );

        float totalIncome =
                prefs.getFloat("daily_pendapatan", 0f);

        float totalExpense =
                prefs.getFloat("daily_pengeluaran", 0f);

        float totalTabungan =
                prefs.getFloat("daily_tabungan", 0f);

        float totalDarurat =
                prefs.getFloat("daily_darurat", 0f);

        float netDaily =
                totalIncome - totalExpense;

        tvDailyAmount.setText(
                "Rp" + (int) netDaily
        );

        tvGridPendapatan.setText(
                "Rp" + (int) totalIncome
        );

        tvGridPengeluaran.setText(
                "Rp" + (int) totalExpense
        );

        tvGridTabungan.setText(
                "Rp" + (int) totalTabungan
        );

        tvGridDarurat.setText(
                "Rp" + (int) totalDarurat
        );
    }

    private void saveTransaction(float nominal) {

        SharedPreferences prefs =
                requireActivity().getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE
                );

        float currentIncome =
                prefs.getFloat(
                        "daily_pendapatan",
                        0f
                ) + nominal;

        SharedPreferences.Editor editor =
                prefs.edit();

        editor.putFloat(
                "daily_pendapatan",
                currentIncome
        );

        editor.apply();

        etNominal.setText("");

        loadDailyData();

        Toast.makeText(
                getContext(),
                "Cashflow harian berhasil dicatat!",
                Toast.LENGTH_SHORT
        ).show();
    }
}
