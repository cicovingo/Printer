import java.util.List;

import com.mongodb.MongoClient;


import com.mongodb.DB;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Created with IntelliJ IDEA.
 * User: naili.ilhami.bahadir
 * Date: 24.03.2016
 * Time: 11:51
 * To change this template use File | Settings | File Templates.
 */
public class PrinterImpl implements Printer {
    private final String tarih;
    private final String vendor;
    private final String model;
    private final String ip;
    private final List<String> yazdirilanSayfaTipi;
    private final int kopyalanan;
    private final int yazdirilan;
    private final int fax;
    private final int toplam;

    public PrinterImpl(String tarih, String vendor, String model, String ip, List<String> yazdirilanSayfaTipi, int kopyalanan, int yazdirilan, int fax, int toplam) {
        this.tarih = tarih;
        this.vendor = vendor;
        this.model = model;
        this.ip = ip;
        this.yazdirilanSayfaTipi = yazdirilanSayfaTipi;
        this.kopyalanan = kopyalanan;
        this.yazdirilan = yazdirilan;
        this.fax = fax;
        this.toplam = toplam;
    }

    @Override
    public String getPrinterModel() {
        return model;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getPrinterVendor() {
        return vendor;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getPrinterIp() {
        return ip;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> getListYazdirilanSayfaTipi() {
        return yazdirilanSayfaTipi;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getKopylananSayi() {
        return kopyalanan;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getYazdirilanSayi() {
        return yazdirilan;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getFax() {
        return fax;
    }

    @Override
    public int getToplam() {
        return toplam;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getTarih() {
        return tarih;
    }

    @Override
    public void setPrinterModel(String model) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setPrinterVendor(String vendor) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setPrinterIp(String ip) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setListYazdirilanSayfaTipi(List<String> listYazdirilanSayfaTipi) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void save(String selection, String tarih2) {
        try{

            // To connect to mongodb server
            MongoClient mongoClient = new MongoClient( "10.34.24.34" , 27017 );

            // Now connect to your databases
            DB db = mongoClient.getDB( "printers" );
            System.out.println("Connect to database successfully");
//            boolean auth = db.authenticate(myUserName, myPassword);
//            System.out.println("Authentication: "+auth);
            //db.getCollection("printer").find();
            BasicDBObject document = new BasicDBObject();
            document.put("tarih",getTarih());
            document.put("vendor",getPrinterVendor());
            document.put("model",getPrinterModel());
            document.put("ip",getPrinterIp());
            document.put("list",getListYazdirilanSayfaTipi());
            document.put("kopyalanan",getKopylananSayi());
            document.put("yazilan",getYazdirilanSayi());
            document.put("fax",getFax());
            document.put("toplam", getToplam());
            BasicDBObject dbObject = new BasicDBObject();




            if(tarih2 != null){
                BasicDBObject dbObject1 = new BasicDBObject();
                dbObject1.append("ip",getPrinterIp());
                dbObject1.append("tarih",getTarih());
                if(db.getCollection(selection).find(dbObject1).toArray().size() != 0){
//                    dbObject.append("ip",getPrinterIp());
                    if (selection.contains("merkez"))
                        db.getCollection("merkez").update(dbObject1, document);
                    else if (selection.contains("bayrampasa"))
                        db.getCollection("bayrampasa").update(dbObject1, document);
                    else if (selection.contains("yenibosna"))
                        db.getCollection("yenibosna").update(dbObject1, document);
                }else{
                    if (selection.contains("merkez"))
                        db.getCollection("merkez").insert(document);
                    else if (selection.contains("bayrampasa"))
                        db.getCollection("bayrampasa").insert(document);
                    else if (selection.contains("yenibosna"))
                        db.getCollection("yenibosna").insert(document);
                }
            }else{
                if (selection.contains("Bomonti"))
                    db.getCollection("merkez").insert(document);
                else if (selection.contains("Bayrampaşa"))
                    db.getCollection("bayrampasa").insert(document);
                else if (selection.contains("Yenibosna"))
                    db.getCollection("yenibosna").insert(document);
            }
        }catch(Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void update(String selection,String tarih2) {
        try{

            // To connect to mongodb server
            MongoClient mongoClient = new MongoClient( "10.34.24.34" , 27017 );

            // Now connect to your databases
            DB db = mongoClient.getDB( "printers" );
            System.out.println("Connect to database successfully");
//            boolean auth = db.authenticate(myUserName, myPassword);
//            System.out.println("Authentication: "+auth);
            //db.getCollection("printer").find();
            BasicDBObject document = new BasicDBObject();
            document.put("vendor",getPrinterVendor());
            document.put("model",getPrinterModel());
            document.put("ip",getPrinterIp());
            document.put("list",getListYazdirilanSayfaTipi());
            document.put("kopyalanan",getKopylananSayi());
            document.put("yazilan",getYazdirilanSayi());
            document.put("fax",getFax());
            document.put("toplam", getToplam());
            BasicDBObject dbObject = new BasicDBObject();
            dbObject.append("ip",getPrinterIp());



            if (selection.contains("Bomonti"))
                db.getCollection("merkez").update(dbObject, document);
            else if (selection.contains("Bayrampasa"))
                db.getCollection("bayrampasa").update(dbObject, document);
            else if (selection.contains("yenibosna"))
                db.getCollection("yenibosna").update(dbObject, document);
        }catch(Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        //To change body of implemented methods use File | Settings | File Templates.
    }


}
