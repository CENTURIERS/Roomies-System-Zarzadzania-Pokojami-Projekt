module com.roomies {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.hibernate.orm.core;
    requires java.naming;
    requires jakarta.persistence;
    requires java.sql;
    requires jdk.compiler;
    opens com.roomies.controller to javafx.fxml;
    opens com.roomies.model to org.hibernate.orm.core, javafx.base;
    exports com.roomies;
}