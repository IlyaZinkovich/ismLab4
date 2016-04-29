package by.ism.lab4.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.commons.math3.distribution.UniformRealDistribution;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static java.lang.Math.*;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("plot.fxml"));
        primaryStage.setTitle("Plots");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    static int num_steps = 100000;
    static List<List<Double>> A = new ArrayList<>();
    static List<Double> b = new ArrayList<>();

    static void firstIntegral() throws FileNotFoundException {
        int repeatsNumber = 50;
        PrintWriter writer = new PrintWriter(new File("integral1"));
        writer.println("id \t time \t result \t epsilon");
        for (int j = 0; j < repeatsNumber; j++) {
            double a = 0;
            double b = 5.0 * PI / 7.0;
            UniformRealDistribution distribution = new UniformRealDistribution(a, b);
            double t1, t2, dt;
            t1 = System.currentTimeMillis();
            double result, sum = 0.0;
            for (int i = 0; i < num_steps; ++i) {
                double rnd = distribution.sample();
                sum += cos(rnd + sin(rnd));
            }
            result = sum * (b - a) / num_steps;
            t2 = System.currentTimeMillis();
            dt = (t2 - t1);
            double real_result = -0.485736;
            writer.println(j + " \t " + dt + " \t " + result + " \t " + abs(result - real_result));
        }
        writer.flush();
        writer.close();
    }


    static double task1Function(double x, double y){
        return 1.0 / (pow(x, 2) + pow(y, 4));
    }

    static void secondIntegral() throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(new File("integral2"));
        double a = -sqrt(3);
        double b = sqrt(3);
        UniformRealDistribution distribution = new UniformRealDistribution(a, b);
        int repeatsNumber = 50;
        writer.println("id \t time \t result \t epsilon");
        for (int j = 0; j < repeatsNumber; j++) {
            double t1, t2, dt;
            double result, sum = 0.0;
            t1 = System.currentTimeMillis();
            int n = 0;
            for (int i = 0; i < num_steps; ++i) {
                double x = distribution.sample();
                double y = distribution.sample();
                if (pow(x, 2) + pow(y, 2) < 3 && pow(x, 2) + pow(y, 2) >= 1) {
                    sum += task1Function(x, y);
                    n++;
                }
            }
            result = sum * (6.283185307179) / n;
            t2 = System.currentTimeMillis();
            dt = (t2 - t1);
            double real_result = 3.21805828;
            writer.println(j + " \t " + dt + " \t " + result + " \t " + abs(result - real_result));
        }
        writer.flush();
        writer.close();
    }

    public static void main(String[] args) {
        try {
            firstIntegral();
            secondIntegral();
            read("Zinkovich.txt");
            slau();
            task3();
            launch(args);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static void read(String fileName) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(fileName));
        boolean readMatrix = true;
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            if (line.isEmpty()) {
                readMatrix = false;
                continue;
            }
            String[] split = line.split(" ");
            List<Double> row = Arrays.stream(split).map(Double::valueOf).collect(Collectors.toList());
            if (readMatrix) A.add(row);
            else b = row;
        }
    }

    static double[] slau() throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(new File("slau.txt"));
        int n = b.size();
        int N = 1000;
        int m = 10000;
        double[] x = new double[n];  //Решение системы
        double[] h = new double[n];
        double pi = 1.0 / (double) n;
        int[] i = new int[N + 1];    //Цепь Маркова
        double[] Q = new double[N + 1];  //Веса состояний цепи Маркова
        double[][] ksi = new double[n][m];  //СВ
        double alpha;  //БСВ

        for (int k = 0; k < n; k++)
            for (int j = 0; j < m; j++)
                ksi[k][j] = 0;

        UniformRealDistribution distribution = new UniformRealDistribution(0, 1);
        ///////////////////////////////////////
        //Моделируем m цепей Маркова длины N
        for (int j = 0; j < m; j++) {
            for (int k = 0; k <= N; k++) {
                alpha = distribution.sample();
                double temp = 1.0 / (double) n;
                for (int t = 0; t < n; t++) {
                    if (alpha <= temp) {
                        i[k] = t;
                        break;
                    } else
                        temp += 1.0 / n;
                }
            }

            for (int dim = 0; dim < n; dim++) {
                for (int ind = 0; ind < n; ind++) {
                    if (ind == dim) {
                        h[ind] = 1;
                    } else {
                        h[ind] = 0;
                    }
                }
                //Вычисляем веса цепи Маркова
                Q[0] = h[i[0]] / pi;

                for (int k = 1; k <= N; k++) {
                    Q[k] = Q[k - 1] * A.get(i[k - 1]).get(i[k]) / pi;
                }
                for (int k = 0; k <= N; k++)
                    ksi[dim][j] = ksi[dim][j] + Q[k] * b.get(i[k]);
            }
        }
        for (int dim = 0; dim < n; dim++) {
            x[dim] = 0;
            for (int k = 0; k < m; k++)
                x[dim] = x[dim] + ksi[dim][k];
            x[dim] = x[dim] / m;
            writer.print(x[dim] + " ");
        }
        writer.flush();
        writer.close();
        return x;
    }

    static double task3Function(double x, double y, double z) {
        return 8.0 * y * y * z * exp(2.0 * x * y * z);
    }

    static void task3() throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(new File("integral3"));
        UniformRealDistribution dist1 = new UniformRealDistribution(-1.0, 0.0);
        UniformRealDistribution dist2 = new UniformRealDistribution(0.0, 2.0);
        UniformRealDistribution dist3 = new UniformRealDistribution(0.0, 1.0);
        for (int j = num_steps / 20; j < num_steps; j = j + num_steps / 20) {
            double result, sum = 0.0;
            for (int i = 0; i < j; ++i) {
                double x = dist1.sample();
                double y = dist2.sample();
                double z = dist3.sample();
                sum += task3Function(x, y, z) / j;
            }
            result = sum * 2.0;
            double real_result = 5.0 - exp(-4);
            writer.println(j + " " +  abs(result - real_result));
        }
        writer.flush();
        writer.close();
    }

}


