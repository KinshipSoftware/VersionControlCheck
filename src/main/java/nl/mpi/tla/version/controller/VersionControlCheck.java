/*
 * Copyright (C) 2014 Max Planck Institute for Psycholinguistics
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package nl.mpi.tla.version.controller;

import java.io.File;
import java.io.IOException;
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

    @Override
    public void execute() throws MojoExecutionException {
        logger.info("VersionControlCheck");
        logger.info("project: " + project);
        logger.info("outputDirectory: " + outputDirectory);
        logger.info("projectDirectory :" + projectDirectory);
        try {
//        for (String moduleName : new String[]{"rabbit"}) {
            for (MavenProject reactorProject : mavenProjects) {
                logger.info("moduleId: " + reactorProject.getArtifactId());
                logger.info("moduleDir :" + reactorProject.getBasedir());
//                for (MavenProject dependencyProject : reactorProject.getDependencies()) {
//                    logger.info("dependencyProject:" + dependencyProject.getArtifactId());
//                }
                Process process = Runtime.getRuntime().exec(new String[]{"git", "log", "--pretty=oneline", reactorProject.getBasedir().getName()}, null, projectDirectory);
                Scanner scanner = new Scanner(process.getInputStream());
                int lineCount = 0;
                while (scanner.hasNext()) {
                    final String nextLine = scanner.nextLine();
                    if (nextLine.indexOf(" ") != 40) {
                        logger.info(nextLine);
//                        logger.info(nextLine.indexOf(" "));
                        throw new MojoExecutionException("Invalid git log found. Please check the project directory specified is a git repository.");
                    }
                    lineCount++;
                }
                logger.info(reactorProject.getArtifactId() + ".buildVersion: " + Integer.toString(lineCount));
                reactorProject.getProperties().setProperty(reactorProject.getArtifactId() + ".buildVersion", Integer.toString(lineCount));
            }
        } catch (IOException exception) {
            throw new MojoExecutionException("Failed to get the git log.", exception);
        }
    }
}
