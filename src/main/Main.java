
package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Properties;

/*
 * Main class from which to start the application
 */

public class Main {
    public static void main(String[] args) throws FileNotFoundException,
            IOException {
        Configuration config;

        if (args.length == 0) {
            config = new Configuration();
        } else {
            Properties properties = new Properties();
            properties.load(new FileInputStream(args[0]));
            config = new Configuration(properties);
        }

        CreatureEvolution app = new CreatureEvolution(config);
        app.start();
    }
    
    public static void restart(Runnable run) throws IOException {
    	try {
    		String java = System.getProperty("java.home") + "/bin/java";
    		List<String> vmArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
    		
    		StringBuffer vmArgs = new StringBuffer();
    		for(String s : vmArguments) {
    			if(!s.contains("-agentlib")) {
    				vmArgs.append(s);
    				vmArgs.append(" ");
    			}
    		}
//    		System.out.println(vmArgs.toString());
    		final StringBuffer cmd = new StringBuffer(java + vmArgs);
    		
    		String[] mainCommand = System.getProperty("sun.java.command").split(" ");
    		
    		if(mainCommand[0].endsWith(".jar")) {
    			cmd.append("-jar" + new File(mainCommand[0]).getPath());
    		} else {
    			cmd.append("-cp \"" + System.getProperty("java.class.path") + "\" " + mainCommand[0]);
    		}
    		
    		for(int i = 0; i < mainCommand.length; i++) {
//    			cmd.append(" ");
    			cmd.append(mainCommand[i]);
    		}
    		
    		Runtime.getRuntime().addShutdownHook(new Thread() {
    			@Override
    			public void run() {
    				try {
//    					System.out.println(cmd.toString());
//    					String test = "/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/jre/bin/java";
//    					Runtime.getRuntime().exec(test);
    					Runtime.getRuntime().exec(cmd.toString());
    				} catch (IOException e){
    					e.printStackTrace();
    				}
    			}
    		});
    		
    		if(run != null) {
    			run.run();
    		}
    		System.exit(0);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
}