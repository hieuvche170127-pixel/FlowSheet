/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package dal;

import entity.UserAccount;
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
public class UserAccountDAOIT {
    
    public UserAccountDAOIT() {
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
     * Test of getAllAccount method, of class UserAccountDAO.
     */
//    @Test
//    public void testGetAllAccount() {
//        System.out.println("getAllAccount");
//        UserAccountDAO instance = new UserAccountDAO();
//        ArrayList<UserAccount> expResult = null;
//        ArrayList<UserAccount> result = instance.getAllAccount();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of getAccountByAccount method, of class UserAccountDAO.
     */
    @Test
    public void testGetAccountByAccount() {
        System.out.println("getAccountByAccount");
        String username = "admin";
        UserAccountDAO instance = new UserAccountDAO();
        UserAccount result = instance.getAccountByUsername(username);
        assertEquals(username, result.getUsername());
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }
    
}
