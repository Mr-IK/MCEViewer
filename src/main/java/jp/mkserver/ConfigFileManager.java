package jp.mkserver;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.ProtectionDomain;


public class ConfigFileManager {
    File file;
    public ConfigFileManager(){
        try{
            file = new File(getApplicationPath(MCEViewer.class).getParent().toString()+File.separator+"config.txt");
            if(file.createNewFile()) {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true),"utf-8"));
                bw.write("※※余計な文字列を書かないでください。最悪バグります");
                bw.newLine();
                bw.write("※black_modeは画面を暗くするか否かです。");
                bw.newLine();
                bw.write("※trueで暗く、falseで白になります。");
                bw.newLine();
                bw.write("black_mode=true");
                bw.close();
            }else{
                load();
            }
        }catch(IOException | URISyntaxException e){
            System.out.println(e.getMessage());
        }
    }

    public static boolean black_mode = true;

    public void load(){
        System.out.println("[CONFIG]コンフィグをロード中…");
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
            String str = br.readLine();
            while(str != null){
                if(str.startsWith("black_mode=")) {
                    String boor = str.replace("black_mode=", "");
                    if (!boor.equalsIgnoreCase("true")) {
                        black_mode = false;
                        System.out.println("[CONFIG]Logファイル書き込みを無効化しました！");
                    }
                }
                str = br.readLine();
            }

            br.close();
        }catch(IOException ignored){

        }
        System.out.println("[CONFIG]コンフィグのロード完了！");
    }


    public Path getApplicationPath(Class<?> cls) throws URISyntaxException {
        ProtectionDomain pd = cls.getProtectionDomain();
        CodeSource cs = pd.getCodeSource();
        URL location = cs.getLocation();
        URI uri = location.toURI();
        Path path = Paths.get(uri);
        return path;
    }


}
