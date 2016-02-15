package ams.tool;

public interface ABconstants {


	int TEMPLATE_151 			= 0;
	int TEMPLATE_TOOL 			= 1;

	String LOADING_IMAGE_151	= "loading.gif";
	String NO_JAVA_IMAGE		= "no-java.png";
	String TOOL_JSCRIPT			= "applet-fu.js";

//	// 0=keytool.exe loc 
//	// {1}=certifier name {2}=org unit {3}= org {4}=location {5}=state {6}=country code 
//	// {7}=keystore name {8}=keystore password {9}=access password for key {10}=alias name
//	String keygenCmdTemplate = "^{0}^ -genkey -dname ^CN={1}, OU={2}, O={3}, L={4}, S={5}, C={6}^ -keystore {7} -storepass {8} -keypass {9} -alias {10}";
//	// 0=keytool.exe loc {1}=no. of days cert is valid {2}=keystore name {3}=keystore password {4}=access password for key {5}=alias name
//	String selfcertCmdTemplate = "^{0}^ -selfcert -validity {1} -keystore {2} -storepass {3}  -keypass {4} -alias {5} ";
//
//	// 0=jarsigner.exe loc {1}=keystore name {2}=keystore password {3}=access password for key {4}=jar file to sign {5}=alias name
//	String jarsignCmdTemplateORG = "^{0}^ -keystore {1} -storepass {2} -keypass {3} {4} {6}";
//
//	// 0=jarsigner.exe loc {1}=keystore name {2}=keystore password {3}=access password for key 
//	// need to add name of jar file file then alias to complete to sign {5}=alias name
//	String jarsignCmdTemplate = "^{0}^ -keystore {1} -storepass {2} -keypass {3} ";

	String NAME = "CN={0}, OU={1}, O={2}, L={3}, S={4}, C={5}";

	String[] keygenArray = new String[] {
			"",				// 0 full path to keytool
			"-genkey",		// 1
			"-dname",		// 2
			"",				// 3 use Messages,build and NAME to create this one
			"-keystore",	// 4
			"",				// 5 name of keystore
			"-storepass",	// 6
			"",				// 7 store password
			"-keypass",		// 8 
			"",				// 9 key password
			"-alias",		// 10
			""				// alias
	};

	String[] selfCertArray = new String[] {
			"",				// 0 full path to keytool
			"-selfcert",	// 1
			"-validity",	// 2
			"",				// 3
			"-keystore",	// 4
			"",				// 5 name of keystore
			"-storepass",	// 6
			"",				// 7 store password
			"-keypass",		// 8 
			"",				// 9 key password
			"-alias",		// 10
			""				// alias
	};

	String[] jarsignerArray = new String[] {
			"",				// 0 full path to jarsigner
			"-keystore",	// 1
			"",				// 2 name of keystore
			"-storepass",	// 3
			"",				// 4 store password
			"-keypass",		// 5 
			"",				// 6 key password
			"",				// 7 jar (absolute path
			""				// 8 alias name
	};

	// Chars we can use to make random password.
	String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_0123456789";

}
