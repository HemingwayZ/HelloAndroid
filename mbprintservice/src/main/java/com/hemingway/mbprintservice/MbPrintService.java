package com.hemingway.mbprintservice;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.print.PrintJobInfo;
import android.print.PrinterId;
import android.printservice.PrintDocument;
import android.printservice.PrintJob;
import android.printservice.PrintService;
import android.printservice.PrinterDiscoverySession;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MbPrintService extends PrintService {
    private static final String TAG = "MbPrintService";
    private BluetoothAdapter mBluetoothAdapter;
    private PrinterId printId;

    public MbPrintService() {
    }

    @Nullable
    @Override
    protected PrinterDiscoverySession onCreatePrinterDiscoverySession() {
        Log.d(TAG, "onCreatePrinterDiscoverySession()");
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if(bluetoothManager!=null){
            mBluetoothAdapter = bluetoothManager.getAdapter();
            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(enableBtIntent);
            }
        }
        return new MbPrinterDiscoverySession(this,mBluetoothAdapter);
    }

    @Override
    protected void onRequestCancelPrintJob(PrintJob printJob) {
        Log.d(TAG, "onRequestCancelPrintJob()");
    }

    @Override
    protected void onPrintJobQueued(final PrintJob printJob) {
        Log.d(TAG, "onPrintJobQueued()");
        PrintJobInfo printjobinfo = printJob.getInfo();
        PrintDocument printdocument = printJob.getDocument();
//        if (printJob.isQueued()) {
//            return;
//        }
        printJob.start();
        if(printId!=null){
            BluetoothDevice remoteDevice = mBluetoothAdapter.getRemoteDevice(printId.getLocalId());
            if(remoteDevice!=null){
                remoteDevice.connectGatt(this, true, new BluetoothGattCallback() {
                    @Override
                    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                        Log.d(TAG,"status _ "+status);
                        super.onConnectionStateChange(gatt, status, newState);
                        printJob.complete();
                    }

                });
            }
        }

//        mBluetoothAdapter.getRemoteDevice(printJob.);
//        String filename = "docu.pdf";
//        File outfile = new File(this.getFilesDir(), filename);
//        outfile.delete();
//        FileInputStream file = new ParcelFileDescriptor.AutoCloseInputStream(printdocument.getData());
//        //创建一个长度为1024的内存空间
//        byte[] bbuf = new byte[1024];
//        //用于保存实际读取的字节数
//        int hasRead = 0;
//        //使用循环来重复读取数据
//        try {
//            FileOutputStream outStream = new FileOutputStream(outfile);
//            while ((hasRead = file.read(bbuf)) > 0) {
//                //将字节数组转换为字符串输出
//                //System.out.print(new String(bbuf, 0, hasRead));
//                outStream.write(bbuf);
//            }
//            outStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            //关闭文件输出流，放在finally块里更安全
//            try {
//                file.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }


    }

    public void setPrintId(PrinterId printId) {
        this.printId = printId;
    }
}
