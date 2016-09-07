/*
 * JBILLING CONFIDENTIAL
 * _____________________
 *
 * [2003] - [2012] Enterprise jBilling Software Ltd.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Enterprise jBilling Software.
 * The intellectual and technical concepts contained
 * herein are proprietary to Enterprise jBilling Software
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden.
 */

package com.sapienter.jbilling.server.mediation.sourcereader;

import junit.framework.Assert;
import org.junit.BeforeClass;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

/**
 * Created by marcomanzi on 1/27/14.
 */
@Test(groups = { "integration", "mediation" })
public class FtpRemoteFileRetrieverTest {

    private static String HOME_DIR = "/home/";
    private static String FILE = HOME_DIR + "TestFile";
    private String currentTestFile = null;

    private static String CONTENTS = "This is a test Content";
    private FakeFtpServer fakeFtpServer;

    private void createFakeFtpServer() throws Exception {
        if (fakeFtpServer != null) {
            fakeFtpServer.stop();
        }
        if (currentTestFile != null) {
            fakeFtpServer = new FakeFtpServer();
            fakeFtpServer.setServerControlPort(9187);
            FileSystem fileSystem = new UnixFakeFileSystem();
            fileSystem.add(new DirectoryEntry(HOME_DIR));
            fileSystem.add(new FileEntry(currentTestFile, CONTENTS));
            fakeFtpServer.setFileSystem(fileSystem);
            UserAccount userAccount = new UserAccount("user", "password", HOME_DIR);
            fakeFtpServer.addUserAccount(userAccount);
            fakeFtpServer.start();
        }
    }

    @Test
    public void testFtpCopyToLocal() throws Exception {
        currentTestFile = testFile("1");
        createFakeFtpServer();
        ApplicationContext context = new ClassPathXmlApplicationContext("springintegration-infrastructure-test.xml");

        FtpFileRetriever ftpFileRetriever = (FtpFileRetriever) context.getBean("ftpRemoteFileRetriever");

        ftpFileRetriever.setLocalPath(getCurrentFilePath());
        ftpFileRetriever.copyToLocalFileSystem();

        checkFileWasCopied(ftpFileRetriever, currentTestFile);
    }

    @Test
    public void testFtpCopyToLocalFromServerInMethod() throws Exception {
        currentTestFile = testFile("2");
        createFakeFtpServer();
        ApplicationContext context = new ClassPathXmlApplicationContext("springintegration-infrastructure-test.xml");

        FtpFileRetriever ftpFileRetriever = (FtpFileRetriever) context.getBean("ftpRemoteFileRetriever");

        DefaultFtpSessionFactory ftpSessionFactory = (DefaultFtpSessionFactory) context.getBean("ftpTestSessionFactoryClean");
        ftpSessionFactory.setHost("localhost");
        ftpSessionFactory.setPort(9187);
        ftpSessionFactory.setUsername("user");
        ftpSessionFactory.setPassword("password");
        ftpFileRetriever.setLocalPath(getCurrentFilePath());
        ftpFileRetriever.copyToLocalFileSystemFrom(ftpSessionFactory);

        checkFileWasCopied(ftpFileRetriever, currentTestFile);
    }

    @Test
    public void testFtpCopyWithTempFolder() throws Exception {
        currentTestFile = testFile("3");
        createFakeFtpServer();
        ApplicationContext context = new ClassPathXmlApplicationContext("springintegration-infrastructure-test.xml");

        FtpFileRetriever ftpFileRetriever = (FtpFileRetriever) context.getBean("ftpRemoteFileRetriever");
        ftpFileRetriever.setTempFolder("testTemp");

        ftpFileRetriever.setLocalPath(getCurrentFilePath());
        ftpFileRetriever.copyToLocalFileSystem();

        checkFileWasCopied(ftpFileRetriever, currentTestFile);
    }

    private String testFile(String n) {
        return FILE + n + ".dat";
    }

    private String getCurrentFilePath() {
        URL resource = FtpRemoteFileRetrieverTest.class.getClassLoader().getResource(
                "com/sapienter/jbilling/server/mediation/sourcereader/FtpRemoteFileRetrieverTest.class");
        return resource.getPath().substring(0, resource.getPath().lastIndexOf( "/") + 1);
    }

    private void checkFileWasCopied(FtpFileRetriever ftpFileRetriever, String testFileName) throws IOException {
        File fileCopied = new File(getUrlStringOfFileCopied(ftpFileRetriever, testFileName.substring(testFileName.lastIndexOf("/") + 1)));
        Assert.assertTrue(fileCopied.exists());
        BufferedReader reader = new BufferedReader(new FileReader(fileCopied));
        Assert.assertEquals(CONTENTS, reader.readLine());

        reader.close();
        fileCopied.deleteOnExit();
    }

    private String getUrlStringOfFileCopied(FtpFileRetriever ftpFileRetriever, String testFileName) throws IOException {
        return ftpFileRetriever.getLocalPath() + testFileName + ".txt";
    }

}
