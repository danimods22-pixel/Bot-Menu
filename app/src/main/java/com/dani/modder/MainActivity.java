package com.dani.modder;

import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.util.ArrayList;

/** Offline-first bot workspace: commands, chat history and hook templates stay on-device. */
public class MainActivity extends Activity {
    private BotDatabase db;
    private LinearLayout commandList, chatList;
    private TextView commandCount, messageCount, emptyChat;
    private EditText chatInput;
    private final int bg = Color.rgb(12, 15, 28);
    private final int card = Color.rgb(24, 29, 47);
    private final int muted = Color.rgb(157, 165, 190);
    private final int purple = Color.rgb(124, 92, 255);

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        db = new BotDatabase(this);
        buildUi();
        refreshAll();
    }

    private int dp(int value) { return (int) (value * getResources().getDisplayMetrics().density + .5f); }
    private TextView label(String value, float size, int color) {
        TextView view = new TextView(this);
        view.setText(value); view.setTextSize(size); view.setTextColor(color);
        return view;
    }
    private GradientDrawable rounded(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable(); drawable.setColor(color); drawable.setCornerRadius(dp(radius)); return drawable;
    }
    private Button button(String value, int color) {
        Button button = new Button(this); button.setText(value); button.setTextColor(Color.WHITE); button.setTextSize(13); button.setAllCaps(false);
        button.setBackground(rounded(color, 14)); return button;
    }
    private LinearLayout.LayoutParams params(int width, int height) { return new LinearLayout.LayoutParams(width, height); }

    private void buildUi() {
        LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(bg);
        root.setPadding(dp(18), dp(18), dp(18), 0);

        LinearLayout header = new LinearLayout(this); header.setGravity(Gravity.CENTER_VERTICAL); header.setPadding(0, 0, 0, dp(14));
        LinearLayout titleBox = new LinearLayout(this); titleBox.setOrientation(LinearLayout.VERTICAL);
        TextView title = label("BOT MENU", 23, Color.WHITE); title.setTypeface(null, Typeface.BOLD); titleBox.addView(title);
        titleBox.addView(label("Offline automation workspace", 12, muted));
        header.addView(titleBox, new LinearLayout.LayoutParams(0, -2, 1));
        TextView local = label("●  LOCAL", 11, Color.WHITE); local.setGravity(Gravity.CENTER); local.setPadding(dp(13), dp(8), dp(13), dp(8)); local.setBackground(rounded(Color.rgb(45, 174, 128), 30)); header.addView(local);
        root.addView(header);

        LinearLayout hero = new LinearLayout(this); hero.setOrientation(LinearLayout.VERTICAL); hero.setPadding(dp(18), dp(17), dp(18), dp(17)); hero.setBackground(rounded(Color.rgb(31, 26, 65), 22));
        TextView heroTitle = label("Your bot is ready", 22, Color.WHITE); heroTitle.setTypeface(null, Typeface.BOLD); hero.addView(heroTitle);
        TextView heroText = label("Chat with your local bot, create commands, and\ngenerate hook payloads without an API key.", 13, Color.rgb(199, 195, 225)); heroText.setPadding(0, dp(7), 0, dp(14)); hero.addView(heroText);
        LinearLayout heroActions = new LinearLayout(this); heroActions.setGravity(Gravity.CENTER_VERTICAL);
        Button newCommand = button("＋  New command", purple); newCommand.setOnClickListener(v -> addCommand()); heroActions.addView(newCommand, new LinearLayout.LayoutParams(0, dp(46), 1));
        Space space = new Space(this); heroActions.addView(space, params(dp(10), 1));
        Button hook = button("Generate hook", Color.rgb(55, 59, 88)); hook.setOnClickListener(v -> generateHook()); heroActions.addView(hook, new LinearLayout.LayoutParams(0, dp(46), 1));
        hero.addView(heroActions); root.addView(hero);

        LinearLayout stats = new LinearLayout(this); stats.setPadding(0, dp(16), 0, dp(10));
        LinearLayout commandStat = statBox("COMMANDS"); commandCount = label("0", 21, Color.WHITE); commandStat.addView(commandCount); stats.addView(commandStat, new LinearLayout.LayoutParams(0, -2, 1));
        LinearLayout messageStat = statBox("CHAT MESSAGES"); messageCount = label("0", 21, Color.WHITE); messageStat.addView(messageCount); stats.addView(messageStat, new LinearLayout.LayoutParams(0, -2, 1));
        LinearLayout storageStat = statBox("STORAGE"); storageStat.addView(label("SQLITE LOCAL", 11, muted)); stats.addView(storageStat, new LinearLayout.LayoutParams(0, -2, 1)); root.addView(stats);

        TextView chatTitle = label("Bot chat", 18, Color.WHITE); chatTitle.setTypeface(null, Typeface.BOLD); root.addView(chatTitle);
        TextView chatSub = label("Test automatic replies before connecting a server", 12, muted); chatSub.setPadding(0, dp(3), 0, dp(8)); root.addView(chatSub);
        ScrollView chatScroll = new ScrollView(this); chatScroll.setFillViewport(true); chatList = new LinearLayout(this); chatList.setOrientation(LinearLayout.VERTICAL); chatList.setPadding(0, dp(4), 0, dp(8)); chatScroll.addView(chatList); root.addView(chatScroll, new LinearLayout.LayoutParams(-1, 0, 1));
        emptyChat = label("Say hello to your bot to start a local conversation.", 13, muted); emptyChat.setGravity(Gravity.CENTER); chatList.addView(emptyChat, new LinearLayout.LayoutParams(-1, dp(85)));

        LinearLayout composer = new LinearLayout(this); composer.setGravity(Gravity.CENTER_VERTICAL); composer.setPadding(0, dp(8), 0, dp(14));
        chatInput = new EditText(this); chatInput.setSingleLine(true); chatInput.setHint("Type a message or /command"); chatInput.setHintTextColor(Color.rgb(110, 118, 145)); chatInput.setTextColor(Color.WHITE); chatInput.setPadding(dp(15), 0, dp(10), 0); chatInput.setBackground(rounded(card, 16)); composer.addView(chatInput, new LinearLayout.LayoutParams(0, dp(50), 1));
        Button send = button("Send", purple); send.setOnClickListener(v -> sendMessage()); composer.addView(send, params(dp(78), dp(50))); root.addView(composer);
        setContentView(root);
    }

    private LinearLayout statBox(String heading) { LinearLayout box = new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL); TextView h = label(heading, 10, muted); h.setTypeface(null, Typeface.BOLD); box.addView(h); return box; }

    private void addCommand() {
        final EditText input = new EditText(this); input.setHint("Example: /hello"); input.setSingleLine(true); input.setTextColor(Color.DKGRAY);
        new android.app.AlertDialog.Builder(this).setTitle("New local command").setMessage("Commands are saved only on this device.").setView(input).setNegativeButton("Cancel", null).setPositiveButton("Save", (dialog, which) -> { String value = input.getText().toString().trim(); if (!value.isEmpty()) { db.addCommand(value); refreshAll(); } }).show();
    }

    private void sendMessage() {
        String text = chatInput.getText().toString().trim(); if (text.isEmpty()) return;
        db.addMessage(text, "user"); db.addMessage(replyFor(text), "bot"); chatInput.setText(""); refreshChat();
    }
    private String replyFor(String message) {
        String lower = message.toLowerCase();
        if (lower.equals("/start") || lower.contains("halo") || lower.contains("hello")) return "Halo! Bot Menu aktif dan siap membantu. Coba /help untuk melihat command.";
        if (lower.equals("/help") || lower.contains("bantuan")) return "Command lokal: /start, /help, /info. Kamu juga bisa membuat command baru dari dashboard.";
        if (lower.equals("/info")) return "Bot Menu v2.1 berjalan offline-first. Data chat dan command tersimpan di SQLite perangkat.";
        return "Pesan diterima secara lokal. Buat aturan reply server sendiri nanti melalui template hook yang tersedia.";
    }

    private void generateHook() {
        String hook = "{\n  \"event\": \"message.created\",\n  \"command\": \"/hello\",\n  \"reply\": \"Bot Menu is online!\"\n}";
        new android.app.AlertDialog.Builder(this).setTitle("Hook template generated").setMessage(hook + "\n\nTemplate ini aman dan offline. Sambungkan ke backend milik Anda jika membutuhkan webhook sungguhan.").setNegativeButton("Close", null).setPositiveButton("Copy", (dialog, which) -> { android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE); clipboard.setPrimaryClip(android.content.ClipData.newPlainText("Bot Menu hook", hook)); Toast.makeText(this, "Hook disalin", Toast.LENGTH_SHORT).show(); }).show();
    }

    private void refreshAll() { refreshChat(); commandCount.setText(String.valueOf(db.commandCount())); }
    private void refreshChat() {
        if (chatList == null) return; chatList.removeAllViews(); ArrayList<BotDatabase.Message> messages = db.messages(); messageCount.setText(String.valueOf(messages.size()));
        if (messages.isEmpty()) { chatList.addView(emptyChat, new LinearLayout.LayoutParams(-1, dp(85))); return; }
        for (BotDatabase.Message message : messages) addBubble(message.text, message.sender.equals("user"));
    }
    private void addBubble(String value, boolean user) {
        LinearLayout line = new LinearLayout(this); line.setGravity(user ? Gravity.RIGHT : Gravity.LEFT); line.setPadding(dp(3), dp(4), dp(3), dp(4));
        TextView bubble = label(value, 14, Color.WHITE); bubble.setPadding(dp(14), dp(10), dp(14), dp(10)); bubble.setMaxWidth(dp(310)); bubble.setBackground(rounded(user ? purple : card, 18)); line.addView(bubble); chatList.addView(line, new LinearLayout.LayoutParams(-1, -2));
    }

    @Override protected void onDestroy() { if (db != null) db.close(); super.onDestroy(); }
}
