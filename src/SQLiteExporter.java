import core.Entity;
import core.Exporter;
import core.PinY;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

public class SQLiteExporter implements Exporter {

    private static final String OUTPUT = "res/China.db", TABLE_ALL = "China", TABLE_P = "province", TABLE_C = "city", TABLE_D = "district";
    private static Connection sConnection = null;

    public SQLiteExporter() {
        if (sConnection == null) {
            synchronized (SQLiteExporter.class) {
                if (sConnection == null) {
                    try {
                        Class.forName("org.sqlite.JDBC");
                        // create a database connection
                        sConnection = DriverManager.getConnection("jdbc:sqlite:" + OUTPUT);
                        Statement statement = sConnection.createStatement();

                        for (String tab : Arrays.asList(
                                TABLE_ALL
                                , TABLE_P
                                , TABLE_C
                                , TABLE_D
                        )) {
                            createTable(statement, tab);
                        }

                        statement.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void add(Entity entity, Entity parent) {
        if (entity != null) {
            convertProvince(entity, parent);
        }
    }

    @Override
    public void done() {
        try {
            sConnection.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sConnection = null;
        }
    }

    private void createTable(Statement statement, String tableName) throws SQLException {
        final String sql = "create table if not exists " + tableName +
                " (name varchar" +//行政区名称
                ", cityCode varchar" +//城市编码
                ", adCode varchar" +//区域编码  街道没有独有的adcode，均继承父类（区县）的adcode
                ", center varchar" +//区域中心点
                ", level varchar" +//行政区划级别
                //  country:国家
                //  province:省份（直辖市会在province和city显示）
                //  city:市（直辖市会在province和city显示）
                //  district:区县
                //  street:街道
                ", pinyin varchar" +//全拼
                ", pinyinShort varchar" +//缩写
                ", parentCityCode varchar" +//上一级的 城市编码
                ", parentName varchar" +//上一级的 行政区名称
                ")";
        System.out.println(sql);
        statement.execute(sql);
    }

    private void execute(String table, Entity values, Entity parent) {
        try {
            Statement statement = sConnection.createStatement();
            String[] pinyins = PinY.get(values.name);
            final String sql = "insert into " + table + " values ('" +
                    values.name
                    + "', '" + values.citycode
                    + "', '" + values.adcode
                    + "', '" + values.center
                    + "', '" + values.level
                    + "', '" + pinyins[0]
                    + "', '" + pinyins[1]
                    + "', '" + (parent == null ? " " : parent.citycode)
                    + "', '" + (parent == null ? " " : parent.name)
                    + "')";
            System.out.println(sql);
            statement.execute(sql);
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void convertProvince(Entity province, Entity country) {
        if (province == null) return;
        //if ("province".equals(province.level)) {
        execute(TABLE_ALL, province, country);
        execute(TABLE_P, province, country);
        if (province.districts != null) {
            for (Entity city : province.districts) {
                convertCity(city, province);
            }
        }
        /*} else {
            System.err.println("data error: province=" + province + " country=" + country);
        }*/
    }

    private void convertCity(Entity city, Entity province) {
        if (city == null || province == null) return;
        //if ("city".equals(city.level)) {
        execute(TABLE_ALL, city, province);
        execute(TABLE_C, city, province);
        if (city.districts != null) {
            for (Entity district : city.districts) {
                convertDistrict(district, city);
            }
        }
        /*} else {
            System.err.println("data error: city=" + city + " province=" + province);
        }*/
    }

    private void convertDistrict(Entity district, Entity city) {
        if (district == null || city == null) return;
        //if ("district".equals(district.level)) {
        execute(TABLE_ALL, district, city);
        execute(TABLE_D, district, city);
        /*} else {
            System.err.println("data error: district=" + district + " city=" + city);
        }*/
    }
}
