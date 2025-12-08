/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package dal;

import entity.TimeSheetEntry;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Admin
 */
public class TimesheetEntryDAOIT {
    
    public TimesheetEntryDAOIT() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getAllTimesheet method, of class TimesheetEntryDAO.
     */
    @Test
    public void testGetAllTimesheet() {
        System.out.println("getAllTimesheet");
        TimesheetEntryDAO instance = new TimesheetEntryDAO();
        ArrayList<TimeSheetEntry> expResult = null;
        ArrayList<TimeSheetEntry> result = instance.getAllTimesheet();
        assertEquals(3, result.size());
    }
    
}
