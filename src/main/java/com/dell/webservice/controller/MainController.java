package com.dell.webservice.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.dell.webservice.exception.StorageException;
import com.dell.webservice.service.StorageService;

@Controller
public class MainController {
	
	@Autowired
	StorageService storageService;
	
	
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String index() {
		return "index.html";
	}
	
	@RequestMapping(value="/do-upload", method=RequestMethod.POST, consumes= {"multipart/form-data"})
	public String upload(@RequestParam MultipartFile file) {
		storageService.uploadFile(file);
		return "redirect:/success.html";
	}
	
	
	@RequestMapping(value="/download", method=RequestMethod.GET)
	public ResponseEntity<InputStreamResource> downloadFile(@RequestParam(value="filename", required=true)String filename) {
		
		File file;
		InputStreamResource resource;
		try {
			file = new File(storageService.getPath(filename));
			resource = new InputStreamResource(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new StorageException("Falied to download file "+filename);
		}
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));
		
		return ResponseEntity.ok().headers(headers).contentLength(file.length())
				.contentType(MediaType.parseMediaType("application/text"))
				.body(resource);
		
	}
	
	
	@ExceptionHandler(StorageException.class)
	public String handleStorageException() {
		return "redirect:/failure.html";
	}
}
