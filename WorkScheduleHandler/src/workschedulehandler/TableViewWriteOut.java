package workschedulehandler;

import javafx.beans.property.SimpleStringProperty;

public class TableViewWriteOut {
    private SimpleStringProperty startTime;
    private SimpleStringProperty endTime;
    private SimpleStringProperty totalTime;
    private SimpleStringProperty date;
    private SimpleStringProperty id;
    
    public TableViewWriteOut(WorkTime worktime) {      
        this.date = new SimpleStringProperty(worktime.getYear() + "." + formatter(worktime.getMonth()) + "." + formatter(worktime.getDay()) + ".");
        this.id = new SimpleStringProperty("" + worktime.getID());
        this.startTime = new SimpleStringProperty(formatter(worktime.getStartHour(), worktime.getStartMinute()));
        this.endTime = new SimpleStringProperty(formatter(worktime.getEndHour(), worktime.getEndMinute()));
        this.totalTime = new SimpleStringProperty(formatter(worktime.getTotalHour(), worktime.getTotalMinute()));
    }

    // Formatting the time (9:3 -> 09:03)
    public static String formatter(int hour, int minute) {
        if (hour < 10 && minute < 10) {
            return "0" + hour + ":0" + minute;
        } else if (hour < 10 && minute > 9) {
            return "0" + hour + ":" + minute;
        } else if (hour > 9 && minute < 10) {
            return hour + ":0" + minute;
        } else if (hour > 9 && minute > 9) {
            return hour + ":" + minute;
        }
       // return hour + ":" + minute;
       return null;
    }
    
    // Formatting the month/day (2002.9.3 -> 2002.09.03)
    public static String formatter(int number) {
        if (number < 10) {
            return "0" + number;
        } else {
            return "" + number;
        }
    } 
        
    //<editor-fold defaultstate="collapsed" desc="GETTER/SETTER">
    public String getStartTime() {
        return startTime.getValue();
    }
    
    public void setStartTime(SimpleStringProperty startTime) {
        this.startTime = startTime;
    }
    
    public String getEndTime() {
        return endTime.getValue();
    }
    
    public void setEndTime(SimpleStringProperty endTime) {
        this.endTime = endTime;
    }
    
    public String getTotalTime() {
        return totalTime.getValue();
    }
    
    public void setTotalTime(SimpleStringProperty totalTime) {
        this.totalTime = totalTime;
    }
    
    public String getDate() {
        return date.getValue();
    }
    
    public void setDate(SimpleStringProperty date) {
        this.date = date;
    }
    
    public String getId() {
        return id.getValue();
    }
    
    public void setId(SimpleStringProperty id) {
        this.id = id;
    }
//</editor-fold>
    
    @Override
    public String toString() {
        StringBuilder tmp = new StringBuilder("");
        tmp.append("ID: ");
        tmp.append(id);
        tmp.append("; Start: ");
        tmp.append(startTime);
        tmp.append("; End: ");
        tmp.append(endTime);
        tmp.append("; Total: ");
        tmp.append(totalTime);
        tmp.append("; Date: ");
        tmp.append(date);
        String returnStr = tmp.toString();
        return returnStr;
    }
}
