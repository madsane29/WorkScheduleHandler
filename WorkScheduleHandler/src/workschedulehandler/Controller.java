package workschedulehandler;

//<editor-fold defaultstate="collapsed" desc="IMPORTS">
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
//</editor-fold>

public class Controller implements Initializable {

    DB db = new DB();
    private final ObservableList<WorkTime> workTimeData = FXCollections.observableArrayList();
    private final ObservableList<TableViewWriteOutExtended> TVWODataExtended = FXCollections.observableArrayList();

    private final ArrayList<WorkTime> workTimeDataArrayList = new ArrayList<>();
    private final ArrayList<Worker> workerDataArrayList = new ArrayList<>();

//<editor-fold defaultstate="collapsed" desc="FXML">
//<editor-fold defaultstate="collapsed" desc="DatePickers">
    @FXML
    DatePicker datePicker;
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="TextFields">
    @FXML
    TextField startHourInput;
    @FXML
    TextField endHourInput;
    @FXML
    TextField startMinuteInput;
    @FXML
    TextField endMinuteInput;
    @FXML
    TextField wageHourly;
    @FXML
    TextField searchTextField;
    @FXML
    TextField workerNameTextField;
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Buttons">
    @FXML
    Button deleteButton;
    @FXML
    Button addButton;
    @FXML
    Button searchButton;
    @FXML
    Button openAddNewWorkerButton;
    @FXML
    Button addNewWorkerButton;
    @FXML
    Button cancelNewWorkerButton;
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Tables">
    @FXML
    TableView table;
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Labels">
    @FXML
    Label totalLabel;
    @FXML
    Label bonusLabel;
    @FXML
    Label normalHoursLabel;
    @FXML
    Label extraHoursLabel;
    @FXML
    Label bigextraHoursLabel;
    @FXML
    Label normalAmount;
    @FXML
    Label extraAmount;
    @FXML
    Label bigextraAmount;
    @FXML
    Label totalam;
    @FXML
    Label net;

//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="ChoiceBoxes">
    @FXML
    ChoiceBox yearChoiceBox;
    @FXML
    ChoiceBox monthChoiceBox;
    @FXML
    ChoiceBox searchNameChoiceBox;
    @FXML
    ChoiceBox addNameChoiceBox;
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Panes">
    @FXML
    AnchorPane addWorkerAnchorPane;
    @FXML
    AnchorPane mainAnchorPane;
//</editor-fold>  
//<editor-fold defaultstate="collapsed" desc="ProgressIndicator">
    @FXML
    ProgressIndicator pi;
//</editor-fold>
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="CHOICE BOXES">
    public void fillChoiceBoxes() {
        setYearChoiceBox();
        setMonthChoiceBox();
        setNamesChoiceBoxes();
    }

    public void setMonthChoiceBox() {
        ObservableList<String> months = FXCollections.observableArrayList("All", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");
        monthChoiceBox.setItems(months);
        monthChoiceBox.setValue("All");
    }

    public void setYearChoiceBox() {
        int year = Year.now().getValue();
        ArrayList<String> list = new ArrayList<>();
        list.add("All");

        for (int i = 2017; i <= year; i++) {
            list.add("" + i);
        }
        ObservableList<String> years = FXCollections.observableList(list);

        yearChoiceBox.setItems(years);
        yearChoiceBox.setValue("All");
    }

    public void setNamesChoiceBoxes() {
        ArrayList<Worker> workers = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        
        workers = db.getWorkers();

        for (Worker i : workers) {
            String tmp = i.getName() + " (" + i.getID() + ")";
            names.add(tmp);
        }

        ArrayList<String> searchNames = new ArrayList<>(names);
        searchNames.add(0, "All");
        ObservableList<String> search = FXCollections.observableList(searchNames);
        searchNameChoiceBox.setItems(search);
        searchNameChoiceBox.getSelectionModel().selectFirst();

        ArrayList<String> addNames = new ArrayList<>(names);
        addNames.add(0, "");
        ObservableList<String> add = FXCollections.observableList(addNames);
        addNameChoiceBox.setItems(add);
        addNameChoiceBox.getSelectionModel().selectFirst();
    }
//</editor-fold>

    private void tableColumns() {
        TableColumn date = new TableColumn("Date");
        TableColumn startTime = new TableColumn("Start time");
        TableColumn endTime = new TableColumn("End time");
        TableColumn totalTime = new TableColumn("Worked");
        TableColumn worker = new TableColumn("Worker");

        double datePrefWidth = date.getPrefWidth();
        double startTimePrefWidth = startTime.getPrefWidth();
        double endTimePrefWidth = endTime.getPrefWidth();
        double totalTimePrefWidth = totalTime.getPrefWidth();
        double workerPrefWidth = worker.getPrefWidth();

        date.setMinWidth(datePrefWidth);
        startTime.setMinWidth(startTimePrefWidth);
        endTime.setMinWidth(endTimePrefWidth);
        totalTime.setMinWidth(totalTimePrefWidth);
        worker.setMinWidth(workerPrefWidth);

        date.setMaxWidth(100);
        startTime.setMaxWidth(100);
        endTime.setMaxWidth(100);
        totalTime.setMaxWidth(100);
        worker.setMaxWidth(100);

        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        startTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTime.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        totalTime.setCellValueFactory(new PropertyValueFactory<>("totalTime"));
        worker.setCellValueFactory(new PropertyValueFactory<>("workerName"));

        table.getColumns().addAll(date, worker, startTime, endTime, totalTime);
        table.setItems(TVWODataExtended);
        search();
    }

    private void WorkTimeToTVWOEFromDB() {
        TVWODataExtended.clear();
        workTimeData.clear();

        ArrayList<WorkTime> works = db.getAllWorkTimes();
        ArrayList<Worker> workers = db.getWorkers();

        for (WorkTime i : works) {
            workTimeData.add(i);
            for (Worker j : workers) {
                if (i.getWorkerID() == j.getID()) {
                    TableViewWriteOutExtended tvwoe = new TableViewWriteOutExtended(i, j.getName());
                    TVWODataExtended.add(tvwoe);
                }
            }
        }
    }

    private void WorkTimeToTVWOE() {
        TVWODataExtended.clear();
        ArrayList<Worker> workers = db.getWorkers();
        for (WorkTime i : workTimeData) {
            workTimeData.add(i);
            for (Worker j : workers) {
                if (i.getWorkerID() == j.getID()) {
                    TableViewWriteOutExtended tvwoe = new TableViewWriteOutExtended(i, j.getName());
                    TVWODataExtended.add(tvwoe);
                }
            }
        }

    }

    private void WorkTimesFromDB() {
        workTimeData.clear();
        ArrayList<WorkTime> works = db.getAllWorkTimes();
        workTimeData.addAll(works);
    }

    private void search() {
        try {
            int year = 0, month = -999, workerID = -999;

            String yearString = "" + yearChoiceBox.getSelectionModel().getSelectedItem();
            if (!yearString.equals("All")) {
                year = Integer.parseInt(yearString);
            }
            
            String idString = "" + searchNameChoiceBox.getSelectionModel().getSelectedItem();
            if (!idString.equals("All") && (idString.indexOf("(") > 0)) {
                workerID = Integer.parseInt(idString.substring(idString.indexOf("(") + 1, idString.indexOf(")")));
            } else {
                workerID = 0;
            }
            
            month = (Integer) monthChoiceBox.getSelectionModel().getSelectedIndex();

            TVWODataExtended.clear();

            if (year == 0 && month == 0 && workerID == 0) {
                WorkTimeToTVWOEFromDB();
            } else {
                WorkTimesFromDB();
                ArrayList<WorkTime> tmp = new ArrayList<>();
                for (WorkTime i : workTimeData) {
                    if ((i.getYear() == year || year == 0) && (i.getMonth() == month || month == 0) && (i.getWorkerID() == workerID || workerID == 0)) {
                        TVWODataExtended.add(new TableViewWriteOutExtended(i, db.getWorkerNameByID(i.getWorkerID())));
                        tmp.add(i);
                    }
                }
                workTimeData.clear();
                workTimeData.addAll(tmp);
            }
            calculateTotalWorkedHours();
        } catch (NumberFormatException ex) {
            System.out.println("Problem in \"FXMLDocumentController\" \"search\" method!");
            System.out.println(ex);
        }
    }

    private void calculateTotalWorkedHours() {

        int hour = 0, minute = 0;
        for (WorkTime i : workTimeData) {
            hour += i.getTotalHour();
            minute += i.getTotalMinute();
        }

        while (minute > 59) {
            hour++;
            minute -= 60;
        }

        totalLabel.setText(TableViewWriteOutExtended.formatter(hour, minute));
        bonuses();
    }

    private void bonuses() {
        double normal = 0, extra = 0, bigextra = 0;
        int fridayOrSaturday = 0;

        if (workTimeData.isEmpty()) {
            normalAmount.setText("");
            extraAmount.setText("");
            bigextraAmount.setText("");
            totalam.setText("");
            net.setText("");
            normalHoursLabel.setText("");
            extraHoursLabel.setText("");
            bigextraHoursLabel.setText("");
            bonusLabel.setText("0");

        } else {
            for (WorkTime i : workTimeData) {
                try {
                    int start = 0, end = 0;
                    Boolean gotBonus = false;
                    String dateString = String.format("%d-%d-%d", i.getYear(), i.getMonth(), i.getDay());
                    Date date = new SimpleDateFormat("yyyy-M-d").parse(dateString);
                    String dayOfWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date);

                    start = i.getStartHour() * 60 + i.getStartMinute();
                    end = i.getEndHour() * 60 + i.getEndMinute();
                    if (start >= 360 && start <= 1020 && end <= 1080 && end != 0) {
                        normal += end - start;
                    }
                    if (start >= 360 && start <= 1020 && (end >= 1140 || end <= 360 || end == 0)) {
                        end = 1080;
                        normal += end - start;
                    }
                    if (start >= 1080 && end >= 420 && end <= 900) {
                        start = 360;
                        normal += end - start;
                    }

                    start = i.getStartHour() * 60 + i.getStartMinute();
                    end = i.getEndHour() * 60 + i.getEndMinute();
                    if (start >= 360 && start <= 1260 && ((end >= 1140 && end <= 1440) || end <= 600)) {
                        if (start <= 1020) {
                            start = 1080;
                        }
                        if (end <= 600) {
                            end = 1320;
                        }
                        if (end >= 1380) {
                            end = 1320;
                        }
                        extra += end - start;
                    }

                    start = i.getStartHour() * 60 + i.getStartMinute();
                    end = i.getEndHour() * 60 + i.getEndMinute();

                    if (start >= 1320 && (end >= 1320 || end < 600)) {
                        if (start < 1320) {
                            start = 1320;
                        }
                        if (end < 360) {
                            end += 1440;
                        } else {
                            end = 360 + 1440;
                        }
                        if (end - start == 480) {
                            gotBonus = true;
                        }
                        bigextra += end - start;

                    }

                    if (gotBonus && (dayOfWeek.equals("Friday") || dayOfWeek.equals("Saturday"))) {
                        fridayOrSaturday++;
                    }
                } catch (ParseException ex) {
                    System.out.println("Problem in FXMLDocumentController/bonuses: " + ex);
                }
            }

            int bonus = fridayOrSaturday * 3000;
            normal /= 60;
            extra /= 60;
            bigextra /= 60;
            String l1 = "" + normal;
            String l2 = "" + extra;
            String l3 = "" + bigextra;
            String l4 = "" + bonus;

            normalHoursLabel.setText(l1);
            extraHoursLabel.setText(l2);
            bigextraHoursLabel.setText(l3);
            bonusLabel.setText(l4);

            double normalSalary, extraSalary, bigextraSalary;

            int salary = Integer.parseInt(wageHourly.getText());

            normalSalary = salary;
            extraSalary = salary * 1.3;
            bigextraSalary = salary * 1.4;

            double normalam = normal * normalSalary;
            double extraam = extra * extraSalary;
            double bigextraam = bigextra * bigextraSalary;
            double totalamm = normalam + extraam + bigextraam + bonus;
            double netam = totalamm * 0.85;

            if (netam > 58823) {
                netam -= 1000;
            } else {
                netam *= 0.983;
            }

            
            int normalamInt = keepTheChange((int) normalam);
            int extraamInt = keepTheChange((int) extraam);
            int bigextraamInt = keepTheChange((int) bigextraam);
            int totalammInt = keepTheChange((int) totalamm);
            int netamInt = keepTheChange((int) netam);

            String i1 = "" + normalamInt;
            String i2 = "" + extraamInt;
            String i3 = "" + bigextraamInt;
            String i4 = "" + totalammInt;
            String i5 = "" + netamInt;

            normalAmount.setText(i1);
            extraAmount.setText(i2);
            bigextraAmount.setText(i3);
            totalam.setText(i4);
            net.setText(i5);
        }
    }

    private void calculateNormal() {
        
    }
    
    private void calculateExtra() {
        
    }
    
    private void calculateBigExtra() {
        
    }
    
    private int keepTheChange(int amount) {
        HashSet<Integer> smallerSide = new HashSet<>();
        smallerSide.add(1);
        smallerSide.add(2);
        smallerSide.add(3);
        smallerSide.add(4);

        HashSet<Integer> biggerSide = new HashSet<>();
        biggerSide.add(6);
        biggerSide.add(7);
        biggerSide.add(8);
        biggerSide.add(9);

        if (smallerSide.contains(amount % 10)) {
            amount -= amount % 10;
        } else if (biggerSide.contains(amount % 10)) {
            amount += (10 - (amount % 10));
        }

        return amount;
    }

    private void refreshTableFromDB() {
        WorkTimeToTVWOEFromDB();
        table.setItems(TVWODataExtended);
        search();
    }

//<editor-fold defaultstate="collapsed" desc="Buttons">
    @FXML
    private void openAddNewWorkerButton(ActionEvent event) {
        mainAnchorPane.setOpacity(0.3);
        mainAnchorPane.setDisable(true);
        addWorkerAnchorPane.setVisible(true);
    }
    
    @FXML
    private void addNewWorkerButton(ActionEvent event) {
        String name = workerNameTextField.getText();
        workerNameTextField.setText("");
        db.addWorker(name);
        addWorkerAnchorPane.setVisible(false);
        mainAnchorPane.setOpacity(1);
        mainAnchorPane.setDisable(false);
        
        setNamesChoiceBoxes();
    }
    
    @FXML
    private void cancelNewWorkerButton(ActionEvent event) {
        addWorkerAnchorPane.setVisible(false);
        mainAnchorPane.setOpacity(1);
        mainAnchorPane.setDisable(false);
    }
    
    @FXML
    private void addButton(ActionEvent event) {
        try {
            Boolean t = false;
            int startHour = 0, endHour = 0, startMinute = 0, endMinute = 0;
            
            startHour = Integer.parseInt(startHourInput.getText());
            endHour = Integer.parseInt(endHourInput.getText());
            startMinute = Integer.parseInt(startMinuteInput.getText());
            endMinute = Integer.parseInt(endMinuteInput.getText());
            
            if (startHour < 0 || startHour > 23 || endHour < 0 || endHour > 23 || startMinute < 0 || startMinute > 59 || endMinute < 0 || endMinute > 59) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Problem!");
                alert.setHeaderText("Wrong input!");
                alert.setContentText("Bad interval!");
                alert.showAndWait();
            } else {
                
                HourMinute startTMP = new HourMinute(startHour, startMinute);
                HourMinute endTMP = new HourMinute(endHour, endMinute);
                HourMinute totalTMP = CalcTotal.calcTotal(startTMP, endTMP);
                LocalDate ld = datePicker.getValue();
                
                String choiceBoxName = (String) addNameChoiceBox.getSelectionModel().getSelectedItem();
                int workerid = Integer.parseInt(choiceBoxName.substring(choiceBoxName.indexOf("(") + 1, choiceBoxName.indexOf(")")));
                
                WorkTime workTime = new WorkTime(startTMP, endTMP, totalTMP, ld.getYear(), ld.getMonthValue(), ld.getDayOfMonth(), -1, workerid);
                db.addWork(workTime);
                
                search();
            }
            
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Problem!");
            alert.setHeaderText("Wrong input!");
            alert.setContentText("Please make sure you type only numbers in the fields!");
            alert.showAndWait();
        }
    }
    
    @FXML
    private void deleteButton(ActionEvent event) {
        if (table.getSelectionModel().getSelectedIndex() != -1) {
            TableViewWriteOutExtended selectedTVWO = (TableViewWriteOutExtended) table.getSelectionModel().getSelectedItem();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("You are about to delete a record!");
            alert.setContentText("Are you sure about deleting the selected record?");
            
            Optional<ButtonType> result = alert.showAndWait();
            
            if (result.get() == ButtonType.OK) {
                db.deleteFromTable("worktime", Integer.parseInt(selectedTVWO.getId()));
                search();
                calculateTotalWorkedHours();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("You did not choose any data!");
            alert.showAndWait();
        }
    }
//</editor-fold>
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        datePicker.setValue(LocalDate.now());        
        fillChoiceBoxes();
        tableColumns();
        
        yearChoiceBox.setOnAction((event) -> {
            search();
        });
        monthChoiceBox.setOnAction((event) -> {
            search();
        });
        searchNameChoiceBox.setOnAction((event) -> {
            search();
        });
//<editor-fold defaultstate="collapsed" desc="Stuff">
        wageHourly.textProperty().addListener((observable, oldValue, newValue) -> {
            String tmp = wageHourly.getText();
            if (tmp.equals("")) {
                wageHourly.setText("0");
            }
            calculateTotalWorkedHours();
        });

        InputStream input = getClass().getResourceAsStream("/images/plus.png");
        Image image = new Image(input);
        ImageView imageView = new ImageView(image);
        addButton.setGraphic(imageView);

        InputStream input2 = getClass().getResourceAsStream("/images/delete.png");
        Image image2 = new Image(input2);
        ImageView imageView2 = new ImageView(image2);
        deleteButton.setGraphic(imageView2);

        /*
DropShadow shadow = new DropShadow();
addButton.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
    @Override
    public void handle(MouseEvent e) {
        addButton.setEffect(shadow);
    }
});
addButton.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
    @Override
    public void handle(MouseEvent e) {
        addButton.setEffect(null);
    }
});

deleteButton.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
    @Override
    public void handle(MouseEvent e) {
        deleteButton.setEffect(shadow);
    }
});
deleteButton.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
    @Override
    public void handle(MouseEvent e) {
        deleteButton.setEffect(null);
    }
});*/
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="TEXT FIELDS ONLY NUMBERS (TEXT PROPERTIES)">
        startHourInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    startHourInput.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        startMinuteInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    startMinuteInput.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        endHourInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    endHourInput.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        endMinuteInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    endMinuteInput.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        wageHourly.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    wageHourly.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
//</editor-fold>

    }
}
