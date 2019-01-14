
package workschedulehandler;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DB {

    final String JDBC_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    final String URL = "jdbc:derby:WorkDB;create=true";

    Connection conn = null;
    DatabaseMetaData dbmd = null;
    Statement statement = null;

    public DB() {
        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException ex) {
            System.out.println("SQLException in class \"DB\" (\"conn = DriverManager.getConnection(URL);\" failed): " + ex);
        }

        if (conn != null) {
            try {
                statement = conn.createStatement();
            } catch (SQLException ex) {
                System.out.println("SQLException in class \"DB\" (\"statement = conn.createStatement();\" failed): " + ex);
            }
        }

        try {
            dbmd = conn.getMetaData();
        } catch (SQLException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            ResultSet rs = dbmd.getTables(null, "APP", "WORKTIME", null);
            if (!rs.next()) {
                String sql = "create table worktime (id INT not null primary key GENERATED ALWAYS AS IDENTITY(START WITH 1, INCREMENT BY 1), starthour int, endhour int, startminute int, endminute int, y3ar int, month int, day int)";
                statement.execute(sql);
            }
        } catch (SQLException ex) {
            System.out.println("SQLException in class \"DB\" (\"ResultSet/Creating table\" failed): " + ex);
        }
        
    }
    
    public void addWork(WorkTime workTime) {
        try {
            String sql = "insert into worktime (starthour, endhour, startminute, endminute, y3ar, month, day) values(?,?,?,?,?,?,?)";
            PreparedStatement ppst = conn.prepareStatement(sql);
            ppst.setInt(1, workTime.getStartHour());
            ppst.setInt(2, workTime.getEndHour());
            ppst.setInt(3, workTime.getStartMinute());
            ppst.setInt(4, workTime.getEndMinute());
            ppst.setInt(5, workTime.getYear());
            ppst.setInt(6, workTime.getMonth());
            ppst.setInt(7, workTime.getDay());
            ppst.execute();
    
        } catch (SQLException ex) {
            System.out.println("SQLException in class \"DB\" (\"addWork(WorkTime workTime)\"): " + ex);
        }
    }

    public void deleteWork(int ID) {
        try {
            String sql = "delete from worktime where id = ?";
            PreparedStatement ppst = conn.prepareStatement(sql);
            ppst.setInt(1, ID);
            ppst.execute();
        } catch (SQLException ex) {
            System.out.println("SQLException in class \"DB\" (\"deleteAnimal(int ID) failed\"): " + ex);
        }
    }  

    public ArrayList<TableViewWriteOut> getAllTVWO() {
        
        String sql = "select * from worktime";
        ArrayList<TableViewWriteOut> list = null;
        try {
            ResultSet rs = statement.executeQuery(sql);
            list = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                
                HourMinute start = new HourMinute(rs.getInt("starthour"), rs.getInt("startminute"));
                HourMinute end = new HourMinute(rs.getInt("endhour"), rs.getInt("endminute"));
                HourMinute total = calc(start, end);
                
                int year = rs.getInt("y3ar");
                int month = rs.getInt("month");
                int day = rs.getInt("day");
                
                WorkTime tmp = new WorkTime(start, end, total, year, month, day, id);
                TableViewWriteOut tbwo = new TableViewWriteOut(tmp);
                list.add(tbwo);
            }
        } catch (SQLException ex) {
            System.out.println("SQLException in class \"DB\" (\"getAllWork() failed\"): " + ex);
        }
        
        return list;
    }
    
    public ArrayList<WorkTime> getAllWorkWorkTime() {
        String sql = "select * from worktime";
        ArrayList<WorkTime> list = null;
        try {
            ResultSet rs = statement.executeQuery(sql);
            list = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");

                HourMinute start = new HourMinute(rs.getInt("starthour"), rs.getInt("startminute"));
                HourMinute end = new HourMinute(rs.getInt("endhour"), rs.getInt("endminute"));
                HourMinute total = calc(start, end);
                int year = rs.getInt("y3ar");
                int month = rs.getInt("month");
                int day = rs.getInt("day");

                WorkTime tmp = new WorkTime(start, end, total, year, month, day, id);
                list.add(tmp);
            }
        } catch (SQLException ex) {
            System.out.println("SQLException in class \"DB\" (\"getAllWork() failed\"): " + ex);
        }
        return list;
    }

    public static HourMinute calc(HourMinute start, HourMinute end) {

        int startHour = start.getHour();
        int endHour = end.getHour();
        
        int hourTotal = 0, minuteTotal = 0;
        
        if (endHour < startHour) endHour += 24;
        hourTotal = endHour - startHour;
        minuteTotal = end.getMinute() - start.getMinute();

        if (minuteTotal < 0) {
            hourTotal--;
            minuteTotal += 60;
        }
        HourMinute total = new HourMinute(hourTotal, minuteTotal);

        return total;
    }
    
    
}