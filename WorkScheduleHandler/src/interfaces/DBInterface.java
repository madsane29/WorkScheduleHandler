
package interfaces;

import java.util.ArrayList;
import workschedulehandler.WorkTime;
import workschedulehandler.Worker;

public interface DBInterface {
    public void addWork(WorkTime workTime);
    public void addWorker(String name);
    public ArrayList<WorkTime> getAllWorkTimes();
    public ArrayList<Worker> getWorkers();
    public WorkTime getWorkByID(int id);
    public String getWorkerNameByID(int workerid);
    public void deleteFromTable(String tableName, int ID);
    public ArrayList<String> getAllWorkerNames();
    public ArrayList<Worker> getAllWorkers();
    public void deleteWorker(int id);
    public int getNumberOfWorkers();
    public ArrayList<Worker> getHalfOfTheWorkers(int limit, Boolean asc);
}
