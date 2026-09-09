package com.helmi.incomestruct;

import android.app.Activity;
import android.app.AlertDialog;
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
import java.util.Locale;

public class MainActivity extends Activity {
    private SharedPreferences pref;
    private LinearLayout containerContent;
    private int currentTab = 0;
    private String searchQuery = "";
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
        nav.setPadding(0, dpToPx(8), 0, dpToPx(12));

        String[] tabs = {"Beranda", "Transaksi", "Rekap", "Pengaturan"};
        String[] icons = {"⌂", "⇆", "📊", "⚙"};

        for (int i = 0; i < tabs.length; i++) {
            final int tabIndex = i;
            LinearLayout itemLayout = new LinearLayout(this);
            itemLayout.setOrientation(LinearLayout.VERTICAL);
            itemLayout.setGravity(Gravity.CENTER);

            TextView tvIcon = new TextView(this);
            tvIcon.setText(icons[i]);
            tvIcon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            tvIcon.setGravity(Gravity.CENTER);
            tvIcon.setTextColor(i == currentTab ? Color.parseColor("#F59E0B") : Color.parseColor("#64748B"));

            TextView tvText = new TextView(this);
            tvText.setText(tabs[i]);
            tvText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            tvText.setGravity(Gravity.CENTER);
            tvText.setTextColor(i == currentTab ? Color.parseColor("#F59E0B") : Color.parseColor("#64748B"));

            itemLayout.addView(tvIcon);
            itemLayout.addView(tvText);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            itemLayout.setLayoutParams(params);
            itemLayout.setOnClickListener(v -> renderTab(tabIndex));
            nav.addView(itemLayout);
        }
        return nav;
    }

    private void renderTab(int tabIndex) {
        currentTab = tabIndex;
        containerContent.removeAllViews();
        containerContent.setPadding(dpToPx(16), dpToPx(20), dpToPx(16), dpToPx(20));

        if (tabIndex == 0) renderBeranda();
        else if (tabIndex == 1) renderTransaksi();
        else if (tabIndex == 2) renderRekap();
        else renderPengaturan();
    }

    // ================= TAB 1: BERANDA =================
    private void renderBeranda() {
        LinearLayout headerRow = new LinearLayout(this);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView tvSub = new TextView(this);
        tvSub.setText("STRUCTURED FINANCIAL • OFFLINE");
        tvSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        tvSub.setTypeface(Typeface.DEFAULT_BOLD);
        tvSub.setTextColor(Color.parseColor("#F59E0B"));
        tvSub.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

        TextView tvBadge = new TextView(this);
        tvBadge.setText("● Active");
        tvBadge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        tvBadge.setTextColor(Color.parseColor("#10B981"));
        tvBadge.setPadding(dpToPx(8), dpToPx(3), dpToPx(8), dpToPx(3));
        
        GradientDrawable gdBadge = new GradientDrawable();
        gdBadge.setColor(Color.parseColor("#16231E"));
        gdBadge.setCornerRadius(dpToPx(12));
        tvBadge.setBackground(gdBadge);

        headerRow.addView(tvSub);
        headerRow.addView(tvBadge);
        containerContent.addView(headerRow);

        TextView tvTitle = new TextView(this);
        tvTitle.setText("Halo, Helmi Zainul");
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setPadding(0, dpToPx(2), 0, dpToPx(14));
        containerContent.addView(tvTitle);

        // CARD SALDO UTAMA
        LinearLayout cardSaldo = new LinearLayout(this);
        cardSaldo.setOrientation(LinearLayout.VERTICAL);
        cardSaldo.setPadding(dpToPx(18), dpToPx(18), dpToPx(18), dpToPx(18));
        
        GradientDrawable gdSaldo = new GradientDrawable();
        gdSaldo.setColor(Color.parseColor("#F59E0B"));
        gdSaldo.setCornerRadius(dpToPx(20));
        cardSaldo.setBackground(gdSaldo);

        TextView labelSaldo = new TextView(this);
        labelSaldo.setText("SALDO BERSIH (SIAP PAKAI)");
        labelSaldo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        labelSaldo.setTypeface(Typeface.DEFAULT_BOLD);
        labelSaldo.setTextColor(Color.parseColor("#78350F"));

        long in = pref.getLong("in", 0);
        long out = pref.getLong("out", 0);
        long tab = pref.getLong("tabungan", 0);
        long emg = pref.getLong("darurat", 0);
        long saldo = in - out - tab - emg;

        TextView tvSaldo = new TextView(this);
        tvSaldo.setText(formatRupiah(saldo));
        tvSaldo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
        tvSaldo.setTypeface(Typeface.DEFAULT_BOLD);
        tvSaldo.setTextColor(Color.parseColor("#0F172A"));
        tvSaldo.setPadding(0, dpToPx(4), 0, dpToPx(4));

        TextView tvSubSaldo = new TextView(this);
        tvSubSaldo.setText("Bebas potongan cicilan, keluarga & tabungan");
        tvSubSaldo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        tvSubSaldo.setTextColor(Color.parseColor("#92400E"));

        cardSaldo.addView(labelSaldo);
        cardSaldo.addView(tvSaldo);
        cardSaldo.addView(tvSubSaldo);
        
        LinearLayout.LayoutParams pSaldo = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        pSaldo.setMargins(0, 0, 0, dpToPx(12));
        cardSaldo.setLayoutParams(pSaldo);
        containerContent.addView(cardSaldo);

        // GRID CARD INDIKATOR KEUANGAN
        LinearLayout row1 = new LinearLayout(this);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        row1.addView(createGridCard("Total Income", formatShort(in), "#10B981", "↓"));
        row1.addView(createGridCard("Total Expense", formatShort(out), "#EF4444", "↑"));
        containerContent.addView(row1);

        LinearLayout row2 = new LinearLayout(this);
        row2.setOrientation(LinearLayout.HORIZONTAL);
        row2.addView(createGridCard("Pos Tabungan", formatShort(tab), "#3B82F6", "👛"));
        row2.addView(createGridCard("Dana Darurat", formatShort(emg), "#F59E0B", "🛡"));
        containerContent.addView(row2);

        // BARIS CATAT TRANSAKSI
        LinearLayout catatHeader = new LinearLayout(this);
        catatHeader.setOrientation(LinearLayout.HORIZONTAL);
        catatHeader.setPadding(0, dpToPx(12), 0, dpToPx(8));

        TextView tvCatat = new TextView(this);
        tvCatat.setText("Aksi Cepat");
        tvCatat.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        tvCatat.setTypeface(Typeface.DEFAULT_BOLD);
        tvCatat.setTextColor(Color.WHITE);
        tvCatat.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

        TextView tvSubCatat = new TextView(this);
        tvSubCatat.setText("Input transaksi harian");
        tvSubCatat.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        tvSubCatat.setTextColor(Color.parseColor("#64748B"));

        catatHeader.addView(tvCatat);
        catatHeader.addView(tvSubCatat);
        containerContent.addView(catatHeader);

        // TOMBOL AKSI CEPAT
        LinearLayout btnGrid = new LinearLayout(this);
        btnGrid.setOrientation(LinearLayout.HORIZONTAL);
        btnGrid.addView(createActionButton("Income", "+", "in"));
        btnGrid.addView(createActionButton("Expense", "-", "out"));
        btnGrid.addView(createActionButton("Tabungan", "👛", "tabungan"));
        btnGrid.addView(createActionButton("Darurat", "🛡", "darurat"));
        containerContent.addView(btnGrid);

        // TARGET KEUANGAN BERJALAN
        TextView tvTarget = new TextView(this);
        tvTarget.setText("Target berjalan");
        tvTarget.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        tvTarget.setTypeface(Typeface.DEFAULT_BOLD);
        tvTarget.setTextColor(Color.WHITE);
        tvTarget.setPadding(0, dpToPx(16), 0, dpToPx(8));
        containerContent.addView(tvTarget);

        long targetTab = pref.getLong("target_tabungan", 10000000);
        long targetEmg = pref.getLong("target_darurat", 15000000);

        containerContent.addView(createTargetCard("Tabungan Masa Depan", tab, targetTab));
        containerContent.addView(createTargetCard("Dana Darurat Keluarga", emg, targetEmg));
    }

    private View createGridCard(String title, String val, String colorHex, String icon) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#121721"));
        gd.setCornerRadius(dpToPx(14));
        card.setBackground(gd);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        params.setMargins(dpToPx(3), 0, dpToPx(3), dpToPx(6));
        card.setLayoutParams(params);

        TextView tvIcon = new TextView(this);
        tvIcon.setText(icon);
        tvIcon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        tvIcon.setGravity(Gravity.CENTER);
        tvIcon.setTextColor(Color.WHITE);
        
        GradientDrawable gdIcon = new GradientDrawable();
        gdIcon.setColor(Color.parseColor(colorHex.equals("#10B981") ? "#064E3B" : colorHex.equals("#F59E0B") ? "#451A03" : colorHex.equals("#EF4444") ? "#451A1A" : "#1E3A8A"));
        gdIcon.setShape(GradientDrawable.OVAL);
        tvIcon.setBackground(gdIcon);
        
        LinearLayout.LayoutParams pIcon = new LinearLayout.LayoutParams(dpToPx(22), dpToPx(22));
        pIcon.setMargins(0, 0, 0, dpToPx(6));
        tvIcon.setLayoutParams(pIcon);

        TextView label = new TextView(this);
        label.setText(title);
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        label.setTextColor(Color.parseColor("#94A3B8"));

        TextView valueTv = new TextView(this);
        valueTv.setText(val);
        valueTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        valueTv.setTypeface(Typeface.DEFAULT_BOLD);
        valueTv.setTextColor(Color.parseColor(colorHex));

        card.addView(tvIcon);
        card.addView(label);
        card.addView(valueTv);
        return card;
    }

    private View createActionButton(String label, String symbol, final String type) {
        LinearLayout btn = new LinearLayout(this);
        btn.setOrientation(LinearLayout.VERTICAL);
        btn.setGravity(Gravity.CENTER);
        btn.setPadding(dpToPx(8), dpToPx(10), dpToPx(8), dpToPx(10));

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#121721"));
        gd.setCornerRadius(dpToPx(14));
        btn.setBackground(gd);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dpToPx(66), 1.0f);
        params.setMargins(dpToPx(3), 0, dpToPx(3), 0);
        btn.setLayoutParams(params);

        TextView tvSym = new TextView(this);
        tvSym.setText(symbol);
        tvSym.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        tvSym.setGravity(Gravity.CENTER);
        tvSym.setTextColor(Color.parseColor("#F59E0B"));

        GradientDrawable gdSym = new GradientDrawable();
        gdSym.setColor(Color.parseColor("#2D1F0D"));
        gdSym.setShape(GradientDrawable.OVAL);
        tvSym.setBackground(gdSym);
        
        LinearLayout.LayoutParams pSym = new LinearLayout.LayoutParams(dpToPx(20), dpToPx(20));
        pSym.setMargins(0, 0, 0, dpToPx(3));
        tvSym.setLayoutParams(pSym);

        TextView tvLabel = new TextView(this);
        tvLabel.setText(label);
        tvLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        tvLabel.setTextColor(Color.WHITE);

        btn.addView(tvSym);
        btn.addView(tvLabel);
        btn.setOnClickListener(v -> showFormDialog(type));
        return btn;
    }

    private View createTargetCard(String title, long current, long max) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dpToPx(14), dpToPx(12), dpToPx(14), dpToPx(12));

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#121721"));
        gd.setCornerRadius(dpToPx(14));
        card.setBackground(gd);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, dpToPx(8));
        card.setLayoutParams(params);

        LinearLayout rowHead = new LinearLayout(this);
        rowHead.setOrientation(LinearLayout.HORIZONTAL);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

        int pct = (max > 0) ? (int) ((current * 100) / max) : 0;
        TextView tvPct = new TextView(this);
        tvPct.setText(pct + "%");
        tvPct.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        tvPct.setTextColor(Color.parseColor("#F59E0B"));

        rowHead.addView(tvTitle);
        rowHead.addView(tvPct);

        ProgressBar pb = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        pb.setMax((int) Math.max(max, 1));
        pb.setProgress((int) current);
        pb.setPadding(0, dpToPx(6), 0, dpToPx(6));

        TextView tvVal = new TextView(this);
        tvVal.setText(formatRupiah(current) + " dari " + formatRupiah(max));
        tvVal.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        tvVal.setTextColor(Color.parseColor("#64748B"));

        card.addView(rowHead);
        card.addView(pb);
        card.addView(tvVal);
        return card;
    }

    // ================= TAB 2: TRANSAKSI =================
    private void renderTransaksi() {
        TextView tvSub = new TextView(this);
        tvSub.setText("LEDGER TRANSAKSI");
        tvSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        tvSub.setTypeface(Typeface.DEFAULT_BOLD);
        tvSub.setTextColor(Color.parseColor("#F59E0B"));

        TextView tvTitle = new TextView(this);
        tvTitle.setText("Transaksi");
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
        tvTitle.setTextColor(Color.WHITE);

        TextView tvDesc = new TextView(this);
        tvDesc.setText("Cari, kelola, dan pantau riwayat arus kas.");
        tvDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        tvDesc.setTextColor(Color.parseColor("#64748B"));
        tvDesc.setPadding(0, dpToPx(2), 0, dpToPx(12));

        containerContent.addView(tvSub);
        containerContent.addView(tvTitle);
        containerContent.addView(tvDesc);

        EditText etSearch = new EditText(this);
        etSearch.setHint("🔍 Cari Gaji, Grab, Shopee, Cicilan, Adek...");
        etSearch.setHintTextColor(Color.parseColor("#64748B"));
        etSearch.setTextColor(Color.WHITE);
        etSearch.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        etSearch.setPadding(dpToPx(14), dpToPx(10), dpToPx(14), dpToPx(10));
        
        GradientDrawable gdSearch = new GradientDrawable();
        gdSearch.setColor(Color.parseColor("#121721"));
        gdSearch.setCornerRadius(dpToPx(12));
        etSearch.setBackground(gdSearch);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString().toLowerCase();
                renderTransaksiList();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
        containerContent.addView(etSearch);

        LinearLayout btnFilterRow = new LinearLayout(this);
        btnFilterRow.setOrientation(LinearLayout.HORIZONTAL);
        btnFilterRow.setPadding(0, dpToPx(10), 0, dpToPx(12));

        btnFilterRow.addView(createOrangeFilterBtn("+ Income", "in"));
        btnFilterRow.addView(createDarkFilterBtn("- Expense", "out"));
        btnFilterRow.addView(createDarkFilterBtn("+ Tabungan", "tabungan"));
        containerContent.addView(btnFilterRow);

        renderTransaksiList();
    }

    private View createOrangeFilterBtn(String text, final String type) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextColor(Color.parseColor("#0F172A"));
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        btn.setTypeface(Typeface.DEFAULT_BOLD);
        
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#F59E0B"));
        gd.setCornerRadius(dpToPx(10));
        btn.setBackground(gd);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dpToPx(36), 1.0f);
        params.setMargins(0, 0, dpToPx(3), 0);
        btn.setLayoutParams(params);
        btn.setOnClickListener(v -> showFormDialog(type));
        return btn;
    }

    private View createDarkFilterBtn(String text, final String type) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        btn.setTypeface(Typeface.DEFAULT_BOLD);
        
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#121721"));
        gd.setCornerRadius(dpToPx(10));
        btn.setBackground(gd);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dpToPx(36), 1.0f);
        params.setMargins(dpToPx(2), 0, dpToPx(2), 0);
        btn.setLayoutParams(params);
        btn.setOnClickListener(v -> showFormDialog(type));
        return btn;
    }

    private void renderTransaksiList() {
        View existingList = containerContent.findViewWithTag("trans_list");
        if (existingList != null) containerContent.removeView(existingList);

        LinearLayout listLayout = new LinearLayout(this);
        listLayout.setTag("trans_list");
        listLayout.setOrientation(LinearLayout.VERTICAL);

        String logs = pref.getString("logs", "");
        if (logs.isEmpty()) {
            LinearLayout cardEmpty = new LinearLayout(this);
            cardEmpty.setPadding(dpToPx(14), dpToPx(14), dpToPx(14), dpToPx(14));
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(Color.parseColor("#121721"));
            gd.setCornerRadius(dpToPx(12));
            cardEmpty.setBackground(gd);

            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("Belum ada catatan transaksi tersimpan.");
            tvEmpty.setTextColor(Color.parseColor("#94A3B8"));
            tvEmpty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            cardEmpty.addView(tvEmpty);
            listLayout.addView(cardEmpty);
        } else {
            String[] items = logs.split("\n");
            int count = 0;
            for (String item : items) {
                if (item.trim().isEmpty()) continue;
                if (!searchQuery.isEmpty() && !item.toLowerCase().contains(searchQuery)) continue;
                count++;

                LinearLayout card = new LinearLayout(this);
                card.setOrientation(LinearLayout.HORIZONTAL);
                card.setGravity(Gravity.CENTER_VERTICAL);
                card.setPadding(0, dpToPx(8), 0, dpToPx(8));

                TextView tvIcon = new TextView(this);
                tvIcon.setText(item.contains("[+]") ? "↓" : "↑");
                tvIcon.setGravity(Gravity.CENTER);
                tvIcon.setTextColor(Color.WHITE);
                
                GradientDrawable gdIcon = new GradientDrawable();
                gdIcon.setColor(Color.parseColor(item.contains("[+]") ? "#064E3B" : "#451A03"));
                gdIcon.setShape(GradientDrawable.OVAL);
                tvIcon.setBackground(gdIcon);

                LinearLayout.LayoutParams pIcon = new LinearLayout.LayoutParams(dpToPx(28), dpToPx(28));
                pIcon.setMargins(0, 0, dpToPx(10), 0);
                tvIcon.setLayoutParams(pIcon);

                LinearLayout colText = new LinearLayout(this);
                colText.setOrientation(LinearLayout.VERTICAL);
                colText.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

                TextView tvTitle = new TextView(this);
                tvTitle.setText(item.replaceAll("\\[\\+\\]|\\[\\-\\]", "").split("—")[1].trim());
                tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
                tvTitle.setTextColor(Color.WHITE);

                TextView tvSub = new TextView(this);
                tvSub.setText("Tersimpan di perangkat");
                tvSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                tvSub.setTextColor(Color.parseColor("#64748B"));

                colText.addView(tvTitle);
                colText.addView(tvSub);

                TextView tvVal = new TextView(this);
                tvVal.setText((item.contains("[+]") ? "+" : "-") + item.split("—")[0].replaceAll("\\[\\+\\]|\\[\\-\\]", "").trim());
                tvVal.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                tvVal.setTypeface(Typeface.DEFAULT_BOLD);
                tvVal.setTextColor(Color.parseColor(item.contains("[+]") ? "#10B981" : "#EF4444"));

                card.addView(tvIcon);
                card.addView(colText);
                card.addView(tvVal);

                listLayout.addView(card);
            }

            TextView tvFound = new TextView(this);
            tvFound.setText(count + " transaksi ditemukan");
            tvFound.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            tvFound.setTextColor(Color.parseColor("#64748B"));
            tvFound.setPadding(0, 0, 0, dpToPx(8));
            listLayout.addView(tvFound, 0);
        }
        containerContent.addView(listLayout);
    }

    // ================= TAB 3: REKAP =================
    private void renderRekap() {
        TextView tvSub = new TextView(this);
        tvSub.setText("ANALISIS KEUANGAN");
        tvSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        tvSub.setTypeface(Typeface.DEFAULT_BOLD);
        tvSub.setTextColor(Color.parseColor("#F59E0B"));

        TextView tvTitle = new TextView(this);
        tvTitle.setText("Rekap");
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
        tvTitle.setTextColor(Color.WHITE);

        TextView tvDesc = new TextView(this);
        tvDesc.setText("Pantau arus uang dengan ringkas dan jelas.");
        tvDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        tvDesc.setTextColor(Color.parseColor("#64748B"));
        tvDesc.setPadding(0, dpToPx(2), 0, dpToPx(12));

        containerContent.addView(tvSub);
        containerContent.addView(tvTitle);
        containerContent.addView(tvDesc);

        // FILTER PERIODE
        LinearLayout periodRow = new LinearLayout(this);
        periodRow.setOrientation(LinearLayout.HORIZONTAL);
        periodRow.setPadding(0, 0, 0, dpToPx(12));

        String[] periods = {"Harian", "Mingguan", "Bulanan", "Tahunan"};
        for (String p : periods) {
            TextView btnP = new TextView(this);
            btnP.setText(p);
            btnP.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
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
            gd.setCornerRadius(dpToPx(10));
            btnP.setBackground(gd);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dpToPx(32), 1.0f);
            lp.setMargins(dpToPx(2), 0, dpToPx(2), 0);
            btnP.setLayoutParams(lp);
            btnP.setOnClickListener(v -> {
                periodFilter = p;
                renderTab(2);
            });
            periodRow.addView(btnP);
        }
        containerContent.addView(periodRow);

        long in = pref.getLong("in", 0);
        long out = pref.getLong("out", 0);
        long tab = pref.getLong("tabungan", 0);
        long emg = pref.getLong("darurat", 0);
        long saldo = in - out - tab - emg;

        // BIG REKAP CARD
        LinearLayout cardBig = new LinearLayout(this);
        cardBig.setOrientation(LinearLayout.VERTICAL);
        cardBig.setPadding(dpToPx(14), dpToPx(14), dpToPx(14), dpToPx(14));

        GradientDrawable gdBig = new GradientDrawable();
        gdBig.setColor(Color.parseColor("#121721"));
        gdBig.setCornerRadius(dpToPx(14));
        cardBig.setBackground(gdBig);

        TextView tvBigLabel = new TextView(this);
        tvBigLabel.setText("SISA BERSIH PERIODE INI");
        tvBigLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
        tvBigLabel.setTextColor(Color.parseColor("#64748B"));

        TextView tvBigVal = new TextView(this);
        tvBigVal.setText(formatRupiah(saldo));
        tvBigVal.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        tvBigVal.setTypeface(Typeface.DEFAULT_BOLD);
        tvBigVal.setTextColor(Color.WHITE);

        cardBig.addView(tvBigLabel);
        cardBig.addView(tvBigVal);
        
        LinearLayout.LayoutParams lpBig = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lpBig.setMargins(0, 0, 0, dpToPx(10));
        cardBig.setLayoutParams(lpBig);
        containerContent.addView(cardBig);

        // GRID REKAP 2x2
        LinearLayout row1 = new LinearLayout(this);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        row1.addView(createSimpleGridCard("Pendapatan", formatShort(in), "#10B981"));
        row1.addView(createSimpleGridCard("Pengeluaran", formatShort(out), "#EF4444"));
        containerContent.addView(row1);

        LinearLayout row2 = new LinearLayout(this);
        row2.setOrientation(LinearLayout.HORIZONTAL);
        row2.addView(createSimpleGridCard("Tabungan", formatShort(tab), "#3B82F6"));
        row2.addView(createSimpleGridCard("Dana Darurat", formatShort(emg), "#F59E0B"));
        containerContent.addView(row2);

        // PERBANDINGAN ARUS UANG
        TextView tvArus = new TextView(this);
        tvArus.setText("Perbandingan arus uang");
        tvArus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tvArus.setTypeface(Typeface.DEFAULT_BOLD);
        tvArus.setTextColor(Color.WHITE);
        tvArus.setPadding(0, dpToPx(12), 0, dpToPx(8));
        containerContent.addView(tvArus);

        LinearLayout cardArus = new LinearLayout(this);
        cardArus.setOrientation(LinearLayout.VERTICAL);
        cardArus.setPadding(dpToPx(14), dpToPx(14), dpToPx(14), dpToPx(14));

        GradientDrawable gdArus = new GradientDrawable();
        gdArus.setColor(Color.parseColor("#121721"));
        gdArus.setCornerRadius(dpToPx(14));
        cardArus.setBackground(gdArus);

        cardArus.addView(createArusRow("Pendapatan", in, Math.max(in, 1), "#10B981"));
        cardArus.addView(createArusRow("Pengeluaran", out, Math.max(in, 1), "#EF4444"));
        cardArus.addView(createArusRow("Tabungan", tab, Math.max(in, 1), "#3B82F6"));
        cardArus.addView(createArusRow("Dana Darurat", emg, Math.max(in, 1), "#F59E0B"));

        containerContent.addView(cardArus);
    }

    private View createSimpleGridCard(String label, String val, String colorHex) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10));

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#121721"));
        gd.setCornerRadius(dpToPx(12));
        card.setBackground(gd);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        params.setMargins(dpToPx(3), 0, dpToPx(3), dpToPx(6));
        card.setLayoutParams(params);

        TextView tvL = new TextView(this);
        tvL.setText(label);
        tvL.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        tvL.setTextColor(Color.parseColor("#64748B"));

        TextView tvV = new TextView(this);
        tvV.setText(val);
        tvV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tvV.setTypeface(Typeface.DEFAULT_BOLD);
        tvV.setTextColor(Color.parseColor(colorHex));

        card.addView(tvL);
        card.addView(tvV);
        return card;
    }

    private View createArusRow(String label, long val, long max, String colorHex) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dpToPx(4), 0, dpToPx(4));

        TextView tvL = new TextView(this);
        tvL.setText(label);
        tvL.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        tvL.setTextColor(Color.parseColor("#94A3B8"));
        tvL.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(80), ViewGroup.LayoutParams.WRAP_CONTENT));

        ProgressBar pb = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        pb.setMax((int) max);
        pb.setProgress((int) val);
        pb.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

        TextView tvV = new TextView(this);
        tvV.setText(formatShort(val));
        tvV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        tvV.setTypeface(Typeface.DEFAULT_BOLD);
        tvV.setTextColor(Color.WHITE);
        tvV.setGravity(Gravity.END);
        tvV.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(60), ViewGroup.LayoutParams.WRAP_CONTENT));

        row.addView(tvL);
        row.addView(pb);
        row.addView(tvV);
        return row;
    }

    // ================= TAB 4: PENGATURAN =================
    private void renderPengaturan() {
        TextView tvSub = new TextView(this);
        tvSub.setText("PREFERENSI LOKAL");
        tvSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        tvSub.setTypeface(Typeface.DEFAULT_BOLD);
        tvSub.setTextColor(Color.parseColor("#F59E0B"));

        TextView tvTitle = new TextView(this);
        tvTitle.setText("Pengaturan");
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
        tvTitle.setTextColor(Color.WHITE);

        TextView tvDesc = new TextView(this);
        tvDesc.setText("Semua data tersimpan aman secara offline di HP Anda.");
        tvDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        tvDesc.setTextColor(Color.parseColor("#64748B"));
        tvDesc.setPadding(0, dpToPx(2), 0, dpToPx(14));

        containerContent.addView(tvSub);
        containerContent.addView(tvTitle);
        containerContent.addView(tvDesc);

        // TARGET KEUANGAN
        TextView tvSec1 = new TextView(this);
        tvSec1.setText("🚩 Target Keuangan Utama");
        tvSec1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvSec1.setTypeface(Typeface.DEFAULT_BOLD);
        tvSec1.setTextColor(Color.WHITE);
        tvSec1.setPadding(0, 0, 0, dpToPx(6));
        containerContent.addView(tvSec1);

        EditText etTargetTab = createInput("Target Tabungan (Rp)", String.valueOf(pref.getLong("target_tabungan", 10000000)));
        EditText etTargetEmg = createInput("Target Dana Darurat (Rp)", String.valueOf(pref.getLong("target_darurat", 15000000)));
        containerContent.addView(etTargetTab);
        containerContent.addView(etTargetEmg);

        // PENGINGAT PENYISIHAN
        TextView tvSec2 = new TextView(this);
        tvSec2.setText("🔔 Pengingat Penyisihan");
        tvSec2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvSec2.setTypeface(Typeface.DEFAULT_BOLD);
        tvSec2.setTextColor(Color.WHITE);
        tvSec2.setPadding(0, dpToPx(12), 0, dpToPx(6));
        containerContent.addView(tvSec2);

        LinearLayout cardRemind = new LinearLayout(this);
        cardRemind.setOrientation(LinearLayout.HORIZONTAL);
        cardRemind.setGravity(Gravity.CENTER_VERTICAL);
        cardRemind.setPadding(dpToPx(12), dpToPx(10), dpToPx(12), dpToPx(10));

        GradientDrawable gdRem = new GradientDrawable();
        gdRem.setColor(Color.parseColor("#121721"));
        gdRem.setCornerRadius(dpToPx(12));
        cardRemind.setBackground(gdRem);

        LinearLayout colRem = new LinearLayout(this);
        colRem.setOrientation(LinearLayout.VERTICAL);
        colRem.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

        TextView tvRemTitle = new TextView(this);
        tvRemTitle.setText("Pengingat Evaluasi Gaji & Ojol");
        tvRemTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        tvRemTitle.setTypeface(Typeface.DEFAULT_BOLD);
        tvRemTitle.setTextColor(Color.WHITE);

        TextView tvRemSub = new TextView(this);
        tvRemSub.setText("Setiap tgl 10 & akhir pekan pukul 20:00");
        tvRemSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        tvRemSub.setTextColor(Color.parseColor("#64748B"));

        colRem.addView(tvRemTitle);
        colRem.addView(tvRemSub);

        Switch sw = new Switch(this);
        sw.setChecked(pref.getBoolean("reminder_active", false));
        sw.setOnCheckedChangeListener((buttonView, isChecked) -> pref.edit().putBoolean("reminder_active", isChecked).apply());

        cardRemind.addView(colRem);
        cardRemind.addView(sw);
        containerContent.addView(cardRemind);

        // BUTTON RESET & SIMPAN
        Button btnSave = new Button(this);
        btnSave.setText("Simpan Target");
        btnSave.setTextColor(Color.parseColor("#0F172A"));
        btnSave.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        btnSave.setTypeface(Typeface.DEFAULT_BOLD);

        GradientDrawable gdSave = new GradientDrawable();
        gdSave.setColor(Color.parseColor("#F59E0B"));
        gdSave.setCornerRadius(dpToPx(12));
        btnSave.setBackground(gdSave);

        LinearLayout.LayoutParams lpSave = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(40));
        lpSave.setMargins(0, dpToPx(14), 0, dpToPx(6));
        btnSave.setLayoutParams(lpSave);
        btnSave.setOnClickListener(v -> {
            String tabS = etTargetTab.getText().toString();
            String emgS = etTargetEmg.getText().toString();
            if (!tabS.isEmpty()) pref.edit().putLong("target_tabungan", Long.parseLong(tabS)).apply();
            if (!emgS.isEmpty()) pref.edit().putLong("target_darurat", Long.parseLong(emgS)).apply();
            Toast.makeText(MainActivity.this, "Pengaturan berhasil disimpan!", Toast.LENGTH_SHORT).show();
            renderTab(0);
        });
        containerContent.addView(btnSave);

        Button btnReset = new Button(this);
        btnReset.setText("Reset Semua Data");
        btnReset.setTextColor(Color.WHITE);
        btnReset.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        btnReset.setTypeface(Typeface.DEFAULT_BOLD);

        GradientDrawable gdReset = new GradientDrawable();
        gdReset.setColor(Color.parseColor("#121721"));
        gdReset.setCornerRadius(dpToPx(12));
        btnReset.setBackground(gdReset);

        LinearLayout.LayoutParams lpReset = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(40));
        btnReset.setLayoutParams(lpReset);
        btnReset.setOnClickListener(v -> {
            pref.edit().clear().apply();
            Toast.makeText(MainActivity.this, "Data berhasil dibersihkan!", Toast.LENGTH_SHORT).show();
            renderTab(0);
        });
        containerContent.addView(btnReset);

        TextView tvAuthor = new TextView(this);
        tvAuthor.setText("\nINCOMESTRUCT v1.0\nCreated by Helmi Zainul Pahmi");
        tvAuthor.setTextColor(Color.parseColor("#F59E0B"));
        tvAuthor.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        tvAuthor.setTypeface(Typeface.DEFAULT_BOLD);
        tvAuthor.setGravity(Gravity.CENTER_HORIZONTAL);
        tvAuthor.setPadding(0, dpToPx(20), 0, 0);
        containerContent.addView(tvAuthor);
    }

    private EditText createInput(String hint, String val) {
        EditText et = new EditText(this);
        et.setHint(hint);
        et.setText(val);
        et.setHintTextColor(Color.parseColor("#64748B"));
        et.setTextColor(Color.WHITE);
        et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        et.setInputType(InputType.TYPE_CLASS_NUMBER);
        et.setPadding(dpToPx(12), dpToPx(8), dpToPx(12), dpToPx(8));

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#121721"));
        gd.setCornerRadius(dpToPx(10));
        et.setBackground(gd);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, dpToPx(6));
        et.setLayoutParams(lp);
        return et;
    }

    // HELPER FORMATTING
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

    private void showFormDialog(final String type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("CATAT " + type.toUpperCase());

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dpToPx(20), dpToPx(10), dpToPx(20), dpToPx(10));

        final Spinner spinnerCategory = new Spinner(this);
        String[] options;
        if (type.equals("in")) {
            options = new String[]{"Gaji Bulanan (Tgl 10)", "Ojol Grab (Weekend)", "Shopee (Weekend)", "Pemasukan Lainnya"};
        } else if (type.equals("out")) {
            options = new String[]{"Cicilan Rutin", "Arisan", "Uang Orang Tua", "Uang Saku Adek (2 Orang)", "Uang Jajan Keponakan (2 Orang)", "Kebutuhan Sehari-hari"};
        } else {
            options = new String[]{"Pos Tabungan", "Pos Dana Darurat"};
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, options);
        spinnerCategory.setAdapter(adapter);
        layout.addView(spinnerCategory);

        final EditText inputJumlah = new EditText(this);
        inputJumlah.setHint("Nominal (Rp)");
        inputJumlah.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputJumlah);

        final EditText inputKet = new EditText(this);
        inputKet.setHint("Catatan tambahan (Opsional)");
        layout.addView(inputKet);

        builder.setView(layout);

        builder.setPositiveButton("Simpan", (dialog, which) -> {
            String nom = inputJumlah.getText().toString();
            String kat = spinnerCategory.getSelectedItem().toString();
            String ket = inputKet.getText().toString();

            if (!nom.isEmpty()) {
                long val = Long.parseLong(nom);
                long current = pref.getLong(type, 0);
                pref.edit().putLong(type, current + val).apply();

                String logs = pref.getString("logs", "");
                String tag = (type.equals("in") || type.equals("tabungan") || type.equals("darurat")) ? "[+] " : "[-] ";
                String detail = kat + (ket.isEmpty() ? "" : " (" + ket + ")");
                logs = tag + formatRupiah(val) + " — " + detail + "\n" + logs;
                pref.edit().putString("logs", logs).apply();

                renderTab(currentTab);
            }
        });
        builder.setNegativeButton("Batal", null);
        builder.show();
    }
}
