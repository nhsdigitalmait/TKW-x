<?xml version="1.0" encoding="UTF-8"?>
<!-- Validate FHIR PractitionerRoleDCH business rules -->
<!-- Currently this code ONLY validates the "additional population guidance" aka "business rules" for DCH Mesaging - Chris Brown July 2019-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fhir="http://hl7.org/fhir" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:output method="html" encoding="UTF-8" indent="yes" omit-xml-declaration="yes"/>
	<xsl:template match="/">
		<table border="0">
			<xsl:for-each select="//fhir:resource/fhir:PractitionerRole">
				<tr>
					<td style="color:#008000">
						<xsl:text>PASS:  PractitionerRole Id '</xsl:text>
						<xsl:value-of select="./fhir:id/@value"/>
						<xsl:text>'.</xsl:text>
					</td>
				</tr>
				
				
				<!-- element Ref to Organization is (1..1) (Business Rule) with a type of 'Organization' -->
				<xsl:call-template name="Mandatory_Resource_1_to_1">
					<xsl:with-param name="Param_Resource_Validating" select=" 'PractitionerRole' "/>
					<xsl:with-param name="Param_Resource_Target" select=" 'Organization' "/>
					<xsl:with-param name="Param_Field_Name" select=" 'organization' "/>
					<xsl:with-param name="Param_Resource_Path" select="./fhir:organization"/>
					<xsl:with-param name="Param_Resource_Reference_Path" select="./fhir:organization/fhir:reference/@value"/>
				</xsl:call-template>
				
				<!-- element Ref to Practitioner is (1..1) (Business Rule) with a type of 'Practitioner' -->
				<xsl:call-template name="Mandatory_Resource_1_to_1">
					<xsl:with-param name="Param_Resource_Validating" select=" 'PractitionerRole' "/>
					<xsl:with-param name="Param_Resource_Target" select=" 'Practitioner' "/>
					<xsl:with-param name="Param_Field_Name" select=" 'practitioner' "/>
					<xsl:with-param name="Param_Resource_Path" select="./fhir:practitioner"/>
					<xsl:with-param name="Param_Resource_Reference_Path" select="./fhir:practitioner/fhir:reference/@value"/>
				</xsl:call-template>
				
				
				<!-- validate PractitionerRole.code(careProfessionalType) here when example message available  -->
				<xsl:variable name="care_prof_code" select="./fhir:code/fhir:coding/fhir:system[@value='https://fhir.nhs.uk/STU3/ValueSet/DCH-ProfessionalType-1']/../fhir:code/@value"/>
				<xsl:variable name="care_prof_display" select="./fhir:code/fhir:coding/fhir:system[@value='https://fhir.nhs.uk/STU3/ValueSet/DCH-ProfessionalType-1']/../fhir:display/@value"/>
				<xsl:variable name="care_prof_count" select="count(./fhir:code/fhir:coding/fhir:system[@value='https://fhir.nhs.uk/STU3/ValueSet/DCH-ProfessionalType-1'])"/>
				
				
				<!-- look up contents of careProffessionalType against table  -->
				<xsl:variable name="cPT_Code" select="document('./DCH_Profession_Type_1_DCH_1.0.0-Beta.xml')"/>
				<xsl:variable name="reffile_code" select="$cPT_Code//Import/Row[Code=$care_prof_code]/Code"/>
				<xsl:variable name="reffile_display" select="$cPT_Code//Import/Row[Code=$care_prof_code]/Display"/>
				
				<!--<xsl:value-of select="$reffile_code"/>
				<xsl:value-of select="$reffile_display"/> -->
				
				<xsl:choose>
					<xsl:when test="$care_prof_count != 1">
						<tr>
							<td style="color:#900000">
								<xsl:text>ERROR:  Mandatory PractitionerRole code (careProfessionalType) does not have cardinality of 1..1 in xpath '</xsl:text>
								<xsl:call-template name="plotPath">
									<xsl:with-param name="forNode" select="./fhir:code[1]"/>
								</xsl:call-template>
								<xsl:text>'.</xsl:text>
							</td>
						</tr>
					</xsl:when>
					<xsl:when test="$care_prof_code=$reffile_code and $care_prof_display=$reffile_display ">
						<tr>
							<td style="color:#008000">
								<xsl:text>PASS:  Mandatory PractitionerRole (careProfessionalType) Code Coding and Display values are present and correct '</xsl:text>
								<xsl:value-of select="$care_prof_code"/>
								<xsl:text>' and '</xsl:text>
								<xsl:value-of select="$care_prof_display"/>
								<xsl:text>' in xpath '</xsl:text>
								<xsl:call-template name="plotPath">
									<xsl:with-param name="forNode" select="./fhir:code[1]"/>
								</xsl:call-template>
								<xsl:text>'.</xsl:text>
							</td>
						</tr>
					</xsl:when>
					<xsl:otherwise>
						<tr>
							<td style="color:#900000">
								<xsl:text>ERROR:  Mandatory PractitionerRole code (careProfessionalType) is not correct '</xsl:text>
								<xsl:value-of select="$care_prof_code"/>
								<xsl:text>' and '</xsl:text>
								<xsl:value-of select="$care_prof_display"/>
								<xsl:text>' in xpath '</xsl:text>
								<xsl:call-template name="plotPath">
									<xsl:with-param name="forNode" select="./fhir:code[1]"/>
								</xsl:call-template>
								<xsl:text>'.</xsl:text>
							</td>
						</tr>
					</xsl:otherwise>
				</xsl:choose>
				
				
				
				
				<!-- validate PractitionerRole.code(keyWorkerStatus)  here (1..1)when example message available  -->
				<xsl:variable name="key_worker_code" select="./fhir:code/fhir:coding/fhir:system[@value='https://fhir.nhs.uk/STU3/CodeSystem/DCH-KeyWorkerStatus-1']/../fhir:code/@value"/>
				<xsl:variable name="key_worker_display" select="./fhir:code/fhir:coding/fhir:system[@value='https://fhir.nhs.uk/STU3/CodeSystem/DCH-KeyWorkerStatus-1']/../fhir:display/@value"/>
				<xsl:variable name="key_worker_count" select="count(./fhir:code/fhir:coding/fhir:system[@value='https://fhir.nhs.uk/STU3/CodeSystem/DCH-KeyWorkerStatus-1'])"/>
				
				<xsl:choose>
					<xsl:when test="$key_worker_count != 1">
						<tr>
							<td style="color:#900000">
								<xsl:text>ERROR:  Mandatory PractitionerRole code keyWorkerStatus does not have cardinality of 1..1 in xpath '</xsl:text>
								<xsl:call-template name="plotPath">
									<xsl:with-param name="forNode" select="./fhir:code[1]"/>
								</xsl:call-template>
								<xsl:text>'.</xsl:text>
							</td>
						</tr>
					</xsl:when>
					<xsl:when test="$key_worker_code='keyWorker' and $key_worker_display='Is a Key Worker' ">
						<tr>
							<td style="color:#008000">
								<xsl:text>PASS:  Mandatory PractitionerRole (Key Worker Status) Code Coding and Display values are present and correct '</xsl:text>
								<xsl:value-of select="$key_worker_code"/>
								<xsl:text>' and '</xsl:text>
								<xsl:value-of select="$key_worker_display"/>
								<xsl:text>' in xpath '</xsl:text>
								<xsl:call-template name="plotPath">
									<xsl:with-param name="forNode" select="./fhir:code[1]"/>
								</xsl:call-template>
								<xsl:text>'.</xsl:text>
							</td>
						</tr>
					</xsl:when>
					<xsl:otherwise>
						<tr>
							<td style="color:#900000">
								<xsl:text>ERROR:  Mandatory PractitionerRole code (keyWorkerStatus) is not correct '</xsl:text>
								<xsl:value-of select="$key_worker_code"/>
								<xsl:text>' and '</xsl:text>
								<xsl:value-of select="$key_worker_display"/>
								<xsl:text>' in xpath '</xsl:text>
								<xsl:call-template name="plotPath">
									<xsl:with-param name="forNode" select="./fhir:code[1]"/>
								</xsl:call-template>
								<xsl:text>'.</xsl:text>
							</td>
						</tr>
					</xsl:otherwise>
					
				</xsl:choose>
				
				
				
				
				<!-- look up contents of DCH Speciality against table  -->
				<xsl:variable name="spec_code" select="./fhir:speciality/fhir:coding/fhir:code/@value"/>
				
				<xsl:variable name="S_Code" select="document('./DCH_Speciality_1_DCH_1.0.0-Beta.xml')"/>
				<xsl:variable name="reffile_s_code" select="$S_Code//Import/Row[Code=$spec_code]/Code"/>
				<xsl:variable name="reffile_s_display" select="$S_Code//Import/Row[Code=$spec_code]/Display"/>	
				<xsl:value-of select="$reffile_s_code"/>
				<xsl:value-of select="$reffile_s_display"/> 
				
							
				
				<!-- validate speciality is present (1..1) (Business Rule)   -->
				<xsl:choose>
					<xsl:when test="count(./fhir:speciality) != 1">
						<tr>
							<td style="color:#900000">
								<xsl:text>ERROR:  Mandatory PractitionerRole speciality does not have cardinality of 1..1 in xpath '</xsl:text>
								<xsl:call-template name="plotPath">
									<xsl:with-param name="forNode" select="."/>
								</xsl:call-template>
								<xsl:text>'.</xsl:text>
							</td>
						</tr>
					</xsl:when>
					<xsl:when test="./fhir:speciality/fhir:coding/fhir:code/@value =$reffile_s_code and ./fhir:speciality/fhir:coding/fhir:display/@value=$reffile_s_display ">
						<tr>
							<td style="color:#008000">
								<xsl:text>PASS:  Mandatory PractitionerRole speciality Code Coding and Display values are present and correct '</xsl:text>
								<xsl:value-of select="./fhir:speciality/fhir:coding/fhir:code/@value"/>
								<xsl:text>' and '</xsl:text>
								<xsl:value-of select="./fhir:speciality/fhir:coding/fhir:display/@value"/>
								<xsl:text>' in xpath '</xsl:text>
								<xsl:call-template name="plotPath">
									<xsl:with-param name="forNode" select="./fhir:speciality[1]"/>
								</xsl:call-template>
								<xsl:text>'.</xsl:text>
							</td>
						</tr>
					</xsl:when>
					<xsl:otherwise>
						<tr>
							<td style="color:#900000">
								<xsl:text>ERROR:  Mandatory PractitionerRole code speciality is not correct '</xsl:text>
								<xsl:value-of select="./fhir:speciality/fhir:coding/fhir:code/@value"/>
								<xsl:text>' and '</xsl:text>
								<xsl:value-of select="./fhir:speciality/fhir:coding/fhir:display/@value"/>
								<xsl:text>' in xpath '</xsl:text>
								<xsl:call-template name="plotPath">
									<xsl:with-param name="forNode" select="./fhir:speciality[1]"/>
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
