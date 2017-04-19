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

import javax.jcr.Credentials;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.File;
import java.io.IOException;

public class Main_005 {

    // check permissions
    public static void main(String[] args) {
        File directory = new File(Util.PATH_REPOSITORY);
        FileStoreBuilder fsb = FileStoreBuilder.fileStoreBuilder(directory);
        Session session = null;
        try (FileStore fs = fsb.build()) {
            SegmentNodeStore ns = SegmentNodeStoreBuilders.builder(fs).build();
            Repository repo = new Jcr(new Oak(ns)).createRepository();

            for (Credentials creds : Util.CREDS) {
                session = repo.login(creds);
                Util.checkPermissions(session, "/");
                Util.checkPermissions(session, "/hello");
                Util.checkPermissions(session, "/hello/world");
                session.logout();
            }

        } catch (InvalidFileStoreVersionException | IOException | RepositoryException e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }
}