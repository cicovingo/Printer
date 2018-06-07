import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.sun.deploy.util.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sukru.okul
 * Date: 24.03.2016
 * Time: 10:03
 * To change this template use File | Settings | File Templates.
 */
public class PrinterGet extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private DataPanel myDataPanel;
    private Connection dbconn;
    private static int numPeople = 0;
    private static String info;
    JButton btnBayrampasa = new JButton("Bayrampaşa");        //**
    JButton btnYenibosna = new JButton("YeniBosna");
    JButton btnMerkez = new JButton("Merkez");
    private static JTextArea txtInfo = new JTextArea(8, 40);


    public PrinterGet() throws IOException {
        super("Yazıcı Takip Sistemi");
        GridLayout myGridLayout = new GridLayout(6, 1); //2 rows 1 col allows 2 panels
        Container p = getContentPane();
        myDataPanel = new DataPanel();
        p.add(myDataPanel);
        myDataPanel.setLayout(myGridLayout);
        txtInfo.setText(info);   //sets connection information
        setSize(600, 400);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
        this.setIconImage(Toolkit.getDefaultToolkit().getImage("resources\\ico.jng"));
//        set
        setVisible(true);
    }

    public static void main(String args[]) throws IOException {
        //printerEkle("hp","test","10.34.24.177");
        PrinterGet printerGet = new PrinterGet();
        printerGet.addWindowListener
                (
                        new WindowAdapter() {
                            public void windowClosing(WindowEvent e) {
                                System.exit(0);
                            }
                        }
                );

    }

    class DataPanel extends JPanel implements ActionListener {
        JLabel lbBolge = new JLabel("Bölge");
        String[] bolgeler = {"Merkez (Bomonti)", "Yenibosna", "Bayrampaşa"};
        JComboBox comboBox = new JComboBox(bolgeler);

        JLabel lblip = new JLabel("İp");
        JTextField txtip = new JTextField(10);
        JLabel lblModel = new JLabel("Model");
        JTextField txtModel = new JTextField(10);
        JButton btnAdd = new JButton("Cihaz Ekle");
        JButton btnUpdate = new JButton("Cihaz Kayıtlarını Görüntüle");        //**
        JButton btnClear = new JButton("Temizle");
        JButton btnExit = new JButton("Çıkış");
        JButton print = new JButton("Excel e Aktar");
        JButton print1 = new JButton("Excel e Aktar");
        JButton print2 = new JButton("Excel e Aktar");
        JLabel lblBaslangic = new JLabel("Başlangıç Tarihi");
        JLabel lbBitis = new JLabel("Bitiş Tarihi");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        JLabel lblcopy = new JLabel("\u00a9 Şükrü Okul");
        Date date = new Date();
        JLabel lbltarih = new JLabel(String.valueOf(dateFormat.format(date)));
        //        SwingCalendar calendarPanel = new SwingCalendar();
//        SwingCalendar calendarPanel1 = new SwingCalendar();
        JTextField txtdate = new JTextField(10);

        public DataPanel() throws IOException {
            JPanel myPanel = new JPanel();
            JPanel myPanel2 = new JPanel();
            JPanel myPanel3 = new JPanel();
            JPanel myPanel4 = new JPanel();
            JPanel mypanel5 = new JPanel();
            JPanel mypanel6 = new JPanel();
            myPanel.setLayout(new GridLayout(3, 2)); //4 rows 2 cols
            myPanel2.setLayout(new GridLayout(2, 2)); //2 rows 3 cols
            myPanel3.setLayout(new GridLayout(1, 3));
            myPanel4.setLayout(new GridLayout(2, 2));
            mypanel6.setLayout(new GridLayout(1, 1));
            myPanel4.add(lbBitis);
//            myPanel4.add(calendarPanel.getContentPane());
            myPanel4.add(lbltarih);
            myPanel4.add(lblBaslangic);
            txtdate.setEnabled(false);
            myPanel4.add(txtdate);
            mypanel5.add(lblcopy);
            add(myPanel);
            add(myPanel2);
            add(myPanel3);
            add(myPanel4);
            add(mypanel6);
            add(mypanel5);
            myPanel.add(lbBolge);
            myPanel.add(comboBox);
            myPanel.add(lblModel);
            myPanel.add(txtModel);
            myPanel.add(lblip);
            myPanel.add(txtip);
            myPanel2.add(btnAdd);
            myPanel2.add(btnUpdate);
            myPanel2.add(btnClear);
            myPanel2.add(btnExit);
            btnBayrampasa.setEnabled(false);
            btnYenibosna.setEnabled(false);
            btnMerkez.setEnabled(false);
            myPanel3.add(btnBayrampasa);
            myPanel3.add(btnYenibosna);
            myPanel3.add(btnMerkez);

            //puts txtInfo on application and allows it to scroll
            btnAdd.addActionListener(this);
            btnUpdate.addActionListener(this);
            btnClear.addActionListener(this);
            btnExit.addActionListener(this);
            btnBayrampasa.addActionListener(this);
            btnYenibosna.addActionListener(this);
            btnMerkez.addActionListener(this);
        }

        public void actionPerformed(ActionEvent event) {
            String Ip = "";
            String model = "";
            Object source = event.getSource();
            Ip = txtip.getText().trim();
            model = txtModel.getText().trim();
            txtip.setText(Ip);
            if (source.equals(btnAdd)) {
                //********************************
                try {
                    if (ipValidator(Ip)) {
                        printerEkle(String.valueOf(dateFormat.format(date)), null, Ip, model, false, false, comboBox.getSelectedItem().toString());
                        JOptionPane.showMessageDialog(null, "Cihazınız Sisteme Başarıyla Eklendi", "Cihaz Eklendi", JOptionPane.INFORMATION_MESSAGE);
                    } else
                        JOptionPane.showMessageDialog(null, "Lütfen IP Adresini Kontrol Ediniz", "IP Hatası", JOptionPane.ERROR_MESSAGE);
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                //txtip.setText("");


            }
            //****************************
            if (source.equals(btnUpdate)) {
                btnBayrampasa.setEnabled(true);
                btnMerkez.setEnabled(true);
                btnYenibosna.setEnabled(true);
                txtdate.setEnabled(true);
            }

            if (source.equals(btnBayrampasa)) {
                List<DBObject> listAll = null;
                if (txtdate != null && dateValidator(txtdate.getText())) {
                    try {
                        listAll = listele(lbltarih.getText(), txtdate.getText(), true, false, false);
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    if (listAll == null) {
                        JOptionPane.showMessageDialog(null, txtdate.getText() + " tarihinde yazıcılara ait kayıt bulunamadı", "Kayıt  Bulunamadı", JOptionPane.ERROR_MESSAGE);
                    } else {
                        Vector rowData = new Vector();
                        String[] columnNames = {"Model", "IP", "A4", "B5", "A5", "Folio", "Legal", "Mektup",
                                "Statement", "Diger (Cift)", "Diger (Tek)", "Kopyalanan", "Yazilan", "Fax", "Fark", "Toplam"};

                        Vector columnNamesV = new Vector(Arrays.asList(columnNames));

                        int i;
                        List<String> stringMap = new LinkedList<String>();
                        for (i = 0; i < listAll.size(); i = i + 2) {
                            if(listAll.get(i).get("list").toString().equals("[ ]")){

                            }else {
                            String ip = String.valueOf(listAll.get(i).get("ip"));
                            Vector colData = new Vector(Arrays.asList(listAll.get(i).get("model"), //listAll.get(i).get("vendor"),
                                    listAll.get(i).get("ip"),
                                    listAll.get(i).get("list").toString().replace("\"", "").replace("[", "").replace("]", "").replace(" ", "").split(",")[0],
                                    listAll.get(i).get("list").toString().replace("\"", "").replace("[", "").replace("]", "").replace(" ", "").split(",")[1],
                                    listAll.get(i).get("list").toString().replace("\"", "").replace("[", "").replace("]", "").replace(" ", "").split(",")[2],
                                    listAll.get(i).get("list").toString().replace("\"", "").replace("[", "").replace("]", "").replace(" ", "").split(",")[3],
                                    listAll.get(i).get("list").toString().replace("\"", "").replace("[", "").replace("]", "").replace(" ", "").split(",")[4],
                                    listAll.get(i).get("list").toString().replace("\"", "").replace("[", "").replace("]", "").replace(" ", "").split(",")[5],
                                    listAll.get(i).get("list").toString().replace("\"", "").replace("[", "").replace("]", "").replace(" ", "").split(",")[6],
                                    listAll.get(i).get("list").toString().replace("\"", "").replace("[", "").replace("]", "").replace(" ", "").split(",")[7],
                                    listAll.get(i).get("list").toString().replace("\"", "").replace("[", "").replace("]", "").replace(" ", "").split(",")[8],
                                    listAll.get(i).get("kopyalanan"), listAll.get(i).get("yazilan"), listAll.get(i).get("fax"), listAll.get(i + 1).get("fark"), listAll.get(i).get("toplam")));
                            if(!stringMap.contains(ip)){
                                rowData.add(colData);
                                stringMap.add(ip);
                            }
                            }
                        }
                        JFrame f = new JFrame();
                        table = new JTable(rowData, columnNamesV);
                        f.setSize(1300, 400);
                        f.setLayout(new GridLayout(2, 20));
                        f.add(new JScrollPane(table));
                        f.add(print1);
                        f.setVisible(true);
//                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
//                f.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Başlangıç Tarihini Lütfen Uygun Formatta Doldurunuz (yyyy/MM/dd)", "Tarih Hatası", JOptionPane.ERROR_MESSAGE);
                }
                print1.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                        Date date = new Date();
                        toExcel(table, new File("C:\\Users\\" + System.getProperty("user.name") + "\\Desktop\\Bayrampasa" + dateFormat.format(date).replace("/", "") + ".xls"));
                    }
                });
                btnBayrampasa.setEnabled(false);
                btnMerkez.setEnabled(false);
                btnYenibosna.setEnabled(false);
                txtdate.setEnabled(false);
            }

            if (source.equals(btnYenibosna)) {
                List<DBObject> listAll = null;
                List<DBObject> listAll1 = null;
                List<String> listAll2 = null;
                if (txtdate != null && dateValidator(txtdate.getText())) {
                    try {
                        listAll = listele(lbltarih.getText(), txtdate.getText(), false, true, false);
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    if (listAll == null) {
                        JOptionPane.showMessageDialog(null, txtdate.getText() + " tarihinde yazıcılara ait kayıt bulunamadı", "Kayıt  Bulunamadı", JOptionPane.ERROR_MESSAGE);
                    } else {
                        Vector rowData = new Vector();
                        Vector rowData1 = new Vector();
                        String[] columnNames = {"Model", "IP", "A4", "B5", "A5", "Folio", "Legal", "Mektup",
                                "Statement", "Diger (Cift)", "Diger (Tek)", "Kopyalanan", "Yazilan", "Fax", "Fark", "Toplam"};

                        Vector columnNamesV = new Vector(Arrays.asList(columnNames));


                        int i;
                        List<String> stringMap = new LinkedList<String>();
                        for (i = 0; i < listAll.size(); i = i+2) {
                            String ip = String.valueOf(listAll.get(i).get("ip"));
                            Vector colData = new Vector(Arrays.asList(listAll.get(i).get("model"), //listAll.get(i).get("vendor"),
                                    listAll.get(i).get("ip"),
                                    listAll.get(i).get("list").toString().replace("\"", "").replace("[", "").replace("]", "").replace(" ", "").split(",")[0],
                                    listAll.get(i).get("list").toString().replace("\"", "").replace("[", "").replace("]", "").replace(" ", "").split(",")[1],
                                    listAll.get(i).get("list").toString().replace("\"", "").replace("[", "").replace("]", "").replace(" ", "").split(",")[2],
                                    listAll.get(i).get("list").toString().replace("\"", "").replace("[", "").replace("]", "").replace(" ", "").split(",")[3],
                                    listAll.get(i).get("list").toString().replace("\"", "").replace("[", "").replace("]", "").replace(" ", "").split(",")[4],
                                    listAll.get(i).get("list").toString().replace("\"", "").replace("[", "").replace("]", "").replace(" ", "").split(",")[5],
                                    listAll.get(i).get("list").toString().replace("\"", "").replace("[", "").replace("]", "").replace(" ", "").split(",")[6],
                                    listAll.get(i).get("list").toString().replace("\"", "").replace("[", "").replace("]", "").replace(" ", "").split(",")[7],
                                    listAll.get(i).get("list").toString().replace("\"", "").replace("[", "").replace("]", "").replace(" ", "").split(",")[8],
                                    listAll.get(i).get("kopyalanan"), listAll.get(i).get("yazilan"), listAll.get(i).get("fax"), listAll.get(i + 1).get("fark"), listAll.get(i).get("toplam")));
                            if(!stringMap.contains(ip)){
                                rowData.add(colData);
                                stringMap.add(ip);
                            }
                        }
                        JFrame f = new JFrame();
                        table = new JTable(rowData, columnNamesV);
                        f.setSize(1300, 400);
                        f.setLayout(new GridLayout(2, 20));
                        f.add(new JScrollPane(table));
                        f.add(print2);
                        f.setVisible(true);
//                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
//                f.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Başlangıç Tarihini Lütfen Uygun Formatta Doldurunuz (yyyy/MM/dd)", "Tarih Hatası", JOptionPane.ERROR_MESSAGE);
                }
                print2.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                        Date date = new Date();
                        toExcel(table, new File("C:\\Users\\" + System.getProperty("user.name") + "\\Desktop\\Yenibosna" + dateFormat.format(date).replace("/", "") + ".xls"));
                    }
                });
                btnBayrampasa.setEnabled(false);
                btnMerkez.setEnabled(false);
                btnYenibosna.setEnabled(false);
                txtdate.setEnabled(false);
            }

            if (source.equals(btnMerkez)) {
                List<DBObject> listAll = null;
                List<DBObject> listAll1 = null;
                List<String> listAll2 = null;
                if (txtdate != null && dateValidator(txtdate.getText())) {
                    try {
                        listAll = listele(lbltarih.getText(), txtdate.getText(), false, false, true);
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    if (listAll == null) {
                        JOptionPane.showMessageDialog(null, txtdate.getText() + " tarihinde yazıcılara ait kayıt bulunamadı", "Kayıt  Bulunamadı", JOptionPane.ERROR_MESSAGE);
                    } else {
                        Vector rowData = new Vector();
                        String[] columnNames = {"Model", "IP", "A4", "B5", "A5", "Folio", "Legal", "Mektup",
                                "Statement", "Diger (Cift)", "Diger (Tek)", "Kopyalanan", "Yazilan", "Fax", "Fark", "Toplam"};

                        Vector columnNamesV = new Vector(Arrays.asList(columnNames));

                        int i;
                        List<String> stringMap = new LinkedList<String>();
                        for (i = 0; i < listAll.size(); i = i + 2) {
                            String ip = String.valueOf(listAll.get(i).get("ip"));
                            Vector colData = new Vector(Arrays.asList(listAll.get(i).get("model"), //listAll.get(i).get("vendor"),
                                            listAll.get(i).get("ip"),
                                            listAll.get(i).get("list").toString().replace("\"", "").replace("[", "").replace("]", "").replace(" ", "").split(",")[0],
                                            listAll.get(i).get("list").toString().replace("\"", "").replace("[", "").replace("]", "").replace(" ", "").split(",")[1],
                                            listAll.get(i).get("list").toString().replace("\"", "").replace("[", "").replace("]", "").replace(" ", "").split(",")[2],
                                            listAll.get(i).get("list").toString().replace("\"", "").replace("[", "").replace("]", "").replace(" ", "").split(",")[3],
                                            listAll.get(i).get("list").toString().replace("\"", "").replace("[", "").replace("]", "").replace(" ", "").split(",")[4],
                                            listAll.get(i).get("list").toString().replace("\"", "").replace("[", "").replace("]", "").replace(" ", "").split(",")[5],
                                            listAll.get(i).get("list").toString().replace("\"", "").replace("[", "").replace("]", "").replace(" ", "").split(",")[6],
                                            listAll.get(i).get("list").toString().replace("\"", "").replace("[", "").replace("]", "").replace(" ", "").split(",")[7],
                                            listAll.get(i).get("list").toString().replace("\"", "").replace("[", "").replace("]", "").replace(" ", "").split(",")[8],
                                            listAll.get(i).get("kopyalanan"), listAll.get(i).get("yazilan"), listAll.get(i).get("fax"), listAll.get(i + 1).get("fark"), listAll.get(i).get("toplam")));
                            if(!stringMap.contains(ip)){
                                rowData.add(colData);
                                stringMap.add(ip);
                            }
                        }
                        JFrame f = new JFrame();
                        table = new JTable(rowData, columnNamesV);
                        f.setSize(1300, 400);
                        f.setLayout(new GridLayout(2, 20));
                        f.add(new JScrollPane(table));
                        f.add(print);
                        f.setVisible(true);
//                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
//                f.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Başlangıç Tarihini Lütfen Uygun Formatta Doldurunuz (yyyy/MM/dd)", "Tarih Hatası", JOptionPane.ERROR_MESSAGE);
                }
                print.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                        Date date = new Date();
                        toExcel(table, new File("C:\\Users\\" + System.getProperty("user.name") + "\\Desktop\\Bomonti" + dateFormat.format(date).replace("/", "") + ".xls"));
                    }
                });
                btnBayrampasa.setEnabled(false);
                btnMerkez.setEnabled(false);
                btnYenibosna.setEnabled(false);
                txtdate.setEnabled(false);
            }
            //********************************************
            if (source.equals(btnClear)) {
//                List<DBObject> listAll = null;
//                try {
//                    listAll = listele();
//                } catch (IOException e) {
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                }
//
//                int i;
//                for (i = 0; i < listAll.size(); i++) {
//                            String ip = (String) listAll.get(i).get("ip");
//                    try {
//                        printerEkle(ip,false,true);
//                    } catch (IOException e) {
//                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                    }
//                }
                txtModel.setText("");
                txtip.setText("");
            }
            //********************************************
            if (source.equals(btnExit)) {
                System.exit(0);
            }
        }

        //********************************************
        public void toExcel(JTable table, File file) {
            try {
                TableModel model = table.getModel();
                FileWriter excel = new FileWriter(file);

                for (int i = 0; i < model.getColumnCount(); i++) {
                    excel.write(model.getColumnName(i) + "\t");
                }

                excel.write("\n");

                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        excel.write(model.getValueAt(i, j).toString() + "\t");
                    }
                    excel.write("\n");
                }

                excel.close();

            } catch (IOException e) {
                System.out.println(e);
            }
        }

    }

    private boolean ipValidator(String ip) {
        if (ip == null) {
            return false;
        } else {
            if (ip.contains(".")) {
                try {
                    ip = ip.replace(".", "");
                    int number = Integer.parseInt(ip);
                    return true;
                    // no exception thrown, that means its a valid Integer
                } catch (NumberFormatException e) {
                    return false;
                    // invalid Integer
                }
            } else {
                return false;
            }
        }

    }

    private boolean dateValidator(String txtdate) {
        if (txtdate == null) {
            return false;
        } else {
            if (txtdate.contains("/")) {
                if (txtdate.split("/").length == 3) {
                    if (txtdate.split("/")[0].length() == 4 && txtdate.split("/")[1].length() == 2 && txtdate.split("/")[2].length() == 2) {
                        return true;
                    } else
                        return false;
                } else
                    return false;
            } else
                return false;
        }
    }

    private static int printerEkle(String tarih, String tarih2, String ip, String model, boolean update, boolean kontrol, String selection) throws IOException {
        int a4 = 0, a5 = 0, b5 = 0, folio = 0, lgl = 0, lt = 0, statement = 0, diger_tek = 0, diger_cift = 0, kopyalayici = 0, yazici = 0, toplam = 0, fax = 0;       //lgl =mektup
        if (kontrol) {
//            String url = "http://" + ip;
//            URL obj = new URL(url);
//            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//
//            // optional default is GET
//            con.setRequestMethod("GET");
//            //add request header
//            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36");
//
//            int responseCode = con.getResponseCode();
//            System.out.println("\nSending 'GET' request to URL : " + url);
//            System.out.println("Response Code : " + responseCode);
//
//            BufferedReader in = new BufferedReader(
//                    new InputStreamReader(con.getInputStream()));
//            String inputLine;
//            StringBuffer response = new StringBuffer();
//
//            while ((inputLine = in.readLine()) != null) {
//                response.append(inputLine);
//                if(inputLine.contains("Durum")){
//
//                }
//            }
        } else {
            String vendor = "";
            boolean guncel = true;
            List<String> yazdirilanSayfaTipi = new LinkedList<String>();
            if (model.equals("ECOSYS M2530dn")) {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://" + ip + "/esu/set.cgi");
                httpPost.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                httpPost.addHeader("Accept-Encoding", "gzip, deflate");
                httpPost.addHeader("Accept-Language", "en-US,en;q=0.8");
                httpPost.addHeader("Connection", "keep-alive");
                httpPost.addHeader("Host", ip);
                httpPost.addHeader("Origin", "http://" + ip);
                httpPost.addHeader("Referer", "http://" + ip + "/DeepSleep.htm");
                httpPost.addHeader("Cookie", "rtl=0");
                httpPost.addHeader("Cache-Control", "max-age=0");
                httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36");

                List<NameValuePair> formData = new ArrayList<NameValuePair>();
                formData.add(new BasicNameValuePair("submit001", "Start"));
                formData.add(new BasicNameValuePair("okhtmfile", "DeepSleepApply.htm"));
                formData.add(new BasicNameValuePair("func", "wakeup"));

                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formData, "UTF-8");
                httpPost.setEntity(entity);
                int responseCode;
                String url = null;
                HttpURLConnection con = null;
                try {
                HttpResponse httpResponse = httpClient.execute(httpPost);
                System.out.print(httpResponse);

                try {
                    Thread.sleep(2000);                 //1000 milliseconds is one second.
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                url = "http://" + ip + "/dvcinfo/dvccounter/DvcInfo_Counter_PaperSize.htm";
                URL obj = new URL(url);
                 con = (HttpURLConnection) obj.openConnection();

                // optional default is GET
                con.setRequestMethod("GET");

                //add request header
                con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                con.setRequestProperty("Accept-Encoding", "gzip, deflate");
                con.setRequestProperty("Accept-Language", "en-US,en;q=0.8");
                con.setRequestProperty("Connection", "keep-alive");
                con.setRequestProperty("Host", ip);
                con.setRequestProperty("Referer", "http://" + ip + "/startwlm/Start_Wlm.htm");
                con.setRequestProperty("Cookie", "rtl=0");
                con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36");

                try{
                    con.setConnectTimeout(5000);
                    responseCode = con.getResponseCode();
                } catch (java.net.SocketTimeoutException e) {
                    return 404;
                } catch (java.io.IOException e) {
                    responseCode = 404;
                }
                }catch (ConnectException e){
                 return 404;
                }
                System.out.println("\nSending 'GET' request to URL : " + url);
                System.out.println("Response Code : " + responseCode);  //31
                if (responseCode == 404) {
                    guncel = false;
                    JOptionPane.showMessageDialog(null, "IP adresi : " + ip + " olan yazıcıya şuanda erişilemiyor.", "Yazıcı Erişim", JOptionPane.INFORMATION_MESSAGE);
                    return 404;
                } else {
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                        if (inputLine.contains("counterTotal[0]")) {
                            a4 = Integer.parseInt(inputLine.split("=")[1].replace(";", "").trim());
                            yazdirilanSayfaTipi.add(0, String.valueOf(a4));
                        } else if (inputLine.contains("counterTotal[1]")) {
                            b5 = Integer.parseInt(inputLine.split("=")[1].replace(";", "").trim());
                            yazdirilanSayfaTipi.add(1, String.valueOf(b5));
                        } else if (inputLine.contains("counterTotal[2]")) {
                            a5 = Integer.parseInt(inputLine.split("=")[1].replace(";", "").trim());
                            yazdirilanSayfaTipi.add(2, String.valueOf(a5));
                        } else if (inputLine.contains("counterTotal[3]")) {
                            folio = Integer.parseInt(inputLine.split("=")[1].replace(";", "").trim());
                            yazdirilanSayfaTipi.add(3, String.valueOf(folio));
                        } else if (inputLine.contains("counterTotal[4]")) {
                            lgl = Integer.parseInt(inputLine.split("=")[1].replace(";", "").trim());
                            yazdirilanSayfaTipi.add(4, String.valueOf(lgl));
                        } else if (inputLine.contains("counterTotal[5]")) {
                            lt = Integer.parseInt(inputLine.split("=")[1].replace(";", "").trim());
                            yazdirilanSayfaTipi.add(5, String.valueOf(lt));
                        } else if (inputLine.contains("counterTotal[6]")) {
                            statement = Integer.parseInt(inputLine.split("=")[1].replace(";", "").trim());
                            yazdirilanSayfaTipi.add(6, String.valueOf(statement));
                        } else if (inputLine.contains("counterTotal[7]")) {
                            diger_tek = Integer.parseInt(inputLine.split("=")[1].replace(";", "").trim());
                            yazdirilanSayfaTipi.add(7, String.valueOf(diger_tek));
                        } else if (inputLine.contains("counterTotal[8]")) {
                            diger_cift = Integer.parseInt(inputLine.split("=")[1].replace(";", "").trim());
                            yazdirilanSayfaTipi.add(8, String.valueOf(diger_cift));
                        }

                    }


                    String url1 = "http://" + ip + "/dvcinfo/dvccounter/DvcInfo_Counter_PrnCounter.htm";
                    URL obj1 = new URL(url1);
                    HttpURLConnection con1 = (HttpURLConnection) obj1.openConnection();

                    // optional default is GET
                    con1.setRequestMethod("GET");

                    //add request header
                    con1.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                    con1.setRequestProperty("Accept-Encoding", "gzip, deflate");
                    con1.setRequestProperty("Accept-Language", "en-US,en;q=0.8");
                    con1.setRequestProperty("Connection", "keep-alive");
                    con1.setRequestProperty("Host", ip);
                    con1.setRequestProperty("Referer", "http://" + ip + "/startwlm/Start_Wlm.htm");
                    con1.setRequestProperty("Cookie", "rtl=0");
                    con1.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36");

                    responseCode = con1.getResponseCode();
                    System.out.println("\nSending 'GET' request to URL : " + url1);
                    System.out.println("Response Code : " + responseCode);  //31

                    BufferedReader in1 = new BufferedReader(
                            new InputStreamReader(con1.getInputStream()));
                    String inputLine1;
                    StringBuffer response1 = new StringBuffer();

                    while ((inputLine1 = in1.readLine()) != null) {
                        response1.append(inputLine1);
                        if (inputLine1.contains("counterTotal[0]")) {
                            if (kopyalayici == 0)
                                kopyalayici = Integer.parseInt(inputLine1.split("=")[1].replace(";", "").replace(" ", ""));
                        } else if (inputLine1.contains("counterTotal[1]")) {
                            if (yazici == 0)
                                yazici = Integer.parseInt(inputLine1.split("=")[1].replace(";", "").trim().replace(" ", ""));
                        } else if (inputLine1.contains("counterTotal[2]") && !inputLine1.contains("cntrTotal") && !inputLine1.contains("counterTotal[i]")) {
                            if (fax == 0)
                                fax = Integer.parseInt(inputLine1.split("=")[1].replace(";", "").trim().replace(" ", ""));
                        }
                    }
                }
//                "d-Copia 403MFen\n" +
//                        "FS-3040MFP+\n" +
//                        "FS-3140MFP+\n" +
//                        "FS-1028MFP\n" +
//                        "FS-1130MFP"
            } else if (model.contains("d-COPIA3504MF") || model.equals("d-COPIA3503MF") || model.equals("d-Copia 403MFen") || model.equals("FS-3040MFP+") || model.equals("FS-1118MFP") || model.equals("FS-1128MFP") || model.equals("FS-3140MFP+") || model.equals("FS-1028MFP") || model.equals("FS-1130MFP")) {
                String url = "http://" + ip + "/start/StatCntFunc.htm";
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                // optional default is GET
                con.setRequestMethod("GET");

                //add request header
                con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36");
                int responseCode;
                try{
                con.setConnectTimeout(5000);
                   responseCode = con.getResponseCode();
                } catch (java.net.SocketTimeoutException e) {
                   return 404;
            } catch (java.io.IOException e) {
                    responseCode = 404;
            }

                System.out.println("\nSending 'GET' request to URL : " + url);
                System.out.println("Response Code : " + responseCode);  //31
                if (responseCode == 404) {
                    guncel = false;
                    JOptionPane.showMessageDialog(null, "IP adresi : " + ip + " olan yazıcıya şuanda erişilemiyor.", "Yazıcı Erişim", JOptionPane.INFORMATION_MESSAGE);
                    return 404;
                } else {
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                        if (inputLine.contains("var A4_total")) {
                            a4 = Integer.parseInt(inputLine.split("=")[1].split(" ")[1]);
                            yazdirilanSayfaTipi.add(0, String.valueOf(a4));
                        } else if (inputLine.contains("var A5_total")) {
                            a5 = Integer.parseInt(inputLine.split("=")[1].split(" ")[1]);
                            yazdirilanSayfaTipi.add(1, String.valueOf(a5));
                        } else if (inputLine.contains("var B5_total")) {
                            b5 = Integer.parseInt(inputLine.split("=")[1].split(" ")[1]);
                            yazdirilanSayfaTipi.add(2, String.valueOf(b5));
                        } else if (inputLine.contains("var Folio_total")) {
                            folio = Integer.parseInt(inputLine.split("=")[1].split(" ")[1]);
                            yazdirilanSayfaTipi.add(3, String.valueOf(folio));
                        } else if (inputLine.contains("var LGL_total")) {
                            lgl = Integer.parseInt(inputLine.split("=")[1].split(" ")[1]);
                            yazdirilanSayfaTipi.add(4, String.valueOf(lgl));
                        } else if (inputLine.contains("var LT_total")) {
                            lt = Integer.parseInt(inputLine.split("=")[1].split(" ")[1]);
                            yazdirilanSayfaTipi.add(5, String.valueOf(lt));
                        } else if (inputLine.contains("var Statement_total")) {
                            statement = Integer.parseInt(inputLine.split("=")[1].split(" ")[1]);
                            yazdirilanSayfaTipi.add(6, String.valueOf(statement));
                        } else if (inputLine.contains("var Other1_total")) {
                            diger_tek = Integer.parseInt(inputLine.split("=")[1].split(" ")[1]);
                            yazdirilanSayfaTipi.add(7, String.valueOf(diger_tek));
                        } else if (inputLine.contains("var Other2_total")) {
                            diger_cift = Integer.parseInt(inputLine.split("=")[1].split(" ")[1]);
                            yazdirilanSayfaTipi.add(8, String.valueOf(diger_cift));
                        } else if (inputLine.contains("sLabel[0] = GlobalNav[4];sData[0]")) {
                            if (!(inputLine.split("=")[2].replace(";", "").trim().replace("\"", "")).equals("")) {
                                kopyalayici = Integer.parseInt(inputLine.split("=")[2].replace(";", "").replace("\"", "").trim());
                            }
                        } else if (inputLine.contains("sLabel[1] = GlobalNav[2];sData[1]")) {
                            if (!(inputLine.split("=")[2].replace(";", "").trim().replace("\"", "")).equals("")) {
                                yazici = Integer.parseInt(inputLine.split("=")[2].replace(";", "").replace("\"", "").trim());
                            }
                        } else if (inputLine.contains("sLabel[2] = NavFax[13];sData[2]")) {
                            if (!(inputLine.split("=")[2].replace(";", "").trim().replace("\"", "")).equals("")) {
                                fax = Integer.parseInt(inputLine.split("=")[2].replace(";", "").replace("\"", "").trim());
                            }
                        }
                    }
                    in.close();
                    //http://10.34.56.41/start/StatCntMedia.htm
                    if (yazdirilanSayfaTipi.size() == 0) {
                        String url2 = "http://" + ip + "/start/StatCntMedia.htm";
                        URL obj2 = new URL(url2);
                        HttpURLConnection con2 = (HttpURLConnection) obj2.openConnection();

                        // optional default is GET
                        con2.setRequestMethod("GET");

                        //add request header
                        con2.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36");

                        int responseCode2 = con2.getResponseCode();
                        System.out.println("\nSending 'GET' request to URL : " + url2);
                        System.out.println("Response Code : " + responseCode2);  //31

                        BufferedReader in2 = new BufferedReader(
                                new InputStreamReader(con2.getInputStream()));
                        String inputLine2;
                        StringBuffer response2 = new StringBuffer();

                        while ((inputLine2 = in2.readLine()) != null) {
                            response.append(inputLine2);
                            if (inputLine2.contains("sLabel[0]")) {
                                a4 = Integer.parseInt(inputLine2.split("=")[2].replace("\"", "").replace(";", "").replace(" ", ""));
                                yazdirilanSayfaTipi.add(0, String.valueOf(a4));
                            } else if (inputLine2.contains("sLabel[1]")) {
                                a5 = Integer.parseInt(inputLine2.split("=")[2].replace("\"", "").replace(";", "").replace(" ", ""));
                                yazdirilanSayfaTipi.add(1, String.valueOf(a5));
                            } else if (inputLine2.contains("sLabel[2]")) {
                                b5 = Integer.parseInt(inputLine2.split("=")[2].replace("\"", "").replace(";", "").replace(" ", ""));
                                yazdirilanSayfaTipi.add(2, String.valueOf(b5));
                            } else if (inputLine2.contains("sLabel[3]")) {
                                folio = Integer.parseInt(inputLine2.split("=")[2].replace("\"", "").replace(";", "").replace(" ", ""));
                                yazdirilanSayfaTipi.add(3, String.valueOf(folio));
                            } else if (inputLine2.contains("sLabel[4]")) {
                                lgl = Integer.parseInt(inputLine2.split("=")[2].replace("\"", "").replace(";", "").replace(" ", ""));
                                yazdirilanSayfaTipi.add(4, String.valueOf(lgl));
                            } else if (inputLine2.contains("sLabel[5]")) {
                                lt = Integer.parseInt(inputLine2.split("=")[2].replace("\"", "").replace(";", "").replace(" ", ""));
                                yazdirilanSayfaTipi.add(5, String.valueOf(lt));
                            } else if (inputLine2.contains("sLabel[6]")) {
                                statement = Integer.parseInt(inputLine2.split("=")[2].replace("\"", "").replace(";", "").replace(" ", ""));
                                yazdirilanSayfaTipi.add(6, String.valueOf(statement));
                            } else if (inputLine2.contains("sLabel[7]")) {
                                diger_tek = Integer.parseInt(inputLine2.split("=")[2].replace("\"", "").replace(";", "").replace(" ", ""));
                                yazdirilanSayfaTipi.add(7, String.valueOf(diger_tek));
                            } else if (inputLine2.contains("sLabel[8]")) {
                                diger_cift = Integer.parseInt(inputLine2.split("=")[2].replace("\"", "").replace(";", "").replace(" ", ""));
                                yazdirilanSayfaTipi.add(8, String.valueOf(diger_cift));
                            }
                        }
                    }

                    String url5 = "http://" + ip + "/start/StatCntFunc.htm";
                    URL obj5 = new URL(url5);
                    HttpURLConnection con5 = (HttpURLConnection) obj5.openConnection();

                    // optional default is GET
                    con5.setRequestMethod("GET");

                    //add request header
                    con5.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36");

                    int responseCode2 = con5.getResponseCode();
                    System.out.println("\nSending 'GET' request to URL : " + url5);
                    System.out.println("Response Code : " + responseCode2);  //31

                    BufferedReader in5 = new BufferedReader(
                            new InputStreamReader(con5.getInputStream()));
                    String inputLine5;
                    StringBuffer response5 = new StringBuffer();

                    while ((inputLine5 = in5.readLine()) != null) {
                        response5.append(inputLine5);
                        if (inputLine5.contains("sLabel[0] = GlobalNav[2];sData[0]")) {
                            if (!(inputLine5.split("=")[2].replace(";", "").trim().replace("\"", "")).equals("")) {
                                kopyalayici = Integer.parseInt(inputLine5.split("=")[2].replace(";", "").replace("\"", "").trim());
                            }
                        } else if (inputLine5.contains("sLabel[1] = GlobalNav[4];sData[1]")) {
                            if (!(inputLine5.split("=")[2].replace(";", "").trim().replace("\"", "")).equals("")) {
                                yazici = Integer.parseInt(inputLine5.split("=")[2].replace(";", "").replace("\"", "").trim());
                            }
                        } else if (inputLine5.contains("sLabel[2] = GlobalNav[5];sData[2]")) {
                            if (!(inputLine5.split("=")[2].replace(";", "").trim().replace("\"", "")).equals("")) {
                                fax = Integer.parseInt(inputLine5.split("=")[2].replace(";", "").replace("\"", "").trim());
                            }
                        }
                    }

                    String url2 = "http://" + ip + "/start/About.htm";
                    URL obj2 = new URL(url2);
                    HttpURLConnection con2 = (HttpURLConnection) obj2.openConnection();

                    // optional default is GET
                    con2.setRequestMethod("GET");

                    //add request header
                    con2.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36");

                    responseCode2 = con2.getResponseCode();
                    System.out.println("\nSending 'GET' request to URL : " + url2);
                    System.out.println("Response Code : " + responseCode2);

                    BufferedReader in2 = new BufferedReader(
                            new InputStreamReader(con2.getInputStream()));
                    String inputLine2;
                    StringBuffer response2 = new StringBuffer();

                    while ((inputLine2 = in2.readLine()) != null) {
                        response2.append(inputLine2);
                        if (inputLine2.contains("GeneralStrings[38].toUpperCase()")) {
                            model = inputLine2.split("strong>")[2].replace("+", "").split("<")[0];
                        } else if (inputLine2.contains("GeneralStrings[37].toUpperCase()")) {
                            vendor = inputLine2.split("strong>")[2].split("<")[0];
                        }

                    }
                    in2.close();
                }
            }
            if(model.equals("FS-3040MFP"))
                model = "FS-3040MFP+";
            if(model.equals("FS-3140MFP"))
                model = "FS-3140MFP+";
            toplam = kopyalayici + yazici + fax;
            if (update & guncel) {
                Printer printer = new PrinterImpl(tarih, vendor, model, ip, yazdirilanSayfaTipi, kopyalayici, yazici, fax, toplam);
                printer.save(selection, tarih2);
            } else if (guncel) {
                Printer printer1 = new PrinterImpl(tarih, vendor, model, ip, yazdirilanSayfaTipi, kopyalayici, yazici, fax, toplam);
                printer1.save(selection, null);
            }
            //print result
            System.out.println(toplam);
        }
        return 0;
    }

    private static List<DBObject> listele(String tarih, String tarih2, boolean bayrampasa, boolean yenibosna, boolean merkez) throws IOException {
        try {

            // To connect to mongodb server
            MongoClient mongoClient = new MongoClient("10.34.24.34", 27017);

            // Now connect to your databases
            DB db = mongoClient.getDB("printers");
            System.out.println("Connect to database successfully");
//            boolean auth = db.authenticate(myUserName, myPassword);
//            System.out.println("Authentication: "+auth);
            //db.getCollection("printer").find();
            String selection = null;
            if (bayrampasa)
                selection = "bayrampasa";
            else if (yenibosna)
                selection = "yenibosna";
            else if (merkez)
                selection = "merkez";
            List<DBObject> dbObjects = db.getCollection(selection).find().toArray();
            List<DBObject> temp = new LinkedList<DBObject>();
            List<DBObject> asil = new LinkedList<DBObject>();
            List<DBObject> temp1 = new LinkedList<DBObject>();
            List<DBObject> ipnon = new LinkedList<DBObject>();
            int i, j, fark = 0;
            boolean diff = false;
            int detect = 0;
            for (i = 0; i < dbObjects.size(); i++) {
                String ip = (String) dbObjects.get(i).get("ip");
                String model = (String) dbObjects.get(i).get("model");
                detect = printerEkle(tarih, tarih2, ip, model, true, false, selection);
                if(detect == 404){
                  ipnon.add(new BasicDBObject("ip",ip));
                }
            }
            dbObjects = db.getCollection(selection).find().toArray();
            for (i = 0; i < dbObjects.size(); i++) {
                String ip = (String) dbObjects.get(i).get("ip");
                BasicDBObject dbObject = new BasicDBObject();
                dbObject.append("ip", ip);
                dbObject.append("tarih", tarih);
                BasicDBObject dbObject1 = new BasicDBObject();
                dbObject1.append("ip", ip);
                dbObject1.append("tarih", tarih2);

                if (db.getCollection(selection).find(dbObject).toArray().size() == 0 && !ipnon.contains(new BasicDBObject("ip",ip))) {
                    return null;
                } else if (db.getCollection(selection).find(dbObject1).toArray().size() == 0 && !ipnon.contains(new BasicDBObject("ip",ip))) {
                    return null;
                } else {
                    temp = db.getCollection(selection).find(dbObject).toArray();
                    temp1 = db.getCollection(selection).find(dbObject1).toArray();//temp-temp1
                    if(temp.size()!=0 && temp1.size()!=0) {
                    fark = Integer.parseInt(String.valueOf((temp.get(0)).get("toplam"))) - Integer.parseInt(String.valueOf((temp1.get(0)).get("toplam")));
                    temp.add(1, new BasicDBObject("fark", fark));
                    asil.addAll(temp);
                    }
                }
            }
            return asil;

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return null;
    }
}
