package workschedulehandler;

public class CalcTotal {
    
     public static HourMinute calcTotal(HourMinute start, HourMinute end) {
        int startHour = start.getHour();
        int endHour = end.getHour();
        int hourTotal = 0, minuteTotal = 0;

        if (endHour < startHour) {
            endHour += 24;
        }
        
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
