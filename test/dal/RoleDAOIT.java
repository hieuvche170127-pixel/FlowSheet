/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package dal;

import entity.Role;
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
public class RoleDAOIT {
    
    public RoleDAOIT() {
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
     * Test of getAllRoleData method, of class RoleDAO.
     */
    @Test
    public void testGetAllRoleData() {
        System.out.println("getAllRoleData");
        RoleDAO instance = new RoleDAO();
        // nhớ sửa lại sau khi add- mà thực ra có fix3 role thôi nên ko sao :))) 
        int expResult = 3;
        ArrayList<Role> result = instance.getAllRoleData();
        assertEquals(expResult, result.size());
    }

    /**
     * Test of main method, of class RoleDAO.
     */
//    @Test
//    public void testMain() {
//        System.out.println("main");
//        String[] args = null;
//        RoleDAO.main(args);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    
}
