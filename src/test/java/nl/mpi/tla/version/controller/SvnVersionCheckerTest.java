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
    final private String mockSvnLogResult = "------------------------------------------------------------------------\n"
            + "r40846 | petwit | 2014-05-21 11:38:31 +0200 (Wed, 21 May 2014)\n"
            + "------------------------------------------------------------------------\n"
            + "r40845 | petwit | 2014-05-21 11:30:07 +0200 (Wed, 21 May 2014)\n"
            + "------------------------------------------------------------------------\n"
            + "r40842 | petwit | 2014-05-21 11:21:19 +0200 (Wed, 21 May 2014)\n"
            + "------------------------------------------------------------------------\n"
            + "r40841 | petwit | 2014-05-21 10:39:09 +0200 (Wed, 21 May 2014)\n"
            + "------------------------------------------------------------------------\n"
            + "r40837 | petwit | 2014-05-21 10:20:11 +0200 (Wed, 21 May 2014)\n"
            + "------------------------------------------------------------------------\n"
            + "r40829 | petwit | 2014-05-20 15:31:14 +0200 (Tue, 20 May 2014)\n"
            + "------------------------------------------------------------------------\n"
            + "r40828 | petwit | 2014-05-20 15:29:15 +0200 (Tue, 20 May 2014)\n"
            + "------------------------------------------------------------------------\n"
            + "r40814 | petwit | 2014-05-20 14:00:51 +0200 (Tue, 20 May 2014)\n"
            + "------------------------------------------------------------------------\n"
            + "r40810 | petwit | 2014-05-19 17:26:39 +0200 (Mon, 19 May 2014)\n"
            + "------------------------------------------------------------------------\n"
            + "r39555 | twagoo | 2013-12-12 09:09:38 +0100 (Thu, 12 Dec 2013)\n"
            + "------------------------------------------------------------------------\n"
            + "r39380 | twagoo | 2013-11-28 16:51:55 +0100 (Thu, 28 Nov 2013)\n"
            + "------------------------------------------------------------------------\n"
            + "r39113 | petwit | 2013-11-13 14:18:49 +0100 (Wed, 13 Nov 2013)\n"
            + "------------------------------------------------------------------------\n"
            + "r39103 | petwit | 2013-11-12 14:58:16 +0100 (Tue, 12 Nov 2013)\n"
            + "------------------------------------------------------------------------\n"
            + "r39098 | petwit | 2013-11-12 14:13:14 +0100 (Tue, 12 Nov 2013)\n"
            + "------------------------------------------------------------------------\n"
            + "r38315 | petwit | 2013-09-10 13:15:20 +0200 (Tue, 10 Sep 2013)\n"
            + "------------------------------------------------------------------------\n"
            + "r37870 | petwit | 2013-08-15 18:07:13 +0200 (Thu, 15 Aug 2013)\n"
            + "------------------------------------------------------------------------\n"
            + "r37850 | petwit | 2013-08-15 11:22:13 +0200 (Thu, 15 Aug 2013)\n"
            + "------------------------------------------------------------------------";

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
        final int expResult = 17;
        context.checking(new Expectations() {
            {
                oneOf(commandRunner).runCommand(with(any(String[].class)), with(any(File.class)));
                will(returnValue(new ByteArrayInputStream(mockSvnLogResult.getBytes())));
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
