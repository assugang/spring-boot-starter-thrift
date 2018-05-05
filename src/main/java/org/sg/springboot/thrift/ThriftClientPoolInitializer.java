package org.sg.springboot.thrift;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Constructor;

/**
 * <p>Title: ThriftClientPoolInitializer</p>
 * <p>Description: thrift 链接池初始化</p>
 * <p>Company: www.inveno.com</p>
 *
 * @author Mr.Su
 * @date 2018/5/3
 */
@Configuration
@EnableConfigurationProperties({ThriftProperties.class})
public class ThriftClientPoolInitializer {

    private static Logger logger = LoggerFactory.getLogger(ThriftClientPoolInitializer.class);

    @Autowired
    private ThriftProperties thriftProperties;

    @Bean({"thriftClientPoolConfig"})
    public GenericObjectPoolConfig thriftClientPoolConfig() {
        logger.debug("Init thriftThreadPoolConfig.");
        GenericObjectPoolConfig thriftThreadPoolConfig = new GenericObjectPoolConfig();
        thriftThreadPoolConfig.setMaxTotal(this.thriftProperties.getThreadPool().getMaxTotal());
        thriftThreadPoolConfig.setMaxIdle(this.thriftProperties.getThreadPool().getMinIdle());
        thriftThreadPoolConfig.setMinIdle(this.thriftProperties.getThreadPool().getMinIdle());
        thriftThreadPoolConfig.setMaxWaitMillis(this.thriftProperties.getThreadPool().getMaxWaitMillis());
        thriftThreadPoolConfig.setMinEvictableIdleTimeMillis(this.thriftProperties.getThreadPool().getMinEvictableIdleTimeMillis());
        thriftThreadPoolConfig.setSoftMinEvictableIdleTimeMillis(this.thriftProperties.getThreadPool().getSoftMinEvictableIdleTimeMillis());
        thriftThreadPoolConfig.setTimeBetweenEvictionRunsMillis(this.thriftProperties.getThreadPool().getTimeBetweenEvictionRunsMillis());
        thriftThreadPoolConfig.setJmxEnabled(false);
        return thriftThreadPoolConfig;
    }

    @Bean({"thriftClientPool"})
    public GenericObjectPool<TServiceClient> thriftObjectPool(@Qualifier("thriftClientPoolConfig") GenericObjectPoolConfig thriftThreadPoolConfig,
                                                              @Qualifier("thriftClientFactory") BasePooledObjectFactory<TServiceClient> basePooledObjectFactory) {
        logger.debug("Init genericObjectPool.");
        GenericObjectPool<TServiceClient> pool = new GenericObjectPool(basePooledObjectFactory, thriftThreadPoolConfig);
        pool.setTestOnBorrow(true);
        return pool;
    }

    @Bean({"thriftClientFactory"})
    public BasePooledObjectFactory<TServiceClient> thriftClientFactory() {
        logger.debug("Init thriftClientFactory.");
        return new BasePooledObjectFactory<TServiceClient>() {
            @Override
            public TServiceClient create() throws Exception {
                TTransport transport = new TSocket(ThriftClientPoolInitializer.this.thriftProperties.getHost(), ThriftClientPoolInitializer.this.thriftProperties.getPort(), ThriftClientPoolInitializer.this.thriftProperties.getTimeout());
                TProtocol protocol = new TBinaryProtocol(new TFramedTransport(transport));
                transport.open();

                try {
                    String thriftClientClazzName = ThriftClientPoolInitializer.this.thriftProperties.getThriftClientClazzName();
                    String outerClassName = thriftClientClazzName.substring(0, thriftClientClazzName.lastIndexOf("."));
                    Class clazz = Class.forName(outerClassName);
                    Class<?>[] innerClazzArr = clazz.getDeclaredClasses();
                    Class<?> innerClazz = null;
                    for (Class<?> c : innerClazzArr) {
                        if (c.getSimpleName().equals(thriftClientClazzName.substring(thriftClientClazzName.lastIndexOf(".") + 1))) {
                            innerClazz = c;
                            break;
                        }
                    }
                    if (null == innerClazz) {
                        throw new NullPointerException("Class not found.");
                    }
                    Constructor<?> constructor = innerClazz.getConstructor(TProtocol.class);
                    return (TServiceClient)constructor.newInstance(protocol);
                } catch (Exception e) {
                    throw new IllegalArgumentException("thriftClientClazzName: " + ThriftClientPoolInitializer.this.thriftProperties.getThriftClientClazzName());
                }
            }

            @Override
            public PooledObject<TServiceClient> wrap(TServiceClient client) {
                return new DefaultPooledObject(client);
            }

            @Override
            public void destroyObject(PooledObject<TServiceClient> p) throws Exception {
                TServiceClient client = p.getObject();
                TTransport transport = client.getInputProtocol().getTransport();
                logger.debug("destroyObject.destroyObject {}...");
                transport.close();
            }

            @Override
            public boolean validateObject(PooledObject<TServiceClient> pool) {
                TServiceClient client = pool.getObject();
                TTransport transport = client.getInputProtocol().getTransport();
                logger.debug("transport.isOpen() {}...", transport.isOpen());
                return transport.isOpen();
            }

            @Override
            public void activateObject(PooledObject<TServiceClient> p) throws Exception {
                super.activateObject(p);
            }

            @Override
            public void passivateObject(PooledObject<TServiceClient> p) throws Exception {
                super.passivateObject(p);
            }
        };
    }

}
