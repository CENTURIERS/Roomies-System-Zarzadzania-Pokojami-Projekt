module com.roomies {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.hibernate.orm.core;
    requires java.naming;
    requires jakarta.persistence;
    opens com.roomies.controller to javafx.fxml;
    opens com.roomies.model to org.hibernate.orm.core;
    exports com.roomies;
}