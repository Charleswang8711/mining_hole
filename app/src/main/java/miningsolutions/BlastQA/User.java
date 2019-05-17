package miningsolutions.BlastQA;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class User {

    public String id;
    public String firstName;
    public String lastName;
    public String lastLogin;

    public User(String inFirstName, String inLastName) {
        firstName = inFirstName;
        lastName = inLastName;

        // Generate unique ID
        id = UUID.randomUUID().toString();

        // Get current datetime stamp
        SimpleDateFormat ft = new SimpleDateFormat("E hh:mm:ss a zzz dd.MM/yyyy");
        lastLogin = ft.format(new Date());
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

}
