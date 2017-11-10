package com.ihemingway.helloworld

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import kotlinx.android.synthetic.main.activity_screen_lock.*
import android.content.Intent
import android.util.Log
import android.widget.Toast


class ScreenLockActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvLock -> {
                Toast.makeText(this, "Lock Screen", Toast.LENGTH_SHORT).show()
                lockScreen()
            }
        }
    }

    /**
     *
     */
    fun lockScreen() {
        Thread {
            SystemClock.sleep(1000)
            var manager: DevicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            var companet = ComponentName(applicationContext, AdminReceiver::class.java)
            Log.d(ScreenLockActivity::class.java.simpleName,"isAdminActive"+companet.packageName)
            if (manager.isAdminActive(companet)) {
                Log.d(ScreenLockActivity::class.java.simpleName,"isAdminActive")
                manager.lockNow()
                //杀死当前应用
                android.os.Process.killProcess(android.os.Process.myPid())
            } else {
                activeManager()
            }
        }.start()
    }

    fun activeManager() {
        //使用隐式意图调用系统方法来激活指定的设备管理器
        Log.d(ScreenLockActivity::class.java.simpleName,"activeManager")
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "一键锁屏")
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_lock)
        tvLock.setOnClickListener { onClick(tvLock) }
    }
}
