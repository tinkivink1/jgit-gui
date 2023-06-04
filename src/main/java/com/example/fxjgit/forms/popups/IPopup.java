package com.example.fxjgit.forms.popups;

import com.example.fxjgit.db.entities.User;
import org.eclipse.jgit.api.Git;

public interface IPopup {
    Git finalAction();
    void setUser(User user);
}
