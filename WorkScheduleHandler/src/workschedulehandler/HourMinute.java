
package workschedulehandler;

public class HourMinute {
    private int hour;
    private int minute;
    
    public HourMinute() {
        this.hour = 0;
        this.minute = 0;
    }

    public HourMinute(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }
    
    public String toString() {
        if (hour < 10 && minute < 10) {
            return "0" + hour + ":0" + minute;
        } else if (hour < 10 && minute > 9) {
            return "0" + hour + ":" + minute;
        } else if (hour > 9 && minute < 10) {
            return hour + ":0" + minute;
        } else if (hour > 9 && minute > 9) {
            return hour + ":" + minute;
        }

        return hour + ":" + minute;
    }
}
