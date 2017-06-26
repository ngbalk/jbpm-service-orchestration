package org.rhc.workflow.config;

/**
 * Created by nbalkiss on 5/11/17.
 */

public class SVMServiceConfig {
    private String name;
    private String url;
    private String username;
    private String password;
    private String token;
    private int timeout;
    private int retryTimes;

    protected SVMServiceConfig(String name, String url, String username, String password, String token, int timeout, int retryTimes){
        this.name=name;
        this.url=url;
        this.username=username;
        this.password=password;
        this.token=token;
        this.timeout=timeout;
        this.retryTimes=retryTimes;
    }

    public String getName(){
        return name;
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

    @Override
    public String toString() {
        return "SVMServiceConfig{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", token='" + token + '\'' +
                ", timeout=" + timeout +
                ", retryTimes=" + retryTimes +
                '}';
    }
}
