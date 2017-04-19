package com.axamit.aem;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.principal.PrincipalIterator;
import org.apache.jackrabbit.api.security.principal.PrincipalManager;
import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.oak.segment.SegmentNodeStore;
import org.apache.jackrabbit.oak.segment.SegmentNodeStoreBuilders;
import org.apache.jackrabbit.oak.segment.file.FileStore;
import org.apache.jackrabbit.oak.segment.file.FileStoreBuilder;
import org.apache.jackrabbit.oak.segment.file.InvalidFileStoreVersionException;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.security.*;
import java.io.File;
import java.io.IOException;
import java.security.Principal;

public class Main_010 {

    // check principals
    public static void main(String[] args) {
        File directory = new File(Util.PATH_REPOSITORY);
        FileStoreBuilder fsb = FileStoreBuilder.fileStoreBuilder(directory);
        Session session = null;
        try (FileStore fs = fsb.build()) {
            SegmentNodeStore ns = SegmentNodeStoreBuilders.builder(fs).build();
            Repository repo = new Jcr(new Oak(ns)).createRepository();

            session = repo.login(Util.getAdminCreds());

	        JackrabbitSession jSession = (JackrabbitSession) session;
	        PrincipalManager principalManager = jSession.getPrincipalManager();
	        PrincipalIterator principalIterator = principalManager.findPrincipals(null);
			System.out.println("\nprincipals:");
			while (principalIterator.hasNext()) {
				Principal principal = principalIterator.nextPrincipal();
				System.out.println(principal);
				System.out.println(principal.getName());
				System.out.println(principal.getClass());
			}
			System.out.println("");

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