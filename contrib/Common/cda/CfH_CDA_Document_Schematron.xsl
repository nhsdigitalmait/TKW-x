<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<xsl:stylesheet xmlns:iso="http://www.ascc.net/xml/schematron"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:sch="http://www.ascc.net/xml/schematron"
                xmlns:hl7v3="urn:hl7-org:v3"
                xmlns:npfitlc="NPFIT:HL7:Localisation"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                hl7v3:dummy-for-xmlns=""
                npfitlc:dummy-for-xmlns=""
                xsi:dummy-for-xmlns=""
                version="1.0"><!--Implementers: please note that overriding process-prolog or process-root is 
    the preferred method for meta-stylesheets to use where possible. The name or details of 
    this mode may change during 1Q 2007.-->


<!--PHASES-->


<!--PROLOG-->
<xsl:output xmlns:xs="http://www.w3.org/2001/XMLSchema"
               xmlns:svrl="http://www.ascc.net/xml/schematron"
               method="xml"
               omit-xml-declaration="no"
               standalone="yes"
               indent="yes"/>

   <!--KEYS-->


<!--DEFAULT RULES-->


<!--MODE: SCHEMATRON-FULL-PATH-->
<!--This mode can be used to generate an ugly though full XPath for locators-->
<xsl:template match="*" mode="schematron-get-full-path">
      <xsl:apply-templates select="parent::*" mode="schematron-get-full-path"/>
      <xsl:text>/</xsl:text>
      <xsl:choose>
         <xsl:when test="namespace-uri()=''">
            <xsl:value-of select="name()"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:text>*:</xsl:text>
            <xsl:value-of select="local-name()"/>
            <xsl:text>[namespace-uri()='</xsl:text>
            <xsl:value-of select="namespace-uri()"/>
            <xsl:text>']</xsl:text>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:variable name="preceding"
                    select="count(preceding-sibling::*[local-name()=local-name(current())                                   and namespace-uri() = namespace-uri(current())])"/>
      <xsl:text>[</xsl:text>
      <xsl:value-of select="1+ $preceding"/>
      <xsl:text>]</xsl:text>
   </xsl:template>
   <xsl:template match="@*" mode="schematron-get-full-path">
      <xsl:apply-templates select="parent::*" mode="schematron-get-full-path"/>
      <xsl:text>/</xsl:text>
      <xsl:choose>
         <xsl:when test="namespace-uri()=''">@schema</xsl:when>
         <xsl:otherwise>
            <xsl:text>@*[local-name()='</xsl:text>
            <xsl:value-of select="local-name()"/>
            <xsl:text>' and namespace-uri()='</xsl:text>
            <xsl:value-of select="namespace-uri()"/>
            <xsl:text>']</xsl:text>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>

   <!--MODE: SCHEMATRON-FULL-PATH-2-->
<!--This mode can be used to generate prefixed XPath for humans-->
<xsl:template match="node()" mode="schematron-get-full-path-2">
      <xsl:for-each select="ancestor-or-self::*">
         <xsl:text>/</xsl:text>
         <xsl:value-of select="name(.)"/>
         <xsl:if test="preceding-sibling::*[name(.)=name(current())]">
            <xsl:text>[</xsl:text>
            <xsl:value-of select="count(preceding-sibling::*[name(.)=name(current())])+1"/>
            <xsl:text>]</xsl:text>
         </xsl:if>
      </xsl:for-each>
      <xsl:if test="not(self::*)">
         <xsl:text/>/@<xsl:value-of select="name(.)"/>
      </xsl:if>
   </xsl:template>

   <!--MODE: GENERATE-ID-FROM-PATH -->
<xsl:template match="/" mode="generate-id-from-path"/>
   <xsl:template match="text()" mode="generate-id-from-path">
      <xsl:apply-templates select="parent::*" mode="generate-id-from-path"/>
      <xsl:value-of select="concat('.text-', 1+count(preceding-sibling::text()), '-')"/>
   </xsl:template>
   <xsl:template match="comment()" mode="generate-id-from-path">
      <xsl:apply-templates select="parent::*" mode="generate-id-from-path"/>
      <xsl:value-of select="concat('.comment-', 1+count(preceding-sibling::comment()), '-')"/>
   </xsl:template>
   <xsl:template match="processing-instruction()" mode="generate-id-from-path">
      <xsl:apply-templates select="parent::*" mode="generate-id-from-path"/>
      <xsl:value-of select="concat('.processing-instruction-', 1+count(preceding-sibling::processing-instruction()), '-')"/>
   </xsl:template>
   <xsl:template match="@*" mode="generate-id-from-path">
      <xsl:apply-templates select="parent::*" mode="generate-id-from-path"/>
      <xsl:value-of select="concat('.@', name())"/>
   </xsl:template>
   <xsl:template match="*" mode="generate-id-from-path" priority="-0.5">
      <xsl:apply-templates select="parent::*" mode="generate-id-from-path"/>
      <xsl:text>.</xsl:text>
      <xsl:choose>
         <xsl:when test="count(. | ../namespace::*) = count(../namespace::*)">
            <xsl:value-of select="concat('.namespace::-',1+count(namespace::*),'-')"/>
         </xsl:when>
         <xsl:otherwise>
            <xsl:value-of select="concat('.',name(),'-',1+count(preceding-sibling::*[name()=name(current())]),'-')"/>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>
   <!--Strip characters--><xsl:template match="text()" priority="-1"/>

   <!--SCHEMA METADATA-->
<xsl:template match="/">
      <svrl:schematron-output xmlns:xs="http://www.w3.org/2001/XMLSchema"
                              xmlns:svrl="http://www.ascc.net/xml/schematron"
                              title="NPfIT Schematron for CDA documents"
                              schemaVersion="">
         <svrl:ns-prefix-in-attribute-values uri="urn:hl7-org:v3" prefix="hl7v3"/>
         <svrl:ns-prefix-in-attribute-values uri="NPFIT:HL7:Localisation" prefix="npfitlc"/>
         <svrl:ns-prefix-in-attribute-values uri="http://www.w3.org/2001/XMLSchema-instance" prefix="xsi"/>
         <svrl:active-pattern>
            <xsl:apply-templates/>
         </svrl:active-pattern>
         <xsl:apply-templates select="/" mode="M4"/>
         <svrl:active-pattern>
            <xsl:apply-templates/>
         </svrl:active-pattern>
         <xsl:apply-templates select="/" mode="M5"/>
         <svrl:active-pattern>
            <xsl:apply-templates/>
         </svrl:active-pattern>
         <xsl:apply-templates select="/" mode="M6"/>
         <svrl:active-pattern>
            <xsl:apply-templates/>
         </svrl:active-pattern>
         <xsl:apply-templates select="/" mode="M7"/>
         <svrl:active-pattern>
            <xsl:apply-templates/>
         </svrl:active-pattern>
         <xsl:apply-templates select="/" mode="M8"/>
         <svrl:active-pattern>
            <xsl:apply-templates/>
         </svrl:active-pattern>
         <xsl:apply-templates select="/" mode="M9"/>
         <svrl:active-pattern>
            <xsl:apply-templates/>
         </svrl:active-pattern>
         <xsl:apply-templates select="/" mode="M10"/>
         <svrl:active-pattern>
            <xsl:apply-templates/>
         </svrl:active-pattern>
         <xsl:apply-templates select="/" mode="M11"/>
         <svrl:active-pattern>
            <xsl:apply-templates/>
         </svrl:active-pattern>
         <xsl:apply-templates select="/" mode="M12"/>
         <svrl:active-pattern>
            <xsl:apply-templates/>
         </svrl:active-pattern>
         <xsl:apply-templates select="/" mode="M13"/>
         <svrl:active-pattern>
            <xsl:apply-templates/>
         </svrl:active-pattern>
         <xsl:apply-templates select="/" mode="M14"/>
         <svrl:active-pattern>
            <xsl:apply-templates/>
         </svrl:active-pattern>
         <xsl:apply-templates select="/" mode="M15"/>
         <svrl:active-pattern>
            <xsl:apply-templates/>
         </svrl:active-pattern>
         <xsl:apply-templates select="/" mode="M16"/>
         <svrl:active-pattern>
            <xsl:apply-templates/>
         </svrl:active-pattern>
         <xsl:apply-templates select="/" mode="M17"/>
         <svrl:active-pattern>
            <xsl:apply-templates/>
         </svrl:active-pattern>
         <xsl:apply-templates select="/" mode="M18"/>
         <svrl:active-pattern>
            <xsl:apply-templates/>
         </svrl:active-pattern>
         <xsl:apply-templates select="/" mode="M19"/>
         <svrl:active-pattern>
            <xsl:apply-templates/>
         </svrl:active-pattern>
         <xsl:apply-templates select="/" mode="M20"/>
         <svrl:active-pattern>
            <xsl:apply-templates/>
         </svrl:active-pattern>
         <xsl:apply-templates select="/" mode="M21"/>
         <svrl:active-pattern>
            <xsl:apply-templates/>
         </svrl:active-pattern>
         <xsl:apply-templates select="/" mode="M22"/>
         <svrl:active-pattern>
            <xsl:apply-templates/>
         </svrl:active-pattern>
         <xsl:apply-templates select="/" mode="M23"/>
      </svrl:schematron-output>
   </xsl:template>

   <!--SCHEMATRON PATTERNS-->
<svrl:text xmlns:xs="http://www.w3.org/2001/XMLSchema"
              xmlns:svrl="http://www.ascc.net/xml/schematron">NPfIT Schematron for CDA documents</svrl:text>

   <!--PATTERN -->


	<!--RULE -->
<xsl:template match="hl7v3:*[child::hl7v3:templateId and preceding-sibling::npfitlc:contentId]"
                 priority="4000"
                 mode="M4">
      <svrl:fired-rule xmlns:xs="http://www.w3.org/2001/XMLSchema"
                       xmlns:svrl="http://www.ascc.net/xml/schematron"
                       context="hl7v3:*[child::hl7v3:templateId and preceding-sibling::npfitlc:contentId]"/>

		    <!--ASSERT -->
<xsl:choose>
         <xsl:when test="preceding-sibling::npfitlc:contentId/@extension = child::hl7v3:templateId/@extension"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:xs="http://www.w3.org/2001/XMLSchema"
                                xmlns:svrl="http://www.ascc.net/xml/schematron"
                                test="preceding-sibling::npfitlc:contentId/@extension = child::hl7v3:templateId/@extension">
               <xsl:attribute name="location">XPath:N/A</xsl:attribute>
               <svrl:text>Error:Preceding contentId extension is not matching templateId, the value should be '<xsl:text/>
                  <xsl:value-of select="child::hl7v3:templateId/@extension"/>
                  <xsl:text/>' but is '<xsl:text/>
                  <xsl:value-of select="preceding-sibling::npfitlc:contentId/@extension"/>
                  <xsl:text/>'.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M4"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M4"/>
   <xsl:template match="@*|node()" priority="-2" mode="M4">
      <xsl:apply-templates select="@*|node()" mode="M4"/>
   </xsl:template>

   <!--PATTERN -->


	<!--RULE -->
<xsl:template match="hl7v3:*[hl7v3:templateId[contains(@extension,'Ref') and substring(@extension,string-length(@extension)-2,3)='Ref' and contains(@extension,'COCD_TP147')]]"
                 priority="4000"
                 mode="M5">
      <svrl:fired-rule xmlns:xs="http://www.w3.org/2001/XMLSchema"
                       xmlns:svrl="http://www.ascc.net/xml/schematron"
                       context="hl7v3:*[hl7v3:templateId[contains(@extension,'Ref') and substring(@extension,string-length(@extension)-2,3)='Ref' and contains(@extension,'COCD_TP147')]]"/>

		    <!--ASSERT -->
<xsl:choose>
         <xsl:when test="count(hl7v3:code/@*) = 1 and hl7v3:code/@nullFlavor='NA'"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:xs="http://www.w3.org/2001/XMLSchema"
                                xmlns:svrl="http://www.ascc.net/xml/schematron"
                                test="count(hl7v3:code/@*) = 1 and hl7v3:code/@nullFlavor='NA'">
               <xsl:attribute name="location">XPath:N/A</xsl:attribute>
               <svrl:text>Error:Template Reference class '<xsl:text/>
                  <xsl:value-of select="name(.)"/>
                  <xsl:text/>' should contain a code element with nullFlavour attribute only.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
<xsl:choose>
         <xsl:when test="hl7v3:id/@root = //hl7v3:*[hl7v3:templateId[contains(@extension,'146')]]/hl7v3:id/@root or hl7v3:id/@root = //hl7v3:*[hl7v3:templateId[contains(@extension,'146')]]/hl7v3:id/@extension"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:xs="http://www.w3.org/2001/XMLSchema"
                                xmlns:svrl="http://www.ascc.net/xml/schematron"
                                test="hl7v3:id/@root = //hl7v3:*[hl7v3:templateId[contains(@extension,'146')]]/hl7v3:id/@root or hl7v3:id/@root = //hl7v3:*[hl7v3:templateId[contains(@extension,'146')]]/hl7v3:id/@extension">
               <xsl:attribute name="location">XPath:N/A</xsl:attribute>
               <svrl:text>Error: reference template '<xsl:text/>
                  <xsl:value-of select="hl7v3:templateId/@extension"/>
                  <xsl:text/>' id '<xsl:text/>
                  <xsl:value-of select="hl7v3:id/@root"/>
                  <xsl:text/>' is orphaned</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M5"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M5"/>
   <xsl:template match="@*|node()" priority="-2" mode="M5">
      <xsl:apply-templates select="@*|node()" mode="M5"/>
   </xsl:template>

   <!--PATTERN -->


	<!--RULE -->
<xsl:template match="hl7v3:templateId[contains(@extension,'COCD_TP147')]" priority="4000"
                 mode="M6">
      <svrl:fired-rule xmlns:xs="http://www.w3.org/2001/XMLSchema"
                       xmlns:svrl="http://www.ascc.net/xml/schematron"
                       context="hl7v3:templateId[contains(@extension,'COCD_TP147')]"/>

		    <!--ASSERT -->
<xsl:choose>
         <xsl:when test="//hl7v3:templateId[contains(@extension,substring-before(substring-after(current()/@extension,'#'),'Ref')) and contains(@extension,'146')]"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:xs="http://www.w3.org/2001/XMLSchema"
                                xmlns:svrl="http://www.ascc.net/xml/schematron"
                                test="//hl7v3:templateId[contains(@extension,substring-before(substring-after(current()/@extension,'#'),'Ref')) and contains(@extension,'146')]">
               <xsl:attribute name="location">XPath:N/A</xsl:attribute>
               <svrl:text>Error: reference template '<xsl:text/>
                  <xsl:value-of select="@extension"/>
                  <xsl:text/>' used is never referenced in the message. The reference template can be an disallowed one, please check the respective domain for the allowable list.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M6"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M6"/>
   <xsl:template match="@*|node()" priority="-2" mode="M6">
      <xsl:apply-templates select="@*|node()" mode="M6"/>
   </xsl:template>

   <!--PATTERN -->


	<!--RULE -->
<xsl:template match="hl7v3:ClinicalDocument[npfitlc:messageType]" priority="4000" mode="M7">
      <svrl:fired-rule xmlns:xs="http://www.w3.org/2001/XMLSchema"
                       xmlns:svrl="http://www.ascc.net/xml/schematron"
                       context="hl7v3:ClinicalDocument[npfitlc:messageType]"/>

		    <!--ASSERT -->
<xsl:choose>
         <xsl:when test="npfitlc:messageType/@root = '2.16.840.1.113883.2.1.3.2.4.18.17'"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:xs="http://www.w3.org/2001/XMLSchema"
                                xmlns:svrl="http://www.ascc.net/xml/schematron"
                                test="npfitlc:messageType/@root = '2.16.840.1.113883.2.1.3.2.4.18.17'">
               <xsl:attribute name="location">XPath:N/A</xsl:attribute>
               <svrl:text>Error:ClinicalDocument messageType/@root value should be '2.16.840.1.113883.2.1.3.2.4.18.17' but is '<xsl:text/>
                  <xsl:value-of select="npfitlc:messageType/@root"/>
                  <xsl:text/>'.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M7"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M7"/>
   <xsl:template match="@*|node()" priority="-2" mode="M7">
      <xsl:apply-templates select="@*|node()" mode="M7"/>
   </xsl:template>

   <!--PATTERN -->


	<!--RULE -->
<xsl:template match="hl7v3:contentId" priority="4000" mode="M8">
      <svrl:fired-rule xmlns:xs="http://www.w3.org/2001/XMLSchema"
                       xmlns:svrl="http://www.ascc.net/xml/schematron"
                       context="hl7v3:contentId"/>

		    <!--ASSERT -->
<xsl:choose>
         <xsl:when test="@root = '2.16.840.1.113883.2.1.3.2.4.18.16'"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:xs="http://www.w3.org/2001/XMLSchema"
                                xmlns:svrl="http://www.ascc.net/xml/schematron"
                                test="@root = '2.16.840.1.113883.2.1.3.2.4.18.16'">
               <xsl:attribute name="location">XPath:N/A</xsl:attribute>
               <svrl:text>Error:contentId/@root value should be '2.16.840.1.113883.2.1.3.2.4.18.16' but is '<xsl:text/>
                  <xsl:value-of select="@root"/>
                  <xsl:text/>'.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M8"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M8"/>
   <xsl:template match="@*|node()" priority="-2" mode="M8">
      <xsl:apply-templates select="@*|node()" mode="M8"/>
   </xsl:template>

   <!--PATTERN -->


	<!--RULE -->
<xsl:template match="hl7v3:content[@ID]" priority="4000" mode="M9">
      <svrl:fired-rule xmlns:xs="http://www.w3.org/2001/XMLSchema"
                       xmlns:svrl="http://www.ascc.net/xml/schematron"
                       context="hl7v3:content[@ID]"/>

		    <!--ASSERT -->
<xsl:choose>
         <xsl:when test="substring-after(@ID,'_') = //hl7v3:id/@root or concat('#',@ID) = //hl7v3:reference/@value"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:xs="http://www.w3.org/2001/XMLSchema"
                                xmlns:svrl="http://www.ascc.net/xml/schematron"
                                test="substring-after(@ID,'_') = //hl7v3:id/@root or concat('#',@ID) = //hl7v3:reference/@value">
               <xsl:attribute name="location">XPath:N/A</xsl:attribute>
               <svrl:text>Error:content '<xsl:text/>
                  <xsl:value-of select="@ID"/>
                  <xsl:text/>' is orphaned.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M9"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M9"/>
   <xsl:template match="@*|node()" priority="-2" mode="M9">
      <xsl:apply-templates select="@*|node()" mode="M9"/>
   </xsl:template>

   <!--PATTERN -->


	<!--RULE -->
<xsl:template match="hl7v3:COCD_TP146224GB02.ObservationMedia[@ID]" priority="4000"
                 mode="M10">
      <svrl:fired-rule xmlns:xs="http://www.w3.org/2001/XMLSchema"
                       xmlns:svrl="http://www.ascc.net/xml/schematron"
                       context="hl7v3:COCD_TP146224GB02.ObservationMedia[@ID]"/>

		    <!--ASSERT -->
<xsl:choose>
         <xsl:when test="@ID = //hl7v3:renderMultiMedia/@referencedObject"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:xs="http://www.w3.org/2001/XMLSchema"
                                xmlns:svrl="http://www.ascc.net/xml/schematron"
                                test="@ID = //hl7v3:renderMultiMedia/@referencedObject">
               <xsl:attribute name="location">XPath:N/A</xsl:attribute>
               <svrl:text>Error:Multimedia Reference '<xsl:text/>
                  <xsl:value-of select="@ID"/>
                  <xsl:text/>' is orphaned.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M10"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M10"/>
   <xsl:template match="@*|node()" priority="-2" mode="M10">
      <xsl:apply-templates select="@*|node()" mode="M10"/>
   </xsl:template>

   <!--PATTERN -->


	<!--RULE -->
<xsl:template match="hl7v3:reference[@value]" priority="4000" mode="M11">
      <svrl:fired-rule xmlns:xs="http://www.w3.org/2001/XMLSchema"
                       xmlns:svrl="http://www.ascc.net/xml/schematron"
                       context="hl7v3:reference[@value]"/>

		    <!--ASSERT -->
<xsl:choose>
         <xsl:when test="substring-after(current()/@value,'#') = //hl7v3:content/@ID"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:xs="http://www.w3.org/2001/XMLSchema"
                                xmlns:svrl="http://www.ascc.net/xml/schematron"
                                test="substring-after(current()/@value,'#') = //hl7v3:content/@ID">
               <xsl:attribute name="location">XPath:N/A</xsl:attribute>
               <svrl:text>Error:originalText/reference '<xsl:text/>
                  <xsl:value-of select="@value"/>
                  <xsl:text/>' is orphaned.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--REPORT -->
<xsl:if test="count(//hl7v3:reference/@value[. = current()/@value]) &gt; 1">
         <svrl:successful-report xmlns:xs="http://www.w3.org/2001/XMLSchema"
                                 xmlns:svrl="http://www.ascc.net/xml/schematron"
                                 test="count(//hl7v3:reference/@value[. = current()/@value]) &gt; 1">
            <xsl:attribute name="location">XPath:N/A</xsl:attribute>
            <svrl:text>Information: originalText/reference '<xsl:text/>
               <xsl:value-of select="substring-after(current()/@value,'#')"/>
               <xsl:text/>' is referenced multiple times.</svrl:text>
         </svrl:successful-report>
      </xsl:if>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M11"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M11"/>
   <xsl:template match="@*|node()" priority="-2" mode="M11">
      <xsl:apply-templates select="@*|node()" mode="M11"/>
   </xsl:template>

   <!--PATTERN -->


	<!--RULE -->
<xsl:template match="hl7v3:* [not(ancestor::hl7v3:text)]" priority="4000" mode="M12">
      <svrl:fired-rule xmlns:xs="http://www.w3.org/2001/XMLSchema"
                       xmlns:svrl="http://www.ascc.net/xml/schematron"
                       context="hl7v3:* [not(ancestor::hl7v3:text)]"/>

		    <!--REPORT -->
<xsl:if test="not(@*) and normalize-space(.)='' and not(*)">
         <svrl:successful-report xmlns:xs="http://www.w3.org/2001/XMLSchema"
                                 xmlns:svrl="http://www.ascc.net/xml/schematron"
                                 test="not(@*) and normalize-space(.)='' and not(*)">
            <xsl:attribute name="location">XPath:N/A</xsl:attribute>
            <svrl:text>Error empty element <xsl:text/>
               <xsl:value-of select="name(.)"/>
               <xsl:text/> has no attributes or text</svrl:text>
         </svrl:successful-report>
      </xsl:if>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M12"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M12"/>
   <xsl:template match="@*|node()" priority="-2" mode="M12">
      <xsl:apply-templates select="@*|node()" mode="M12"/>
   </xsl:template>

   <!--PATTERN -->


	<!--RULE -->
<xsl:template match="hl7v3:*[child::hl7v3:templateId[contains(@extension,'CRETypeList') and contains(@extension,'COCD_TP146')]]/hl7v3:value"
                 priority="4000"
                 mode="M13">
      <svrl:fired-rule xmlns:xs="http://www.w3.org/2001/XMLSchema"
                       xmlns:svrl="http://www.ascc.net/xml/schematron"
                       context="hl7v3:*[child::hl7v3:templateId[contains(@extension,'CRETypeList') and contains(@extension,'COCD_TP146')]]/hl7v3:value"/>

		    <!--ASSERT -->
<xsl:choose>
         <xsl:when test="@root = //hl7v3:id/@root"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:xs="http://www.w3.org/2001/XMLSchema"
                                xmlns:svrl="http://www.ascc.net/xml/schematron"
                                test="@root = //hl7v3:id/@root">
               <xsl:attribute name="location">XPath:N/A</xsl:attribute>
               <svrl:text>Error:CREType list CRETypeList value/@root '<xsl:text/>
                  <xsl:value-of select="@root"/>
                  <xsl:text/>' is orphaned.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
<xsl:choose>
         <xsl:when test="count(//hl7v3:value/@root[. = current()/@root]) = 1"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:xs="http://www.w3.org/2001/XMLSchema"
                                xmlns:svrl="http://www.ascc.net/xml/schematron"
                                test="count(//hl7v3:value/@root[. = current()/@root]) = 1">
               <xsl:attribute name="location">XPath:N/A</xsl:attribute>
               <svrl:text>Error:CREType list value/@root '<xsl:text/>
                  <xsl:value-of select="@root"/>
                  <xsl:text/>' is duplicated.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M13"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M13"/>
   <xsl:template match="@*|node()" priority="-2" mode="M13">
      <xsl:apply-templates select="@*|node()" mode="M13"/>
   </xsl:template>

   <!--PATTERN -->


	<!--RULE -->
<xsl:template match="hl7v3:*[child::hl7v3:templateId[contains(@extension,'DIProcedureEvent') and contains(@extension,'COCD_TP146')]]/hl7v3:id"
                 priority="4000"
                 mode="M14">
      <svrl:fired-rule xmlns:xs="http://www.w3.org/2001/XMLSchema"
                       xmlns:svrl="http://www.ascc.net/xml/schematron"
                       context="hl7v3:*[child::hl7v3:templateId[contains(@extension,'DIProcedureEvent') and contains(@extension,'COCD_TP146')]]/hl7v3:id"/>

		    <!--ASSERT -->
<xsl:choose>
         <xsl:when test="@root = //hl7v3:*[child::hl7v3:templateId[not(contains(@extension,'DIProcedureEvent')) and contains(@extension,'COCD_TP146')]]/hl7v3:id/@root"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:xs="http://www.w3.org/2001/XMLSchema"
                                xmlns:svrl="http://www.ascc.net/xml/schematron"
                                test="@root = //hl7v3:*[child::hl7v3:templateId[not(contains(@extension,'DIProcedureEvent')) and contains(@extension,'COCD_TP146')]]/hl7v3:id/@root">
               <xsl:attribute name="location">XPath:N/A</xsl:attribute>
               <svrl:text>Error:DIProcedureEvent list id/@root '<xsl:text/>
                  <xsl:value-of select="@root"/>
                  <xsl:text/>' is orphaned.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
<xsl:choose>
         <xsl:when test="count(//hl7v3:*[child::hl7v3:templateId[contains(@extension,'DIProcedureEvent') and contains(@extension,'COCD_TP146')]]/hl7v3:id/@root[.= current()/@root]) = 1"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:xs="http://www.w3.org/2001/XMLSchema"
                                xmlns:svrl="http://www.ascc.net/xml/schematron"
                                test="count(//hl7v3:*[child::hl7v3:templateId[contains(@extension,'DIProcedureEvent') and contains(@extension,'COCD_TP146')]]/hl7v3:id/@root[.= current()/@root]) = 1">
               <xsl:attribute name="location">XPath:N/A</xsl:attribute>
               <svrl:text>Error:DIProcedureEvent list id/@root '<xsl:text/>
                  <xsl:value-of select="@root"/>
                  <xsl:text/>' is duplicated.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M14"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M14"/>
   <xsl:template match="@*|node()" priority="-2" mode="M14">
      <xsl:apply-templates select="@*|node()" mode="M14"/>
   </xsl:template>

   <!--PATTERN -->


	<!--RULE -->
<xsl:template match="hl7v3:*[child::hl7v3:templateId[contains(@extension,'COCD_TP146029UK') or contains(@extension,'COCD_TP146030UK') or contains(@extension,'COCD_TP146031UK') or contains(@extension,'COCD_TP146032UK') or contains(@extension,'COCD_TP146033UK') or contains(@extension,'COCD_TP146034UK')] and preceding-sibling::npfitlc:contentId]"
                 priority="4000"
                 mode="M15">
      <svrl:fired-rule xmlns:xs="http://www.w3.org/2001/XMLSchema"
                       xmlns:svrl="http://www.ascc.net/xml/schematron"
                       context="hl7v3:*[child::hl7v3:templateId[contains(@extension,'COCD_TP146029UK') or contains(@extension,'COCD_TP146030UK') or contains(@extension,'COCD_TP146031UK') or contains(@extension,'COCD_TP146032UK') or contains(@extension,'COCD_TP146033UK') or contains(@extension,'COCD_TP146034UK')] and preceding-sibling::npfitlc:contentId]"/>

		    <!--ASSERT -->
<xsl:choose>
         <xsl:when test="//hl7v3:*[child::hl7v3:templateId[contains(@extension,'COCD_TP146062UK')] and preceding-sibling::npfitlc:contentId]/hl7v3:code/@code='185361000000102'"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:xs="http://www.w3.org/2001/XMLSchema"
                                xmlns:svrl="http://www.ascc.net/xml/schematron"
                                test="//hl7v3:*[child::hl7v3:templateId[contains(@extension,'COCD_TP146062UK')] and preceding-sibling::npfitlc:contentId]/hl7v3:code/@code='185361000000102'">
               <xsl:attribute name="location">XPath:N/A</xsl:attribute>
               <svrl:text>Error: Medication template '<xsl:text/>
                  <xsl:value-of select="hl7v3:templateId/@extension"/>
                  <xsl:text/>' is used and CRETypeList code value is wrong should be '185361000000102' but is '<xsl:text/>
                  <xsl:value-of select="//hl7v3:*[child::hl7v3:templateId[contains(@extension,'COCD_TP146062UK')]]/hl7v3:code/@code"/>
                  <xsl:text/>'.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
<xsl:choose>
         <xsl:when test="hl7v3:id/@root=//hl7v3:*[child::hl7v3:templateId[contains(@extension,'COCD_TP146062UK')] and preceding-sibling::npfitlc:contentId]/hl7v3:value/@root"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:xs="http://www.w3.org/2001/XMLSchema"
                                xmlns:svrl="http://www.ascc.net/xml/schematron"
                                test="hl7v3:id/@root=//hl7v3:*[child::hl7v3:templateId[contains(@extension,'COCD_TP146062UK')] and preceding-sibling::npfitlc:contentId]/hl7v3:value/@root">
               <xsl:attribute name="location">XPath:N/A</xsl:attribute>
               <svrl:text>Error: Medication template '<xsl:text/>
                  <xsl:value-of select="hl7v3:templateId/@extension"/>
                  <xsl:text/>' id '<xsl:text/>
                  <xsl:value-of select="hl7v3:id/@root"/>
                  <xsl:text/>' is not in CRETypeList</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M15"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M15"/>
   <xsl:template match="@*|node()" priority="-2" mode="M15">
      <xsl:apply-templates select="@*|node()" mode="M15"/>
   </xsl:template>

   <!--PATTERN -->


	<!--RULE -->
<xsl:template match="hl7v3:*[child::hl7v3:templateId[contains(@extension,'COCD_TP146025UK')] and preceding-sibling::npfitlc:contentId]"
                 priority="4000"
                 mode="M16">
      <svrl:fired-rule xmlns:xs="http://www.w3.org/2001/XMLSchema"
                       xmlns:svrl="http://www.ascc.net/xml/schematron"
                       context="hl7v3:*[child::hl7v3:templateId[contains(@extension,'COCD_TP146025UK')] and preceding-sibling::npfitlc:contentId]"/>

		    <!--ASSERT -->
<xsl:choose>
         <xsl:when test="//hl7v3:*[child::hl7v3:templateId[contains(@extension,'COCD_TP146062UK')] and preceding-sibling::npfitlc:contentId]/hl7v3:code/@code='163221000000102'"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:xs="http://www.w3.org/2001/XMLSchema"
                                xmlns:svrl="http://www.ascc.net/xml/schematron"
                                test="//hl7v3:*[child::hl7v3:templateId[contains(@extension,'COCD_TP146062UK')] and preceding-sibling::npfitlc:contentId]/hl7v3:code/@code='163221000000102'">
               <xsl:attribute name="location">XPath:N/A</xsl:attribute>
               <svrl:text>Error: Allergy template '<xsl:text/>
                  <xsl:value-of select="hl7v3:templateId/@extension"/>
                  <xsl:text/>' is used and CRETypeList code value is wrong should be '163221000000102' but is '<xsl:text/>
                  <xsl:value-of select="//hl7v3:*[child::hl7v3:templateId[contains(@extension,'COCD_TP146062UK')]]/hl7v3:code/@code"/>
                  <xsl:text/>'.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
<xsl:choose>
         <xsl:when test="hl7v3:id/@root=//hl7v3:*[child::hl7v3:templateId[contains(@extension,'COCD_TP146062UK')] and preceding-sibling::npfitlc:contentId]/hl7v3:value/@root"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:xs="http://www.w3.org/2001/XMLSchema"
                                xmlns:svrl="http://www.ascc.net/xml/schematron"
                                test="hl7v3:id/@root=//hl7v3:*[child::hl7v3:templateId[contains(@extension,'COCD_TP146062UK')] and preceding-sibling::npfitlc:contentId]/hl7v3:value/@root">
               <xsl:attribute name="location">XPath:N/A</xsl:attribute>
               <svrl:text>Error: Medication template '<xsl:text/>
                  <xsl:value-of select="hl7v3:templateId/@extension"/>
                  <xsl:text/>' id '<xsl:text/>
                  <xsl:value-of select="hl7v3:id/@root"/>
                  <xsl:text/>' is not in CRETypeList</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M16"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M16"/>
   <xsl:template match="@*|node()" priority="-2" mode="M16">
      <xsl:apply-templates select="@*|node()" mode="M16"/>
   </xsl:template>

   <!--PATTERN -->


	<!--RULE -->
<xsl:template match="hl7v3:*[child::hl7v3:templateId[contains(@extension,'COCD_TP146062UK')] and preceding-sibling::npfitlc:contentId]"
                 priority="4000"
                 mode="M17">
      <svrl:fired-rule xmlns:xs="http://www.w3.org/2001/XMLSchema"
                       xmlns:svrl="http://www.ascc.net/xml/schematron"
                       context="hl7v3:*[child::hl7v3:templateId[contains(@extension,'COCD_TP146062UK')] and preceding-sibling::npfitlc:contentId]"/>

		    <!--ASSERT -->
<xsl:choose>
         <xsl:when test="hl7v3:code/@code='163221000000102' or hl7v3:code/@code='185361000000102'"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:xs="http://www.w3.org/2001/XMLSchema"
                                xmlns:svrl="http://www.ascc.net/xml/schematron"
                                test="hl7v3:code/@code='163221000000102' or hl7v3:code/@code='185361000000102'">
               <xsl:attribute name="location">XPath:N/A</xsl:attribute>
               <svrl:text>Error: CRETypeList template '<xsl:text/>
                  <xsl:value-of select="hl7v3:templateId/@extension"/>
                  <xsl:text/>' code/@code value is wrong should be '163221000000102' or '185361000000102' but is '<xsl:text/>
                  <xsl:value-of select="hl7v3:code/@code"/>
                  <xsl:text/>'.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M17"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M17"/>
   <xsl:template match="@*|node()" priority="-2" mode="M17">
      <xsl:apply-templates select="@*|node()" mode="M17"/>
   </xsl:template>

   <!--PATTERN -->


	<!--RULE -->
<xsl:template match="hl7v3:*[child::hl7v3:templateId[contains(@extension,'COCD_TP146') and not(contains(@extension,'DIProcedureEvent'))] and preceding-sibling::npfitlc:contentId and child::hl7v3:id[@root and not(@extension)]]/hl7v3:id"
                 priority="4000"
                 mode="M18">
      <svrl:fired-rule xmlns:xs="http://www.w3.org/2001/XMLSchema"
                       xmlns:svrl="http://www.ascc.net/xml/schematron"
                       context="hl7v3:*[child::hl7v3:templateId[contains(@extension,'COCD_TP146') and not(contains(@extension,'DIProcedureEvent'))] and preceding-sibling::npfitlc:contentId and child::hl7v3:id[@root and not(@extension)]]/hl7v3:id"/>

		    <!--ASSERT -->
<xsl:choose>
         <xsl:when test="count(//hl7v3:*[child::hl7v3:templateId[contains(@extension,'COCD_TP146') and not(contains(@extension,'DIProcedureEvent'))] and preceding-sibling::npfitlc:contentId and child::hl7v3:id[@root and not(@extension)]]/hl7v3:id/@root[.=current()/@root])=1"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:xs="http://www.w3.org/2001/XMLSchema"
                                xmlns:svrl="http://www.ascc.net/xml/schematron"
                                test="count(//hl7v3:*[child::hl7v3:templateId[contains(@extension,'COCD_TP146') and not(contains(@extension,'DIProcedureEvent'))] and preceding-sibling::npfitlc:contentId and child::hl7v3:id[@root and not(@extension)]]/hl7v3:id/@root[.=current()/@root])=1">
               <xsl:attribute name="location">XPath:N/A</xsl:attribute>
               <svrl:text>Error: ClinicalTemplate id is duplicated '<xsl:text/>
                  <xsl:value-of select="@root"/>
                  <xsl:text/>'</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M18"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M18"/>
   <xsl:template match="@*|node()" priority="-2" mode="M18">
      <xsl:apply-templates select="@*|node()" mode="M18"/>
   </xsl:template>

   <!--PATTERN -->


	<!--RULE -->
<xsl:template match="hl7v3:value[(contains(name(..),'LifeStyle') or contains(name(..),'SocialOrPersonalCircumstance')) and contains(name(..),'COCD_TP146')]"
                 priority="4000"
                 mode="M19">
      <svrl:fired-rule xmlns:xs="http://www.w3.org/2001/XMLSchema"
                       xmlns:svrl="http://www.ascc.net/xml/schematron"
                       context="hl7v3:value[(contains(name(..),'LifeStyle') or contains(name(..),'SocialOrPersonalCircumstance')) and contains(name(..),'COCD_TP146')]"/>

		    <!--ASSERT -->
<xsl:choose>
         <xsl:when test="@xsi:type = 'IVL_TS' or @xsi:type = 'ST' or @xsi:type = 'REAL' or @xsi:type = 'INT' or @xsi:type = 'PQ'"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:xs="http://www.w3.org/2001/XMLSchema"
                                xmlns:svrl="http://www.ascc.net/xml/schematron"
                                test="@xsi:type = 'IVL_TS' or @xsi:type = 'ST' or @xsi:type = 'REAL' or @xsi:type = 'INT' or @xsi:type = 'PQ'">
               <xsl:attribute name="location">XPath:N/A</xsl:attribute>
               <svrl:text>Error: value @xsi:type is wrong should be IVL_TS or ST or REAL or PQ or INT but is '<xsl:text/>
                  <xsl:value-of select="@xsi:type"/>
                  <xsl:text/>'.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M19"/>
   </xsl:template>

	  <!--RULE -->
<xsl:template match="hl7v3:value[contains(name(..),'scale') and contains(name(..),'COCD_TP146065')]"
                 priority="3999"
                 mode="M19">
      <svrl:fired-rule xmlns:xs="http://www.w3.org/2001/XMLSchema"
                       xmlns:svrl="http://www.ascc.net/xml/schematron"
                       context="hl7v3:value[contains(name(..),'scale') and contains(name(..),'COCD_TP146065')]"/>

		    <!--ASSERT -->
<xsl:choose>
         <xsl:when test="@xsi:type = 'ST' or @xsi:type = 'REAL' or @xsi:type = 'PQ'"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:xs="http://www.w3.org/2001/XMLSchema"
                                xmlns:svrl="http://www.ascc.net/xml/schematron"
                                test="@xsi:type = 'ST' or @xsi:type = 'REAL' or @xsi:type = 'PQ'">
               <xsl:attribute name="location">XPath:N/A</xsl:attribute>
               <svrl:text>Error: value @xsi:type is wrong should be ST or REAL or PQ but is '<xsl:text/>
                  <xsl:value-of select="@xsi:type"/>
                  <xsl:text/>'.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M19"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M19"/>
   <xsl:template match="@*|node()" priority="-2" mode="M19">
      <xsl:apply-templates select="@*|node()" mode="M19"/>
   </xsl:template>

   <!--PATTERN -->


	<!--RULE -->
<xsl:template match="hl7v3:*[child::hl7v3:templateId[contains(@extension,'COCD_TP145004UK') or contains(@extension,'COCD_TP145021UK')] and preceding-sibling::npfitlc:contentId]"
                 priority="4000"
                 mode="M20">
      <svrl:fired-rule xmlns:xs="http://www.w3.org/2001/XMLSchema"
                       xmlns:svrl="http://www.ascc.net/xml/schematron"
                       context="hl7v3:*[child::hl7v3:templateId[contains(@extension,'COCD_TP145004UK') or contains(@extension,'COCD_TP145021UK')] and preceding-sibling::npfitlc:contentId]"/>

		    <!--ASSERT -->
<xsl:choose>
         <xsl:when test="hl7v3:id[1]/@root='1.2.826.0.1285.0.2.0.65' and hl7v3:id[2]/@root='1.2.826.0.1285.0.2.0.67'"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:xs="http://www.w3.org/2001/XMLSchema"
                                xmlns:svrl="http://www.ascc.net/xml/schematron"
                                test="hl7v3:id[1]/@root='1.2.826.0.1285.0.2.0.65' and hl7v3:id[2]/@root='1.2.826.0.1285.0.2.0.67'">
               <xsl:attribute name="location">XPath:N/A</xsl:attribute>
               <svrl:text>Error: SDS User ID and SDS User Role Profile ID OID's are in the wrong order should be '1.2.826.0.1285.0.2.0.65' and '1.2.826.0.1285.0.2.0.67' but in order '<xsl:text/>
                  <xsl:value-of select="hl7v3:id[1]/@root"/>
                  <xsl:text/>' and '<xsl:text/>
                  <xsl:value-of select="hl7v3:id[2]/@root"/>
                  <xsl:text/>'</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
<xsl:choose>
         <xsl:when test="hl7v3:id[1]/@extension!=hl7v3:id[2]/@extension"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:xs="http://www.w3.org/2001/XMLSchema"
                                xmlns:svrl="http://www.ascc.net/xml/schematron"
                                test="hl7v3:id[1]/@extension!=hl7v3:id[2]/@extension">
               <xsl:attribute name="location">XPath:N/A</xsl:attribute>
               <svrl:text>Error: SDS User ID and SDS User Role Profile ID are identical '<xsl:text/>
                  <xsl:value-of select="hl7v3:id[1]/@extension"/>
                  <xsl:text/>' and '<xsl:text/>
                  <xsl:value-of select="hl7v3:id[2]/@extension"/>
                  <xsl:text/>'</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
<xsl:choose>
         <xsl:when test="count(hl7v3:id) = 2"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:xs="http://www.w3.org/2001/XMLSchema"
                                xmlns:svrl="http://www.ascc.net/xml/schematron"
                                test="count(hl7v3:id) = 2">
               <xsl:attribute name="location">XPath:N/A</xsl:attribute>
               <svrl:text>Error: Count of id's for user and role profile are wrong should be 2 but is '<xsl:text/>
                  <xsl:value-of select="count(hl7v3:id)"/>
                  <xsl:text/>'</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M20"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M20"/>
   <xsl:template match="@*|node()" priority="-2" mode="M20">
      <xsl:apply-templates select="@*|node()" mode="M20"/>
   </xsl:template>

   <!--PATTERN -->


	<!--RULE -->
<xsl:template match="hl7v3:*[child::hl7v3:templateId[contains(@extension,'COCD_TP145001UK') or contains(@extension,'COCD_TP145022UK')] and preceding-sibling::npfitlc:contentId and (child::hl7v3:id[1]/@root and child::hl7v3:id[2]/@root)]"
                 priority="4000"
                 mode="M21">
      <svrl:fired-rule xmlns:xs="http://www.w3.org/2001/XMLSchema"
                       xmlns:svrl="http://www.ascc.net/xml/schematron"
                       context="hl7v3:*[child::hl7v3:templateId[contains(@extension,'COCD_TP145001UK') or contains(@extension,'COCD_TP145022UK')] and preceding-sibling::npfitlc:contentId and (child::hl7v3:id[1]/@root and child::hl7v3:id[2]/@root)]"/>

		    <!--ASSERT -->
<xsl:choose>
         <xsl:when test="hl7v3:id[1]/@root='1.2.826.0.1285.0.2.0.65' and hl7v3:id[2]/@root='1.2.826.0.1285.0.2.0.67'"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:xs="http://www.w3.org/2001/XMLSchema"
                                xmlns:svrl="http://www.ascc.net/xml/schematron"
                                test="hl7v3:id[1]/@root='1.2.826.0.1285.0.2.0.65' and hl7v3:id[2]/@root='1.2.826.0.1285.0.2.0.67'">
               <xsl:attribute name="location">XPath:N/A</xsl:attribute>
               <svrl:text>Error: SDS User ID and SDS User Role Profile ID OID's are in the wrong order should be '1.2.826.0.1285.0.2.0.65' and '1.2.826.0.1285.0.2.0.67' but in order '<xsl:text/>
                  <xsl:value-of select="hl7v3:id[1]/@root"/>
                  <xsl:text/>' and '<xsl:text/>
                  <xsl:value-of select="hl7v3:id[2]/@root"/>
                  <xsl:text/>'</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
<xsl:choose>
         <xsl:when test="hl7v3:id[1]/@extension!=hl7v3:id[2]/@extension"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:xs="http://www.w3.org/2001/XMLSchema"
                                xmlns:svrl="http://www.ascc.net/xml/schematron"
                                test="hl7v3:id[1]/@extension!=hl7v3:id[2]/@extension">
               <xsl:attribute name="location">XPath:N/A</xsl:attribute>
               <svrl:text>Error: SDS User ID and SDS User Role Profile ID are identical '<xsl:text/>
                  <xsl:value-of select="hl7v3:id[1]/@extension"/>
                  <xsl:text/>' and '<xsl:text/>
                  <xsl:value-of select="hl7v3:id[2]/@extension"/>
                  <xsl:text/>'</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
<xsl:choose>
         <xsl:when test="count(hl7v3:id) = 2"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:xs="http://www.w3.org/2001/XMLSchema"
                                xmlns:svrl="http://www.ascc.net/xml/schematron"
                                test="count(hl7v3:id) = 2">
               <xsl:attribute name="location">XPath:N/A</xsl:attribute>
               <svrl:text>Error: Count of id's for user and role profile are wrong should be 2 but is '<xsl:text/>
                  <xsl:value-of select="count(hl7v3:id)"/>
                  <xsl:text/>'</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M21"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M21"/>
   <xsl:template match="@*|node()" priority="-2" mode="M21">
      <xsl:apply-templates select="@*|node()" mode="M21"/>
   </xsl:template>

   <!--PATTERN -->


	<!--RULE -->
<xsl:template match="hl7v3:*[child::hl7v3:templateId[contains(@extension,'COCD_TP145001UK') or contains(@extension,'COCD_TP145022UK')] and preceding-sibling::npfitlc:contentId and (child::hl7v3:id[1]/@root and child::hl7v3:id[2]/@nullFlavor)]"
                 priority="4000"
                 mode="M22">
      <svrl:fired-rule xmlns:xs="http://www.w3.org/2001/XMLSchema"
                       xmlns:svrl="http://www.ascc.net/xml/schematron"
                       context="hl7v3:*[child::hl7v3:templateId[contains(@extension,'COCD_TP145001UK') or contains(@extension,'COCD_TP145022UK')] and preceding-sibling::npfitlc:contentId and (child::hl7v3:id[1]/@root and child::hl7v3:id[2]/@nullFlavor)]"/>

		    <!--ASSERT -->
<xsl:choose>
         <xsl:when test="hl7v3:id[1]/@root='1.2.826.0.1285.0.2.0.65' and hl7v3:id[2]/@nullFlavor='UNK'"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:xs="http://www.w3.org/2001/XMLSchema"
                                xmlns:svrl="http://www.ascc.net/xml/schematron"
                                test="hl7v3:id[1]/@root='1.2.826.0.1285.0.2.0.65' and hl7v3:id[2]/@nullFlavor='UNK'">
               <xsl:attribute name="location">XPath:N/A</xsl:attribute>
               <svrl:text>Error: SDS User ID and Unknown SDS User Role Profile should be '1.2.826.0.1285.0.2.0.65' and 'UNK' but in order '<xsl:text/>
                  <xsl:value-of select="hl7v3:id[1]/@root"/>
                  <xsl:text/>' and '<xsl:text/>
                  <xsl:value-of select="hl7v3:id[2]/@nullFlavor"/>
                  <xsl:text/>'</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>

		    <!--ASSERT -->
<xsl:choose>
         <xsl:when test="count(hl7v3:id) = 2"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:xs="http://www.w3.org/2001/XMLSchema"
                                xmlns:svrl="http://www.ascc.net/xml/schematron"
                                test="count(hl7v3:id) = 2">
               <xsl:attribute name="location">XPath:N/A</xsl:attribute>
               <svrl:text>Error: Count of id's for user and role profile are wrong should be 2 but is '<xsl:text/>
                  <xsl:value-of select="count(hl7v3:id)"/>
                  <xsl:text/>'</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M22"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M22"/>
   <xsl:template match="@*|node()" priority="-2" mode="M22">
      <xsl:apply-templates select="@*|node()" mode="M22"/>
   </xsl:template>

   <!--PATTERN -->


	<!--RULE -->
<xsl:template match="hl7v3:*[@*[substring(.,1,1)=' ']]/@*" priority="4000" mode="M23">
      <svrl:fired-rule xmlns:xs="http://www.w3.org/2001/XMLSchema"
                       xmlns:svrl="http://www.ascc.net/xml/schematron"
                       context="hl7v3:*[@*[substring(.,1,1)=' ']]/@*"/>

		    <!--ASSERT -->
<xsl:choose>
         <xsl:when test="substring(.,1,1) !=' '"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:xs="http://www.w3.org/2001/XMLSchema"
                                xmlns:svrl="http://www.ascc.net/xml/schematron"
                                test="substring(.,1,1) !=' '">
               <xsl:attribute name="location">XPath:N/A</xsl:attribute>
               <svrl:text>Error: Attribute '<xsl:text/>
                  <xsl:value-of select="name(.)"/>
                  <xsl:text/>' is carrying a leading space in its value '<xsl:text/>
                  <xsl:value-of select="."/>
                  <xsl:text/>'.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M23"/>
   </xsl:template>

	  <!--RULE -->
<xsl:template match="hl7v3:*[@*[substring(.,string-length(.),1)=' ']]/@*" priority="3999"
                 mode="M23">
      <svrl:fired-rule xmlns:xs="http://www.w3.org/2001/XMLSchema"
                       xmlns:svrl="http://www.ascc.net/xml/schematron"
                       context="hl7v3:*[@*[substring(.,string-length(.),1)=' ']]/@*"/>

		    <!--ASSERT -->
<xsl:choose>
         <xsl:when test="substring(.,string-length(.),1) !=' '"/>
         <xsl:otherwise>
            <svrl:failed-assert xmlns:xs="http://www.w3.org/2001/XMLSchema"
                                xmlns:svrl="http://www.ascc.net/xml/schematron"
                                test="substring(.,string-length(.),1) !=' '">
               <xsl:attribute name="location">XPath:N/A</xsl:attribute>
               <svrl:text>Error: Attribute '<xsl:text/>
                  <xsl:value-of select="name(.)"/>
                  <xsl:text/>' is carrying a trailing space in its value '<xsl:text/>
                  <xsl:value-of select="."/>
                  <xsl:text/>'.</svrl:text>
            </svrl:failed-assert>
         </xsl:otherwise>
      </xsl:choose>
      <xsl:apply-templates select="@*|*|comment()|processing-instruction()" mode="M23"/>
   </xsl:template>
   <xsl:template match="text()" priority="-1" mode="M23"/>
   <xsl:template match="@*|node()" priority="-2" mode="M23">
      <xsl:apply-templates select="@*|node()" mode="M23"/>
   </xsl:template>
</xsl:stylesheet>