package sukhov.danila.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import sukhov.danila.config.UserContext;
import sukhov.danila.domain.services.AuditService;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {
    private final AuditService auditService;

    @After("@annotation(auditAction)")
    public void logAudit(JoinPoint joinPoint, AuditAction auditAction) {
        String username = getCurrentUsername();
        auditService.log(username, auditAction.value());
    }

    private String getCurrentUsername() {
        var user = UserContext.getCurrentUser();
        return user != null ? user.getUsername() : "anonymous";
    }
}
