package com.example.application.views.download;

import com.example.application.data.entity.User;
import com.example.application.data.service.Database;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

@Route("download")
@PageTitle("Download")
public class DownloadView extends Composite {
    private final Database database;
    public DownloadView(Database database) {
        this.database = database;
    }

    @Override
    protected Component initContent() {
        User user = VaadinSession.getCurrent().getAttribute(User.class);

        H2 downloadText = new H2("Your bucket name: " + this.database.getBucketName(user));
        ListBox<String> listBox = new ListBox<>();
        reloadList(listBox, user);
        H3 instructionText = new H3("Please select the folder to download the file to!");
        TextField downloadPathField = new TextField("Enter folder path here");

        return new VerticalLayout(
                downloadText,
                listBox,
                new Button("Reload List", event -> reloadList(listBox, user)),
                new Button("Download", event -> downloadFile(listBox, user, downloadPathField.getValue())),
                downloadPathField,
                new H2("To delete a file, select it and click the button below"),
                new Button("Delete", event -> deleteFile(listBox, user))
        );
    }

    private void deleteFile(ListBox<String> listBox, User user) {
        String fileName = listBox.getValue();
        if (fileName == null || fileName.equals("There is nothing in here!")) {
            Notification.show("Please select a file to delete...");
            return;
        }
        this.database.deleteFile(user, fileName);
        reloadList(listBox, user);
    }

    private void downloadFile(ListBox<String> listBox, User user, String folderPath) {
        String fileName = listBox.getValue();
        if (fileName == null || fileName.equals("There is nothing in here!")) {
            Notification.show("Please select a file to download...");
            return;
        }
        if (folderPath == null || folderPath.length() == 0) {
            Notification.show("Please select a folder to download to...");
            return;
        }
        System.out.println("Folder path: " + folderPath);
        this.database.downloadFile(user, fileName, folderPath);

    }

    private void reloadList(ListBox<String> listBox, User user) {
        List<String> userFileNames = this.database.getUserFileNames(user);
        if (userFileNames.size() == 0) {
            listBox.setItems("There is nothing in here!");
        } else {
            listBox.setItems(userFileNames);
        }
    }
}
