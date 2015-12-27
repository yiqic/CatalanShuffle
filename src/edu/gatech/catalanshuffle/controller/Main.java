package edu.gatech.catalanshuffle.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import edu.gatech.catalanshuffle.view.*;

public class Main extends Application {
	
	public static final int TICK_RATE = 200;
	public static final int N = 8;
	 
    public static void main(String[] args) {
        launch(args);
    }
 
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Catalan Structure");
        Group root = new Group();
        BorderPane frame = new BorderPane();
//        CatalanModelCanvas canvas = new DyckPathCanvas(N, 900, 450);
//        CatalanModelCanvas canvas = new DyckPathCollectionCanvas(N, 900, 450, 100);
//        CatalanModelCanvas canvas = new DyckPathCouplingCanvas(N, 900, 450, false, true);
        CatalanModelCanvas canvas = new PolygonTriangulationCanvas(N, 900, 450);
        
        Timeline timer = new Timeline(new KeyFrame(Duration.millis(TICK_RATE), new TickCanvas(canvas, 1)));
        timer.setCycleCount(Timeline.INDEFINITE);
        
        frame.setCenter(canvas);
        frame.setBottom(getControlPanel(timer, canvas));
        root.getChildren().add(frame);
        primaryStage.setScene(new Scene(root));
        
        primaryStage.show();
        timer.play();
    }
    
    public HBox getControlPanel(Timeline timer, CatalanModelCanvas canvas) {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color: #336699;");

        Button pause = new Button("Pause");
        pause.setPrefSize(100, 20);
        pause.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
            	timer.pause();
            }
        });
        Button play = new Button("Play");
        play.setPrefSize(100, 20);
        play.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
            	timer.play();
            }
        });
        Button tick = new Button("Tick");
        tick.setPrefSize(100, 20);
        tick.setOnAction(new TickCanvas(canvas, 1));
        Button tick5 = new Button("Tick 5 Times");
        tick5.setPrefSize(100, 20);
        tick5.setOnAction(new TickCanvas(canvas, 5));
        Button reset = new Button("Reset");
        reset.setPrefSize(100, 20);
        reset.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
            	canvas.reset();
            }
        });
        
        hbox.getChildren().addAll(pause, play, tick, tick5, reset);
        return hbox;
    }
    
    private class TickCanvas implements EventHandler<ActionEvent> {
    	
    	private final CatalanModelCanvas canvas;
    	private final int itr;
    	
    	public TickCanvas(CatalanModelCanvas canvas, int itr) {
    		this.canvas = canvas;
    		this.itr = itr;
		}

		@Override
		public void handle(ActionEvent event) {
			for (int i = 0; i < itr; i++) {
				canvas.tick();
			}
		}
    }

}