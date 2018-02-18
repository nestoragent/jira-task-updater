package com.jira.updater.lib.util;

import java.util.Locale;

/**
 * Created by SBT-Velichko-AA on 09.03.2016.
 */
public class OsCheck {

    // cached result of OS detection
    protected static OSType detectedOS;

    public enum OSType {
        Windows, MacOS, Linux, Other
    };

    public OSType getOperationSystemType () {
        if (detectedOS == null) {
            String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
            if ((OS.contains("mac")) || (OS.contains("darwin"))) {
                detectedOS = OSType.MacOS;
            } else if (OS.contains("win")) {
                detectedOS = OSType.Windows;
            } else if (OS.contains("nux")) {
                detectedOS = OSType.Linux;
            } else {
                detectedOS = OSType.Other;
            }
        }
        return detectedOS;
    }

    public String getArchType () {
        String wow64Arch = null;
        String arch = null;
        String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        if ((OS.contains("mac")) || (OS.contains("darwin")) || OS.contains("win")) {
            arch = System.getenv("PROCESSOR_ARCHITECTURE");
            wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");
            return arch.endsWith("64")
                    || wow64Arch != null && wow64Arch.endsWith("64")
                    ? "64" : "32";
        } else if (OS.contains("nux")) {
            wow64Arch = System.getProperty("sun.arch.data.model");
            return wow64Arch != null && wow64Arch.endsWith("64")
                    ? "64" : "32";
        }
        return null;
    }
}
