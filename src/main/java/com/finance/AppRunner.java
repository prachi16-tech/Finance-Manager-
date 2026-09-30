package com.finance;

import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;

/**
 * Embedded Server Runner for Instant 1-Click Launch
 */
public class AppRunner {

    public static void main(String[] args) throws Exception {
        int port = 8080;
        String portEnv = System.getenv("PORT");
        if (portEnv != null) {
            try {
                port = Integer.parseInt(portEnv);
            } catch (NumberFormatException ignored) {}
        }

        String webappDirLocation = "src/main/webapp";
        File webappDir = new File(webappDirLocation);
        if (!webappDir.exists()) {
            webappDir = new File("PersonalFinanceManager/src/main/webapp");
        }

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.getConnector(); // Initialize default connector

        String baseDir = new File("target/tomcat").getAbsolutePath();
        new File(baseDir).mkdirs();
        tomcat.setBaseDir(baseDir);

        StandardContext ctx = (StandardContext) tomcat.addWebapp("", webappDir.getAbsolutePath());
        ctx.setReloadable(true);

        // Declare an alternative location for your "WEB-INF/classes" dir
        File additionWebInfClasses = new File("target/classes");
        WebResourceRoot resources = new StandardRoot(ctx);
        if (additionWebInfClasses.exists()) {
            resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                    additionWebInfClasses.getAbsolutePath(), "/"));
        }
        ctx.setResources(resources);

        System.out.println("=================================================================");
        System.out.println("   PERSONAL FINANCE MANAGER - SERVER STARTING");
        System.out.println("   Direct Link: http://localhost:" + port + "/");
        System.out.println("   Login with: mayur@example.com / Password@123");
        System.out.println("=================================================================");

        tomcat.start();
        tomcat.getServer().await();
    }
}
