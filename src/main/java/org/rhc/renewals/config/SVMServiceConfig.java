package org.rhc.renewals.config;

/**
 * Created by nbalkiss on 5/11/17.
 */
// TODO Remove default constructor
// TODO Add fields constructor with protected access
// TODO Remove setters
public class SVMServiceConfig {
    private String url;
    private String username;
    private String password;
    private String token;
    private int timeout = 1000;
    private int retryTimes = 0;
    private int delay = 0;

    public SVMServiceConfig(String url, String username, String password, String token, int timeout, int retryTimes, int delay){
        this.url=url;
        this.username=username;
        this.password=password;
        this.token=token;
        this.timeout=timeout;
        this.retryTimes=retryTimes;
        this.delay=delay;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getTimeout() {
        return timeout;
    }

    public String getToken() {
        return token;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public int getDelay() {
        return delay;
    }

    @Override
    public String toString() {
        return "SVMServiceConfig{" +
                "url='" + url + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", token='" + token + '\'' +
                ", timeout=" + timeout +
                ", retryTimes=" + retryTimes +
                ", delay=" + delay +
                '}';
    }
}
