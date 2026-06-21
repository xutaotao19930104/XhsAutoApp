package com.xhsauto.app;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "XhsAutoApp";
    private static final String CONFIG_DIR = "/sdcard/xhsauto/";
    private static final String CONFIG_FILE = CONFIG_DIR + "config.json";
    
    private TextView tvServiceStatus;
    private TextView tvPostConfig;
    private TextView tvCommentConfig;
    private Button btnOpenAccessibility;
    private Button btnAutoPost;
    private Button btnAutoComment;
    private TextView tvLog;
    
    private XhsAccessibilityService accessibilityService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initViews();
        setupListeners();
        createDefaultConfig();
        updateServiceStatus();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        updateServiceStatus();
    }
    
    private void initViews() {
        tvServiceStatus = findViewById(R.id.tvServiceStatus);
        tvPostConfig = findViewById(R.id.tvPostConfig);
        tvCommentConfig = findViewById(R.id.tvCommentConfig);
        btnOpenAccessibility = findViewById(R.id.btnOpenAccessibility);
        btnAutoPost = findViewById(R.id.btnAutoPost);
        btnAutoComment = findViewById(R.id.btnAutoComment);
        tvLog = findViewById(R.id.tvLog);
    }
    
    private void setupListeners() {
        btnOpenAccessibility.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        });
        
        btnAutoPost.setOnClickListener(v -> {
            if (isAccessibilityServiceEnabled()) {
                startAutoPost();
            } else {
                Toast.makeText(this, "请先开启无障碍服务", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnAutoComment.setOnClickListener(v -> {
            if (isAccessibilityServiceEnabled()) {
                startAutoComment();
            } else {
                Toast.makeText(this, "请先开启无障碍服务", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void updateServiceStatus() {
        boolean isEnabled = isAccessibilityServiceEnabled();
        tvServiceStatus.setText("无障碍服务：" + (isEnabled ? "已开启 ✓" : "未开启 ✗"));
        tvServiceStatus.setTextColor(isEnabled ? 0xFF4CAF50 : 0xFFFF5722);
        
        btnAutoPost.setEnabled(isEnabled);
        btnAutoComment.setEnabled(isEnabled);
        
        if (isEnabled) {
            accessibilityService = XhsAccessibilityService.getInstance();
        }
    }
    
    private boolean isAccessibilityServiceEnabled() {
        AccessibilityManager am = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        
        for (AccessibilityServiceInfo serviceInfo : enabledServices) {
            if (serviceInfo.getId().contains(getPackageName())) {
                return true;
            }
        }
        return false;
    }
    
    private void createDefaultConfig() {
        File configDir = new File(CONFIG_DIR);
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        
        File configFile = new File(CONFIG_FILE);
        if (!configFile.exists()) {
            try {
                JsonObject config = new JsonObject();
                
                JsonObject posts = new JsonObject();
                posts.addProperty("title", "今天分享一个小技巧");
                posts.addProperty("content", "这是一个实用的小技巧，希望对大家有帮助！\n\n#小红书 #生活技巧 #分享");
                
                com.google.gson.JsonArray images = new com.google.gson.JsonArray();
                images.add("/sdcard/xhsauto/images/photo1.jpg");
                posts.add("images", images);
                
                com.google.gson.JsonArray postsArray = new com.google.gson.JsonArray();
                postsArray.add(posts);
                config.add("posts", postsArray);
                
                JsonObject comments = new JsonObject();
                com.google.gson.JsonArray keywords = new com.google.gson.JsonArray();
                keywords.add("生活技巧");
                keywords.add("实用分享");
                comments.add("keywords", keywords);
                comments.addProperty("default_comment", "写得真好，学到了！收藏了～");
                config.add("comments", comments);
                
                FileWriter writer = new FileWriter(configFile);
                writer.write(new Gson().toJson(config));
                writer.close();
                
                appendLog("默认配置文件已创建");
            } catch (IOException e) {
                Log.e(TAG, "创建配置文件失败", e);
                appendLog("创建配置文件失败：" + e.getMessage());
            }
        }
        
        tvPostConfig.setText("配置文件：" + CONFIG_FILE);
        tvCommentConfig.setText("配置文件：" + CONFIG_FILE);
    }
    
    private void startAutoPost() {
        appendLog("开始自动发帖...");
        
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.xingin.xhs");
        if (launchIntent != null) {
            startActivity(launchIntent);
            
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    runOnUiThread(() -> {
                        if (accessibilityService != null) {
                            accessibilityService.startAutoPost();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            Toast.makeText(this, "未安装小红书App", Toast.LENGTH_SHORT).show();
            appendLog("错误：未安装小红书App");
        }
    }
    
    private void startAutoComment() {
        appendLog("开始自动评论...");
        
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.xingin.xhs");
        if (launchIntent != null) {
            startActivity(launchIntent);
            
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    runOnUiThread(() -> {
                        if (accessibilityService != null) {
                            accessibilityService.startAutoComment();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            Toast.makeText(this, "未安装小红书App", Toast.LENGTH_SHORT).show();
            appendLog("错误：未安装小红书App");
        }
    }
    
    public void appendLog(String message) {
        runOnUiThread(() -> {
            String current = tvLog.getText().toString();
            tvLog.setText(current + "\n" + message);
        });
    }
}
