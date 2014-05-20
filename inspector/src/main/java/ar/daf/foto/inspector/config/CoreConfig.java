package ar.daf.foto.inspector.config;

import java.io.File;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.crsh.spring.SpringBootstrap;
import org.crsh.spring.SpringWebBootstrap;
import org.hibernate.SessionFactory;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.data.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import ar.daf.foto.inspector.scanner.DirectoryScanner;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

@Configuration
@ComponentScan(basePackages={"ar.daf.foto.inspector"})
@EnableAutoConfiguration(exclude={JpaBaseConfiguration.class, HibernateJpaAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class, DataSourceAutoConfiguration.class, MessageSourceAutoConfiguration.class, AopAutoConfiguration.class, JmxAutoConfiguration.class})
public class CoreConfig {
	
	private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(getClass());

	private Environment env;
	
	@Autowired
	public void setEnviroment(Environment env) {
		this.env = env;
		String homePath = System.getProperty("user.home");
		String dirConfig = env.getProperty("inspector.dirConfig");
		File file = new File(homePath+File.separator+dirConfig);
		if (!file.exists()) {
			file.mkdir();
		}
		LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
		
		Logger rootLogger = loggerContext.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
		RollingFileAppender<ILoggingEvent> appenderFile = (RollingFileAppender<ILoggingEvent>)rootLogger.getAppender("file");
		appenderFile.stop();
		
		TimeBasedRollingPolicy<?> rolling = ((TimeBasedRollingPolicy<?>)appenderFile.getRollingPolicy());
		rolling.stop();
		
		String logFileName = appenderFile.getFile();
		String logFileNamePattern = rolling.getFileNamePattern();
		Integer maxHistory = env.getProperty("inspector.logFiles.maxHistory", Integer.class);
		appenderFile.setFile(file.toString()+File.separator+logFileName);
		rolling.setFileNamePattern(file.toString()+File.separator+logFileNamePattern);
		rolling.setMaxHistory(maxHistory.intValue());
		
		rolling.start();
		appenderFile.start();
	}
	
	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public DataSource dataSource() throws Exception {
		@SuppressWarnings("serial")
		Properties dbcpProps = new Properties() {
			{
				setProperty("url", env.getProperty("spring.datasource.url"));
				setProperty("username", env.getProperty("spring.datasource.username"));
				setProperty("pasword", env.getProperty("spring.datasource.password"));
				setProperty("driverClassName", env.getProperty("spring.datasource.driverClassName"));
				setProperty("defaultTransactionInsolation", env.getProperty("spring.datasource.TransactionInsolation"));
				setProperty("initialSize", env.getProperty("spring.datasource.initialSize"));
				setProperty("maxTotal", env.getProperty("spring.datasource.maxTotal"));
				setProperty("maxIdle", env.getProperty("spring.datasource.maxIdle"));
				setProperty("minIdle", env.getProperty("spring.datasource.minIdle"));
				setProperty("maxWaitMillis", env.getProperty("spring.datasource.maxWaitMillis"));
			}
		};
		BasicDataSource dataSource = BasicDataSourceFactory.createDataSource(dbcpProps);
		return dataSource;
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	@Autowired
	public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
		@SuppressWarnings("serial")
		Properties hibernateProps = new Properties() {
			{
				setProperty("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.properties.hibernate.hbm2ddl.auto"));
				setProperty("hibernate.dialect", env.getProperty("spring.jpa.properties.hibernate.dialect"));
				setProperty("hibernate.cache.provider_class", "spring.jpa.properties.hibernate.cache.provider_class");
				setProperty("hibernate.show_sql", "spring.jpa.properties.hibernate.show_sql");
				setProperty("hibernate.globally_quoted_identifiers", "spring.jpa.properties.hibernate.globally_quoted_identifiers");
			}
		};

		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(dataSource);
		sessionFactory.setPackagesToScan("ar.daf.foto.inspector.model");
		sessionFactory.setHibernateProperties(hibernateProps);
		return sessionFactory;
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	@Autowired
	public HibernateTransactionManager transactionManager(
			SessionFactory sessionFactory) {
		HibernateTransactionManager txManager = new HibernateTransactionManager();
		txManager.setSessionFactory(sessionFactory);
		return txManager;
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}
	
//	@Bean
//	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public SpringBootstrap crshBootStrap() {
		@SuppressWarnings("serial")
		Properties crshProps = new Properties() {
			{
				setProperty("crash.vfs.refresh_period", env.getProperty("crash.vfs.refresh_period"));
				setProperty("crash.ssh.port", env.getProperty("crash.ssh.port"));
				setProperty("crash.ssh.auth-timeout", env.getProperty("crash.ssh.auth-timeout"));
				setProperty("crash.ssh.idle-timeout", env.getProperty("crash.ssh.idle-timeout"));
				setProperty("crash.auth", env.getProperty("crash.auth"));
				setProperty("crash.auth.simple.username", env.getProperty("crash.auth.simple.username"));
				setProperty("crash.auth.simple.password", env.getProperty("crash.auth.simple.password"));
			}
		};
		SpringBootstrap result = new SpringWebBootstrap();
		result.setConfig(crshProps);
		return result;
	}
	
	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	@Autowired
	public MethodInvokingJobDetailFactoryBean jodDetailScanFactory(DirectoryScanner scanner) {
		MethodInvokingJobDetailFactoryBean jobDetailFactory = new MethodInvokingJobDetailFactoryBean();
		jobDetailFactory.setTargetObject(scanner);
		jobDetailFactory.setTargetMethod("scan");
		jobDetailFactory.setConcurrent(false);
		return jobDetailFactory;
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	@Autowired
	public SimpleTriggerFactoryBean simpleTriggerScanFactory(JobDetail jobDetail) {
		SimpleTriggerFactoryBean simpleTriggerFactory = new SimpleTriggerFactoryBean();
		simpleTriggerFactory.setStartDelay(env.getProperty("inspector.scanning.delayInterval", Integer.class));
		simpleTriggerFactory.setRepeatInterval(env.getProperty("inspector.scanning.repeatInterval", Integer.class));
		simpleTriggerFactory.setJobDetail(jobDetail);
		return simpleTriggerFactory;
	}
	
	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	@Autowired
	public SchedulerFactoryBean schedulerFactory(Trigger trigger) {
		SchedulerFactoryBean result = new SchedulerFactoryBean();
		result.setTriggers(trigger);
		return result;
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public ObjectMapper jsonMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JodaModule());
		mapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS , false);
		return mapper;
	}
}