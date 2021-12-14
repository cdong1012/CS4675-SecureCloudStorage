package com.example.application.views.login;

import com.example.application.data.service.AuthService;
import com.example.application.data.service.Database;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("forget")
public class ForgetView extends Composite {

    private final AuthService authService;
    private final Database database;

    public ForgetView(AuthService authService, Database database) {
        this.authService = authService;
        this.database = database;
    }

    @Override
    protected Component initContent() {
        H2 instructionText = new H2("Please enter the following fields to reset password");
        TextField username = new TextField("Username");
        PasswordField password1 = new PasswordField("Password");
        PasswordField password2 = new PasswordField("Confirm password");
        TextField address = new TextField("Address");
        TextField favColor = new TextField("favColor");
        TextField favAnimal = new TextField("favAnimal");

        return new VerticalLayout(
                instructionText,
                username,
                password1,
                password2,
                address,
                favColor,
                favAnimal,
                new Button("Reset", event -> resetPassword(
                        username.getValue(),
                        password1.getValue(),
                        password2.getValue(),
                        address.getValue(),
                        favColor.getValue(),
                        favAnimal.getValue()
                ))
        );
    }

    private void resetPassword(String username, String password1, String password2, String address, String favColor, String favAnimal) {
        if (username.trim().isEmpty()) {
            Notification.show("Enter a username");
        } else if (password1.isEmpty()) {
            Notification.show("Enter a password");
        } else if (!password1.equals(password2)) {
            Notification.show("Passwords don't match");
        } else if (address.isEmpty()) {
            Notification.show("Enter an address");
        } else if (favColor.isEmpty()) {
            Notification.show("Enter an favorite color");
        } else if (favAnimal.isEmpty()) {
            Notification.show("Enter an favorite animal");
        } else {
            authService.reset(username, password1, address, favColor, favAnimal);
        }
    }


}
