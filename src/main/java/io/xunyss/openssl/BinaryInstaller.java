package io.xunyss.openssl;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URL;

import io.xunyss.commons.exec.ProcessExecutor;
import io.xunyss.commons.io.FileUtils;
import io.xunyss.commons.io.ResourceUtils;
import io.xunyss.commons.lang.SystemUtils;
import io.xunyss.commons.lang.ThreadUtils;
import io.xunyss.commons.lang.ZipUtils;

/**
 * https://wiki.openssl.org/index.php/Binaries
 * https://indy.fulgan.com/SSL/
 * openssl-1.0.2g-i386-win32
 * 
 * @author XUNYSS
 */
public class BinaryInstaller {
	
	/**
	 * 
	 */
	private static class SingletonHolder {
		// singleton object
		private static final BinaryInstaller instance = new BinaryInstaller();
	}
	
	/**
	 * 
	 * @return
	 */
	public static BinaryInstaller getInstance() {
		return SingletonHolder.instance;
	}
	
	
	//----------------------------------------------------------------------------------------------
	
	private static final String RESOURCE_BINARY_PATH = "/io/xunyss/openssl/binary";
	private static final String EXTRACT_DIRECTORY;
	
	static {
		// 서로 다른 JVM 이 임시 디렉토리를 공유하지 않게 하기 위해
		String jvmName = ManagementFactory.getRuntimeMXBean().getName();
		EXTRACT_DIRECTORY = "io_xunyss_openssl_bin_" + jvmName.replace('@', '-');
	}
	
	private String binaryName = "openssl";	// default executable binary name
	private boolean initialized;
	
	
	/**
	 * Constructor.
	 */
	private BinaryInstaller() {
		if (!(initialized = selfTest())) {
			if (SystemUtils.IS_OS_WINDOWS) {	// windows
				temporaryInstall("win32", "openssl.exe");
			}
			else {
				// TODO Linux, Unix, ...
			}
			
			initialized = selfTest();
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean selfTest() {
		try {
			ProcessExecutor processExecutor = new ProcessExecutor(true);
			return ProcessExecutor.EXITVALUE_NORMAL == processExecutor.execute(binaryName, "version");
		}
		catch (IOException ex) {
			return false;
		}
	}
	
	/**
	 * 
	 * @param subPackage
	 * @param simpleBinaryName
	 */
	private void temporaryInstall(String subPackage, String simpleBinaryName) {
		final String srcResourceLocation = RESOURCE_BINARY_PATH + FileUtils.RESOURCE_PATH_SEPARATOR_CHAR + subPackage;
		final File dstDirectory = new File(FileUtils.getTempDirectory(), EXTRACT_DIRECTORY);
		
		// real binary file full path
		binaryName = dstDirectory.getPath() + FileUtils.FILE_SEPARATOR + simpleBinaryName;
		
		try {
			// extract resource to temporary directory
			extractResources(srcResourceLocation, dstDirectory);
		}
		catch (IOException ex) {
			// not not throw any exception
			return;
		}
		finally {
			// remove temporary binaries when system exit
			registerShutdownHook(dstDirectory);
		}
	}
	
	/**
	 * 
	 * @param srcResourceLocation
	 * @param dstDirectory
	 * @throws IOException
	 */
	private void extractResources(String srcResourceLocation, File dstDirectory) throws IOException {
		final URL srcResourceUrl = ResourceUtils.getResource(srcResourceLocation);
		
		// when this is running at '.class'
		if (ResourceUtils.isFileURL(srcResourceUrl)) {
			// copy resources
			FileUtils.copyDirectory(new File(srcResourceUrl.getFile()), dstDirectory);
		}
		// when this is running in '.jar'
		else if (ResourceUtils.isJarURL(srcResourceUrl)) {
			// extract resources
			ZipUtils.unjar(ResourceUtils.getJarFileURL(srcResourceUrl), srcResourceLocation, dstDirectory);
		}
		// other
		else {
			throw new IOException("Unable to extract resource: " + srcResourceLocation);
		}
	}
	
	/**
	 * 
	 * @param dstDirectory
	 */
	private void registerShutdownHook(final File dstDirectory) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			
			static final int MAX_RETRY = 10;
			static final int DELAY_MILLIS = 100;
			
			@Override
			public void run() {
//				FileUtils.deleteDirectoryQuietly(dstDirectory);
				// 2018.03.16 XUNYSS
				// 가능성은 0 에 가깝지만, openssl Process 종료 후 아주 짧은 시간 안에 삭제를 시도하면 실패할 수 있음
				// 임시 디렉토리 삭제시 re-try 로직을 추가 (retry count: 10, delay time: 100ms)
				for (int retryCount = 0; retryCount < MAX_RETRY; retryCount++) {
					FileUtils.deleteDirectoryQuietly(dstDirectory);
					if (!dstDirectory.exists()) {
						return;
					}
					ThreadUtils.sleep(DELAY_MILLIS);
				}
			}
		});
	}
	
	/**
	 * 
	 * @return
	 */
	String getBinaryName() {
		return binaryName;
	}
	
	/**
	 *
	 * @return
	 */
	boolean isInitialized() {
		return initialized;
	}
}
