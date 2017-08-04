package com.seassoon.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

import java.util.Locale;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * File for HSSF testing/examples
 *
 * THIS IS NOT THE MAIN HSSF FILE!! This is a utility for testing functionality.
 * It does contain sample API usage that may be educational to regular API
 * users.
 */
public class ExcelTest {



	private DataFormatter formatter;


	private Workbook workbook;


	private FormulaEvaluator evaluator;

	private void openWorkbook(File file) throws FileNotFoundException, IOException, InvalidFormatException {
		FileInputStream fis = null;
		try {
			System.out.println("Opening workbook [" + file.getName() + "]");

			fis = new FileInputStream(file);

			// Open the workbook and then create the FormulaEvaluator and
			// DataFormatter instances that will be needed to, respectively,
			// force evaluation of forumlae found in cells and create a
			// formatted String encapsulating the cells contents.
			this.workbook = WorkbookFactory.create(fis);
			this.evaluator = this.workbook.getCreationHelper().createFormulaEvaluator();
			this.formatter = new DataFormatter(true);
			
		} finally {
			if (fis != null) {
				fis.close();
			}
		}
	}
}
