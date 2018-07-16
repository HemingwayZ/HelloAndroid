package com.ihemingway.helloworld.test;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.database.Cursor;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Description:android  快捷键创建工具
 * Data：2018/7/16-13:46
 * <p>
 * Author: hemingway
 */
public class ShortCutUtils {
    public static final String ACTION_ADD_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
    //移除快捷方式
    public static final String ACTION_REMOVE_SHORTCUT = "com.android.launcher.action.UNINSTALL_SHORTCUT";
    private static final String TAG = ShortCutUtils.class.getSimpleName();

    /**
     * 启动的activity需要加入行为
     *<intent-filter>
     <action android:name="android.intent.action.CREATE_SHORTCUT"/>
     <action android:name="com.android.launcher.action.INSTALL_SHORTCUT" />
     </intent-filter>
     * @param context
     * @param scName
     * @param scIconId
     * @param launchActivity
     */
    public static void addShortCut(Context context,String scName,int scIconId,Class launchActivity){

//        if(hasShortcut(context,context.getPackageName())){
//            removeShortcut(context,scName,launchActivity);
//            Log.d(TAG,"hasShortcut");
//        }else{
//            Log.d(TAG,"addShortCut");
//        }
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.setClass(context, launchActivity);
        launcherIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER); //不能加入该句 oppo手机无法使用 因为再fest文件已经对MainActivity做了声明
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
        //经测试不是根据快捷方式的名字判断重复的
        // 应该是根据快链的Intent来判断是否重复的,即Intent.EXTRA_SHORTCUT_INTENT字段的value
        // 但是名称不同时，虽然有的手机系统会显示Toast提示重复，仍然会建立快链
        // 屏幕上没有空间时会提示
        // 注意：重复创建的行为MIUI和三星手机上不太一样，小米上似乎不能重复创建快捷方式
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
    private static void removeShortcut(Context context,String name,Class launchActivity) {
        // remove shortcut的方法在小米系统上不管用，在三星上可以移除
        Intent intent = new Intent(ACTION_REMOVE_SHORTCUT);
        // 名字
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
        // 设置关联程序
        Intent launcherIntent = new Intent(context,
                launchActivity).setAction(Intent.ACTION_MAIN);
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, launcherIntent);
        // 发送广播
        context.sendBroadcast(intent);
    }

    public static String getAuthorityFromPermission(Context context) {
        // 先得到默认的Launcher
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        PackageManager mPackageManager = context.getPackageManager();
        ResolveInfo resolveInfo = mPackageManager.resolveActivity(intent, 0);
        if (resolveInfo == null) {
            return null;
        }
//        List<ProviderInfo> info = mPackageManager.queryContentProviders(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.applicationInfo.uid, PackageManager.GET_PROVIDERS);
        List<ProviderInfo> info = mPackageManager.queryContentProviders(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.applicationInfo.uid, PackageManager.GET_PROVIDERS);
        if (info != null) {
            for (int j = 0; j < info.size(); j++) {
                ProviderInfo provider = info.get(j);
                if (provider.readPermission == null) {
                    continue;
                }
                Log.d(TAG,provider.readPermission);
                Log.d(TAG,provider.authority);
                if (Pattern.matches(".*launcher.*READ_SETTINGS", provider.readPermission)) {
                    return provider.authority;
                }
            }
        }
        return null;
    }
    private static boolean hasShortcut(Context context, String appName) {
        long start = System.currentTimeMillis();
        String authority = getAuthorityFromPermission(context);
        if (authority == null) {
            return false;
        }
        long end = System.currentTimeMillis() - start;
        String url = "content://" + authority + "/favorites?notify=true";
        try {
            Uri CONTENT_URI = Uri.parse(url);
            Cursor c = context.getContentResolver().query(CONTENT_URI, null, " title= ? ", new String[]{appName}, null);
            if (c != null && c.moveToNext()) {
                c.close();
                return true;
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }

}
