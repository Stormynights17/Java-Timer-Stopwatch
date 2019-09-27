/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ojl8q7timer;

import com.sun.prism.paint.Color;
import java.util.Optional;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author OJlav
 */
public class Ojl8q7Timer extends Application {
    
    public Timeline timeline;
    public StackPane root;
    
    public String input;
    
    public ImageView dialImageView;
    public ImageView handImageView;
    
    private KeyFrame keyFrame1;
    
    private double secondsElapsed = 0.0;
    private double tickTimeInSeconds = 0.01;
    public double angleDeltaPerSeconds = 6.0;
    
    private HBox buttonBox, recBox, lapBox, numLapsBox;
    private VBox labelBox;
    private Button recordBtn, startBtn;
    private Label record, recTime, lap, lapTime, numLapsLabel, numLaps;
    
    private HBox digitalBox;
    private Text digitalClock;
    
    public int hourInput, minInput, secInput;
    public int Ssec, Tsec;
    
    private int count=0, store=0;
    
    @Override
    public void start(Stage primaryStage) {
        
        primaryStage.setTitle("StopWatch");
        
        //Dialog
        TextInputDialog inputTime = new TextInputDialog("00:00:00");
        inputTime.setTitle("Timer");
        inputTime.setHeaderText("Enter your time:");
        Optional<String> result = inputTime.showAndWait();
        if (result.isPresent()) {
            input = result.get();
            hourInput = Integer.parseInt(input.substring(0,2));
            minInput = Integer.parseInt(input.substring(3,5));
            secInput = Integer.parseInt(input.substring(6,8));
        }
        
        root = new StackPane();
        timeline = new Timeline();
        
        //Images
        dialImageView = new ImageView();
        Image dialImage = new Image(getClass().getResourceAsStream("clockface.png"));
        dialImageView.setImage(dialImage);
        
        handImageView = new ImageView();
        Image handImage = new Image(getClass().getResourceAsStream("hand.png"));
        handImageView.setImage(handImage);
        root.getChildren().addAll(dialImageView, handImageView);
        
        //Buttons
        buttonBox = new HBox();
        recordBtn = new Button("Record");
        recordBtn.setStyle("-fx-background-color: #add8e6; ");
        startBtn = new Button("Start");
        startBtn.setStyle("-fx-background-color: #008000; ");
        buttonBox.getChildren().addAll(recordBtn, startBtn);
        
        //Start button
        startBtn.setOnAction((ActionEvent event) -> {
            if (!isRunning()) {
                startBtn.setText("Stop");
                startBtn.setStyle("-fx-background-color: #FF6347; ");
                timeline.play();
                recordBtn.setText("Record");
            }
            else {
                startBtn.setText("Start");
                startBtn.setStyle("-fx-background-color: #008000; ");
                timeline.pause();
                recordBtn.setText("Reset");
            }
        });
        
        //Labels
        recBox = new HBox();
        record = new Label("Rec  ");
        recTime = new Label("00:00:00");
        recBox.getChildren().addAll(record, recTime);
        recBox.setAlignment(Pos.BOTTOM_CENTER);
        
        lapBox = new HBox();
        lap = new Label("Lap  ");
        lapTime = new Label("00:00:00");
        lapBox.getChildren().addAll(lap, lapTime);
        lapBox.setAlignment(Pos.BOTTOM_CENTER);
        
        numLapsBox = new HBox();
        numLapsLabel = new Label("# of Laps ");
        numLaps = new Label("0");
        numLapsBox.getChildren().addAll(numLapsLabel, numLaps);
        numLapsBox.setAlignment(Pos.BOTTOM_CENTER);
        
        //Digital clock Init
        digitalBox = new HBox();
        digitalBox.setAlignment(Pos.BOTTOM_CENTER);
        digitalClock = new Text("00:00:00");
        digitalBox.getChildren().addAll(digitalClock);
        
        //Formatting
        labelBox = new VBox();
        labelBox.getChildren().addAll(digitalBox, recBox, lapBox, numLapsBox, buttonBox);
        
        root.getChildren().add(labelBox);
        buttonBox.setAlignment(Pos.BOTTOM_CENTER);
        labelBox.setAlignment(Pos.BOTTOM_CENTER);
        labelBox.toFront();
        
        // Record/Reset Button
        recordBtn.setOnAction((ActionEvent event) -> {
            if (isRunning()) {
                //If is running, record the time elapsed
                recTime.setText(getTimeElapsed());
                if (count == 0) {
                    //First time button has been clicked
                    count++;
                    numLaps.setText(Integer.toString(count));
                    store = Ssec;
                }
                else if (count >= 1) {
                    //Second time button has been clicked
                    count++;
                    lapTime.setText(getLapTime());
                    numLaps.setText(Integer.toString(count));
                    
                    store = Ssec;
                }
            }
            else {
                //Resets the timer back to full time
                secondsElapsed = 0.0;
                count = 0;
                recTime.setText("--:--:--");
                lapTime.setText("--:--:--");
                numLaps.setText("0");
            }    
        });
        
        
        Alert timeUp = new Alert(AlertType.INFORMATION);
        timeUp.setTitle("Alert");
        timeUp.setHeaderText("Message");
        timeUp.setContentText("Time is up!!!");
        
        Tsec = getTSec();
        
        //KeyFrame
        keyFrame1 = new KeyFrame(Duration.millis(tickTimeInSeconds * 1000), (ActionEvent event) -> {
            
            secondsElapsed += tickTimeInSeconds;
            double rotation = secondsElapsed * angleDeltaPerSeconds;
            handImageView.setRotate(rotation);
            
            Ssec = (int)secondsElapsed;
            
            digitalClock.setText(getCountDown());
            
            if ( (Tsec-Ssec) <= 0) {
                timeline.stop();
                timeUp.show();
            }
        } );
        
        
        //Timeline and Scene
        timeline = new Timeline();
        timeline.getKeyFrames().addAll(keyFrame1);
        
        timeline.setCycleCount(Animation.INDEFINITE);
        
        
        Scene scene = new Scene(root, 500, 500);
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    //Methods
    
    public boolean isRunning(){
            if (timeline != null){
                if(timeline.getStatus() == Animation.Status.RUNNING){
                    return true;
                }
            }
            return false;
    }
    
    public String getTimeElapsed(){
        int hour=0, min=0, sec=0;
        
        if (Ssec >= 3600) {
            hour = (int)(Ssec / 3600);
            min = (int)(Ssec - (hour*3600))/60;
            sec = (int)((Ssec - (hour*3600)) - (min*60)) ; 
        }
        else if (Ssec < 3600 && Ssec >=60){
            min = (int)(Ssec/60);
            sec = (int)(Ssec - (min*60));
        }
        else {
            sec = Ssec;
        }
        
        return (hour + ":" + min + ":" + sec);
    }
    
    public String getCountDown() {
        int hour=0, min=0, sec=0;
        int seconds = (Tsec - Ssec);
        
        if (seconds >= 3600) {
            hour = (int)(seconds / 3600);
            min = (int)((seconds - (hour*3600))/60);
            sec = (int)((seconds - (hour*3600)) - (min*60)) ; 
        }
        else if (seconds < 3600 && seconds >=60){
            min = (int)(seconds/60);
            sec = (int)(seconds - (min*60));
        }
        else {
            sec = seconds;
        }
            
        return (hour + ":" + min + ":" + sec);
    }
    
    public String getLapTime() {
        int seconds = Ssec - store;
        
        int hour=0, min=0, sec=0;
        
        if (seconds >= 3600) {
            hour = (int)(seconds / 3600);
            min = (int)(seconds - (hour*3600))/60;
            sec = (int)((seconds - (hour*3600)) - (min*60)) ; 
        }
        else if (seconds < 3600 && seconds >=60){
            min = (int)(seconds/60);
            sec = (int)(seconds - (min*60));
        }
        else {
            sec = seconds;
        }
        
        return (hour + ":" + min + ":" + sec);
    }

    public int getTSec() {
        int sec=0;
        
        if (hourInput > 0) {
            sec += (hourInput*3600);
        }
        if (minInput > 0) {
            sec += (minInput*60);
        }
        if (secInput > 0) {
            sec += secInput;
        }
        return sec;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
