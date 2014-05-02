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

import java.io.ByteArrayInputStream;
import java.io.File;
import junit.framework.TestCase;
import org.jmock.Expectations;
import static org.jmock.Expectations.returnValue;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;

/**
 *
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class SvnVersionCheckerTest extends TestCase {

    final private Mockery context = new JUnit4Mockery();
    private CommandRunner commandRunner;
    final private String mockSvnResult = "Path: arbil\n"
            + "Working Copy Root Path: /Users/petwit/Documents/svn.mpi.nl/LAT\n"
            + "URL: https://xxxxxxxxxxxx/Arbil/trunk/arbil\n"
            + "Repository Root: https://svn.mpi.nl/LAT\n"
            + "Repository UUID: xxxxxxxxxxxxx\n"
            + "Revision: 40503\n"
            + "Node Kind: directory\n"
            + "Schedule: normal\n"
            + "Last Changed Author: petwit\n"
            + "Last Changed Rev: 40230\n"
            + "Last Changed Date: 2014-03-13 14:18:11 +0100 (Thu, 13 Mar 2014)";

    @Before
    public void setUp() {
        commandRunner = context.mock(CommandRunner.class);
    }

    /**
     * Test of getBuildNumber method, of class SvnVersionChecker.
     */
    public void testGetBuildNumber() throws Exception {
        System.out.println("getBuildNumber");
        final boolean verbose = false;
        final File projectDirectory = File.createTempFile("unittest", ".tmp");
        final String moduleName = "test";
        final SvnVersionChecker instance = new SvnVersionChecker(commandRunner);
        final int expResult = 40503;
        context.checking(new Expectations() {
            {
                oneOf(commandRunner).runCommand(with(any(String[].class)), with(any(File.class)));
                will(returnValue(new ByteArrayInputStream(mockSvnResult.getBytes())));
            }
        });
        int result = instance.getBuildNumber(verbose, projectDirectory, moduleName);
        assertEquals(expResult, result);
    }

    /**
     * Test of getLastCommitDate method, of class SvnVersionChecker.
     */
    public void testGetLastCommitDate() throws Exception {
        System.out.println("getLastCommitDate");
        final boolean verbose = false;
        final File projectDirectory = File.createTempFile("unittest", ".tmp");
        final String moduleName = "test";
        final SvnVersionChecker instance = new SvnVersionChecker(commandRunner);
        final String expResult = "2014-03-13 14:18:11 +0100 (Thu, 13 Mar 2014)";
        context.checking(new Expectations() {
            {
                oneOf(commandRunner).runCommand(with(any(String[].class)), with(any(File.class)));
                will(returnValue(new ByteArrayInputStream(mockSvnResult.getBytes())));
            }
        });
        String result = instance.getLastCommitDate(verbose, projectDirectory, moduleName);
        assertEquals(expResult, result);
    }

}
