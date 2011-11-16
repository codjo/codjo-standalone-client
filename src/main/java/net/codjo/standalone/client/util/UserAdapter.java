package net.codjo.standalone.client.util;
import net.codjo.profile.User;
/**
 *
 */
public class UserAdapter extends User {
    private final net.codjo.security.common.api.User user;


    public UserAdapter(net.codjo.security.common.api.User user) {
        this.user = user;
    }


    @Override
    public boolean isAllowedTo(String actionKey) {
        return user.isAllowedTo(actionKey);
    }


    @Override
    public String getName() {
        return user.getId().getLogin();
    }


    @Override
    public String getGroup() {
        throw new UnsupportedOperationException("getGroup() est incompatible sur la nouvelle plateforme");
    }


    @Override
    public String toString() {
        return user.toString();
    }
}
