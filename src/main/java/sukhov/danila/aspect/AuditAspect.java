package sukhov.danila.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import sukhov.danila.config.UserContext;
import sukhov.danila.domain.services.AuditService;

@Aspect
public class AuditAspect {
    @After("@annotation(auditAction)")
    public void logAudit(JoinPoint joinPoint, AuditAction auditAction) {
        System.out.println(">>> АСПЕКТ СРАБОТАЛ: " + auditAction.value());
        String username = getCurrentUsername();
        String action = auditAction.value();
        new AuditService().log(username, action);
    }

    private String getCurrentUsername() {
        var user = UserContext.getCurrentUser();
        return user != null ? user.getUsername() : "anonymous";
    }
}
