package com.dell.webservice.service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dell.webservice.exception.StorageException;

@Service
public class StorageService {
	
	@Value("${upload.path}")
	private String path;
	
	public void uploadFile(MultipartFile file) {
		// verify file size
		if(file.isEmpty()) {
			throw new StorageException("Failed to store a empty file !");
		}
		// file upload logic
		try {
			String filename = file.getOriginalFilename();
			InputStream insrc = file.getInputStream();
			Files.copy(insrc, Paths.get(path+filename),StandardCopyOption.REPLACE_EXISTING);
			
		} catch (Exception e) {
			throw new StorageException("Failed to upload a file "+file.getName());
		}
	}

	// get path for a file
	public String getPath(String filename) {
		return path + filename;
	}
	
}
