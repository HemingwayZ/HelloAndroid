package com.ihemingway.helloworld.filereader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.os.Handler;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ihemingway.helloworld.R;

import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.xwpf.converter.core.FileImageExtractor;
import org.apache.poi.xwpf.converter.core.FileURIResolver;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.picture.CTPicture;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageBreak;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBrType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ReadOfficeLocalActivity extends AppCompatActivity {

    private static final String TAG = ReadOfficeLocalActivity.class.getSimpleName();
    String content;
    private Bitmap bitmap;

    Handler handler;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_office_local);
        handler = new Handler();
        textView = findViewById(R.id.textView);
        textView.setText(content);
        new Thread(new Runnable() {
            @Override
            public void run() {
                convertDocx2Local();

                ReadOfficeLocalActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.progressBar).setVisibility(View.GONE);


//                        if (bitmap != null) {
//                            ImageSpan imgSpan = new ImageSpan(ReadOfficeLocalActivity.this, bitmap);
//                            SpannableString spanString = new SpannableString("icon");
//                            spanString.setSpan(imgSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                            textView.append(spanString);
//                        }
                    }
                });
            }
        }).start();

    }

    public void convertDocx2Local() {
//        PrintAttributes attributes = new PrintAttributes.Builder()
//                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
//                .setResolution(new PrintAttributes.Resolution("1", "print", 1200, 1200))
//                .setMinMargins(new PrintAttributes.Margins(0, 0, 0, 0))
//                .setColorMode(PrintAttributes.COLOR_MODE_COLOR) // 可以不传
//                //.setDuplexMode(PrintAttributes.DUPLEX_MODE_NONE)  // API小于23会报错，可以不传
//                .build();
//        PrintedPdfDocument pdfDocument = new PrintedPdfDocument(this, attributes);
//
//// 绘制PDF
//        PdfDocument.Page page = pdfDocument.startPage(0);   // 开始页，页号从0开始
//        Canvas canvas = page.getCanvas();
//        canvas.draw



        XWPFDocument document = null;
        try {
            document = new XWPFDocument(getResources().openRawResource(R.raw.test_word));
//            String fileName = Environment.getExternalStorageDirectory().getPath() + "/test_word2.docx";
//            document = new XWPFDocument(new FileInputStream(fileName));


//            XWPFWordExtractor wordExtractor = new XWPFWordExtractor(document);
//            content = wordExtractor.getText();

            //normal picture
            List<XWPFPictureData> allPictures = document.getAllPictures();
//            for (XWPFPictureData item : allPictures) {
//                Log.d(TAG, "CheckSum = " + String.valueOf(item.getChecksum()));
//                byte[] data = item.getData();
//                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//            }

            List<POIXMLDocumentPart> relations = document.getRelations();
            //normal text
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            StringBuffer sb = new StringBuffer();

            int pages = document.getProperties().getExtendedProperties().getUnderlyingProperties().getPages();
            Log.d(TAG, "pages = " + pages);
            for (XWPFParagraph paramitem : paragraphs) {
//                paramitem.getBorderBottom().

                List<XWPFRun> runs = paramitem.getRuns();
                for (final XWPFRun run :
                        runs) {

                    final String text = run.getText(run.getTextPosition());
                    CTR ctr = run.getCTR();
                    List<CTBr> brList = ctr.getBrList();
                    for (CTBr item : brList) {
                        if (item.getType() == STBrType.PAGE) {
                            Log.d(TAG, "Hemingway page ");
                        }
                    }

//                    XmlCursor xmlCursor = ctr.newCursor();
//                    if (xmlCursor != null) {
//                        while (xmlCursor.hasNextSelection()) {
//                            XmlObject object = xmlCursor.getObject();
//                            if (object instanceof CTPageBreak) {
//                                Log.d(TAG, "PageBreak");
//                            }
//                        }
//                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (text != null) {
//                                String color = run.getColor();
                                int fontSize = run.getFontSize();
                                SpannableStringBuilder ssb = new SpannableStringBuilder(text);
                                if (text.length() > 0) {
                                    String color = run.getColor();
                                    if (!TextUtils.isEmpty(color)) {
                                        ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#" + color)), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    }
                                }

                                if (fontSize > 0) {
                                    ssb.setSpan(new AbsoluteSizeSpan(fontSize, true), 0, text.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
                                }
                                textView.append(ssb);
                            }
                        }
                    });

//                    sb.append(run.getText(run.getTextPosition()));
                    List<XWPFPicture> embeddedPictures = run.getEmbeddedPictures();
                    if (embeddedPictures != null && embeddedPictures.size() > 0) {
                        Log.d(TAG, "position = " + run.getTextPosition());
                        for (XWPFPicture xwpfPicture : embeddedPictures) {
                            byte[] data = xwpfPicture.getPictureData().getData();
                            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (bitmap != null) {
                                        ImageSpan imgSpan = new ImageSpan(ReadOfficeLocalActivity.this, bitmap);
                                        SpannableString spanString = new SpannableString("icon");
                                        spanString.setSpan(imgSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        textView.append(spanString);
                                    }
                                }
                            });

                        }
                    }
                }

                final boolean pageBreak = paramitem.isPageBreak();
                Log.d(TAG, "pageBreak  = " + pageBreak);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (pageBreak) {
                            textView.append("\n\n-----------page break---------\n");
                        }
                        textView.append("\nhemingway no break\n");
                    }
                });
//                sb.append(paramitem.getText()).append("\n");
            }
            content = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }


    }
}
