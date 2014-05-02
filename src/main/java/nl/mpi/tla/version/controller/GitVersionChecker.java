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
 * @since May 2, 2014 2:00:25 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class GitVersionChecker implements VcsVersionChecker {

    final private Logger logger = LoggerFactory.getLogger(getClass());
    final private CommandRunner commandRunner;

    public GitVersionChecker(CommandRunner commandRunner) {
        this.commandRunner = commandRunner;
    }

    public int getBuildNumber(boolean verbose, File projectDirectory, String moduleName) throws MojoExecutionException {
        int lineCount = 0;
        try {
            Scanner logScanner = new Scanner(commandRunner.runCommand(new String[]{"git", "log", "--pretty=oneline", moduleName}, projectDirectory));
            while (logScanner.hasNext()) {
                final String nextLine = logScanner.nextLine();
                if (nextLine.indexOf(" ") != 40) {
                    if (verbose) {
                        logger.info(nextLine);
                    }
//                        logger.info(nextLine.indexOf(" "));
                    throw new MojoExecutionException("Invalid git log found. Please check the project directory specified is a git repository.");
                }
                lineCount++;
            }
        } catch (IOException exception) {
            throw new MojoExecutionException("Failed to get the git log count.", exception);
        }
        return lineCount;
    }

    public String getLastCommitDate(boolean verbose, File projectDirectory, String moduleName) throws MojoExecutionException {
        try {
            Scanner dateScanner = new Scanner(commandRunner.runCommand(new String[]{"git", "log", "-1", "--format=\"%ci\"", moduleName}, projectDirectory));
            final String lastCommitDate = dateScanner.nextLine();
            return lastCommitDate;
        } catch (IOException exception) {
            throw new MojoExecutionException("Failed to get the git last commit date.", exception);
        }
    }
}
