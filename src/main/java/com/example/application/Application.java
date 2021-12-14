package com.example.application;

import com.example.application.data.AWS.AWSController;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.io.File;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the  * and some desktop browsers.
 *
 */
@SpringBootApplication
@PWA(name = "secure-file-storage", shortName = "secure-file-storage")
public class Application extends SpringBootServletInitializer implements AppShellConfigurator {

    public static void main(String[] args) {
        AWSController.createClientConnection();
        String os = System.getProperty("os.name").toLowerCase();
        char fileSlash = '\\';
        if (os.indexOf("mac") >= 0 ||
                (os.indexOf("nix") >= 0
                        || os.indexOf("nux") >= 0
                        || os.indexOf("aix") > 0)) {
            fileSlash = '/';
        }
        File temp = new File(System.getProperty("user.dir") + fileSlash + "publicKey");
        if (!temp.exists()) {
            AWSController.downloadObject("main-cs4675-bucket", "publicKey", System.getProperty("user.dir") + fileSlash + "publicKey");
        }
        SpringApplication.run(Application.class, args);
    }
}
