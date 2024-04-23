package net.modularmods.protogl;

import lombok.Getter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public class ProtoGL {

    @Getter
    private static ProtoGL instance;

    @Getter
    private static final Logger logger = LogManager.getLogger(ProtoGL.class);

    public ProtoGL() {
        instance = this;
    }

    public static void main(String[] args) {
        Configurator.setRootLevel(Level.DEBUG); // Set global log level to debug

        System.out.println("Hello world!");
    }

}