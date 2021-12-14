package com.example.application.views.defaultview;


import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;


import javax.swing.*;

@Route(value = "")
public class DefaultView extends Composite{

    public DefaultView() { }

    @Override
    protected Component initContent() {


         VerticalLayout vlay1= new VerticalLayout(
                 new H1("Secure Cloud Storage on Amazon Web Service"),
                 new H2("About this project"),
                 new Paragraph("In the current age of cloud computing, companies all over the world use cloud-based " +
                        "service to store their databases. However, balancing between the benefits of cloud storage with " +
                        "the risk of cloud security can be a difficult task.\n For this project, we are implementing a " +
                        "system to provide encryption when users want to upload files to Amazon Web Services. Our system " +
                        "will encrypt the file before sending " +
                        "it to AWS for storage and decrypt the requested file that has been retrieved from AWS"),


                new Button("Login", event ->{
                    UI.getCurrent().navigate("login");
                })
        );

         vlay1.setAlignItems(FlexComponent.Alignment.CENTER);

        return vlay1;
    }
}
