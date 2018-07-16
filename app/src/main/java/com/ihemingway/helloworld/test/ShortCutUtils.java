package com.ihemingway.helloworld.test;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;

/**
 * Description:android  快捷键创建工具
 * Data：2018/7/16-13:46
 * <p>
 * Author: hemingway
 */
public class ShortCutUtils {
    public static final String ACTION_ADD_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";

    public static void addShortCut(Context context,String scName,int scIconId,Class launchActivity){
        Intent launcherIntent = new Intent();
        launcherIntent.setClass(context, launchActivity);
        launcherIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        addShortCut(context,scName,scIconId,launcherIntent);
    }
    public static void addShortCut(Context context,String scName,int scIconId,Intent launchIntent){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            addShortCutAboveO(context,scName,scIconId,launchIntent);
        }else{
            addShortCutBelowO(context,scName,scIconId,launchIntent);
        }
    }

    private static void addShortCutBelowO(Context context,String scName,int scIconId,Intent launchIntent){
        Intent addShortcutIntent = new Intent(ACTION_ADD_SHORTCUT);
        // 不允许重复创建，不是根据快捷方式的名字判断重复的
        //小米手机 version < 8.0 设置为false 不能创建出icon
        addShortcutIntent.putExtra("duplicate", true);
        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, scName);
        //图标
        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, scIconId));
        // 设置关联程序
        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launchIntent);
        // 发送广播
        context.sendBroadcast(addShortcutIntent);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private static void addShortCutAboveO(Context context, String scName, int scIconId, Intent launchIntent){
        ShortcutManager shortcutManager = (ShortcutManager) context.getSystemService(Context.SHORTCUT_SERVICE);
        if (shortcutManager!=null&&shortcutManager.isRequestPinShortcutSupported()) {
            Intent shortcutInfoIntent = new Intent(context, ShortCutActivity.class);
            shortcutInfoIntent.setAction(Intent.ACTION_VIEW); //action必须设置，不然报错
            ShortcutInfo info = new ShortcutInfo.Builder(context, "The only id")
                    .setIcon(Icon.createWithResource(context, scIconId))
                    .setShortLabel(scName)
                    .setIntent(shortcutInfoIntent)
                    .build();
            //当添加快捷方式的确认弹框弹出来时，将被回调
            PendingIntent shortcutCallbackIntent = PendingIntent.getBroadcast(context, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            shortcutManager.requestPinShortcut(info, shortcutCallbackIntent.getIntentSender());
        }
    }
}
