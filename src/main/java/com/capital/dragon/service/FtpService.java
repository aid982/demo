
package com.capital.dragon.service;

import com.jcraft.jsch.*;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by osetskiy on 11.01.2017.
 */
@Component
@PropertySource("classpath:ftp.properties")
public class FtpService {
    @Value("${ftp.host}")
    private String host;
    @Value("${password}")
    private String ftpPassword;

    public FtpService() {
    }

    @Value("${ftp.user}")

    private String ftpUser;
    @Value("${ftp.path}")
    private String ftpPath;

    private static final Logger log = LoggerFactory.getLogger(FtpService.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 60000)
    public void ftpGet() {
        JSch jsch = new JSch();
        Date curDate = new Date();
        try {

            String knownHostsFilename = "/home/username/.ssh/known_hosts";
            jsch.setKnownHosts(knownHostsFilename);

            Session session = jsch.getSession(ftpUser, host);
            session.setPassword(ftpPassword);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();

            ChannelSftp sftpChannel = (ChannelSftp) channel;

            Vector<ChannelSftp.LsEntry> obj = sftpChannel.ls("//");
            File folder = new File(ftpPath);
            File[] listOfFiles = folder.listFiles();
            System.out.println("List for downloaded files 1" + listOfFiles);
            List<String> listOfDownloadedFiles =Arrays.stream(listOfFiles).map(s->s.getName()).collect(Collectors.toList());
            System.out.println("List for downloaded files 2" + listOfDownloadedFiles);

            for (int i = 0; i < obj.size(); i++) {
                if(!listOfDownloadedFiles.contains(obj.get(i).getFilename())) {
                    if (obj.get(i).getFilename().matches(".*\\.csv")) {
                        sftpChannel.get(obj.get(i).getFilename(), ftpPath + obj.get(i).getFilename());
                        log.info("The time is {}", dateFormat.format(curDate));
                        log.info("File downloaded {}", obj.get(i).getFilename());

                    }
                }

            }


            sftpChannel.exit();
            session.disconnect();
        } catch (Exception exception) {
            exception.printStackTrace();
            exception.printStackTrace();
            log.error("Error, time :"+dateFormat.format(curDate)+" "+exception.getMessage());
            System.out.println("Login "+ftpUser);
            System.out.println("Password "+ftpPassword);
            System.out.println("ftpPath :"+ftpPath);

        }

    }
}

