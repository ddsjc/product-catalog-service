package sukhov.danila.domain.services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
/**
 * Сервис для записи действий пользователей в лог.
 * <p>
 * По сути — простейший "аудит": кто, когда и что сделал в системе.
 * Пишет всё в обычный текстовый файл, без лишних наворотов.
 * </p>
 *
 * <p>Пример записи:
 * <pre>2025-11-08 -|- danila -|- Создал бренд "Nike"</pre>
 * </p>
 *
 * @author Данила Сухов
 * @version 1.0
 */
public class AuditService {
    private static final String LOG_FILE = "audit.log";

    /**
     * Записывает строку в лог: дата, пользователь и его действие.
     *
     * @param username имя пользователя, выполняющего действие
     * @param action   описание действия
     */
    public static void log(String username, String action){
        try(FileWriter fileWriter = new FileWriter(LOG_FILE, true)){
            fileWriter.write(LocalDateTime.now() + " -|- " + username + " -|- " + action + "\n");
        }catch (IOException e){
            System.out.println("Ошибка записи лога");
        }
    }
}
