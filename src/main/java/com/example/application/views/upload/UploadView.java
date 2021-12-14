package com.example.application.views.upload;

import com.example.application.data.entity.User;
import com.example.application.data.service.Database;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.io.File;
import java.io.InputStream;

@Route("upload")
@PageTitle("Upload")
public class UploadView extends Composite {

    private final Database database;
    public UploadView(Database database) {
        this.database = database;
    }

    @Override
    protected Component initContent() {
        // User user = VaadinSession.getCurrent().getAttribute(User.class);

        H2 uploadText = new H2("Please enter the path of a local file to upload");
        TextField uploadField = new TextField("Enter file path here");

        return new VerticalLayout(
                uploadText,
                uploadField,
                new Button("Upload", event -> upload(uploadField.getValue()))
        );
    }

    private void upload(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            Notification.show("ERROR: File does not exist. Can't upload");
        } else {
            User user = VaadinSession.getCurrent().getAttribute(User.class);
            this.database.upload(user, filePath);
            Notification.show("Finish uploading file...");
        }
    }
}
