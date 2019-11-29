package com.hemingway.mbprintservice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Build;
import android.os.Handler;
import android.print.PrintAttributes;
import android.print.PrinterCapabilitiesInfo;
import android.print.PrinterId;
import android.print.PrinterInfo;
import android.printservice.PrinterDiscoverySession;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Data：2018/7/26-15:15
 * <p>
 * Author: hemingway
 */
public class MbPrinterDiscoverySession extends PrinterDiscoverySession {
    private static final String TAG = "PrinterDiscoverySession";
    private BluetoothAdapter mBluetoothAdapter;
    private final MbPrintService mbPrintService;
    private Handler mHandler;

    boolean isDestroy = false;
    public MbPrinterDiscoverySession(MbPrintService mbPrintService, BluetoothAdapter mBluetoothAdapter) {
        this.mbPrintService = mbPrintService;
        // Initializes Bluetooth adapter.
        mHandler = new Handler();
        this.mBluetoothAdapter = mBluetoothAdapter;
    }


    @Override
    public void onStartPrinterDiscovery(@NonNull List<PrinterId> list) {
        Log.d(TAG, "startPrinterDiscovery");
//        final List<PrinterInfo> printers = this.getPrinters();
        //BLE
        if (mBluetoothAdapter != null) {
            //生成一个假的
//            final String name = "HemingwayTest";
//            PrinterInfo myprinter = new PrinterInfo
//                    .Builder(mbPrintService.generatePrinterId(name), name, PrinterInfo.STATUS_IDLE)
//                    .build();
//            printers.add(myprinter);
//            addPrinters(printers);
//
//            scanLeDevice(true);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                mBluetoothAdapter.getBluetoothLeScanner().stopScan(callBack);
//                new Handler().postDelayed(new Runnable() {
//                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//                    @Override
//                    public void run() {
//                        mBluetoothAdapter.getBluetoothLeScanner().startScan(callBack);
//                    }
//                }, 200);
//
//            }

            easyScan();
        }

    }

    private void easyScan() {
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {

            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                if(isDestroy){
                    return;
                }
                BluetoothDevice device = bleDevice.getDevice();
                String deviceName = bleDevice.getName();
                if (deviceName == null) {
                    deviceName = "";
                }
                Log.d(TAG,deviceName+" "+device.getAddress());
                if (DeviceUtils.isLegalDevice(deviceName)) {
                    deviceName = deviceName+"\n"+device.getAddress();
                    PrinterInfo myprinter = new PrinterInfo
                            .Builder(mbPrintService.generatePrinterId(device.getAddress()), deviceName, PrinterInfo.STATUS_IDLE)
                            .build();
                    List<PrinterInfo> printers = MbPrinterDiscoverySession.this.getPrinters();
                    printers.add(myprinter);
                    addPrinters(printers);
                }
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {

            }
        });
    }

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    private boolean mScanning;
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if(isDestroy) {
//                        return;
//                    }
//                        mScanning = false;
//                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
//
//                }
//            }, SCAN_PERIOD);

            mScanning = true;
            boolean b = mBluetoothAdapter.startLeScan(mLeScanCallback);
            //开始扫描失败
            if(!b){
                Log.d(TAG,"Start Scan failed");
            }
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }
    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    if(isDestroy){
                        return;
                    }
                    String deviceName = device.getName();
                    if (deviceName == null) {
                        deviceName = "";
                    }
                    Log.d(TAG,deviceName+" "+device.getAddress());
                    if (DeviceUtils.isLegalDevice(deviceName)) {
                        deviceName = deviceName+"\n"+device.getAddress();
                        PrinterInfo myprinter = new PrinterInfo
                                .Builder(mbPrintService.generatePrinterId(device.getAddress()), deviceName, PrinterInfo.STATUS_IDLE)
                                .build();
                        List<PrinterInfo> printers = MbPrinterDiscoverySession.this.getPrinters();
                        printers.add(myprinter);
                        addPrinters(printers);
                    }
                }
            };

//    private ScanCallback callBack = new ScanCallback() {
//        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//        @Override
//        public void onScanResult(int callbackType, ScanResult result) {
//            BluetoothDevice device = result.getDevice();
//            String deviceName = device.getName();
//            if (deviceName == null) {
//                deviceName = "";
//            }
//            Log.d(TAG,deviceName+" "+device.getAddress());
//            if (DeviceUtils.isLegalDevice(deviceName)) {
//                deviceName = deviceName+"\n"+device.getAddress();
//                PrinterInfo myprinter = new PrinterInfo
//                        .Builder(mbPrintService.generatePrinterId(device.getAddress()), deviceName, PrinterInfo.STATUS_IDLE)
//                        .build();
//                List<PrinterInfo> printers = MbPrinterDiscoverySession.this.getPrinters();
//                //关闭服务之后是异步操作
//                try {
//                    printers.add(myprinter);
//                    addPrinters(printers);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//
//            super.onScanResult(callbackType, result);
//        }
//
//        @Override
//        public void onBatchScanResults(List<ScanResult> results) {
//            super.onBatchScanResults(results);
//        }
//
//        @Override
//        public void onScanFailed(int errorCode) {
//            super.onScanFailed(errorCode);
//        }
//    };

    @Override
    public void onStopPrinterDiscovery() {
        Log.d(TAG, "onStopPrinterDiscovery()");
//        scanLeDevice(false);
        BleManager.getInstance().cancelScan();
    }

    @Override
    public void onValidatePrinters(@NonNull List<PrinterId> list) {
        Log.d(TAG, "onValidatePrinters()");
    }

    private PrinterInfo findPrinterInfo(PrinterId printerId) {
        List<PrinterInfo> printers = getPrinters();
        final int printerCount = getPrinters().size();
        for (int i = 0; i < printerCount; i++) {
            PrinterInfo printer = printers.get(i);
            if (printer.getId().equals(printerId)) {
                return printer;
            }
        }
        return null;
    }

    /**
     * 选择打印机时调用该方法更新打印机的状态，能力
     *
     * @param printerId
     */
    @Override
    public void onStartPrinterStateTracking(@NonNull PrinterId printerId) {
        Log.d(TAG, "onStartPrinterStateTracking()");
        PrinterInfo printer = findPrinterInfo(printerId);
        if (printer != null) {
            PrinterCapabilitiesInfo capabilities =
                    new PrinterCapabilitiesInfo.Builder(printerId)
                            .setMinMargins(new PrintAttributes.Margins(200, 200, 200, 200))
                            .addMediaSize(PrintAttributes.MediaSize.ISO_A4, true)
                            //.addMediaSize(PrintAttributes.MediaSize.ISO_A5, false)
                            .addResolution(new PrintAttributes.Resolution("R1", "200x200", 200, 200), false)
                            .addResolution(new PrintAttributes.Resolution("R2", "300x300", 300, 300), true)
                            .setColorModes(PrintAttributes.COLOR_MODE_COLOR
                                            | PrintAttributes.COLOR_MODE_MONOCHROME,
                                    PrintAttributes.COLOR_MODE_MONOCHROME)
                            .build();

            printer = new PrinterInfo.Builder(printer)
                    .setCapabilities(capabilities)
                    .setStatus(PrinterInfo.STATUS_IDLE)
                    //        .setDescription("fake print 1!")
                    .build();
            List<PrinterInfo> printers = new ArrayList<>();

            printers.add(printer);
            mbPrintService.setPrintId(printerId);
            Log.d(TAG, printer.getName());
            Log.d(TAG, printer.toString());

            addPrinters(printers);
        }
    }

    @Override
    public void onStopPrinterStateTracking(@NonNull PrinterId printerId) {

    }

    @Override
    public void onDestroy() {
        isDestroy = true;
        mHandler.removeCallbacksAndMessages(null);
        Log.d(TAG, "onDestroy()");
    }
}
