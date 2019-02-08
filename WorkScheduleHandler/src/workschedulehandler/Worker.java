package workschedulehandler;

public class Worker {
    
    private int id;
    private String name;
    
    public Worker(int id, String name) {
        this.id = id;
        this.name = name;
    }

//<editor-fold defaultstate="collapsed" desc="GETTER/SETTER">
    public int getID() {
        return id;
    }
    
    public void setID(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
//</editor-fold>
}
