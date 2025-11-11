package sukhov.danila.out.storage;

import java.io.*;
import java.util.*;
/**
 * Утилитный класс для хранения данных в файлах.
 *
 * <p>Служит такой себе "мини-базой данных" — умеет сохранять и загружать коллекции и карты.
 * Всё работает через стандартную Java-сериализацию, без лишней магии.
 *
 * <p>Основная логика:
 * <ul>
 *     <li>{@link #saveCollection(String, Collection)} — сохраняет коллекцию в файл</li>
 *     <li>{@link #loadCollection(String)} — достаёт коллекцию обратно</li>
 *     <li>{@link #saveMap(String, Map)} — сохраняет карту (Map)</li>
 *     <li>{@link #loadMap(String)} — загружает карту из файла</li>
 * </ul>
 *
 * <p>Если файла нет — создаётся новая пустая коллекция или карта.
 * При ошибке чтения/записи просто выводится сообщение в консоль (никаких падений приложения).
 *
 * <p>Используется в системе для простого хранения данных без базы:
 * бренды, категории, пользователи, товары и всё остальное.
 *
 * @author
 *     Данила Сухов
 * @version 1.0
 */
public class FileStorage {
    @SuppressWarnings("unchecked")
    public static <T> Collection<T> loadCollection(String filename) {
        File file = new File(filename);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Collection<T>) ois.readObject();
        } catch (Exception e) {
            System.out.println("Ошибка загрузки коллекции из файла " + filename + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public static <T> void saveCollection(String filename, Collection<T> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(data);
        } catch (IOException e) {
            System.out.println("Ошибка сохранения коллекции в файл " + filename + ": " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> loadMap(String filename) {
        File file = new File(filename);
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<K, V>) ois.readObject();
        } catch (Exception e) {
            System.out.println("Ошибка загрузки карты из файла " + filename + ": " + e.getMessage());
            return new HashMap<>();
        }
    }

    public static <K, V> void saveMap(String filename, Map<K, V> map) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(map);
        } catch (IOException e) {
            System.out.println("Ошибка сохранения карты в файл " + filename + ": " + e.getMessage());
        }
    }
}
