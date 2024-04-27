import java.lang.annotation.*;
import java.lang.reflect.*;
import java.io.*;
import java.util.Scanner;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Save {
}

class MyClass implements Serializable {
    @Save
    private String field1 = "Hello";
    @Save
    private int field2 = 123;
    @Save
    private double field3 = 3.14;

    public String toString() {
        return "Field1: " + field1 + ", Field2: " + field2 + ", Field3: " + field3;
    }
}

public class Serializer {
    public static void serialize(Object obj, String fileName) {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName)))) {
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Save.class)) {
                    field.setAccessible(true);
                    writer.println(field.get(obj).toString());
                }
            }
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void deserialize(Object obj, String fileName) {
        try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(fileName)))) {
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Save.class)) {
                    field.setAccessible(true);
                    String value = scanner.nextLine();
                    if (field.getType() == int.class) {
                        field.setInt(obj, Integer.parseInt(value));
                    } else if (field.getType() == double.class) {
                        field.setDouble(obj, Double.parseDouble(value));
                    } else if (field.getType() == String.class) {
                        field.set(obj, value);
                    }
                }
            }
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MyClass myObject = new MyClass();
        String filePath = "C:\\data.txt";
        serialize(myObject, filePath);

        MyClass deserializedObject = new MyClass();
        deserialize(deserializedObject, filePath);

        System.out.println("Deserialized object: " + deserializedObject);
    }
}
