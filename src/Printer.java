import com.mongodb.DBObject;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: naili.ilhami.bahadir
 * Date: 24.03.2016
 * Time: 11:39
 * To change this template use File | Settings | File Templates.
 */
public interface Printer {
    public String getPrinterModel();
    public String getPrinterVendor();
    public String getPrinterIp();
    public List<String> getListYazdirilanSayfaTipi();
    public int getKopylananSayi();
    public int getYazdirilanSayi();
    public int getToplam();

    public void setPrinterModel(String model);
    public void setPrinterVendor(String vendor);
    public void setPrinterIp(String ip);
    public void setListYazdirilanSayfaTipi(List<String> listYazdirilanSayfaTipi);


    public void update(String selection,String tarih2);

    public void save(String selection,String tarih2);

    int getFax();

    String getTarih();
}
