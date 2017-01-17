package com.capital.dragon;

import com.capital.dragon.service.FtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.ApplicationContext;

import java.lang.management.ManagementFactory;

@SpringBootApplication
@EnableScheduling
public class FtpApplication {

	private static final Logger logger = LoggerFactory.getLogger(FtpService.class);
	@Autowired
	FtpService ftpService;

	private static ApplicationContext applicationContext = null;



	public static void main(String[] args) {
		String mode = args != null && args.length > 0 ? args[0] : null;

		if (logger.isDebugEnabled()) {
			logger.debug("PID:" + ManagementFactory.getRuntimeMXBean().getName() + " Application mode:" + mode + " context:" + applicationContext);
		}
		if (applicationContext != null && mode != null && "stop".equals(mode)) {
			System.exit(SpringApplication.exit(applicationContext, new ExitCodeGenerator() {
				@Override
				public int getExitCode() {
					return 0;
				}
			}));
		}
		else {
			SpringApplication app = new SpringApplication(FtpApplication.class);
			applicationContext = app.run(args);
			if (logger.isDebugEnabled()) {
				logger.debug("PID:" + ManagementFactory.getRuntimeMXBean().getName() + " Application started context:" + applicationContext);
			}
		}
	}

}
