package cz.nos.twitterbot;

import java.time.LocalDateTime;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Config {
    
    private static final String DEFAULT_VALUE = "";

    private static final String BEARER_TOKEN = "bearer.token";
    
    private static final String ACCESS_TOKEN = "access.token";
    
    private static final String ACCESS_TOKEN_SECRET = "access.token.secret";
    
    private static final String CLIENT_ID = "client.id";
    
    private static final String CLIENT_SECRET = "client.secret";
    
    private static final String CONSUMER_KEY = "consumer.key";
    
    private static final String CONSUMER_KEY_SECRET = "consumer.key.secret";
    
    private static final String USER_ID = "user.id";
    private static final String USER_DEFAULT_ID = "1622552806790930432";
    
    private static final String DB_URL = "db.url";
    private static final String DB_URL_DEFAULT = "resources/database/twitterbot.db";
    
    private static final String LAST_CHECKED_DATE = "last.checked.date";
    
    
    private final String bearerToken;
    private final String accessToken;
    private final String accessTokenSecret;
    private final String clientId;
    private final String clientSecret;
    private final String consumerKey;
    private final String consumerKeySecret;
    private final String userId;
    
    private String dbUrl;
    
    private String lastCheckedDate;
    
    Preferences prefs = Preferences.userRoot().node("prectimito-twitter-bot");
    
    public Config() throws BackingStoreException {
        
        bearerToken = prefs.get(BEARER_TOKEN, DEFAULT_VALUE);
        accessToken = prefs.get(ACCESS_TOKEN, DEFAULT_VALUE);
        accessTokenSecret = prefs.get(ACCESS_TOKEN_SECRET, DEFAULT_VALUE);
        clientId = prefs.get(CLIENT_ID, DEFAULT_VALUE);
        clientSecret = prefs.get(CLIENT_SECRET, DEFAULT_VALUE);
        consumerKey = prefs.get(CONSUMER_KEY, DEFAULT_VALUE);
        consumerKeySecret = prefs.get(CONSUMER_KEY_SECRET, DEFAULT_VALUE);
        userId = prefs.get(USER_ID, USER_DEFAULT_ID);

        dbUrl = prefs.get(DB_URL, DEFAULT_VALUE);
        
        lastCheckedDate = prefs.get(LAST_CHECKED_DATE, DEFAULT_VALUE);
        
        if (bearerToken.isEmpty() ) {
            prefs.put(BEARER_TOKEN, DEFAULT_VALUE);
            prefs.flush();
        }
        
        if (accessToken.isEmpty() ) {
            prefs.put(ACCESS_TOKEN, DEFAULT_VALUE);
            prefs.flush();
        }
        
        if (accessTokenSecret.isEmpty() ) {
            prefs.put(ACCESS_TOKEN_SECRET, DEFAULT_VALUE);
            prefs.flush();
        }
        
        if (consumerKey.isEmpty() ) {
            prefs.put(CONSUMER_KEY, DEFAULT_VALUE);
            prefs.flush();
        }
        
        if (consumerKeySecret.isEmpty() ) {
            prefs.put(CONSUMER_KEY_SECRET, DEFAULT_VALUE);
            prefs.flush();
        }
        
        if (clientId.isEmpty() ) {
            prefs.put(CLIENT_ID, DEFAULT_VALUE);
            prefs.flush();
        }
        
        if (clientSecret.isEmpty() ) {
            prefs.put(CLIENT_SECRET, DEFAULT_VALUE);
            prefs.flush();
        }
        
        if (userId.isEmpty() ) {
            prefs.put(USER_ID, USER_DEFAULT_ID);
            prefs.flush();
        }

        if (dbUrl.isEmpty() ) {
            prefs.put(DB_URL, DB_URL_DEFAULT);
            prefs.flush();
        }

        if (lastCheckedDate.isEmpty() ) {
            prefs.put(LAST_CHECKED_DATE, LocalDateTime.now().toString());
            prefs.flush();
        }
    }

    public String getBearerToken() {
		return bearerToken;
	}

    public String getAccessToken() {
		return accessToken;
	}

    public String getAccessTokenSecret() {
		return accessTokenSecret;
	}

    public String getClientId() {
 		return clientId;
 	}

     public String getClientSecret() {
 		return clientSecret;
 	}

     public String getConsumerKey() {
 		return consumerKey;
 	}

     public String getConsumerKeySecret() {
 		return consumerKeySecret;
 	}

     public String getUserId() {
 		return userId;
 	}

 	public String getDbUrl() {
		return dbUrl;
	}

 	public LocalDateTime getLastCheckedDate() {
 		return LocalDateTime.parse(lastCheckedDate);
	}

 	public void setLastCheckedDate(LocalDateTime localDateTime) throws BackingStoreException {
 		prefs.put(LAST_CHECKED_DATE, localDateTime.toString());
 		prefs.flush();
 	}
}