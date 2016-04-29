package by.ism.lab4.test;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Controller implements Initializable {
    @FXML
    LineChart<Integer, Double> chart;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        XYChart.Series<Integer, Double> series = new XYChart.Series<>();
        series.setName("Plot");
        chart.getData().add(series);
        try {
            Scanner scanner = new Scanner(new File("integral3"));
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                String[] split = line.split(" ");
                series.getData().add(new XYChart.Data<>(Integer.valueOf(split[0]), Double.valueOf(split[1])));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println();
    }

}

