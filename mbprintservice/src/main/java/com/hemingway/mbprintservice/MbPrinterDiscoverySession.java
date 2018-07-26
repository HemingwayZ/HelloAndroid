package com.hemingway.mbprintservice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.print.PrintAttributes;
import android.print.PrinterCapabilitiesInfo;
import android.print.PrinterId;
import android.print.PrinterInfo;
import android.printservice.PrinterDiscoverySession;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

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
    public MbPrinterDiscoverySession(MbPrintService mbPrintService, BluetoothAdapter mBluetoothAdapter) {
        this.mbPrintService = mbPrintService;
        // Initializes Bluetooth adapter.
        this.mBluetoothAdapter = mBluetoothAdapter;
    }

    @Override
    public void onStartPrinterDiscovery(@NonNull List<PrinterId> list) {
        Log.d(TAG, "startPrinterDiscovery");
        final List<PrinterInfo> printers = this.getPrinters();



        //BLE
        if(mBluetoothAdapter!=null){
            //生成一个假的
            final String name = "HemingwayTest";
            PrinterInfo myprinter = new PrinterInfo
                    .Builder(mbPrintService.generatePrinterId(name), name, PrinterInfo.STATUS_IDLE)
                    .build();
            printers.add(myprinter);

//            mBluetoothAdapter.startLeScan(new BluetoothAdapter.LeScanCallback() {
//                @Override
//                public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
//                    StringBuffer sb = new StringBuffer();
//                    sb.append(bluetoothDevice.getName()).append("-")
//                            .append(bluetoothDevice.getAddress());
//                    PrinterInfo myprinter = new PrinterInfo
//                            .Builder(mbPrintService.generatePrinterId(sb.toString()), toString(), PrinterInfo.STATUS_IDLE)
//                            .build();
//                    printers.add(myprinter);
//                }
//            });

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBluetoothAdapter.getBluetoothLeScanner().stopScan(callBack);
                new Handler().postDelayed(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {
                        mBluetoothAdapter.getBluetoothLeScanner().startScan(callBack);
                    }
                },200);

            }
        }
        addPrinters(printers);
    }


    private ScanCallback callBack =  new ScanCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            String deviceName = device.getName();
            if(deviceName==null){
                deviceName = "";
            }
            if(TextUtils.equals(deviceName.toLowerCase(),"XXX")
                    ||TextUtils.equals(deviceName.toLowerCase(),"XXX")){
                PrinterInfo myprinter = new PrinterInfo
                        .Builder(mbPrintService.generatePrinterId(result.getDevice().getAddress()), deviceName, PrinterInfo.STATUS_IDLE)
                        .build();
                List<PrinterInfo> printers = MbPrinterDiscoverySession.this.getPrinters();

                if(myprinter!=null&&printers!=null){
                    printers.add(myprinter);
                    addPrinters(printers);
                }

            }

            super.onScanResult(callbackType, result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };
    @Override
    public void onStopPrinterDiscovery() {
        Log.d(TAG, "onStopPrinterDiscovery()");
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
            Log.d(TAG,printer.getName());
            Log.d(TAG,printer.toString());

            addPrinters(printers);
        }
    }

    @Override
    public void onStopPrinterStateTracking(@NonNull PrinterId printerId) {
        Log.d(TAG, "onStopPrinterStateTracking()");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
    }
}
