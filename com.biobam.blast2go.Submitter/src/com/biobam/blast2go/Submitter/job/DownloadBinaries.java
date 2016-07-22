package com.biobam.blast2go.Submitter.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.biobam.blast2go.api.b2gjob.NullProgressMonitor;
import com.biobam.blast2go.api.utils.FileUtils;
import com.biobam.blast2go.api.utils.net.OSUtils.OSValidator;
import com.biobam.blast2go.preferences.proxy.VersionChecker;

public class DownloadBinaries {

	static String download(String b2gFilePath) {


		String b2gSubmitterExecutable = b2gFilePath + (b2gFilePath.endsWith(File.separator) ? "" : File.separator) + "NCBISubmitter" + File.separator + "tbl2asn";
		String enlace = null;
		File executable = new File(b2gSubmitterExecutable);
		if (!executable.exists()) {
			executable.getParentFile().mkdirs();
		} else {
			return b2gSubmitterExecutable;

		}

		if (OSValidator.isMac()) {
			enlace = "ftp://ftp.ncbi.nih.gov/toolbox/ncbi_tools/converters/by_program/tbl2asn/mac.tbl2asn.gz";
		} else if (OSValidator.isUnix()) {
			enlace = "ftp://ftp.ncbi.nih.gov/toolbox/ncbi_tools/converters/by_program/tbl2asn/linux64.tbl2asn.gz";
		} else if (OSValidator.isWindows()) {
			enlace = "ftp://ftp.ncbi.nih.gov/toolbox/ncbi_tools/converters/by_program/tbl2asn/win.tbl2asn.zip";
			b2gSubmitterExecutable += ".exe";
		}

		File tempFile;
		// YO SOLITO :C
		if (OSValidator.isWindows()) {
			byte[] buffer = new byte[1024];
			File directory = executable.getParentFile();
			File zipDownload = new File(directory.getAbsolutePath() + File.separator + "tbl2asnBin.zip");
			try {
				if (!directory.exists()) {
					directory.mkdirs();
				}
				VersionChecker.download(zipDownload.getAbsolutePath(), enlace, new NullProgressMonitor());

				ZipInputStream zis = new ZipInputStream(new FileInputStream(zipDownload));
				ZipEntry ze = zis.getNextEntry();
				while (ze != null) {
					String fileName = ze.getName();
					File newFile = new File(directory + File.separator + fileName);
					FileOutputStream fos = new FileOutputStream(newFile);
					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}

					fos.close();
					ze = zis.getNextEntry();

				}
				zis.closeEntry();
				zis.close();

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			try {
				tempFile = File.createTempFile("tbl2asnBin", ".gz");

				VersionChecker.download(tempFile.getAbsolutePath(), enlace, new NullProgressMonitor());
				FileUtils.gunzipIt(tempFile.getAbsolutePath(), b2gSubmitterExecutable);
				executable.setExecutable(true);
				tempFile.delete();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return b2gSubmitterExecutable;

	}
}
