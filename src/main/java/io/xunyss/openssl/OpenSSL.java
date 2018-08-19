package io.xunyss.openssl;

import java.io.IOException;

import io.xunyss.commons.exec.ExecuteException;
import io.xunyss.commons.exec.ProcessExecutor;
import io.xunyss.commons.exec.support.ToStringStreamHandler;

/**
 * 
 * @author XUNYSS
 */
public class OpenSSL {
	
	private boolean enableExecute;
	private String binaryName;
	
	private ProcessExecutor processExecutor;
	private ToStringStreamHandler toStringStreamHandler;
	
	/**
	 * 
	 */
	public OpenSSL() {
		BinaryInstaller binaryInstaller = BinaryInstaller.getInstance();
		enableExecute = binaryInstaller.isInitialized();
		binaryName = binaryInstaller.getBinaryName();
		
		if (enableExecute) {
			toStringStreamHandler = new ToStringStreamHandler();
			processExecutor = new ProcessExecutor(true);
			processExecutor.setStreamHandler(toStringStreamHandler);
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
		return toStringStreamHandler.getOutputString();
	}
	
	/**
	 * 
	 * @return
	 */
	public String getError() {
		return toStringStreamHandler.getErrorString();
	}
}
