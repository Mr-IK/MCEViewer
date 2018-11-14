package jp.mkserver;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import org.apache.commons.io.FilenameUtils;
import org.jfree.chart.ChartPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimerTask;
import java.util.Timer;

public class MainPanel extends JPanel implements ActionListener {

    JFrame frame;
    JPanel jpane;
    JLabel iconlabel;
    JLabel money;
    JLabel nowupdate;
    JComboBox intCombo;
    String[] ids = new String[56];

    String url1 = "http://man10.red/mce/";
    String url3 = "http://man10.red/mce/image/item";
    String url4 = ".png";
    String url2 = "/index.csv";
    String url5 = "/hour.csv";
    ConfigFileManager config;

    boolean nowrun = false;

    String latest_id = "1";

    public String getUrl(String id){
        return url1+id+url2;
    }

    public String getHourUrl(String id){
        return url1+id+url5;
    }

    public String getImage(String id){
        return url3+id+url4;
    }

    public MainPanel(GUImanager gui){
        frame = gui;
        System.out.println("DEBUG: create main panel start");
        idCreate();
        System.out.println("DEBUG: create main panel 10%");
        setName("MainGUI");
        System.out.println("DEBUG: create main panel 30%");
        iconlabel = new JLabel();
        iconlabel.setOpaque(false);
        setLayout(new FlowLayout());
        add(iconlabel);
        System.out.println("DEBUG: create main panel 60%");
        intCombo = new JComboBox<>(ids);
        intCombo.addActionListener(this);
        add(intCombo);
        money = new JLabel();
        System.out.println("DEBUG: create main panel 90%");
        add(money);
        nowupdate = new JLabel();
        add(nowupdate);
        setmoney("0","0");
        config = new ConfigFileManager();
        jpane = new JPanel();
        if(ConfigFileManager.black_mode) {
            setBackground(Color.darkGray);
            intCombo.setBackground(Color.darkGray);
            jpane.setBackground(Color.darkGray);
            intCombo.setForeground(Color.WHITE);
            money.setForeground(Color.WHITE);
            nowupdate.setForeground(Color.WHITE);
        }
        add(jpane,BorderLayout.SOUTH);
        System.out.println("DEBUG: create main panel end");
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Update();
            }
        };
        timer.schedule(task, 60000, 60000);
    }

    public void actionPerformed(ActionEvent e) {
        if(nowrun){
            JOptionPane.showMessageDialog(this, "現在処理中です。");
            return;
        }
        new Thread(()->{
            nowrun = true;
            nowupdate.setText("更新中…");
            String id = (String)intCombo.getSelectedItem();
            latest_id = id;
            //v2
            try (final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
                webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
                webClient.getPage(getUrl(id));
                webClient.waitForBackgroundJavaScript(10_000);
                //waiting…
                Page page = webClient.getPage(getUrl(id));
                Page hourpage = webClient.getPage(getHourUrl(id));
                // Download the file.
                InputStream inputStream = page.getWebResponse().getContentAsStream();
                FileOutputStream outputStream = new FileOutputStream(getApplicationPath(MCEViewer.class).getParent().toString() + File.separator + "index.csv");
                int read;
                byte[] bytes = new byte[1024];
                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
                inputStream.close();
                outputStream.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(getApplicationPath(MCEViewer.class).getParent().toString() + File.separator + "index.csv"),"UTF-8"));

                String str = br.readLine();
                setmoney(id,str);

                inputStream = hourpage.getWebResponse().getContentAsStream();
                outputStream = new FileOutputStream(getApplicationPath(MCEViewer.class).getParent().toString() + File.separator + "hour.csv");
                bytes = new byte[1024];
                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
                inputStream.close();
                outputStream.close();
                ChartPanel pane = ChartPanels.loadhourCSV(id);
                if(pane != null){
                    jpane.removeAll();
                    jpane.add(pane,BorderLayout.CENTER);
                }
            } catch (IOException | URISyntaxException e1) {
                e1.printStackTrace();
            }
            //v2

            //v2
            try (final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
                if(!new File(getApplicationPath(MCEViewer.class).getParent().toString() + File.separator +"images").exists()){
                    new File(getApplicationPath(MCEViewer.class).getParent().toString() + File.separator +"images").mkdir();
                }
                if(!new File(getApplicationPath(MCEViewer.class).getParent().toString() + File.separator +"images"+ File.separator + id+".png").exists()) {
                    webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
                    webClient.getPage(getImage(id));
                    webClient.waitForBackgroundJavaScript(10_000);
                    //waiting…
                    Page page = webClient.getPage(getImage(id));
                    // Download the file.
                    InputStream inputStream = page.getWebResponse().getContentAsStream();
                    FileOutputStream outputStream = new FileOutputStream(getApplicationPath(MCEViewer.class).getParent().toString() + File.separator +"images"+ File.separator + id+".png");
                    int read;
                    byte[] bytes = new byte[1024];
                    while ((read = inputStream.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, read);
                    }
                    inputStream.close();
                    outputStream.close();
                }
                ImageIcon icon = new ImageIcon(getApplicationPath(MCEViewer.class).getParent().toString() + File.separator +"images"+ File.separator + id+".png");
                icon.getImage().flush();
                SwingUtilities.invokeLater(() -> iconlabel.setIcon(icon));
            } catch (IOException | URISyntaxException e1) {
                e1.printStackTrace();
            }
            //v2
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("更新: a h時mm分");
            nowupdate.setText(sdf.format(calendar.getTime()));
            nowrun = false;
        }).start();
    }

    public void Update(){
        new Thread(()->{
            nowupdate.setText("更新中…");
            nowrun = true;
            String id = latest_id;
            //v2
            try (final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
                webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
                webClient.getPage(getUrl(id));
                webClient.waitForBackgroundJavaScript(10_000);
                //waiting…
                Page page = webClient.getPage(getUrl(id));
                Page hourpage = webClient.getPage(getHourUrl(id));
                // Download the file.
                InputStream inputStream = page.getWebResponse().getContentAsStream();
                FileOutputStream outputStream = new FileOutputStream(getApplicationPath(MCEViewer.class).getParent().toString() + File.separator + "index.csv");
                int read;
                byte[] bytes = new byte[1024];
                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
                inputStream.close();
                outputStream.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(getApplicationPath(MCEViewer.class).getParent().toString() + File.separator + "index.csv"),"UTF-8"));

                String str = br.readLine();
                setmoney(id,str);

                inputStream = hourpage.getWebResponse().getContentAsStream();
                outputStream = new FileOutputStream(getApplicationPath(MCEViewer.class).getParent().toString() + File.separator + "hour.csv");
                bytes = new byte[1024];
                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
                inputStream.close();
                outputStream.close();
                ChartPanel pane = ChartPanels.loadhourCSV(id);
                if(pane != null){
                    jpane.removeAll();
                    jpane.add(pane,BorderLayout.CENTER);
                }
            } catch (IOException | URISyntaxException e1) {
                e1.printStackTrace();
            }
            //v2

            //v2
            try (final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
                if(!new File(getApplicationPath(MCEViewer.class).getParent().toString() + File.separator +"images").exists()){
                    new File(getApplicationPath(MCEViewer.class).getParent().toString() + File.separator +"images").mkdir();
                }
                if(!new File(getApplicationPath(MCEViewer.class).getParent().toString() + File.separator +"images"+ File.separator + id+".png").exists()) {
                    webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
                    webClient.getPage(getImage(id));
                    webClient.waitForBackgroundJavaScript(10_000);
                    //waiting…
                    Page page = webClient.getPage(getImage(id));
                    // Download the file.
                    InputStream inputStream = page.getWebResponse().getContentAsStream();
                    FileOutputStream outputStream = new FileOutputStream(getApplicationPath(MCEViewer.class).getParent().toString() + File.separator +"images"+ File.separator + id+".png");
                    int read;
                    byte[] bytes = new byte[1024];
                    while ((read = inputStream.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, read);
                    }
                    inputStream.close();
                    outputStream.close();
                }
                ImageIcon icon = new ImageIcon(getApplicationPath(MCEViewer.class).getParent().toString() + File.separator +"images"+ File.separator + id+".png");
                icon.getImage().flush();
                SwingUtilities.invokeLater(() -> iconlabel.setIcon(icon));
            } catch (IOException | URISyntaxException e1) {
                e1.printStackTrace();
            }
            //v2
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("更新: a h時mm分");
            nowupdate.setText(sdf.format(calendar.getTime()));
            nowrun = false;
        }).start();
    }

    public void idCreate(){
        System.out.println("DEBUG: create ids start");
        for(int i = 1;i <= 56;i++){
            ids[i-1] = i+"";
            System.out.println("DEBUG: create id =>"+i);
        }
    }

    public void setmoney(String id,String bal){
        bal = bal.replace("\thttp://man10.red/mce/image/item"+id+".png","");
        money.setText(bal);
    }


    public static Path getApplicationPath(Class<?> cls) throws URISyntaxException {
        ProtectionDomain pd = cls.getProtectionDomain();
        CodeSource cs = pd.getCodeSource();
        URL location = cs.getLocation();
        URI uri = location.toURI();
        return Paths.get(uri);
    }
}
