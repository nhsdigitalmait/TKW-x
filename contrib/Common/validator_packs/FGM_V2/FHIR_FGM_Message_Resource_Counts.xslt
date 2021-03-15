<?xml version="1.0" encoding="UTF-8"?>
<!-- Check MessageHeader data reference has correct format. -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:fhir="http://hl7.org/fhir">
	<xsl:output method="html" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<table border="0">
			<xsl:variable name="event_code" select="substring(//fhir:MessageHeader/fhir:event/fhir:code/@value,39)"/>
			
			<xsl:variable name="bundle_count" select="count(//fhir:Bundle)"/>
			
			<xsl:variable name="header_count" select="(count(//fhir:resource/fhir:MessageHeader))+(count(//fhir:contained/fhir:MessageHeader))"/>
			
			<xsl:variable name="flag_count" select="(count(//fhir:resource/fhir:Flag) ) +(count(//fhir:contained/fhir:Flag) )"/>
			
			<xsl:variable name="parameters_count" select="(count(//fhir:resource/fhir:Parameters) ) +(count(//fhir:contained/fhir:Parameters) )"/>
			
			<xsl:variable name="spine_organisation_count" select="(count(//fhir:resource/fhir:Organization[fhir:meta/fhir:profile/@value='http://fhir.nhs.net/StructureDefinition/spine-organization-1-0']) ) +(count(//fhir:contained/fhir:Organization[fhir:meta/fhir:profile/@value='http://fhir.nhs.net/StructureDefinition/spine-organization-1-0']) )"/>
			
			<xsl:variable name="gp_organisation_count" select="(count(//fhir:resource/fhir:Organization[fhir:meta/fhir:profile/@value='http://fhir.nhs.net/StructureDefinition/spine-gp-organization-1-0']) ) +(count(//fhir:contained/fhir:Organization[fhir:meta/fhir:profile/@value='http://fhir.nhs.net/StructureDefinition/spine-gp-organization-1-0']) )"/>
			
			<xsl:variable name="practitioner_count" select="(count(//fhir:resource/fhir:Practitioner) ) +(count(//fhir:contained/fhir:Practitioner) )"/>
			
			<xsl:variable name="patient_count" select="(count(//fhir:resource/fhir:Patient) ) +(count(//fhir:contained/fhir:Patient) )"/>
			
			<!-- tests common to all messages in FGM -->
			<xsl:if test="$bundle_count=1">
				<tr>
					<td style="color:#008000">
						<xsl:text>PASS: The correct number of Bundle resources is present for a </xsl:text>
						<xsl:value-of select="$event_code"/>
					</td>
				</tr>
			</xsl:if>
			<xsl:if test="$bundle_count !=1">
				<tr>
					<td style="color:#900000">
						<xsl:text>ERROR: The correct number of Bundle resources is not present for a </xsl:text>
						<xsl:value-of select="$event_code"/>
					</td>
				</tr>
			</xsl:if>
			<xsl:if test="$header_count=1">
				<tr>
					<td style="color:#008000">
						<xsl:text>PASS: The correct number of MessageHeader resources is present for a </xsl:text>
						<xsl:value-of select="$event_code"/>
					</td>
				</tr>
			</xsl:if>
			<xsl:if test="$header_count !=1">
				<tr>
					<td style="color:#900000">
						<xsl:text>ERROR: The correct number of MessageHeader resources is not present for a </xsl:text>
						<xsl:value-of select="$event_code"/>
					</td>
				</tr>
			</xsl:if>
			<xsl:if test="$practitioner_count=1">
				<tr>
					<td style="color:#008000">
						<xsl:text>PASS: The correct number of Practitioner resources is present for a </xsl:text>
						<xsl:value-of select="$event_code"/>
					</td>
				</tr>
			</xsl:if>
			<xsl:if test="$practitioner_count !=1">
				<tr>
					<td style="color:#900000">
						<xsl:text>ERROR: The correct number of Practitioner resources is not present for a </xsl:text>
						<xsl:value-of select="$event_code"/>
					</td>
				</tr>
			</xsl:if>
			<xsl:if test="$spine_organisation_count=1">
				<tr>
					<td style="color:#008000">
						<xsl:text>PASS: The correct number of (spine) Organization resources is present for a </xsl:text>
						<xsl:value-of select="$event_code"/>
					</td>
				</tr>
			</xsl:if>
			<xsl:if test="$spine_organisation_count !=1">
				<tr>
					<td style="color:#900000">
						<xsl:text>ERROR: The correct number of (spine) Organization resources is not present for a </xsl:text>
						<xsl:value-of select="$event_code"/>
					</td>
				</tr>
			</xsl:if>
			
			<xsl:choose>
				<xsl:when test="$event_code =  'FGMQuery_2_0'">
					
							<xsl:if test="$flag_count =1">
						<tr>
							<td style="color:#900000">
								<xsl:text>ERROR: The Flag resources should not be present for a </xsl:text>
								<xsl:value-of select="$event_code"/>
							</td>
						</tr>
					</xsl:if>
					
					<xsl:if test="$gp_organisation_count =1">
						<tr>
							<td style="color:#900000">
								<xsl:text>ERROR: The (GP) Organization resources should not be present for a </xsl:text>
								<xsl:value-of select="$event_code"/>
							</td>
						</tr>
					</xsl:if>
										
					<xsl:if test="$patient_count =1">
						<tr>
							<td style="color:#900000">
								<xsl:text>ERROR: The correct number of Patient resources should not be present for a </xsl:text>
								<xsl:value-of select="$event_code"/>
							</td>
						</tr>
					</xsl:if>
				</xsl:when>
				
				<xsl:when test="$event_code =  'FGMCreate_1_0'">
					<xsl:if test="$flag_count=1">
						<tr>
							<td style="color:#008000">
								<xsl:text>PASS: The correct number of Flag resources is present for a </xsl:text>
								<xsl:value-of select="$event_code"/>
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="$flag_count !=1">
						<tr>
							<td style="color:#900000">
								<xsl:text>ERROR: The correct number of Flag resources is not present for a </xsl:text>
								<xsl:value-of select="$event_code"/>
							</td>
						</tr>
					</xsl:if>
					
					<xsl:if test="$gp_organisation_count=1">
						<tr>
							<td style="color:#008000">
								<xsl:text>PASS: The correct number of (GP) Organization resources is present for a </xsl:text>
								<xsl:value-of select="$event_code"/>
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="$gp_organisation_count !=1">
						<tr>
							<td style="color:#900000">
								<xsl:text>ERROR: The correct number of (GP) Organization resources is not present for a </xsl:text>
								<xsl:value-of select="$event_code"/>
							</td>
						</tr>
					</xsl:if>
					
					<xsl:if test="$patient_count=1">
						<tr>
							<td style="color:#008000">
								<xsl:text>PASS: The correct number of Patient resources is present for a </xsl:text>
								<xsl:value-of select="$event_code"/>
							</td>
						</tr>
					</xsl:if>
					<xsl:if test="$patient_count !=1">
						<tr>
							<td style="color:#900000">
								<xsl:text>ERROR: The correct number of Patient resources is not present for a </xsl:text>
								<xsl:value-of select="$event_code"/>
							</td>
						</tr>
					</xsl:if>
				</xsl:when>
				
				<xsl:when test="$event_code =  'FGMDelete_1_0'">
									
					<xsl:if test="$flag_count =1">
						<tr>
							<td style="color:#900000">
								<xsl:text>ERROR: The Flag resources should not be present for a </xsl:text>
								<xsl:value-of select="$event_code"/>
							</td>
						</tr>
					</xsl:if>
					
					<xsl:if test="$gp_organisation_count =1">
						<tr>
							<td style="color:#900000">
								<xsl:text>ERROR: The (GP) Organization resources should not be present for a </xsl:text>
								<xsl:value-of select="$event_code"/>
							</td>
						</tr>
					</xsl:if>
					
					<xsl:if test="$patient_count =1">
						<tr>
							<td style="color:#900000">
								<xsl:text>ERROR: The correct number of Patient resources should not be present for a </xsl:text>
								<xsl:value-of select="$event_code"/>
							</td>
						</tr>
					</xsl:if>
						
					</xsl:when>
				<xsl:otherwise>
					<!-- Do Nothing. -->
				</xsl:otherwise>
			</xsl:choose>
		</table>
	</xsl:template>
</xsl:stylesheet>
