package com.axamit.aem;

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

public class Main_008 {

    // check policies
    public static void main(String[] args) {
        File directory = new File(Util.PATH_REPOSITORY);
        FileStoreBuilder fsb = FileStoreBuilder.fileStoreBuilder(directory);
        Session session = null;
        try (FileStore fs = fsb.build()) {
            SegmentNodeStore ns = SegmentNodeStoreBuilders.builder(fs).build();
            Repository repo = new Jcr(new Oak(ns)).createRepository();

            for (Credentials creds : Util.CREDS) {
                session = repo.login(creds);
                try {
                    Util.checkPolicies(session, "/");
                    Util.checkPolicies(session, "/hello");
                    Util.checkPolicies(session, "/hello/world");
                } catch (RepositoryException e) {
                    e.printStackTrace();
                }
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