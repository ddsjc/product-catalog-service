package sukhov.danila.aspect;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import sukhov.danila.config.UserContext;
import sukhov.danila.domain.services.AuditService;

@Aspect
@Component
public class AuditAspect {
    @After("@annotation(sukhov.danila.aspect.AuditAction)")
    public void logAudit(org.aspectj.lang.JoinPoint joinPoint, AuditAction auditAction) {
        String username = getCurrentUsername();
        new AuditService().log(username, auditAction.value());
    }

    private String getCurrentUsername() {
        var user = UserContext.getCurrentUser();
        return user != null ? user.getUsername() : "anonymous";
    }
}
