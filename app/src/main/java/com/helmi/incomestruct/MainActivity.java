package com.helmi.incomestruct;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {
    private SharedPreferences pref;
    private LinearLayout containerContent;
    private int currentTab = 0;
    private String searchQuery = "";
    private String selectedType = "in"; 
    private String periodFilter = "Bulanan";

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
        scrollView.setFillViewport(true);
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

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private LinearLayout createBottomNav() {
        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setBackgroundColor(Color.parseColor("#0B0E14"));
        nav.setPadding(0, dpToPx(12), 0, dpToPx(16));

        String[] tabs = {"Beranda", "Transaksi", "Rekap", "Pengaturan"};
        String[] textIcons = {"⌂", "⇆", "📊", "⚙"}; 

        for (int i = 0; i < tabs.length; i++) {
            final int tabIndex = i;
            LinearLayout itemLayout = new LinearLayout(this);
            itemLayout.setOrientation(LinearLayout.VERTICAL);
            itemLayout.setGravity(Gravity.CENTER);

            TextView tvIcon = new TextView(this);
            tvIcon.setText(textIcons[i]);
            tvIcon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            tvIcon.setGravity(Gravity.CENTER);
            tvIcon.setTextColor(i == currentTab ? Color.parseColor("#F59E0B") : Color.parseColor("#64748B"));
            itemLayout.addView(tvIcon);

            TextView tvText = new TextView(this);
            tvText.setText(tabs[i]);
            tvText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            tvText.setGravity(Gravity.CENTER);
            tvText.setTypeface(Typeface.DEFAULT_BOLD);
            tvText.setTextColor(i == currentTab ? Color.parseColor("#F59E0B") : Color.parseColor("#64748B"));

            itemLayout.addView(tvText);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            itemLayout.setLayoutParams(params);
            itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    renderTab(tabIndex);
                }
            });
            nav.addView(itemLayout);
        }
        return nav;
    }

    private void renderTab(int tabIndex) {
        currentTab = tabIndex;
        containerContent.removeAllViews();
        containerContent.setPadding(dpToPx(20), dpToPx(28), dpToPx(20), dpToPx(28));

        if (tabIndex == 0) renderBeranda();
        else if (tabIndex == 1) renderTransaksi();
        else if (tabIndex == 2) renderRekap();
        else renderPengaturan();
    }

    private void renderBeranda() {
        TextView tvSub = new TextView(this);
        tvSub.setText("KEUANGAN PRIBADI");
        tvSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvSub.setTypeface(Typeface.DEFAULT_BOLD);
        tvSub.setTextColor(Color.parseColor("#F59E0B"));

        String ownerName = pref.getString("owner_name", "Helmi Zainul");

        TextView tvTitle = new TextView(this);
        tvTitle.setText("Halo, " + ownerName);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
        tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setPadding(0, dpToPx(4), 0, dpToPx(18));

        containerContent.addView(tvSub);
        containerContent.addView(tvTitle);

        LinearLayout cardSaldo = new LinearLayout(this);
        cardSaldo.setOrientation(LinearLayout.VERTICAL);
        cardSaldo.setPadding(dpToPx(24), dpToPx(22), dpToPx(24), dpToPx(22));
        
        GradientDrawable gdSaldo = new GradientDrawable();
        gdSaldo.setColor(Color.parseColor("#F59E0B"));
        gdSaldo.setCornerRadius(dpToPx(24));
        cardSaldo.setBackground(gdSaldo);

        TextView labelSaldo = new TextView(this);
        labelSaldo.setText("SALDO BERSIH (SIAP PAKAI)");
        labelSaldo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        labelSaldo.setTypeface(Typeface.DEFAULT_BOLD);
        labelSaldo.setTextColor(Color.parseColor("#78350F"));

        long in = pref.getLong("in", 0);
        long out = pref.getLong("out", 0);
        long tab = pref.getLong("tabungan", 0);
        long emg = pref.getLong("darurat", 0);
        long saldo = in - out - tab - emg;

        TextView tvSaldo = new TextView(this);
        tvSaldo.setText(formatRupiah(saldo));
        tvSaldo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36);
        tvSaldo.setTypeface(Typeface.DEFAULT_BOLD);
        tvSaldo.setTextColor(Color.parseColor("#0F172A"));
        tvSaldo.setPadding(0, dpToPx(6), 0, dpToPx(6));

        TextView tvSubSaldo = new TextView(this);
        tvSubSaldo.setText("Manajemen keuangan fleksibel & aman");
        tvSubSaldo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvSubSaldo.setTextColor(Color.parseColor("#78350F"));

        cardSaldo.addView(labelSaldo);
        cardSaldo.addView(tvSaldo);
        cardSaldo.addView(tvSubSaldo);
        
        LinearLayout.LayoutParams pSaldo = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        pSaldo.setMargins(0, 0, 0, dpToPx(16));
        cardSaldo.setLayoutParams(pSaldo);
        containerContent.addView(cardSaldo);

        LinearLayout row1 = new LinearLayout(this);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        row1.addView(createStatCard("Pendapatan", formatShort(in), "#10B981", "📈"));
        row1.addView(createStatCard("Pengeluaran", formatShort(out), "#F59E0B", "📉"));
        containerContent.addView(row1);

        LinearLayout row2 = new LinearLayout(this);
        row2.setOrientation(LinearLayout.HORIZONTAL);
        row2.addView(createStatCard("Tabungan", formatShort(tab), "#3B82F6", "💰"));
        row2.addView(createStatCard("Dana darurat", formatShort(emg), "#F59E0B", "🛡️"));
        containerContent.addView(row2);

        TextView tvCatat = new TextView(this);
        tvCatat.setText("Catat Transaksi");
        tvCatat.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tvCatat.setTypeface(Typeface.DEFAULT_BOLD);
        tvCatat.setTextColor(Color.WHITE);
        tvCatat.setPadding(0, dpToPx(22), 0, dpToPx(4));

        TextView tvCatatSub = new TextView(this);
        tvCatatSub.setText("Catat pemasukan dan pengeluaran harian");
        tvCatatSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tvCatatSub.setTextColor(Color.parseColor("#64748B"));
        tvCatatSub.setPadding(0, 0, 0, dpToPx(12));

        containerContent.addView(tvCatat);
        containerContent.addView(tvCatatSub);

        renderCatatFormUI();
    }

    private View createStatCard(String title, String val, String colorHex, String emojiIcon) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dpToPx(18), dpToPx(18), dpToPx(18), dpToPx(18));

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#121721"));
        gd.setCornerRadius(dpToPx(20));
        card.setBackground(gd);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        params.setMargins(dpToPx(6), 0, dpToPx(6), dpToPx(12));
        card.setLayoutParams(params);

        TextView tvEmoji = new TextView(this);
        tvEmoji.setText(emojiIcon);
        tvEmoji.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tvEmoji.setPadding(0, 0, 0, dpToPx(6));

        TextView label = new TextView(this);
        label.setText(title);
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        label.setTextColor(Color.parseColor("#94A3B8"));

        TextView valueTv = new TextView(this);
        valueTv.setText(val);
        valueTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        valueTv.setTypeface(Typeface.DEFAULT_BOLD);
        valueTv.setTextColor(Color.parseColor(colorHex));
        valueTv.setPadding(0, dpToPx(4), 0, 0);

        card.addView(tvEmoji);
        card.addView(label);
        card.addView(valueTv);
        return card;
    }

    private void renderCatatFormUI() {
        final LinearLayout formCard = new LinearLayout(this);
        formCard.setOrientation(LinearLayout.VERTICAL);
        formCard.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));

        GradientDrawable gdForm = new GradientDrawable();
        gdForm.setColor(Color.parseColor("#121721"));
        gdForm.setCornerRadius(dpToPx(22));
        formCard.setBackground(gdForm);

        TextView lblType = new TextView(this);
        lblType.setText("Kategori Utama");
        lblType.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        lblType.setTextColor(Color.parseColor("#94A3B8"));
        formCard.addView(lblType);

        LinearLayout typeRow1 = new LinearLayout(this);
        typeRow1.setOrientation(LinearLayout.HORIZONTAL);
        typeRow1.setPadding(0, dpToPx(8), 0, dpToPx(12));

        typeRow1.addView(createTypeButton("Pendapatan", "in"));
        typeRow1.addView(createTypeButton("Pengeluaran", "out"));
        formCard.addView(typeRow1);

        LinearLayout typeRow2 = new LinearLayout(this);
        typeRow2.setOrientation(LinearLayout.HORIZONTAL);
        typeRow2.setPadding(0, 0, 0, dpToPx(16));

        typeRow2.addView(createTypeButton("Tabungan", "tabungan"));
        typeRow2.addView(createTypeButton("Dana Darurat", "darurat"));
        formCard.addView(typeRow2);

        TextView lblNom = new TextView(this);
        lblNom.setText("Nominal (Rp)");
        lblNom.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        lblNom.setTextColor(Color.parseColor("#94A3B8"));
        formCard.addView(lblNom);

        final EditText etNominal = new EditText(this);
        etNominal.setHint("0");
        etNominal.setHintTextColor(Color.parseColor("#475569"));
        etNominal.setTextColor(Color.WHITE);
        etNominal.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
        etNominal.setTypeface(Typeface.DEFAULT_BOLD);
        etNominal.setInputType(InputType.TYPE_CLASS_NUMBER);
        etNominal.setPadding(dpToPx(16), dpToPx(14), dpToPx(16), dpToPx(14));

        GradientDrawable gdInput = new GradientDrawable();
        gdInput.setColor(Color.parseColor("#0B0E14"));
        gdInput.setCornerRadius(dpToPx(14));
        etNominal.setBackground(gdInput);

        final LinearLayout.LayoutParams pNom = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        pNom.setMargins(0, dpToPx(6), 0, dpToPx(16));
        etNominal.setLayoutParams(pNom);
        formCard.addView(etNominal);

        LinearLayout dateRow = new LinearLayout(this);
        dateRow.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout colTgl = new LinearLayout(this);
        colTgl.setOrientation(LinearLayout.VERTICAL);
        colTgl.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        
        TextView lblTgl = new TextView(this);
        lblTgl.setText("Tanggal");
        lblTgl.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        lblTgl.setTextColor(Color.parseColor("#94A3B8"));
        colTgl.addView(lblTgl);

        final EditText etTgl = new EditText(this);
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        etTgl.setText(sdfDate.format(new Date()));
        etTgl.setTextColor(Color.WHITE);
        etTgl.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        etTgl.setPadding(dpToPx(14), dpToPx(12), dpToPx(14), dpToPx(12));
        etTgl.setBackground(gdInput);
        LinearLayout.LayoutParams pSub = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        pSub.setMargins(0, dpToPx(6), dpToPx(6), dpToPx(16));
        etTgl.setLayoutParams(pSub);
        colTgl.addView(etTgl);
        dateRow.addView(colTgl);

        LinearLayout colWaktu = new LinearLayout(this);
        colWaktu.setOrientation(LinearLayout.VERTICAL);
        colWaktu.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

        TextView lblWaktu = new TextView(this);
        lblWaktu.setText("Waktu");
        lblWaktu.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        lblWaktu.setTextColor(Color.parseColor("#94A3B8"));
        colWaktu.addView(lblWaktu);

        final EditText etWaktu = new EditText(this);
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
        etWaktu.setText(sdfTime.format(new Date()));
        etWaktu.setTextColor(Color.WHITE);
        etWaktu.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        etWaktu.setPadding(dpToPx(14), dpToPx(12), dpToPx(14), dpToPx(12));
        etWaktu.setBackground(gdInput);
        LinearLayout.LayoutParams pSub2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        pSub2.setMargins(dpToPx(6), dpToPx(6), 0, dpToPx(16));
        etWaktu.setLayoutParams(pSub2);
        colWaktu.addView(etWaktu);
        dateRow.addView(colWaktu);

        formCard.addView(dateRow);

        final TextView lblKat = new TextView(this);
        lblKat.setText("Sumber / Detail Kategori");
        lblKat.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        lblKat.setTextColor(Color.parseColor("#94A3B8"));
        formCard.addView(lblKat);

        final Spinner spinnerKat = new Spinner(this);
        updateSpinnerOptions(spinnerKat, selectedType);
        spinnerKat.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));
        spinnerKat.setBackground(gdInput);
        final LinearLayout.LayoutParams pSp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(48));
        pSp.setMargins(0, dpToPx(6), 0, dpToPx(16));
        spinnerKat.setLayoutParams(pSp);

        final EditText etKatManual = new EditText(this);
        etKatManual.setHint("Ketik nama pengeluaran sendiri...");
        etKatManual.setHintTextColor(Color.parseColor("#475569"));
        etKatManual.setTextColor(Color.WHITE);
        etKatManual.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        etKatManual.setPadding(dpToPx(14), dpToPx(12), dpToPx(14), dpToPx(12));
        etKatManual.setBackground(gdInput);
        etKatManual.setLayoutParams(pNom);

        if (selectedType.equals("out")) {
            spinnerKat.setVisibility(View.GONE);
            etKatManual.setVisibility(View.VISIBLE);
            lblKat.setText("Nama Pengeluaran (Ketik Sendiri)");
        } else {
            spinnerKat.setVisibility(View.VISIBLE);
            etKatManual.setVisibility(View.GONE);
            lblKat.setText("Sumber / Detail Kategori");
        }

        formCard.addView(spinnerKat);
        formCard.addView(etKatManual);

        TextView lblKet = new TextView(this);
        lblKet.setText("Catatan (Opsional)");
        lblKet.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        lblKet.setTextColor(Color.parseColor("#94A3B8"));
        formCard.addView(lblKet);

        final EditText etKet = new EditText(this);
        etKet.setHint("Catatan tambahan");
        etKet.setHintTextColor(Color.parseColor("#475569"));
        etKet.setTextColor(Color.WHITE);
        etKet.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        etKet.setPadding(dpToPx(14), dpToPx(12), dpToPx(14), dpToPx(12));
        etKet.setBackground(gdInput);
        etKet.setLayoutParams(pNom);
        formCard.addView(etKet);

        Button btnSave = new Button(this);
        btnSave.setText("Simpan Transaksi");
        btnSave.setTextColor(Color.parseColor("#0F172A"));
        btnSave.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        btnSave.setTypeface(Typeface.DEFAULT_BOLD);

        GradientDrawable gdBtn = new GradientDrawable();
        gdBtn.setColor(Color.parseColor("#F59E0B"));
        gdBtn.setCornerRadius(dpToPx(16));
        btnSave.setBackground(gdBtn);

        LinearLayout.LayoutParams lpBtn = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(52));
        btnSave.setLayoutParams(lpBtn);
        
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomStr = etNominal.getText().toString();
                if (!nomStr.isEmpty()) {
                    long val = Long.parseLong(nomStr);
                    long current = pref.getLong(selectedType, 0);
                    pref.edit().putLong(selectedType, current + val).apply();

                    String kat = "";
                    if (selectedType.equals("out")) {
                        kat = etKatManual.getText().toString().trim();
                        if (kat.isEmpty()) kat = "Pengeluaran";
                    } else {
                        kat = spinnerKat.getSelectedItem() != null ? spinnerKat.getSelectedItem().toString() : "Lainnya";
                    }

                    String tgl = etTgl.getText().toString() + " " + etWaktu.getText().toString();
                    String ket = etKet.getText().toString();

                    String logs = pref.getString("logs", "");
                    String tag = (selectedType.equals("in") || selectedType.equals("tabungan") || selectedType.equals("darurat")) ? "[+] " : "[-] ";
                    String detail = kat + " (" + tgl + ")" + (ket.isEmpty() ? "" : " - " + ket);
                    logs = tag + formatRupiah(val) + " — " + detail + "\n" + logs;
                    pref.edit().putString("logs", logs).apply();

                    Toast.makeText(MainActivity.this, "Transaksi Berhasil Disimpan!", Toast.LENGTH_SHORT).show();
                    renderTab(0);
                } else {
                    Toast.makeText(MainActivity.this, "Masukkan nominal terlebih dahulu!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        formCard.addView(btnSave);
        containerContent.addView(formCard);
    }

    private Button createTypeButton(String label, final String typeKey) {
        Button btn = new Button(this);
        btn.setText(label);
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        btn.setTypeface(Typeface.DEFAULT_BOLD);

        GradientDrawable gd = new GradientDrawable();
        if (selectedType.equals(typeKey)) {
            btn.setTextColor(Color.parseColor("#0F172A"));
            gd.setColor(Color.parseColor("#F59E0B"));
        } else {
            btn.setTextColor(Color.parseColor("#94A3B8"));
            gd.setColor(Color.parseColor("#0B0E14"));
        }
        gd.setCornerRadius(dpToPx(14));
        btn.setBackground(gd);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dpToPx(42), 1.0f);
        params.setMargins(dpToPx(3), 0, dpToPx(3), 0);
        btn.setLayoutParams(params);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedType = typeKey;
                renderTab(0);
            }
        });
        return btn;
    }

    private void updateSpinnerOptions(Spinner spinner, String type) {
        String[] options = new String[]{
            "Gaji", 
            "Pendapatan Grab", 
            "Pendapatan Gojek", 
            "ShopeeFood", 
            "Pemasukan Lainnya"
        };
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTextColor(Color.WHITE);
                view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                return view;
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTextColor(Color.WHITE);
                view.setBackgroundColor(Color.parseColor("#121721"));
                view.setPadding(dpToPx(16), dpToPx(14), dpToPx(16), dpToPx(14));
                view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void renderTransaksi() {
        TextView tvSub = new TextView(this);
        tvSub.setText("RIWAYAT KEUANGAN");
        tvSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvSub.setTypeface(Typeface.DEFAULT_BOLD);
        tvSub.setTextColor(Color.parseColor("#F59E0B"));

        TextView tvTitle = new TextView(this);
        tvTitle.setText("Transaksi");
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
        tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
        tvTitle.setTextColor(Color.WHITE);

        containerContent.addView(tvSub);
        containerContent.addView(tvTitle);

        EditText etSearch = new EditText(this);
        etSearch.setHint("🔍 Cari kategori atau catatan...");
        etSearch.setHintTextColor(Color.parseColor("#64748B"));
        etSearch.setTextColor(Color.WHITE);
        etSearch.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        etSearch.setPadding(dpToPx(16), dpToPx(14), dpToPx(16), dpToPx(14));
        
        GradientDrawable gdSearch = new GradientDrawable();
        gdSearch.setColor(Color.parseColor("#121721"));
        gdSearch.setCornerRadius(dpToPx(16));
        etSearch.setBackground(gdSearch);

        LinearLayout.LayoutParams pSearch = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        pSearch.setMargins(0, dpToPx(14), 0, dpToPx(14));
        etSearch.setLayoutParams(pSearch);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString().toLowerCase();
                renderTransaksiList();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
        containerContent.addView(etSearch);
        renderTransaksiList();
    }

    private void renderTransaksiList() {
        View existingList = containerContent.findViewWithTag("trans_list");
        if (existingList != null) containerContent.removeView(existingList);

        LinearLayout listLayout = new LinearLayout(this);
        listLayout.setTag("trans_list");
        listLayout.setOrientation(LinearLayout.VERTICAL);

        String logs = pref.getString("logs", "");
        if (logs.isEmpty()) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("Belum ada catatan transaksi tersimpan.");
            tvEmpty.setTextColor(Color.parseColor("#94A3B8"));
            tvEmpty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tvEmpty.setPadding(0, dpToPx(16), 0, 0);
            listLayout.addView(tvEmpty);
        } else {
            String[] items = logs.split("\n");
            for (String item : items) {
                if (item.trim().isEmpty()) continue;
                if (!searchQuery.isEmpty() && !item.toLowerCase().contains(searchQuery)) continue;

                LinearLayout card = new LinearLayout(this);
                card.setOrientation(LinearLayout.HORIZONTAL);
                card.setGravity(Gravity.CENTER_VERTICAL);
                card.setPadding(dpToPx(16), dpToPx(14), dpToPx(16), dpToPx(14));

                GradientDrawable gdCard = new GradientDrawable();
                gdCard.setColor(Color.parseColor("#121721"));
                gdCard.setCornerRadius(dpToPx(16));
                card.setBackground(gdCard);

                LinearLayout.LayoutParams pCard = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                pCard.setMargins(0, 0, 0, dpToPx(10));
                card.setLayoutParams(pCard);

                TextView tvIcon = new TextView(this);
                tvIcon.setText(item.contains("[+]") ? "📈" : "📉");
                tvIcon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                
                LinearLayout.LayoutParams pIcon = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                pIcon.setMargins(0, 0, dpToPx(14), 0);
                tvIcon.setLayoutParams(pIcon);

                LinearLayout colText = new LinearLayout(this);
                colText.setOrientation(LinearLayout.VERTICAL);
                colText.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

                TextView tvTitle = new TextView(this);
                tvTitle.setText(item.replaceAll("\\[\\+\\]|\\[\\-\\]", "").split("—")[1].trim());
                tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
                tvTitle.setTextColor(Color.WHITE);

                TextView tvSub = new TextView(this);
                tvSub.setText("Tercatat di perangkat");
                tvSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                tvSub.setTextColor(Color.parseColor("#64748B"));

                colText.addView(tvTitle);
                colText.addView(tvSub);

                TextView tvVal = new TextView(this);
                tvVal.setText((item.contains("[+]") ? "+" : "-") + item.split("—")[0].replaceAll("\\[\\+\\]|\\[\\-\\]", "").trim());
                tvVal.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                tvVal.setTypeface(Typeface.DEFAULT_BOLD);
                tvVal.setTextColor(Color.parseColor(item.contains("[+]") ? "#10B981" : "#EF4444"));

                card.addView(tvIcon);
                card.addView(colText);
                card.addView(tvVal);

                listLayout.addView(card);
            }
        }
        containerContent.addView(listLayout);
    }

    private void renderRekap() {
        TextView tvSub = new TextView(this);
        tvSub.setText("ANALISIS KEUANGAN");
        tvSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvSub.setTypeface(Typeface.DEFAULT_BOLD);
        tvSub.setTextColor(Color.parseColor("#F59E0B"));

        TextView tvTitle = new TextView(this);
        tvTitle.setText("Rekap");
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
        tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setPadding(0, dpToPx(4), 0, dpToPx(12));

        TextView tvDesc = new TextView(this);
        tvDesc.setText("Pantau arus uang dengan ringkas dan jelas.");
        tvDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvDesc.setTextColor(Color.parseColor("#64748B"));
        tvDesc.setPadding(0, 0, 0, dpToPx(16));

        containerContent.addView(tvSub);
        containerContent.addView(tvTitle);
        containerContent.addView(tvDesc);

        LinearLayout periodRow = new LinearLayout(this);
        periodRow.setOrientation(LinearLayout.HORIZONTAL);
        periodRow.setPadding(0, 0, 0, dpToPx(16));

        String[] periods = {"Harian", "Mingguan", "Bulanan", "Tahunan"};
        for (final String p : periods) {
            TextView btnP = new TextView(this);
            btnP.setText(p);
            btnP.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            btnP.setGravity(Gravity.CENTER);
            btnP.setTypeface(Typeface.DEFAULT_BOLD);
            
            GradientDrawable gd = new GradientDrawable();
            if (p.equals(periodFilter)) {
                btnP.setTextColor(Color.parseColor("#0F172A"));
                gd.setColor(Color.parseColor("#F59E0B"));
            } else {
                btnP.setTextColor(Color.parseColor("#94A3B8"));
                gd.setColor(Color.parseColor("#121721"));
            }
            gd.setCornerRadius(dpToPx(14));
            btnP.setBackground(gd);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dpToPx(42), 1.0f);
            lp.setMargins(dpToPx(3), 0, dpToPx(3), 0);
            btnP.setLayoutParams(lp);
            btnP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    periodFilter = p;
                    renderTab(2);
                }
            });
            periodRow.addView(btnP);
        }
        containerContent.addView(periodRow);

        long in = pref.getLong("in", 0);
        long out = pref.getLong("out", 0);
        long tab = pref.getLong("tabungan", 0);
        long emg = pref.getLong("darurat", 0);
        long saldo = in - out - tab - emg;

        LinearLayout cardBig = new LinearLayout(this);
        cardBig.setOrientation(LinearLayout.VERTICAL);
        cardBig.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));

        GradientDrawable gdBig = new GradientDrawable();
        gdBig.setColor(Color.parseColor("#121721"));
        gdBig.setCornerRadius(dpToPx(20));
        cardBig.setBackground(gdBig);

        TextView tvBigLabel = new TextView(this);
        tvBigLabel.setText("SISA BERSIH SAAT INI");
        tvBigLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tvBigLabel.setTextColor(Color.parseColor("#94A3B8"));

        TextView tvBigVal = new TextView(this);
        tvBigVal.setText(formatRupiah(saldo));
        tvBigVal.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        tvBigVal.setTypeface(Typeface.DEFAULT_BOLD);
        tvBigVal.setTextColor(Color.WHITE);
        tvBigVal.setPadding(0, dpToPx(6), 0, 0);

        cardBig.addView(tvBigLabel);
        cardBig.addView(tvBigVal);
        
        LinearLayout.LayoutParams lpBig = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lpBig.setMargins(0, 0, 0, dpToPx(16));
        cardBig.setLayoutParams(lpBig);
        containerContent.addView(cardBig);

        LinearLayout row1 = new LinearLayout(this);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        row1.addView(createSimpleGridCard("Pendapatan", formatShort(in), "#10B981"));
        row1.addView(createSimpleGridCard("Pengeluaran", formatShort(out), "#F59E0B"));
        containerContent.addView(row1);

        LinearLayout row2 = new LinearLayout(this);
        row2.setOrientation(LinearLayout.HORIZONTAL);
        row2.addView(createSimpleGridCard("Tabungan", formatShort(tab), "#3B82F6"));
        row2.addView(createSimpleGridCard("Dana darurat", formatShort(emg), "#F59E0B"));
        containerContent.addView(row2);

        TextView tvArus = new TextView(this);
        tvArus.setText("Perbandingan arus uang");
        tvArus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tvArus.setTypeface(Typeface.DEFAULT_BOLD);
        tvArus.setTextColor(Color.WHITE);
        tvArus.setPadding(0, dpToPx(24), 0, dpToPx(12));
        containerContent.addView(tvArus);

        LinearLayout cardArus = new LinearLayout(this);
        cardArus.setOrientation(LinearLayout.VERTICAL);
        cardArus.setPadding(dpToPx(20), dpToPx(18), dpToPx(20), dpToPx(18));

        GradientDrawable gdArus = new GradientDrawable();
        gdArus.setColor(Color.parseColor("#121721"));
        gdArus.setCornerRadius(dpToPx(20));
        cardArus.setBackground(gdArus);

        long maxArus = Math.max(Math.max(Math.max(in, out), tab), Math.max(emg, 1));
        cardArus.addView(createArusRow("Pendapatan", in, maxArus, "#10B981"));
        cardArus.addView(createArusRow("Pengeluaran", out, maxArus, "#F59E0B"));
        cardArus.addView(createArusRow("Tabungan", tab, maxArus, "#3B82F6"));
        cardArus.addView(createArusRow("Darurat", emg, maxArus, "#F59E0B"));

        containerContent.addView(cardArus);
    }

    private View createSimpleGridCard(String label, String val, String colorHex) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#121721"));
        gd.setCornerRadius(dpToPx(18));
        card.setBackground(gd);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        params.setMargins(dpToPx(6), 0, dpToPx(6), dpToPx(12));
        card.setLayoutParams(params);

        TextView tvL = new TextView(this);
        tvL.setText(label);
        tvL.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvL.setTextColor(Color.parseColor("#94A3B8"));

        TextView tvV = new TextView(this);
        tvV.setText(val);
        tvV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tvV.setTypeface(Typeface.DEFAULT_BOLD);
        tvV.setTextColor(Color.parseColor(colorHex));
        tvV.setPadding(0, dpToPx(4), 0, 0);

        card.addView(tvL);
        card.addView(tvV);
        return card;
    }

    private View createArusRow(String label, long val, long max, String colorHex) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dpToPx(8), 0, dpToPx(8));

        TextView tvL = new TextView(this);
        tvL.setText(label);
        tvL.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvL.setTextColor(Color.parseColor("#94A3B8"));
        tvL.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(95), ViewGroup.LayoutParams.WRAP_CONTENT));

        ProgressBar pb = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        pb.setMax((int) max);
        pb.setProgress((int) val);
        pb.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

        TextView tvV = new TextView(this);
        tvV.setText(formatShort(val));
        tvV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvV.setTypeface(Typeface.DEFAULT_BOLD);
        tvV.setTextColor(Color.WHITE);
        tvV.setGravity(Gravity.END);
        tvV.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(75), ViewGroup.LayoutParams.WRAP_CONTENT));

        row.addView(tvL);
        row.addView(pb);
        row.addView(tvV);
        return row;
    }

    private void renderPengaturan() {
        TextView tvSub = new TextView(this);
        tvSub.setText("PREFERENSI LOKAL");
        tvSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvSub.setTypeface(Typeface.DEFAULT_BOLD);
        tvSub.setTextColor(Color.parseColor("#F59E0B"));

        TextView tvTitle = new TextView(this);
        tvTitle.setText("Pengaturan");
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
        tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setPadding(0, dpToPx(4), 0, dpToPx(16));

        containerContent.addView(tvSub);
        containerContent.addView(tvTitle);

        TextView tvSecName = new TextView(this);
        tvSecName.setText("👤 Nama Profil Pengguna");
        tvSecName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        tvSecName.setTypeface(Typeface.DEFAULT_BOLD);
        tvSecName.setTextColor(Color.WHITE);
        tvSecName.setPadding(0, 0, 0, dpToPx(8));
        containerContent.addView(tvSecName);

        final EditText etOwnerName = createInputText("Nama Pemilik", pref.getString("owner_name", "Helmi Zainul"));
        Button btnSaveName = new Button(this);
        btnSaveName.setText("Simpan Nama Profil");
        btnSaveName.setTextColor(Color.parseColor("#0F172A"));
        btnSaveName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        btnSaveName.setTypeface(Typeface.DEFAULT_BOLD);

        GradientDrawable gdBtnName = new GradientDrawable();
        gdBtnName.setColor(Color.parseColor("#F59E0B"));
        gdBtnName.setCornerRadius(dpToPx(12));
        btnSaveName.setBackground(gdBtnName);

        btnSaveName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = etOwnerName.getText().toString();
                if (!newName.isEmpty()) {
                    pref.edit().putString("owner_name", newName).apply();
                    Toast.makeText(MainActivity.this, "Nama Profil Berhasil Disimpan!", Toast.LENGTH_SHORT).show();
                    renderTab(3);
                }
            }
        });

        LinearLayout cardName = new LinearLayout(this);
        cardName.setOrientation(LinearLayout.VERTICAL);
        cardName.setPadding(dpToPx(16), dpToPx(14), dpToPx(16), dpToPx(14));
        GradientDrawable gdCardName = new GradientDrawable();
        gdCardName.setColor(Color.parseColor("#121721"));
        gdCardName.setCornerRadius(dpToPx(16));
        cardName.setBackground(gdCardName);
        cardName.addView(etOwnerName);
        cardName.addView(btnSaveName);
        containerContent.addView(cardName);

        TextView tvSec1 = new TextView(this);
        tvSec1.setText("🚩 Target Keuangan");
        tvSec1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        tvSec1.setTypeface(Typeface.DEFAULT_BOLD);
        tvSec1.setTextColor(Color.WHITE);
        tvSec1.setPadding(0, dpToPx(16), 0, dpToPx(8));
        containerContent.addView(tvSec1);

        final EditText etTargetTab = createInputNumber("Target Tabungan", String.valueOf(pref.getLong("target_tabungan", 10000000)));
        final EditText etTargetEmg = createInputNumber("Target Dana Darurat", String.valueOf(pref.getLong("target_darurat", 15000000)));
        containerContent.addView(etTargetTab);
        containerContent.addView(etTargetEmg);

        TextView tvSec2 = new TextView(this);
        tvSec2.setText("🔔 Pengingat Penyisihan");
        tvSec2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        tvSec2.setTypeface(Typeface.DEFAULT_BOLD);
        tvSec2.setTextColor(Color.WHITE);
        tvSec2.setPadding(0, dpToPx(16), 0, dpToPx(8));
        containerContent.addView(tvSec2);

        LinearLayout cardRemind = new LinearLayout(this);
        cardRemind.setOrientation(LinearLayout.HORIZONTAL);
        cardRemind.setGravity(Gravity.CENTER_VERTICAL);
        cardRemind.setPadding(dpToPx(16), dpToPx(14), dpToPx(16), dpToPx(14));

        GradientDrawable gdRem = new GradientDrawable();
        gdRem.setColor(Color.parseColor("#121721"));
        gdRem.setCornerRadius(dpToPx(16));
        cardRemind.setBackground(gdRem);

        LinearLayout colRem = new LinearLayout(this);
        colRem.setOrientation(LinearLayout.VERTICAL);
        colRem.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

        TextView tvRemTitle = new TextView(this);
        tvRemTitle.setText("Pengingat Harian (Pencatatan Keuangan)");
        tvRemTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvRemTitle.setTypeface(Typeface.DEFAULT_BOLD);
        tvRemTitle.setTextColor(Color.WHITE);

        TextView tvRemSub = new TextView(this);
        tvRemSub.setText("Setiap hari pukul 20:00");
        tvRemSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        tvRemSub.setTextColor(Color.parseColor("#64748B"));

        colRem.addView(tvRemTitle);
        colRem.addView(tvRemSub);

        Switch sw = new Switch(this);
        sw.setChecked(pref.getBoolean("reminder_active", false));
        sw.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(android.widget.CompoundButton buttonView, boolean isChecked) {
                pref.edit().putBoolean("reminder_active", isChecked).apply();
            }
        });

        cardRemind.addView(colRem);
        cardRemind.addView(sw);
        containerContent.addView(cardRemind);

        TextView tvSec3 = new TextView(this);
        tvSec3.setText("≡ Daftar Custom Sumber");
        tvSec3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        tvSec3.setTypeface(Typeface.DEFAULT_BOLD);
        tvSec3.setTextColor(Color.WHITE);
        tvSec3.setPadding(0, dpToPx(16), 0, dpToPx(8));
        containerContent.addView(tvSec3);

        containerContent.addView(createCustomBox("Sumber Pendapatan / Tabungan / Darurat", "Gaji · Grab · Gojek · ShopeeFood"));
        containerContent.addView(createCustomBox("Kategori Pengeluaran", "Ketik nama pengeluaran sendiri"));

        TextView tvSec4 = new TextView(this);
        tvSec4.setText("🔒 Keamanan Aplikasi");
        tvSec4.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        tvSec4.setTypeface(Typeface.DEFAULT_BOLD);
        tvSec4.setTextColor(Color.WHITE);
        tvSec4.setPadding(0, dpToPx(16), 0, dpToPx(8));
        containerContent.addView(tvSec4);

        LinearLayout cardPin = new LinearLayout(this);
        cardPin.setOrientation(LinearLayout.VERTICAL);
        cardPin.setPadding(dpToPx(16), dpToPx(14), dpToPx(16), dpToPx(14));

        GradientDrawable gdPin = new GradientDrawable();
        gdPin.setColor(Color.parseColor("#121721"));
        gdPin.setCornerRadius(dpToPx(16));
        cardPin.setBackground(gdPin);

        TextView tvPinTitle = new TextView(this);
        tvPinTitle.setText("Aktifkan PIN");
        tvPinTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvPinTitle.setTypeface(Typeface.DEFAULT_BOLD);
        tvPinTitle.setTextColor(Color.WHITE);

        final EditText etPin = createInputNumber("Buat PIN (4-6 angka)", pref.getString("app_pin", ""));
        etPin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        Button btnPin = new Button(this);
        btnPin.setText("Simpan PIN");
        btnPin.setTextColor(Color.parseColor("#0F172A"));
        btnPin.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        btnPin.setTypeface(Typeface.DEFAULT_BOLD);

        GradientDrawable gdBtnPin = new GradientDrawable();
        gdBtnPin.setColor(Color.parseColor("#F59E0B"));
        gdBtn_setCornerRadius(gdBtnPin, dpToPx(12));
        btnPin.setBackground(gdBtnPin);

        btnPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String p = etPin.getText().toString();
                if (!p.isEmpty()) {
                    pref.edit().putString("app_pin", p).apply();
                    Toast.makeText(MainActivity.this, "PIN Berhasil Disimpan!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cardPin.addView(tvPinTitle);
        cardPin.addView(etPin);
        cardPin.addView(btnPin);
        containerContent.addView(cardPin);

        TextView tvAuthor = new TextView(this);
        tvAuthor.setText("\nINCOMESTRUCT v1.0\nDibuat oleh Helmi Zainul Pahmi");
        tvAuthor.setTextColor(Color.parseColor("#F59E0B"));
        tvAuthor.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tvAuthor.setTypeface(Typeface.DEFAULT_BOLD);
        tvAuthor.setGravity(Gravity.CENTER_HORIZONTAL);
        tvAuthor.setPadding(0, dpToPx(20), 0, 0);
        containerContent.addView(tvAuthor);
    }

    private void gdBtn_setCornerRadius(GradientDrawable gd, int radius) {
        gd.setCornerRadius(radius);
    }

    private View createCustomBox(String title, String sub) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dpToPx(16), dpToPx(14), dpToPx(16), dpToPx(14));

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#121721"));
        gd.setCornerRadius(dpToPx(16));
        card.setBackground(gd);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, dpToPx(8));
        card.setLayoutParams(lp);

        TextView tvTitle = new TextView(this);
        tvTitle.setText("≡ " + title);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
        tvTitle.setTextColor(Color.WHITE);

        TextView tvSub = new TextView(this);
        tvSub.setText(sub);
        tvSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        tvSub.setTextColor(Color.parseColor("#64748B"));
        tvSub.setPadding(0, dpToPx(4), 0, 0);

        card.addView(tvTitle);
        tvSub.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        card.addView(tvSub);
        return card;
    }

    private EditText createInputNumber(String hint, String val) {
        EditText et = new EditText(this);
        et.setHint(hint);
        et.setText(val);
        et.setHintTextColor(Color.parseColor("#475569"));
        et.setTextColor(Color.WHITE);
        et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        et.setInputType(InputType.TYPE_CLASS_NUMBER);
        et.setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12));

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#0B0E14"));
        gd.setCornerRadius(dpToPx(14));
        et.setBackground(gd);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, dpToPx(6), 0, dpToPx(8));
        et.setLayoutParams(lp);
        return et;
    }

    private EditText createInputText(String hint, String val) {
        EditText et = new EditText(this);
        et.setHint(hint);
        et.setText(val);
        et.setHintTextColor(Color.parseColor("#475569"));
        et.setTextColor(Color.WHITE);
        et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        et.setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12));

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#0B0E14"));
        gd.setCornerRadius(dpToPx(14));
        et.setBackground(gd);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, dpToPx(6), 0, dpToPx(8));
        et.setLayoutParams(lp);
        return et;
    }

    private String formatRupiah(long amount) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat format = NumberFormat.getCurrencyInstance(localeID);
        return format.format(amount).replace(",00", "");
    }

    private String formatShort(long amount) {
        if (amount >= 1000000000) return "Rp" + (amount / 1000000000) + " M";
        if (amount >= 1000000) return "Rp" + (amount / 1000000) + " jt";
        if (amount >= 1000) return "Rp" + (amount / 1000) + " rb";
        return "Rp" + amount;
    }
}
