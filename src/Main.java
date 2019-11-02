import com.alibaba.fastjson.JSON;
import core.Entity;
import core.Exporter;
import core.Http;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        //System.out.println(PinY.get("重庆市"));
        //System.out.println(PinY.get("长春市"));
        convert();
    }

    private static void convert() {
        final String key = "9a6d08a8f0e0ea8a9a2b78c8cf86895a";
        String json = Http.get("https://restapi.amap.com/v3/config/district?key=" + key + "&subdistrict=3");
        Entity countries = JSON.parseObject(json, Entity.class);
        if (countries != null) {
            List<Entity> country = countries.districts;
            if (country != null) {
                Exporter exporter = new SQLiteExporter();
                for (Entity china : country) {
                    if (china != null) {//中华人民共和国
                        List<Entity> provinces = china.districts;//各个省市
                        for (Entity province : provinces) {
                            exporter.add(province, china);
                        }
                    }
                }
                exporter.done();
            }
        }
    }
}
