/**
 * For demonstration purposes and for avoiding some JavaFX warnings, the RoboRally
 * application is now configured as a Java module.
 */
module rest_client {

    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.web;
    requires com.fasterxml.jackson.annotation;
    requires javafx.controls;
    requires spring.hateoas;
    requires spring.core;
    requires spring.webflux;
    requires reactor.core;

    exports dk.dtu.compute.course02324.part4.consuming_rest;

    exports dk.dtu.compute.course02324.part4.consuming_rest.model;
    exports dk.dtu.compute.course02324.part4.consuming_rest.wrappers;


    /*
    opens static;
    ...
     */


}