/**
 * Copyright 2015 Thomas Cashman
 */
package org.tilepacker.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;

/**
 *
 */
public class NativeLoader {
	private static final String DESKTOP_OS = System.getProperty("os.name") != null
			? System.getProperty("os.name").toLowerCase() : null;
	private static final boolean IS_64_BIT = System.getProperty("os.arch").equals("amd64")
			|| System.getProperty("os.arch").equals("x86_64");

	public static void loadNatives(ClassLoader classLoader) {
		if (DESKTOP_OS.indexOf("win") >= 0) {
			loadWindowsNatives(classLoader);
		} else if (DESKTOP_OS.indexOf("mac") >= 0) {
			loadMacNatives(classLoader);
		} else {
			loadLinuxNatives(classLoader);
		}
	}

	private static void loadWindowsNatives(ClassLoader classLoader) {
		File lwjglFile = null;
		if (IS_64_BIT) {
			load(classLoader, "jinput-dx8_64.dll");
			load(classLoader, "jinput-raw_64.dll");
			lwjglFile = load(classLoader, "lwjgl64.dll");
			load(classLoader, "OpenAL64.dll");
		} else {
			load(classLoader, "jinput-dx8.dll");
			load(classLoader, "jinput-raw.dll");
			lwjglFile = load(classLoader, "lwjgl.dll");
			load(classLoader, "OpenAL32.dll");
		}
		System.setProperty("org.lwjgl.librarypath", lwjglFile.getParentFile().getAbsolutePath());
	}

	private static void loadMacNatives(ClassLoader classLoader) {
		load(classLoader, "libjinput-osx.dylib");
		File lwjglFile = load(classLoader, "liblwjgl.dylib");
		load(classLoader, "openal.dylib");
		System.setProperty("org.lwjgl.librarypath", lwjglFile.getParentFile().getAbsolutePath());
	}

	private static void loadLinuxNatives(ClassLoader classLoader) {
		File lwjglFile = null;
		if (IS_64_BIT) {
			load(classLoader, "libjinput-linux64.so");
			lwjglFile = load(classLoader, "liblwjgl64.so");
			load(classLoader, "libopenal64.so");
		} else {
			load(classLoader, "libjinput-linux.so");
			lwjglFile = load(classLoader, "liblwjgl.so");
			load(classLoader, "libopenal.so");
		}
		System.setProperty("org.lwjgl.librarypath", lwjglFile.getParentFile().getAbsolutePath());
	}

	private static InputStream readFile(ClassLoader classLoader, String nativeFile) {
		return NativeLoader.class.getClassLoader().getResourceAsStream(nativeFile);
	}

	private static File load(ClassLoader classLoader, String nativeLibrary) {
		String sourceCrc = crc(readFile(classLoader, nativeLibrary));

		String libraryFileName = new File(nativeLibrary).getName();

		File libraryFilePath = new File(System.getProperty("java.io.tmpdir") + "/tilepacker"
				+ System.getProperty("user.name") + "/" + sourceCrc, libraryFileName);
		loadFile(classLoader, nativeLibrary, sourceCrc, libraryFilePath);
		return libraryFilePath;
	}

	private static String crc(InputStream input) {
		if (input == null)
			throw new TilePackerException("Native library file not found");
		CRC32 crc = new CRC32();
		byte[] buffer = new byte[4096];
		try {
			while (true) {
				int length = input.read(buffer);
				if (length == -1) {
					break;
				}
				crc.update(buffer, 0, length);
			}
		} catch (Exception e) {
			throw new TilePackerException("Could not calculate CRC32 for native library", e);
		}
		return Long.toString(crc.getValue(), 16);
	}

	private static File extractFile(ClassLoader classLoader, String nativeLibrary, String nativeLibraryCrc, File libraryFilePath) throws IOException {
		String extractedCrc = null;
		if (libraryFilePath.exists()) {
			try {
				extractedCrc = crc(new FileInputStream(libraryFilePath));
			} catch (FileNotFoundException ignored) {
			}
		}
		
		if (extractedCrc == null || !extractedCrc.equals(nativeLibraryCrc)) {
			try {
				InputStream inputStream = classLoader.getResourceAsStream(nativeLibrary);
				libraryFilePath.getParentFile().mkdirs();
				FileOutputStream outputStream = new FileOutputStream(libraryFilePath);
				byte[] buffer = new byte[4096];
				while (true) {
					int length = inputStream.read(buffer);
					if (length == -1) {
						break;
					}
					outputStream.write(buffer, 0, length);
				}
				inputStream.close();
				outputStream.close();
			} catch (IOException ex) {
				throw new TilePackerException("Could not extract " + nativeLibrary, ex);
			}
		}

		return libraryFilePath;
	}

	private static void loadFile(ClassLoader classLoader, String nativeLibrary, String nativeLibraryCrc, File libraryFilePath) {
		try {
			System.load(extractFile(classLoader, nativeLibrary, nativeLibraryCrc, libraryFilePath).getAbsolutePath());
		} catch (Exception e) {
			throw new TilePackerException("Could load load " + nativeLibrary, e);
		}
	}
}
