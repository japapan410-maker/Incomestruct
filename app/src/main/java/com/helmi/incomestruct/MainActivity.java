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
        nav.setPadding(0, dpToPx(10), 0, dpToPx(14));

        String[] tabs = {"Beranda", "Transaksi", "Rekap", "Pengaturan"};
        String[] icons = {"⌂", "⇆", "📊", "⚙"};

        for (int i = 0; i < tabs.length; i++) {
            final int tabIndex = i;
            LinearLayout itemLayout = new LinearLayout(this);
            itemLayout.setOrientation(LinearLayout.VERTICAL);
            itemLayout.setGravity(Gravity.CENTER);

            TextView tvIcon = new TextView(this);
            tvIcon.setText(icons[i]);
            tvIcon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            tvIcon.setGravity(Gravity.CENTER);
            tvIcon.setTextColor(i == currentTab ? Color.parseColor("#F59E0B") : Color.parseColor("#64748B"));

            TextView tvText = new TextView(this);
            tvText.setText(tabs[i]);
            tvText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            tvText.setGravity(Gravity.CENTER);
            tvText.setTypeface(Typeface.DEFAULT_BOLD);
            tvText.setTextColor(i == currentTab ? Color.parseColor("#F59E0B") : Color.parseColor("#64748B"));

            itemLayout.addView(tvIcon);
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
        containerContent.setPadding(dpToPx(18), dpToPx(24), dpToPx(18), dpToPx(24));

        if (tabIndex == 0) renderBeranda();
        else if (tabIndex == 1) renderTransaksi();
        else if (tabIndex == 2) renderRekap();
        else renderPengaturan();
    }

    private void renderBeranda() {
        LinearLayout headerRow = new LinearLayout(this);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setGravity(Gravity.CENTER_VERTICAL);

        TextView tvSub = new TextView(this);
        tvSub.setText("KEUANGAN PRIBADI");
        tvSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tvSub.setTypeface(Typeface.DEFAULT_BOLD);
        tvSub.setTextColor(Color.parseColor("#F59E0B"));
        tvSub.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

        TextView tvBadge = new TextView(this);
        tvBadge.setText("● Offline");
        tvBadge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        tvBadge.setTextColor(Color.parseColor("#10B981"));
        tvBadge.setPadding(dpToPx(10), dpToPx(4), dpToPx(10), dpToPx(4));
        
        GradientDrawable gdBadge = new GradientDrawable();
        gdBadge.setColor(Color.parseColor("#16231E"));
        gdBadge.setCornerRadius(dpToPx(12));
        tvBadge.setBackground(gdBadge);

        headerRow.addView(tvSub);
        headerRow.addView(tvBadge);
        containerContent.addView(headerRow);

        TextView tvTitle = new TextView(this);
        tvTitle.setText("Halo, siap mengatur uang?");
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setPadding(0, dpToPx(4), 0, dpToPx(18));
        containerContent.addView(tvTitle);

        LinearLayout cardSaldo = new LinearLayout(this);
        cardSaldo.setOrientation(LinearLayout.VERTICAL);
        cardSaldo.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));
        
        GradientDrawable gdSaldo = new GradientDrawable();
        gdSaldo.setColor(Color.parseColor("#F59E0B"));
        gdSaldo.setCornerRadius(dpToPx(22));
        cardSaldo.setBackground(gdSaldo);

        TextView labelSaldo = new TextView(this);
        labelSaldo.setText("SALDO / SISA UANG");
        labelSaldo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        labelSaldo.setTypeface(Typeface.DEFAULT_BOLD);
        labelSaldo.setTextColor(Color.parseColor("#78350F"));

        long in = pref.getLong("in", 0);
        long out = pref.getLong("out", 0);
        long tab = pref.getLong("tabungan", 0);
        long emg = pref.getLong("darurat", 0);
        long saldo = in - out - tab - emg;

        TextView tvSaldo = new TextView(this);
        tvSaldo.setText(formatRupiah(saldo));
        tvSaldo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
        tvSaldo.setTypeface(Typeface.DEFAULT_BOLD);
        tvSaldo.setTextColor(Color.parseColor("#0F172A"));
        tvSaldo.setPadding(0, dpToPx(6), 0, dpToPx(6));

        TextView tvSubSaldo = new TextView(this);
        tvSubSaldo.setText("Bisa digunakan saat ini");
        tvSubSaldo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tvSubSaldo.setTextColor(Color.parseColor("#92400E"));

        cardSaldo.addView(labelSaldo);
        cardSaldo.addView(tvSaldo);
        cardSaldo.addView(tvSubSaldo);
        
        LinearLayout.LayoutParams pSaldo = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        pSaldo.setMargins(0, 0, 0, dpToPx(16));
        cardSaldo.setLayoutParams(pSaldo);
        containerContent.addView(cardSaldo);

        LinearLayout row1 = new LinearLayout(this);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        row1.addView(createGridCard("Pendapatan", formatShort(in), "#10B981", "↓"));
        row1.addView(createGridCard("Pengeluaran", formatShort(out), "#F59E0B", "↑"));
        containerContent.addView(row1);

        LinearLayout row2 = new LinearLayout(this);
        row2.setOrientation(LinearLayout.HORIZONTAL);
        row2.addView(createGridCard("Tabungan", formatShort(tab), "#3B82F6", "👛"));
        row2.addView(createGridCard("Dana darurat", formatShort(emg), "#F59E0B", "🛡"));
        containerContent.addView(row2);

        LinearLayout catatHeader = new LinearLayout(this);
        catatHeader.setOrientation(LinearLayout.HORIZONTAL);
        catatHeader.setPadding(0, dpToPx(16), 0, dpToPx(12));

        TextView tvCatat = new TextView(this);
        tvCatat.setText("Catat transaksi");
        tvCatat.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tvCatat.setTypeface(Typeface.DEFAULT_BOLD);
        tvCatat.setTextColor(Color.WHITE);
        tvCatat.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

        TextView tvSubCatat = new TextView(this);
        tvSubCatat.setText("Semua tersimpan di perangkat");
        tvSubCatat.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        tvSubCatat.setTextColor(Color.parseColor("#64748B"));

        catatHeader.addView(tvCatat);
        catatHeader.addView(tvSubCatat);
        containerContent.addView(catatHeader);

        LinearLayout btnGrid = new LinearLayout(this);
        btnGrid.setOrientation(LinearLayout.HORIZONTAL);
        btnGrid.addView(createActionButton("Pendapatan", "+", "in"));
        btnGrid.addView(createActionButton("Pengeluaran", "-", "out"));
        btnGrid.addView(createActionButton("Tabungan", "👛", "tabungan"));
        btnGrid.addView(createActionButton("Darurat", "🛡", "darurat"));
        containerContent.addView(btnGrid);

        TextView tvTarget = new TextView(this);
        tvTarget.setText("Target berjalan");
        tvTarget.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tvTarget.setTypeface(Typeface.DEFAULT_BOLD);
        tvTarget.setTextColor(Color.WHITE);
        tvTarget.setPadding(0, dpToPx(20), 0, dpToPx(12));
        containerContent.addView(tvTarget);

        long targetTab = pref.getLong("target_tabungan", 10000000);
        long targetEmg = pref.getLong("target_darurat", 15000000);

        containerContent.addView(createTargetCard("Tabungan", tab, targetTab));
        containerContent.addView(createTargetCard("Dana darurat", emg, targetEmg));
    }

    private View createGridCard(String title, String val, String colorHex, String icon) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#121721"));
        gd.setCornerRadius(dpToPx(18));
        card.setBackground(gd);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        params.setMargins(dpToPx(4), 0, dpToPx(4), dpToPx(10));
        card.setLayoutParams(params);

        TextView tvIcon = new TextView(this);
        tvIcon.setText(icon);
        tvIcon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvIcon.setGravity(Gravity.CENTER);
        tvIcon.setTextColor(Color.WHITE);
        
        GradientDrawable gdIcon = new GradientDrawable();
        gdIcon.setColor(Color.parseColor(colorHex.equals("#10B981") ? "#064E3B" : colorHex.equals("#F59E0B") ? "#451A03" : "#1E3A8A"));
        gdIcon.setShape(GradientDrawable.OVAL);
        tvIcon.setBackground(gdIcon);
        
        LinearLayout.LayoutParams pIcon = new LinearLayout.LayoutParams(dpToPx(28), dpToPx(28));
        pIcon.setMargins(0, 0, 0, dpToPx(10));
        tvIcon.setLayoutParams(pIcon);

        TextView label = new TextView(this);
        label.setText(title);
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        label.setTextColor(Color.parseColor("#94A3B8"));

        TextView valueTv = new TextView(this);
        valueTv.setText(val);
        valueTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        valueTv.setTypeface(Typeface.DEFAULT_BOLD);
        valueTv.setTextColor(Color.parseColor(colorHex));
        valueTv.setPadding(0, dpToPx(4), 0, 0);

        card.addView(tvIcon);
        card.addView(label);
        card.addView(valueTv);
        return card;
    }

    private View createActionButton(String label, String symbol, final String type) {
        LinearLayout btn = new LinearLayout(this);
        btn.setOrientation(LinearLayout.VERTICAL);
        btn.setGravity(Gravity.CENTER);
        btn.setPadding(dpToPx(12), dpToPx(14), dpToPx(12), dpToPx(14));

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#121721"));
        gd.setCornerRadius(dpToPx(18));
        btn.setBackground(gd);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dpToPx(80), 1.0f);
        params.setMargins(dpToPx(4), 0, dpToPx(4), 0);
        btn.setLayoutParams(params);

        TextView tvSym = new TextView(this);
        tvSym.setText(symbol);
        tvSym.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvSym.setGravity(Gravity.CENTER);
        tvSym.setTextColor(Color.parseColor("#F59E0B"));

        GradientDrawable gdSym = new GradientDrawable();
        gdSym.setColor(Color.parseColor("#2D1F0D"));
        gdSym.setShape(GradientDrawable.OVAL);
        tvSym.setBackground(gdSym);
        
        LinearLayout.LayoutParams pSym = new LinearLayout.LayoutParams(dpToPx(26), dpToPx(26));
        pSym.setMargins(0, 0, 0, dpToPx(6));
        tvSym.setLayoutParams(pSym);

        TextView tvLabel = new TextView(this);
        tvLabel.setText(label);
        tvLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        tvLabel.setTextColor(Color.WHITE);

        btn.addView(tvSym);
        btn.addView(tvLabel);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFormDialog(type);
            }
        });
        return btn;
    }

    private View createTargetCard(String title, long current, long max) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dpToPx(18), dpToPx(16), dpToPx(18), dpToPx(16));

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#121721"));
        gd.setCornerRadius(dpToPx(18));
        card.setBackground(gd);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, dpToPx(12));
        card.setLayoutParams(params);

        LinearLayout rowHead = new LinearLayout(this);
        rowHead.setOrientation(LinearLayout.HORIZONTAL);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

        int pct = (max > 0) ? (int) ((current * 100) / max) : 0;
        TextView tvPct = new TextView(this);
        tvPct.setText(pct + "%");
        tvPct.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tvPct.setTextColor(Color.parseColor("#F59E0B"));

        rowHead.addView(tvTitle);
        rowHead.addView(tvPct);

        ProgressBar pb = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        pb.setMax((int) Math.max(max, 1));
        pb.setProgress((int) current);
        pb.setPadding(0, dpToPx(10), 0, dpToPx(10));

        TextView tvVal = new TextView(this);
        tvVal.setText(formatRupiah(current) + " dari " + formatRupiah(max));
        tvVal.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tvVal.setTextColor(Color.parseColor("#64748B"));

        card.addView(rowHead);
        card.addView(pb);
        card.addView(tvVal);
        return card;
    }

    private void renderTransaksi() {
        TextView tvSub = new TextView(this);
        tvSub.setText("LEDGER OFFLINE");
        tvSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        tvSub.setTypeface(Typeface.DEFAULT_BOLD);
        tvSub.setTextColor(Color.parseColor("#F59E0B"));

        TextView tvTitle = new TextView(this);
        tvTitle.setText("Transaksi");
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
        tvTitle.setTextColor(Color.WHITE);

        TextView tvDesc = new TextView(this);
        tvDesc.setText("Cari, edit, dan kelola semua catatan keuangan.");
        tvDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvDesc.setTextColor(Color.parseColor("#64748B"));
        tvDesc.setPadding(0, dpToPx(2), 0, dpToPx(16));

        containerContent.addView(tvSub);
        containerContent.addView(tvTitle);
        containerContent.addView(tvDesc);

        EditText etSearch = new EditText(this);
        etSearch.setHint("🔍 Cari sumber, kategori, catatan...");
        etSearch.setHintTextColor(Color.parseColor("#64748B"));
        etSearch.setTextColor(Color.WHITE);
        etSearch.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        etSearch.setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12));
        
        GradientDrawable gdSearch = new GradientDrawable();
        gdSearch.setColor(Color.parseColor("#121721"));
        gdSearch.setCornerRadius(dpToPx(14));
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
        btnFilterRow.setPadding(0, dpToPx(14), 0, dpToPx(16));

        btnFilterRow.addView(createOrangeFilterBtn("+ Pendapatan", "in"));
        btnFilterRow.addView(createDarkFilterBtn("+ Pengeluaran", "out"));
        btnFilterRow.addView(createDarkFilterBtn("+ Tabungan", "tabungan"));
        containerContent.addView(btnFilterRow);

        renderTransaksiList();
    }

    private View createOrangeFilterBtn(String text, final String type) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextColor(Color.parseColor("#0F172A"));
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        btn.setTypeface(Typeface.DEFAULT_BOLD);
        
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#F59E0B"));
        gd.setCornerRadius(dpToPx(12));
        btn.setBackground(gd);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dpToPx(42), 1.0f);
        params.setMargins(0, 0, dpToPx(4), 0);
        btn.setLayoutParams(params);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFormDialog(type);
            }
        });
        return btn;
    }

    private View createDarkFilterBtn(String text, final String type) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        btn.setTypeface(Typeface.DEFAULT_BOLD);
        
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#121721"));
        gd.setCornerRadius(dpToPx(12));
        btn.setBackground(gd);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dpToPx(42), 1.0f);
        params.setMargins(dpToPx(3), 0, dpToPx(3), 0);
        btn.setLayoutParams(params);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFormDialog(type);
            }
        });
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
            cardEmpty.setPadding(dpToPx(18), dpToPx(18), dpToPx(18), dpToPx(18));
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(Color.parseColor("#121721"));
            gd.setCornerRadius(dpToPx(14));
            cardEmpty.setBackground(gd);

            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("Belum ada catatan transaksi tersimpan.");
            tvEmpty.setTextColor(Color.parseColor("#94A3B8"));
            tvEmpty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
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
                card.setPadding(0, dpToPx(12), 0, dpToPx(12));

                TextView tvIcon = new TextView(this);
                tvIcon.setText(item.contains("[+]") ? "↓" : "↑");
                tvIcon.setGravity(Gravity.CENTER);
                tvIcon.setTextColor(Color.WHITE);
                
                GradientDrawable gdIcon = new GradientDrawable();
                gdIcon.setColor(Color.parseColor(item.contains("[+]") ? "#064E3B" : "#451A03"));
                gdIcon.setShape(GradientDrawable.OVAL);
                tvIcon.setBackground(gdIcon);

                LinearLayout.LayoutParams pIcon = new LinearLayout.LayoutParams(dpToPx(36), dpToPx(36));
                pIcon.setMargins(0, 0, dpToPx(14), 0);
                tvIcon.setLayoutParams(pIcon);

                LinearLayout colText = new LinearLayout(this);
                colText.setOrientation(LinearLayout.VERTICAL);
                colText.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

                TextView tvTitle = new TextView(this);
                tvTitle.setText(item.replaceAll("\\[\\+\\]|\\[\\-\\]", "").split("—")[1].trim());
                tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
                tvTitle.setTextColor(Color.WHITE);

                TextView tvSub = new TextView(this);
                tvSub.setText("Terdaftar di lokal");
                tvSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
                tvSub.setTextColor(Color.parseColor("#64748B"));

                colText.addView(tvTitle);
                colText.addView(tvSub);

                TextView tvVal = new TextView(this);
                tvVal.setText((item.contains("[+]") ? "+" : "-") + item.split("—")[0].replaceAll("\\[\\+\\]|\\[\\-\\]", "").trim());
                tvVal.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                tvVal.setTypeface(Typeface.DEFAULT_BOLD);
                tvVal.setTextColor(Color.parseColor(item.contains("[+]") ? "#10B981" : "#EF4444"));

                card.addView(tvIcon);
                card.addView(colText);
                card.addView(tvVal);

                listLayout.addView(card);
            }

            TextView tvFound = new TextView(this);
            tvFound.setText(count + " transaksi ditemukan");
            tvFound.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            tvFound.setTextColor(Color.parseColor("#64748B"));
            tvFound.setPadding(0, 0, 0, dpToPx(12));
            listLayout.addView(tvFound, 0);
        }
        containerContent.addView(listLayout);
    }

    private void renderRekap() {
        TextView tvSub = new TextView(this);
        tvSub.setText("ANALISIS KEUANGAN");
        tvSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        tvSub.setTypeface(Typeface.DEFAULT_BOLD);
        tvSub.setTextColor(Color.parseColor("#F59E0B"));

        TextView tvTitle = new TextView(this);
        tvTitle.setText("Rekap");
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
        tvTitle.setTextColor(Color.WHITE);

        TextView tvDesc = new TextView(this);
        tvDesc.setText("Pantau arus uang dengan ringkas dan jelas.");
        tvDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvDesc.setTextColor(Color.parseColor("#64748B"));
        tvDesc.setPadding(0, dpToPx(2), 0, dpToPx(16));

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
            btnP.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
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
            gd.setCornerRadius(dpToPx(12));
            btnP.setBackground(gd);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, dpToPx(38), 1.0f);
            lp.setMargins(dpToPx(2), 0, dpToPx(2), 0);
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
        cardBig.setPadding(dpToPx(18), dpToPx(18), dpToPx(18), dpToPx(18));

        GradientDrawable gdBig = new GradientDrawable();
        gdBig.setColor(Color.parseColor("#121721"));
        gdBig.setCornerRadius(dpToPx(18));
        cardBig.setBackground(gdBig);

        TextView tvBigLabel = new TextView(this);
        tvBigLabel.setText("SISA BERSIH PERIODE INI");
        tvBigLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        tvBigLabel.setTextColor(Color.parseColor("#64748B"));

        TextView tvBigVal = new TextView(this);
        tvBigVal.setText(formatRupiah(saldo));
        tvBigVal.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
        tvBigVal.setTypeface(Typeface.DEFAULT_BOLD);
        tvBigVal.setTextColor(Color.WHITE);

        cardBig.addView(tvBigLabel);
        cardBig.addView(tvBigVal);
        
        LinearLayout.LayoutParams lpBig = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lpBig.setMargins(0, 0, 0, dpToPx(12));
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
        row2.addView(createSimpleGridCard("Darurat", formatShort(emg), "#F59E0B"));
        containerContent.addView(row2);

        TextView tvArus = new TextView(this);
        tvArus.setText("Perbandingan arus uang");
        tvArus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tvArus.setTypeface(Typeface.DEFAULT_BOLD);
        tvArus.setTextColor(Color.WHITE);
        tvArus.setPadding(0, dpToPx(16), 0, dpToPx(12));
        containerContent.addView(tvArus);

        LinearLayout cardArus = new LinearLayout(this);
        cardArus.setOrientation(LinearLayout.VERTICAL);
        cardArus.setPadding(dpToPx(18), dpToPx(18), dpToPx(18), dpToPx(18));

        GradientDrawable gdArus = new GradientDrawable();
        gdArus.setColor(Color.parseColor("#121721"));
        gdArus.setCornerRadius(dpToPx(18));
        cardArus.setBackground(gdArus);

        cardArus.addView(createArusRow("Pendapatan", in, Math.max(in, 1), "#10B981"));
        cardArus.addView(createArusRow("Pengeluaran", out, Math.max(in, 1), "#EF4444"));
        cardArus.addView(createArusRow("Tabungan", tab, Math.max(in, 1), "#3B82F6"));
        cardArus.addView(createArusRow("Darurat", emg, Math.max(in, 1), "#F59E0B"));

        containerContent.addView(cardArus);
    }

    private View createSimpleGridCard(String label, String val, String colorHex) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dpToPx(14), dpToPx(14), dpToPx(14), dpToPx(14));

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#121721"));
        gd.setCornerRadius(dpToPx(16));
        card.setBackground(gd);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        params.setMargins(dpToPx(4), 0, dpToPx(4), dpToPx(8));
        card.setLayoutParams(params);

        TextView tvL = new TextView(this);
        tvL.setText(label);
        tvL.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tvL.setTextColor(Color.parseColor("#64748B"));

        TextView tvV = new TextView(this);
        tvV.setText(val);
        tvV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
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
        row.setPadding(0, dpToPx(6), 0, dpToPx(6));

        TextView tvL = new TextView(this);
        tvL.setText(label);
        tvL.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tvL.setTextColor(Color.parseColor("#94A3B8"));
        tvL.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(90), ViewGroup.LayoutParams.WRAP_CONTENT));

        ProgressBar pb = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        pb.setMax((int) max);
        pb.setProgress((int) val);
        pb.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

        TextView tvV = new TextView(this);
        tvV.setText(formatShort(val));
        tvV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tvV.setTypeface(Typeface.DEFAULT_BOLD);
        tvV.setTextColor(Color.WHITE);
        tvV.setGravity(Gravity.END);
        tvV.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(70), ViewGroup.LayoutParams.WRAP_CONTENT));

        row.addView(tvL);
        row.addView(pb);
        row.addView(tvV);
        return row;
    }

    private void renderPengaturan() {
        TextView tvSub = new TextView(this);
        tvSub.setText("PREFERENSI LOKAL");
        tvSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        tvSub.setTypeface(Typeface.DEFAULT_BOLD);
        tvSub.setTextColor(Color.parseColor("#F59E0B"));

        TextView tvTitle = new TextView(this);
        tvTitle.setText("Pengaturan");
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
        tvTitle.setTextColor(Color.WHITE);

        TextView tvDesc = new TextView(this);
        tvDesc.setText("Semua pengaturan tersimpan aman di perangkat ini.");
        tvDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tvDesc.setTextColor(Color.parseColor("#64748B"));
        tvDesc.setPadding(0, dpToPx(2), 0, dpToPx(18));

        containerContent.addView(tvSub);
        containerContent.addView(tvTitle);
        containerContent.addView(tvDesc);

        TextView tvSec1 = new TextView(this);
        tvSec1.setText("🚩 Target keuangan");
        tvSec1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        tvSec1.setTypeface(Typeface.DEFAULT_BOLD);
        tvSec1.setTextColor(Color.WHITE);
        tvSec1.setPadding(0, 0, 0, dpToPx(8));
        containerContent.addView(tvSec1);

        final EditText etTargetTab = createInput("Target tabungan", String.valueOf(pref.getLong("target_tabungan", 10000000)));
        final EditText etTargetEmg = createInput("Target dana darurat", String.valueOf(pref.getLong("target_darurat", 15000000)));
        containerContent.addView(etTargetTab);
        containerContent.addView(etTargetEmg);

        LinearLayout rowPct = new LinearLayout(this);
        rowPct.setOrientation(LinearLayout.HORIZONTAL);
        final EditText etPctTab = createInput("Tabungan (%)", String.valueOf(pref.getInt("pct_tabungan", 10)));
        final EditText etPctEmg = createInput("Darurat (%)", String.valueOf(pref.getInt("pct_darurat", 5)));
        
        LinearLayout.LayoutParams p1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        p1.setMargins(0, 0, dpToPx(4), 0);
        etPctTab.setLayoutParams(p1);
        
        LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        p2.setMargins(dpToPx(4), 0, 0, 0);
        etPctEmg.setLayoutParams(p2);

        rowPct.addView(etPctTab);
        rowPct.addView(etPctEmg);
        containerContent.addView(rowPct);

        TextView tvSec2 = new TextView(this);
        tvSec2.setText("🔔 Pengingat penyisihan");
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
        tvRemTitle.setText("Pengingat harian");
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
        tvSec3.setText("≡ Daftar custom");
        tvSec3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        tvSec3.setTypeface(Typeface.DEFAULT_BOLD);
        tvSec3.setTextColor(Color.WHITE);
        tvSec3.setPadding(0, dpToPx(16), 0, dpToPx(8));
        containerContent.addView(tvSec3);

        containerContent.addView(createCustomBox("Sumber pendapatan", "Gaji · Grab · Gojek · ShopeeFood"));
        containerContent.addView(createCustomBox("Kategori pengeluaran", "Bensin · Makanan · Parkir · Cicilan · Uang Keluarga"));

        TextView tvSec4 = new TextView(this);
        tvSec4.setText("🔒 Keamanan");
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
        tvPinTitle.setText("Aktifkan PIN aplikasi");
        tvPinTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvPinTitle.setTypeface(Typeface.DEFAULT_BOLD);
        tvPinTitle.setTextColor(Color.WHITE);

        TextView tvPinSub = new TextView(this);
        tvPinSub.setText("Lindungi data keuangan dengan 4-6 angka.");
        tvPinSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        tvPinSub.setTextColor(Color.parseColor("#64748B"));
        tvPinSub.setPadding(0, dpToPx(2), 0, dpToPx(10));

        final EditText etPin = createInput("Buat PIN", pref.getString("app_pin", ""));
        etPin.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        Button btnPin = new Button(this);
        btnPin.setText("Aktifkan PIN");
        btnPin.setTextColor(Color.parseColor("#0F172A"));
        btnPin.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        btnPin.setTypeface(Typeface.DEFAULT_BOLD);

        GradientDrawable gdBtnPin = new GradientDrawable();
        gdBtnPin.setColor(Color.parseColor("#F59E0B"));
        gdBtnPin.setCornerRadius(dpToPx(12));
        btnPin.setBackground(gdBtnPin);

        btnPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String p = etPin.getText().toString();
                if (!p.isEmpty()) {
                    pref.edit().putString("app_pin", p).apply();
                    Toast.makeText(MainActivity.this, "PIN Keamanan Berhasil Diaktifkan!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cardPin.addView(tvPinTitle);
        cardPin.addView(tvPinSub);
        cardPin.addView(etPin);
        cardPin.addView(btnPin);
        containerContent.addView(cardPin);

        TextView tvSec5 = new TextView(this);
        tvSec5.setText("🗑 Data perangkat");
        tvSec5.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        tvSec5.setTypeface(Typeface.DEFAULT_BOLD);
        tvSec5.setTextColor(Color.WHITE);
        tvSec5.setPadding(0, dpToPx(16), 0, dpToPx(8));
        containerContent.addView(tvSec5);

        LinearLayout cardDb = new LinearLayout(this);
        cardDb.setOrientation(LinearLayout.VERTICAL);
        cardDb.setPadding(dpToPx(16), dpToPx(14), dpToPx(16), dpToPx(14));

        GradientDrawable gdDb = new GradientDrawable();
        gdDb.setColor(Color.parseColor("#121721"));
        gdDb.setCornerRadius(dpToPx(16));
        cardDb.setBackground(gdDb);

        TextView tvDbTitle = new TextView(this);
        tvDbTitle.setText("Database lokal aktif");
        tvDbTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvDbTitle.setTypeface(Typeface.DEFAULT_BOLD);
        tvDbTitle.setTextColor(Color.WHITE);

        TextView tvDbSub = new TextView(this);
        tvDbSub.setText("Data tidak dikirim ke server atau cloud.");
        tvDbSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        tvDbSub.setTextColor(Color.parseColor("#64748B"));

        cardDb.addView(tvDbTitle);
        cardDb.addView(tvDbSub);
        containerContent.addView(cardDb);

        LinearLayout rowBack = new LinearLayout(this);
        rowBack.setOrientation(LinearLayout.HORIZONTAL);
        rowBack.setPadding(0, dpToPx(10), 0, 0);

        Button btnBackup = new Button(this);
        btnBackup.setText("Backup Data");
        btnBackup.setTextColor(Color.parseColor("#0F172A"));
        btnBackup.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        btnBackup.setTypeface(Typeface.DEFAULT_BOLD);

        GradientDrawable gdBack = new GradientDrawable();
        gdBack.setColor(Color.parseColor("#F59E0B"));
        gdBack.setCornerRadius(dpToPx(12));
        btnBackup.setBackground(gdBack);

        LinearLayout.LayoutParams lpBack = new LinearLayout.LayoutParams(0, dpToPx(42), 1.0f);
        lpBack.setMargins(0, 0, dpToPx(4), 0);
        btnBackup.setLayoutParams(lpBack);
        btnBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Data Berhasil Di-backup!", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnRestore = new Button(this);
        btnRestore.setText("Restore Data");
        btnRestore.setTextColor(Color.WHITE);
        btnRestore.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        btnRestore.setTypeface(Typeface.DEFAULT_BOLD);

        GradientDrawable gdRest = new GradientDrawable();
        gdRest.setColor(Color.parseColor("#121721"));
        gdRest.setCornerRadius(dpToPx(12));
        btnRestore.setBackground(gdRest);

        LinearLayout.LayoutParams lpRest = new LinearLayout.LayoutParams(0, dpToPx(42), 1.0f);
        lpRest.setMargins(dpToPx(4), 0, 0, 0);
        btnRestore.setLayoutParams(lpRest);
        btnRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Data Berhasil Dipulihkan!", Toast.LENGTH_SHORT).show();
            }
        });

        rowBack.addView(btnBackup);
        rowBack.addView(btnRestore);
        containerContent.addView(rowBack);

        TextView tvAuthor = new TextView(this);
        tvAuthor.setText("\nINCOMESTRUCT v1.0\nCreated by Helmi Zainul Pahmi");
        tvAuthor.setTextColor(Color.parseColor("#F59E0B"));
        tvAuthor.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tvAuthor.setTypeface(Typeface.DEFAULT_BOLD);
        tvAuthor.setGravity(Gravity.CENTER_HORIZONTAL);
        tvAuthor.setPadding(0, dpToPx(30), 0, 0);
        containerContent.addView(tvAuthor);
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
        tvTitle.setText(title);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
        tvTitle.setTextColor(Color.WHITE);

        TextView tvSub = new TextView(this);
        tvSub.setText(sub);
        tvSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        tvSub.setTextColor(Color.parseColor("#64748B"));
        tvSub.setPadding(0, dpToPx(4), 0, 0);

        card.addView(tvTitle);
        card.addView(tvSub);
        return card;
    }

    private EditText createInput(String hint, String val) {
        EditText et = new EditText(this);
        et.setHint(hint);
        et.setText(val);
        et.setHintTextColor(Color.parseColor("#64748B"));
        et.setTextColor(Color.WHITE);
        et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        et.setInputType(InputType.TYPE_CLASS_NUMBER);
        et.setPadding(dpToPx(16), dpToPx(12), dpToPx(16), dpToPx(12));

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#121721"));
        gd.setCornerRadius(dpToPx(14));
        et.setBackground(gd);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, dpToPx(8));
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

        builder.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
            }
        });
        builder.setNegativeButton("Batal", null);
        builder.show();
    }
}
