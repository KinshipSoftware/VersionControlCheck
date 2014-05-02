/**
 * Copyright (C) 2014 The Language Archive, Max Planck Institute for
 * Psycholinguistics
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package nl.mpi.tla.version.controller;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Goal which validates the major, minor and build numbers of modules in a given
 * parent pom. The build number is derived from either the SVN revision number
 * or the changes since last branch in GIT. The resulting properties are also
 * stored in properties files for use in the application at run time. Properties
 * are also provided for each module to be used in setting the dependencies
 * between the various modules.
 *
 * @since Apr 30, 2014 10:09:52 AM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 * @goal revision
 * @phase process-sources
 */
public class VersionControlCheck extends AbstractMojo {

    enum BuildType {

        pretesting, testing, stable, SNAPSHOT
    }

    enum VcsType {

        svn, git
    }
    final private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;
    /**
     * @parameter expression="${project.build.directory}"
     * @required
     * @readonly
     */
    private File outputDirectory;
    /**
     * @parameter expression="${project.basedir}"
     * @required
     * @readonly
     */
    private File projectDirectory;
    /**
     * The modules in the reactor.
     *
     * @parameter expression="${reactorProjects}"
     * @readonly
     */
    private List<MavenProject> mavenProjects;
    /**
     * @parameter expression="${majorVersion}"
     * @required
     * @readonly
     */
    private String majorVersion;
    /**
     * @parameter expression="${minorVersion}"
     * @required
     * @readonly
     */
    private String minorVersion;
    /**
     * @parameter expression="${buildType}"
     * @required
     * @readonly
     */
    private BuildType buildType;
    /**
     * @parameter expression="${vcsType}"
     * @required
     * @readonly
     */
    private VcsType vcsType;
    /**
     * Modules which are allowed short module versions: eg 1.0 instead of
     * 1.0.0-testing.
     *
     * @parameter alias="shortVersionModules"
     * @readonly
     */
    private List<String> modulesWithShortVersion;

    /**
     * @parameter expression="${verbose}"
     * @required
     * @readonly
     */
    private boolean verbose;
    /**
     * @parameter expression="${allowSnapshots}"
     * @required
     * @readonly
     */
    private boolean allowSnapshots;
    /**
     * @parameter expression="${propertiesPrefix}"
     * @required
     * @readonly
     */
    private String propertiesPrefix;

    @Override
    public void execute() throws MojoExecutionException {
        if (vcsType == VcsType.svn) {
            throw new MojoExecutionException("SVN is not yet supported.");
        }
        if (verbose) {
            logger.info("VersionControlCheck");
            logger.info("project: " + project);
            logger.info("majorVersion: " + majorVersion);
            logger.info("minorVersion: " + minorVersion);
            logger.info("buildType: " + buildType);
            logger.info("outputDirectory: " + outputDirectory);
            logger.info("projectDirectory :" + projectDirectory);
        }
        try {
            for (MavenProject reactorProject : mavenProjects) {
                final String artifactId = reactorProject.getArtifactId();
                final String moduleVersion = reactorProject.getVersion();
                final String groupId = reactorProject.getGroupId();
                logger.info("Checking version numbers for " + artifactId);
                if (verbose) {
                    logger.info("artifactId: " + artifactId);
                    logger.info("moduleVersion: " + moduleVersion);
                    logger.info("moduleDir :" + reactorProject.getBasedir());
                }
//                for (MavenProject dependencyProject : reactorProject.getDependencies()) {
//                    logger.info("dependencyProject:" + dependencyProject.getArtifactId());
//                }
                final String expectedVersion;
                final String buildVersionString;
                final String lastCommitDate;
                if (allowSnapshots && moduleVersion.contains("SNAPSHOT")) {
                    expectedVersion = majorVersion + "." + minorVersion + "-" + buildType + "-SNAPSHOT";
                    buildVersionString = ""; //"SNAPSHOT"; it will be nice to have snapshot here but we need to update some of the unit tests first
                    lastCommitDate = "";
                } else {
                    Process logProcess = Runtime.getRuntime().exec(new String[]{"git", "log", "--pretty=format:%H", reactorProject.getBasedir().getName()}, null, projectDirectory);
                    Scanner logScanner = new Scanner(logProcess.getInputStream());
                    int lineCount = 0;
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
                    logger.info(artifactId + ".buildVersion: " + Integer.toString(lineCount));
                    Process dateProcess = Runtime.getRuntime().exec(new String[]{"git", "log", "-1", "--format=\"%ci\""}, null, reactorProject.getBasedir());
                    Scanner dateScanner = new Scanner(dateProcess.getInputStream());
                    lastCommitDate = dateScanner.nextLine();
                    System.out.println("committedDate:" + lastCommitDate);
                    if (modulesWithShortVersion != null && modulesWithShortVersion.contains(artifactId)) {
                        expectedVersion = majorVersion + "." + minorVersion;
                    } else {
                        expectedVersion = majorVersion + "." + minorVersion + "." + lineCount + "-" + buildType;
                    }
                    buildVersionString = Integer.toString(lineCount);
                }
                if (!expectedVersion.equals(moduleVersion)) {
                    logger.error("Expecting version number: " + expectedVersion);
                    logger.error("But found: " + moduleVersion);
                    logger.error("Artifact: " + artifactId);
                    throw new MojoExecutionException("The build numbers to not match for '" + artifactId + "': '" + expectedVersion + "' vs '" + moduleVersion + "'");
                }

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                final String buildDate = dateFormat.format(date);

                final String versionPropertyName = groupId + "." + artifactId + ".moduleVersion";
                logger.info("Setting property '" + versionPropertyName + "' to '" + expectedVersion + "'");
                reactorProject.getProperties().setProperty(versionPropertyName, expectedVersion);
                reactorProject.getProperties().setProperty(propertiesPrefix + ".majorVersion", majorVersion);
                reactorProject.getProperties().setProperty(propertiesPrefix + ".minorVersion", minorVersion);
                reactorProject.getProperties().setProperty(propertiesPrefix + ".buildVersion", buildVersionString);
                reactorProject.getProperties().setProperty(propertiesPrefix + ".lastCommitDate", lastCommitDate);
                reactorProject.getProperties().setProperty(propertiesPrefix + ".buildDate", buildDate);

            }
        } catch (IOException exception) {
            throw new MojoExecutionException("Failed to get the git log.", exception);
        }
    }
}
