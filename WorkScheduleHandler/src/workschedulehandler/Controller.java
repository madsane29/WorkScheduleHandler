package workschedulehandler;

//<editor-fold defaultstate="collapsed" desc="IMPORTS">
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
//</editor-fold>

public class Controller implements Initializable {
    
    DB db = new DB();
    private final ObservableList<TableViewWriteOut> TVWOData = FXCollections.observableArrayList();
    private final ObservableList<WorkTime> workTimeData = FXCollections.observableArrayList();
    
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
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Buttons">
    @FXML
    Button deleteButton;
    @FXML
    Button addButton;
    /*@FXML
    Button search;*/
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
//</editor-fold>
//</editor-fold>

    
    public void fillChoiceBoxes() {
        ObservableList<String> years = FXCollections.observableArrayList("All", "2017", "2018", "2019", "2020", "2021");
        ObservableList<String> months = FXCollections.observableArrayList("All", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");

        yearChoiceBox.setItems(years);
        monthChoiceBox.setItems(months);

        yearChoiceBox.setValue("All");
        monthChoiceBox.setValue("All");
    }

    public void tableColumns() {
        TableColumn date = new TableColumn("Date");
        TableColumn startTime = new TableColumn("Start time");
        TableColumn endTime = new TableColumn("End time");
        TableColumn totalTime = new TableColumn("Worked");

        double datePrefWidth = date.getPrefWidth();
        double startTimePrefWidth = startTime.getPrefWidth();
        double endTimePrefWidth = endTime.getPrefWidth();
        double totalTimePrefWidth = totalTime.getPrefWidth();

        date.setMinWidth(datePrefWidth);
        startTime.setMinWidth(startTimePrefWidth);
        endTime.setMinWidth(endTimePrefWidth);
        totalTime.setMinWidth(totalTimePrefWidth);

        date.setMaxWidth(100);
        startTime.setMaxWidth(100);
        endTime.setMaxWidth(100);
        totalTime.setMaxWidth(100);

        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        startTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTime.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        totalTime.setCellValueFactory(new PropertyValueFactory<>("totalTime"));

        table.getColumns().addAll(date, startTime, endTime, totalTime);
        refreshTableFromDB();
        //searchButton();
    }

    public void splitTVWODataToWorkTimeData() {
        workTimeData.clear();

        for (TableViewWriteOut tmp : TVWOData) {
            int sHour, sMinute, eHour, eMinute, tHour, tMinute;

            String str = "";
            String[] array = new String[3];

            str = tmp.getStartTime();
            array = str.split(":");
            sHour = Integer.parseInt(array[0]);
            sMinute = Integer.parseInt(array[1]);

            str = tmp.getEndTime();
            array = str.split(":");
            eHour = Integer.parseInt(array[0]);
            eMinute = Integer.parseInt(array[1]);

            str = tmp.getTotalTime();
            array = str.split(":");
            tHour = Integer.parseInt(array[0]);
            tMinute = Integer.parseInt(array[1]);

            str = tmp.getDate();
            int year = Integer.parseInt(str.substring(0, 4));
            int month = Integer.parseInt(str.substring(5, 7));
            int day = Integer.parseInt(str.substring(8, 10));

            int id = Integer.parseInt(tmp.getId());

            WorkTime wt = new WorkTime(new HourMinute(sHour, sMinute), new HourMinute(eHour, eMinute), new HourMinute(tHour, tMinute), year, month, day, id);
            workTimeData.add(wt);
        }
    }

    
    // Summs the worked hours shown in table
    public void calculateTotalWorkedHours() {
        workTimeData.clear();
        splitTVWODataToWorkTimeData();

        int hour = 0, minute = 0;
        for (WorkTime tmp : workTimeData) {
            hour += tmp.getTotalHour();
            minute += tmp.getTotalMinute();
        }

        while (minute > 59) {
            hour++;
            minute -= 60;
        }

        bonuses();
        totalLabel.setText(TableViewWriteOut.formatter(hour, minute));
    }

    public void bonuses() {
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

            normalam = BigDecimal.valueOf(normalam).setScale(0, RoundingMode.HALF_UP).doubleValue();
            extraam = BigDecimal.valueOf(extraam).setScale(0, RoundingMode.HALF_UP).doubleValue();
            bigextraam = BigDecimal.valueOf(bigextraam).setScale(0, RoundingMode.HALF_UP).doubleValue();
            totalamm = BigDecimal.valueOf(totalamm).setScale(0, RoundingMode.HALF_UP).doubleValue();
            netam = BigDecimal.valueOf(netam).setScale(0, RoundingMode.HALF_UP).doubleValue();

            String i1 = "" + normalam;
            String i2 = "" + extraam;
            String i3 = "" + bigextraam;
            String i4 = "" + totalamm;
            String i5 = "" + netam;

            normalAmount.setText(i1);
            extraAmount.setText(i2);
            bigextraAmount.setText(i3);
            totalam.setText(i4);
            net.setText(i5);
        }
    }
    
    public void refreshTableFromDB() {
        TVWOData.clear();
        TVWOData.addAll(db.getAllTVWO());
        splitTVWODataToWorkTimeData();
        table.setItems(TVWOData);
        search();
        calculateTotalWorkedHours();
    }
    
    
    private void search() {
        Boolean t = false;

        int year, month;
        year = (Integer) yearChoiceBox.getSelectionModel().getSelectedIndex();
        month = (Integer) monthChoiceBox.getSelectionModel().getSelectedIndex();

        if (year != 0) {
            year += 2016;
        }

        try {
            TVWOData.clear();
            if (year == 0 && month == 0) {
                TVWOData.addAll(db.getAllTVWO());
            } else {
                ObservableList<WorkTime> tempWorkTimeData = FXCollections.observableArrayList();
                tempWorkTimeData.addAll(db.getAllWorkWorkTime());
                for (WorkTime tmp : tempWorkTimeData) {
                    if (tmp.getYear() == year && tmp.getMonth() == month) {
                        TVWOData.add(new TableViewWriteOut(tmp));
                    } else if (tmp.getYear() == year && month == 0) {
                        TVWOData.add(new TableViewWriteOut(tmp));
                    } else if (year == 0 && tmp.getMonth() == month) {
                        TVWOData.add(new TableViewWriteOut(tmp));
                    }
                }
            }

            calculateTotalWorkedHours();
        } catch (Exception ex) {
            System.out.println("Problem in \"FXMLDocumentController\" \"searchButton\" method!");
            System.out.println(ex);
        }
    }
    
    @FXML
    public void addButton(ActionEvent event) {
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
                HourMinute totalTMP = DB.calc(startTMP, endTMP);
                LocalDate ld = datePicker.getValue();

                WorkTime workTMP = new WorkTime(startTMP, endTMP, totalTMP, ld.getYear(), ld.getMonthValue(), ld.getDayOfMonth(), -1);

                db.addWork(workTMP);
                
                refreshTableFromDB();
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
        TableViewWriteOut selectedTVWO = (TableViewWriteOut) table.getSelectionModel().getSelectedItem();
         Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("You are about to delete a record!");
            alert.setContentText("Are you sure about deleting the selected record?");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == ButtonType.OK) {
        db.deleteWork(Integer.parseInt(selectedTVWO.getId()));
        TVWOData.remove(selectedTVWO);
        table.setItems(TVWOData);
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        datePicker.setValue(LocalDate.now());
        tableColumns();
        fillChoiceBoxes();
        refreshTableFromDB();
        
        yearChoiceBox.setOnAction((event) -> {
            search();
        });
        monthChoiceBox.setOnAction((event) -> {
            search();
        });
        
        wageHourly.textProperty().addListener((observable, oldValue, newValue) -> {
            calculateTotalWorkedHours();
        });

    }

}
