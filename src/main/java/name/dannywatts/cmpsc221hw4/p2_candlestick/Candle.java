package name.dannywatts.cmpsc221hw4.p2_candlestick;

// One row of the Binance candlestick data
public class Candle {
    private final long openTime; // milliseconds since 1970-01-01 UTC
    private final double open;
    private final double high;
    private final double low;
    private final double close;

    public Candle(long openTime, double open, double high, double low, double close) {
        this.openTime = openTime;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
    }

    public long getOpenTime() {
        return openTime;
    }

    public double getOpen() {
        return open;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getClose() {
        return close;
    }
}
