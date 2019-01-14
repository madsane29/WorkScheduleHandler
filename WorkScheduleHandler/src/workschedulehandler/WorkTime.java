package workschedulehandler;

public class WorkTime {
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private int totalHour;
    private int totalMinute;
    private int year;
    private int month;
    private int day;
    private int id;
    
    public WorkTime(HourMinute start, HourMinute end, HourMinute total) {
        this.startHour = start.getHour();
        this.startMinute = start.getMinute();
        this.endHour = end.getHour();
        this.endMinute = end.getMinute();
        this.totalHour = total.getHour();
        this.totalMinute = total.getMinute();
        this.year = -1000;
        this.month = -1000;
        this.day = -1000;
        this.id = -1000;
    }

    public WorkTime(HourMinute start, HourMinute end, HourMinute total, int year, int month, int day, int id) {
        this.startHour = start.getHour();
        this.startMinute = start.getMinute();
        this.endHour = end.getHour();
        this.endMinute = end.getMinute();        
        this.totalHour = total.getHour();
        this.totalMinute = total.getMinute();      
        this.year = year;
        this.month = month;
        this.day = day;
        this.id = id;
    }

    @Override
    public String toString() {
        return this.startHour+":"+this.startMinute+ "  " +this.endHour+":"+this.endMinute + "  " + this.totalHour+":"+this.totalMinute + " " + this.year + "." + this.month + "." + this.day; 
    }

    
    
    //<editor-fold defaultstate="collapsed" desc="GETTERS">
    public int getStartHour() {
        return startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public int getTotalHour() {
        return totalHour;
    }

    public int getTotalMinute() {
        return totalMinute;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getID() {
        return id;
    }
//</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="SETTERS">
    public void setStartHour(int x) {
        this.startHour = x;
    }
    
    public void setStartMinute(int x) {
        this.startMinute = x;
    }
    
    public void setEndHour(int x) {
        this.endHour = x;
    }
    
    public void setEndMinute(int x) {
        this.endMinute = x;
    }
    
    /*public void setTotalHour(int x) {
        this.totalHour = x;
    }
    
    public void setTotalMinute(int x) {
        this.totalMinute = x;
    }*/
    
    public void setYear(int year) {
        this.year = year;
    }
    public void setMonth(int month) {
        this.month = month;
    }
    public void setDay(int day) {
        this.day = day;
    }
    
    public void setid(int x) {
        this.id = x;
    }
//</editor-fold>
}
