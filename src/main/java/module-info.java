module name.dannywatts.cmpsc221hw4 {
    requires javafx.controls;
    requires javafx.fxml;


    opens name.dannywatts.cmpsc221hw4 to javafx.fxml;
    exports name.dannywatts.cmpsc221hw4;
}