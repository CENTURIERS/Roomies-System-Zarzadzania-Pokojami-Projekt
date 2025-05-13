module com.roomies {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.hibernate.orm.core;


    opens com.roomies to javafx.fxml;
    exports com.roomies;
}