import java.lang.annotation.*;
import java.lang.reflect.*;
import java.io.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface Save {
}

class MyClass implements Serializable {
    @Save
    private String field1 = "Hello";
    private int field2 = 123;
    @Save
    private double field3 = 3.14;

    public String toString() {
        return "Field1: " + field1 + ", Field2: " + field2 + ", Field3: " + field3;
    }
}

public class Serializer {
    public static void serialize(Object obj, String fileName) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Save.class)) {
                    field.setAccessible(true);
                    out.writeObject(field.get(obj));
                }
            }
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void deserialize(Object obj, String fileName) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Save.class)) {
                    field.setAccessible(true);
                    field.set(obj, in.readObject());
                }
            }
        } catch (IOException | ClassNotFoundException | IllegalAccessException e) {
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