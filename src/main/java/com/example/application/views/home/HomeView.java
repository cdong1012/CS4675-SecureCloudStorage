package com.example.application.views.home;

import com.example.application.data.entity.User;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.IronIcon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.VaadinSession;

import java.util.Arrays;
import java.util.List;

@PageTitle("Home")
@CssImport(value = "./styles/views/home/home-view.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
public class HomeView extends Composite {

    public HomeView() {
    }

    @Override
    protected Component initContent() {
        User user = VaadinSession.getCurrent().getAttribute(User.class);

        H2 uploadText = new H2("Go to the Upload page");
        H2 downloadText = new H2("Go to the Download page");
        H2 logoutText = new H2("Logout now");
        H2 deleteText = new H2("Go to the Delete Account page");
        return new VerticalLayout(
                new H1("Secure Cloud Storage"),
                uploadText,
                new Button("Upload", event -> {
                    UI.getCurrent().navigate("upload");
                }),
                downloadText,
                new Button("Download", event ->{
                    UI.getCurrent().navigate("download");
                }),
                logoutText,
                new Button("Logout", event ->{
                    UI.getCurrent().navigate("logout");
                }),
                deleteText,
                new Button("Delete Account", event ->{
                    UI.getCurrent().navigate("delete");
                }),
                new H3("User name: " + user.getUsername())
        );
    }

}
