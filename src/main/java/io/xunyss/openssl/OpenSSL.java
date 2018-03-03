package io.xunyss.openssl;

import java.io.IOException;

import io.xunyss.commons.exec.ExecuteException;
import io.xunyss.commons.exec.ProcessExecutor;
import io.xunyss.commons.exec.support.StringOutputHandler;

/**
 * 
 * @author XUNYSS
 */
public class OpenSSL {
	
	private boolean enableExecute;
	private String binaryName;
	
	private ProcessExecutor processExecutor;
	private StringOutputHandler stringOutputHandler;
	
	/**
	 * 
	 */
	public OpenSSL() {
		BinaryInstaller binaryInstaller = BinaryInstaller.getInstance();
		enableExecute = binaryInstaller.isInitialized();
		binaryName = binaryInstaller.getBinaryName();
		
		if (enableExecute) {
			stringOutputHandler = new StringOutputHandler();
			processExecutor = new ProcessExecutor(true);
			processExecutor.setStreamHandler(stringOutputHandler);
		}
	}
	
	/**
	 * 
	 * @param commands
	 * @throws IOException
	 */
	public void exec(String... commands) throws IOException {
		if (!enableExecute) {
			throw new IOException("Unable to execute openSSL");
		}
		
		try {
			processExecutor.execute(binaryName, commands);
		}
		catch (ExecuteException ex) {
			throw ex;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public String getOutput() {
		return stringOutputHandler.getOutputString();
	}
	
	/**
	 * 
	 * @return
	 */
	public String getError() {
		return stringOutputHandler.getErrorString();
	}
}
