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

public class Main_011 {

    // add entry
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

			AccessControlManager acm = session.getAccessControlManager();
			String absPath = "/hello";
//			Principal everyonePrincipal = principalManager.getEveryone();
			Principal principal = principalManager.getPrincipal("userName1");
			Privilege privilege = acm.privilegeFromName("jcr:all");
			Privilege[] privileges = {privilege};

/*
			AccessControlPolicyIterator acpIterator = acm.getApplicablePolicies(absPath);
			while (acpIterator.hasNext()) {
				AccessControlPolicy policy = acpIterator.nextAccessControlPolicy();
				System.out.println(policy);
				boolean isList = policy instanceof AccessControlList;
				System.out.println("is policy acl: " + isList);
				if (isList) {
					AccessControlList acl = (AccessControlList) policy;
					AccessControlEntry[] entries = acl.getAccessControlEntries();
					System.out.println("number of entries: " + entries.length);
//					acl.removeAccessControlEntry(entries[entries.length-1]);
					acl.addAccessControlEntry(principal, privileges);
					acm.setPolicy("/hello", acl);
					session.save();
				}
			}
*/

			AccessControlPolicy[] policies = acm.getPolicies(absPath);
			for (AccessControlPolicy policy : policies) {
				System.out.println(policy);
				boolean isList = policy instanceof AccessControlList;
				System.out.println("is policy acl: " + isList);
				if (isList) {
					AccessControlList acl = (AccessControlList) policy;
					AccessControlEntry[] entries = acl.getAccessControlEntries();
					System.out.println("number of entries: " + entries.length);
//					acl.removeAccessControlEntry(entries[entries.length-1]);
					acl.addAccessControlEntry(principal, privileges);
					acm.setPolicy("/hello", acl);
					session.save();
				}
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