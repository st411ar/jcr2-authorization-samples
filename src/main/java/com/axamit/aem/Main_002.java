package com.axamit.aem;

import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.oak.segment.SegmentNodeStore;
import org.apache.jackrabbit.oak.segment.SegmentNodeStoreBuilders;
import org.apache.jackrabbit.oak.segment.file.FileStore;
import org.apache.jackrabbit.oak.segment.file.FileStoreBuilder;
import org.apache.jackrabbit.oak.segment.file.InvalidFileStoreVersionException;
import org.eclipse.jetty.util.security.Credential;

import javax.jcr.*;
import java.io.File;
import java.io.IOException;

public class Main_002 {

    // admin session connection
    public static void main(String[] args) {
        File directory = new File(Util.PATH_REPOSITORY);
        FileStoreBuilder fsb = FileStoreBuilder.fileStoreBuilder(directory);
        Session session = null;
        try (FileStore fs = fsb.build()) {
            SegmentNodeStore ns = SegmentNodeStoreBuilders.builder(fs).build();
            Repository repo = new Jcr(new Oak(ns)).createRepository();

            session = repo.login(Util.getAdminCreds());
            Util.showSessionInfo(repo, session);

            Node root = session.getRootNode();
            Node hello = Util.getOrCreateNode(root, "hello");
            Node world = Util.getOrCreateNode(hello, "world");
            world.setProperty("message", "Hello, World!");
            session.save();

            Node node = root.getNode("hello/world");
            System.out.println(node.getPath());
            Property msg = node.getProperty("message");
            System.out.println(msg.getPath());
            System.out.println(msg.getString());

            Util.traverse(root);
            Util.traverse(hello);

        } catch (InvalidFileStoreVersionException | IOException | RepositoryException e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.logout();
            }
        }
    }
}