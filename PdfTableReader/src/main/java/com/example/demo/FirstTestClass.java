package com.example.demo;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

public class FirstTestClass {

	public File getFilePathAndName(){
	 	File selectedFile = null;
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setCurrentDirectory(new File("/User/alvinreyes"));
         
        int result = jFileChooser.showOpenDialog(new JFrame());
        
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = jFileChooser.getSelectedFile();
        }
        
        return selectedFile;
    }
	
   public static void main(String args[]) throws IOException {
	   
	   FirstTestClass m = new FirstTestClass();
   	
	   File file = m.getFilePathAndName();
	   String filePath = file.getAbsolutePath();
	   String filename=file.getName(); 
   	
	   try {
			//Create PdfReader instance.
			PdfReader pdfReader = new PdfReader(filePath);	
		 
			//Get the number of pages in pdf.
			int pages = pdfReader.getNumberOfPages(); 
		 
			//Iterate the pdf through pages.
			for(int i=1; i<=pages; i++) { 
			  //Extract the page content using PdfTextExtractor.
			  String pageContent = 
			  	PdfTextExtractor.getTextFromPage(pdfReader, i);
		 
			  //Print the page content on console.
			  System.out.println("Content on Page "+ i + ": @|||||@" + pageContent);
		      }
		 
		      //Close the PdfReader.
		      pdfReader.close();
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		  }
		}

