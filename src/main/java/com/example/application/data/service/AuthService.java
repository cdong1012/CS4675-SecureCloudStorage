package com.example.application.data.service;
import com.example.application.data.AWS.AWSController;
import com.example.application.data.entity.Contact;
import com.example.application.data.entity.Role;
import com.example.application.data.entity.SecurityQuestion;
import com.example.application.data.entity.User;
import com.example.application.views.delete.DeleteView;
import com.example.application.views.download.DownloadView;
import com.example.application.views.home.HomeView;
import com.example.application.views.logout.LogoutView;
import com.example.application.views.main.MainView;
import com.example.application.views.upload.UploadView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.stereotype.Service;
import com.vaadin.flow.component.notification.Notification;

import java.security.Security;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuthService {

    public record AuthorizedRoute(String route, String name, Class<? extends Component> view) {

    }

    public class AuthException extends Exception {

    }

    private final UserRepository userRepository;
    private final Database database;

    public AuthService(UserRepository userRepository, Database database) {
        this.userRepository = userRepository;
        this.database = database;
    }

    public void authenticate(String username, String password) throws AuthException {
        User user = userRepository.getByUsername(username);

        if (user == null) {
            throw new AuthException();
        }

        Contact contact = this.database.getContactFromUser(user);

        if (contact == null) {
            throw new AuthException();
        }

        System.out.println("Contact: " + contact.getBucketName() + " " + contact.getSecurityQuestion().getAddress());
        if (user != null && this.database.authenticatePassword(user, password)) {
            // Authentication successful
            VaadinSession.getCurrent().setAttribute(User.class, user);
            createRoutes(user.getRole());
        } else {
            throw new AuthException();
        }
    }

    private void createRoutes(Role role) {
        getAuthorizedRoutes(role).stream()
                .forEach(route ->
                        RouteConfiguration.forSessionScope().setRoute(
                                route.route, route.view, MainView.class));
    }

    public List<AuthorizedRoute> getAuthorizedRoutes(Role role) {
        var routes = new ArrayList<AuthorizedRoute>();

        if (role.equals(Role.USER)) {
            routes.add(new AuthorizedRoute("home", "Home", HomeView.class));
            routes.add(new AuthorizedRoute("logout", "Logout", LogoutView.class));
            routes.add(new AuthorizedRoute("upload", "Upload", UploadView.class));
            routes.add(new AuthorizedRoute("download", "Download", DownloadView.class));
            routes.add(new AuthorizedRoute("delete", "Delete Account", DeleteView.class));
        } else if (role.equals(Role.ADMIN)) {
            routes.add(new AuthorizedRoute("home", "Home", HomeView.class));
            routes.add(new AuthorizedRoute("logout", "Logout", LogoutView.class));
            routes.add(new AuthorizedRoute("upload", "Upload", UploadView.class));
            routes.add(new AuthorizedRoute("download", "Download", DownloadView.class));
            routes.add(new AuthorizedRoute("delete", "Delete Account", DeleteView.class));
        }
        return routes;
    }

    public void register(String username, String password, String address, String favColor, String favAnimal) {
        if (this.userRepository.getByUsername(username) != null) {
            Notification.show("Register fail. User with the name " + username + " already exists.");
            return;
        }
        User user = this.userRepository.save(new User(username, Role.USER));
        this.database.addUser(user, password, address, favColor, favAnimal);

        Notification.show("Registering user " + username);
    }

    public void activate(String activationCode) throws AuthException {
        User user = userRepository.getByActivationCode(activationCode);
        if (user != null) {
            user.setActive(true);
            userRepository.save(user);
        } else {
            throw new AuthException();
        }
    }

    public void reset(String username, String password, String address, String favColor, String favAnimal) {
        User user = userRepository.getByUsername(username);

        if (user == null) {
            Notification.show("There is no user with this username");
            Notification.show("Please try again");
            return;
        }

        Contact contact = this.database.getContactFromUser(user);

        if (contact == null) {
            Notification.show("There is no user with this username");
            Notification.show("Please try again");
            return;
        }
        SecurityQuestion newSecurityQuestion = new SecurityQuestion(address, favColor, favAnimal);
        if (!contact.getSecurityQuestion().equals(newSecurityQuestion)) {
            Notification.show("Your answers for the security questions are incorrect");
            Notification.show("Please try again");
            return;
        }
        this.database.updatePassword(user, password);
    }

}
