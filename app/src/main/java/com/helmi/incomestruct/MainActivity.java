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
import java.text.SimpleDateFormat;
import java.util.Date;
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
        nav.setPadding(0, dpToPx(12), 0, dpToPx(16));

        String[] tabs = {"Beranda", "Transaksi", "Rekap", "Pengaturan"};
        String[] icons = {"⌂", "⇆", "📊", "⚙"};

        for (int i = 0; i < tabs.length; i++) {
            final int tabIndex = i;
            LinearLayout itemLayout = new LinearLayout(this);
            itemLayout.setOrientation(LinearLayout.VERTICAL);
            itemLayout.setGravity(Gravity.CENTER);

            TextView tvIcon = new TextView(this);
            tvIcon.setText(icons[i]);
            tvIcon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            tvIcon.setGravity(Gravity.CENTER);
            tvIcon.setTextColor(i == currentTab ? Color.parseColor("#F59E0B") : Color.parseColor("#64748B"));

            TextView tvText = new TextView(this);
            tvText.setText(tabs[i]);
            tvText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
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

        TextView tvTitle = new TextView(this);
        tvTitle.setText("Halo, siap atur keuangan?");
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
        tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setPadding(0, dpToPx(6), 0, dpToPx(20));

        containerContent.addView(tvSub);
        containerContent.addView(tvTitle);

        LinearLayout cardSaldo = new LinearLayout(this);
        cardSaldo.setOrientation(LinearLayout.VERTICAL);
        cardSaldo.setPadding(dpToPx(22), dpToPx(22), dpToPx(22), dpToPx(22));
        
        GradientDrawable gdSaldo = new GradientDrawable();
        gdSaldo.setColor(Color.parseColor("#F59E0B"));
        gdSaldo.setCornerRadius(dpToPx(24));
        cardSaldo.setBackground(gdSaldo);

        TextView labelSaldo = new TextView(this);
        labelSaldo.setText("SALDO / SISA UANG");
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
        tvSaldo.setPadding(0, dpToPx(8), 0, dpToPx(8));

        TextView tvSubSaldo = new TextView(this);
        tvSubSaldo.setText("Aman digunakan saat ini");
        tvSubSaldo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvSubSaldo.setTextColor(Color.parseColor("#78350F"));

        cardSaldo.addView(labelSaldo);
        cardSaldo.addView(tvSaldo);
        cardSaldo.addView(tvSubSaldo);
        
        LinearLayout.LayoutParams pSaldo = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        pSaldo.setMargins(0, 0, 0, dpToPx(18));
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
        row2.addView(createGridCard("Dana Darurat", formatShort(emg), "#F59E0B", "🛡"));
        containerContent.addView(row2);

        TextView tvCatat = new TextView(this);
        tvCatat.setText("Catat Transaksi Baru");
        tvCatat.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tvCatat.setTypeface(Typeface.DEFAULT_BOLD);
        tvCatat.setTextColor(Color.WHITE);
        tvCatat.setPadding(0, dpToPx(20), 0, dpToPx(14));
        containerContent.addView(tvCatat);

        LinearLayout btnGrid = new LinearLayout(this);
        btnGrid.setOrientation(LinearLayout.HORIZONTAL);
        btnGrid.addView(createActionButton("Pendapatan", "+", "in"));
        btnGrid.addView(createActionButton("Pengeluaran", "-", "out"));
        btnGrid.addView(createActionButton("Tabungan", "👛", "tabungan"));
        btnGrid.addView(createActionButton("Darurat", "🛡", "darurat"));
        containerContent.addView(btnGrid);

        TextView tvTarget = new TextView(this);
        tvTarget.setText("Target Finansial");
        tvTarget.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tvTarget.setTypeface(Typeface.DEFAULT_BOLD);
        tvTarget.setTextColor(Color.WHITE);
        tvTarget.setPadding(0, dpToPx(24), 0, dpToPx(14));
        containerContent.addView(tvTarget);

        long targetTab = pref.getLong("target_tabungan", 10000000);
        long targetEmg = pref.getLong("target_darurat", 15000000);

        containerContent.addView(createTargetCard("Tabungan", tab, targetTab));
        containerContent.addView(createTargetCard("Dana Darurat", emg, targetEmg));
    }

    private View createGridCard(String title, String val, String colorHex, String icon) {
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

        TextView tvIcon = new TextView(this);
        tvIcon.setText(icon);
        tvIcon.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        tvIcon.setGravity(Gravity.CENTER);
        tvIcon.setTextColor(Color.WHITE);
        
        GradientDrawable gdIcon = new GradientDrawable();
        gdIcon.setColor(Color.parseColor(colorHex.equals("#10B981") ? "#064E3B" : colorHex.equals("#F59E0B") ? "#451A03" : "#1E3A8A"));
        gdIcon.setShape(GradientDrawable.OVAL);
        tvIcon.setBackground(gdIcon);
        
        LinearLayout.LayoutParams pIcon = new LinearLayout.LayoutParams(dpToPx(32), dpToPx(32));
        pIcon.setMargins(0, 0, 0, dpToPx(10));
        tvIcon.setLayoutParams(pIcon);

        TextView label = new TextView(this);
        label.setText(title);
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        label.setTextColor(Color.parseColor("#94A3B8"));

        TextView valueTv = new TextView(this);
        valueTv.setText(val);
        valueTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
        valueTv.setTypeface(Typeface.DEFAULT_BOLD);
        valueTv.setTextColor(Color.parseColor(colorHex));
        valueTv.setPadding(0, dpToPx(6), 0, 0);

        card.addView(tvIcon);
        card.addView(label);
        card.addView(valueTv);
        return card;
    }

    private View createActionButton(String label, String symbol, final String type) {
        LinearLayout btn = new LinearLayout(this);
        btn.setOrientation(LinearLayout.VERTICAL);
        btn.setGravity(Gravity.CENTER);
        btn.setPadding(dpToPx(14), dpToPx(16), dpToPx(14), dpToPx(16));

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#121721"));
        gd.setCornerRadius(dpToPx(20));
        btn.setBackground(gd);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dpToPx(90), 1.0f);
        params.setMargins(dpToPx(6), 0, dpToPx(6), 0);
        btn.setLayoutParams(params);

        TextView tvSym = new TextView(this);
        tvSym.setText(symbol);
        tvSym.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        tvSym.setGravity(Gravity.CENTER);
        tvSym.setTextColor(Color.parseColor("#F59E0B"));

        GradientDrawable gdSym = new GradientDrawable();
        gdSym.setColor(Color.parseColor("#2D1F0D"));
        gdSym.setShape(GradientDrawable.OVAL);
        tvSym.setBackground(gdSym);
        
        LinearLayout.LayoutParams pSym = new LinearLayout.LayoutParams(dpToPx(30), dpToPx(30));
        pSym.setMargins(0, 0, 0, dpToPx(8));
        tvSym.setLayoutParams(pSym);

        TextView tvLabel = new TextView(this);
        tvLabel.setText(label);
        tvLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tvLabel.setTypeface(Typeface.DEFAULT_BOLD);
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
        card.setPadding(dpToPx(20), dpToPx(18), dpToPx(20), dpToPx(18));

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#121721"));
        gd.setCornerRadius(dpToPx(20));
        card.setBackground(gd);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, dpToPx(14));
        card.setLayoutParams(params);

        LinearLayout rowHead = new LinearLayout(this);
        rowHead.setOrientation(LinearLayout.HORIZONTAL);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

        int pct = (max > 0) ? (int) ((current * 100) / max) : 0;
        TextView tvPct = new TextView(this);
        tvPct.setText(pct + "%");
        tvPct.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tvPct.setTypeface(Typeface.DEFAULT_BOLD);
        tvPct.setTextColor(Color.parseColor("#F59E0B"));

        rowHead.addView(tvTitle);
        rowHead.addView(tvPct);

        ProgressBar pb = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        pb.setMax((int) Math.max(max, 1));
        pb.setProgress((int) current);
        pb.setPadding(0, dpToPx(12), 0, dpToPx(12));

        TextView tvVal = new TextView(this);
        tvVal.setText(formatRupiah(current) + " dari " + formatRupiah(max));
        tvVal.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvVal.setTextColor(Color.parseColor("#94A3B8"));

        card.addView(rowHead);
        card.addView(pb);
        card.addView(tvVal);
        return card;
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
                tvIcon.setText(item.contains("[+]") ? "↓" : "↑");
                tvIcon.setGravity(Gravity.CENTER);
                tvIcon.setTextColor(Color.WHITE);
                
                GradientDrawable gdIcon = new GradientDrawable();
                gdIcon.setColor(Color.parseColor(item.contains("[+]") ? "#064E3B" : "#451A03"));
                gdIcon.setShape(GradientDrawable.OVAL);
                tvIcon.setBackground(gdIcon);

                LinearLayout.LayoutParams pIcon = new LinearLayout.LayoutParams(dpToPx(38), dpToPx(38));
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
        tvTitle.setPadding(0, dpToPx(4), 0, dpToPx(16));

        containerContent.addView(tvSub);
        containerContent.addView(tvTitle);

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
        row2.addView(createSimpleGridCard("Darurat", formatShort(emg), "#F59E0B"));
        containerContent.addView(row2);
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

    private void renderPengaturan() {
        TextView tvSub = new TextView(this);
        tvSub.setText("PENGATURAN APLIKASI");
        tvSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvSub.setTypeface(Typeface.DEFAULT_BOLD);
        tvSub.setTextColor(Color.parseColor("#F59E0B"));

        TextView tvTitle = new TextView(this);
        tvTitle.setText("Pengaturan");
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
        tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setPadding(0, dpToPx(4), 0, dpToPx(18));

        containerContent.addView(tvSub);
        containerContent.addView(tvTitle);

        containerContent.addView(createCustomBox("Informasi Pembuat", "INCOMESTRUCT v1.0 — Dibuat oleh Helmi Zainul Pahmi"));
        containerContent.addView(createCustomBox("Database", "Penyimpanan lokal aman berbasis perangkat (Offline)"));
    }

    private View createCustomBox(String title, String sub) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dpToPx(18), dpToPx(16), dpToPx(18), dpToPx(16));

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor("#121721"));
        gd.setCornerRadius(dpToPx(18));
        card.setBackground(gd);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, dpToPx(12));
        card.setLayoutParams(lp);

        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
        tvTitle.setTextColor(Color.WHITE);

        TextView tvSub = new TextView(this);
        tvSub.setText(sub);
        tvSub.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tvSub.setTextColor(Color.parseColor("#94A3B8"));
        tvSub.setPadding(0, dpToPx(4), 0, 0);

        card.addView(tvTitle);
        card.addView(tvSub);
        return card;
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
        layout.setPadding(dpToPx(24), dpToPx(16), dpToPx(24), dpToPx(16));

        final Spinner spinnerCategory = new Spinner(this);
        String[] options;
        if (type.equals("in")) {
            options = new String[]{
                "💰 Gaji Bulanan (Masuk Tgl 10)", 
                "🛵 Ojol Grab (Sabtu-Minggu)", 
                "🛵 Ojol Gojek (Sabtu-Minggu)", 
                "🛍 ShopeeFood", 
                "➕ Pemasukan Lainnya"
            };
        } else if (type.equals("out")) {
            options = new String[]{
                "💳 Cicilan Rutin", 
                "👥 Arisan", 
                "👨‍👩‍👦 Uang Orang Tua", 
                "👦 Uang Saku Adek (2 Orang)", 
                "👶 Uang Jajan Keponakan (2 Orang)", 
                "🛒 Kebutuhan Sehari-hari"
            };
        } else {
            options = new String[]{"👛 Pos Tabungan", "🛡 Pos Dana Darurat"};
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, options);
        spinnerCategory.setAdapter(adapter);
        layout.addView(spinnerCategory);

        final EditText inputTanggal = new EditText(this);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        inputTanggal.setText(sdf.format(new Date()));
        inputTanggal.setHint("Tanggal (DD/MM/YYYY)");
        inputTanggal.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        inputTanggal.setPadding(dpToPx(14), dpToPx(12), dpToPx(14), dpToPx(12));
        
        LinearLayout.LayoutParams pInp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        pInp.setMargins(0, dpToPx(12), 0, 0);
        inputTanggal.setLayoutParams(pInp);
        layout.addView(inputTanggal);

        final EditText inputJumlah = new EditText(this);
        inputJumlah.setHint("Nominal (Rp)");
        inputJumlah.setInputType(InputType.TYPE_CLASS_NUMBER);
        inputJumlah.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        inputJumlah.setPadding(dpToPx(14), dpToPx(12), dpToPx(14), dpToPx(12));
        inputJumlah.setLayoutParams(pInp);
        layout.addView(inputJumlah);

        final EditText inputKet = new EditText(this);
        inputKet.setHint("Catatan tambahan (Opsional)");
        inputKet.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        inputKet.setPadding(dpToPx(14), dpToPx(12), dpToPx(14), dpToPx(12));
        inputKet.setLayoutParams(pInp);
        layout.addView(inputKet);

        builder.setView(layout);

        builder.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nom = inputJumlah.getText().toString();
                String kat = spinnerCategory.getSelectedItem().toString();
                String tgl = inputTanggal.getText().toString();
                String ket = inputKet.getText().toString();

                if (!nom.isEmpty()) {
                    long val = Long.parseLong(nom);
                    long current = pref.getLong(type, 0);
                    pref.edit().putLong(type, current + val).apply();

                    String logs = pref.getString("logs", "");
                    String tag = (type.equals("in") || type.equals("tabungan") || type.equals("darurat")) ? "[+] " : "[-] ";
                    String detail = kat + " (" + tgl + ")" + (ket.isEmpty() ? "" : " - " + ket);
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
