package com.hemingway.mbprintservice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.print.PrintJobInfo;
import android.print.PrinterId;
import android.printservice.PrintDocument;
import android.printservice.PrintJob;
import android.printservice.PrintService;
import android.printservice.PrinterDiscoverySession;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;

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
        BleManager.getInstance().init(getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setSplitWriteNum(20)
                .setConnectOverTime(10000)
                .setOperateTimeout(5000);

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
            BleManager.getInstance().connect(printId.getLocalId(), new BleGattCallback() {
                @Override
                public void onStartConnect() {

                }

                @Override
                public void onConnectFail(BleDevice bleDevice, BleException exception) {

                }

                @Override
                public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                    connectSuccess(bleDevice, gatt, status);

                    Toast.makeText(MbPrintService.this,"连接成功",Toast.LENGTH_LONG).show();
                    printJob.complete();
                }

                @Override
                public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {

                }
            });
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
    // 蓝牙串口服务UUID
    public static final String uuid = "00001101-0000-1000-8000-00805F9B34FB";
    private void connectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
        BleManager.getInstance().read(bleDevice, uuid, uuid
                , new BleReadCallback() {
                    @Override
                    public void onReadSuccess(byte[] data) {
                        String str = new String(data);
                        Log.d(TAG,str);
                        Toast.makeText(MbPrintService.this,str,Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onReadFailure(BleException exception) {
                        Toast.makeText(MbPrintService.this,exception.getDescription(),Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void setPrintId(PrinterId printId) {
        this.printId = printId;
    }
}
