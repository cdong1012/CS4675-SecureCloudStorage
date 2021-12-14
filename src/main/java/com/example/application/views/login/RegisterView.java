package com.example.application.views.login;

import com.example.application.data.service.AuthService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import java.util.Locale;
import java.util.function.IntPredicate;

@Route("register")
public class RegisterView extends Composite {

    private final AuthService authService;

    public RegisterView(AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected Component initContent() {
        TextField username = new TextField("Username");
        PasswordField password1 = new PasswordField("Password");
        PasswordField password2 = new PasswordField("Confirm password");
        TextField address = new TextField("Address");
        TextField favColor = new TextField("Favorite Color");
        TextField favAnimal = new TextField("Favorite Animal");
        return new VerticalLayout(
                new H2("Register"),
                username,
                password1,
                password2,
                address,
                favColor,
                favAnimal,
                new Button("Send", event -> register(
                        username.getValue(),
                        password1.getValue(),
                        password2.getValue(),
                        address.getValue(),
                        favColor.getValue(),
                        favAnimal.getValue()
                )),
                new Button("Return to Login", event ->{
                    UI.getCurrent().navigate("login");
                })
        );
    }

    private void register(String username, String password1, String password2, String address, String favColor, String favAnimal) {
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
        } else if (contains(username, i -> Character.isLetter(i) && Character.isUpperCase(i))) {
            Notification.show("User name can only contain lowercase letters");
        } else {
            authService.register(username, password1, address, favColor, favAnimal);
        }
    }

        private boolean contains(String value, IntPredicate predicate) {
            return value.chars().anyMatch(predicate);
        }
}

