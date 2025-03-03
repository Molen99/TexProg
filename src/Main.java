import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        String fileName = "data.txt";

        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(fileName));
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
            return;
        }

        List<User> users = new ArrayList<>();
        for (String line : lines) {

            String[] parts = line.split(";");

            String fio = parts.length > 0 ? parts[0].trim() : "";
            String birthDate = parts.length > 1 ? parts[1].trim() : "";
            String phone = parts.length > 2 ? parts[2].trim() : "";
            String salary = parts.length > 3 ? parts[3].trim() : "";

            if (parts.length > 4) {
                System.err.println("В строке больше данных, чем ожидалось: " + line);
            }

            User user = createUser(fio, birthDate, phone, salary);
            users.add(user);
        }

        // 1) Вывести всех пользователей
        System.out.println("=== 1) Список всех пользователей ===");
        users.forEach(System.out::println);

        // 2) Кол-во мужчин и женщин
        long countMale = users.stream().filter(u -> u.getGender() == Gender.MALE).count();
        long countFemale = users.stream().filter(u -> u.getGender() == Gender.FEMALE).count();
        System.out.println("\n=== 2) Количество мужчин и женщин ===");
        System.out.println("Мужчин: " + countMale);
        System.out.println("Женщин: " + countFemale);

        // 3) Кол-во пользователей старше 25 лет
        long countOlder25 = users.stream().filter(User::isOlderThan25).count();
        System.out.println("\n=== 3) Количество пользователей старше 25 лет ===");
        System.out.println(countOlder25);

        // 4) Средняя заработная плата (только для валидных делал)
        double averageSalary = users.stream()
                .filter(u -> u.getSalary() != null)
                .mapToDouble(User::getSalary)
                .average()
                .orElse(0.0);
        System.out.println("\n=== 4) Средняя заработная плата ===");
        System.out.println(averageSalary);

        // 5) Кол-во женщин с валидным номером телефона
        long womenWithValidPhone = users.stream()
                .filter(u -> u.getGender() == Gender.FEMALE)
                .filter(User::isPhoneValid)
                .count();
        System.out.println("\n=== 5) Количество женщин с валидным номером телефона ===");
        System.out.println(womenWithValidPhone);

        // 6) Пользователи с невалидными данными
        List<User> invalidUsers = users.stream()
                .filter(u -> !u.getInvalidFields().isEmpty())
                .collect(Collectors.toList());
        System.out.println("\n=== 6) Пользователи с невалидными полями ===");
        for (User u : invalidUsers) {
            System.out.println("Пользователь: " + u.getFio() +
                    "; Невалидные поля: " + String.join(", ", u.getInvalidFields()));
        }
    }

    /**
     * Фабричный метод для создания и валидации User.
     */
    private static User createUser(String fio, String birthDate, String phone, String salary) {

        User user = new User();

        // 1) Валидация ФИО
        if (fio == null || fio.isEmpty()) {
            user.addInvalidField("ФИО");
        } else {
            String[] nameParts = fio.split("\\s+");
            if (nameParts.length >= 2) {
                user.setLastName(nameParts[0]);
                user.setFirstName(nameParts[1]);
                if (nameParts.length >= 3) {
                    user.setPatronymic(nameParts[2]);
                }
            } else {
                user.addInvalidField("ФИО");
            }
        }
        user.setFio(fio);

        user.setGender(Gender.UNKNOWN);
        if (user.getLastName() != null || user.getFirstName() != null || user.getPatronymic() != null) {

            // Проверяю фамилию
            boolean isFemaleByLastName = false;
            if (user.getLastName() != null) {
                String lastName = user.getLastName().toLowerCase(Locale.ROOT);
                if (lastName.endsWith("а") || lastName.endsWith("я")) {
                    isFemaleByLastName = true;
                }
            }

            // Проверяю имя
            boolean isFemaleByFirstName = false;
            if (user.getFirstName() != null) {
                String firstName = user.getFirstName().toLowerCase(Locale.ROOT);
                if (firstName.endsWith("а") || firstName.endsWith("я")) {
                    isFemaleByFirstName = true;
                }
            }

            // Проверяю отчество
            boolean isFemaleByPatronymic = false;
            if (user.getPatronymic() != null) {
                String patronymic = user.getPatronymic().toLowerCase(Locale.ROOT);
                if (patronymic.endsWith("вна") || patronymic.endsWith("чна")) {
                    isFemaleByPatronymic = true;
                }
            }

            // Если хотя бы один из признаков указывает на женский пол, устанавливаю FEMALE
            if (isFemaleByLastName || isFemaleByFirstName || isFemaleByPatronymic) {
                user.setGender(Gender.FEMALE);
            } else {
                user.setGender(Gender.MALE);
            }
        }

        // 2) Валидация даты рождения
        if (birthDate == null || birthDate.isEmpty()) {
            user.addInvalidField("Дата рождения");
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            try {
                LocalDate date = LocalDate.parse(birthDate, formatter);
                user.setBirthDate(date);
            } catch (DateTimeParseException e) {
                user.addInvalidField("Дата рождения");
            }
        }

        // 3) Валидация телефона
        if (phone == null || phone.isEmpty()) {
            user.addInvalidField("Телефон");
        } else {
            String digits = phone.replaceAll("\\D+", "");
            if (digits.length() == 10 || (digits.length() == 11 && (digits.startsWith("7") || digits.startsWith("8")))) {
                user.setPhoneNumber(phone);
                user.setPhoneValid(true);
            } else {
                user.addInvalidField("Телефон");
            }
        }

        // 4) Валидация зарплаты
        if (salary == null || salary.isEmpty()) {
            user.addInvalidField("Зарплата");
        } else {
            try {
                double salaryVal = Double.parseDouble(salary);
                if (salaryVal >= 0) {
                    user.setSalary(salaryVal);
                } else {
                    user.addInvalidField("Зарплата");
                }
            } catch (NumberFormatException e) {
                user.addInvalidField("Зарплата");
            }
        }

        return user;
    }
}
