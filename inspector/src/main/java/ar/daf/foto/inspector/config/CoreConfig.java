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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import ar.daf.foto.inspector.Main;
import ar.daf.foto.inspector.model.Album;
import ar.daf.foto.inspector.scanner.DirectoryScanner;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

@Configuration
@ComponentScan(basePackageClasses={Main.class})
@EnableAutoConfiguration(exclude={JpaBaseConfiguration.class, HibernateJpaAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class, DataSourceAutoConfiguration.class, MessageSourceAutoConfiguration.class, AopAutoConfiguration.class, JmxAutoConfiguration.class})
public class CoreConfig {
	
	private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(getClass());

	private Environment env;
	
	@Autowired
	public void setEnviroment(Environment env) {
		this.env = env;
		/*
		 * Andre: Aca se determina en la variable homePath cual es el path donde se va a buscar 
		 * la carpeta oculta con los archivos de configuracion de la aplicacion (creandola si no existe).
		 * De la forma que esta echo busca el home del usuario que ejecute la aplicacion, mediante 
		 * System.getProperty("user.home").
		 * 
		 * Por lo que contaste en tu caso efectivament el tomcat que te configuraron seguro se levanta con
		 * un usuario ficticio (asumo que tomcat) que no tiene permiso para hacer macanas en el sistema, 
		 * y dado que no se espera que algun usuario lo use para loguearse, seguro el home apunta a 
		 *  /dev/null y por eso no te deja crear carpetas ni archivos dentro de el.
		 *  
		 * Tiene sentido crear esa carpeta de configuracion si es una aplicacion que la ejecuta un usuario, 
		 * que es como la habia pensado yo inicialmente. Pero en tu caso, como sera una aplicacion web
		 * el archivo de configuracion 'inspectorConfig' yo lo ubicaria dentro de la carpeta de resources de 
		 * la aplicacion (a la misma altura donde esta el arhivo application.propertes).
		 * Y en ese caso las lineas:
		 * 
		 *   String homePath = System.getProperty("user.home");
		 *   String dirConfig = env.getProperty("inspector.dirConfig");
		 *   File file = new File(homePath+File.separator+dirConfig);
		 *   if (!file.exists()) {
		 *   	file.mkdir();
		 *   }
		 *   
		 * Habria que reemplazarla por:
		 *  
		 *   ClassPathResource fileConfigResource = new ClassPathResource("classpath://"+env.getProperty("inspector.fileConfigName"));
		 *   String homePath = fileConfigResource.getFile().getCanonicalPath(); 
         *
		 * Fijate que en este lugar se busca el path UNICAMENTE para crear la carpeta si no existe.
		 * Si la ubicas dentro de resource, eso no tendria sentido asi que no hay que hacer nada solo comentar codigo.
		 * 
		 * Donde si se usa busca y carga el archivo es en la clase DirectoryScannerImpl.java
		 * Revisa el comentario que te dejo en esa clase.
		 * 
		 * Comento a continuacion las lineas que hay que sacar.
		 */
//		String homePath = System.getProperty("user.home");
//		String dirConfig = env.getProperty("inspector.dirConfig");
//		File file = new File(homePath+File.separator+dirConfig);
//		if (!file.exists()) {
//			file.mkdir();
//		}
		LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
		
		Logger rootLogger = loggerContext.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
		RollingFileAppender<ILoggingEvent> appenderFile = (RollingFileAppender<ILoggingEvent>)rootLogger.getAppender("file");
		appenderFile.stop();
		
		TimeBasedRollingPolicy<?> rolling = ((TimeBasedRollingPolicy<?>)appenderFile.getRollingPolicy());
		rolling.stop();

		/*
		 * El codigo que sigue modifica la configuracion del logguer para que genere el archivo de log
		 * externo dentro de la carpeta de configuracion de la aplicacion.
		 * Al no existir dicha carpeta ahora, estando el archivo en resources, NO ES COVNENIENTE
		 * que el arhivo de logs lo genere en resources.
		 * Sino mas bien deberia ir (en caso de querer generar un archivo de log propio para la aplicacion)
		 * en algun path PREDEFINIDO DONDE DEL TOMCAT TENGA PERMISO DE ESCRITURA. Por ejemplo en:
		 *   /var/log/inspector.log
		 * Pero ese PATH lo tenes que pedir a quien administra el servidor. 
		 * Y para setearlo mejor seria ponerlo directamente en el arhivo logback.xml
		 * Por lo que todo lo que sigue habria que sacarlo. Yo lo comento.
		 */
//		String logFileName = appenderFile.getFile();
//		String logFileNamePattern = rolling.getFileNamePattern();
//		Integer maxHistory = env.getProperty("inspector.logFiles.maxHistory", Integer.class);
//		appenderFile.setFile(file.toString()+File.separator+logFileName);
//		rolling.setFileNamePattern(file.toString()+File.separator+logFileNamePattern);
//		rolling.setMaxHistory(maxHistory.intValue());
//		
//		rolling.start();
//		appenderFile.start();
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
				setProperty("hibernate.cache.provider_class", env.getProperty("spring.jpa.properties.hibernate.cache.provider_class"));
				setProperty("hibernate.show_sql", env.getProperty("spring.jpa.properties.hibernate.show_sql"));
//				setProperty("hibernate.globally_quoted_identifiers", env.getProperty("spring.jpa.properties.hibernate.globally_quoted_identifiers"));
			}
		};

		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(dataSource);
		sessionFactory.setPackagesToScan(Album.class.getPackage().getName());
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