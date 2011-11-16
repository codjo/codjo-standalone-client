package net.codjo.standalone.client.util;
import net.codjo.agent.UserId;
import net.codjo.security.common.api.UserMock;
import junit.framework.TestCase;
/**
 *
 */
public class UserAdapterTest extends TestCase {
    private UserMock securityUser = new UserMock();
    private UserAdapter adapter = new UserAdapter(securityUser);


    public void test_isAllowedTo() throws Exception {
        securityUser.mockIsAllowedTo("a-func", true);
        assertTrue(adapter.isAllowedTo("a-func"));

        securityUser.mockIsAllowedTo("other-func", false);
        assertFalse(adapter.isAllowedTo("other-func"));
    }


    public void test_getName() throws Exception {
        securityUser.mockGetUserId(UserId.createId("bernaju", "secret"));
        assertEquals("bernaju", adapter.getName());
    }


    public void test_getGroup() throws Exception {
        try {
            adapter.getGroup();
            fail();
        }
        catch (UnsupportedOperationException ex) {
            ; // Ok
        }
    }


    public void test_toString() throws Exception {
        assertEquals(securityUser.toString(), adapter.toString());
    }
}
