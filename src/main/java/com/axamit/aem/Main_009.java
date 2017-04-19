package com.axamit.aem;

import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.oak.segment.SegmentNodeStore;
import org.apache.jackrabbit.oak.segment.SegmentNodeStoreBuilders;
import org.apache.jackrabbit.oak.segment.file.FileStore;
import org.apache.jackrabbit.oak.segment.file.FileStoreBuilder;
import org.apache.jackrabbit.oak.segment.file.InvalidFileStoreVersionException;

import javax.jcr.*;
import javax.jcr.security.*;
import java.io.File;
import java.io.IOException;

public class Main_009 {

    // add policy
    public static void main(String[] args) {
        File directory = new File(Util.PATH_REPOSITORY);
        FileStoreBuilder fsb = FileStoreBuilder.fileStoreBuilder(directory);
        Session session = null;
        try (FileStore fs = fsb.build()) {
            SegmentNodeStore ns = SegmentNodeStoreBuilders.builder(fs).build();
            Repository repo = new Jcr(new Oak(ns)).createRepository();

            session = repo.login(Util.getAdminCreds());
            AccessControlManager acm = session.getAccessControlManager();
            AccessControlPolicyIterator acpIterator = acm.getApplicablePolicies("/hello");
            System.out.println("\napplicable policies size: " + acpIterator.getSize());
            while (acpIterator.hasNext()) {
                AccessControlPolicy policy = acpIterator.nextAccessControlPolicy();
                System.out.println(policy);
                boolean isList = policy instanceof AccessControlList;
                System.out.println("is policy acl: " + isList);
                if (isList) {
                    AccessControlList acl = (AccessControlList) policy;
                    AccessControlEntry[] entries = acl.getAccessControlEntries();
                    System.out.println("number of entries: " + entries.length);

                }
                acm.setPolicy("/hello", policy);
            }

            session.save();


        } catch (InvalidFileStoreVersionException | IOException | RepositoryException e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }
}