package com.helmi.incomestruct;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends Activity {
    private SharedPreferences pref;
    private LinearLayout containerContent;
    private int currentTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getSharedPreferences("incomestruct_pref", Context.MODE_PRIVATE);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#0B0E14"));

        containerContent = new LinearLayout(this);
        containerContent.setOrientation(LinearLayout.VERTICAL);
        
        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(containerContent);
        
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f);
        scrollView.setLayoutParams(scrollParams);
        root.addView(scrollView);

        LinearLayout bottomNav = createBottomNav();
        root.addView(bottomNav);

        setContentView(root);
        renderTab(0);
    }

    private LinearLayout createBottomNav() {
        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setBackgroundColor(Color.parseColor("#121721"));
        nav.setPadding(10, 28, 10, 28);

        String[] tabs = {"BERANDA", "TRANSAKSI", "REKAP", "PENGATURAN"};
        for (int i = 0; i < tabs.length; i++) {
            final int tabIndex = i;
            TextView item = new TextView(this);
            item.setText(tabs[i]);
            item.setTextSize(12);
            item.setGravity(Gravity.CENTER);
            item.setTypeface(Typeface.DEFAULT_BOLD);
            item.setTextColor(i == currentTab ? Color.parseColor("#F59E0B") : Color.parseColor("#94A3B8"));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            item.setLayoutParams(params);
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    renderTab(tabIndex);
                }
            });
            nav.addView(item);
        }
        return nav;
    }

    private void renderTab(int tabIndex) {
        currentTab = tabIndex;
        containerContent.removeAllViews();
        containerContent.setPadding(40, 50, 40, 50);

        if (tabIndex == 0) renderBeranda();
        else if (tabIndex == 1) renderTransaksi();
        else if (tabIndex == 2) renderRekap();
        else renderPengaturan();
    }

    private void renderBeranda() {
        TextView tvSub = new TextView(this);
        tvSub.setText("KEUANGAN PRIBADI • OFFLINE");
        tvSub.setTextSize(11);
        tvSub.setTypeface(Typeface.DEFAULT_BOLD);
        tvSub.setTextColor(Color.parseColor("#F59E0B"));

        TextView tvTitle = new TextView(this);
        tvTitle.setText("HALO, SIAP MENGATUR UANG?");
        tvTitle.setTextSize(22);
        tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setPadding(0, 8, 0, 35);

        containerContent.addView(tvSub);
        containerContent.addView(tvTitle);

        LinearLayout cardSaldo = createCard("#F59E0B", "#F59E0B");
        TextView labelSaldo = new TextView(this);
        labelSaldo.setText("SALDO / SISA UANG");
        labelSaldo.setTextSize(12);
        labelSaldo.setTypeface(Typeface.DEFAULT_BOLD);
        labelSaldo.setTextColor(Color.parseColor("#78350F"));

        long in = pref.getLong("in", 0);
        long out = pref.getLong("out", 0);
        long tab = pref.getLong("tabungan", 0);
        long emg = pref.getLong("darurat", 0);
        long saldo = in - out - tab - emg;

        TextView tvSaldo = new TextView(this);
        tvSaldo.setText(formatRupiah(saldo));
        tvSaldo.setTextSize(34);
        tvSaldo.setTypeface(Typeface.DEFAULT_BOLD);
        tvSaldo.setTextColor(Color.parseColor("#0F172A"));
        tvSaldo.setPadding(0, 10, 0, 5);

        TextView tvSubSaldo = new TextView(this);
        tvSubSaldo.setText("Bisa digunakan saat ini");
        tvSubSaldo.setTextSize(12);
        tvSubSaldo.setTextColor(Color.parseColor("#92400E"));

        cardSaldo.addView(labelSaldo);
        cardSaldo.addView(tvSaldo);
        cardSaldo.addView(tvSubSaldo);
        containerContent.addView(cardSaldo);

        LinearLayout row1 = new LinearLayout(this);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        row1.addView(createSmallCard("PENDAPATAN", formatRupiah(in), "#10B981"));
        row1.addView(createSmallCard("PENGELUARAN", formatRupiah(out), "#EF4444"));
        containerContent.addView(row1);

        LinearLayout row2 = new LinearLayout(this);
        row2.setOrientation(LinearLayout.HORIZONTAL);
        row2.addView(createSmallCard("TABUNGAN", formatRupiah(tab), "#3B82F6"));
        row2.addView(createSmallCard("DANA DARURAT", formatRupiah(emg), "#F59E0B"));
        containerContent.addView(row2);

        TextView tvCatatLabel = new TextView(this);
        tvCatatLabel.setText("CATAT TRANSAKSI");
        tvCatatLabel.setTextSize(16);
        tvCatatLabel.setTypeface(Typeface.DEFAULT_BOLD);
        tvCatatLabel.setTextColor(Color.WHITE);
        tvCatatLabel.setPadding(0, 30, 0, 15);
        containerContent.addView(tvCatatLabel);

        LinearLayout btnRow = new LinearLayout(this);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);

        Button btnIn = createQuickBtn("+ PENDAPATAN", "#1E293B");
        btnIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFormDialog("in");
            }
        });

        Button btnOut = createQuickBtn("- PENGELUARAN", "#1E293B");
        btnOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFormDialog("out");
            }
        });

        Button btnTab = createQuickBtn("+ TABUNGAN", "#1E293B");
        btnTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFormDialog("tabungan");
            }
        });

        btnRow.addView(btnIn);
        btnRow.addView(btnOut);
        btnRow.addView(btnTab);
        containerContent.addView(btnRow);

        TextView tvTarget = new TextView(this);
        tvTarget.setText("TARGET BERJALAN");
        tvTarget.setTextSize(16);
        tvTarget.setTypeface(Typeface.DEFAULT_BOLD);
        tvTarget.setTextColor(Color.WHITE);
        tvTarget.setPadding(0, 35, 0, 15);
        containerContent.addView(tvTarget);

        containerContent.addView(createProgressCard("TABUNGAN", tab, 10000000, "#3B82F6"));
        containerContent.addView(createProgressCard("DANA DARURAT", emg, 15000000, "#F59E0B"));
    }

    private void renderTransaksi() {
        TextView tvSub = new TextView(this);
        tvSub.setText("LEDGER OFFLINE");
        tvSub.setTextSize(11);
        tvSub.setTypeface(Typeface.DEFAULT_BOLD);
        tvSub.setTextColor(Color.parseColor("#F59E0B"));

        TextView tvTitle = new TextView(this);
        tvTitle.setText("TRANSAKSI");
        tvTitle.setTextSize(24);
        tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setPadding(0, 5, 0, 20);

        containerContent.addView(tvSub);
        containerContent.addView(tvTitle);

        String logs = pref.getString("logs", "Belum ada catatan transaksi tersimpan.");
        LinearLayout cardHist = createCard("#161B22", "#21262D");
        TextView tvLogs = new TextView(this);
        tvLogs.setText(logs);
        tvLogs.setTextColor(Color.parseColor("#E2E8F0"));
        tvLogs.setTextSize(14);
        tvLogs.setLineSpacing(12, 1.2f);
        cardHist.addView(tvLogs);
        containerContent.addView(cardHist);
    }

    private void renderRekap() {
        TextView tvSub = new TextView(this);
        tvSub.setText("ANALISIS KEUANGAN");
        tvSub.setTextSize(11);
        tvSub.setTypeface(Typeface.DEFAULT_BOLD);
        tvSub.setTextColor(Color.parseColor("#F59E0B"));

        TextView tvTitle = new TextView(this);
        tvTitle.setText("REKAP");
        tvTitle.setTextSize(24);
        tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setPadding(0, 5, 0, 20);

        containerContent.addView(tvSub);
        containerContent.addView(tvTitle);

        long in = pref.getLong("in", 0);
        long out = pref.getLong("out", 0);

        LinearLayout cardStat = createCard("#161B22", "#21262D");
        TextView tvStatIn = new TextView(this);
        tvStatIn.setText("TOTAL PENDAPATAN: " + formatRupiah(in));
        tvStatIn.setTextColor(Color.parseColor("#10B981"));
        tvStatIn.setTextSize(15);
        tvStatIn.setTypeface(Typeface.DEFAULT_BOLD);

        TextView tvStatOut = new TextView(this);
        tvStatOut.setText("TOTAL PENGELUARAN: " + formatRupiah(out));
        tvStatOut.setTextColor(Color.parseColor("#EF4444"));
        tvStatOut.setTextSize(15);
        tvStatOut.setTypeface(Typeface.DEFAULT_BOLD);
        tvStatOut.setPadding(0, 15, 0, 0);

        cardStat.addView(tvStatIn);
        cardStat.addView(tvStatOut);
        containerContent.addView(cardStat);
    }

    private void renderPengaturan() {
        TextView tvTitle = new TextView(this);
        tvTitle.setText("PENGATURAN");
        tvTitle.setTextSize(24);
        tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setPadding(0, 0, 0, 25);
        containerContent.addView(tvTitle);

        Button btnReset = createQuickBtn("RESET SEMUA DATA", "#DC2626");
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pref.edit().clear().apply();
                Toast.makeText(MainActivity.this, "Data berhasil dibersihkan", Toast.LENGTH_SHORT).show();
                renderTab(0);
            }
        });
        containerContent.addView(btnReset);

        TextView tvAuthor = new TextView(this);
        tvAuthor.setText("\nINCOMESTRUCT v1.0\nCREATED BY HELMI ZAINUL PAHMI");
        tvAuthor.setTextColor(Color.parseColor("#F59E0B"));
        tvAuthor.setTextSize(14);
        tvAuthor.setTypeface(Typeface.DEFAULT_BOLD);
        tvAuthor.setGravity(Gravity.CENTER_HORIZONTAL);
        tvAuthor.setPadding(0, 50, 0, 0);
        containerContent.addView(tvAuthor);
    }

    private LinearLayout createCard(String bgColor, String borderColor) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(40, 40, 40, 40);
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(bgColor));
        gd.setCornerRadius(24);
        gd.setStroke(2, Color.parseColor(borderColor));
        card.setBackground(gd);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 24);
        card.setLayoutParams(params);
        return card;
    }

    private View createSmallCard(String title, String val, String colorHex) {
        LinearLayout card = createCard("#161B22", "#21262D");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        params.setMargins(8, 0, 8, 16);
        card.setLayoutParams(params);

        TextView label = new TextView(this);
        label.setText(title);
        label.setTextSize(11);
        label.setTextColor(Color.parseColor("#94A3B8"));

        TextView valueTv = new TextView(this);
        valueTv.setText(val);
        valueTv.setTextSize(15);
        valueTv.setTypeface(Typeface.DEFAULT_BOLD);
        valueTv.setTextColor(Color.parseColor(colorHex));
        valueTv.setPadding(0, 8, 0, 0);

        card.addView(label);
        card.addView(valueTv);
        return card;
    }

    private View createProgressCard(String title, long val, long max, String colorHex) {
        LinearLayout card = createCard("#161B22", "#21262D");
        
        TextView label = new TextView(this);
        label.setText(title);
        label.setTextSize(14);
        label.setTypeface(Typeface.DEFAULT_BOLD);
        label.setTextColor(Color.WHITE);

        ProgressBar pb = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        pb.setMax((int) max);
        pb.setProgress((int) val);
        pb.setPadding(0, 15, 0, 15);

        TextView tvVal = new TextView(this);
        tvVal.setText(formatRupiah(val) + " DARI " + formatRupiah(max));
        tvVal.setTextSize(12);
        tvVal.setTextColor(Color.parseColor("#94A3B8"));

        card.addView(label);
        card.addView(pb);
        card.addView(tvVal);
        return card;
    }

    private Button createQuickBtn(String text, String colorHex) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(12);
        btn.setTypeface(Typeface.DEFAULT_BOLD);
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(colorHex));
        gd.setCornerRadius(16);
        btn.setBackground(gd);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        params.setMargins(6, 6, 6, 6);
        btn.setLayoutParams(params);
        return btn;
    }

    private String formatRupiah(long amount) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat format = NumberFormat.getCurrencyInstance(localeID);
        return format.format(amount).replace(",00", "");
    }

    private void showFormDialog(final String type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("CATAT " + type.toUpperCase());

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        final EditText inputJumlah = new EditText(this);
        inputJumlah.setHint("Nominal (Rp)");
        inputJumlah.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputJumlah);

        final EditText inputKet = new EditText(this);
        inputKet.setHint("Keterangan");
        layout.addView(inputKet);

        builder.setView(layout);

        builder.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nom = inputJumlah.getText().toString();
                String ket = inputKet.getText().toString();
                if (!nom.isEmpty()) {
                    long val = Long.parseLong(nom);
                    long current = pref.getLong(type, 0);
                    pref.edit().putLong(type, current + val).apply();

                    String logs = pref.getString("logs", "");
                    String tag = type.equals("in") ? "[+] " : "[-] ";
                    logs = tag + formatRupiah(val) + " — " + (ket.isEmpty() ? "Catatan" : ket) + "\n" + logs;
                    pref.edit().putString("logs", logs).apply();

                    renderTab(0);
                }
            }
        });
        builder.setNegativeButton("Batal", null);
        builder.show();
    }
}
