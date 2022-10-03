package models;

import play.data.validation.*;
import play.db.jpa.*;
import play.libs.Codec;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="user")
public class User extends Model {

    @Email
    @Required
    @Column
    @Unique
    public String email;

    @Required
    public String passwordHash;

    @Required
    @Column
    public String name;
    public String needConfirmation;

    public User(String email, String password, String name) {
        this.email= email;
        this.passwordHash= Codec.hexMD5(password);
        this.name= name;
        this.needConfirmation= Codec.UUID();
        create();
    }

    public static User findByEmail(String email) {
        return find("email", email).first();

    }
    public boolean checkPassword(String password) {
        return passwordHash.equals(Codec.hexMD5(password));

    }
    public static User findByRegistrationUUID(String uuid) {
        return find("needConfirmation", uuid).first();
    }

    public static List<User> findAll(int page, int pageSize) {
        return User.all().fetch(page, pageSize);
    }




}