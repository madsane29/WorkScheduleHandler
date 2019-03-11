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
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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
import javafx.util.Duration;
import org.json.JSONException;
//</editor-fold>

public class Controller implements Initializable {

    DB db = new DB();
    private final ArrayList<WorkTime> workTimeData = new ArrayList<>();
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
    /*@FXML
    Button cancelSecondaryPanes;*/
    @FXML
    Button openDeleteWorkerAnchorPane;
    @FXML
    Button deleteWorkerButton;
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
    @FXML
    Label dateLabel;

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
    @FXML
    ChoiceBox deleteNameChoiceBox;
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Panes">
    @FXML
    AnchorPane addWorkerAnchorPane;
    @FXML
    AnchorPane mainAnchorPane;
    @FXML
    AnchorPane deleteWorkerAnchorPane;
//</editor-fold>  
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="CHOICE BOXES">
    private void fillChoiceBoxes() {
        setYearChoiceBox();
        setMonthChoiceBox();
        setNamesChoiceBoxes();
    }
    private void setMonthChoiceBox() {
        ObservableList<String> months = FXCollections.observableArrayList("All", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");
        monthChoiceBox.setItems(months);
        monthChoiceBox.setValue("All");
    }
    private void setYearChoiceBox() {
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
    private void setNamesChoiceBoxes() {
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
        
        ArrayList<String> deleteNames = new ArrayList<>(names);
        deleteNames.add(0, "");
        ObservableList<String> delete = FXCollections.observableList(deleteNames);
        deleteNameChoiceBox.setItems(delete);
        deleteNameChoiceBox.getSelectionModel().selectFirst();
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


    private void search() {
        try {
            int year = 0, month = -999, workerID = -999;

            String yearString = "" + yearChoiceBox.getSelectionModel().getSelectedItem();
            if (!yearString.equals("All")) {
                year = Integer.parseInt(yearString);
            }
            
            String idString = "" + searchNameChoiceBox.getSelectionModel().getSelectedItem();
            if (!idString.equals("All")) {
                workerID = Integer.parseInt(idString.substring(idString.indexOf("(") + 1, idString.indexOf(")")));
            } else {
                workerID = 0;
            }
            
            month = (Integer) monthChoiceBox.getSelectionModel().getSelectedIndex();

            TVWODataExtended.clear();
            workTimeData.clear();
            workTimeData.addAll(db.getAllWorkTimes());
            ArrayList<WorkTime> tmp = new ArrayList<>();
            for (WorkTime i : workTimeData) {
                if ((i.getYear() == year || year == 0) && (i.getMonth() == month || month == 0) && (i.getWorkerID() == workerID || workerID == 0)) {
                    TVWODataExtended.add(new TableViewWriteOutExtended(i, db.getWorkerNameByID(i.getWorkerID())));
                    tmp.add(i);
                }
            }
            workTimeData.clear();
            workTimeData.addAll(tmp);

            calculateTotalWorkedHours();
        } catch (NumberFormatException ex) {
            System.out.println("Problem in \"FXMLDocumentController\" \"search\" method!");
            System.out.println(ex);
        }
    }

//<editor-fold defaultstate="collapsed" desc="Calculate Hours/Money">
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
        calcMoney();
        
    }
    private void setLabelsIfEmpty() {

        normalAmount.setText("0");
        extraAmount.setText("0");
        bigextraAmount.setText("0");
        totalam.setText("0");
        net.setText("0");
        normalHoursLabel.setText("0");
        extraHoursLabel.setText("0");
        bigextraHoursLabel.setText("0");
        bonusLabel.setText("0");

    }
    private void calcMoney() {
        if (workTimeData.isEmpty()) {
            setLabelsIfEmpty();
        } else {
            calculateNormal();
            calculateExtra();
            calculateBigExtra();
            calculateTotal();
        }
    }
    private void calculateNormal() {
        double hours = 0;
        for (WorkTime i : workTimeData) {
            int start = 0, end = 0;
            start = i.getStartHour() * 60 + i.getStartMinute();
            end = i.getEndHour() * 60 + i.getEndMinute();
            
            
            if (start >= 360 && start <= 1020 && end <= 1080 && end != 0) {
                hours += end - start;
            }
            if (start >= 360 && start <= 1020 && (end >= 1140 || end <= 360 || end == 0)) {
                end = 1080;
                hours += end - start;
            }
            if (start >= 1080 && end >= 420 && end <= 900) {
                start = 360;
                hours += end - start;
            }
        }
        
        hours /= 60;
        normalHoursLabel.setText("" + hours);
        int salary = Integer.parseInt(wageHourly.getText());
        double totalSalary = hours * salary;
        int salaryInt = keepTheChange((int) totalSalary);
        normalAmount.setText("" + salaryInt);
    }
    private void calculateExtra() {
        double hours = 0;
        for (WorkTime i : workTimeData) {
            int start = 0, end = 0;
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
                hours += end - start;
            }
        }
        
        hours /= 60;
        extraHoursLabel.setText("" + hours);
        int salary = Integer.parseInt(wageHourly.getText());
        double totalSalary = hours * salary * 1.3;
        int salaryInt = keepTheChange((int) totalSalary);
        extraAmount.setText("" + salaryInt);
    }
    private void calculateBigExtra() {
        double hours = 0;
        int countFridayOrSaturday = 0;
        Boolean fridayOrSaturday = false;
        Boolean workedEightHours = false;
        
        for (WorkTime i : workTimeData) {
            int start = 0, end = 0;
            start = i.getStartHour() * 60 + i.getStartMinute();
            end = i.getEndHour() * 60 + i.getEndMinute();
            
            if (start <= 1320 && end >= 360) workedEightHours = true;
            
            if ((start < 1320 || start >= 1320) && (end >= 1320 || end < 600)) {
                try {
                    if (end < 360) {
                        end += 1440;
                    } else {
                        end = 360 + 1440;
                    }
                    if (start < 1320) start = 1320;
                    hours += end - start;
                      
                    String dateString = String.format("%d-%d-%d", i.getYear(), i.getMonth(), i.getDay());
                    Date date = null;
                    date = new SimpleDateFormat("yyyy-M-d").parse(dateString);
                    
                    String dayOfWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date);
                    
                    if (dayOfWeek.equals("Friday") || dayOfWeek.equals("Saturday")) {
                        fridayOrSaturday = true;
                    }
                    
                    if (fridayOrSaturday && workedEightHours) countFridayOrSaturday++;
                } catch (ParseException ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        int bonus = countFridayOrSaturday * 3000;
        bonusLabel.setText(""+bonus);
        
        hours /= 60;
        bigextraHoursLabel.setText("" + hours);
        int salary = Integer.parseInt(wageHourly.getText());
        double totalSalary = hours * salary * 1.4;
        int salaryInt = keepTheChange((int) totalSalary);
        bigextraAmount.setText("" + salaryInt);
        
        
    }
    private void calculateTotal() {
        int normal = Integer.parseInt(normalAmount.getText());
        int extra = Integer.parseInt(extraAmount.getText());
        int bigextra = Integer.parseInt(bigextraAmount.getText());
        int bonus = Integer.parseInt(bonusLabel.getText());
        
        double totalamm = normal + extra + bigextra + bonus;
        double netam = totalamm * 0.85;
        
        if (netam > 58823) {
            netam -= 1000;
        } else {
            netam *= 0.983;
        }
        
        int totalammInt = keepTheChange((int) totalamm);
        int netamInt = keepTheChange((int) netam);
        
        totalam.setText("" + totalammInt);
        net.setText("" + netamInt);
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
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Buttons">

    private void transition(Node whatToTransition, double from, double to, int millisDuration) {
        FadeTransition tmp = new FadeTransition();
        tmp.setDuration(Duration.millis(millisDuration));
        tmp.setNode(whatToTransition);
        tmp.setFromValue(from);
        tmp.setToValue(to);
        tmp.play();
    }

    @FXML
    private void openButtons(ActionEvent event) {
        transition(mainAnchorPane, 1, 0.2, 250);
        mainAnchorPane.setDisable(true);
        final Node source = (Node) event.getSource();
        String id = source.getId();

        if (id.equals("openAddNewWorkerButton")) {
            addWorkerAnchorPane.setVisible(true);
            transition(addWorkerAnchorPane, 0.1, 1, 250);
        } else if (id.equals("openDeleteWorkerButton")) {
            deleteWorkerAnchorPane.setVisible(true);
            transition(deleteWorkerAnchorPane, 0.1, 1, 250);
        }

    }
    
    @FXML
    private void cancelSecondaryPanes(ActionEvent event) {
        if (addWorkerAnchorPane.isVisible()) {
            addWorkerAnchorPane.setVisible(false);
        } else if (deleteWorkerAnchorPane.isVisible()) {
            deleteWorkerAnchorPane.setVisible(false);
        }
        
        transition(mainAnchorPane, 0.3, 1, 250);
        mainAnchorPane.setDisable(false);
    }
    
    @FXML
    private void addNewWorkerButton(ActionEvent event) {
        String name = workerNameTextField.getText();
        if (!name.equals("")) {
            workerNameTextField.setText("");
            db.addWorker(name);

            setNamesChoiceBoxes();
            cancelSecondaryPanes(event);
        }
    }


    @FXML
    private void deleteWorker(ActionEvent event) {
        String choiceBoxName = (String) deleteNameChoiceBox.getSelectionModel().getSelectedItem();
        int workerid = Integer.parseInt(choiceBoxName.substring(choiceBoxName.indexOf("(") + 1, choiceBoxName.indexOf(")")));
        db.deleteWorker(workerid);
        
        setNamesChoiceBoxes();     
        cancelSecondaryPanes(event);
    }
    
    @FXML
    private void addButton(ActionEvent event) {
        if (!addNameChoiceBox.getValue().equals("")) {
            int startHour = 0, endHour = 0, startMinute = 0, endMinute = 0;

            startHour = Integer.parseInt(startHourInput.getText());
            endHour = Integer.parseInt(endHourInput.getText());
            startMinute = Integer.parseInt(startMinuteInput.getText());
            endMinute = Integer.parseInt(endMinuteInput.getText());

            HourMinute startTMP = new HourMinute(startHour, startMinute);
            HourMinute endTMP = new HourMinute(endHour, endMinute);
            HourMinute totalTMP = CalcTotal.calcTotal(startTMP, endTMP);
            LocalDate ld = datePicker.getValue();

            String choiceBoxName = (String) addNameChoiceBox.getSelectionModel().getSelectedItem();
            int workerid = Integer.parseInt(choiceBoxName.substring(choiceBoxName.indexOf("(") + 1, choiceBoxName.indexOf(")")));

            WorkTime workTime = new WorkTime(startTMP, endTMP, totalTMP, ld.getYear(), ld.getMonthValue(), ld.getDayOfMonth(), -1, workerid);
            db.addWork(workTime);

            search();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please select a worker!");
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
    
    private void setDateLabel() {
        try {
            AtomicLong unixtime = new AtomicLong(Unixtime.getTime());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
                try {
                    Date date = new Date(unixtime.getAndIncrement() * 1000L);
                    dateLabel.setText(sdf.format(date));
                } catch (Exception ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }), new KeyFrame(Duration.seconds(1)));
            clock.setCycleCount(Animation.INDEFINITE);
            clock.play();
        } catch (Exception ex) {
            System.out.println("Problem in Controller/setDateLabel method!");
            System.out.println(ex);
            
        }

    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        datePicker.setValue(LocalDate.now());
        fillChoiceBoxes();
        tableColumns();
        setDateLabel();
        
        yearChoiceBox.setOnAction((event) -> {
            search();
        });
        monthChoiceBox.setOnAction((event) -> {
            search();
        });
        searchNameChoiceBox.setOnAction((event) -> {
            search();
        });
        
        
//<editor-fold defaultstate="collapsed" desc="IMAGES">

        InputStream input = getClass().getResourceAsStream("/images/plus.png");
        Image image = new Image(input);
        ImageView imageView = new ImageView(image);
        addButton.setGraphic(imageView);

        InputStream input2 = getClass().getResourceAsStream("/images/delete.png");
        Image image2 = new Image(input2);
        ImageView imageView2 = new ImageView(image2);
        deleteButton.setGraphic(imageView2);

//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="TEXT FIELD LISTENERS">
        startHourInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    startHourInput.setText(newValue.replaceAll("[^\\d]", ""));
                } else if (newValue.equals("")) {
                    startHourInput.setText("0");
                } else if (oldValue.equals("0")) {
                    startHourInput.setText(newValue.substring(1));
                } else {
                    int time = Integer.parseInt(startHourInput.getText());
                    if (time > 23) {
                        startHourInput.setText(oldValue);
                    }
                }
            }
        });
        
        startMinuteInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    startMinuteInput.setText(newValue.replaceAll("[^\\d]", ""));
                } else if (newValue.equals("")) {
                    startMinuteInput.setText("0");
                } else if (oldValue.equals("0")) {
                    startMinuteInput.setText(newValue.substring(1));
                } else {
                    int time = Integer.parseInt(startMinuteInput.getText());
                    if (time > 59) {
                        startMinuteInput.setText(oldValue);
                    }
                }
            }
        });
        
        endHourInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    endHourInput.setText(newValue.replaceAll("[^\\d]", ""));
                } else if (newValue.equals("")) {
                    endHourInput.setText("0");
                } else if (oldValue.equals("0")) {
                    endHourInput.setText(newValue.substring(1));
                } else {
                    int time = Integer.parseInt(endHourInput.getText());
                    if (time > 23) {
                        endHourInput.setText(oldValue);
                    }
                }
            }
        });
        
        endMinuteInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    endMinuteInput.setText(newValue.replaceAll("[^\\d]", ""));
                } else if (newValue.equals("")) {
                    endMinuteInput.setText("0");
                } else if (oldValue.equals("0")) {
                    endMinuteInput.setText(newValue.substring(1));
                } else {
                    int time = Integer.parseInt(endMinuteInput.getText());
                    if (time > 59) {
                       endMinuteInput.setText(oldValue);
                    }
                }
            }
        });
        
        wageHourly.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    wageHourly.setText(newValue.replaceAll("[^\\d]", ""));
                } else if (newValue.equals("")) {
                    wageHourly.setText("0");
                }
                calculateTotalWorkedHours();
            }
        });
//</editor-fold>

    }
}
