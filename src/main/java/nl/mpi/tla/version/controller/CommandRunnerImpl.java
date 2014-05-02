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
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is intended to allow mocking in the unit tests for the
 * VcsVersionChecker implementations.
 *
 * @since May 2, 2014 2:19:41 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class CommandRunnerImpl implements CommandRunner {

    final private Logger logger = LoggerFactory.getLogger(getClass());

    public InputStream runCommand(String[] commandArray, File workingDirectory) throws IOException {
        logger.info(commandArray[0]);
        logger.info(" ");
        logger.info(commandArray[1]);
        logger.info(" ");
        logger.info(commandArray[2]);
        logger.info(" ");
        logger.info(commandArray[3]);
        logger.info(" ");
        logger.info(workingDirectory.toString());
        Process logProcess = Runtime.getRuntime().exec(commandArray, null, workingDirectory);
        return logProcess.getInputStream();
    }
}