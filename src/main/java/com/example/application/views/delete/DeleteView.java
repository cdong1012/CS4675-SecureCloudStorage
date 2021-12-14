package com.example.application.views.delete;

import com.example.application.data.entity.User;
import com.example.application.data.service.Database;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route("delete")
@PageTitle("Delete Account")
public class DeleteView extends Composite {
    private final Database database;
    public DeleteView(Database database) {
        this.database = database;
    }

    @Override
    protected Component initContent() {
        User user = VaadinSession.getCurrent().getAttribute(User.class);

        H2 warnText = new H2("You are about to delete user " + user.getUsername());
        H3 instruction1Text = new H3("Note: you will immediately log out after deletion!");
        H3 instruction2Text = new H3("Please type your name into the text field to confirm deletion!");
        TextField nameField = new TextField("Enter your user name here");

        return new VerticalLayout(
                warnText,
                instruction1Text,
                instruction2Text,
                nameField,
                new Button("Delete", event -> deleteUser(user, nameField.getValue()))
        );
    }

    private void deleteUser(User user, String enteredName) {
        if (!user.getUsername().equals(enteredName)) {
            Notification.show("The name you type does not match with your user name");
            Notification.show("Please re-try");
            return;
        }
        this.database.deleteUser(user);
        UI.getCurrent().getPage().setLocation("login");
        VaadinSession.getCurrent().getSession().invalidate();
        VaadinSession.getCurrent().close();
    }
}
