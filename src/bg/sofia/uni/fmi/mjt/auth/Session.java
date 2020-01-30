package bg.sofia.uni.fmi.mjt.auth;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Session {

    private final String userName;
    private final String id;
    private OffsetDateTime timeToLive;

    public Session(String userName, int timeToLive) {

        this.userName = userName;
        this.id = UUID.randomUUID().toString();
        this.timeToLive = OffsetDateTime.now().plusSeconds(timeToLive);
    }

    public boolean hasSessionExpired() {

        return timeToLive.isBefore(OffsetDateTime.now());
    }

    public String getUserName() {

        return userName;
    }

    public String getID() {

        return id;
    }

    public OffsetDateTime getTimeToLive() {

        return timeToLive;
    }
}
