<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fhir="http://hl7.org/fhir" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<xsl:output method="html" encoding="UTF-8" indent="yes" omit-xml-declaration="yes"/>

	
	<!--                                                                                  PARAMETERS  Required when calling                                                                        -->
	<!-- Param_Resource_Ref   - Value of Resource Reference i.e. xpath to resource reference e.g. "./fhir:asserter/fhir:reference/@value"                         -->
	<!-- Param_Valid_Resource - Valid Resource Types for this reference to be (provide as a space seperated list) e.g. 'Practitioner Patient Organization' -->
	<!-- Code check that he Resource referenced is of the type specified in the parameter(s)                                                                                        --> 
	<!-- Check that URL resolves to a unique identifier, if not report and exit.																											      -->
	
	<xsl:template name="Test_Resource_Type">
		<xsl:param name="Param_Resource_Ref"/>
		<xsl:param name="Param_Valid_Resource"/>
		
			<xsl:variable name="url_reference_count" select="count(//fhir:entry/fhir:fullUrl[@value=$Param_Resource_Ref]/..//fhir:resource/child::*[1])"/>		
			<xsl:variable name="relative_reference_count" select="count(//fhir:resource/*/fhir:id[@value=(substring-after($Param_Resource_Ref, '/'))]/..)"/>
			<xsl:variable name="contained_reference_count" select="count(//fhir:resource/*/fhir:id[@value=(substring-after($Param_Resource_Ref, '#'))]/..)"/>
		
			<!-- if the URL is not unique !!! then report error and do not validate any further -->
			<xsl:choose>
					<xsl:when test="(($url_reference_count &gt; 1) or ($relative_reference_count &gt; 1) or ($contained_reference_count &gt; 1))"> 
						<tr>
							<td style="color:#900000">
								<xsl:text>ERROR -  Reference '</xsl:text>
								<xsl:value-of select="$Param_Resource_Ref"/>
								<xsl:text>' in xpath '</xsl:text>
								<xsl:call-template name="plotPath">
									<xsl:with-param name="forNode" select="$Param_Resource_Ref"/>
								</xsl:call-template>
								<xsl:text> is not unique. More than one resource has this indentifier. </xsl:text>	
							</td>
						</tr>
					</xsl:when>
					
					<!-- validate away  -->
					<xsl:otherwise>
					
						<xsl:variable name="url_reference" select="name(//fhir:entry/fhir:fullUrl[@value=$Param_Resource_Ref]/..//fhir:resource/child::*[1])"/>
						<xsl:variable name="relative_reference" select="name(//fhir:resource/*/fhir:id[@value=(substring-after($Param_Resource_Ref, '/'))]/..)"/>
						<xsl:variable name="contained_reference" select="name(//fhir:resource/*/fhir:id[@value=(substring-after($Param_Resource_Ref, '#'))]/..) "/>
						<!-- Test reference is to the correct resource type -->	
						<xsl:choose>
							
							<xsl:when test="($url_reference) and (contains($Param_Valid_Resource, $url_reference)) ">
								<tr>
									<td style="color:#008000">
										<xsl:text>PASS - Reference '</xsl:text>
										<xsl:value-of select="$Param_Resource_Ref"/>
										<xsl:text>' is to one of '</xsl:text>
										<xsl:value-of select="$Param_Valid_Resource"/>
										<xsl:text>' using full URL reference  in xpath '</xsl:text>
										<xsl:call-template name="plotPath">
											<xsl:with-param name="forNode" select="$Param_Resource_Ref"/>
										</xsl:call-template>  
										<xsl:text>' </xsl:text>
									</td>
								</tr>
							</xsl:when>
							
							<xsl:when test="($relative_reference) and (contains( $Param_Valid_Resource, $relative_reference)) ">
								<tr>
									<td style="color:#008000">
										<xsl:text>PASS - Reference '</xsl:text>
										<xsl:value-of select="$Param_Resource_Ref"/>
										<xsl:text>' is to one of '</xsl:text>
										<xsl:value-of select="$Param_Valid_Resource"/>
										 <xsl:text>' using relative resource reference in xpath '</xsl:text>
										<xsl:call-template name="plotPath">
											<xsl:with-param name="forNode" select="$Param_Resource_Ref"/>
										</xsl:call-template>  
										<xsl:text>' </xsl:text>
									</td>
								</tr>
							</xsl:when>
							
							<xsl:when test="($contained_reference) and  (contains($Param_Valid_Resource, $contained_reference)) ">
								<tr>
									<td style="color:#008000">
										<xsl:text>PASS - Reference '</xsl:text>
										<xsl:value-of select="$Param_Resource_Ref"/>
										<xsl:text>' is to one of '</xsl:text>
										<xsl:value-of select="$Param_Valid_Resource"/>
										<xsl:text>' using contained resource reference in xpath'</xsl:text>
										<xsl:call-template name="plotPath">
											<xsl:with-param name="forNode" select="$Param_Resource_Ref"/>
										</xsl:call-template>
										<xsl:text>' </xsl:text>
									</td>
								</tr>
							</xsl:when>
												
						<xsl:otherwise>
							<tr>
								<td style="color:#900000">
									<xsl:text>ERROR -  Reference '</xsl:text>
									<xsl:value-of select="$Param_Resource_Ref"/>
									<xsl:text>' is not found or is to an incorrect resource type in xpath '</xsl:text>
									<xsl:call-template name="plotPath">
										<xsl:with-param name="forNode" select="$Param_Resource_Ref"/>
									</xsl:call-template>
									<xsl:text>' </xsl:text>
									<xsl:text>Expected to map to one of resource types '</xsl:text>	
										<xsl:value-of select="$Param_Valid_Resource"/>
									<xsl:text>'</xsl:text>
								</td>
							</tr>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>	
			</xsl:choose>	
				
			<!--</table>-->
		</xsl:template>
	</xsl:stylesheet>
