package sukhov.danila.domain.helpers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * Утилитарный класс для работы с паролями.
 * <p>
 * Содержит методы для хеширования паролей с использованием алгоритма SHA-256
 * и проверки корректности введенного пароля при входе в систему.
 * </p>
 *
 * <p>Используется в сервисе {@link sukhov.danila.domain.services.AuthService}</p>
 *
 * @author Данила
 * @version 1.0
 */
public class PasswordUtil {
    /**
     * Выполняет хеширование строки пароля с помощью алгоритма SHA-256.
     *
     * @param password исходный пароль в виде строки
     * @return строка, представляющая хеш пароля в шестнадцатеричном формате
     * @throws RuntimeException если алгоритм SHA-256 не найден в среде выполнения
     */
    public static String hashPassword(String password){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder stringBuilder = new StringBuilder();
            for(byte b : hashBytes){
                stringBuilder.append(String.format("%02x", b));
            }
            return  stringBuilder.toString();

        }catch(NoSuchAlgorithmException e){
            throw new RuntimeException("При хешировании пароля произошла ошибка!", e);
        }
    }
    /**
     * Проверяет, совпадает ли введённый пользователем пароль с ранее сохранённым хешем.
     *
     * @param rawPassword     исходный пароль, введённый пользователем
     * @param hashedPassword  сохранённый ранее хеш пароля
     * @return {@code true}, если хеши совпадают, иначе {@code false}
     */
    public static boolean successSignIn(String rawPassword, String hashedPassword){
        return  hashPassword(rawPassword).equals(hashedPassword);
    }
}
