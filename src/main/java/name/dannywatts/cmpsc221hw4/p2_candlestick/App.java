package name.dannywatts.cmpsc221hw4.p2_candlestick;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App extends Application {
    // Plot area inside the chart pane (prices go in the right margin, dates in the bottom margin)
    private final double LEFT = 10;
    private final double TOP = 10;
    private final double PLOT_WIDTH = 910;
    private final double PLOT_HEIGHT = 530;

    // Candle colors
    private final Color UP_COLOR = Color.GREEN;
    private final Color DOWN_COLOR = Color.RED;
    private final Color GRID_COLOR = Color.LIGHTGRAY;

    private final Pane chart = new Pane();
    private final List<Candle> candles = new ArrayList<>();
    private double minPrice;
    private double maxPrice;

    @Override
    public void start(Stage stage) {
        chart.setPrefSize(1000, 570);

        if (loadCandles()) {
            findPriceRange();
            drawPriceGrid();
            drawTimeAxis();
            for (int i = 0; i < candles.size(); i++) {
                drawCandle(candles.get(i), i);
            }
            drawBorder();
        } else {
            chart.getChildren().add(new Label("Could not find BTCUSDT-4h.csv"));
        }

        BorderPane root = new BorderPane();
        root.setTop(new Label("BTC/USDT 4h (Binance)"));
        root.setCenter(chart);

        stage.setTitle("Candlestick chart");
        stage.setScene(new Scene(root));
        stage.show();
    }

    // Reads the CSV from the resources folder. Returns false if the file is missing.
    private boolean loadCandles() {
        InputStream stream = getClass().getResourceAsStream("BTCUSDT-4h.csv");
        if (stream == null) {
            return false;
        }

        try (Scanner scanner = new Scanner(stream)) {
            scanner.nextLine(); // skip the header row
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.isBlank()) {
                    continue;
                }
                // Columns: open time, open, high, low, close, (volume and others are not needed)
                String[] columns = line.split(",");
                candles.add(new Candle(
                        Long.parseLong(columns[0]),
                        Double.parseDouble(columns[1]),
                        Double.parseDouble(columns[2]),
                        Double.parseDouble(columns[3]),
                        Double.parseDouble(columns[4])));
            }
        }
        return true;
    }

    // Lowest low and highest high, with 5% padding so candles don't touch the edges
    private void findPriceRange() {
        minPrice = candles.getFirst().getLow();
        maxPrice = candles.getFirst().getHigh();
        for (Candle candle : candles) {
            minPrice = Math.min(minPrice, candle.getLow());
            maxPrice = Math.max(maxPrice, candle.getHigh());
        }
        double padding = (maxPrice - minPrice) * 0.05;
        minPrice -= padding;
        maxPrice += padding;
    }

    // Converts a price to a y coordinate in the chart (higher prices are higher up)
    private double priceToY(double price) {
        return TOP + (maxPrice - price) / (maxPrice - minPrice) * PLOT_HEIGHT;
    }

    // x coordinate of the center of the candle at this index
    private double indexToX(int index) {
        double slot = PLOT_WIDTH / candles.size();
        return LEFT + slot * index + slot / 2;
    }

    // Horizontal grid lines with price labels on the right, at "nice" round numbers
    private void drawPriceGrid() {
        double step = niceStep((maxPrice - minPrice) / 7);
        double price = Math.ceil(minPrice / step) * step; // first multiple of step in the range
        while (price <= maxPrice) {
            double y = priceToY(price);
            Line gridLine = new Line(LEFT, y, LEFT + PLOT_WIDTH, y);
            gridLine.setStroke(GRID_COLOR);

            Label priceLabel = new Label(String.format("%,.0f", price));
            priceLabel.setLayoutX(LEFT + PLOT_WIDTH + 5);
            priceLabel.setLayoutY(y - 9); // roughly centers the text on the line

            chart.getChildren().addAll(gridLine, priceLabel);
            price += step;
        }
    }

    // Rounds a step size to 1, 2 or 5 times a power of ten (e.g. 534 becomes 500)
    private double niceStep(double rawStep) {
        double magnitude = Math.pow(10, Math.floor(Math.log10(rawStep)));
        double normalized = rawStep / magnitude;
        if (normalized < 1.5) {
            return magnitude;
        } else if (normalized < 3.5) {
            return 2 * magnitude;
        } else if (normalized < 7.5) {
            return 5 * magnitude;
        } else {
            return 10 * magnitude;
        }
    }

    // Vertical grid line and date label at the first candle of each day (midnight UTC, like Binance)
    private void drawTimeAxis() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d");
        for (int i = 0; i < candles.size(); i++) {
            ZonedDateTime time = Instant.ofEpochMilli(candles.get(i).getOpenTime()).atZone(ZoneOffset.UTC);
            if (time.getHour() == 0) {
                double x = indexToX(i);
                Line gridLine = new Line(x, TOP, x, TOP + PLOT_HEIGHT);
                gridLine.setStroke(GRID_COLOR);

                Label dateLabel = new Label(time.format(formatter));
                dateLabel.setLayoutX(x - 15);
                dateLabel.setLayoutY(TOP + PLOT_HEIGHT + 5);

                chart.getChildren().addAll(gridLine, dateLabel);
            }
        }
    }

    private void drawCandle(Candle candle, int index) {
        Color color;
        if (candle.getClose() >= candle.getOpen()) {
            color = UP_COLOR;
        } else {
            color = DOWN_COLOR;
        }

        double x = indexToX(index);

        // Wick from the high to the low
        Line wick = new Line(x, priceToY(candle.getHigh()), x, priceToY(candle.getLow()));
        wick.setStroke(color);

        // Body from the open to the close, at least 1px tall so a flat candle still shows
        double bodyWidth = PLOT_WIDTH / candles.size() * 0.6;
        double bodyTop = priceToY(Math.max(candle.getOpen(), candle.getClose()));
        double bodyHeight = Math.max(1, Math.abs(priceToY(candle.getOpen()) - priceToY(candle.getClose())));
        Rectangle body = new Rectangle(x - bodyWidth / 2, bodyTop, bodyWidth, bodyHeight);
        body.setFill(color);

        chart.getChildren().addAll(wick, body); // body is added last so it covers the wick
    }

    // Frames the bottom and right edges of the plot, where the axes are
    private void drawBorder() {
        Line bottom = new Line(LEFT, TOP + PLOT_HEIGHT, LEFT + PLOT_WIDTH, TOP + PLOT_HEIGHT);
        Line right = new Line(LEFT + PLOT_WIDTH, TOP, LEFT + PLOT_WIDTH, TOP + PLOT_HEIGHT);
        bottom.setStroke(Color.GRAY);
        right.setStroke(Color.GRAY);
        chart.getChildren().addAll(bottom, right);
    }
}
