<?xml version="1.0" encoding="UTF-8"?>
<!-- Validate FHIR Procedure resource details. Expandable for future Procedure validations-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fhir="http://hl7.org/fhir" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:output method="html" encoding="UTF-8" indent="yes" omit-xml-declaration="yes"/>
	<xsl:template match="/">
		<table border="0">
			<xsl:for-each select="//fhir:resource/fhir:Procedure">
				<tr>
					<td style="color:#008000">
						<xsl:text>PASS:  Procedure Id '</xsl:text>
						<xsl:value-of select="./fhir:id/@value"/>
						<xsl:text>'.</xsl:text>
					</td>
				</tr>
				
				<!-- If IDENTIFIER is present then SYSTEM & VALUE are madatory -->
				<xsl:for-each select="./fhir:identifier">
					<xsl:variable name="identifier_system" select="./fhir:system/@value"/>
					<xsl:variable name="identifier_value" select="./fhir:value/@value"/>
					<xsl:choose>
						<xsl:when test="$identifier_system and (string-length($identifier_system) &gt; 0)">
							<tr>
								<td style="color:#008000">
									<xsl:text>PASS:  Mandatory Procedure Identifier System is present and populated;  '</xsl:text>
									<xsl:value-of select="$identifier_system"/>
									<xsl:text>' in xpath '</xsl:text>
									<xsl:call-template name="plotPath">
										<xsl:with-param name="forNode" select="./fhir:system/@value"/>
									</xsl:call-template>
									<xsl:text>'.</xsl:text>
								</td>
							</tr>
						</xsl:when>
						<xsl:otherwise>
							<tr>
								<td style="color:#900000">
									<xsl:text>ERROR: Procedure Identifier System is mandatory  '</xsl:text>
									<xsl:value-of select="$identifier_system"/>
									<xsl:text>' in xpath '</xsl:text>
									<xsl:call-template name="plotPath">
										<xsl:with-param name="forNode" select="."/>
									</xsl:call-template>
									<xsl:text>'.</xsl:text>
								</td>
							</tr>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:choose>
						<xsl:when test="$identifier_value and (string-length($identifier_value) &gt; 0)">
							<tr>
								<td style="color:#008000">
									<xsl:text>PASS:  Mandatory Procedure Identifier Value is present and populated;  '</xsl:text>
									<xsl:value-of select="$identifier_value"/>
									<xsl:text>' in xpath '</xsl:text>
									<xsl:call-template name="plotPath">
										<xsl:with-param name="forNode" select="./fhir:value/@value"/>
									</xsl:call-template>
									<xsl:text>'.</xsl:text>
								</td>
							</tr>
						</xsl:when>
						<xsl:otherwise>
							<tr>
								<td style="color:#900000">
									<xsl:text>ERROR:   Mandatory Procedure Identifier Value is mandatory '</xsl:text>
									<xsl:value-of select="$identifier_value"/>
									<xsl:text>' in xpath '</xsl:text>
									<xsl:call-template name="plotPath">
										<xsl:with-param name="forNode" select="."/>
									</xsl:call-template>
									<xsl:text>'.</xsl:text>
								</td>
							</tr>
						</xsl:otherwise>
					</xsl:choose>
					
					<!-- If Ref to Assigner (0..1) is present it must be to an Organization  -->
					<xsl:call-template name="Optional_Resource_0_or_1">
						<xsl:with-param name="Param_Resource_Validating" select=" 'Procedure' "/>
						<xsl:with-param name="Param_Resource_Target" select=" 'Organization' "/>
						<xsl:with-param name="Param_Field_Name" select=" 'Assigner' "/>
						<xsl:with-param name="Param_Resource_Path" select="./fhir:assigner"/>
						<xsl:with-param name="Param_Resource_Reference_Path" select="./fhir:assigner/fhir:reference/@value"/>
					</xsl:call-template>
					
				</xsl:for-each>
				
				<!-- SUBJECT is mandatory -->
				<xsl:variable name="procedure_subject" select="./fhir:subject"/>
				<xsl:variable name="procedure_subject_reference" select="./fhir:subject/fhir:reference/@value"/>
				<!--<xsl:if test="$procedure_subject">-->
					<xsl:choose>
						<xsl:when test="(count(./fhir:subject) &gt; 1) or (count(./fhir:subject/fhir:reference/@value) &gt; 1)">
							<tr>
								<td style="color:#900000">
									<xsl:text>ERROR:  No more than one Procedure SUBJECT can be present. Here there are '</xsl:text>
									<xsl:value-of select="count(./fhir:subject/fhir:reference/@value)"/>
									<xsl:text>' in xpath '</xsl:text>
									<xsl:call-template name="plotPath">
										<xsl:with-param name="forNode" select="./fhir:subject/fhir:reference[1]"/>
									</xsl:call-template>
									<xsl:text>'.</xsl:text>
								</td>
							</tr>
						</xsl:when>
						<xsl:when test="(count(./fhir:subject) = 1) and (count(./fhir:subject/fhir:reference/@value) = 1)">
							<xsl:call-template name="Test_Resource_Type">
								<xsl:with-param name="Param_Resource_Ref" select="./fhir:subject/fhir:reference/@value"/>
								<xsl:with-param name="Param_Valid_Resource" select=" 'Patient' "/>
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<tr>
								<td style="color:#900000">
									<xsl:text>ERROR: Procedure Subject is mandatory  '</xsl:text>
									<xsl:value-of select="$procedure_subject_reference"/>
									<xsl:text>' in xpath '</xsl:text>
									<xsl:call-template name="plotPath">
										<xsl:with-param name="forNode" select="."/>
									</xsl:call-template>
									<xsl:text>'.</xsl:text>
								</td>
							</tr>
						</xsl:otherwise>
					</xsl:choose>
				<!--</xsl:if>-->
				
				<!-- check  mandatory STATUS is present -->
				<xsl:variable name="procedure_status" select="./fhir:status/@value"/>
				<xsl:choose>
					<xsl:when test="$procedure_status=('in-progress', 'aborted', 'completed', 'entered-in-error')">
						<tr>
							<td style="color:#008000">
								<xsl:text>PASS:  Mandatory Procedure Status is present and valid '</xsl:text>
								<xsl:value-of select="$procedure_status"/>
								<xsl:text>' in xpath '</xsl:text>
								<xsl:call-template name="plotPath">
									<xsl:with-param name="forNode" select="./fhir:status"/>
								</xsl:call-template>
								<xsl:text>'.</xsl:text>
							</td>
						</tr>
					</xsl:when>
					<xsl:otherwise>
						<tr>
							<td style="color:#900000">
								<xsl:text>ERROR:  Mandatory Procedure Status is invalid or not present  '</xsl:text>
								<xsl:value-of select="$procedure_status"/>
								<xsl:text>' in xpath '</xsl:text>
								<xsl:call-template name="plotPath">
									<xsl:with-param name="forNode" select="."/>
								</xsl:call-template>
								<xsl:text>'.</xsl:text>
							</td>
						</tr>
					</xsl:otherwise>
				</xsl:choose>
				
				<xsl:for-each select="./fhir:performer">
					<!-- If Ref toActor (0..1) is present it must be to an RelatedPerson Organization Patient Practitioner  -->
					<xsl:call-template name="Optional_Resource_0_or_1">
						<xsl:with-param name="Param_Resource_Validating" select=" 'Procedure' "/>
						<xsl:with-param name="Param_Resource_Target" select=" 'RelatedPerson Organization Patient Practitioner' "/>
						<xsl:with-param name="Param_Field_Name" select=" 'Actor' "/>
						<xsl:with-param name="Param_Resource_Path" select="./fhir:actor"/>
						<xsl:with-param name="Param_Resource_Reference_Path" select="./fhir:actor/fhir:reference/@value"/>
					</xsl:call-template>
				</xsl:for-each>
				
				<!-- If Ref Encounter (0..1) is present it must be to an Encounter  -->
				<xsl:call-template name="Optional_Resource_0_or_1">
					<xsl:with-param name="Param_Resource_Validating" select=" 'Procedure' "/>
					<xsl:with-param name="Param_Resource_Target" select=" 'Encounter' "/>
					<xsl:with-param name="Param_Field_Name" select=" 'Encounter' "/>
					<xsl:with-param name="Param_Resource_Path" select="./fhir:encounter"/>
					<xsl:with-param name="Param_Resource_Reference_Path" select="./fhir:encounter/fhir:reference/@value"/>
				</xsl:call-template>
				
				
				<!-- If Ref Location (0..1) is present it must be to a Location  -->
				<xsl:call-template name="Optional_Resource_0_or_1">
					<xsl:with-param name="Param_Resource_Validating" select=" 'Procedure' "/>
					<xsl:with-param name="Param_Resource_Target" select=" 'Location' "/>
					<xsl:with-param name="Param_Field_Name" select=" 'Location' "/>
					<xsl:with-param name="Param_Resource_Path" select="./fhir:location"/>
					<xsl:with-param name="Param_Resource_Reference_Path" select="./fhir:location/fhir:reference/@value"/>
				</xsl:call-template>
				
				
				<!-- If Ref Report (0..*) is present it must be to a DiagnosticReport  -->
				<xsl:for-each select="./fhir:report">
					<xsl:call-template name="Test_Resource_Type">
						<xsl:with-param name="Param_Resource_Ref" select="./fhir:reference/@value"/>
						<xsl:with-param name="Param_Valid_Resource" select=" 'DiagnosticReport' "/>
					</xsl:call-template>
				</xsl:for-each>
				
				<!-- If Ref Request (0..1) is present it must be to a Careplan DiagnosticOrder ProcedureRequest ReferralRequest  -->
				<xsl:call-template name="Optional_Resource_0_or_1">
					<xsl:with-param name="Param_Resource_Validating" select=" 'Procedure' "/>
					<xsl:with-param name="Param_Resource_Target" select=" 'Careplan DiagnosticOrder ProcedureRequest ReferralRequest' "/>
					<xsl:with-param name="Param_Field_Name" select=" 'Request' "/>
					<xsl:with-param name="Param_Resource_Path" select="./fhir:request"/>
					<xsl:with-param name="Param_Resource_Reference_Path" select="./fhir:request/fhir:reference/@value"/>
				</xsl:call-template>
				
				
				<!-- When NOTES is present then TEXT is mandatory -->
				<xsl:for-each select="./fhir:notes">
					<xsl:variable name="procedure_notes_text" select="./fhir:text/@value"/>
					<xsl:choose>
						<xsl:when test="($procedure_notes_text and string-length($procedure_notes_text)&gt; 0)">
							<tr>
								<td style="color:#008000">
									<xsl:text>PASS:  Mandatory Procedure Notes Text is present and valid '</xsl:text>
									<xsl:value-of select="$procedure_notes_text"/>
									<xsl:text>' in xpath '</xsl:text>
									<xsl:call-template name="plotPath">
										<xsl:with-param name="forNode" select="./fhir:text"/>
									</xsl:call-template>
									<xsl:text>'.</xsl:text>
								</td>
							</tr>
						</xsl:when>
						<xsl:otherwise>
							<tr>
								<td style="color:#900000">
									<xsl:text>ERROR:  Mandatory Procedure  Notes Text not present  '</xsl:text>
									<xsl:value-of select="$procedure_notes_text"/>
									<xsl:text>' in xpath '</xsl:text>
									<xsl:call-template name="plotPath">
										<xsl:with-param name="forNode" select="."/>
									</xsl:call-template>
									<xsl:text>'.</xsl:text>
								</td>
							</tr>
						</xsl:otherwise>
					</xsl:choose>
					
					<!-- If Ref Author (0..1) is present it must be to an RelatedPerson String Patient Practitioner  -->
					<xsl:call-template name="Optional_Resource_0_or_1">
						<xsl:with-param name="Param_Resource_Validating" select=" 'Procedure' "/>
						<xsl:with-param name="Param_Resource_Target" select=" 'RelatedPerson String Patient Practitioner' "/>
						<xsl:with-param name="Param_Field_Name" select=" 'Author' "/>
						<xsl:with-param name="Param_Resource_Path" select="./fhir:author"/>
						<xsl:with-param name="Param_Resource_Reference_Path" select="./fhir:author/fhir:reference/@value"/>
					</xsl:call-template>	
					
				</xsl:for-each>
				
				<!-- When FOCALDEVICE is present then MANIPULATED is mandatory with type of 'Device'-->
				<xsl:for-each select="./fhir:focalDevice">
					<xsl:variable name="procedure_device_manipulated" select="./fhir:manipulated/fhir:reference/@value"/>
					<xsl:choose>
						<xsl:when test="(count(./fhir:manipulated) &gt; 1) or (count(./fhir:manipulated/fhir:reference/@value) &gt; 1)">
							<tr>
								<td style="color:#900000">
									<xsl:text>ERROR:  No more than one Procedure FocalDevice MANIPULATED can be present. Here there are '</xsl:text>
									<xsl:value-of select="count(./fhir:manipulated/fhir:reference/@value)"/>
									<xsl:text>' in xpath '</xsl:text>
									<xsl:call-template name="plotPath">
										<xsl:with-param name="forNode" select="./fhir:manipulated/fhir:manipulated[1]"/>
									</xsl:call-template>
									<xsl:text>'.</xsl:text>
								</td>
							</tr>
						</xsl:when>
						<xsl:when test="(count(./fhir:manipulated) = 1) and (count(./fhir:manipulated/fhir:reference/@value) = 1)">
							<xsl:call-template name="Test_Resource_Type">
								<xsl:with-param name="Param_Resource_Ref" select="./fhir:manipulated/fhir:reference/@value"/>
								<xsl:with-param name="Param_Valid_Resource" select=" 'Device' "/>
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<tr>
								<td style="color:#900000">
									<xsl:text>ERROR: Procedure FocalDevice Manipulated is mandatory  '</xsl:text>
									<xsl:value-of select="$procedure_device_manipulated"/>
									<xsl:text>' in xpath '</xsl:text>
									<xsl:call-template name="plotPath">
										<xsl:with-param name="forNode" select="."/>
									</xsl:call-template>
									<xsl:text>'.</xsl:text>
								</td>
							</tr>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:for-each>
				
				<!-- If Ref Used (0..*) is present it must be to a type of 'Device Substance Medication' -->
				<xsl:for-each select="./fhir:used">
					<xsl:call-template name="Test_Resource_Type">
						<xsl:with-param name="Param_Resource_Ref" select="./fhir:reference/@value"/>
						<xsl:with-param name="Param_Valid_Resource" select=" 'Device Substance Medication' "/>
					</xsl:call-template>
				</xsl:for-each>
				
				<!--  DCH Specific Validation Here Onwards -->
				<!-- Identify message type from Header here -->
				<xsl:variable name="event_type" select="./../../../fhir:entry/fhir:resource/fhir:MessageHeader/fhir:event/fhir:display/@value"/>
				<xsl:choose>
				<!-- Newborn Hearing  -->
					<xsl:when test="$event_type = 'Newborn Hearing' ">
						<tr>
							<td style="color:#008000">
								<xsl:text>PASS:  Newborn Hearing '</xsl:text>
								<xsl:text>'.</xsl:text>
							</td>
						</tr>
						
						<xsl:choose>
							<xsl:when test="./fhir:code/fhir:coding/fhir:display/@value='Automated auditory brainstem response test' and ./fhir:code/fhir:coding/fhir:code/@value=413083006 and ./fhir:code/fhir:coding/fhir:system/@value='http://snomed.info/sct' ">
								<tr>
									<td style="color:#008000">
										<xsl:text>PASS:  Mandatory Procedure (AABR Hearing Test) Code System, Coding and Display values are present and correct '</xsl:text>
										<xsl:value-of select="./fhir:code/fhir:coding/fhir:system/@value"/>
										<xsl:text>' and '</xsl:text>
										<xsl:value-of select="./fhir:code/fhir:coding/fhir:code/@value"/>
										<xsl:text>' and '</xsl:text>
										<xsl:value-of select="./fhir:code/fhir:coding/fhir:display/@value"/>
										<xsl:text>' in xpath '</xsl:text>
										<xsl:call-template name="plotPath">
											<xsl:with-param name="forNode" select="./fhir:type/fhir:coding"/>
										</xsl:call-template>
										<xsl:text>'.</xsl:text>
									</td>
								</tr>
							</xsl:when>
							<xsl:when test="./fhir:code/fhir:coding/fhir:display/@value='Automated otoacoustic emission test' and ./fhir:code/fhir:coding/fhir:code/@value=446077009 and ./fhir:code/fhir:coding/fhir:system/@value='http://snomed.info/sct' ">
								<tr>
									<td style="color:#008000">
										<xsl:text>PASS:  Mandatory Procedure (AOAE Hearing Test) Code System, Coding and Display values are present and correct '</xsl:text>
										<xsl:value-of select="./fhir:code/fhir:coding/fhir:system/@value"/>
										<xsl:text>' and '</xsl:text>
										<xsl:value-of select="./fhir:code/fhir:coding/fhir:code/@value"/>
										<xsl:text>' and '</xsl:text>
										<xsl:value-of select="./fhir:code/fhir:coding/fhir:display/@value"/>
										<xsl:text>' in xpath '</xsl:text>
										<xsl:call-template name="plotPath">
											<xsl:with-param name="forNode" select="./fhir:type/fhir:coding"/>
										</xsl:call-template>
										<xsl:text>'.</xsl:text>
									</td>
								</tr>
							</xsl:when>
							<xsl:otherwise>
								<tr>
									<td style="color:#900000">
										<xsl:text>ERROR:  Mandatory Procedure (Hearing Test) Code System, Coding and Display values are not correct  '</xsl:text>
										<xsl:value-of select="./fhir:code/fhir:coding/fhir:system/@value"/>
										<xsl:text>' and '</xsl:text>
										<xsl:value-of select="./fhir:code/fhir:coding/fhir:code/@value"/>
										<xsl:text>' and '</xsl:text>
										<xsl:value-of select="./fhir:code/fhir:coding/fhir:display/@value"/>
										<xsl:text>' in xpath '</xsl:text>
										<xsl:text>' in xpath '</xsl:text>
										<xsl:call-template name="plotPath">
											<xsl:with-param name="forNode" select="."/>
										</xsl:call-template>
										<xsl:text>'.</xsl:text>
									</td>
								</tr>
							</xsl:otherwise>	
						</xsl:choose>
					</xsl:when>
					
										
					<!-- Blood Spot Test Outcome  -->
					<xsl:when test="$event_type = 'Blood Spot Test Outcome' ">
						<tr>
							<td style="color:#008000">
								<xsl:text>PASS:  Blood Spot Test Outcome '</xsl:text>
								<xsl:text>'.</xsl:text>
							</td>
						</tr>
					  
					
						<xsl:choose>
								<xsl:when test="./fhir:code/fhir:coding/fhir:display/@value='Phenylketonuria screening test' and ./fhir:code/fhir:coding/fhir:code/@value=314081000 and ./fhir:code/fhir:coding/fhir:system/@value='http://snomed.info/sct' ">
									<tr>
										<td style="color:#008000">
											<xsl:text>PASS:  Mandatory Procedure (Blood Spot Screening, Phenylketonuria) Code System, Coding and Display values are present and correct '</xsl:text>
											<xsl:value-of select="./fhir:code/fhir:coding/fhir:system/@value"/>
											<xsl:text>' and '</xsl:text>
											<xsl:value-of select="./fhir:code/fhir:coding/fhir:code/@value"/>
											<xsl:text>' and '</xsl:text>
											<xsl:value-of select="./fhir:code/fhir:coding/fhir:display/@value"/>
											<xsl:text>' in xpath '</xsl:text>
											<xsl:call-template name="plotPath">
												<xsl:with-param name="forNode" select="./fhir:type/fhir:coding"/>
											</xsl:call-template>
											<xsl:text>'.</xsl:text>
										</td>
									</tr>
								</xsl:when>
								<xsl:when test="./fhir:code/fhir:coding/fhir:display/@value='Sickle cell disease screening test' and ./fhir:code/fhir:coding/fhir:code/@value=314090007 and ./fhir:code/fhir:coding/fhir:system/@value='http://snomed.info/sct' ">
									<tr>
										<td style="color:#008000">
											<xsl:text>PASS:  Mandatory Procedure (Blood Spot Screening, Sickle Cell Disease) Code System, Coding and Display values are present and correct '</xsl:text>
											<xsl:value-of select="./fhir:code/fhir:coding/fhir:system/@value"/>
											<xsl:text>' and '</xsl:text>
											<xsl:value-of select="./fhir:code/fhir:coding/fhir:code/@value"/>
											<xsl:text>' and '</xsl:text>
											<xsl:value-of select="./fhir:code/fhir:coding/fhir:display/@value"/>
											<xsl:text>' in xpath '</xsl:text>
											<xsl:call-template name="plotPath">
												<xsl:with-param name="forNode" select="./fhir:type/fhir:coding"/>
											</xsl:call-template>
											<xsl:text>'.</xsl:text>
										</td>
									</tr>
								</xsl:when>
								<xsl:when test="./fhir:code/fhir:coding/fhir:display/@value='Cystic fibrosis screening test' and ./fhir:code/fhir:coding/fhir:code/@value=314080004 and ./fhir:code/fhir:coding/fhir:system/@value='http://snomed.info/sct' ">
									<tr>
										<td style="color:#008000">
											<xsl:text>PASS:  Mandatory Procedure (Blood Spot Screening, Cystic Fibrosis) Code System, Coding and Display values are present and correct '</xsl:text>
											<xsl:value-of select="./fhir:code/fhir:coding/fhir:system/@value"/>
											<xsl:text>' and '</xsl:text>
											<xsl:value-of select="./fhir:code/fhir:coding/fhir:code/@value"/>
											<xsl:text>' and '</xsl:text>
											<xsl:value-of select="./fhir:code/fhir:coding/fhir:display/@value"/>
											<xsl:text>' in xpath '</xsl:text>
											<xsl:call-template name="plotPath">
												<xsl:with-param name="forNode" select="./fhir:type/fhir:coding"/>
											</xsl:call-template>
											<xsl:text>'.</xsl:text>
										</td>
									</tr>
								</xsl:when>
								<xsl:when test="./fhir:code/fhir:coding/fhir:display/@value='Congenital hypothyroidism screening test' and ./fhir:code/fhir:coding/fhir:code/@value=400984005 and ./fhir:code/fhir:coding/fhir:system/@value='http://snomed.info/sct' ">
									<tr>
										<td style="color:#008000">
											<xsl:text>PASS:  Mandatory Procedure (Blood Spot Screening, Congenital Hypothyroidism) Code System, Coding and Display values are present and correct '</xsl:text>
											<xsl:value-of select="./fhir:code/fhir:coding/fhir:system/@value"/>
											<xsl:text>' and '</xsl:text>
											<xsl:value-of select="./fhir:code/fhir:coding/fhir:code/@value"/>
											<xsl:text>' and '</xsl:text>
											<xsl:value-of select="./fhir:code/fhir:coding/fhir:display/@value"/>
											<xsl:text>' in xpath '</xsl:text>
											<xsl:call-template name="plotPath">
												<xsl:with-param name="forNode" select="./fhir:type/fhir:coding"/>
											</xsl:call-template>
											<xsl:text>'.</xsl:text>
										</td>
									</tr>
								</xsl:when>
								<xsl:when test="./fhir:code/fhir:coding/fhir:display/@value='Medium-chain acyl-coenzyme A dehydrogenase deficiency screening test' and ./fhir:code/fhir:coding/fhir:code/@value=428056008 and ./fhir:code/fhir:coding/fhir:system/@value='http://snomed.info/sct' ">
									<tr>
										<td style="color:#008000">
											<xsl:text>PASS:  Mandatory Procedure (Blood Spot Screening, Medium-chain Acyl-Coenzyme A Dehydrogenase Deficiency) Code System, Coding and Display values are present and correct '</xsl:text>
											<xsl:value-of select="./fhir:code/fhir:coding/fhir:system/@value"/>
											<xsl:text>' and '</xsl:text>
											<xsl:value-of select="./fhir:code/fhir:coding/fhir:code/@value"/>
											<xsl:text>' and '</xsl:text>
											<xsl:value-of select="./fhir:code/fhir:coding/fhir:display/@value"/>
											<xsl:text>' in xpath '</xsl:text>
											<xsl:call-template name="plotPath">
												<xsl:with-param name="forNode" select="./fhir:type/fhir:coding"/>
											</xsl:call-template>
											<xsl:text>'.</xsl:text>
										</td>
									</tr>
								</xsl:when>
								<xsl:when test="./fhir:code/fhir:coding/fhir:display/@value='Blood spot homocystinuria screening test' and ./fhir:code/fhir:coding/fhir:code/@value=940201000000107 and ./fhir:code/fhir:coding/fhir:system/@value='http://snomed.info/sct' ">
									<tr>
										<td style="color:#008000">
											<xsl:text>PASS:  Mandatory Procedure (Blood Spot Screening, Homocystinuria) Code System, Coding and Display values are present and correct '</xsl:text>
											<xsl:value-of select="./fhir:code/fhir:coding/fhir:system/@value"/>
											<xsl:text>' and '</xsl:text>
											<xsl:value-of select="./fhir:code/fhir:coding/fhir:code/@value"/>
											<xsl:text>' and '</xsl:text>
											<xsl:value-of select="./fhir:code/fhir:coding/fhir:display/@value"/>
											<xsl:text>' in xpath '</xsl:text>
											<xsl:call-template name="plotPath">
												<xsl:with-param name="forNode" select="./fhir:type/fhir:coding"/>
											</xsl:call-template>
											<xsl:text>'.</xsl:text>
										</td>
									</tr>
								</xsl:when>
								<xsl:when test="./fhir:code/fhir:coding/fhir:display/@value='Blood spot maple syrup urine disease screening test' and ./fhir:code/fhir:coding/fhir:code/@value=940221000000103 and ./fhir:code/fhir:coding/fhir:system/@value='http://snomed.info/sct' ">
									<tr>
										<td style="color:#008000">
											<xsl:text>PASS:  Mandatory Procedure (Blood Spot Screening, Maple Syrup Urine Disease) Code System, Coding and Display values are present and correct '</xsl:text>
											<xsl:value-of select="./fhir:code/fhir:coding/fhir:system/@value"/>
											<xsl:text>' and '</xsl:text>
											<xsl:value-of select="./fhir:code/fhir:coding/fhir:code/@value"/>
											<xsl:text>' and '</xsl:text>
											<xsl:value-of select="./fhir:code/fhir:coding/fhir:display/@value"/>
											<xsl:text>' in xpath '</xsl:text>
											<xsl:call-template name="plotPath">
												<xsl:with-param name="forNode" select="./fhir:type/fhir:coding"/>
											</xsl:call-template>
											<xsl:text>'.</xsl:text>
										</td>
									</tr>
								</xsl:when>
								<xsl:when test="./fhir:code/fhir:coding/fhir:display/@value='Blood spot glutaric aciduria type 1 screening test' and ./fhir:code/fhir:coding/fhir:code/@value=940131000000109 and ./fhir:code/fhir:coding/fhir:system/@value='http://snomed.info/sct' ">
									<tr>
										<td style="color:#008000">
											<xsl:text>PASS:  Mandatory Procedure (Blood Spot Screening, Glutaric Aciduria Type 1) Code System, Coding and Display values are present and correct '</xsl:text>
											<xsl:value-of select="./fhir:code/fhir:coding/fhir:system/@value"/>
											<xsl:text>' and '</xsl:text>
											<xsl:value-of select="./fhir:code/fhir:coding/fhir:code/@value"/>
											<xsl:text>' and '</xsl:text>
											<xsl:value-of select="./fhir:code/fhir:coding/fhir:display/@value"/>
											<xsl:text>' in xpath '</xsl:text>
											<xsl:call-template name="plotPath">
												<xsl:with-param name="forNode" select="./fhir:type/fhir:coding"/>
											</xsl:call-template>
											<xsl:text>'.</xsl:text>
										</td>
									</tr>
								</xsl:when>
								<xsl:when test="./fhir:code/fhir:coding/fhir:display/@value='Blood spot isovaleric acidaemia screening test' and ./fhir:code/fhir:coding/fhir:code/@value=940151000000102 and ./fhir:code/fhir:coding/fhir:system/@value='http://snomed.info/sct' ">
									<tr>
										<td style="color:#008000">
											<xsl:text>PASS:  Mandatory Procedure (Blood Spot Screening, Isovaleric Acidaemia) Code System, Coding and Display values are present and correct '</xsl:text>
											<xsl:value-of select="./fhir:code/fhir:coding/fhir:system/@value"/>
											<xsl:text>' and '</xsl:text>
											<xsl:value-of select="./fhir:code/fhir:coding/fhir:code/@value"/>
											<xsl:text>' and '</xsl:text>
											<xsl:value-of select="./fhir:code/fhir:coding/fhir:display/@value"/>
											<xsl:text>' in xpath '</xsl:text>
											<xsl:call-template name="plotPath">
												<xsl:with-param name="forNode" select="./fhir:type/fhir:coding"/>
											</xsl:call-template>
											<xsl:text>'.</xsl:text>
										</td>
									</tr>
								</xsl:when>
								<xsl:otherwise>
									<tr>
										<td style="color:#900000">
											<xsl:text>ERROR:  Mandatory Procedure (Blood Spot Test Outcome) Code System, Coding and Display values are not correct  '</xsl:text>
											<xsl:value-of select="./fhir:code/fhir:coding/fhir:system/@value"/>
											<xsl:text>' and '</xsl:text>
											<xsl:value-of select="./fhir:code/fhir:coding/fhir:code/@value"/>
											<xsl:text>' and '</xsl:text>
											<xsl:value-of select="./fhir:code/fhir:coding/fhir:display/@value"/>
											<xsl:text>' in xpath '</xsl:text>
											<xsl:text>' in xpath '</xsl:text>
											<xsl:call-template name="plotPath">
												<xsl:with-param name="forNode" select="."/>
											</xsl:call-template>
											<xsl:text>'.</xsl:text>
										</td>
									</tr>
								</xsl:otherwise>	
							</xsl:choose>
						</xsl:when>
					
					
					
					                  
					<!-- NIPE Outcome  -->
					<xsl:when test="$event_type = 'NIPE outcome' ">
						<tr>
							<td style="color:#008000">
								<xsl:text>PASS:  NIPE outcome '</xsl:text>
								<xsl:text>'.</xsl:text>
							</td>
						</tr>
						
						<xsl:choose>
							<xsl:when test="./fhir:code/fhir:coding/fhir:display/@value='Newborn and Infant Physical Examination Screening Programme, hip examination' and ./fhir:code/fhir:coding/fhir:code/@value=985531000000102 and ./fhir:code/fhir:coding/fhir:system/@value='http://snomed.info/sct' ">
								<tr>
									<td style="color:#008000">
										<xsl:text>PASS:  Mandatory Procedure (Physical Examination, Hips) Code System, Coding and Display values are present and correct '</xsl:text>
										<xsl:value-of select="./fhir:code/fhir:coding/fhir:system/@value"/>
										<xsl:text>' and '</xsl:text>
										<xsl:value-of select="./fhir:code/fhir:coding/fhir:code/@value"/>
										<xsl:text>' and '</xsl:text>
										<xsl:value-of select="./fhir:code/fhir:coding/fhir:display/@value"/>
										<xsl:text>' in xpath '</xsl:text>
										<xsl:call-template name="plotPath">
											<xsl:with-param name="forNode" select="./fhir:type/fhir:coding"/>
										</xsl:call-template>
										<xsl:text>'.</xsl:text>
									</td>
								</tr>
							</xsl:when>
							<xsl:when test="./fhir:code/fhir:coding/fhir:display/@value='Newborn and Infant Physical Examination Screening Programme, eye examination' and ./fhir:code/fhir:coding/fhir:code/@value=988361000000105 and ./fhir:code/fhir:coding/fhir:system/@value='http://snomed.info/sct' ">
								<tr>
									<td style="color:#008000">
										<xsl:text>PASS:  Mandatory Procedure (Physical Examination, Eyes) Code System, Coding and Display values are present and correct '</xsl:text>
										<xsl:value-of select="./fhir:code/fhir:coding/fhir:system/@value"/>
										<xsl:text>' and '</xsl:text>
										<xsl:value-of select="./fhir:code/fhir:coding/fhir:code/@value"/>
										<xsl:text>' and '</xsl:text>
										<xsl:value-of select="./fhir:code/fhir:coding/fhir:display/@value"/>
										<xsl:text>' in xpath '</xsl:text>
										<xsl:call-template name="plotPath">
											<xsl:with-param name="forNode" select="./fhir:type/fhir:coding"/>
										</xsl:call-template>
										<xsl:text>'.</xsl:text>
									</td>
								</tr>
							</xsl:when>
							<xsl:when test="./fhir:code/fhir:coding/fhir:display/@value='Newborn and Infant Physical Examination Screening Programme, testis examination' and ./fhir:code/fhir:coding/fhir:code/@value=988371000000103 and ./fhir:code/fhir:coding/fhir:system/@value='http://snomed.info/sct' ">
								<tr>
									<td style="color:#008000">
										<xsl:text>PASS:  Mandatory Procedure (Physical Examination, Testes) Code System, Coding and Display values are present and correct '</xsl:text>
										<xsl:value-of select="./fhir:code/fhir:coding/fhir:system/@value"/>
										<xsl:text>' and '</xsl:text>
										<xsl:value-of select="./fhir:code/fhir:coding/fhir:code/@value"/>
										<xsl:text>' and '</xsl:text>
										<xsl:value-of select="./fhir:code/fhir:coding/fhir:display/@value"/>
										<xsl:text>' in xpath '</xsl:text>
										<xsl:call-template name="plotPath">
											<xsl:with-param name="forNode" select="./fhir:type/fhir:coding"/>
										</xsl:call-template>
										<xsl:text>'.</xsl:text>
									</td>
								</tr>
							</xsl:when>							
							<xsl:when test="./fhir:code/fhir:coding/fhir:display/@value='Newborn and Infant Physical Examination Screening Programme, heart examination' and ./fhir:code/fhir:coding/fhir:code/@value=988371000000103 and ./fhir:code/fhir:coding/fhir:system/@value='http://snomed.info/sct' ">
								<tr>
									<td style="color:#008000">
										<xsl:text>PASS:  Mandatory Procedure (Physical Examination, Heart) Code System, Coding and Display values are present and correct '</xsl:text>
										<xsl:value-of select="./fhir:code/fhir:coding/fhir:system/@value"/>
										<xsl:text>' and '</xsl:text>
										<xsl:value-of select="./fhir:code/fhir:coding/fhir:code/@value"/>
										<xsl:text>' and '</xsl:text>
										<xsl:value-of select="./fhir:code/fhir:coding/fhir:display/@value"/>
										<xsl:text>' in xpath '</xsl:text>
										<xsl:call-template name="plotPath">
											<xsl:with-param name="forNode" select="./fhir:type/fhir:coding"/>
										</xsl:call-template>
										<xsl:text>'.</xsl:text>
									</td>
								</tr>
							</xsl:when>			
							<xsl:otherwise>
								<tr>
									<td style="color:#900000">
										<xsl:text>ERROR:  Mandatory Procedure (NIPE outcome) Code System, Coding and Display values are not correct  '</xsl:text>
										<xsl:value-of select="./fhir:code/fhir:coding/fhir:system/@value"/>
										<xsl:text>' and '</xsl:text>
										<xsl:value-of select="./fhir:code/fhir:coding/fhir:code/@value"/>
										<xsl:text>' and '</xsl:text>
										<xsl:value-of select="./fhir:code/fhir:coding/fhir:display/@value"/>
										<xsl:text>' in xpath '</xsl:text>
										<xsl:text>' in xpath '</xsl:text>
										<xsl:call-template name="plotPath">
											<xsl:with-param name="forNode" select="."/>
										</xsl:call-template>
										<xsl:text>'.</xsl:text>
									</td>
								</tr>
							</xsl:otherwise>	
						</xsl:choose>
					</xsl:when>
					
					<!-- Other so fail  -->
					<xsl:otherwise>
					<tr>
							<td style="color:#900000">
								<xsl:text>ERROR:  Unexpected Event Type. Not validating business rules'</xsl:text>
								<xsl:text>'.</xsl:text>
							</td>
						</tr>
					</xsl:otherwise>
				</xsl:choose>
				
				<!-- this test common to all 3 event types. Currently not checking values as sets too large  -->
				<xsl:choose>
						<xsl:when test="(count(./fhir:outcome/fhir:coding) &gt; 1)">
							<tr>
								<td style="color:#900000">
									<xsl:text>ERROR:  No more than one Procedure Outcome Coding can be present. Here there are '</xsl:text>
									<xsl:value-of select="count(./fhir:outcome/fhir:coding)"/>
									<xsl:text>' in xpath '</xsl:text>
									<xsl:call-template name="plotPath">
										<xsl:with-param name="forNode" select="./fhir:outcome/fhir:coding[1]"/>
									</xsl:call-template>
									<xsl:text>'.</xsl:text>
								</td>
							</tr>
						</xsl:when>
						<xsl:when test="(count(./fhir:outcome/fhir:coding) = 1)">
							<tr>
								<td style="color:#008000">
									<xsl:text>PASS:  Mandatory Procedure Outcome Coding is present in xpath '</xsl:text>
										<xsl:call-template name="plotPath">
										<xsl:with-param name="forNode" select="./fhir:outcome/fhir:coding"/>
									</xsl:call-template>
									<xsl:text>'.</xsl:text>
								</td>
							</tr>
						</xsl:when>
						<xsl:otherwise>
							<tr>
								<td style="color:#900000">
									<xsl:text>ERROR: Procedure Outcome Coding is mandatory in xpath  '</xsl:text>
										<xsl:call-template name="plotPath">
										<xsl:with-param name="forNode" select="."/>
									</xsl:call-template>
									<xsl:text>'.</xsl:text>
								</td>
							</tr>
						</xsl:otherwise>
					</xsl:choose>
				
			</xsl:for-each>
		</table>
	</xsl:template>
	
	<xsl:include href="FHIR_Generic_Reference_Check_v1.0.xslt"/>				
	<xsl:include href="FHIR_Generic_Get_Xpath_v1.0.xslt"/>
	<xsl:include href="FHIR_Generic_Resource_Type_Check_v1.0.xslt"/>
</xsl:stylesheet>
