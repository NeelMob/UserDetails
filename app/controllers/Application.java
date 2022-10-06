package controllers;

import play.*;
import play.mvc.*;
import play.data.validation.*;

import models.*;


public class Application extends Controller {

    static Integer pageSize = Integer.parseInt(Play.configuration.getProperty("users.pageSize", "5"));

    @Before
    static void globals() {
        renderArgs.put("connected", connectedUser());
        renderArgs.put("pageSize", pageSize);
    }


    public static void signup() {

        render();
    }

    public static void register(@Required @Email String email, @Required @MinSize(5) String password, @Equals("password") String password2, @Required String name) {
        if (validation.hasErrors()) {
            validation.keep();
            params.flash();
            flash.error("Please correct these errors !");
            signup();
        }

        try {
            User user = new User(email, password, name);
                flash.success("Your account is created. Please login");
                login();

        } catch (Exception e) {
            flash.error("Duplicate email");
            Logger.error(e, "Mail error");
        }
        login();
    }

    public static void confirmRegistration(String uuid) {
        User user = User.findByRegistrationUUID(uuid);
        notFoundIfNull(user);
        user.needConfirmation = null;
        user.save();
        connect(user);
        flash.success("Welcome %s !", user.name);
        Users.show(user.id);
    }

    public static void login() {

        render();
    }

    public static void authenticate(String email, String password) {
        User user = User.findByEmail(email);
        if (user == null || !user.checkPassword(password)) {
            flash.error("Bad email or bad password");
            flash.put("email", email);
            login();

        }
        connect(user);
        flash.success("Welcome back %s !", user.name);
        Users.show(user.id);
    }

    public static void logout() {
        flash.success("You've been logged out");
        session.clear();
        Users.index();
    }


    static void connect(User user) {

        session.put("logged", user.id);
    }

    static User connectedUser() {
        String userId = session.get("logged");
        return userId == null ? null : (User) User.findById(Long.parseLong(userId));
    }
}