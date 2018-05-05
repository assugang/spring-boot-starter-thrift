package org.sg.springboot.thrift;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>Title: ThriftProperties</p>
 * <p>Description: thrift配置项</p>
 * <p>Company: www.inveno.com</p>
 *
 * @author Mr.Su
 * @date 2018/5/3
 */
@ConfigurationProperties("spring.thrift")
public class ThriftProperties {

    private String thriftClientClazzName;
    private String host;
    private Integer port;
    private Integer timeout;
    private ThriftProperties.ThreadPool threadPool;

    public String getThriftClientClazzName() {
        return thriftClientClazzName;
    }

    public void setThriftClientClazzName(String thriftClientClazzName) {
        this.thriftClientClazzName = thriftClientClazzName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public ThriftProperties.ThreadPool getThreadPool() {
        return threadPool;
    }

    public void setThreadPool(ThriftProperties.ThreadPool threadPool) {
        this.threadPool = threadPool;
    }

    public class ThreadPool {
        private int maxTotal;
        private int maxIdle;
        private int minIdle;
        private int maxWaitMillis;
        private int minEvictableIdleTimeMillis;
        private int softMinEvictableIdleTimeMillis;
        private int timeBetweenEvictionRunsMillis;

        public ThreadPool() {
        }

        public int getMaxTotal() {
            return this.maxTotal;
        }

        public void setMaxTotal(int maxTotal) {
            this.maxTotal = maxTotal;
        }

        public int getMaxIdle() {
            return this.maxIdle;
        }

        public void setMaxIdle(int maxIdle) {
            this.maxIdle = maxIdle;
        }

        public int getMinIdle() {
            return this.minIdle;
        }

        public void setMinIdle(int minIdle) {
            this.minIdle = minIdle;
        }

        public int getMaxWaitMillis() {
            return this.maxWaitMillis;
        }

        public void setMaxWaitMillis(int maxWaitMillis) {
            this.maxWaitMillis = maxWaitMillis;
        }

        public int getMinEvictableIdleTimeMillis() {
            return this.minEvictableIdleTimeMillis;
        }

        public void setMinEvictableIdleTimeMillis(int minEvictableIdleTimeMillis) {
            this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
        }

        public int getSoftMinEvictableIdleTimeMillis() {
            return this.softMinEvictableIdleTimeMillis;
        }

        public void setSoftMinEvictableIdleTimeMillis(int softMinEvictableIdleTimeMillis) {
            this.softMinEvictableIdleTimeMillis = softMinEvictableIdleTimeMillis;
        }

        public int getTimeBetweenEvictionRunsMillis() {
            return this.timeBetweenEvictionRunsMillis;
        }

        public void setTimeBetweenEvictionRunsMillis(int timeBetweenEvictionRunsMillis) {
            this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
        }
    }
}
