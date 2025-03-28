package spot.spot.global.stomp;

import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class ActiveSessionTracker {
    private final ConcurrentHashMap<String, Boolean> activeSessions = new ConcurrentHashMap<>();

    public void addSession(String sessionId) {
        activeSessions.put(sessionId, true);
    }

    public void removeSession(String sessionId) {
        activeSessions.remove(sessionId);
    }

    public boolean isSessionActive(String sessionId) {
        return activeSessions.containsKey(sessionId);
    }
}
