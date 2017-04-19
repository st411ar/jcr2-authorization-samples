package com.axamit.aem;

import java.io.*;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Iterator;
import java.util.Set;
import javax.jcr.*;
import javax.jcr.security.*;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.principal.PrincipalIterator;
import org.apache.jackrabbit.api.security.principal.PrincipalManager;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.core.security.principal.EveryonePrincipal;
import org.apache.jackrabbit.oak.*;
import org.apache.jackrabbit.oak.api.ContentSession;
import org.apache.jackrabbit.oak.api.Root;
import org.apache.jackrabbit.oak.jcr.*;
// import org.apache.jackrabbit.oak.security.user.UserConfigurationImpl();
import org.apache.jackrabbit.oak.security.principal.PrincipalConfigurationImpl;
import org.apache.jackrabbit.oak.segment.*;
import org.apache.jackrabbit.oak.segment.file.*;
import org.apache.jackrabbit.oak.spi.security.principal.PrincipalProvider;

public class App 
{
	private static final String[] PERMISSION_ACTIONS = {
		Session.ACTION_ADD_NODE,
		Session.ACTION_READ,
		Session.ACTION_REMOVE,
		Session.ACTION_SET_PROPERTY
	};

    public static void main( String[] args )
    {
        System.out.println( "Hello, World!" );

        // File directory = new File("sample-repository");
        File directory = new File("C:/dev/java/jcr/oak/oak-sample/sample-repository");
        FileStoreBuilder fsb = FileStoreBuilder.fileStoreBuilder(directory);
        Session session = null;

        try (FileStore fs = fsb.build()) {
	        SegmentNodeStore ns = SegmentNodeStoreBuilders.builder(fs).build();
	        Repository repo = new Jcr(new Oak(ns)).createRepository();


//			session = repo.login(getAdminCreds());
//			session = repo.login(new GuestCredentials());
	        session = repo.login(buildCreds("ololoName", "ololoPassword"));

/*
	        if ("admin".equals(session.getUserID())) {
	        	Node root = session.getRootNode();
	        	root.addNode("sampleNode", "");
			}
*/

			checkPermissions(session, "/");
			checkPermissions(session, "/hello");
			checkPermissions(session, "/hello/world");
	        if ("true".equals(repo.getDescriptor(Repository.OPTION_ACCESS_CONTROL_SUPPORTED))) {
//				checkACM(session, "/");
				checkACM(session, "/hello");
				checkACM(session, "/hello/world");
	        }

	        Node node = session.getNode("/hello");
			System.out.println(node.canAddMixin("rep:AccessControllable"));
			node.addMixin("rep:AccessControllable");
//	        node.setProperty("jcr:mixinTypes","[rep:AccessControllable]");
	        session.save();

/*
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

			AccessControlManager acm = session.getAccessControlManager();
			String absPath = "/hello";
			Principal everyonePrincipal = principalManager.getEveryone();
			Principal principal = principalManager.getPrincipal("ololoName");
			Privilege privilege = acm.privilegeFromName("jcr:all");
			Privilege[] privileges = {privilege};
			AccessControlPolicyIterator acpIterator = acm.getApplicablePolicies(absPath);
			acpIterator = acm.getApplicablePolicies(absPath);
			while (acpIterator.hasNext()) {
				AccessControlPolicy policy = acpIterator.nextAccessControlPolicy();
				System.out.println(policy);
				boolean isList = policy instanceof AccessControlList;
				System.out.println("is policy acl: " + isList);
				if (isList) {
					AccessControlList acl = (AccessControlList) policy;
					AccessControlEntry[] entries = acl.getAccessControlEntries();
					System.out.println("number of entries: " + entries.length);
					acl.removeAccessControlEntry(entries[entries.length-1]);
					acl.addAccessControlEntry(principal, privileges);
					acm.setPolicy("/hello", acl);
					session.save();
				}
			}

			AccessControlPolicy[] policies = acm.getPolicies(absPath);
			for (AccessControlPolicy policy : policies) {
				System.out.println(policy);
				boolean isList = policy instanceof AccessControlList;
				System.out.println("is policy acl: " + isList);
				if (isList) {
					AccessControlList acl = (AccessControlList) policy;
					AccessControlEntry[] entries = acl.getAccessControlEntries();
					System.out.println("number of entries: " + entries.length);
					acl.removeAccessControlEntry(entries[entries.length-1]);
					acl.addAccessControlEntry(principal, privileges);
					acm.setPolicy("/hello", acl);
					session.save();
				}
			}
*/


	        firstHop(repo, session);
//	        secondHop(session);
	        thirdHop(session);

/*
	        Node user = session.getNode("/rep:security/rep:authorizables/rep:users/o/ol/ololoName");
	        System.out.println(principal);
	        System.out.println(principal.hasProperty("rep:principalName"));
	        principal.setProperty("rep:principalName", "admin");

			user.remove();
			session.save();
*/




/*	        Node root = session.getRootNode();
	        if (root.hasNode("hello")) {
	        	Node hello = root.getNode("hello");
	        	long count = hello.getProperty("count").getLong();
	        	hello.setProperty("count", count + 1);
	        	System.out.println("found the hello node, count = " + count);
	        } else {
	        	System.out.println("creating the hello node");
	        	root.addNode("hello").setProperty("count", 1);
	        }

	        session.save();
	        session.logout();

	    	System.out.println("loged in like guest");
	        session = repo.login(new GuestCredentials());
	        thirdHop(session);
	        session.logout();
	    	System.out.println("loged in like guest - done");
*/	    	
        } catch (InvalidFileStoreVersionException | 
        		IOException | 
        		RepositoryException e) {
        	e.printStackTrace();
        } finally {
        	if (session != null) {
		        session.logout();
        	}
        }

        System.out.println( "See you, World!" );
    }

    private static SimpleCredentials getAdminCreds() {
    	return new SimpleCredentials("admin", "admin".toCharArray());
    }

    private static SimpleCredentials buildCreds(String name, String password) {
    	return new SimpleCredentials(name, password.toCharArray());
    }

    private static void firstHop(Repository repository, Session session) {
    	String user = session.getUserID();
    	String rep = repository.getDescriptor(Repository.REP_NAME_DESC);
    	System.out.println(
    			"Logged in as '" + user + "' to a '" + rep + "' repository"
    	);
    }

    private static void secondHop(Session session) throws RepositoryException {
		Node root = session.getRootNode();
		Node hello = getNodeSafely(root, "hello");
		Node world = getNodeSafely(hello, "world");
		world.setProperty("message", "Hello, World!");
		session.save();

		Node node = root.getNode("hello/world");
		System.out.println(node.getPath());
		Property msg = node.getProperty("message");
		System.out.println(msg.getPath());
		System.out.println(msg.getString());

/*		root.getNode("hello").remove();
		session.save();
*/    }


    private static void thirdHop(Session session) throws RepositoryException {
//    	Node root = session.getRootNode();
//    	Node hello = getNodeSafely(root, "hello");
    	Node hello = session.getNode("/hello");
   		traverse(hello);
    }

    private static Node getNodeSafely(Node parent, String nodeName)
    		throws RepositoryException {
    	if (parent.hasNode(nodeName)) {
    		return parent.getNode(nodeName);
    	} else {
    		return parent.addNode(nodeName);
    	}
    }

    private static void traverse(Node node) throws RepositoryException {
    	System.out.println(node.getPath());
    	traverseProps(node);
    	NodeIterator nodes = node.getNodes();
    	while (nodes.hasNext()) {
    		traverse(nodes.nextNode());
    	}
    }

    private static void traverseProps(Node node) throws RepositoryException {
    	PropertyIterator props = node.getProperties();
    	while (props.hasNext()) {
    		Property prop = props.nextProperty();
    		if (prop.getDefinition().isMultiple()) {
    			Value[] values = prop.getValues();
    			for (int i = 0; i < values.length; i++) {
    				System.out.println(
    						prop.getPath() + " = " + values[i].getString());
    			}
    		} else {
    			System.out.println(prop.getPath() + " = " + prop.getString());
    		}
    	}
    }

    private static void checkPermissions(Session session, String absPath) throws RepositoryException {
    	System.out.println("session '" + session.getUserID() + "' permissions toward absolute path '" + absPath + "'");
    	for (String action : PERMISSION_ACTIONS) {
	    	System.out.println("'" + action + "' : '" + session.hasPermission(absPath, action) + "'");
    	}
    }

    private static void checkCapabilities(Session session) {

    }

    private static void checkACM(Session session, String absPath) throws RepositoryException {
    	System.out.println("privileges of path '" + absPath + "'");
        AccessControlManager acm = session.getAccessControlManager();

        System.out.println("\nsupported:");
        for (Privilege privilege : acm.getSupportedPrivileges(absPath)) {
        	System.out.println(privilege.getName());
        }

        System.out.println("\naccording to session:");
        for (Privilege privilege : acm.getSupportedPrivileges(absPath)) {
	       	Privilege[] privileges = {privilege};
        	System.out.println("toward the absPath '" + absPath + "' session has privilege '" + privilege.getName() + "' : " + acm.hasPrivileges(absPath, privileges));
        }

        Privilege[] privileges = acm.getPrivileges(absPath);
        System.out.println("\nsession has " + privileges.length + " privileges:");
        for (Privilege privilege : privileges) {
        	System.out.println(privilege.getName());
        }

        AccessControlPolicyIterator acpIterator = acm.getApplicablePolicies(absPath);
        System.out.println("\napplicable policies size: " + acpIterator.getSize());
        while (acpIterator.hasNext()) {
        	AccessControlPolicy policy = acpIterator.nextAccessControlPolicy();
        	System.out.println(policy);
		}

        AccessControlPolicy[] policies = acm.getPolicies(absPath);
        System.out.println("\nbounded policies size: " + policies.length);
        for (AccessControlPolicy policy : policies) {
        	System.out.println(policy);
        }

		analyzeEffectivePolicies(acm, absPath);

/*
		acpIterator = acm.getApplicablePolicies(absPath);
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
			acm.setPolicy("/", policy);
		}

		System.out.println("\nadding policies");
		acpIterator = acm.getApplicablePolicies(absPath);
		System.out.println("\napplicable policies size: " + acpIterator.getSize());
		while (acpIterator.hasNext()) {
			AccessControlPolicy policy = acpIterator.nextAccessControlPolicy();
			System.out.println(policy);
			acm.setPolicy("/", policy);
		}
		session.save();

		policies = acm.getPolicies(absPath);
		System.out.println("\nbounded policies size: " + policies.length);
		for (AccessControlPolicy policy : policies) {
			System.out.println(policy);
		}

		analyzeEffectivePolicies(acm, absPath);
*/
    }

    private static void analyzeEffectivePolicies(AccessControlManager acm, String absPath) throws RepositoryException {
		AccessControlPolicy[] policies = acm.getEffectivePolicies(absPath);
		System.out.println("\neffective policies size: " + policies.length);
		for (AccessControlPolicy policy : policies) {
			System.out.println(policy);
			boolean isList = policy instanceof AccessControlList;
			System.out.println("is policy acl: " + isList);
			if (isList) {
				AccessControlList acl = (AccessControlList) policy;
				AccessControlEntry[] entries = acl.getAccessControlEntries();
				System.out.println("number of entries: " + entries.length);
			}
		}
	}
}