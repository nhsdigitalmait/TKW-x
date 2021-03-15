<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fhir="http://hl7.org/fhir" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:output method="html" encoding="UTF-8" indent="yes" omit-xml-declaration="yes"/>
	
<!--																																																						-->
<!-- 															Validate References to Resources with cardinality (1..1)   																			-->	
<!--																																																						-->

<xsl:template name="Mandatory_Resource_1_to_1">
		<xsl:param name="Param_Resource_Validating"/> 			<!-- Name of the Resource to be validated  -->
		<xsl:param name="Param_Field_Name"/>						<!-- Name of the Field to be validated -->
		<xsl:param name="Param_Resource_Target"/>				<!-- Resource Type that the Resource should resolve to (e.g. 'Patient', Organization etc) -->
		<xsl:param name="Param_Resource_Path"/>					<!-- Path of the resource -->
		<xsl:param name="Param_Resource_Reference_Path"/> <!-- Path of Reference to the Resource -->

	<xsl:choose>
		<xsl:when test="(count($Param_Resource_Path) &gt; 1) or (count($Param_Resource_Reference_Path) &gt; 1)">
			<tr>
				<td style="color:#900000">
					<xsl:text>ERROR:  No more than one </xsl:text>
					<xsl:value-of select="$Param_Resource_Validating"/>
					<xsl:text> </xsl:text>
					<xsl:value-of select="upper-case($Param_Field_Name)"/>
					<xsl:text> can be present. Here there are '</xsl:text>
					<xsl:value-of select="count($Param_Resource_Path)"/>
					<xsl:text>' </xsl:text>
					<xsl:value-of select="$Param_Field_Name"/>
					<xsl:text> and '</xsl:text>
					<xsl:value-of select="count($Param_Resource_Reference_Path)"/>
					<xsl:text>' </xsl:text>
					<xsl:value-of select="$Param_Field_Name"/>
					<xsl:text> References in xpath '</xsl:text>
					<xsl:call-template name="plotPath">
						<xsl:with-param name="forNode" select="$Param_Resource_Path[1]"/>
					</xsl:call-template>
					<xsl:text>'.</xsl:text>
				</td>
			</tr>
		</xsl:when>
		<xsl:when test="(count($Param_Resource_Path) = 1)and  (count($Param_Resource_Reference_Path) = 1)">
				<xsl:call-template name="Test_Resource_Type">
					<xsl:with-param name="Param_Resource_Ref" select="$Param_Resource_Reference_Path"/>
					<xsl:with-param name="Param_Valid_Resource" select="$Param_Resource_Target"/>
				</xsl:call-template>
			</xsl:when>
		<xsl:otherwise>
			<tr>
				<td style="color:#900000">
					<xsl:text>ERROR:  </xsl:text>
					 <xsl:value-of select="$Param_Resource_Validating"/>
					<xsl:text> </xsl:text>
					<xsl:value-of select="upper-case($Param_Field_Name)"/>
					<xsl:text> is mandatory '</xsl:text>
					<xsl:value-of select="count($Param_Resource_Path)"/>
					<xsl:text>' in xpath '</xsl:text>
					<xsl:call-template name="plotPath">
						<xsl:with-param name="forNode" select="."/>
					</xsl:call-template>
					<xsl:text>'.</xsl:text>
				</td>
			</tr>
		</xsl:otherwise>
	</xsl:choose>
					
	</xsl:template>		
	
	
<!--																																																						-->
<!-- 															Validate References to Resources with cardinality (0..1)   																			-->	
<!--																																																						-->

	<xsl:template name="Optional_Resource_0_or_1">
		<xsl:param name="Param_Resource_Validating"/> 			 <!-- Name of the Resource to be validated  -->
		<xsl:param name="Param_Field_Name"/>						<!-- Name of the Field to be validated -->
		<xsl:param name="Param_Resource_Target"/>				<!-- Resource Type that the Resource should resolve to (e.g. 'Patient', Organization etc) -->
		<xsl:param name="Param_Resource_Path"/>					<!-- Path of the resource -->
		<xsl:param name="Param_Resource_Reference_Path"/> <!-- Path of Reference to the Resource -->
		
		<xsl:choose>
			<xsl:when test="(count($Param_Resource_Path) &gt; 1) or (count($Param_Resource_Reference_Path) &gt; 1)">
				<tr>
					<td style="color:#900000">
						<xsl:text>ERROR:  No more than one </xsl:text>
						<xsl:value-of select="$Param_Resource_Validating"/>
						<xsl:text> </xsl:text>
						<xsl:value-of select="upper-case($Param_Field_Name)"/>
						<xsl:text> can be present. Here there are '</xsl:text>
						<xsl:value-of select="count($Param_Resource_Path)"/>
						<xsl:text>' </xsl:text>
						<xsl:value-of select="$Param_Field_Name"/>
						<xsl:text> and '</xsl:text>
						<xsl:value-of select="count($Param_Resource_Reference_Path)"/>
						<xsl:text>' </xsl:text>
						<xsl:value-of select="$Param_Field_Name"/>
						<xsl:text> References in xpath '</xsl:text>
						<xsl:call-template name="plotPath">
							<xsl:with-param name="forNode" select="$Param_Resource_Path[1]"/>
						</xsl:call-template>
						<xsl:text>'.</xsl:text>
					</td>
				</tr>
			</xsl:when>
			<xsl:when test="(count($Param_Resource_Path) = 1)and  (count($Param_Resource_Reference_Path) = 1)">
				<xsl:call-template name="Test_Resource_Type">
					<xsl:with-param name="Param_Resource_Ref" select="$Param_Resource_Reference_Path"/>
					<xsl:with-param name="Param_Valid_Resource" select="$Param_Resource_Target"/>
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
	</xsl:template>		
	
		</xsl:stylesheet>
