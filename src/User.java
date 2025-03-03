import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String fio;
    private String lastName;
    private String firstName;
    private String patronymic;
    private LocalDate birthDate;
    private String phoneNumber;
    private Double salary;

    private Gender gender = Gender.UNKNOWN;

    private List<String> invalidFields = new ArrayList<>();
    private boolean phoneValid;

    public String getFio() {
        return fio;
    }
    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPatronymic() {
        return patronymic;
    }
    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Double getSalary() {
        return salary;
    }
    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public Gender getGender() {
        return gender;
    }
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public List<String> getInvalidFields() {
        return invalidFields;
    }
    public void addInvalidField(String fieldName) {
        invalidFields.add(fieldName);
    }

    public boolean isPhoneValid() {
        return phoneValid;
    }
    public void setPhoneValid(boolean phoneValid) {
        this.phoneValid = phoneValid;
    }

    // Возраст больше 25?
    public boolean isOlderThan25() {
        if (birthDate == null) {
            return false;
        }
        int years = Period.between(birthDate, LocalDate.now()).getYears();
        return years > 25;
    }

    @Override
    public String toString() {
        return String.format(
                "User{ФИО='%s', Дата рождения=%s, Телефон='%s' (валиден=%s), Зарплата=%s, Пол=%s, Невалидные поля=%s}",
                fio,
                birthDate,
                phoneNumber,
                phoneValid,
                salary,
                gender,
                invalidFields
        );
    }
}
