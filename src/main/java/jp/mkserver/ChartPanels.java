package jp.mkserver;

import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.DefaultHighLowDataset;

import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class ChartPanels {

    public static ChartPanel loadhourCSV(String id){

        //ファイル読み込みで使用する３つのクラス
        FileInputStream fi = null;
        InputStreamReader is = null;
        BufferedReader br = null;

        try {

            //読み込みファイルのインスタンス生成
            //ファイル名を指定する
            fi = new FileInputStream(MainPanel.getApplicationPath(MCEViewer.class).getParent().toString() + File.separator + "hour.csv");
            is = new InputStreamReader(fi,"UTF-8");
            br = new BufferedReader(is);

            //読み込み行
            String line;

            //読み込み行数の管理
            int i = 0;


            int lines = (int)countLines()-1;

            if(lines > 99){
                lines = 99;
            }

            Date[] date = new Date[lines];
            double[] high = new double[lines];
            double[] low = new double[lines];
            double[] open = new double[lines];
            double[] close = new double[lines];
            double[] volume = new double[lines];

            //1行ずつ読み込みを行う
            while ((line = br.readLine()) != null) {

                //先頭行は列名 100行に到達後break
                if(i == 100) {
                    break;
                }else if (i != 0) {

                    //カンマで分割した内容を配列に格納する
                    String[] data = line.split(",");

                    //配列の中身を順位表示する。列数(=列名を格納した配列の要素数)分繰り返す
                    int colno = i-1;
                    SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd hh");
                    date[colno] = sdFormat.parse(data[0]);
                    open[colno] = Double.parseDouble(data[1]);
                    high[colno] = Double.parseDouble(data[2]);
                    low[colno] = Double.parseDouble(data[3]);
                    close[colno] = Double.parseDouble(data[4]);
                    volume[colno] = Double.parseDouble(data[5]);
                }

                //行数のインクリメント
                i++;

            }

            DefaultHighLowDataset data = new DefaultHighLowDataset(""+id,
                    date,
                    high,
                    low,
                    open,
                    close,
                    volume);
            ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
            JFreeChart chart = ChartFactory.createCandlestickChart("価格推移",
                    "日付",
                    "価格",
                    data,
                    false);

            if(ConfigFileManager.black_mode) {
                chart.setBackgroundPaint(ChartColor.darkGray);
                chart.getTitle().setPaint(ChartColor.WHITE);
                chart.getXYPlot().getDomainAxis().setTickLabelPaint(ChartColor.WHITE);
                chart.getXYPlot().getDomainAxis().setLabelPaint(ChartColor.WHITE);
                chart.getXYPlot().getRangeAxis().setTickLabelPaint(ChartColor.WHITE);
                chart.getXYPlot().getRangeAxis().setLabelPaint(ChartColor.WHITE);
                chart.getPlot().setBackgroundPaint(ChartColor.darkGray);
            }

            return  new ChartPanel(chart) {
                public Dimension getPreferredSize() {
                    return new Dimension(450,450);
                }
            };

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert br != null;
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static ChartPanel loadhourCSV_simple(String name){

        //ファイル読み込みで使用する３つのクラス
        FileInputStream fi = null;
        InputStreamReader is = null;
        BufferedReader br = null;

        try {

            //読み込みファイルのインスタンス生成
            //ファイル名を指定する
            fi = new FileInputStream(MainPanel.getApplicationPath(MCEViewer.class).getParent().toString() + File.separator + "hour.csv");
            is = new InputStreamReader(fi,"UTF-8");
            br = new BufferedReader(is);

            //読み込み行
            String line;

            //読み込み行数の管理
            int i = (int)countLines()-1;

            if(i > 10){
                i = 10;
            }

            ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
            DefaultCategoryDataset datas = new DefaultCategoryDataset();

            ArrayList<ChartData> chartDatas = new ArrayList<>();
            //1行ずつ読み込みを行う
            while ((line = br.readLine()) != null) {

                //先頭行はskip 10行に到達後break
                if(i == 10) {
                    i--;
                    continue;
                }else if(i == 0){
                    break;
                }

                //カンマで分割した内容を配列に格納する
                String[] data = line.split(",");

                //配列の中身を順位表示する。列数(=列名を格納した配列の要素数)分繰り返す
                SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd HH");
                SimpleDateFormat sdf = new SimpleDateFormat("HH");
                Date date = sdFormat.parse(data[0]);
                String dates = sdf.format(date);
                Double closed = Double.parseDouble(data[4]);
                ChartData chartData = new ChartData();
                chartData.closed = closed;
                chartData.dates = dates;
                chartDatas.add(chartData);

                //行数のインクリメント
                i--;

            }

            Collections.reverse(chartDatas);//要素を逆順にする

            for (ChartData l : chartDatas) {
                datas.addValue(l.closed,name,l.dates);
            }

            JFreeChart chart = ChartFactory.createLineChart("価格推移", "日付(時)", "価格", datas, PlotOrientation.VERTICAL, true, false, false);

            if(ConfigFileManager.black_mode) {
                chart.setBackgroundPaint(ChartColor.darkGray);
                chart.getTitle().setPaint(ChartColor.WHITE);
                chart.getCategoryPlot().getDomainAxis().setTickLabelPaint(ChartColor.WHITE);
                chart.getCategoryPlot().getDomainAxis().setLabelPaint(ChartColor.WHITE);
                chart.getCategoryPlot().getRangeAxis().setTickLabelPaint(ChartColor.WHITE);
                chart.getCategoryPlot().getRangeAxis().setLabelPaint(ChartColor.WHITE);
                chart.getPlot().setBackgroundPaint(ChartColor.darkGray);
            }

            return  new ChartPanel(chart) {
                public Dimension getPreferredSize() {
                    return new Dimension(450,450);
                }
            };

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert br != null;
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static long countLines(){
        try{
            BufferedReader fin = new BufferedReader(new FileReader(MainPanel.getApplicationPath(MCEViewer.class).getParent().toString() + File.separator + "hour.csv"));
            String aLine;
            long n = 0L;

            while(null!=(aLine = fin.readLine())){
                n++;
            }
            fin.close();
            return n;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    static class ChartData{
        String dates;
        Double closed;
    }
}
