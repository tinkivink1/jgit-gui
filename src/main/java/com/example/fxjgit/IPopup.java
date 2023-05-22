package com.example.fxjgit;

import org.eclipse.jgit.api.Git;

public interface IPopup {
    Git git = null;
    Git returnValueOnClose();
}
