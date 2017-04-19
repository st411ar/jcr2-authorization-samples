package com.axamit.aem;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.oak.segment.SegmentNodeStore;
import org.apache.jackrabbit.oak.segment.SegmentNodeStoreBuilders;
import org.apache.jackrabbit.oak.segment.file.FileStore;
import org.apache.jackrabbit.oak.segment.file.FileStoreBuilder;
import org.apache.jackrabbit.oak.segment.file.InvalidFileStoreVersionException;

import javax.jcr.GuestCredentials;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.File;
import java.io.IOException;

public class Main_004 {

    // creation of a couple of users
    public static void main(String[] args) {
        File directory = new File(Util.PATH_REPOSITORY);
        FileStoreBuilder fsb = FileStoreBuilder.fileStoreBuilder(directory);
        Session session = null;
        try (FileStore fs = fsb.build()) {
            SegmentNodeStore ns = SegmentNodeStoreBuilders.builder(fs).build();
            Repository repo = new Jcr(new Oak(ns)).createRepository();
            session = repo.login(Util.getAdminCreds());

            JackrabbitSession jSession = (JackrabbitSession) session;
	        UserManager userManager = jSession.getUserManager();
            userManager.createUser("userName1", "userPass1");
            userManager.createUser("userName2", "userPass2");
            jSession.save();
        } catch (InvalidFileStoreVersionException | IOException | RepositoryException e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }
}