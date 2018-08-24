package com.ihemingway.helloworld.filereader;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.ihemingway.helloworld.R;

import org.apache.poi.xwpf.converter.core.FileImageExtractor;
import org.apache.poi.xwpf.converter.core.FileURIResolver;
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

//import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
//import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;

@RuntimePermissions
public class ReadOfficeActivity extends AppCompatActivity {
    private static final String TAG = "ReadOfficeActivity";
    private String docPath = "/mnt/sdcard/";
    private String docName = "testWord.docx";
    private String savePath = "/mnt/sdcard/";
    private String name;
    private WebView webView;
    private String fileUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_office);

//        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
//        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
//        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");

        name = docName.substring(0, docName.indexOf("."));
        ReadOfficeActivityPermissionsDispatcher.needPermissionWithPermissionCheck(this);
        //WebView加载显示本地html文件
        webView = this.findViewById(R.id.office);
        WebSettings webSettings = webView.getSettings();
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setUseWideViewPort(true);//设置webview推荐使用的窗口
        webSettings.setLoadWithOverviewMode(true);//设置webview加载的页面的模式
        webSettings.setDisplayZoomControls(false);//隐藏webview缩放按钮
        webSettings.setJavaScriptEnabled(true); // 设置支持javascript脚本
//        webSettings.setAllowFileAccess(true); // 允许访问文件
//        webSettings.setBuiltInZoomControls(true); // 设置显示缩放按钮
//        webSettings.setSupportZoom(true); // 支持缩放
        fileUrl = "file:/" + savePath + name + ".html";


        webView.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(!loadOnce) {
                    view.loadUrl("javascript:window.local_obj.showSource('<head>'+" + "document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });


        //html to pdf



    }

    private void html2Pdf(String html) {

//        android.graphics.pdf.PdfDocument pdfDocument = new android.graphics.pdf.PdfDocument();
//        // crate a page description
//        android.graphics.pdf.PdfDocument.PageInfo pageInfo = new android.graphics.pdf.PdfDocument.PageInfo.Builder(100,100,1).create();
//        pdfDocument.startPage(pageInfo);
//        View content = getc


//        try {
//            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/test.pdf");
//            if(!file.exists()){
//                file.createNewFile();
//            }
////            PdfDocument document = new PdfDocument();
//            com.itextpdf.text.Document document =new com.itextpdf.text.Document();
////            Rectangle pageSize = new Rectangle(30,30,60,0);
////            document.setPageSize(pageSize);
//            FileInputStream inputStream = new FileInputStream(new File(Environment.getExternalStorageDirectory().getPath()+"/testWord.docx"));
//            OutputStream fileOutputStream = new FileOutputStream(file);
//            PdfWriter pdfWriter = PdfWriter.getInstance(document, fileOutputStream);
//            document.open();
//            XMLWorkerHelper instance = XMLWorkerHelper.getInstance();
//            instance.parseXHtml(pdfWriter,document,inputStream);
////            document.add(new Paragraph(Html.fromHtml(html).toString()));
//            document.close();
//        } catch (DocumentException | IOException e) {
//            e.printStackTrace();
//        }


//        try {
//            OutputStream fileOutputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getPath()+"/test.pdf"));
//            PdfWriter pdfWriter = new PdfWriter(fileOutputStream);
//            HtmlConverter.convertToDocument(fileUrl,pdfWriter);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void showSource(String html) {
            refreshHtmlContent(html);
        }
    }

    boolean loadOnce = false;
    private void refreshHtmlContent(final String html) {
        loadOnce= true;
        //解析html字符串为对象
        Document document = Jsoup.parse(html);
        // 通过类名获取到一组Elements，获取一组中第一个element并设置其html
        //通过ID获取到element并设置其src属性
//        Element element = document.getElementById("imageView");
//        if (element != null) {
//            element.attr("src", "file:///android_asset/dragon.jpg"); //通过类名获取到一组Elements，获取一组中第一个element并设置其文本
//        }
        Element head = document.head();
        head.append("" +
//                "<head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" /> " +
                "<style>div{width:100pt;}</style>" +
                "");

//        Elements div = document.getElementsByTag("div");
//        if (div != null) {
//            div.attr("style\"width", "100pt");
//        }


//        final String body = document.toString();
//        Log.d(TAG,body);
//        webView.post(new Runnable() {
//            @Override
//            public void run() {
//                webView.loadDataWithBaseURL(null, body, "text/html", "utf-8", null);
//            }
//        });

        webView.post(new Runnable() {
            @Override
            public void run() {
//                html2Pdf(html);
                TextView tvContent = findViewById(R.id.tv_content);
                tvContent.setText(Html.fromHtml(html));

            }
        });

    }

    public void convertDocx2Html(String fileName, String outFile) {
        if (!new File(fileName).exists()) {
            Log.d("Hemingway", "file is no exist");
            return;
        }
        XWPFDocument document = null;
        try {
            document = new XWPFDocument(getResources().openRawResource(R.raw.test_word));
            XHTMLOptions options = XHTMLOptions.create();// .indent( 4 );
            // Extract image
            File imageFolder = new File(Environment.getExternalStorageDirectory() + "/images/" + "tmp");
            options.setExtractor(new FileImageExtractor(imageFolder));
            options.setFragment(true);
            options.setOmitHeaderFooterPages(true);
            // URI resolver
            options.URIResolver(new FileURIResolver(imageFolder));
            OutputStream out = new FileOutputStream(new File(outFile));
            XHTMLConverter.getInstance().convert(document, out, options);

            FileOutputStream pdfOut = new FileOutputStream(new File(Environment.getExternalStorageDirectory().getPath()+"/hemingway.pdf"));

            PdfOptions op= PdfOptions.create();
//            PdfConverter.getInstance().convert(document,pdfOut,op);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

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
//
//        HWPFDocument wordDocument = new HWPFDocument(new FileInputStream(fileName));
//        WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
//                DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
//
//        //设置图片路径
//        wordToHtmlConverter.setPicturesManager(new PicturesManager() {
//            public String savePicture(byte[] content,
//                                      PictureType pictureType, String suggestedName,
//                                      float widthInches, float heightInches) {
//                String name = docName.substring(0, docName.indexOf("."));
//                return name + "/" + suggestedName;
//            }
//        });
//
//        //保存图片
//        List<Picture> pics = wordDocument.getPicturesTable().getAllPictures();
//        if (pics != null) {
//            for (int i = 0; i < pics.size(); i++) {
//                Picture pic = (Picture) pics.get(i);
//                System.out.println(pic.suggestFullFileName());
//                try {
//                    String name = docName.substring(0, docName.indexOf("."));
//                    pic.writeImageContent(new FileOutputStream(savePath + name + "/"
//                            + pic.suggestFullFileName()));
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        wordToHtmlConverter.processDocument(wordDocument);
//        Document htmlDocument = wordToHtmlConverter.getDocument();
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        DOMSource domSource = new DOMSource(htmlDocument);
//        StreamResult streamResult = new StreamResult(out);
//
//        TransformerFactory tf = TransformerFactory.newInstance();
//        Transformer serializer = tf.newTransformer();
//        serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
//        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
//        serializer.setOutputProperty(OutputKeys.METHOD, "html");
//        serializer.transform(domSource, streamResult);
//        out.close();
//        //保存html文件
//        writeFile(new String(out.toByteArray()), outPutFile);
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!(new File(savePath + name).exists()))
                        new File(savePath + name).mkdirs();
                    convertDocx2Html(docPath + docName, savePath + name + ".html");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Jsoup.connect(fileUrl);
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadUrl(fileUrl);

                    }
                });

            }
        }).start();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ReadOfficeActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
