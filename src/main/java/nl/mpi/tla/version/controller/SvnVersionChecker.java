/*
 * Copyright (C) 2014 The Language Archive, Max Planck Institute for Psycholinguistics
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package nl.mpi.tla.version.controller;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import org.apache.maven.plugin.MojoExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since May 2, 2014 2:01:01 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class SvnVersionChecker implements VcsVersionChecker {

    final private Logger logger = LoggerFactory.getLogger(getClass());
    final private CommandRunner commandRunner;

    public SvnVersionChecker(CommandRunner commandRunner) {
        this.commandRunner = commandRunner;
    }

    public int getBuildNumber(boolean verbose, File projectDirectory, String moduleName) throws MojoExecutionException {
        try {
            Scanner outputScanner = new Scanner(commandRunner.runCommand(new String[]{"svn", "info", moduleName}, projectDirectory));
            outputScanner.useDelimiter(":\\s|\n");
            while (outputScanner.hasNext()) {
                if (outputScanner.next().equals("Revision")) {
                    int buildNumber = outputScanner.nextInt();
                    return buildNumber;
                }
            }
        } catch (IOException exception) {
            throw new MojoExecutionException("Failed to get the svn last commit id.", exception);
        }
        throw new MojoExecutionException("Failed to get the svn last commit id.");
    }

    public String getLastCommitDate(boolean verbose, File projectDirectory, String moduleName) throws MojoExecutionException {
        try {
            //System.out.println("getLastCommitDate");
            //System.out.println("running svn info " + moduleName + " in " + projectDirectory);
            Scanner outputScanner = new Scanner(commandRunner.runCommand(new String[]{"svn", "info", moduleName}, projectDirectory));
            outputScanner.useDelimiter(":\\s|\n");
            while (outputScanner.hasNext()) {
                final String next = outputScanner.next();
                //System.out.println("next:" + next);
                if (next.equals("Last Changed Date")) {
                    String lastCommitDate = outputScanner.next();
                    return lastCommitDate;
                }
            }
        } catch (IOException exception) {
            throw new MojoExecutionException("Failed to get the svn last commit date.", exception);
        }
        throw new MojoExecutionException("Failed to get the svn last commit date.");
    }
}
