package entelect.training.incubator.spring.booking.model;

import lombok.Data;

@Data
public class Customer {
    private Integer id;
    private String phoneNumber;
    private String passportNumber;
    private String firstName;
    private String lastName;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
