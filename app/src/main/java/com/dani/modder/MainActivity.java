package com.dani.modder;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.util.Locale;

/** Offline bot dashboard. Data is stored locally in SQLite; no API key is required. */
public class MainActivity extends Activity {
    BotDatabase db;
    LinearLayout list;
    TextView status, count;
    int purple = Color.rgb(124, 92, 255);

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        db = new BotDatabase(this);
        buildUi();
        refresh();
    }

    TextView text(String value, float size, int color) {
        TextView t = new TextView(this); t.setText(value); t.setTextSize(size); t.setTextColor(color);
        t.setFontFeatureSettings("kern"); return t;
    }
    GradientDrawable bg(int color, float radius) { GradientDrawable g = new GradientDrawable(); g.setColor(color); g.setCornerRadius(radius); return g; }
    TextView pill(String value) { TextView t=text(value,12,Color.WHITE); t.setGravity(Gravity.CENTER); t.setPadding(18,8,18,8); t.setBackground(bg(purple,40)); return t; }
    LinearLayout.LayoutParams lp(int w,int h) { return new LinearLayout.LayoutParams(w,h); }
    int dp(float v) { return (int)(v*getResources().getDisplayMetrics().density+.5f); }

    void buildUi() {
        LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(20),dp(24),dp(20),0); root.setBackground(bg(Color.rgb(13,16,30),0));
        LinearLayout header = new LinearLayout(this); header.setGravity(Gravity.CENTER_VERTICAL); header.setPadding(0,0,0,dp(18));
        LinearLayout titles = new LinearLayout(this); titles.setOrientation(LinearLayout.VERTICAL); titles.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1));
        TextView brand=text("BOT MENU",22,Color.WHITE); brand.setTypeface(Typeface.DEFAULT,Typeface.BOLD); titles.addView(brand);
        TextView sub=text("Offline automation workspace",13,Color.rgb(158,164,187)); titles.addView(sub);
        header.addView(titles); header.addView(pill("●  LOCAL")); root.addView(header);

        LinearLayout hero=new LinearLayout(this); hero.setOrientation(LinearLayout.VERTICAL); hero.setPadding(dp(18),dp(18),dp(18),dp(18)); hero.setBackground(bg(Color.rgb(28,24,59),dp(22))); 
        TextView hi=text("Your bot is ready",23,Color.WHITE); hi.setTypeface(null,Typeface.BOLD); hero.addView(hi);
        TextView hs=text("Create commands, save them on this device,\nand generate a safe webhook template.",14,Color.rgb(194,190,224)); hs.setPadding(0,dp(7),0,dp(14)); hero.addView(hs);
        LinearLayout actions=new LinearLayout(this); actions.setGravity(Gravity.CENTER_VERTICAL);
        Button add=new Button(this); add.setText("＋  New command"); add.setTextColor(Color.WHITE); add.setAllCaps(false); add.setBackground(bg(purple,dp(14))); add.setOnClickListener(v -> addCommand()); actions.addView(add,lp(0,dp(48))); ((LinearLayout.LayoutParams)add.getLayoutParams()).weight=1;
        Space gap=new Space(this); actions.addView(gap,lp(dp(10),1)); Button hook=new Button(this); hook.setText("Generate hook"); hook.setTextColor(Color.WHITE); hook.setAllCaps(false); hook.setBackground(bg(Color.rgb(49,53,78),dp(14))); hook.setOnClickListener(v -> generateHook()); actions.addView(hook,lp(0,dp(48))); ((LinearLayout.LayoutParams)hook.getLayoutParams()).weight=1; hero.addView(actions); root.addView(hero);

        LinearLayout stats=new LinearLayout(this); stats.setPadding(0,dp(17),0,dp(10)); TextView a=text("COMMANDS\n",11,Color.rgb(143,149,175)); count=text("0",23,Color.WHITE); a.setTypeface(null,Typeface.BOLD); stats.addView(a,lp(0,-2)); ((LinearLayout.LayoutParams)a.getLayoutParams()).weight=1; TextView b=text("STORAGE\nLOCAL SQLITE",11,Color.rgb(143,149,175)); stats.addView(b); root.addView(stats);
        status=text("Recent commands",16,Color.WHITE); status.setTypeface(null,Typeface.BOLD); root.addView(status);
        ScrollView scroll=new ScrollView(this); list=new LinearLayout(this); list.setOrientation(LinearLayout.VERTICAL); list.setPadding(0,dp(10),0,dp(30)); scroll.addView(list); root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1)); setContentView(root);
    }
    void addCommand() { final EditText input=new EditText(this); input.setHint("Example: /hello"); input.setSingleLine(); new android.app.AlertDialog.Builder(this).setTitle("New command").setMessage("Save a local bot command").setView(input).setNegativeButton("Cancel",null).setPositiveButton("Save",(d,w)->{String s=input.getText().toString().trim();if(!s.isEmpty()){db.add(s);refresh();}}).show(); }
    void generateHook() { String hook="{\n  \"event\": \"message.created\",\n  \"command\": \"/hello\",\n  \"reply\": \"Bot Menu is online!\"\n}"; new android.app.AlertDialog.Builder(this).setTitle("Hook template generated").setMessage(hook+"\n\nThis is an offline template. Connect your own server later if you need real webhooks.").setPositiveButton("Copy",(d,w)->{((android.content.ClipboardManager)getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(android.content.ClipData.newPlainText("Bot hook",hook));Toast.makeText(this,"Copied to clipboard",Toast.LENGTH_SHORT).show();}).setNegativeButton("Close",null).show(); }
    void refresh() { list.removeAllViews(); java.util.ArrayList<String> rows=db.all(); count.setText(String.valueOf(rows.size())); if(rows.isEmpty()){ TextView empty=text("No commands yet. Tap New command to start.",14,Color.rgb(143,149,175)); empty.setPadding(dp(4),dp(20),0,0); list.addView(empty); return; } for(String s:rows){LinearLayout row=new LinearLayout(this); row.setGravity(Gravity.CENTER_VERTICAL); row.setPadding(dp(15),dp(13),dp(10),dp(13)); row.setBackground(bg(Color.rgb(25,29,47),dp(16))); TextView name=text(s,15,Color.WHITE); name.setTypeface(null,Typeface.BOLD); row.addView(name,lp(0,dp(52))); ((LinearLayout.LayoutParams)name.getLayoutParams()).weight=1; TextView ok=text("ACTIVE",11,Color.rgb(86,220,167)); ok.setPadding(dp(10),0,dp(10),0); row.addView(ok); Button del=new Button(this); del.setText("×"); del.setTextColor(Color.rgb(220,160,180)); del.setBackgroundColor(Color.TRANSPARENT); del.setOnClickListener(v->{db.remove(s);refresh();}); row.addView(del,lp(dp(45),dp(52))); LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,dp(78)); rp.bottomMargin=dp(8); list.addView(row,rp); } }
}
