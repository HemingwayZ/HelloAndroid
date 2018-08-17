package com.ihemingway.helloworld.filereader;

import android.Manifest;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.ihemingway.helloworld.R;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.apache.poi.xwpf.converter.core.FileImageExtractor;
import org.apache.poi.xwpf.converter.core.FileURIResolver;
//import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
//import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.w3c.dom.Document;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class ReadOfficeActivity extends AppCompatActivity {
    private String docPath = "/mnt/sdcard/";
    private String docName = "testWord.docx";
    private String savePath = "/mnt/sdcard/";
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_office);

        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");

        name = docName.substring(0, docName.indexOf("."));
        ReadOfficeActivityPermissionsDispatcher.needPermissionWithPermissionCheck(this);
        //WebView加载显示本地html文件
        WebView webView = this.findViewById(R.id.office);
        WebSettings webSettings = webView.getSettings();
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webView.loadUrl("file:/" + savePath + name + ".html");
    }

    public void convertDocx2Html(String fileName,String outFile) {
        if (!new File(fileName).exists()) {
            Log.d("Hemingway", "file is no exist");
            return;
        }
        XWPFDocument document = null;
        try {
            document = new XWPFDocument(new FileInputStream(fileName));

            XHTMLOptions options = XHTMLOptions.create();// .indent( 4 );
            // Extract image
            File imageFolder = new File(Environment.getExternalStorageDirectory() + "/images/" + "tmp");
            options.setExtractor(new FileImageExtractor(imageFolder));
            // URI resolver
            options.URIResolver(new FileURIResolver(imageFolder));

            OutputStream out = new FileOutputStream(new File(outFile));
            XHTMLConverter.getInstance().convert(document, out, options);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(document!=null) {
                try {
                    document.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    /**
     * word文档转成html格式
     */
    public void convert2Html(String fileName, String outPutFile)
            throws TransformerException, IOException,
            ParserConfigurationException {
        if (!new File(fileName).exists()) {
            Log.d("Hemingway", "file is no exist");
            return;
        }

        HWPFDocument wordDocument = new HWPFDocument(new FileInputStream(fileName));
        WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
                DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());

        //设置图片路径
        wordToHtmlConverter.setPicturesManager(new PicturesManager() {
            public String savePicture(byte[] content,
                                      PictureType pictureType, String suggestedName,
                                      float widthInches, float heightInches) {
                String name = docName.substring(0, docName.indexOf("."));
                return name + "/" + suggestedName;
            }
        });

        //保存图片
        List<Picture> pics = wordDocument.getPicturesTable().getAllPictures();
        if (pics != null) {
            for (int i = 0; i < pics.size(); i++) {
                Picture pic = (Picture) pics.get(i);
                System.out.println(pic.suggestFullFileName());
                try {
                    String name = docName.substring(0, docName.indexOf("."));
                    pic.writeImageContent(new FileOutputStream(savePath + name + "/"
                            + pic.suggestFullFileName()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        wordToHtmlConverter.processDocument(wordDocument);
        Document htmlDocument = wordToHtmlConverter.getDocument();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DOMSource domSource = new DOMSource(htmlDocument);
        StreamResult streamResult = new StreamResult(out);

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer serializer = tf.newTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.METHOD, "html");
        serializer.transform(domSource, streamResult);
        out.close();
        //保存html文件
        writeFile(new String(out.toByteArray()), outPutFile);
    }

    /**
     * 将html文件保存到sd卡
     */
    public void writeFile(String content, String path) {
        FileOutputStream fos = null;
        BufferedWriter bw = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            bw = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"));
            bw.write(content);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fos != null)
                    fos.close();
            } catch (IOException ie) {
            }
        }
    }

    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void needPermission() {
        try {
            if (!(new File(savePath + name).exists()))
                new File(savePath + name).mkdirs();
            convertDocx2Html(docPath + docName, savePath + name + ".html");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ReadOfficeActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
