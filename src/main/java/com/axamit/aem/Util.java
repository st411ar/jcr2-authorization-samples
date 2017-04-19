package com.axamit.aem;

import javax.jcr.*;
import javax.jcr.security.*;
import java.util.ArrayList;
import java.util.List;

public class Util {

    public static final String PATH_REPOSITORY = "C:/oak-repository";

    public static List<Credentials> CREDS = new ArrayList<Credentials>();

    static {
        CREDS.add(getAdminCreds());
        CREDS.add(new GuestCredentials());
        CREDS.add(buildCreds("userName1", "userPass1"));
        CREDS.add(buildCreds("userName2", "userPass2"));
    }

    public static SimpleCredentials buildCreds(String name, String password) {
        return new SimpleCredentials(name, password.toCharArray());
    }

    public static SimpleCredentials getAdminCreds() {
        return buildCreds("admin", "admin");
    }

    public static void showSessionInfo(Repository repository, Session session) throws RepositoryException {
        String user = session.getUserID();
        String rep = repository.getDescriptor(Repository.REP_NAME_DESC);
        System.out.println(
                "Logged in as '" + user + "' to a '" + rep + "' repository"
        );
        System.out.println("session.isLive(): " + session.isLive());
        System.out.println("session: " + session);
        System.out.println("session.getUserID(): " + session.getUserID());
        System.out.println("attributes: ");
        for (String key : session.getAttributeNames()) {
            System.out.println("'" + key + "' : '" + session.getAttribute(key) + "'");
        }
        System.out.println("workspace: " + session.getWorkspace().getName());
        System.out.println("accessible workspace names: ");
        for (String workspaceName : session.getWorkspace().getAccessibleWorkspaceNames()) {
        	System.out.println("workspace '" + workspaceName + "'");
        }
/*
	        System.out.println(session.itemExists("/"));
	        checkPermissions(session, "/");

	        System.out.println("ACL support: " + Repository.OPTION_ACCESS_CONTROL_SUPPORTED);
	        AccessControlManager acm = session.getAccessControlManager();
*/
    }

    public static Node getOrCreateNode(Node parent, String nodeName)
            throws RepositoryException {
        if (parent.hasNode(nodeName)) {
            return parent.getNode(nodeName);
        } else {
            return parent.addNode(nodeName);
        }
    }

    public static void traverse(Node node) throws RepositoryException {
        System.out.println(node.getPath());
        traverseProps(node);
        NodeIterator nodes = node.getNodes();
        while (nodes.hasNext()) {
            traverse(nodes.nextNode());
        }
    }

    public static void traverseProps(Node node) throws RepositoryException {
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

    public static final String[] PERMISSION_ACTIONS = {
            Session.ACTION_ADD_NODE,
            Session.ACTION_READ,
            Session.ACTION_REMOVE,
            Session.ACTION_SET_PROPERTY
    };

    public static void checkPermissions(Session session, String absPath) throws RepositoryException {
        System.out.println("session '" + session.getUserID() + "' permissions toward absolute path '" + absPath + "'");
        for (String action : PERMISSION_ACTIONS) {
            System.out.println("'" + action + "' : '" + session.hasPermission(absPath, action) + "'");
        }
    }

    public static void checkPrivileges(Session session, String absPath) throws RepositoryException {
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
    }

    public static void checkPolicies(Session session, String absPath) throws RepositoryException {

        System.out.println("policies of path '" + absPath + "'");
        AccessControlManager acm = session.getAccessControlManager();

        AccessControlPolicyIterator acpIterator = acm.getApplicablePolicies(absPath);
        System.out.println("\napplicable policies size: " + acpIterator.getSize());
        while (acpIterator.hasNext()) {
            AccessControlPolicy policy = acpIterator.nextAccessControlPolicy();
            System.out.println(policy);
            checkAccessControlList(policy);
        }

        AccessControlPolicy[] policies = acm.getPolicies(absPath);
        System.out.println("\nbounded policies size: " + policies.length);
        for (AccessControlPolicy policy : policies) {
            System.out.println(policy);
            checkAccessControlList(policy);
        }

        policies = acm.getEffectivePolicies(absPath);
        System.out.println("\neffective policies size: " + policies.length);
        for (AccessControlPolicy policy : policies) {
            System.out.println(policy);
            checkAccessControlList(policy);
        }
    }

    public static void checkACM(Session session, String absPath) throws RepositoryException {
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

    public static void checkAccessControlList(AccessControlPolicy policy) throws RepositoryException {
        boolean isList = policy instanceof AccessControlList;
        System.out.println("is policy acl: " + isList);
        if (isList) {
            AccessControlList acl = (AccessControlList) policy;
            AccessControlEntry[] entries = acl.getAccessControlEntries();
            System.out.println("number of entries: " + entries.length);
        }
    }
}