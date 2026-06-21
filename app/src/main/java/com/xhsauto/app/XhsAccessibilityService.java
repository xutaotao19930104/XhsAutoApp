package com.xhsauto.app;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class XhsAccessibilityService extends AccessibilityService {
    private static final String TAG = "XhsAutoService";
    private static final String CONFIG_FILE = "/sdcard/xhsauto/config.json";
    private static final int CLICK_DELAY = 500;
    private static final int INPUT_DELAY = 300;
    private static final int WAIT_DELAY = 1000;
    
    private static XhsAccessibilityService instance;
    private Handler mainHandler;
    private JsonObject config;
    private boolean isRunning = false;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "服务被中断");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        instance = this;
        mainHandler = new Handler(Looper.getMainLooper());
        Log.d(TAG, "无障碍服务已连接");
    }
    
    public static XhsAccessibilityService getInstance() {
        return instance;
    }
    
    private boolean loadConfig() {
        try {
            File file = new File(CONFIG_FILE);
            if (!file.exists()) {
                Log.e(TAG, "配置文件不存在");
                return false;
            }
            
            FileReader reader = new FileReader(file);
            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[1024];
            int len;
            while ((len = reader.read(buffer)) != -1) {
                sb.append(buffer, 0, len);
            }
            reader.close();
            
            config = new Gson().fromJson(sb.toString(), JsonObject.class);
            return config != null;
        } catch (IOException e) {
            Log.e(TAG, "读取配置文件失败", e);
            return false;
        }
    }
    
    public void startAutoPost() {
        if (isRunning) {
            Log.d(TAG, "任务正在执行中");
            return;
        }
        
        if (!loadConfig()) {
            showToast("配置文件读取失败");
            return;
        }
        
        isRunning = true;
        new Thread(() -> {
            try {
                performAutoPost();
            } catch (Exception e) {
                Log.e(TAG, "自动发帖失败", e);
                showToast("自动发帖失败：" + e.getMessage());
            } finally {
                isRunning = false;
            }
        }).start();
    }
    
    public void startAutoComment() {
        if (isRunning) {
            Log.d(TAG, "任务正在执行中");
            return;
        }
        
        if (!loadConfig()) {
            showToast("配置文件读取失败");
            return;
        }
        
        isRunning = true;
        new Thread(() -> {
            try {
                performAutoComment();
            } catch (Exception e) {
                Log.e(TAG, "自动评论失败", e);
                showToast("自动评论失败：" + e.getMessage());
            } finally {
                isRunning = false;
            }
        }).start();
    }
    
    private void performAutoPost() throws InterruptedException {
        showToast("开始自动发帖...");
        Thread.sleep(3000);
        
        AccessibilityNodeInfo addBtn = findNodeByText("发布");
        if (addBtn == null) addBtn = findNodeByDescription("发布");
        if (addBtn != null) {
            clickNode(addBtn);
            Thread.sleep(CLICK_DELAY);
        } else {
            performClick(540, 2200);
            Thread.sleep(CLICK_DELAY);
        }
        
        Thread.sleep(WAIT_DELAY);
        AccessibilityNodeInfo photoOption = findNodeByText("图文");
        if (photoOption != null) {
            clickNode(photoOption);
            Thread.sleep(CLICK_DELAY);
        }
        
        Thread.sleep(WAIT_DELAY);
        selectImages();
        Thread.sleep(WAIT_DELAY);
        
        AccessibilityNodeInfo confirmBtn = findNodeByText("下一步");
        if (confirmBtn == null) confirmBtn = findNodeByText("确定");
        if (confirmBtn != null) {
            clickNode(confirmBtn);
            Thread.sleep(CLICK_DELAY);
        }
        
        Thread.sleep(WAIT_DELAY);
        JsonArray posts = config.getAsJsonArray("posts");
        if (posts != null && posts.size() > 0) {
            JsonObject post = posts.get(0).getAsJsonObject();
            
            AccessibilityNodeInfo titleInput = findNodeByClassName("android.widget.EditText");
            if (titleInput != null) {
                pasteText(titleInput, post.get("title").getAsString());
                Thread.sleep(INPUT_DELAY);
            }
            
            AccessibilityNodeInfo contentInput = findNodeByClassName("android.widget.EditText");
            if (contentInput != null) {
                pasteText(contentInput, post.get("content").getAsString());
                Thread.sleep(INPUT_DELAY);
            }
        }
        
        AccessibilityNodeInfo publishBtn = findNodeByText("发布笔记");
        if (publishBtn == null) publishBtn = findNodeByText("发布");
        if (publishBtn != null) {
            clickNode(publishBtn);
            Thread.sleep(2000);
            showToast("发帖完成！");
        } else {
            showToast("未找到发布按钮");
        }
    }
    
    private void performAutoComment() throws InterruptedException {
        showToast("开始自动评论...");
        Thread.sleep(3000);
        
        AccessibilityNodeInfo searchBtn = findNodeByDescription("搜索");
        if (searchBtn == null) searchBtn = findNodeByText("搜索");
        if (searchBtn != null) {
            clickNode(searchBtn);
            Thread.sleep(CLICK_DELAY);
        }
        
        Thread.sleep(WAIT_DELAY);
        JsonArray keywords = config.getAsJsonObject("comments").getAsJsonArray("keywords");
        String keyword = keywords.get(0).getAsString();
        
        AccessibilityNodeInfo searchInput = findNodeByClassName("android.widget.EditText");
        if (searchInput != null) {
            pasteText(searchInput, keyword);
            Thread.sleep(INPUT_DELAY);
            
            AccessibilityNodeInfo searchConfirm = findNodeByText("搜索");
            if (searchConfirm != null) {
                clickNode(searchConfirm);
                Thread.sleep(2000);
            }
        }
        
        Thread.sleep(WAIT_DELAY);
        AccessibilityNodeInfo firstPost = findNodeByClassName("android.widget.FrameLayout");
        if (firstPost != null) {
            clickNode(firstPost);
            Thread.sleep(CLICK_DELAY);
        }
        
        Thread.sleep(WAIT_DELAY);
        AccessibilityNodeInfo commentInput = findNodeByText("说点什么...");
        if (commentInput == null) commentInput = findNodeByDescription("评论");
        if (commentInput != null) {
            clickNode(commentInput);
            Thread.sleep(CLICK_DELAY);
        }
        
        String comment = config.getAsJsonObject("comments").get("default_comment").getAsString();
        AccessibilityNodeInfo inputField = findNodeByClassName("android.widget.EditText");
        if (inputField != null) {
            pasteText(inputField, comment);
            Thread.sleep(INPUT_DELAY);
        }
        
        AccessibilityNodeInfo sendBtn = findNodeByText("发送");
        if (sendBtn != null) {
            clickNode(sendBtn);
            Thread.sleep(2000);
            showToast("评论完成！");
        } else {
            showToast("未找到发送按钮");
        }
    }
    
    private void selectImages() {
        AccessibilityNodeInfo imageContainer = findNodeByClassName("android.widget.GridView");
        if (imageContainer == null) {
            imageContainer = findNodeByClassName("androidx.recyclerview.widget.RecyclerView");
        }
        if (imageContainer != null && imageContainer.getChildCount() > 0) {
            AccessibilityNodeInfo firstImage = imageContainer.getChild(0);
            if (firstImage != null) {
                clickNode(firstImage);
            }
        }
    }
    
    private AccessibilityNodeInfo findNodeByText(String text) {
        List<AccessibilityNodeInfo> nodes = getRootInActiveWindow().findAccessibilityNodeInfosByText(text);
        if (nodes != null && !nodes.isEmpty()) {
            return nodes.get(0);
        }
        return null;
    }
    
    private AccessibilityNodeInfo findNodeByDescription(String desc) {
        List<AccessibilityNodeInfo> nodes = getRootInActiveWindow().findAccessibilityNodeInfosByViewId(desc);
        if (nodes != null && !nodes.isEmpty()) {
            return nodes.get(0);
        }
        return null;
    }
    
    private AccessibilityNodeInfo findNodeByClassName(String className) {
        return findNodeByClassRecursive(getRootInActiveWindow(), className);
    }
    
    private AccessibilityNodeInfo findNodeByClassRecursive(AccessibilityNodeInfo node, String className) {
        if (node == null) return null;
        
        if (className.equals(node.getClassName())) {
            return node;
        }
        
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo result = findNodeByClassRecursive(node.getChild(i), className);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
    
    private void clickNode(AccessibilityNodeInfo node) {
        if (node == null) return;
        
        Rect rect = new Rect();
        node.getBoundsInScreen(rect);
        performClick(rect.centerX(), rect.centerY());
    }
    
    private void performClick(float x, float y) {
        Path path = new Path();
        path.moveTo(x, y);
        
        GestureDescription.Builder builder = new GestureDescription.Builder();
        builder.addStroke(new GestureDescription.StrokeDescription(path, 0, 100));
        
        dispatchGesture(builder.build(), null, null);
    }
    
    private void pasteText(AccessibilityNodeInfo node, String text) {
        if (node == null) return;
        
        Bundle arguments = new Bundle();
        arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
        node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
    }
    
    private void showToast(String message) {
        mainHandler.post(() -> {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        });
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
    }
}
