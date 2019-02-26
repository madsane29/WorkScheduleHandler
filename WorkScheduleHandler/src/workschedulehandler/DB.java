package workschedulehandler;

import interfaces.DBInterface;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;



public class DB implements DBInterface{
    final String JDBC_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    final String URL = "jdbc:derby:WorkDB5;create=true";

    Connection conn = null;
    DatabaseMetaData dbmd = null;
    Statement statement = null;
    
    public DB() {
        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException ex) {
            System.out.println("SQLException in class \"DB\" (constructor):\n" + ex);
        }

        try {
            statement = conn.createStatement();
        } catch (Exception ex) {
            System.out.println("Exception in DB class --  statement = conn.createStatement(); " + ex);
        }
        try {
            dbmd = conn.getMetaData();
        } catch (Exception ex) {
            System.out.println("Exception in DB class -- dbmd = conn.getMetaData(); " + ex);
        }

        try {
            ResultSet rs = dbmd.getTables(null, "APP", "NAMES", null);
            if (!rs.next()) {
                String sql = "create table names (id INT not null primary key GENERATED ALWAYS AS IDENTITY(START WITH 1, INCREMENT BY 1), "
                        + "name VARCHAR(50))";
                statement.execute(sql);
            }
        } catch (SQLException ex) {
            System.out.println("SQLException in class \"DB\" (\"ResultSet/Creating table ('''''names''''') \" failed): " + ex);
        }

        try {
            ResultSet rs = dbmd.getTables(null, "APP", "WORKTIME", null);
            if (!rs.next()) {
                String sql = "create table worktime (id INT not null primary key GENERATED ALWAYS AS IDENTITY(START WITH 1, INCREMENT BY 1), "
                        + "starthour int, endhour int, startminute int, endminute int, y3ar int, month int, day int, workerid int, FOREIGN KEY(workerid) REFERENCES names(id))";
                statement.execute(sql);
            }
        } catch (SQLException ex) {
            System.out.println("SQLException in class \"DB\" (\"ResultSet/Creating table ('''''worktime''''') \" failed): " + ex);
        }


    }
    
    @Override
    public void addWork(WorkTime workTime) {
        try {
            String sql = "insert into worktime (starthour, endhour, startminute, endminute, y3ar, month, day, workerid) values(?,?,?,?,?,?,?,?)";
            PreparedStatement ppst = conn.prepareStatement(sql);
            ppst.setInt(1, workTime.getStartHour());
            ppst.setInt(2, workTime.getEndHour());
            ppst.setInt(3, workTime.getStartMinute());
            ppst.setInt(4, workTime.getEndMinute());
            ppst.setInt(5, workTime.getYear());
            ppst.setInt(6, workTime.getMonth());
            ppst.setInt(7, workTime.getDay());
            ppst.setInt(8, workTime.getWorkerID());
            ppst.execute();

        } catch (SQLException ex) {
            System.out.println("SQLException in class \"DB\" (\"addWork(WorkTime workTime)\"): " + ex);
        }
    }

    @Override
    public void addWorker(String name) {
        try {
            String sql = "insert into names (name) values (?)";
            PreparedStatement ppst = conn.prepareStatement(sql);
            ppst.setString(1, name);
            ppst.execute();
        } catch (Exception ex) {
            System.out.println("SQLException in class \"DB\" (\"addWorker(String name)\"): " + ex);
        }
    }

    @Override
    public ArrayList<WorkTime> getAllWorkTimes() {
        String sql = "select * from worktime";
        ArrayList<WorkTime> list = null;
        try {
            ResultSet rs = statement.executeQuery(sql);
            list = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");

                HourMinute start = new HourMinute(rs.getInt("starthour"), rs.getInt("startminute"));
                HourMinute end = new HourMinute(rs.getInt("endhour"), rs.getInt("endminute"));
                HourMinute total = CalcTotal.calcTotal(start, end);
                int year = rs.getInt("y3ar");
                int month = rs.getInt("month");
                int day = rs.getInt("day");
                int workerid = rs.getInt("workerid");

                WorkTime tmp = new WorkTime(start, end, total, year, month, day, id, workerid);
                list.add(tmp);
            }
        } catch (SQLException ex) {
            System.out.println("SQLException in class \"DB\" (\"getWorkTimes() failed\"): " + ex);
        }
        return list;
    }
    
    @Override
    public ArrayList<Worker> getWorkers() {
        ArrayList<Worker> workers = new ArrayList<>();
        String sql = "select * from names";

        try {
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                workers.add(new Worker(id, name));
            }
        } catch (Exception ex) {
            System.out.println("Exception in class \"DB\" (\" public ArrayList<Worker> getWorkers()\" failed): " + ex);
        }
        return workers;
    }

    @Override
    public WorkTime getWorkByID(int id) {
        WorkTime tmp = null;
        try {
            String sql = "select * from worktime where id = " + id;
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                HourMinute start = new HourMinute(rs.getInt("starthour"), rs.getInt("startminute"));
                HourMinute end = new HourMinute(rs.getInt("endhour"), rs.getInt("endminute"));
                HourMinute total = CalcTotal.calcTotal(start, end);
                int year = rs.getInt("y3ar");
                int month = rs.getInt("month");
                int day = rs.getInt("day");
                int workerid = rs.getInt("workerid");

                tmp = new WorkTime(start, end, total, year, month, day, id, workerid);
            }

        } catch (Exception ex) {
            System.out.println("SQLException in class \"DB\" (\"getWorkers() failed\"): " + ex);
        }

        return tmp;
    }

    @Override
    public String getWorkerNameByID(int workerid) {
        String sql = "select * from names";
        try {
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("id");
                if (workerid == id) return rs.getString("name");
            }
        } catch (SQLException ex) {
             System.out.println("SQLException in class \"DB\" (\"public String getWorkerNameByID(int workerid)\" failed): " + ex);
        }        
        return "No worker with this ID...";
    }

    @Override
    public void deleteFromTable(String tableName, int ID) {
         try {
            String sql = "DELETE FROM " + tableName + " WHERE ID = ?";
            PreparedStatement ppst = conn.prepareStatement(sql);
            ppst.setInt(1, ID);
            ppst.execute();
        } catch (SQLException ex) {
            System.out.println("SQLException in class \"DB\" (\"deleteFromTable(String tableName, int ID) failed\"): " + ex);            
        }
    }
    
    @Override
    public ArrayList<String> getAllWorkerNames() {
        ArrayList<String> names = new ArrayList<>();
        String sql = "select name from names";
        try {
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                names.add(rs.getString("name"));
            }
        } catch (SQLException ex) {
            System.out.println("SQLException in class \"DB\" (\"ArrayList<String> getAllNames() failed\"): " + ex);
        }
        return names;
    }

    @Override
    public ArrayList<Worker> getAllWorkers() {
        ArrayList<Worker> workers = new ArrayList<>();
        String sql = "select * from names";
        try {
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                String name = rs.getString("name");
                int id = rs.getInt("id");
                workers.add(new Worker(id, name));
            }
        } catch (SQLException ex) {
            System.out.println("SQLException in class \"DB\" (\"ArrayList<Worker> getAllWorkers() failed\"): " + ex);
        }
        
        return workers;
    }

    @Override
    public void deleteWorker(int id) {
        try {
            String sql = "DELETE FROM names WHERE ID = ?";
            PreparedStatement ppst = conn.prepareStatement(sql);
            ppst.setInt(1, id);
            ppst.execute();
        } catch (SQLException ex) {
            System.out.println("SQLException in class \"DB\" (\"deleteWorker(int id) failed\"): " + ex);   
        }
    }

    @Override
    public int getNumberOfWorkers() {
        String sql = "SELECT COUNT(*) FROM NAMES";
        try {
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) return rs.getInt(1);
        } catch (SQLException ex) {
            System.out.println("SQLException in class \"DB\" (\"public int getNumberOfWorkers() failed\"): " + ex);   
        }
        return 0;
    }
    
    @Override
    public ArrayList<Worker> getHalfOfTheWorkers(int limit, Boolean firstHalf) {
        String sql;
        ArrayList<Worker> workers = new ArrayList<>();
        if (firstHalf) {
            sql = "SELECT * FROM NAMES ORDER BY ID FETCH NEXT " + limit + " ROWS ONLY";
        } else {
            sql = "SELECT * FROM NAMES ORDER BY ID OFFSET " + limit + " ROWS";
        }
        try {
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                String name = rs.getString("name");
                int id = rs.getInt("id");
                workers.add(new Worker(id, name));
            }
        } catch (Exception ex) {

        }
        return workers;
    }
}
